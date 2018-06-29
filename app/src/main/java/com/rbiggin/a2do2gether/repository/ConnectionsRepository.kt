package com.rbiggin.a2do2gether.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.rbiggin.a2do2gether.firebase.IntFirebaseDatabase
import com.rbiggin.a2do2gether.firebase.IntFirebaseDatabaseListener
import com.rbiggin.a2do2gether.model.UserConnectionRequest
import com.rbiggin.a2do2gether.model.UserConnectionSearch
import com.rbiggin.a2do2gether.utils.Constants
import javax.inject.Inject

/**
 * Handles the storage and update of user's connections
 */
class ConnectionsRepository @Inject constructor(private val databaseApi: IntFirebaseDatabase,
                                                private val constants: Constants) :
                                                IntConnectionsRepository,
                                                IntFirebaseDatabaseListener {
    /** Fragment Listener */
    private var mFragmentListener: IntConnectionsRepositoryListener? = null

    /** Database Reference */
    private var mDatabase: DatabaseReference? = null

    /** ... */
    private lateinit var mUid: String

    /**
     * Set Fragment
     */
    override fun setPresenter(listener: IntConnectionsRepositoryListener) {
        mFragmentListener = listener
    }

    /**
     * Detach Fragment
     */
    override fun detachPresenter() {
        mFragmentListener = null
    }

    /**
     * Setup
     */
    override fun setup(uid: String) {
        if (mFragmentListener == null){
            throw ExceptionInInitializerError("ConnectionsRepository, setup: Cannot setup connections" +
                    "repository without first having set \"mFragmentListener: IntConnectionsRepositoryListener\"")
        } else{
            mDatabase = com.google.firebase.database.FirebaseDatabase.getInstance().reference
            mUid = uid
        }
    }

    /**
     * Connection Search Submitted
     */
    override fun connectionSearchSubmitted(searchString: String) {
        mDatabase?.let {
            databaseApi.doEqualToRead(it, constants.FB_USER_PROFILE, constants.FB_NICKNAME,
                                      searchString, this, constants.dbApiFindUsers())
        }
    }

    /**
     * Get Pending Connection Requests
     */
    override fun getPendingConnectionRequests(uid: String) {
        mDatabase?.let {
            databaseApi.doRead(it, "${constants.FB_CONNECTION_REQUEST}/$uid", this,
                    constants.dbApiFindPendingConnections())
        }
    }

    /**
     * Database Read Result
     */
    override fun onDatabaseResult(type: Constants.DatabaseApiType, data: DataSnapshot?, success: Boolean, message: String?) {
        when (type){
            constants.dbApiFindUsers() -> {
                if (success){
                    data?.apply {
                        handleConnectionSearchResults(data)
                    }
                } else {

                }
            }
            constants.dbApiFindPendingConnections() -> {
                if (success){
                    data?.apply {
                        handlePendingConnectionsResults(data)
                    }
                } else {

                }
            }
            else -> {
                //todo throw exception
            }
        }
    }

    /**
     * Handle Connection Search Results
     */
    private fun handleConnectionSearchResults(data: DataSnapshot){
        val users = ArrayList<UserConnectionSearch>()
        for (foundUser in data.children){
            if (foundUser.child(constants.FB_DISCOVERABLE).value.toString() == "true"){
                val first_name = foundUser.child(constants.FB_FIRST_NAME).value.toString()
                val second_name = foundUser.child(constants.FB_SECOND_NAME).value.toString()
                val nickname = foundUser.child(constants.FB_NICKNAME).value.toString()
                val uid = foundUser.key.toString()
                var type = constants.searchResultNewConnection()
                if (mUid == foundUser.key.toString()){
                    type = constants.searchResultSelf()
                }
                if (false){
                    //todo look for already connected
                }
                users.add(UserConnectionSearch(first_name, second_name, nickname, uid, type))
            }
        }
        mFragmentListener?.onSearchResults(users)
    }

    /**
     * Handle Pending Connections Results
     */
    private fun handlePendingConnectionsResults(data: DataSnapshot){
        val requests = ArrayList<UserConnectionRequest>()
        for (foundRequest in data.children){
            val first_name = foundRequest.child(constants.FB_FIRST_NAME).value.toString()
            val second_name = foundRequest.child(constants.FB_SECOND_NAME).value.toString()
            val nickname = foundRequest.child(constants.FB_NICKNAME).value.toString()
            val uid = foundRequest.key.toString()
            requests.add(UserConnectionRequest(first_name, second_name, nickname, uid))
        }
        mFragmentListener?.onPendingConnectionResults(requests)
    }

    /**
     * Submit Connection Request
     */
    override fun submitConnectionRequest(targetUid: String) {
        val connectionRequest = hashMapOf(mUid to false as Any)

        val path = "${constants.FB_CONNECTION_REQUEST}/$targetUid"

        mDatabase?.let {
            databaseApi.doWrite(it, path, connectionRequest)
        } ?: throw ExceptionInInitializerError()
    }
}