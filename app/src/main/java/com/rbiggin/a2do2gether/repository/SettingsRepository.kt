package com.rbiggin.a2do2gether.repository

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.rbiggin.a2do2gether.firebase.FirebaseDatabaseWriter
import com.rbiggin.a2do2gether.firebase.FirebaseReadWatcher
import com.rbiggin.a2do2gether.model.SettingsUpdate
import com.rbiggin.a2do2gether.utils.Constants
import com.rbiggin.a2do2gether.utils.Utilities
import io.reactivex.subjects.BehaviorSubject
import javax.inject.Inject

class SettingsRepository @Inject constructor(private val uidProvider: UidProvider,
                                             private val databaseWriter: FirebaseDatabaseWriter,
                                             private val utilities: Utilities) :
        FirebaseReadWatcher.Listener {

    private var mDatabase: DatabaseReference? = null

    private var mUid: String? = null

    private var settingsWatcher: FirebaseReadWatcher? = null

    val reorderListSubject: BehaviorSubject<Boolean> = BehaviorSubject.create()

    val profilePublicSubject: BehaviorSubject<Boolean> = BehaviorSubject.create()

    val connectionRequestsSubject: BehaviorSubject<Boolean> = BehaviorSubject.create()

    val newConnectionsSubject: BehaviorSubject<Boolean> = BehaviorSubject.create()

    val newListSubject: BehaviorSubject<Boolean> = BehaviorSubject.create()

    val analyticsSubject: BehaviorSubject<Boolean> = BehaviorSubject.create()

    fun initialise() {
        mDatabase = com.google.firebase.database.FirebaseDatabase.getInstance().reference
        mUid = uidProvider.getUid()
        if (mUid.isNullOrBlank()) {
            throw NullPointerException("Uid provided by UidProvider has returned null")
        }

        watchUserSettings()
    }

    private fun watchUserSettings() {
        mDatabase?.let {
            settingsWatcher = FirebaseReadWatcher(it, "${Constants.FB_SETTINGS}/$mUid",
                    Constants.DatabaseApi.READ_SETTINGS, this)

        }
    }

    fun presenterDetached() {
        settingsWatcher?.detachListener()
        settingsWatcher = null
    }

    override fun onReadWatcherValueEvent(snapshot: DataSnapshot?, success: Boolean,
                                         errorMessage: String?, type: Constants.DatabaseApi) {
        when (type) {
            Constants.DatabaseApi.READ_SETTINGS -> {
                if (success){
                    snapshot?.let {
                        updateSettingsSubjects(it)
                    }
                } else {
                    throw Exception("SettingsWatcher error, message: $errorMessage")
                }
            }
            else -> {
                throw IllegalArgumentException("SettingsRepository, onReadWatcherValueEvent: " +
                        "Inappropriate type returned from FirebaseReadWatcher")
            }
        }
    }

    private fun updateSettingsSubjects(data: DataSnapshot){
        for (setting in data.children){
            val value = setting.value.toString().toBoolean()
            when (setting.key){
                Constants.Setting.LIST_REORDER.key ->{
                    reorderListSubject.onNext(value)
                }
                Constants.Setting.PROFILE_PRIVACY.key -> {
                    profilePublicSubject.onNext(value)
                }
                Constants.Setting.CONNECTION_REQUEST.key -> {
                    connectionRequestsSubject.onNext(value)
                }
                Constants.Setting.NEW_CONNECTIONS.key -> {
                    newConnectionsSubject.onNext(value)
                }
                Constants.Setting.NEW_LIST.key -> {
                    newListSubject.onNext(value)
                }
                Constants.Setting.ANALYTICS.key -> {
                    analyticsSubject.onNext(value)
                }
            }
        }
    }

    fun updateSetting(update: SettingsUpdate) {
        val path = "${Constants.FB_SETTINGS}/$mUid"
        val userDetailsPath = "${Constants.FB_USER_PROFILE}/$mUid"
        val data = hashMapOf(update.type.key to update.value as Any)
        mDatabase?.let {
            databaseWriter.doWrite(it, path, data)

            if (update.type == Constants.Setting.PROFILE_PRIVACY){
                databaseWriter.doWrite(it, userDetailsPath, data)
            }
        }
    }
}