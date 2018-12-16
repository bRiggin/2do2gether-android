package com.rbiggin.a2do2gether.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.rbiggin.a2do2gether.firebase.FirebaseDatabaseWriter
import com.rbiggin.a2do2gether.firebase.FirebaseReadWatcher
import com.rbiggin.a2do2gether.model.Checklist
import com.rbiggin.a2do2gether.model.ToDoListItem
import com.rbiggin.a2do2gether.model.ToDoListMap
import com.rbiggin.a2do2gether.utils.Constants
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import java.lang.IllegalArgumentException
import java.lang.IllegalStateException
import java.util.*
import javax.inject.Inject

class ToDoListRepository @Inject constructor(private val uidProvider: UidProvider,
                                             private val databaseWriter: FirebaseDatabaseWriter) :
        FirebaseReadWatcher.Listener {

    private val fbDbToDoListKey: String = "to_do_lists"
    private val fbDbToDoListReferencesKey: String = "to_do_lists_references"

    private lateinit var dbRef: DatabaseReference
    private lateinit var uid: String

    private var toDoListsWatcher: FirebaseReadWatcher? = null
    private val toDoListsWatcherMap: HashMap<String, FirebaseReadWatcher> = HashMap()
    private val toDoListsSubjectMap: HashMap<String, BehaviorSubject<ToDoListMap>> = HashMap()

    private val toDoListsSubject: BehaviorSubject<ArrayList<String>> = BehaviorSubject.create()
    fun onToDoListsChanged(): Observable<ArrayList<String>> = toDoListsSubject

    fun initialise() {
        dbRef = com.google.firebase.database.FirebaseDatabase.getInstance().reference
        if (dbRef == null)
            throw NullPointerException("dbRef was unsuccessfully initialised  by FirebaseDatabase")

        uidProvider.getUid()?.let { uid = it }
        if (uid.isNullOrBlank())
            throw NullPointerException("Uid provided by UidProvider has returned null")

        toDoListsWatcher = FirebaseReadWatcher(dbRef, "$fbDbToDoListReferencesKey/$uid",
                Constants.DatabaseApi.READ_TO_DO_LISTS_REFS, this)
    }

    fun onToDoListChanged(id: String): Observable<ToDoListMap> {
        if (toDoListsSubjectMap.containsKey(id)) {
            toDoListsSubjectMap[id]?.let {
                return it
            } ?: throw IllegalStateException("toDoListsWatcherMap contains $id but was unable to " +
                    "retrieve the associated BehaviourSubject")
        } else {
            val subject: BehaviorSubject<ToDoListMap> = BehaviorSubject.create()
            toDoListsSubjectMap[id] = subject
            return subject
        }
    }

    fun removeRepositoryReferences() {
        toDoListsWatcher?.detachListener()
        toDoListsWatcher = null

        toDoListsWatcherMap.forEach { it.value.detachListener() }
    }

    override fun onReadWatcherValueEvent(snapshot: DataSnapshot?, success: Boolean,
                                         errorMessage: String?, type: Constants.DatabaseApi) {
        when (type) {
            Constants.DatabaseApi.READ_TO_DO_LISTS_REFS ->
                when (success) {
                    true -> (snapshot?.value as? HashMap<*, *>)?.let { buildManifest(it) }
                    false -> Timber.w("db watcher failed to read to do list references")
                }
            Constants.DatabaseApi.READ_TO_DO_LIST ->
                when (success) {
                    true -> snapshot?.let { updateToDoList(it) }
                    false -> Timber.w("db watcher failed to read to do list")
                }
            else -> Timber.w("onReadWatcherValueEvent called with unknown type: $type")
        }
    }

    private fun buildManifest(map: HashMap<*, *>) {
        val manifest: ArrayList<String> = ArrayList()
        map.forEach { manifest.add(it.value.toString()) }
        updateWatcherMap(manifest)

        toDoListsSubject.onNext(manifest)
    }

    private fun updateWatcherMap(keys: ArrayList<String>) {
        keys.forEach {
            if (!toDoListsWatcherMap.containsKey(it)) {
                toDoListsWatcherMap[it] = FirebaseReadWatcher(dbRef, "$fbDbToDoListKey/$it",
                        Constants.DatabaseApi.READ_TO_DO_LIST, this)
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

    fun deleteItem(listId: String, itemId: String) {
        databaseWriter.doDelete(dbRef, "$fbDbToDoListKey/$uid/$listId/items/$itemId")
    }

    fun addItem(listId: String?, newText: String) {
        //todo this needs to construct to do list item and write it
        val path = "$fbDbToDoListKey/$uid/$listId/items"
        databaseWriter.doPushWrite(dbRef, path, arrayListOf(newText as Any))
    }

    private fun constructToDoListItems(items: HashMap<String, *>): HashMap<String, ToDoListItem> {
        val toDoListItems = HashMap<String, ToDoListItem>()
        items.forEach { map ->
            constructToDoListItem(map.value as HashMap<String, *>)?.let { toDoListItems[map.key] = it }
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
            creator is String && description is String && priority is String ->
                ToDoListItem(description, creator, status, completedBy, ToDoListItem.parsePriority(priority))
            else -> null
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun publishNewToDoListFromChecklist(title: String, checklist: Checklist) {
        val toDoList = constructToDoListFromChecklist(title, checklist)
        val referencePath = "$fbDbToDoListReferencesKey/$uid"

        (toDoList.items as? HashMap<String, Any>)?.let { items ->
            val toDoListReference = databaseWriter.doPushWrite(dbRef, fbDbToDoListKey, toDoList)
            databaseWriter.doPushWrite(dbRef, referencePath, arrayListOf(toDoListReference.key as Any))
        }
    }

    private fun constructToDoListFromChecklist(title: String, checklist: Checklist): ToDoListMap =
            ToDoListMap("", title, uid, constructToDoListItemsFromChecklist(checklist.items))

    private fun constructToDoListItemsFromChecklist(items: ArrayList<Pair<String, String>>)
            : HashMap<String, ToDoListItem> {
        val newItems: HashMap<String, ToDoListItem> = HashMap()
        items.forEach {
            newItems[it.first] = ToDoListItem(it.second, uid, false, null, ToDoListItem.Priority.TODO)
        }
        return newItems
    }
}
