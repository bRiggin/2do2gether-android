package com.rbiggin.a2do2gether.ui.settings

import android.content.SharedPreferences
import com.rbiggin.a2do2gether.repository.IntAuthRepository
import com.rbiggin.a2do2gether.repository.IntAuthRepositoryListener
import com.rbiggin.a2do2gether.ui.base.BasePresenter
import com.rbiggin.a2do2gether.utils.Constants
import com.rbiggin.a2do2gether.utils.Utilities
import javax.inject.Inject

/**
 * Insert class/object/interface/file description...
 */
class SettingPresenter @Inject constructor(private val authRepo: IntAuthRepository,
                                           utilities: Utilities,
                                           sharedPreferences: SharedPreferences) :
                                           BasePresenter<SettingsFragment>(sharedPreferences, utilities),
                                           IntSettingsPresenter,
                                           IntAuthRepositoryListener{
    /**
     * Initialise presenter
     */
    override fun onViewWillShow() {
        authRepo.setup(this)
    }

    /**
     *
     */
    override fun logout() {
        authRepo.logout()

    }

    /**
     *
     */
    override fun onViewWillHide() {
        super.onViewWillHide()
        authRepo.detach()
    }

    /**
     *
     */
    override fun onAuthStateChange(response_id: Int, message: String?) {
        if (response_id == Constants.AUTH_STATE_LOGGED_OUT){
            mFragment?.launchLoginActivity()
        }
    }
}