package com.rbiggin.a2do2gether.ui.settings

import com.rbiggin.a2do2gether.repository.ChecklistRepository
import com.rbiggin.a2do2gether.ui.base.BasePresenter
import com.rbiggin.a2do2gether.ui.checklists.ChecklistsFragment
import com.rbiggin.a2do2gether.ui.main.MainActivity
import com.rbiggin.a2do2gether.utils.Constants
import io.reactivex.Scheduler
import javax.inject.Inject

class ChecklistsPresenter @Inject constructor(private val checklistRepository: ChecklistRepository,
                                              private val uiThread: Scheduler,
                                              private val computationThread: Scheduler) :
                                              BasePresenter<ChecklistsFragment>(){

    var checklistManifest: ArrayList<String> = ArrayList()

    override fun onViewAttached(view: ChecklistsFragment) {
        super.onViewAttached(view)

        disposeOnViewWillDetach(checklistRepository.checklistsSubject
                .observeOn(uiThread)
                .distinctUntilChanged()
                .subscribe {
                    checklistManifest.clear()
                    checklistManifest.addAll(it)
                    view.onUpdateAdapter()
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
                    .subscribe { text ->
                        view?.clearEditText()
                        checklistRepository.addItem(view?.currentListId(), text)
                    })

            disposeOnViewWillHide(it.menuItemSubject
                    .observeOn(uiThread)
                    .subscribe { menuButton ->
                        when (menuButton){
                            Constants.MenuBarItem.PLUS -> {
                                it.displayNewChecklistDialog()
                            }
                            Constants.MenuBarItem.DELETE -> {
                                it.displayDeleteChecklistDialog()
                            }
                            Constants.MenuBarItem.SHARE_PUBLISH -> {
                                it.displayPublishChecklistDialog()
                            }
                        }
                    })
        }
    }

    fun deleteCurrentChecklist(currentIndex: Int){
        val index = when(currentIndex){
            0 -> {
                0
            }
            else ->{
                currentIndex - 1
            }
        }
        view?.setAdapterResetIndex(index)
        view?.currentListId()?.let{
            checklistRepository.deleteChecklist(it)
        }
    }

    fun newCurrentChecklist(title: String){
        view?.setAdapterResetIndex(checklistManifest.size + 1)
        checklistRepository.newChecklist(title)
    }

    interface View : BasePresenter.View {
        fun onUpdateAdapter()

        fun clearEditText()

        fun setAdapterResetIndex(index: Int)

        fun currentListId(): String?

        fun displayNewChecklistDialog()

        fun displayDeleteChecklistDialog()

        fun displayPublishChecklistDialog()
    }
}