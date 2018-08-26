package com.rbiggin.a2do2gether.ui.todo

import com.rbiggin.a2do2gether.ui.base.BasePresenter
import javax.inject.Inject

class ToDoListPresenter @Inject constructor() : BasePresenter<ToDoListFragment>() {
    interface View : BasePresenter.View {}
}