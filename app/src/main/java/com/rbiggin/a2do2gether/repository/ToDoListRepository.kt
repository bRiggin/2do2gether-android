package com.rbiggin.a2do2gether.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.rbiggin.a2do2gether.firebase.FirebaseDatabaseWriter
import com.rbiggin.a2do2gether.firebase.FirebaseReadWatcher
import com.rbiggin.a2do2gether.model.ToDoListItem
import com.rbiggin.a2do2gether.model.ToDoListMap
import com.rbiggin.a2do2gether.utils.Constants
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import javax.inject.Inject

class ToDoListRepository @Inject constructor(private val uidProvider: UidProvider,
                                             private val databaseWriter: FirebaseDatabaseWriter):
                                             FirebaseReadWatcher.Listener {

    private var mDatabase: DatabaseReference? = null

    private var mUid: String? = null

    private var toDoListsWatcher: FirebaseReadWatcher? = null

    private val toDoListsWatcherMap: HashMap<String, FirebaseReadWatcher> = HashMap()

    private val toDoListsSubjectMap: HashMap<String, BehaviorSubject<ToDoListMap>> = HashMap()

    val toDoListsSubject: BehaviorSubject<ArrayList<String>> = BehaviorSubject.create()

    fun initialise() {
        mDatabase = com.google.firebase.database.FirebaseDatabase.getInstance().reference
        mUid = uidProvider.getUid()
        if (mUid.isNullOrBlank()) {
            throw NullPointerException("Uid provided by UidProvider has returned null")

        }
        mUid?.let {
            watchUserToDopLists(it)
        }

    }

    private fun watchUserToDopLists(id: String) {
        mDatabase?.let {
            toDoListsWatcher = FirebaseReadWatcher(it, "${Constants.FB_TO_DO_LISTS_REF}/$mUid",
                    Constants.DatabaseApi.READ_TO_DO_LISTS_REFS, this)
        }
    }

    fun presenterDetached() {
        toDoListsWatcher?.detachListener()
        toDoListsWatcher = null
    }

    fun getToDoListSubject(id: String) : BehaviorSubject<ToDoListMap> {
        if (toDoListsSubjectMap.containsKey(id)){
            toDoListsSubjectMap[id]?.let {
                return it
            } ?: throw Exception()
            //todo add info
        } else {
            val subject: BehaviorSubject<ToDoListMap> = BehaviorSubject.create()
            toDoListsSubjectMap[id] = subject
            return subject
        }
    }

    override fun onReadWatcherValueEvent(snapshot: DataSnapshot?, success: Boolean,
                                         errorMessage: String?, type: Constants.DatabaseApi) {
        when(type){
            Constants.DatabaseApi.READ_TO_DO_LISTS_REFS -> {
                if (success){
                    if (snapshot?.value is HashMap<*, *>){
                        buildManifest(snapshot.value as HashMap<*, *>)
                    }
                } else {
                    //todo timber
                }

            }
            Constants.DatabaseApi.READ_TO_DO_LIST -> {
                if (success){
                    snapshot?.let {
                        updateToDoList(it)
                    }
                } else {
                    //todo timber
                }
            }
            else -> {
                //todo add info
                Timber.d("ekjrbfre")
            }
        }
    }

    private fun buildManifest(map: HashMap<*, *>){
        val manifest = ArrayList<String>()
        for ((_, item) in map) {
            manifest.add(item.toString())
        }
        updateWatcherMap(manifest)

        //todo onNext on subject
    }

    private fun updateWatcherMap(keys: ArrayList<String>){
        for (key in keys){
            if (!toDoListsWatcherMap.containsKey(key)){
                mDatabase?.let {
                    toDoListsWatcherMap[key] = FirebaseReadWatcher(it,
                            "${Constants.FB_TO_DO_LISTS}/$key",
                            Constants.DatabaseApi.READ_TO_DO_LIST, this)
                }
            }
        }
    }

    private fun updateToDoList(snapshot: DataSnapshot){
        val creator = snapshot.child("creator").value.toString()
        val title = snapshot.child("list_title").value.toString()
        val items = constructToDoListItems(snapshot.child("items").value as HashMap<String, *>)

        val test = ToDoListMap(snapshot.key.toString(), title, creator, items)

    }

    private fun constructToDoListItems(items: HashMap<String, *>): HashMap<String, ToDoListItem>{
        val toDoListItems = HashMap<String, ToDoListItem>()
        for ((key, item) in items){
            val temp = constructToDoListItem(item as HashMap<String, *>)
            temp?.let{
                toDoListItems[key] = it
            }
        }
        return toDoListItems
    }

    private fun constructToDoListItem(items: HashMap<String, *>): ToDoListItem?{
        val creator = items[Constants.FB_TO_DO_CREATOR]
        val description = items[Constants.FB_TO_DO_DESCRIPTION]
        val status = items[Constants.FB_TO_DO_STATUS].toString().toBoolean()
        val completedBy: String? = items[Constants.FB_TO_DO_COMPLETED] as String?

        if (creator is String && description is String){
            return ToDoListItem(description, creator, status, completedBy)
        } else { return null }
    }
}