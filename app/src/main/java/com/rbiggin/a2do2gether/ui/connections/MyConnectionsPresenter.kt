package com.rbiggin.a2do2gether.ui.connections

import android.content.SharedPreferences
import com.rbiggin.a2do2gether.model.UserConnectionRequest
import com.rbiggin.a2do2gether.model.UserConnectionSearch
import com.rbiggin.a2do2gether.model.UserDetails
import com.rbiggin.a2do2gether.repository.*
import com.rbiggin.a2do2gether.ui.base.BasePresenter
import com.rbiggin.a2do2gether.ui.base.IntBaseFragment
import com.rbiggin.a2do2gether.utils.Constants
import com.rbiggin.a2do2gether.utils.Utilities
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

/**
 * Interface responsible for the My Connections Fragment
 */
class MyConnectionsPresenter @Inject constructor(private val connectionsRepo: ConnectionsRepository,
                                                 private val userRepo: UserProfileRepository,
                                                 private val utilities: Utilities,
                                                 sharedPreferences: SharedPreferences) :
        BasePresenter<MyConnectionsFragment>(sharedPreferences, utilities) {
    private var currentView: Constants.MyConnection? = null

    private var isProcessingBol: Boolean = false

    override fun onViewAttached(view: MyConnectionsFragment) {
        super.onViewAttached(view)

        disposeOnViewWillDetach(connectionsRepo.connectionsSubject
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val connections = utilities.hashMapToArray(it) as ArrayList<UserDetails>
                    view.onDisplayConnections(connections)
                })

        disposeOnViewWillDetach(connectionsRepo.pendingRequestsSubject
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    val requests = utilities.hashMapToArray(it) as ArrayList<UserConnectionRequest>
                    view.onDisplayConnectionRequests(requests)
                })

        disposeOnViewWillDetach(connectionsRepo.connectionSearchSubject
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    isProcessing(false)
                    if (!it.isEmpty()) {
                        this.view?.onDisplaySearchResults(it)
                    } else {
                        this.view?.onDisplaySearchResults(ArrayList())
                    }
                })
    }


    override fun onViewWillShow() {
        view?.onDisplayView(Constants.MyConnection.MAIN_VIEW)
        currentView = Constants.MyConnection.MAIN_VIEW
        connectionsRepo.setupConnectionWatchers()
    }

    override fun onViewWillHide() {
        super.onViewWillHide()
        connectionsRepo.presenterDetached()
    }

    fun onPlusButtonPressed() {
        if (currentView == Constants.MyConnection.MAIN_VIEW) {
            if (userRepo.isUserDiscoverable()) {
                view?.onDisplayView(Constants.MyConnection.SEARCH_VIEW)
                currentView = Constants.MyConnection.SEARCH_VIEW
            } else {
                view?.onDisplayDialogMessage(Constants.ERROR_USER_NOT_PUBLIC, null)
            }

        }
    }

    fun onSearchButtonPressed(searchString: String) {
        if (!isProcessingBol) {
            if (!view?.hasNetworkConnection()!!) {
                //todo display error message
            } else if (searchString.length < Constants.NUMBER_OF_CHARACTERS_IN_NICKNAME ||
                    searchString.contains(" ")) {
                view?.onClearSearchView()
                view?.onDisplayDialogMessage(Constants.ERROR_NICKNAME_STRUCTURE_ERROR, null)
            } else {
                //View?.displayNoResultsFound(false)
                connectionsRepo.connectionSearchSubmitted(searchString)
                isProcessing(true)
            }
        }
    }

    fun onRecyclerViewButtonPressed(type: Constants.ConnectionsActionType, targetUid: String) {
        when (type) {
            Constants.ConnectionsActionType.CONNECTION_REQUEST -> {
                connectionsRepo.submitConnectionRequest(targetUid)
                view?.onClearSearchView()
                view?.onDisplayDialogMessage(Constants.DB_CONNECTION_REQUEST_SUBMITTED, null)
                view?.onDisplayView(Constants.MyConnection.MAIN_VIEW)
                currentView = Constants.MyConnection.MAIN_VIEW
            }
            Constants.ConnectionsActionType.ACCEPT_CONNECTION_REQUEST -> {
                connectionsRepo.onConnectionRequestResponse(targetUid, true)
            }
            Constants.ConnectionsActionType.REJECT_CONNECTION_REQUEST -> {
                connectionsRepo.onConnectionRequestResponse(targetUid, false)
            }
            else -> {
                //todo throw exception
            }
        }
    }

    fun onMainActivityBackPressed(): Boolean {
        return when (currentView) {
            Constants.MyConnection.MAIN_VIEW -> {
                true
            }
            else -> {
                view?.onDisplayView(Constants.MyConnection.MAIN_VIEW)
                view?.onClearSearchView()
                //View?.displayNoResultsFound(false)
                currentView = Constants.MyConnection.MAIN_VIEW
                false
            }
        }
    }

    private fun isProcessing(processing: Boolean) {
        if (processing) {
            isProcessingBol = true
            view?.displayProgressSpinner(true)
        } else {
            isProcessingBol = false
            view?.displayProgressSpinner(false)
        }
    }

    interface View : IntBaseFragment {
        //fun onPendingConnectionRequestsChanged(): Disposable

        //fun onConnectionSearchResultsChanged(): Disposable

        fun onDisplayView(view: Constants.MyConnection)

        fun onDisplayConnections(connections: ArrayList<UserDetails>)

        fun onDisplaySearchResults(result: ArrayList<UserConnectionSearch>)

        fun onDisplayConnectionRequests(requests: ArrayList<UserConnectionRequest>)

        fun onClearSearchView()

        fun displayProgressSpinner(show: Boolean)
    }

    interface Button {
        fun onRecyclerViewButtonClicked(type: Constants.ConnectionsActionType, targetUid: String)
    }
}