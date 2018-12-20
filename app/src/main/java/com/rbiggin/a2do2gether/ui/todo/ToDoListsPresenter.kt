package com.rbiggin.a2do2gether.ui.todo

import com.rbiggin.a2do2gether.repository.ToDoListRepository
import com.rbiggin.a2do2gether.ui.base.BasePresenter
import com.rbiggin.a2do2gether.utils.Constants
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

class ToDoListsPresenter @Inject constructor(private val toDoListRepository: ToDoListRepository,
                                             private val uiThread: Scheduler,
                                             private val computationThread: Scheduler) :
        BasePresenter<ToDoListsPresenter.View>() {

    var toDoListsManifest: ArrayList<String> = ArrayList()

    private val popUpCommandSubject: PublishSubject<PopUpType> = PublishSubject.create()

    override fun onViewWillShow() {
        super.onViewWillShow()
        view?.let { view ->
            toDoListRepository.onToDoListsChanged()?.let { toDoLists ->
                disposeOnViewWillHide(toDoLists
                        .subscribeOn(computationThread)
                        .distinctUntilChanged()
                        .observeOn(uiThread)
                        .subscribe {
                            toDoListsManifest.clear()
                            toDoListsManifest.addAll(it)
                            view.onUpdateAdapter()
                        })
            }

            disposeOnViewWillHide(view.onNewItemCtreated()
                    .observeOn(computationThread)
                    .filter { text -> !text.trim().isEmpty() }
                    .map { text -> text.trim() }
                    .subscribe { text ->
                        view?.clearEditText()
                        toDoListRepository.addItem(view?.currentListId(), text)
                    })

            disposeOnViewWillHide(view.onMenuItemSelected()
                    .observeOn(uiThread)
                    .subscribe { menuButton ->
                        when (menuButton) {
                            Constants.MenuBarItem.PLUS ->
                                popUpCommandSubject.onNext(PopUpType.NEW_TO_DO_LIST)
                            Constants.MenuBarItem.DELETE ->
                                popUpCommandSubject.onNext(PopUpType.DELETE_TO_DO_LIST)
                            Constants.MenuBarItem.SHARE_PUBLISH ->
                                popUpCommandSubject.onNext(PopUpType.SHARE_TO_DO_LIST)
                        }
                    })

            disposeOnViewWillHide(view.onDisplayPopUpCommand(popUpCommandSubject.observeOn(uiThread)))
        }
    }

    enum class PopUpType {
        NEW_TO_DO_LIST,
        DELETE_TO_DO_LIST,
        RENAME_TO_DO_LIST,
        SHARE_TO_DO_LIST
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