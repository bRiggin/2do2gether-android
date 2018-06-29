package com.rbiggin.a2do2gether.ui.settings

import com.rbiggin.a2do2gether.ui.base.BasePresenter
import com.rbiggin.a2do2gether.ui.checklists.ChecklistsFragment
import com.rbiggin.a2do2gether.utils.Constants
import javax.inject.Inject

/**
 * Presenter responsible for the Checklists Fragment
 */
class ChecklistsPresenter @Inject constructor(private val constants: Constants) :
                                              BasePresenter<ChecklistsFragment>(),
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