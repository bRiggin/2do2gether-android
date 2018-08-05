package com.rbiggin.a2do2gether.repository

import com.rbiggin.a2do2gether.model.UserConnectionRequest
import com.rbiggin.a2do2gether.model.UserConnectionSearch
import com.rbiggin.a2do2gether.model.UserDetails

/**
 * Defines Connections Repository calls
 */
interface IntConnectionsRepository {

    fun setup(uid: String)

    fun presenterDetached()

    fun setupConnectionWatchers()

    fun connectionSearchSubmitted(searchString: String)

    fun submitConnectionRequest(targetUid: String)

    fun onConnectionRequestResponse(uid: String, accepted: Boolean)
}

/**
 * Defines callbacks from Connections Repository
 */
interface IntConnectionsRepositoryListener {
    fun onSearchResults(users: ArrayList<UserConnectionSearch>)

    fun onPendingConnectionResults(requests: ArrayList<UserConnectionRequest>)
}