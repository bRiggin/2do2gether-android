package com.rbiggin.a2do2gether.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.rbiggin.a2do2gether.firebase.FirebaseDatabaseWriter
import com.rbiggin.a2do2gether.firebase.FirebaseReadWatcher
import com.rbiggin.a2do2gether.model.Checklist
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

    val checklistsWatcherMap: HashMap<String, BehaviorSubject<Checklist>> = HashMap()

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

    fun getChecklistSubject(id: String) : BehaviorSubject<Checklist> {
        if (checklistsWatcherMap.containsKey(id)){
            checklistsWatcherMap[id]?.let {
                return it
            } ?: throw Exception()
            //todo add info
        } else {
            val subject: BehaviorSubject<Checklist> = BehaviorSubject.create()
            checklistsWatcherMap[id] = subject
            return subject
        }
    }

    private fun constructChecklist(id: String, data: DataSnapshot): Checklist {
        val items = ArrayList<String>()
        for (item in data.child(Constants.FB_CHECKLIST_ITEMS).children){
            items.add(item.value.toString())
        }
        val title = data.child(Constants.FB_CHECKLIST_TITLE).value.toString()

        return Checklist(id, title, items)
    }
}