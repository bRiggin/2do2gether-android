package com.rbiggin.a2do2gether.ui.todo

import com.rbiggin.a2do2gether.model.ToDoList
import com.rbiggin.a2do2gether.model.ToDoListItem
import com.rbiggin.a2do2gether.repository.SettingsRepository
import com.rbiggin.a2do2gether.repository.ToDoListRepository
import com.rbiggin.a2do2gether.ui.base.BasePresenter
import io.reactivex.Scheduler
import javax.inject.Inject

class ToDoListPresenter @Inject constructor(private val settingsRepository: SettingsRepository,
                                            private val toDoListRepository: ToDoListRepository,
                                            private val uiThread: Scheduler) : BasePresenter<ToDoListFragment>() {

    override fun onViewAttached(view: ToDoListFragment) {
        super.onViewAttached(view)
        //disposeOnViewWillDetach(settingsRepository.onReorderListByCompletionChanged())
    }

    override fun onViewWillShow() {
        super.onViewWillShow()
    }

    fun onIdSupplied(id: String) {
        toDoListRepository.onToDoListChanged(id)?.let { subject ->
            disposeOnViewWillDetach(subject
                    .observeOn(uiThread)
                    .distinctUntilChanged()
                    .subscribe {
                        view?.onToDoListUpdate(it)
                    })
        }
    }

    fun sortToDoListItems(toDoList: ToDoList): ArrayList<Pair<String, ToDoListItem>>{
        val list: ArrayList<Pair<String, ToDoListItem>> = ArrayList()
        //todo actually do some sorting
        toDoList.items?.forEach { list.add(Pair(it.key, it.value)) }
        return list
    }

    fun onItemDeleted(itemId: String, listId: String) {
        toDoListRepository.deleteItem(listId, itemId)
    }

    interface View : BasePresenter.View {
        fun onToDoListUpdate(toDoList: ToDoList)
    }
}