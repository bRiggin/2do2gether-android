package com.rbiggin.a2do2gether.ui.checklists

import com.rbiggin.a2do2gether.model.Checklist
import com.rbiggin.a2do2gether.repository.ChecklistRepository
import com.rbiggin.a2do2gether.ui.base.BasePresenter
import io.reactivex.Scheduler
import javax.inject.Inject

class ChecklistPresenter @Inject constructor(private val checklistRepository: ChecklistRepository,
                                             private val uiThread: Scheduler):
                                             BasePresenter<ChecklistFragment>() {
    fun onIdSupplied(id: String){
        disposeOnViewWillDetach(checklistRepository.getChecklistSubject(id)
                .observeOn(uiThread)
                .distinctUntilChanged()
                .subscribe {
                    view?.onChecklistUpdate(it)
                })
    }

    interface View : BasePresenter.View {
        fun onChecklistUpdate(checklist: Checklist)
    }
}