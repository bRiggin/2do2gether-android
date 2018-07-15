package com.rbiggin.a2do2gether.ui.connections

import android.content.SharedPreferences
import com.rbiggin.a2do2gether.model.UserConnectionRequest
import com.rbiggin.a2do2gether.model.UserConnectionSearch
import com.rbiggin.a2do2gether.repository.*
import com.rbiggin.a2do2gether.ui.base.BasePresenter
import com.rbiggin.a2do2gether.utils.Constants
import com.rbiggin.a2do2gether.utils.Utilities
import javax.inject.Inject

/**
 * Presenter responsible for the My Connections Fragment
 */
class MyConnectionsPresenter @Inject constructor(private val constants: Constants,
                                                 private val connectionsRepo: IntConnectionsRepository,
                                                 private val userRepo: UserProfileRepository,
                                                 utilities: Utilities,
                                                 sharedPreferences: SharedPreferences) :
                                                 BasePresenter<MyConnectionsFragment>(sharedPreferences, utilities, constants),
                                                 IntMyConnectionsPresenter,
                                                 IntConnectionsRepositoryListener,
                                                 IntAuthRepositoryListener {
    /** Current view in view flipper */
    private var currentView: Constants.MyConnectionView? = null

    /**  */
    private var isProcessingBol: Boolean = false

    /**
     * View Will Show
     */
    override fun onViewWillShow() {
        mFragment?.onDisplayView(constants.connectionsMainView())
        currentView = constants.connectionsMainView()
        connectionsRepo.setPresenter(this)
        getUid()?.let {
            connectionsRepo.setup(it)
            connectionsRepo.getPendingConnectionRequests(it)
        }
    }

    /**
     * View Will Hide
     */
    override fun onViewWillHide() {
        super.onViewWillHide()
        connectionsRepo.detachPresenter()
    }

    /**
     * Plus Button Pressed
     */
    override fun onPlusButtonPressed() {
        if (currentView == constants.connectionsMainView()){
            if (userRepo.isUserDiscoverable()){
                mFragment?.onDisplayView(constants.connectionsSearchView())
                currentView = constants.connectionsSearchView()
            } else {
                mFragment?.onDisplayDialogMessage(constants.ERROR_USER_NOT_PUBLIC, null)
            }

        }
    }

    /**
     * Search Button Pressed
     */
    override fun onSearchButtonPressed(searchString: String) {
        if (!isProcessingBol){
            if (!mFragment?.hasNetworkConnection()!!){
                //todo display error message
            } else if(searchString.length < constants.NUMBER_OF_CHARACTERS_IN_NICKNAME ||
                    searchString.contains(" ")){
                mFragment?.onClearSearchView()
                mFragment?.onDisplayDialogMessage(constants.ERROR_NICKNAME_STRUCTURE_ERROR, null)

            }
            else {
                mFragment?.displayNoResultsFound(false)
                connectionsRepo.connectionSearchSubmitted(searchString)
                isProcessing(true)
            }
        }
    }



    /**
     *
     */
    override fun onRecyclerViewButtonPressed(type: Constants.ConnectionsActionType, targetUid: String) {
        when (type){
            constants.connectionsActionRequest() -> {
                connectionsRepo.submitConnectionRequest(targetUid)
            }
            constants.connectionsActionAccept() -> {

            }
            constants.connectionsActionReject() -> {

            }
            else -> {
                //todo throw exception
            }
        }
    }

    /**
     * Search Results
     */
    override fun onSearchResults(users: ArrayList<UserConnectionSearch>) {
        isProcessing(false)
        if (!users.isEmpty()){
            mFragment?.onDisplaySearchResults(users)
        } else {
            mFragment?.displayNoResultsFound(true)
        }
    }

    /**
     * Pending Connection Results
     */
    override fun onPendingConnectionResults(requests: ArrayList<UserConnectionRequest>) {
        if (!requests.isEmpty()){
            //mFragment?.onDisplaySearchResults(requests)
        } else {
            //mFragment?.displayNoResultsFound(true)
        }
    }

    /**
     * Main Activity Back Pressed
     */
    override fun onMainActivityBackPressed(): Boolean {
        return when (currentView){
            constants.connectionsMainView() -> {
                true
            } else -> {
                mFragment?.onDisplayView(constants.connectionsMainView())
                mFragment?.onClearSearchView()
                mFragment?.displayNoResultsFound(false)
                currentView = constants.connectionsMainView()
                false
            }
        }
    }

    /**
     * Is Processing
     */
    private fun isProcessing(processing: Boolean){
        if (processing){
            isProcessingBol = true
            mFragment?.displayProgressSpinner(true)
        } else {
            isProcessingBol = false
            mFragment?.displayProgressSpinner(false)
        }
    }

    /**
     * Auth State Change
     */
    override fun onAuthStateChange(response_id: Int, message: String?) {
        //todo perform logout
    }
}