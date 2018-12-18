package com.rbiggin.a2do2gether.ui.todo

import com.rbiggin.a2do2gether.model.ToDoList
import com.rbiggin.a2do2gether.repository.SettingsRepository
import com.rbiggin.a2do2gether.repository.ToDoListRepository
import com.rbiggin.a2do2gether.ui.base.BasePresenter
import io.reactivex.Scheduler
import javax.inject.Inject

class ToDoListPresenter @Inject constructor(private val settingsRepository: SettingsRepository,
                                            private val toDoListRepository: ToDoListRepository,
                                            private val uiThread: Scheduler) : BasePresenter<ToDoListFragment>() {

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

    fun onItemDeleted(itemId: String, listId: String) {
        toDoListRepository.deleteItem(listId, itemId)
    }

    interface View : BasePresenter.View {
        fun onToDoListUpdate(toDoList: ToDoList)
    }
}