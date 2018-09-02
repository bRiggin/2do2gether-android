package com.rbiggin.a2do2gether.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.rbiggin.a2do2gether.firebase.FirebaseDatabaseWriter
import com.rbiggin.a2do2gether.firebase.FirebaseReadWatcher
import com.rbiggin.a2do2gether.model.ChecklistArray
import com.rbiggin.a2do2gether.model.ChecklistMap
import com.rbiggin.a2do2gether.utils.Constants
import io.reactivex.subjects.BehaviorSubject
import timber.log.Timber
import javax.inject.Inject

class ChecklistRepository @Inject constructor(private val uidProvider: UidProvider,
                                              private val databaseWriter: FirebaseDatabaseWriter):
                                              FirebaseReadWatcher.Listener {

    private var mDatabase: DatabaseReference? = null

    private var mUid: String? = null

    private var checklistsWatcher: FirebaseReadWatcher? = null

    private val checklistsWatcherMap: HashMap<String, BehaviorSubject<ChecklistMap>> = HashMap()

    val checklistsSubject: BehaviorSubject<ArrayList<String>> = BehaviorSubject.create()

    fun initialise() {
        mDatabase = com.google.firebase.database.FirebaseDatabase.getInstance().reference
        mUid = uidProvider.getUid()
        if (mUid.isNullOrBlank()) {
            throw NullPointerException("Uid provided by UidProvider has returned null")
        }
        watchUserChecklists()
    }

    private fun watchUserChecklists() {
        mDatabase?.let {
            checklistsWatcher = FirebaseReadWatcher(it, "${Constants.FB_CHECKLISTS}/$mUid",
                    Constants.DatabaseApi.READ_CHECKLISTS, this)
        }
    }

    fun presenterDetached() {
        checklistsWatcher?.detachListener()
        checklistsWatcher = null
    }

    override fun onReadWatcherValueEvent(snapshot: DataSnapshot?, success: Boolean,
                                         errorMessage: String?, type: Constants.DatabaseApi) {
        when (type) {
            Constants.DatabaseApi.READ_CHECKLISTS -> {
                if (success) {
                    snapshot?.let {
                        updateChecklists(it)
                    }
                } else {
                    Timber.d("ChecklistsWatcher error, message: $errorMessage")
                }
            }
            else -> {
                throw IllegalArgumentException("SettingsRepository, onReadWatcherValueEvent: " +
                        "Inappropriate type returned from FirebaseReadWatcher")
            }
        }
    }

    private fun updateChecklists(data: DataSnapshot) {
        for (checklist in data.children) {
            checklist.key?.let{
                getChecklistSubject(it).onNext(constructChecklist(it, checklist))
            }
        }
        checklistsSubject.onNext(constructChecklistManifest(data))
    }

    private fun constructChecklistManifest(data: DataSnapshot): ArrayList<String> {
        val manifest = ArrayList<String>()
        for (checklist in data.children) {
            checklist.key?.let {
                manifest.add(it)
            }
        }
        return manifest
    }

    fun getChecklistSubject(id: String) : BehaviorSubject<ChecklistMap> {
        if (checklistsWatcherMap.containsKey(id)){
            checklistsWatcherMap[id]?.let {
                return it
            } ?: throw Exception()
            //todo add info
        } else {
            val subject: BehaviorSubject<ChecklistMap> = BehaviorSubject.create()
            checklistsWatcherMap[id] = subject
            return subject
        }
    }

    fun deleteChecklistSubject(id: String) {
        if (checklistsWatcherMap.containsKey(id)){
            checklistsWatcherMap.remove(id)
        }
    }

    private fun constructChecklist(id: String, data: DataSnapshot): ChecklistMap {
        val values = ArrayList<String>()
        val keys = ArrayList<String>()
        for (item in data.child(Constants.FB_CHECKLIST_ITEMS).children){
            values.add(item.value.toString())
            keys.add(item.key.toString())
        }
        val title = data.child(Constants.FB_CHECKLIST_TITLE).value.toString()

        return ChecklistMap(id, title, hashMapOf("keys" to keys, "values" to values))
    }

    fun addItem(listId: String?, newText: String){
        val path = "${Constants.FB_CHECKLISTS}/$mUid/$listId/items"
        mDatabase?.let {
            databaseWriter.doPushWrite(it, path, arrayListOf(newText as Any))
        }
    }

    fun deleteItem(listId: String, itemIndex: Int){
        val itemId = checklistsWatcherMap[listId]?.value?.items?.get("keys")?.get(itemIndex)
        val path = "${Constants.FB_CHECKLISTS}/$mUid/$listId/items/$itemId"
        mDatabase?.let {
            databaseWriter.doDelete(it, path)
        }
    }

    fun deleteChecklist(listId: String){
        val path = "${Constants.FB_CHECKLISTS}/$mUid/$listId"
        mDatabase?.let {
            databaseWriter.doDelete(it, path)
        }
        deleteChecklistSubject(listId)
    }

    fun newChecklist(title: String){
        val path = "${Constants.FB_CHECKLISTS}/$mUid/"
        val data = hashMapOf(Constants.FB_CHECKLIST_TITLE to title as Any)
        mDatabase?.let {
            databaseWriter.doPushWrite(it, path, data)
        }
    }
}