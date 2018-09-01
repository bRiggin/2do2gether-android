package com.rbiggin.a2do2gether.ui.settings

import com.rbiggin.a2do2gether.repository.ChecklistRepository
import com.rbiggin.a2do2gether.ui.base.BasePresenter
import com.rbiggin.a2do2gether.ui.checklists.ChecklistsFragment
import io.reactivex.Scheduler
import javax.inject.Inject

class ChecklistsPresenter @Inject constructor(private val checklistRepository: ChecklistRepository,
                                              private val uiThread: Scheduler):
                                              BasePresenter<ChecklistsFragment>() {

    override fun onViewAttached(view: ChecklistsFragment) {
        super.onViewAttached(view)

        disposeOnViewWillDetach(checklistRepository.checklistsSubject
                .observeOn(uiThread)
                .distinctUntilChanged()
                .subscribe {
                    view.onCheckListManifestUpdate(it)
                })
    }

    interface View : BasePresenter.View {
        fun onCheckListManifestUpdate(manifest: ArrayList<String>)
    }
}