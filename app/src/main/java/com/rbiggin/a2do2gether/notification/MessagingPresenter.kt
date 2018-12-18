package com.rbiggin.a2do2gether.notification

import com.google.firebase.messaging.RemoteMessage
import com.rbiggin.a2do2gether.repository.AuthRepository
import com.rbiggin.a2do2gether.repository.SettingsRepository
import com.rbiggin.a2do2gether.utils.Constants
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import timber.log.Timber
import javax.inject.Inject

class MessagingPresenter @Inject constructor(private val authRepository: AuthRepository,
                                             private val settingsRepository: SettingsRepository) {

    private val attachedDisposables: CompositeDisposable = CompositeDisposable()

    private var notifyConnectionRequests: Boolean = false

    private var notifyNewConnections: Boolean = false

    private var notifyNewLists: Boolean = false

    private var service: Service? = null

    fun onServiceCreated(service: Service) {
        this.service = service

        disposeOnServiceDestroyed(authRepository.uidSubject
                .subscribe {
                    settingsRepository.initialise()
                })

        disposeOnServiceDestroyed(settingsRepository.onConnectionRequestsChanged()
                .distinctUntilChanged()
                .subscribe {
                    notifyConnectionRequests = it
                })

        disposeOnServiceDestroyed(settingsRepository.onNewConnectionsChanged()
                .distinctUntilChanged()
                .subscribe {
                    notifyNewConnections = it
                })

        disposeOnServiceDestroyed(settingsRepository.onNewListChanged()
                .distinctUntilChanged()
                .subscribe {
                    notifyNewLists = it
                })
    }

    fun onServiceDestroyed() {
        attachedDisposables.clear()
        service = null
    }

    private fun disposeOnServiceDestroyed(disposable: Disposable) {
        attachedDisposables.add(disposable)
    }

    fun onNewToken(token: String?) {
        token?.let {
            service?.saveFcmToken(it)
        }
    }

    fun onMessageReceived(remoteMessage: RemoteMessage?) {
        remoteMessage?.let {
            val type = remoteMessage.data["type"]
            val title = remoteMessage.notification?.title ?: "Unknown"
            val body = remoteMessage.notification?.body ?: "Unknown"

            when (type) {
                Constants.NotificationType.REQUEST.value ->
                    if (notifyConnectionRequests) {
                        service?.onConnectionsNotification(title, body)
                    }
                Constants.NotificationType.NEW_CONNECTION.value ->
                    if (notifyNewConnections) {
                        service?.onConnectionsNotification(title, body)
                    }
                Constants.NotificationType.NEW_LIST.value ->
                    if (notifyNewLists) {

                    }
                else -> Timber.d("Unknown notification type: $type")
            }
        }
    }

    interface Service {
        fun onConnectionsNotification(title: String, body: String)
        fun onListNotification(title: String, body: String)
        fun saveFcmToken(token: String)
    }
}
