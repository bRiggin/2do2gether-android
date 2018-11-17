package com.rbiggin.a2do2gether.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.rbiggin.a2do2gether.firebase.FirebaseDatabaseWriter
import com.rbiggin.a2do2gether.firebase.FirebaseReadWatcher
import com.rbiggin.a2do2gether.model.ChecklistMap
import com.rbiggin.a2do2gether.model.ToDoListItem
import com.rbiggin.a2do2gether.model.ToDoListMap
import com.rbiggin.a2do2gether.utils.Constants
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import javax.inject.Inject

class ToDoListRepository @Inject constructor(private val uidProvider: UidProvider,
                                             private val databaseWriter: FirebaseDatabaseWriter) :
        FirebaseReadWatcher.Listener {

    private val fbDbToDoListKey: String = "to_do_lists"
    private val fbDbToDoListReferencesKey: String = "to_do_lists_references"

    private var mDatabase: DatabaseReference? = null

    private var uid: String? = null

    private var toDoListsWatcher: FirebaseReadWatcher? = null

    private val toDoListsWatcherMap: HashMap<String, FirebaseReadWatcher> = HashMap()

    private val toDoListsSubjectMap: HashMap<String, BehaviorSubject<ToDoListMap>> = HashMap()

    val toDoListsSubject: BehaviorSubject<ArrayList<String>> = BehaviorSubject.create()

    fun initialise() {
        mDatabase = com.google.firebase.database.FirebaseDatabase.getInstance().reference
        uid = uidProvider.getUid()
        if (uid.isNullOrBlank()) {
            throw NullPointerException("Uid provided by UidProvider has returned null")
        }
        uid?.let {
            watchUserToDoLists(it)
        }
    }

    private fun watchUserToDoLists(id: String) {
        mDatabase?.let {
            toDoListsWatcher = FirebaseReadWatcher(it, "$fbDbToDoListReferencesKey/$uid",
                    Constants.DatabaseApi.READ_TO_DO_LISTS_REFS, this)
        }
    }

    fun presenterDetached() {
        toDoListsWatcher?.detachListener()
        toDoListsWatcher = null

        for ((_, watcher) in toDoListsWatcherMap) {
            watcher.detachListener()
        }
    }

    fun onToDoListChanged(id: String): BehaviorSubject<ToDoListMap> {
        if (toDoListsSubjectMap.containsKey(id)) {
            toDoListsSubjectMap[id]?.let {
                return it
            }
                    ?: throw IllegalArgumentException("ToDoListRepository, onToDoListChanged: invalid list id request")
        } else {
            val subject: BehaviorSubject<ToDoListMap> = BehaviorSubject.create()
            toDoListsSubjectMap[id] = subject
            return subject
        }
    }

    override fun onReadWatcherValueEvent(snapshot: DataSnapshot?, success: Boolean,
                                         errorMessage: String?, type: Constants.DatabaseApi) {
        when (type) {
            Constants.DatabaseApi.READ_TO_DO_LISTS_REFS -> {
                if (success) {
                    if (snapshot?.value is HashMap<*, *>) {
                        buildManifest(snapshot.value as HashMap<*, *>)
                    }
                } else {
                    //todo timber
                }
            }
            Constants.DatabaseApi.READ_TO_DO_LIST -> {
                if (success) {
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

    private fun buildManifest(map: HashMap<*, *>) {
        val manifest = ArrayList<String>()
        for ((_, item) in map) {
            manifest.add(item.toString())
        }
        updateWatcherMap(manifest)

        //todo onNext on subject
    }

    private fun updateWatcherMap(keys: ArrayList<String>) {
        for (key in keys) {
            if (!toDoListsWatcherMap.containsKey(key)) {
                mDatabase?.let {
                    toDoListsWatcherMap[key] = FirebaseReadWatcher(it,
                            "$fbDbToDoListKey/$key",
                            Constants.DatabaseApi.READ_TO_DO_LIST, this)
                }
            }
        }
    }

    private fun updateToDoList(snapshot: DataSnapshot) {
//        val creator = snapshot.child("creator").value.toString()
//        val title = snapshot.child("list_title").value.toString()
//        val items = constructToDoListItems(snapshot.child("items").value as HashMap<String, *>)
//
//        val test = ToDoListMap(snapshot.key.toString(), title, creator, items)

    }

    private fun constructToDoListItems(items: HashMap<String, *>): HashMap<String, ToDoListItem> {
        val toDoListItems = HashMap<String, ToDoListItem>()
        for ((key, item) in items) {
            val temp = constructToDoListItem(item as HashMap<String, *>)

            temp?.let {
                toDoListItems[key] = it
            }
        }
        return toDoListItems
    }

    private fun constructToDoListItem(items: HashMap<String, *>): ToDoListItem? {
        val creator = items[ToDoListItem.DataBaseKeys.CREATOR.key]
        val description = items[ToDoListItem.DataBaseKeys.CREATOR.key]
        val status = items[ToDoListItem.DataBaseKeys.CREATOR.key].toString().toBoolean()
        val completedBy: String? = items[ToDoListItem.DataBaseKeys.CREATOR.key] as String?
        val priority = items[ToDoListItem.DataBaseKeys.PRIORITY.key]

        return when {
            creator is String &&
                    description is String &&
                    priority is String -> {
                ToDoListItem(description,
                        creator,
                        status,
                        completedBy,
                        ToDoListItem.parsePriority(priority))
            }
            else -> {
                null
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun publishNewToDoListFromChecklist(title: String, checklist: ChecklistMap) {
        val toDoList = constructToDoListFromChecklist(title, checklist)
        val referencePath = "$fbDbToDoListReferencesKey/$uid"
        val data = toDoList.items as HashMap<String, Any>?

        mDatabase?.let { db ->
            data?.let { items ->
                val toDoListReference = databaseWriter.doPushWrite(db, fbDbToDoListKey, toDoList)
                databaseWriter.doPushWrite(db, referencePath, arrayListOf(toDoListReference.key as Any))
            }
        }
    }

    private fun constructToDoListFromChecklist(title: String, checklist: ChecklistMap): ToDoListMap {
        return uid?.let { id ->
            ToDoListMap("",
                    title,
                    id,
                    constructToDoListItemsFromChecklist(checklist.items))
        } ?: throw IllegalStateException()
    }

    private fun constructToDoListItemsFromChecklist(items: HashMap<String, String>): HashMap<String, ToDoListItem> {
        val newItems: HashMap<String, ToDoListItem> = HashMap()

        uid?.let { id ->
            items.forEach{
                newItems[it.key] = ToDoListItem(it.value,
                        id,
                        false,
                        null,
                        ToDoListItem.Priority.TODO)
            }
        } ?: IllegalStateException() //todo add description

        return newItems
    }
}