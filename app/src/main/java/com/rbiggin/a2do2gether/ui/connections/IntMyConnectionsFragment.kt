package com.rbiggin.a2do2gether.ui.connections

import com.rbiggin.a2do2gether.model.UserConnectionRequest
import com.rbiggin.a2do2gether.model.UserConnectionSearch
import com.rbiggin.a2do2gether.model.UserDetails
import com.rbiggin.a2do2gether.ui.base.IntBaseFragment
import com.rbiggin.a2do2gether.utils.Constants

/**
 * Insert class/object/interface/file description...
 */
interface IntMyConnectionsFragment : IntBaseFragment {
    fun onDisplayView(view: Constants.MyConnectionView)

    fun onDisplaySearchResults(result: ArrayList<UserConnectionSearch>)

    fun onDisplayConnectionRequests(requests: HashMap<String, UserConnectionRequest>)

    fun onClearSearchView()

    fun onDisplayDialogMessage(message_id: Int, message: String?)

    fun displayNoResultsFound(show: Boolean)

    fun displayProgressSpinner(show: Boolean)
}

interface IntMyConnectionsRecyclerButton {
    fun onRecyclerViewButtonClicked(type: Constants.ConnectionsActionType, targetUid: String)
}