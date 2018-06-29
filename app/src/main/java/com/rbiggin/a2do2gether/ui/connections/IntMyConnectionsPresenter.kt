package com.rbiggin.a2do2gether.ui.connections

import com.rbiggin.a2do2gether.ui.base.IntBasePresenter
import com.rbiggin.a2do2gether.utils.Constants

/**
 * Defines specific functions to be implemented by checklists presenter.
 */
interface IntMyConnectionsPresenter : IntBasePresenter<MyConnectionsFragment> {
    fun onPlusButtonPressed()

    fun onMainActivityBackPressed(): Boolean

    fun onSearchButtonPressed(searchString: String)

    fun onRecyclerViewButtonPressed(type: Constants.ConnectionsActionType, targetUid: String)
}