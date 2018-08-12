package com.rbiggin.a2do2gether.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.rbiggin.a2do2gether.utils.Constants

class FirebaseReadEqualWatcher(dbRef: DatabaseReference,
                               path: String,
                               sortBy: String,
                               equalTo: String,
                               type: Constants.DatabaseApi,
                               private var listener: FirebaseReadEqualWatcher.Listener?) {

    private val mReference: DatabaseReference? = null
    private lateinit var mValuerEventListener: ValueEventListener

    init{
        doEqualToRead(dbRef, path, sortBy, equalTo, type, listener)
    }

    private fun doEqualToRead(dbRef: DatabaseReference, path: String, sortBy: String, equalTo: String,
                              type: Constants.DatabaseApi, listener: FirebaseReadEqualWatcher.Listener?) {
        val mReference = dbRef.child(path).orderByChild(sortBy).equalTo(equalTo)
        mReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listener?.onReadEqualWatcherValueEvent(dataSnapshot, true, null, type)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                listener?.onReadEqualWatcherValueEvent( null, false, databaseError.message, type)
            }
        })
    }

    fun detachListener(){
        mReference?.removeEventListener(mValuerEventListener)
        listener = null
    }

    interface Listener{
        fun onReadEqualWatcherValueEvent(snapshot: DataSnapshot?,
                                         success: Boolean,
                                         errorMessage: String?,
                                         type: Constants.DatabaseApi)
    }
}