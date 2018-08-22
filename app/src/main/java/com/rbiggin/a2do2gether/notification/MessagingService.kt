package com.rbiggin.a2do2gether.notification

import android.support.v4.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.rbiggin.a2do2gether.R
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.content.SharedPreferences
import com.rbiggin.a2do2gether.ui.main.MainActivity
import android.support.v4.app.NotificationManagerCompat
import com.rbiggin.a2do2gether.application.MyApplication
import com.rbiggin.a2do2gether.utils.Constants
import com.rbiggin.a2do2gether.utils.Utilities
import javax.inject.Inject

class MessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var utilities: Utilities

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        (application as MyApplication).daggerComponent.inject(this)
    }

    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        token?.let {
            saveFcmToken(it)
        }

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.putExtra(Constants.LOAD_FRAGMENT, Constants.Fragment.MY_CONNECTIONS.toString())
        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntentWithParentStack(intent)
        val pendingIntent = stackBuilder.getPendingIntent(1234567, PendingIntent.FLAG_UPDATE_CURRENT)

        val mBuilder = NotificationCompat.Builder(applicationContext, getString(R.string.notification_channel_id))
                .setSmallIcon(R.drawable.logo_background)
                .setContentTitle(remoteMessage?.notification?.title)
                .setContentText(remoteMessage?.notification?.body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(longArrayOf(0))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)

        val notificationManager = NotificationManagerCompat.from(this)

        notificationManager.notify(123456789, mBuilder.build())
    }

    private fun saveFcmToken(token: String){
        sharedPreferences.edit().putString(utilities.encode(Constants.SP_FCM_TOKEN),
                utilities.encode(token)).apply()
    }
}