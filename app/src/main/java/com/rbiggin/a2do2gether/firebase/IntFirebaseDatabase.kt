package com.rbiggin.a2do2gether.firebase

import com.google.firebase.database.DatabaseReference
import com.rbiggin.a2do2gether.utils.Constants

interface IntFirebaseDatabase {
    fun doRead(dbRef: DatabaseReference, path: String, listener: IntFirebaseDatabaseListener?,
               type: Constants.DatabaseApi)

    fun doEqualToRead(dbRef: DatabaseReference, path: String, sortBy: String, equalTo: String,
                      listener: IntFirebaseDatabaseListener?, type: Constants.DatabaseApi)

    fun doSortByRead(dbRef: DatabaseReference, path: String, searchValue: String,
                      listener: IntFirebaseDatabaseListener?, type: Constants.DatabaseApi)

    fun doWrite(dbRef: DatabaseReference, path: String, data: HashMap<String, Any>,
                listener: IntFirebaseDatabaseListener?, type: Constants.DatabaseApi)

    fun doWrite(dbRef: DatabaseReference, path: String, data: HashMap<String, Any>)

    fun doDelete(dbRef: DatabaseReference, path: String)

    fun doPushWrite(dbRef: DatabaseReference, path: String, data: ArrayList<Any>)
}