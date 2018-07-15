package com.rbiggin.a2do2gether.ui.settings

import android.content.SharedPreferences
import com.rbiggin.a2do2gether.ui.base.BasePresenter
import com.rbiggin.a2do2gether.ui.checklists.ChecklistsFragment
import com.rbiggin.a2do2gether.utils.Constants
import com.rbiggin.a2do2gether.utils.Utilities
import javax.inject.Inject

/**
 * Presenter responsible for the Checklists Fragment
 */
class ChecklistsPresenter @Inject constructor(private val constants: Constants,
                                              utilities: Utilities,
                                              sharedPreferences: SharedPreferences) :
                                              BasePresenter<ChecklistsFragment>(sharedPreferences, utilities, constants),
                                              IntChecklistsPresenter{
    /**
     * View Will Show
     */
    override fun onViewWillShow() {

    }

    /**
     * View Will Hide
     */
    override fun onViewWillHide() {
        super.onViewWillHide()

    }
}