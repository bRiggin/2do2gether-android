package com.rbiggin.a2do2gether.ui.todo

import android.animation.ValueAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
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
    private val cachedUiState: HashMap<String, Pair<Boolean, Boolean>> = HashMap()

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
            if(it.value.status)
                numOfCompletedItems++
        }
        items?.let {
            valueAnimator.setIntValues(currentProgress, ((numOfCompletedItems/it.size) * 100).toInt())
        } ?: run {
            valueAnimator.setIntValues(currentProgress, 0)
        }
        valueAnimator.duration = 500
        valueAnimator.start()
    }

    fun sortToDoListItems(toDoList: ToDoList): ArrayList<Pair<String, ToDoListItem>> {
        val list: ArrayList<Pair<String, ToDoListItem>> = ArrayList()
        //todo actually do some sorting
        toDoList.items?.forEach { list.add(Pair(it.key, it.value)) }
        return list
    }

    fun onItemCompleted(itemId: String, listId: String) {
        toDoListRepository.toDoList(listId)?.let { toDoList ->
            toDoList.items?.get(itemId)?.status?.let {
                toDoListRepository.completeItem(listId, itemId, !it)
            }
        }
    }

    fun updateCachedUi(itemId: String, type: CachedUiType, state: Boolean){
        when(type){
            CachedUiType.EXPANDED ->
                cachedUiState[itemId]?.let {
                    cachedUiState[itemId] = Pair(state, it.second)
                } ?: run {
                    cachedUiState[itemId] = Pair(state, false)
                }
            CachedUiType.COMPLETED ->
                cachedUiState[itemId]?.let {
                    cachedUiState[itemId] = Pair(it.first, state)
                } ?: run {
                    cachedUiState[itemId] = Pair(false, state)
                }
        }
    }

    fun onItemDeleted(itemId: String, listId: String) {
        toDoListRepository.deleteItem(listId, itemId)
        cachedUiState.remove(itemId)
    }

    enum class CachedUiType{
        EXPANDED,
        COMPLETED
    }

    interface View : BasePresenter.View {
        fun onToDoListUpdate(toDoList: ToDoList, cachedUiData: HashMap<String, Pair<Boolean, Boolean>>)
        fun onTitleChanged(listTitle: String)
        fun onProgressChanged(progress: Int)
    }
}