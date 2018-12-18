package com.rbiggin.a2do2gether.repository

import androidx.annotation.VisibleForTesting
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.rbiggin.a2do2gether.firebase.FirebaseReadEqualWatcher
import com.rbiggin.a2do2gether.firebase.FirebaseReadWatcher
import com.rbiggin.a2do2gether.firebase.IntFirebaseDatabase
import com.rbiggin.a2do2gether.model.UserConnectionRequest
import com.rbiggin.a2do2gether.model.UserConnectionSearch
import com.rbiggin.a2do2gether.model.UserDetails
import com.rbiggin.a2do2gether.utils.Constants
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ConnectionsRepository @Inject constructor(private val databaseApi: IntFirebaseDatabase,
                                                private val uidProvider: UidProvider) :
        FirebaseReadEqualWatcher.Listener,
        FirebaseReadWatcher.Listener {

    private lateinit var dbRef: DatabaseReference
    private lateinit var uid: String

    @VisibleForTesting
    val connectionsMap: HashMap<String, FirebaseReadWatcher> = HashMap()
    @VisibleForTesting
    var connectionsWatcher: FirebaseReadWatcher? = null
    @VisibleForTesting
    val pendingRequestsMap: HashMap<String, FirebaseReadWatcher> = HashMap()
    @VisibleForTesting
    var pendingRequestsWatcher: FirebaseReadWatcher? = null
    @VisibleForTesting
    var searchResultWatcher: FirebaseReadEqualWatcher? = null

    val pendingRequestsSubject: BehaviorSubject<HashMap<String, UserConnectionRequest>> = BehaviorSubject.create<HashMap<String, UserConnectionRequest>>()
    val connectionsSubject: BehaviorSubject<HashMap<String, UserDetails>> = BehaviorSubject.create<HashMap<String, UserDetails>>()
    val connectionSearchSubject: PublishSubject<ArrayList<UserConnectionSearch>> = PublishSubject.create()

    private val mConnectionRequests: HashMap<String, UserConnectionRequest> = HashMap()
    private val mConnections: HashMap<String, UserDetails> = HashMap()

    fun initialise() {
        dbRef = FirebaseDatabase.getInstance().reference
        uidProvider.getUid()?.let { uid = it }
        if (!this::uid.isInitialized)
            throw UninitializedPropertyAccessException("Uid provided by UidProvider has returned null")
    }

    fun presenterDetached() {
        clearConnectionsMap()
        connectionsWatcher?.detachListener()
        connectionsWatcher = null
        clearPendingRequestMap()
        pendingRequestsWatcher?.detachListener()
        pendingRequestsWatcher = null
        searchResultWatcher?.detachListener()
        searchResultWatcher = null
    }

    private fun clearPendingRequestMap() {
        pendingRequestsMap.forEach { it.value.detachListener() }
        pendingRequestsMap.clear()
    }

    private fun clearConnectionsMap() {
        connectionsMap.forEach { it.value.detachListener() }
        connectionsMap.clear()
    }

    fun connectionSearchSubmitted(searchString: String) {
        searchResultWatcher = FirebaseReadEqualWatcher(dbRef, Constants.FB_USER_PROFILE,
                Constants.FB_NICKNAME, searchString,
                Constants.DatabaseApi.FIND_PENDING_CONNECTIONS,
                this)
    }

    fun setupConnectionWatchers() {
        pendingRequestsWatcher = FirebaseReadWatcher(dbRef, "${Constants.FB_CONNECTION_REQUEST}/$uid",
                Constants.DatabaseApi.FIND_PENDING_CONNECTIONS, this)

        connectionsWatcher = FirebaseReadWatcher(dbRef, "${Constants.FB_CONNECTIONS}/$uid",
                Constants.DatabaseApi.FIND_CONNECTIONS, this)
    }

    private fun handleConnectionSearchResults(data: DataSnapshot) {
        val users = ArrayList<UserConnectionSearch>()
        for (foundUser in data.children) {
            if (foundUser.child(Constants.FB_DISCOVERABLE).value.toString() == "true"
                    && foundUser.child(Constants.Setting.PROFILE_PRIVACY.value).value.toString() == "true") {
                val firstName = foundUser.child(Constants.FB_FIRST_NAME).value.toString()
                val secondName = foundUser.child(Constants.FB_SECOND_NAME).value.toString()
                val nickname = foundUser.child(Constants.FB_NICKNAME).value.toString()
                val uid = foundUser.key.toString()
                var type = Constants.ConnectionsSearchResult.NEW_CONNECTION
                if (this.uid == foundUser.key.toString()) {
                    type = Constants.ConnectionsSearchResult.SELF
                } else if (mConnections.containsKey(foundUser.key.toString())) {
                    type = Constants.ConnectionsSearchResult.EXISTING_CONNECTION
                }
                users.add(UserConnectionSearch(firstName, secondName, nickname, uid, type))
            }
        }
        connectionSearchSubject.onNext(users)
    }

    private fun handlePendingConnectionsResults(data: DataSnapshot) {
        mConnectionRequests.clear()
        pendingRequestsSubject.onNext(mConnectionRequests)
        clearPendingRequestMap()

        for (foundRequest in data.children) {
            val uid = foundRequest.key.toString()
            val watcher = FirebaseReadWatcher(dbRef, "${Constants.FB_USER_PROFILE}/$uid",
                    Constants.DatabaseApi.READ_CONNECTION_REQUEST_DETAILS, this)
            pendingRequestsMap[uid] = watcher
        }
    }

    private fun handleConnectionsResults(data: DataSnapshot) {
        mConnections.clear()
        connectionsSubject.onNext(mConnections)
        clearConnectionsMap()

        for (foundConnection in data.children) {
            val uid = foundConnection.value.toString()
            val watcher = FirebaseReadWatcher(dbRef, "${Constants.FB_USER_PROFILE}/$uid",
                    Constants.DatabaseApi.READ_USER_DETAILS, this)
            pendingRequestsMap[uid] = watcher
        }
    }

    override fun onReadWatcherValueEvent(snapshot: DataSnapshot?, success: Boolean,
                                         errorMessage: String?, type: Constants.DatabaseApi) {
        when (type) {
            Constants.DatabaseApi.FIND_PENDING_CONNECTIONS -> {
                when(success){
                    true -> snapshot?.let { handlePendingConnectionsResults(snapshot) }
                    false -> pendingRequestsSubject.onError(Throwable("todo, add a real mesaage"))
                }
            }
            Constants.DatabaseApi.READ_CONNECTION_REQUEST_DETAILS ->
                if (success)
                    snapshot?.let { handlePendingConnectionDetails(snapshot) }
            Constants.DatabaseApi.FIND_CONNECTIONS ->
                if (success)
                    snapshot?.let { handleConnectionsResults(snapshot) }
            Constants.DatabaseApi.READ_USER_DETAILS ->
                if (success)
                    snapshot?.let { handleConnectionsDetails(snapshot) }
        }
    }

    override fun onReadEqualWatcherValueEvent(snapshot: DataSnapshot?, success: Boolean,
                                              errorMessage: String?, type: Constants.DatabaseApi) {
        when (type) {
            Constants.DatabaseApi.FIND_USERS -> {
                if (success) {
                    snapshot?.let { handleConnectionSearchResults(snapshot) }
                } else {
                    //todo add data to throwable
                    connectionSearchSubject.onError(Throwable())
                }
            }
        }
    }

    private fun handleConnectionsDetails(data: DataSnapshot) {
        val firstName = data.child(Constants.FB_FIRST_NAME).value.toString()
        val secondName = data.child(Constants.FB_SECOND_NAME).value.toString()
        val nickname = data.child(Constants.FB_NICKNAME).value.toString()
        val uid = data.key.toString()
        val connection = UserDetails(firstName, secondName, nickname, uid, true)
        if (!mConnections.contains(uid)) {
            mConnections[uid] = connection
            connectionsSubject.onNext(mConnections)
        } else {
            mConnections.remove(uid)
            mConnections[uid] = connection
            connectionsSubject.onNext(mConnections)
        }
    }

    private fun handlePendingConnectionDetails(data: DataSnapshot) {
        val firstName = data.child(Constants.FB_FIRST_NAME).value.toString()
        val secondName = data.child(Constants.FB_SECOND_NAME).value.toString()
        val nickname = data.child(Constants.FB_NICKNAME).value.toString()
        val uid = data.key.toString()
        val request = UserConnectionRequest(firstName, secondName, nickname, uid)
        if (!mConnectionRequests.contains(uid)) {
            mConnectionRequests[uid] = request
            pendingRequestsSubject.onNext(mConnectionRequests)
        } else {
            mConnectionRequests.remove(uid)
            mConnectionRequests[uid] = request
            pendingRequestsSubject.onNext(mConnectionRequests)
        }
    }

    fun onConnectionRequestResponse(uid: String, accepted: Boolean) {
        val connectionResponse = hashMapOf(uid to true as Any)
        val path = "${Constants.FB_CONNECTION_REQUEST}/${this.uid}"
        if (accepted)
            databaseApi.doWrite(dbRef, path, connectionResponse)
        databaseApi.doDelete(dbRef, path)
    }

    fun submitConnectionRequest(targetUid: String) {
        val connectionRequest = hashMapOf(uid to false as Any)
        val path = "${Constants.FB_CONNECTION_REQUEST}/$targetUid"
        databaseApi.doWrite(dbRef, path, connectionRequest)
    }
}