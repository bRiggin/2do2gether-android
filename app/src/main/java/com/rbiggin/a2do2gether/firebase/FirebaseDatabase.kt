package com.rbiggin.a2do2gether.firebase

import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.rbiggin.a2do2gether.utils.Constants

/**
 * Object that performs all Firebase database calls
 */
object FirebaseDatabase : IntFirebaseDatabase {

    /**
     * Do Read
     */
    override fun doRead(dbRef: DatabaseReference, path: String,
                        listener: IntFirebaseDatabaseListener?, type: Constants.DatabaseApiType) {
        val mReference = dbRef.child(path)

        val postListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listener?.onDatabaseResult(type, dataSnapshot, true, null)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                listener?.onDatabaseResult(type, null, false, databaseError.message)
            }
        }
        mReference.addValueEventListener(postListener)
    }

    /**
     * Do Equal to Read
     */
    override fun doEqualToRead(dbRef: DatabaseReference, path: String, sortBy: String, equalTo: String,
                               listener: IntFirebaseDatabaseListener?, type: Constants.DatabaseApiType) {
        val mReference = dbRef.child(path).orderByChild(sortBy).equalTo(equalTo)
        mReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listener?.onDatabaseResult(type, dataSnapshot, true, null)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                listener?.onDatabaseResult(type, null, false, databaseError.message)
            }
        })
    }

    override fun doSortByRead(dbRef: DatabaseReference, path: String, searchValue: String, listener: IntFirebaseDatabaseListener?, type: Constants.DatabaseApiType) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Do Write
     */
    override fun doWrite(dbRef: DatabaseReference, path: String, data: HashMap<String, Any>,
                listener: IntFirebaseDatabaseListener?, type: Constants.DatabaseApiType){
        val mReference = dbRef.child(path)
        mReference.updateChildren(data).addOnSuccessListener{
            listener?.onDatabaseResult(type, null, true, null)
        }.addOnFailureListener{
            listener?.onDatabaseResult(type, null, false, it.message)
        }
    }

    /**
     * Do Write
     */
    override fun doWrite(dbRef: DatabaseReference, path: String, data: HashMap<String, Any>){
        val mReference = dbRef.child(path)
        mReference.updateChildren(data)
    }

    /**
     * Do Push Write
     */
    override fun doPushWrite(dbRef: DatabaseReference, path: String, data: ArrayList<Any>) {
        val mReference = dbRef.child(path)
        for (item in data){
            mReference.push().setValue(item)
        }
    }
}