package com.rbiggin.a2do2gether.notification

import android.content.SharedPreferences
import com.google.firebase.database.DatabaseReference
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.iid.FirebaseInstanceId
import com.rbiggin.a2do2gether.application.MyApplication
import com.rbiggin.a2do2gether.firebase.IntFirebaseDatabase
import com.rbiggin.a2do2gether.utils.Constants
import com.rbiggin.a2do2gether.utils.Utilities
import javax.inject.Inject


class IdService : FirebaseInstanceIdService() {

    /** Injected Utilities instance */
    @Inject lateinit var utilities: Utilities

    /** Injected Shared Preferences instance */
    @Inject lateinit var sharedPreferences: SharedPreferences

    /**
     * On Create
     */
    override fun onCreate() {
        super.onCreate()
        (application as MyApplication).daggerComponent.inject(this)
    }

    /**
     * On Token Refresh
     */
    override fun onTokenRefresh() {
        val refreshedToken = FirebaseInstanceId.getInstance().token
        refreshedToken?.let { saveFcmToken(it) }
    }

    /**
     * Save FCM Token
     */
    private fun saveFcmToken(token: String){
        sharedPreferences.edit().putString(utilities.encode(Constants.SP_FCM_TOKEN),
                                           utilities.encode(token)).apply()
    }
}