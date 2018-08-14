package com.rbiggin.a2do2gether.ui.settings

import android.content.SharedPreferences
import com.rbiggin.a2do2gether.repository.AuthRepository
import com.rbiggin.a2do2gether.repository.IntAuthRepository
import com.rbiggin.a2do2gether.repository.IntAuthRepositoryListener
import com.rbiggin.a2do2gether.ui.base.BasePresenter
import com.rbiggin.a2do2gether.utils.Constants
import com.rbiggin.a2do2gether.utils.Utilities
import javax.inject.Inject

class SettingsPresenter @Inject constructor(private val authRepo: AuthRepository,
                                            utilities: Utilities,
                                            sharedPreferences: SharedPreferences) :
                                           BasePresenter<SettingsFragment>(sharedPreferences, utilities),
        AuthRepository.Listener{
    override fun onViewWillShow() {
        authRepo.setListener(this)
    }

    fun logout() {
        authRepo.logout()
        authRepo.removeFcmToken()
    }

    override fun onViewWillHide() {
        super.onViewWillHide()
        authRepo.detach()
    }

    override fun onAuthStateChange(response_id: Int, message: String?) {
        if (response_id == Constants.AUTH_STATE_LOGGED_OUT){
            view?.launchLoginActivity()
        }
    }
}