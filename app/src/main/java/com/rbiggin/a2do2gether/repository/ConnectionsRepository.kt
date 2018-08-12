package com.rbiggin.a2do2gether.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.rbiggin.a2do2gether.firebase.FirebaseReadEqualWatcher
import com.rbiggin.a2do2gether.firebase.FirebaseReadWatcher
import com.rbiggin.a2do2gether.firebase.IntFirebaseDatabase
import com.rbiggin.a2do2gether.firebase.IntFirebaseDatabaseListener
import com.rbiggin.a2do2gether.model.UserConnectionRequest
import com.rbiggin.a2do2gether.model.UserConnectionSearch
import com.rbiggin.a2do2gether.model.UserDetails
import com.rbiggin.a2do2gether.utils.Constants
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Handles the storage and update of user's connections
 */
class ConnectionsRepository @Inject constructor(private val databaseApi: IntFirebaseDatabase) :
                                                IntConnectionsRepository,
                                                IntFirebaseDatabaseListener,
                                                FirebaseReadEqualWatcher.Listener,
                                                FirebaseReadWatcher.Listener{
    /** Database Reference */
    private var mDatabase: DatabaseReference? = null

    /** ... */
    private lateinit var mUid: String

    private val connectionsMap: HashMap<String, FirebaseReadWatcher> = HashMap()
    private var connectionsWatcher: FirebaseReadWatcher? = null
    private val pendingRequestsMap: HashMap<String, FirebaseReadWatcher> = HashMap()
    private var pendingRequestsWatcher: FirebaseReadWatcher? = null
    private var searchResultWatcher: FirebaseReadEqualWatcher? = null


    val pendingRequestsSubject: BehaviorSubject<HashMap<String, UserConnectionRequest>>
            = BehaviorSubject.create<HashMap<String, UserConnectionRequest>>()

    val connectionsSubject: BehaviorSubject<HashMap<String, UserDetails>>
            = BehaviorSubject.create<HashMap<String, UserDetails>>()

    val connectionSearchSubject: PublishSubject<ArrayList<UserConnectionSearch>> = PublishSubject.create()

    private val mConnectionRequests: HashMap<String, UserConnectionRequest> = HashMap()
    private val mConnections: HashMap<String,UserDetails> = HashMap()

    override fun presenterDetached() {
        clearConnectionsMap()
        connectionsWatcher?.detachListener()
        connectionsWatcher = null
        clearPendingRequestMap()
        pendingRequestsWatcher?.detachListener()
        pendingRequestsWatcher = null
        searchResultWatcher?.detachListener()
        searchResultWatcher = null
    }

    private fun clearPendingRequestMap(){
        for ((_, reader) in pendingRequestsMap) {
            reader.detachListener()
        }
        pendingRequestsMap.clear()
    }

    private fun clearConnectionsMap(){
        for ((_, reader) in connectionsMap) {
            reader.detachListener()
        }
        connectionsMap.clear()
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
            val id = Constants.DatabaseApi.FIND_USERS.toString()
            mDatabase?.let {
                searchResultWatcher = FirebaseReadEqualWatcher(it, Constants.FB_USER_PROFILE,
                                                       Constants.FB_NICKNAME, searchString,
                                                       Constants.DatabaseApi.FIND_PENDING_CONNECTIONS,
                                                       this)
            }
        }

        mDatabase?.let {
            databaseApi.doEqualToRead(it, Constants.FB_USER_PROFILE, Constants.FB_NICKNAME,
                                      searchString, this, Constants.DatabaseApi.FIND_USERS)
        }
    }

    /**
     * Get Pending Connection Requests
     */
    override fun setupConnectionWatchers() {
        mDatabase?.let {
            pendingRequestsWatcher = FirebaseReadWatcher(it, "${Constants.FB_CONNECTION_REQUEST}/$mUid",
                    Constants.DatabaseApi.FIND_PENDING_CONNECTIONS, this)

            connectionsWatcher = FirebaseReadWatcher(it, "${Constants.FB_CONNECTIONS}/$mUid",
                    Constants.DatabaseApi.FIND_CONNECTIONS, this)

        }
    }

    /**
     * Database Read Result
     */
    override fun onDatabaseResult(type: Constants.DatabaseApi, data: DataSnapshot?, success: Boolean, message: String?) {
        when (type){
            Constants.DatabaseApi.FIND_USERS -> {
                if (success){
                    data?.let { handleConnectionSearchResults(data) }
                }
            }
            Constants.DatabaseApi.FIND_PENDING_CONNECTIONS -> {

            }
            Constants.DatabaseApi.READ_USER_DETAILS -> {
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
            if (foundUser.child(Constants.FB_DISCOVERABLE).value.toString() == "true"){
                val firstName = foundUser.child( Constants.FB_FIRST_NAME).value.toString()
                val secondName = foundUser.child(Constants.FB_SECOND_NAME).value.toString()
                val nickname = foundUser.child(Constants.FB_NICKNAME).value.toString()
                val uid = foundUser.key.toString()
                var type = Constants.ConnectionsSearchResult.NEW_CONNECTION
                if (mUid == foundUser.key.toString()){
                    type = Constants.ConnectionsSearchResult.SELF
                } else if (mConnections.containsKey(foundUser.key.toString())) {
                    type = Constants.ConnectionsSearchResult.EXISTING_CONNECTION
                }
                users.add(UserConnectionSearch(firstName, secondName, nickname, uid, type))
            }
        }
        connectionSearchSubject.onNext(users)
    }

    private fun handlePendingConnectionsResults(data: DataSnapshot){
        mConnectionRequests.clear()
        pendingRequestsSubject.onNext(mConnectionRequests)
        clearPendingRequestMap()

        for (foundRequest in data.children){
            val uid = foundRequest.key.toString()
            mDatabase?.let {
                val watcher = FirebaseReadWatcher(it, "${Constants.FB_USER_PROFILE}/$uid",
                        Constants.DatabaseApi.READ_CONNECTION_REQUEST_DETAILS, this)
                pendingRequestsMap.put(uid, watcher)
            }
        }
    }

    private fun handleConnectionsResults(data: DataSnapshot){
        mConnections.clear()
        connectionsSubject.onNext(mConnections)
        clearConnectionsMap()

        for (foundConnection in data.children){
            val uid = foundConnection.value.toString()
            mDatabase?.let {
                val watcher = FirebaseReadWatcher(it, "${Constants.FB_USER_PROFILE}/$uid",
                        Constants.DatabaseApi.READ_USER_DETAILS, this)
                pendingRequestsMap.put(uid, watcher)
            }
        }
    }

    override fun onReadWatcherValueEvent(snapshot: DataSnapshot?, success: Boolean,
                                         errorMessage: String?, type: Constants.DatabaseApi) {
        when (type){
            Constants.DatabaseApi.FIND_PENDING_CONNECTIONS ->{
                if (success){
                    snapshot?.let { handlePendingConnectionsResults(snapshot) }
                } else {
                    //todo add data to throwable
                    pendingRequestsSubject.onError(Throwable())
                }
            }
            Constants.DatabaseApi.READ_CONNECTION_REQUEST_DETAILS -> {
                if (success){
                    snapshot?.let { handlePendingConnectionDetails(snapshot) }
                }
            }
            Constants.DatabaseApi.FIND_CONNECTIONS -> {
                if (success){
                    snapshot?.let { handleConnectionsResults(snapshot) }
                }
            }
            Constants.DatabaseApi.READ_USER_DETAILS -> {
                if (success){
                    snapshot?.let { handleConnectionsDetails(snapshot) }
                }
            }
        }
    }

    override fun onReadEqualWatcherValueEvent(snapshot: DataSnapshot?, success: Boolean,
                                              errorMessage: String?, type: Constants.DatabaseApi) {
        when (type){
            Constants.DatabaseApi.FIND_USERS -> {
                if (success){
                    snapshot?.let { handleConnectionSearchResults(snapshot) }
                } else {
                    //todo add data to throwable
                    connectionSearchSubject.onError(Throwable())
                }
            }
        }
    }

    private fun handleConnectionsDetails(data: DataSnapshot){
        val firstName = data.child(Constants.FB_FIRST_NAME).value.toString()
        val secondName = data.child(Constants.FB_SECOND_NAME).value.toString()
        val nickname = data.child(Constants.FB_NICKNAME).value.toString()
        val uid = data.key.toString()
        val connection = UserDetails(firstName, secondName, nickname, uid, true)
        if (!mConnections.contains(uid)){
            mConnections[uid] = connection
            connectionsSubject.onNext(mConnections)
        } else {
            mConnections.remove(uid)
            mConnections[uid] = connection
            connectionsSubject.onNext(mConnections)
        }
    }

    private fun handlePendingConnectionDetails(data: DataSnapshot){
        val firstName = data.child(Constants.FB_FIRST_NAME).value.toString()
        val secondName = data.child(Constants.FB_SECOND_NAME).value.toString()
        val nickname = data.child(Constants.FB_NICKNAME).value.toString()
        val uid = data.key.toString()
        val request = UserConnectionRequest(firstName, secondName, nickname, uid)
        if (!mConnectionRequests.contains(uid)){
            mConnectionRequests[uid] = request
            pendingRequestsSubject.onNext(mConnectionRequests)
        } else {
            mConnectionRequests.remove(uid)
            mConnectionRequests[uid] = request
            pendingRequestsSubject.onNext(mConnectionRequests)
        }
    }

    override fun onConnectionRequestResponse(uid: String, accepted: Boolean) {
        val connectionResponse = hashMapOf(uid to true as Any)

        val path = "${Constants.FB_CONNECTION_REQUEST}/$mUid"

        mDatabase?.let {
            if (accepted){
                databaseApi.doWrite(it, path, connectionResponse)
            }

            databaseApi.doDelete(it, path)
        } ?: throw ExceptionInInitializerError()
    }

    /**
     * Submit Connection Request
     */
    override fun submitConnectionRequest(targetUid: String) {
        val connectionRequest = hashMapOf(mUid to false as Any)

        val path = "${Constants.FB_CONNECTION_REQUEST}/$targetUid"

        mDatabase?.let {
            databaseApi.doWrite(it, path, connectionRequest)
        } ?: throw ExceptionInInitializerError()
    }
}