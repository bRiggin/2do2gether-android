package com.rbiggin.a2do2gether.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.rbiggin.a2do2gether.utils.Constants

class FirebaseReadWatcher(dbRef: DatabaseReference, path: String, type: Constants.DatabaseApi,
                          listener: FirebaseReadWatcher.Listener) {

    private val reference: DatabaseReference = dbRef
    private val dbPath: String = path
    private var presenter: FirebaseReadWatcher.Listener? = listener
    private val resultType: Constants.DatabaseApi = type

    private lateinit var mReference: DatabaseReference
    private lateinit var mValuerEventListener: ValueEventListener

    init{
        doRead(reference, dbPath, presenter)
    }

    private fun doRead(dbRef: DatabaseReference, path: String, listener: FirebaseReadWatcher.Listener?) {
        mReference = dbRef.child(path)

        mValuerEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listener?.onReadWatcherValueEvent(dataSnapshot, true, null, resultType)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                listener?.onReadWatcherValueEvent(null, false, databaseError.message, resultType)
            }
        }
        mReference.addValueEventListener(mValuerEventListener)
    }

    fun detachListener(){
        mReference.removeEventListener(mValuerEventListener)
        presenter = null
    }

    interface Listener{
        fun onReadWatcherValueEvent(snapshot: DataSnapshot?,
                                    success: Boolean,
                                    errorMessage: String?,
                                    type: Constants.DatabaseApi)
    }
}