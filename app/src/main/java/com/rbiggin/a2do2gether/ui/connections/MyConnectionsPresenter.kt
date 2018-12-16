package com.rbiggin.a2do2gether.ui.connections

import androidx.annotation.VisibleForTesting
import com.rbiggin.a2do2gether.model.UserConnectionRequest
import com.rbiggin.a2do2gether.model.UserConnectionSearch
import com.rbiggin.a2do2gether.model.UserDetails
import com.rbiggin.a2do2gether.repository.*
import com.rbiggin.a2do2gether.ui.base.BasePresenter
import com.rbiggin.a2do2gether.utils.Constants
import com.rbiggin.a2do2gether.utils.Utilities
import io.reactivex.Scheduler
import javax.inject.Inject

class MyConnectionsPresenter @Inject constructor(private val connectionsRepo: ConnectionsRepository,
                                                 private val userRepo: UserProfileRepository,
                                                 private val settingsRepository: SettingsRepository,
                                                 private val utilities: Utilities,
                                                 private val uiThread: Scheduler) :
                                                 BasePresenter<MyConnectionsFragment>() {

    @VisibleForTesting
    var currentView: Window? = null

    @VisibleForTesting
    var isProcessingBol: Boolean = false

    private var profileIsPublic: Boolean = false

    override fun onViewAttached(view: MyConnectionsFragment) {
        super.onViewAttached(view)

        disposeOnViewWillDetach(connectionsRepo.connectionsSubject
                .observeOn(uiThread)
                .map {
                    utilities.hashMapToArray(it)
                }
                .subscribe {
                    view.onDisplayConnections(it)
                })

        disposeOnViewWillDetach(connectionsRepo.pendingRequestsSubject
                .observeOn(uiThread)
                .map{
                    utilities.hashMapToArray(it)
                }
                .subscribe {
                    view.onDisplayConnectionRequests(it)
                })

        disposeOnViewWillDetach(connectionsRepo.connectionSearchSubject
                .observeOn(uiThread)
                .subscribe {
                    isProcessing(false)
                    if (!it.isEmpty()) {
                        this.view?.onDisplaySearchResults(it)
                    } else {
                        this.view?.onDisplaySearchResults(ArrayList())
                    }
                })

        disposeOnViewWillDetach(settingsRepository.profilePublicSubject
                .observeOn(uiThread)
                .subscribe {
                    profileIsPublic = it
                })
    }


    override fun onViewWillShow() {
        view?.onDisplayView(Window.MAIN_VIEW)
        currentView = Window.MAIN_VIEW
        connectionsRepo.setupConnectionWatchers()
    }

    override fun onViewWillHide() {
        super.onViewWillHide()
        connectionsRepo.presenterDetached()
    }

    fun onPlusButtonPressed() {
        if (currentView == Window.MAIN_VIEW) {
            if (userRepo.isUserDiscoverable() && profileIsPublic) {
                view?.onDisplayView(Window.SEARCH_VIEW)
                currentView = Window.SEARCH_VIEW
            } else {
                view?.onDisplayDialogMessage(Constants.ERROR_USER_NOT_PUBLIC, null)
            }

        }
    }

    fun onSearchButtonPressed(searchString: String) {
        if (!isProcessingBol) {
            if (!view?.hasNetworkConnection()!!) {
                view?.onDisplayDialogMessage(Constants.ERROR_NO_NETWORK, null)
            } else if (searchString.length < Constants.NUMBER_OF_CHARACTERS_IN_NICKNAME ||
                    searchString.contains(" ")) {
                view?.onClearSearchView()
                view?.onDisplayDialogMessage(Constants.ERROR_NICKNAME_STRUCTURE_ERROR, null)
            } else {
                connectionsRepo.connectionSearchSubmitted(searchString)
                isProcessing(true)
            }
        }
    }

    fun onRecyclerViewButtonPressed(type: Action, targetUid: String) {
        when (type) {
            Action.CONNECTION_REQUEST -> {
                connectionsRepo.submitConnectionRequest(targetUid)
                view?.onClearSearchView()
                view?.onDisplayDialogMessage(Constants.DB_CONNECTION_REQUEST_SUBMITTED, null)
                view?.onDisplayView(Window.MAIN_VIEW)
                currentView = Window.MAIN_VIEW
            }
            Action.ACCEPT_CONNECTION_REQUEST -> {
                connectionsRepo.onConnectionRequestResponse(targetUid, true)
            }
            Action.REJECT_CONNECTION_REQUEST -> {
                connectionsRepo.onConnectionRequestResponse(targetUid, false)
            }
        }
    }

    fun onMainActivityBackPressed(): Boolean {
        return when (currentView) {
            MyConnectionsPresenter.Window.MAIN_VIEW -> {
                true
            }
            else -> {
                view?.onDisplayView(Window.MAIN_VIEW)
                view?.onClearSearchView()
                currentView = Window.MAIN_VIEW
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

    enum class Window {
        MAIN_VIEW,
        SEARCH_VIEW;
    }

    enum class Action{
        CONNECTION_REQUEST,
        ACCEPT_CONNECTION_REQUEST,
        REJECT_CONNECTION_REQUEST;
    }

    interface View : BasePresenter.View{
        fun onDisplayView(view: MyConnectionsPresenter.Window)

        fun onDisplayConnections(connections: ArrayList<UserDetails>)

        fun onDisplaySearchResults(result: ArrayList<UserConnectionSearch>)

        fun onDisplayConnectionRequests(requests: ArrayList<UserConnectionRequest>)

        fun onClearSearchView()

        fun displayProgressSpinner(show: Boolean)
    }

    interface Button {
        fun onRecyclerViewButtonClicked(type: Action, targetUid: String)
    }
}