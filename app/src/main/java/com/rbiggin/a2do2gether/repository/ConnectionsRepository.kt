package com.rbiggin.a2do2gether.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.rbiggin.a2do2gether.firebase.IntFirebaseDatabase
import com.rbiggin.a2do2gether.firebase.IntFirebaseDatabaseListener
import com.rbiggin.a2do2gether.model.PendingRequests
import com.rbiggin.a2do2gether.model.UserConnectionRequest
import com.rbiggin.a2do2gether.model.UserConnectionSearch
import com.rbiggin.a2do2gether.utils.Constants
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

/**
 * Handles the storage and update of user's connections
 */
class ConnectionsRepository @Inject constructor(private val databaseApi: IntFirebaseDatabase,
                                                private val constants: Constants) :
                                                IntConnectionsRepository,
                                                IntFirebaseDatabaseListener {
    /** Database Reference */
    private var mDatabase: DatabaseReference? = null

    /** ... */
    private lateinit var mUid: String

    val pendingRequestsSubject = BehaviorSubject.create<ArrayList<UserConnectionRequest>>()

    private val connectionSearchSubject : PublishSubject<ArrayList<UserConnectionSearch>> = PublishSubject.create()

    /** ... */
    private val mConnectionRequests: ArrayList<UserConnectionRequest> = ArrayList()

    override fun presenterDetatched() {
        //todo remove disposables
    }

    override fun setup(uid: String) {
        mDatabase = com.google.firebase.database.FirebaseDatabase.getInstance().reference
        mUid = uid
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
                    data?.let { handleConnectionSearchResults(data) }
                }
            }
            constants.dbApiFindPendingConnections() -> {
                if (success){
                    data?.let { handlePendingConnectionsResults(data) }
                }
            }
            constants.dbApiReadUserDetails() -> {
                if (success){
                    data?.let { handlePendingConnectionDetails(data) }
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
        connectionSearchSubject.onNext(users)
    }

    /**
     * Handle Pending Connections Results
     */
    private fun handlePendingConnectionsResults(data: DataSnapshot){
        for (foundRequest in data.children){
            val uid = foundRequest.key.toString()
            mDatabase?.let {
                databaseApi.doRead(it, "${constants.FB_USER_PROFILE}/$uid", this,
                        constants.dbApiReadUserDetails())
            }
        }
    }

    /**
     * Handle Pending Connection Details
     */
    private fun handlePendingConnectionDetails(data: DataSnapshot){
        val firstName = data.child(constants.FB_FIRST_NAME).value.toString()
        val secondName = data.child(constants.FB_SECOND_NAME).value.toString()
        val nickname = data.child(constants.FB_NICKNAME).value.toString()
        val uid = data.key.toString()
        val request = UserConnectionRequest(firstName, secondName, nickname, uid)
        if (!mConnectionRequests.contains(request)){
            mConnectionRequests.add(request)
            pendingRequestsSubject.onNext(mConnectionRequests)
        } else {
            //todo could check if details have been updated.
        }
    }

    fun connectionSearchResults(): Subject<ArrayList<UserConnectionSearch>>{
        return  connectionSearchSubject
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

    interface User{
        fun onPendingConnectionRequestsChanged(): Disposable

        fun onConnectionSearchResultsChanged()
    }
}