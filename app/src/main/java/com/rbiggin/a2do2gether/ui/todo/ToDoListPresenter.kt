package com.rbiggin.a2do2gether.ui.todo

import com.rbiggin.a2do2gether.ui.base.BasePresenter
import com.rbiggin.a2do2gether.utils.Constants
import javax.inject.Inject

/**
 * Insert class/object/interface/file description...
 */
class ToDoListPresenter @Inject constructor(private val constants: Constants) :
                                              BasePresenter<ToDoListFragment>(),
                                              IntToDoListPresenter{
    /**
     *
     */
    override fun onViewWillShow() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}