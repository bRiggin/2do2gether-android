package com.rbiggin.a2do2gether.ui.todo

import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import com.rbiggin.a2do2gether.model.ToDoList
import com.rbiggin.a2do2gether.model.ToDoListItem
import com.rbiggin.a2do2gether.repository.SettingsRepository
import com.rbiggin.a2do2gether.repository.ToDoListRepository
import com.rbiggin.a2do2gether.ui.base.BasePresenter
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import javax.inject.Inject

class ToDoListPresenter @Inject constructor(private val settingsRepository: SettingsRepository,
                                            private val toDoListRepository: ToDoListRepository,
                                            private val computationThread: Scheduler,
                                            private val uiThread: Scheduler) : BasePresenter<ToDoListPresenter.View>() {

    private var currentProgress = 0
    private val valueAnimator: ValueAnimator = ValueAnimator.ofInt()
    private var reorderListByCompletion: Boolean = false
    //todo implement priority sorting in settings repo
    private var reorderListByPriority: Boolean = true
    private var cachedUiState: HashMap<String, CachedItem>? = null
    private val sortedAdapterList: ArrayList<String> = arrayListOf()
    private val itemObservables: HashMap<String, Observable<ToDoListItem>> = hashMapOf()

    var thing = true



    init {
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()
        valueAnimator.addUpdateListener {
            currentProgress = it.animatedValue as Int
            view?.onProgressChanged(currentProgress)
        }
    }

    override fun onViewAttached(view: View) {
        super.onViewAttached(view)
        disposeOnViewWillDetach(settingsRepository.onReorderListByCompletionChanged()
                .subscribeOn(computationThread)
                .distinctUntilChanged()
                .subscribe { reorderListByCompletion = it })
    }

    fun onIdSupplied(id: String) {
        toDoListRepository.onToDoListChanged(id)?.let { subject ->
            disposeOnViewWillHide(subject
                    .observeOn(uiThread)
                    .distinctUntilChanged()
                    .subscribe {
                        updateToDoList(it)
                    })
        }
    }

    private fun updateToDoList(toDoList: ToDoList) {
        view?.onTitleChanged(toDoList.title)
        updateProgressBar(toDoList.items)

        sortToDoListItems(toDoList)
        createCachedUiState(toDoList)
        updateItemObservables(toDoList)

        cachedUiState?.let { cachedState ->
            if (thing) {
                view?.onToDoListUpdate(
                        ObservablesAndUiState(sortedAdapterList, cachedState, itemObservables)
                )
                thing = false
            }
        }
    }

    private fun updateProgressBar(items: LinkedHashMap<String, ToDoListItem>?) {
        var numOfCompletedItems = 0.0
        items?.forEach {
            if (it.value.status)
                numOfCompletedItems++
        }
        items?.let {
            valueAnimator.setIntValues(currentProgress, ((numOfCompletedItems / it.size) * 100).toInt())
        } ?: run {
            valueAnimator.setIntValues(currentProgress, 0)
        }
        valueAnimator.duration = 500
        valueAnimator.start()
    }

    private fun sortToDoListItems(toDoList: ToDoList) {
        toDoList.items?.let { listItems ->
            val itemsAsList: List<ToDoListItem> = listItems.toSortedMap().map { it.value }.toList()
            val sortedList = when {
                reorderListByCompletion && reorderListByPriority ->
                    itemsAsList.sortedWith(compareBy({ it.status }, { it.priority }))
                !reorderListByCompletion && reorderListByPriority ->
                    itemsAsList.sortedWith(compareBy { it.priority })
                reorderListByCompletion && !reorderListByPriority ->
                    itemsAsList.sortedWith(compareBy { it.status })
                else -> itemsAsList
            }

            for ((index, item) in sortedList.withIndex()) {
                when {
                    sortedAdapterList.size >= index + 1 ->
                        if (sortedAdapterList[index] != item.id)
                            sortedAdapterList[index] = item.id
                    else -> sortedAdapterList.add(index, item.id)
                }
            }
        } ?: run {
            sortedAdapterList.clear()
        }
    }

    private fun createCachedUiState(toDoList: ToDoList) {
        cachedUiState?.let {
            Timber.d("cachedUiState already exists")
        } ?: run {
            cachedUiState = hashMapOf()
            toDoList.items?.values?.forEach {
                cachedUiState?.put(it.id, CachedItem(false, it.status))
            }
        }
    }

    private fun updateItemObservables(toDoList: ToDoList) {
        toDoList.items?.forEach {
            when (itemObservables.containsKey(it.key)) {
                true -> (itemObservables[it.key] as? BehaviorSubject)?.onNext(it.value)
                else -> {
                    val subject = BehaviorSubject.createDefault(it.value).observeOn(uiThread)
                    itemObservables[it.key] = subject
                }
            }
        }
    }

    fun onItemCompleted(itemId: String, listId: String) {
        toDoListRepository.toDoList(listId)?.let { toDoList ->
            toDoList.items?.get(itemId)?.status?.let {
                toDoListRepository.completeItem(listId, itemId, !it)
            }
        }
    }

    fun updateCachedUi(itemId: String, type: CachedUiType, state: Boolean) {
        cachedUiState?.let { uiState ->
            uiState[itemId] = when (type) {
                CachedUiType.EXPANDED ->
                    uiState[itemId]?.let {
                        CachedItem(state, it.completed)
                    } ?: run {
                        CachedItem(state, false)
                    }
                CachedUiType.COMPLETED ->
                    uiState[itemId]?.let {
                        CachedItem(it.expanded, state)
                    } ?: run {
                        CachedItem(false, state)
                    }
            }
        }
    }

    fun onItemDeleted(itemId: String, listId: String) {
        toDoListRepository.deleteItem(listId, itemId)
        cachedUiState?.remove(itemId)
    }

    fun onItemPriorityChanged(itemId: String, listId: String, priority: ToDoListItem.Priority) {
        toDoListRepository.changeItemPriority(listId, itemId, priority)
    }

    enum class CachedUiType {
        EXPANDED,
        COMPLETED
    }

    data class CachedItem(val expanded: Boolean, val completed: Boolean)

    data class ObservablesAndUiState(val sortedAdapterItems: ArrayList<String>,
                                     val cachedUiState: HashMap<String, CachedItem>,
                                     val observables: HashMap<String, Observable<ToDoListItem>>)

    interface View : BasePresenter.View {
        fun onToDoListUpdate(update: ObservablesAndUiState)
        fun onTitleChanged(listTitle: String)
        fun onProgressChanged(progress: Int)
    }
}