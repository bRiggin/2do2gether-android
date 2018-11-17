package com.rbiggin.a2do2gether.firebase

import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.rbiggin.a2do2gether.utils.Constants

@Deprecated("Now replaced by other firebase modules")
object FirebaseDatabase : IntFirebaseDatabase {

    override fun doRead(dbRef: DatabaseReference, path: String,
                        listener: IntFirebaseDatabaseListener?, type: Constants.DatabaseApi) {
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

    override fun doEqualToRead(dbRef: DatabaseReference, path: String, sortBy: String, equalTo: String,
                               listener: IntFirebaseDatabaseListener?, type: Constants.DatabaseApi) {
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

    override fun doSortByRead(dbRef: DatabaseReference, path: String, searchValue: String, listener: IntFirebaseDatabaseListener?, type: Constants.DatabaseApi) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun doWrite(dbRef: DatabaseReference, path: String, data: HashMap<String, Any>,
                listener: IntFirebaseDatabaseListener?, type: Constants.DatabaseApi){
        val mReference = dbRef.child(path)
        mReference.updateChildren(data).addOnSuccessListener{
            listener?.onDatabaseResult(type, null, true, null)
        }.addOnFailureListener{
            listener?.onDatabaseResult(type, null, false, it.message)
        }
    }

    override fun doWrite(dbRef: DatabaseReference, path: String, data: HashMap<String, Any>){
        val mReference = dbRef.child(path)
        mReference.updateChildren(data)
    }

    override fun doPushWrite(dbRef: DatabaseReference, path: String, data: ArrayList<Any>) {
        val mReference = dbRef.child(path)
        for (item in data){
            mReference.push().setValue(item)
        }
    }

    override fun doDelete(dbRef: DatabaseReference, path: String) {
        val mReference = dbRef.child(path)
        mReference.removeValue()
    }
}