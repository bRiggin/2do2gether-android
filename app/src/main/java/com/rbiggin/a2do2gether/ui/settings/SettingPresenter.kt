package com.rbiggin.a2do2gether.ui.settings

import com.rbiggin.a2do2gether.repository.IntAuthRepository
import com.rbiggin.a2do2gether.repository.IntAuthRepositoryListener
import com.rbiggin.a2do2gether.ui.base.BasePresenter
import com.rbiggin.a2do2gether.utils.Constants
import javax.inject.Inject

/**
 * Insert class/object/interface/file description...
 */
class SettingPresenter @Inject constructor(private val authRepo: IntAuthRepository,
                                           private val constants: Constants) :
                                           BasePresenter<SettingsFragment>(),
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
    override fun onAuthCommandResult(response_id: Int, message: String?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     *
     */
    override fun onAuthStateChange(response_id: Int, message: String?) {
        if (response_id == constants.AUTH_STATE_LOGGED_OUT){
            mFragment?.launchLoginActivity()
        }
    }
}