package com.rbiggin.a2do2gether.ui.todo

import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import com.rbiggin.a2do2gether.model.ToDoList
import com.rbiggin.a2do2gether.model.ToDoListItem
import com.rbiggin.a2do2gether.repository.SettingsRepository
import com.rbiggin.a2do2gether.repository.ToDoListRepository
import com.rbiggin.a2do2gether.ui.base.BasePresenter
import io.reactivex.Scheduler
import javax.inject.Inject

class ToDoListPresenter @Inject constructor(private val settingsRepository: SettingsRepository,
                                            private val toDoListRepository: ToDoListRepository,
                                            private val uiThread: Scheduler) : BasePresenter<ToDoListPresenter.View>() {

    private var currentProgress = 0
    private val valueAnimator: ValueAnimator = ValueAnimator.ofInt()
    private val cachedUiState: HashMap<String, CachedItem> = HashMap()

    init {
        valueAnimator.interpolator = AccelerateDecelerateInterpolator()
        valueAnimator.addUpdateListener {
            currentProgress = it.animatedValue as Int
            view?.onProgressChanged(currentProgress)
        }
    }

    fun onIdSupplied(id: String) {
        toDoListRepository.onToDoListChanged(id)?.let { subject ->
            disposeOnViewWillHide(subject
                    .observeOn(uiThread)
                    .distinctUntilChanged()
                    .subscribe {
                        view?.onTitleChanged(it.title)
                        updateProgressBar(it.items)
                        view?.onToDoListUpdate(it, cachedUiState)
                    })
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

    fun sortToDoListItems(toDoList: ToDoList): ArrayList<Pair<String, ToDoListItem>> {
        val list: ArrayList<Pair<String, ToDoListItem>> = ArrayList()
        //todo actually do some sorting
        toDoList.items?.toSortedMap()?.forEach { list.add(Pair(it.key, it.value)) }
        return list
    }

    fun onItemCompleted(itemId: String, listId: String) {
        toDoListRepository.toDoList(listId)?.let { toDoList ->
            toDoList.items?.get(itemId)?.status?.let {
                toDoListRepository.completeItem(listId, itemId, !it)
            }
        }
    }

    fun updateCachedUi(itemId: String, type: CachedUiType, state: Boolean) {
        cachedUiState[itemId] = when (type) {
            CachedUiType.EXPANDED ->
                cachedUiState[itemId]?.let {
                    CachedItem(state, it.completed)
                } ?: run {
                    CachedItem(state, false)
                }
            CachedUiType.COMPLETED ->
                cachedUiState[itemId]?.let {
                    CachedItem(it.expanded, state)
                } ?: run {
                    CachedItem(false, state)
                }
        }
    }

    fun onItemDeleted(itemId: String, listId: String) {
        toDoListRepository.deleteItem(listId, itemId)
        cachedUiState.remove(itemId)
    }

    fun onItemPriorityChanged(itemId: String, listId: String, priority: ToDoListItem.Priority){
        toDoListRepository.changeItemPriority(listId, itemId, priority)
    }

    enum class CachedUiType {
        EXPANDED,
        COMPLETED
    }

    data class CachedItem(val expanded: Boolean, val completed: Boolean)

    interface View : BasePresenter.View {
        fun onToDoListUpdate(toDoList: ToDoList, cachedUiState: HashMap<String, CachedItem>)
        fun onTitleChanged(listTitle: String)
        fun onProgressChanged(progress: Int)
    }
}