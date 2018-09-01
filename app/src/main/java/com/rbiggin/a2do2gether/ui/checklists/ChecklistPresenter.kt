package com.rbiggin.a2do2gether.ui.checklists

import com.rbiggin.a2do2gether.model.ChecklistArray
import com.rbiggin.a2do2gether.model.ChecklistMap
import com.rbiggin.a2do2gether.repository.ChecklistRepository
import com.rbiggin.a2do2gether.ui.base.BasePresenter
import io.reactivex.Scheduler
import javax.inject.Inject

class ChecklistPresenter @Inject constructor(private val checklistRepository: ChecklistRepository,
                                             private val uiThread: Scheduler) :
                                             BasePresenter<ChecklistFragment>() {
    fun onIdSupplied(id: String) {
        disposeOnViewWillDetach(checklistRepository.getChecklistSubject(id)
                .observeOn(uiThread)
                .distinctUntilChanged()
                .map {
                    constructChecklistArray(it)
                }
                .distinctUntilChanged()
                .subscribe {
                    view?.onChecklistUpdate(it)
                })
    }

    private fun constructChecklistArray(checklistMap: ChecklistMap): ChecklistArray {
        checklistMap.items["values"]?.let {
            return ChecklistArray(checklistMap.id, checklistMap.title, it)
        } ?: throw IllegalStateException()
    }

    fun onItemDeleted(index: Int, listId: String) {
        checklistRepository.deleteItem(listId, index)
    }

    interface View : BasePresenter.View {
        fun onChecklistUpdate(checklist: ChecklistArray)
    }
}