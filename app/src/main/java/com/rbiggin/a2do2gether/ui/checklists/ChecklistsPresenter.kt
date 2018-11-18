package com.rbiggin.a2do2gether.ui.checklists

import com.rbiggin.a2do2gether.repository.ChecklistRepository
import com.rbiggin.a2do2gether.repository.ToDoListRepository
import com.rbiggin.a2do2gether.ui.base.BasePresenter
import com.rbiggin.a2do2gether.utils.Constants
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class ChecklistsPresenter @Inject constructor(private val checklistRepository: ChecklistRepository,
                                              private val toDoListRepository: ToDoListRepository,
                                              private val uiThread: Scheduler,
                                              private val computationThread: Scheduler) :
        BasePresenter<ChecklistsPresenter.View>() {

    var checklistManifest: ArrayList<String> = ArrayList()

    private val popUpCommandSubject: PublishSubject<PopUpType> = PublishSubject.create()

    override fun onViewAttached(view: ChecklistsPresenter.View) {
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
            disposeOnViewWillHide(it.onNewItemCtreated()
                    .observeOn(computationThread)
                    .filter { text ->
                        !text.trim().isEmpty()
                    }
                    .subscribe { text ->
                        view?.clearEditText()
                        checklistRepository.addItem(view?.currentListId(), text)
                    })

            disposeOnViewWillHide(it.onMenuItemSelected()
                    .observeOn(uiThread)
                    .subscribe { menuButton ->
                        when (menuButton) {
                            Constants.MenuBarItem.PLUS -> {
                                popUpCommandSubject.onNext(PopUpType.NEW_CHECKLIST)
                            }
                            Constants.MenuBarItem.DELETE -> {
                                popUpCommandSubject.onNext(PopUpType.DELETE_CHECKLIST)
                            }
                            Constants.MenuBarItem.SHARE_PUBLISH -> {
                                popUpCommandSubject.onNext(PopUpType.PUBLISH_CHECKLIST)
                            }
                        }
                    })

            disposeOnViewWillHide(it.onDisplayPopUpCommand(
                    popUpCommandSubject.observeOn(uiThread)))
        }
    }

    fun deleteCurrentChecklist(currentIndex: Int) {
        val index = when (currentIndex) {
            0 -> {
                0
            }
            else -> {
                currentIndex - 1
            }
        }
        view?.setAdapterResetIndex(index)
        view?.currentListId()?.let {
            checklistRepository.deleteChecklist(it)
        }
    }

    fun newCurrentChecklist(title: String) {
        view?.setAdapterResetIndex(checklistManifest.size + 1)
        checklistRepository.newChecklist(title)
    }

    fun onChecklistPublished(title: String) {
        val listId = view?.let{
            it.currentListId()
        }

        val checklist = listId?.let {
            checklistRepository.requestSpecificChecklist(it)
        }

        checklist?.let {
            toDoListRepository.publishNewToDoListFromChecklist(title, it)
        }
    }

    enum class PopUpType {
        NEW_CHECKLIST,
        DELETE_CHECKLIST,
        PUBLISH_CHECKLIST
    }

    interface View : BasePresenter.View {
        fun onNewItemCtreated(): Observable<String>

        fun onMenuItemSelected(): Observable<Constants.MenuBarItem>

        fun onUpdateAdapter()

        fun clearEditText()

        fun setAdapterResetIndex(index: Int)

        fun currentListId(): String?

        fun onDisplayPopUpCommand(popUpCommand: Observable<PopUpType>): Disposable
    }
}