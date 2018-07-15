package com.rbiggin.a2do2gether.repository

import com.rbiggin.a2do2gether.model.UserConnectionRequest
import com.rbiggin.a2do2gether.model.UserConnectionSearch
import com.rbiggin.a2do2gether.model.UserDetails

/**
 * Defines Connections Repository calls
 */
interface IntConnectionsRepository {
    fun setPresenter(listener: IntConnectionsRepositoryListener)

    fun detachPresenter()

    fun setup(uid: String)

    fun connectionSearchSubmitted(searchString: String)

    fun submitConnectionRequest(targetUid: String)

    fun getPendingConnectionRequests(uid: String)
}

/**
 * Defines callbacks from Connections Repository
 */
interface IntConnectionsRepositoryListener {
    fun onSearchResults(users: ArrayList<UserConnectionSearch>)

    fun onPendingConnectionResults(requests: HashMap<String, UserConnectionRequest>)
}