package com.rbiggin.a2do2gether.ui.todo

import android.content.SharedPreferences
import com.rbiggin.a2do2gether.ui.base.BasePresenter
import com.rbiggin.a2do2gether.utils.Utilities
import javax.inject.Inject

/**
 * Insert class/object/interface/file description...
 */
class ToDoListPresenter @Inject constructor(utilities: Utilities,
                                            sharedPreferences: SharedPreferences) :
                                            BasePresenter<ToDoListFragment>(sharedPreferences, utilities) {
    /**
     *
     */
    override fun onViewWillShow() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}