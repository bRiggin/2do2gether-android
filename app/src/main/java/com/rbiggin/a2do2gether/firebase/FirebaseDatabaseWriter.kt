package com.rbiggin.a2do2gether.firebase

import com.google.firebase.database.DatabaseReference
import com.rbiggin.a2do2gether.model.ToDoList

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

    @Suppress("UNCHECKED_CAST")
    fun doPushWrite(dbRef: DatabaseReference, path: String, data: ToDoList): String? {
        val mReference = dbRef.child(path).push()
        mReference.child(ToDoList.DataBaseKeys.CREATOR.key).setValue(data.creator)
        mReference.child(ToDoList.DataBaseKeys.TITLE.key).setValue(data.title)
        mReference.child(ToDoList.DataBaseKeys.ITEMS.key).updateChildren(data.items as HashMap<String, Any>)
        return  mReference.key
    }

    fun doDelete(dbRef: DatabaseReference, path: String) {
        val mReference = dbRef.child(path)
        mReference.removeValue()
    }
}