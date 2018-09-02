package com.rbiggin.a2do2gether.firebase

import com.google.firebase.database.DatabaseReference

object FirebaseDatabaseWriter {

    fun doWrite(dbRef: DatabaseReference, path: String, data: HashMap<String, Any>){
        val mReference = dbRef.child(path)
        mReference.updateChildren(data)
    }

    fun doPushWrite(dbRef: DatabaseReference, path: String, data: ArrayList<Any>) {
        val mReference = dbRef.child(path)
        for (item in data){
            mReference.push().setValue(item)
        }
    }

    fun doPushWrite(dbRef: DatabaseReference, path: String, data: HashMap<String, Any>) {
        val mReference = dbRef.child(path)
        mReference.push().updateChildren(data)
    }

    fun doDelete(dbRef: DatabaseReference, path: String) {
        val mReference = dbRef.child(path)
        mReference.removeValue()
    }
}