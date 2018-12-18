package com.rbiggin.a2do2gether.ui.settings

import com.rbiggin.a2do2gether.model.SettingsUpdate
import com.rbiggin.a2do2gether.repository.AuthRepository
import com.rbiggin.a2do2gether.repository.SettingsRepository
import com.rbiggin.a2do2gether.ui.base.BasePresenter
import com.rbiggin.a2do2gether.utils.Constants
import io.reactivex.Scheduler
import javax.inject.Inject

class SettingsPresenter @Inject constructor(private val authRepo: AuthRepository,
                                            private val settingsRepo: SettingsRepository,
                                            private val uiThread: Scheduler,
                                            private val computationThread: Scheduler) :
        BasePresenter<SettingsFragment>(),
        AuthRepository.Listener {

    override fun onViewAttached(view: SettingsFragment) {
        super.onViewAttached(view)

        disposeOnViewWillDetach(settingsRepo.onReorderListByCompletionChanged()
                .observeOn(uiThread)
                .distinctUntilChanged()
                .subscribe {
                    view.updateSwitch(SettingsUpdate(Constants.Setting.LIST_REORDER, it))
                })

        disposeOnViewWillDetach(settingsRepo.onProfilePublicChanged()
                .observeOn(uiThread)
                .distinctUntilChanged()
                .subscribe {
                    view.updateSwitch(SettingsUpdate(Constants.Setting.PROFILE_PRIVACY, it))
                })

        disposeOnViewWillDetach(settingsRepo.onConnectionRequestsChanged()
                .observeOn(uiThread)
                .distinctUntilChanged()
                .subscribe {
                    view.updateSwitch(SettingsUpdate(Constants.Setting.CONNECTION_REQUEST, it))
                })

        disposeOnViewWillDetach(settingsRepo.onNewConnectionsChanged()
                .observeOn(uiThread)
                .distinctUntilChanged()
                .subscribe {
                    view.updateSwitch(SettingsUpdate(Constants.Setting.NEW_CONNECTIONS, it))
                })

        disposeOnViewWillDetach(settingsRepo.onNewListChanged()
                .observeOn(uiThread)
                .distinctUntilChanged()
                .subscribe {
                    view.updateSwitch(SettingsUpdate(Constants.Setting.NEW_LIST, it))
                })

        disposeOnViewWillDetach(settingsRepo.onAnalyticsChanged()
                .observeOn(uiThread)
                .distinctUntilChanged()
                .subscribe {
                    view.updateSwitch(SettingsUpdate(Constants.Setting.ANALYTICS, it))
                })
    }

    override fun onViewWillShow() {
        authRepo.setListener(this)

        view?.let {
            disposeOnViewWillHide(it.switchSubject
                    .observeOn(computationThread)
                    .subscribe{update ->
                        settingsRepo.updateSetting(update)
                    })
        }
    }

    override fun onViewWillHide() {
        super.onViewWillHide()
        authRepo.detach()
    }

    fun logout() {
        authRepo.logout()
        authRepo.removeFcmToken()
    }

    override fun onAuthStateChange(response_id: Int, message: String?) {
        if (response_id == Constants.AUTH_STATE_LOGGED_OUT) {
            view?.launchLoginActivity()
        }
    }

    interface View : BasePresenter.View {
        fun launchLoginActivity()

        fun updateSwitch(update: SettingsUpdate)
    }
}