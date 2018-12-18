package com.rbiggin.a2do2gether.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.rbiggin.a2do2gether.firebase.FirebaseDatabaseWriter
import com.rbiggin.a2do2gether.firebase.FirebaseReadEqualWatcher
import com.rbiggin.a2do2gether.firebase.FirebaseReadWatcher
import com.rbiggin.a2do2gether.model.Checklist
import com.rbiggin.a2do2gether.model.ToDoListItem
import com.rbiggin.a2do2gether.model.ToDoList
import com.rbiggin.a2do2gether.utils.Constants
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.reflect.jvm.internal.impl.protobuf.UninitializedMessageException

class ToDoListRepository @Inject constructor(private val uidProvider: UidProvider,
                                             private val databaseWriter: FirebaseDatabaseWriter) :
        FirebaseReadWatcher.Listener, FirebaseReadEqualWatcher.Listener {

    private val fbDbToDoListKey: String = "to_do_lists"
    private val fbDbToDoListReferencesKey: String = "to_do_lists_references"
    private val fbDbToDoListUserId: String = "uid"
    private val fbDbToDoListListId: String = "list_id"

    private lateinit var dbRef: DatabaseReference
    private lateinit var uid: String

    private val toDoListMap: HashMap<String, Pair<FirebaseReadWatcher, BehaviorSubject<ToDoList>>> = HashMap()
    fun onToDoListChanged(id: String): Observable<ToDoList>? = toDoListMap[id]?.second

    private val toDoListsMembersMap: HashMap<String, Pair<FirebaseReadEqualWatcher, BehaviorSubject<ArrayList<String>>>> = HashMap()
    fun onToDoListMembersChanged(id: String): Observable<ArrayList<String>>? = toDoListsMembersMap[id]?.second

    private var toDoListsPair: Pair<FirebaseReadEqualWatcher, BehaviorSubject<ArrayList<String>>>? = null
    fun onToDoListsChanged(): Observable<ArrayList<String>>? = toDoListsPair?.second

    fun initialise() {
        dbRef = FirebaseDatabase.getInstance().reference
        uidProvider.getUid()?.let { uid = it }
        if (!this::uid.isInitialized)
            throw UninitializedPropertyAccessException("Uid provided by UidProvider has returned null")

        toDoListsPair = Pair(FirebaseReadEqualWatcher(dbRef, fbDbToDoListReferencesKey,
                fbDbToDoListUserId, uid, Constants.DatabaseApi.READ_TO_DO_LISTS_REFS, this),
                BehaviorSubject.create())
    }

    private fun updateToDoListMap(keys: ArrayList<String>) {
        //todo only adds watchers, doesn't remove them if list is removed/deleted, does this matter?
        keys.forEach {
            if (!toDoListMap.containsKey(it))
                toDoListMap[it] = Pair(FirebaseReadWatcher(dbRef, "$fbDbToDoListKey/$it",
                        Constants.DatabaseApi.READ_TO_DO_LIST, this), BehaviorSubject.create())
        }
    }

    private fun updateMembersMap(keys: ArrayList<String>) {
        //todo only adds watchers, doesn't remove them if list is removed/deleted, does this matter?
        keys.forEach {
            if (!toDoListsMembersMap.containsKey(it))
                toDoListsMembersMap[it] = Pair(FirebaseReadEqualWatcher(dbRef, fbDbToDoListReferencesKey,
                        fbDbToDoListListId, it, Constants.DatabaseApi.READ_TO_DO_LISTS_MEMBERS, this),
                        BehaviorSubject.create())
        }
    }

    override fun onReadWatcherValueEvent(snapshot: DataSnapshot?, success: Boolean,
                                         errorMessage: String?, type: Constants.DatabaseApi) {
        when (type) {
            Constants.DatabaseApi.READ_TO_DO_LIST ->
                when (success) {
                    true -> snapshot?.let { updateToDoList(it) }
                    false -> Timber.w("db watcher failed to read to do list")
                }
            else -> Timber.w("onReadWatcherValueEvent called with unknown type: $type")
        }
    }

    override fun onReadEqualWatcherValueEvent(snapshot: DataSnapshot?, success: Boolean, errorMessage: String?, type: Constants.DatabaseApi) {
        when (type) {
            Constants.DatabaseApi.READ_TO_DO_LISTS_REFS ->
                when (success) {
                    true -> (snapshot?.value as? HashMap<*, *>)?.let { buildManifest(it) }
                    false -> Timber.w("db watcher failed to read to do list references")
                }
            Constants.DatabaseApi.READ_TO_DO_LISTS_MEMBERS ->
                when (success) {
                    true -> Timber.i("db read references: $snapshot")
                    false -> Timber.w("db watcher failed to read to do list references")
                }
            else -> Timber.w("onReadEqualWatcherValueEvent called with unknown type: $type")
        }
    }

    private fun buildManifest(map: HashMap<*, *>) {
        val manifest: ArrayList<String> = ArrayList()
        map.forEach { entity ->
            (entity.value as? HashMap<*, *>)?.let { item ->
                (item[fbDbToDoListListId] as? String)?.let { manifest.add(it) }
            }
        }
        updateMembersMap(manifest)
        updateToDoListMap(manifest)
        toDoListsPair?.second?.onNext(manifest)
    }

    private fun updateToDoList(snapshot: DataSnapshot) {
        toDoListMap[snapshot.key.toString()]?.let { pair ->
            (ToDoList.parseToDoList(snapshot))?.let { pair.second.onNext(it) }
        }
    }

    fun deleteItem(listId: String, itemId: String) {
        databaseWriter.doDelete(dbRef, "$fbDbToDoListKey/$uid/$listId/items/$itemId")
    }

    fun addItem(listId: String?, newText: String) {
        //todo this needs to construct to do list item and write it
        val path = "$fbDbToDoListKey/$uid/$listId/items"
        databaseWriter.doPushWrite(dbRef, path, arrayListOf(newText as Any))
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

    private fun constructToDoListFromChecklist(title: String, checklist: Checklist): ToDoList =
            ToDoList("", title, uid, constructToDoListItemsFromChecklist(checklist.items))

    private fun constructToDoListItemsFromChecklist(items: ArrayList<Pair<String, String>>)
            : HashMap<String, ToDoListItem> {
        val newItems: HashMap<String, ToDoListItem> = HashMap()
        items.forEach {
            newItems[it.first] = ToDoListItem(it.second, uid, false, null, ToDoListItem.Priority.TODO)
        }
        return newItems
    }

    fun removeRepositoryReferences() {
        toDoListsPair?.first?.detachListener()
        toDoListMap.forEach { it.value.first.detachListener() }
        toDoListsMembersMap.forEach { it.value.first.detachListener() }
    }
}
