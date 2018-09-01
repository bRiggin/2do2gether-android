package com.rbiggin.a2do2gether.ui.settings

import com.rbiggin.a2do2gether.repository.ChecklistRepository
import com.rbiggin.a2do2gether.ui.base.BasePresenter
import com.rbiggin.a2do2gether.ui.checklists.ChecklistsFragment
import io.reactivex.Scheduler
import javax.inject.Inject

class ChecklistsPresenter @Inject constructor(private val checklistRepository: ChecklistRepository,
                                              private val uiThread: Scheduler,
                                              private val computationThread: Scheduler) :
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

    override fun onViewWillShow() {
        super.onViewWillShow()
        view?.let {
            disposeOnViewWillHide(it.newItemSubject
                    .observeOn(computationThread)
                    .filter {text ->
                        !text.trim().isEmpty()
                    }
                    .subscribe {text ->
                        view?.clearEditText()
                        checklistRepository.addItem(view?.currentListId(), text)
                    })
        }

    }

    interface View : BasePresenter.View {
        fun onCheckListManifestUpdate(manifest: ArrayList<String>)

        fun clearEditText()

        fun currentListId(): String?
    }
}