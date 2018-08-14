package com.rbiggin.a2do2gether.ui.login

import android.util.Log
import com.rbiggin.a2do2gether.repository.AuthRepository
import com.rbiggin.a2do2gether.ui.login.fragments.IntLoginFragmentCallbacks
import com.rbiggin.a2do2gether.ui.base.BasePresenter
import com.rbiggin.a2do2gether.utils.Constants
import javax.inject.Inject

class LoginPresenter @Inject constructor(private val authRepo: AuthRepository) :
                                         AuthRepository.ActiveListener,
                                         BasePresenter<LoginActivity>(){

    private var mLoginActivity: IntLoginActivity? = null

    private var tempEmail: String = ""

    private var tempPassword: String = ""

    private var isProcessingBol: Boolean = false

    private val tag = Constants.LOGIN_PRESENTER_TAG

    private fun setup() {
        authRepo.setListener(this)
    }

    override fun onViewAttached(view: LoginActivity) {
        super.onViewAttached(view)
        mLoginActivity = view
        setup()

        if (authRepo.isUserLoggedIn()) {
            enterMainActivity()
        }
    }

    override fun onViewWillShow() {
        super.onViewWillShow()
        mLoginActivity?.displayFragment(Constants.ADDRESS_FRAGMENT_ID)
    }

    override fun onViewDetached() {
        super.onViewDetached()
        authRepo.detach()
        mLoginActivity = null
        isProcessing(false)
    }

    private fun enterMainActivity(){
        authRepo.updateFcmToken()
        val email = authRepo.getEmail() ?: "Unknown"
        mLoginActivity?.launchMainActivity(email)
    }

    fun emailSubmitted(email: String?, reference: IntLoginFragmentCallbacks) {
        if (!isProcessingBol) {
            email?.let {
                if (email.isEmpty()) {
                    mLoginActivity?.displayDialogMessage(Constants.ERROR_BLANK_EMAIL_STRING, null)
                    reference.clearViews()
                } else {
                    tempEmail = email
                    mLoginActivity?.displayFragment(Constants.PASSWORD_FRAGMENT_ID)
                    reference.clearViews()
                }
            }
        }
    }

    fun newAccountBtnPressed(reference: IntLoginFragmentCallbacks) {
        if (!isProcessingBol) {
            mLoginActivity?.displayFragment(Constants.REGISTER_FRAGMENT_ID)
            reference.clearViews()
        }
    }

    fun createNewAccount(email: String, password_one: String, password_two: String,
                                  reference: IntLoginFragmentCallbacks) {
        if (!isProcessingBol) {
            isProcessing(false)
            if (password_one.isEmpty() || password_two.isEmpty()) {
                mLoginActivity?.displayDialogMessage(Constants.ERROR_MISSING_PASSWORD, null)
                reference.clearViews(false, true, true)
            } else if (email.isEmpty()){
                mLoginActivity?.displayDialogMessage(Constants.ERROR_BLANK_EMAIL_STRING, null)
                reference.clearViews(true, false, false)
            } else if (password_one != password_two) {
                mLoginActivity?.displayDialogMessage(Constants.ERROR_PASSWORDS_DO_NOT_MATCH, null)
                reference.clearViews(false, true, true)
            } else {
                isProcessing(true)
                authRepo.createAccount(email, password_one)
                tempPassword = password_one
                tempEmail = email
                reference.clearViews()
            }
        }
    }

    fun loginWithPassword(password: String) {
        if (!isProcessingBol) {
            if (password.isEmpty()) {
                isProcessing(false)
                mLoginActivity?.displayDialogMessage(Constants.ERROR_BLANK_PASSWORD_STRING, null)
            } else {
                isProcessing(true)
                authRepo.login(tempEmail, password)
            }
        }
    }

    fun passwordForgotten() {
        if (!isProcessingBol) {
            mLoginActivity?.displayFunctionalDialog(Constants.DIALOG_FORGOT_PASSWORD)
        }
    }

    fun sendPasswordReset() {
        if (!isProcessingBol) {
            mLoginActivity?.displayFragment(Constants.ADDRESS_FRAGMENT_ID)
            isProcessing(true)
            authRepo.sendPasswordReset(tempEmail)
        }
    }

    override fun onAuthCommandResult(response_id: Int, message: String?) {
        mLoginActivity?.displayFragment(Constants.ADDRESS_FRAGMENT_ID)
        when(response_id){
            Constants.AUTH_LOGIN_SUCCESSFUL -> {
                enterMainActivity()
            }
            Constants.AUTH_LOGIN_FAILED -> {
                isProcessing(false)
                mLoginActivity?.displayDialogMessage(Constants.AUTH_LOGIN_FAILED, message)
            }
            Constants.AUTH_CREATE_ACCOUNT_SUCCESSFUL -> {
                authRepo.login(tempEmail, tempPassword)
            }
            Constants.AUTH_CREATE_ACCOUNT_UNSUCCESSFUL -> {
                isProcessing(false)
                mLoginActivity?.displayDialogMessage(Constants.AUTH_CREATE_ACCOUNT_UNSUCCESSFUL, message)
            }
            Constants.AUTH_PASSWORD_RESET_SUCCESSFUL -> {
                isProcessing(false)
                mLoginActivity?.displayDialogMessage(Constants.AUTH_PASSWORD_RESET_SUCCESSFUL, null)
            }
            Constants.AUTH_PASSWORD_RESET_UNSUCCESSFUL -> {
                isProcessing(false)
                mLoginActivity?.displayDialogMessage(Constants.AUTH_PASSWORD_RESET_UNSUCCESSFUL, message)
            }
        }
    }

    override fun onAuthStateChange(response_id: Int, message: String?) {
        // not currently used within login activity.
    }

    fun backPressedInFragment(fragment_id: Int) {
        Log.d(tag, "Interface notified of back press. Fragment ID: $fragment_id")
        when (fragment_id) {
            Constants.REGISTER_FRAGMENT_ID -> {
                mLoginActivity?.displayFragment(Constants.ADDRESS_FRAGMENT_ID)
            }
            Constants.ADDRESS_FRAGMENT_ID -> {
                Log.w(tag, "Fragment ID: $fragment_id, should never notify presenter.")
            }
            Constants.PASSWORD_FRAGMENT_ID -> {
                mLoginActivity?.displayFragment(Constants.ADDRESS_FRAGMENT_ID)
            }
            else -> {
                Log.w(tag, "Fragment ID: $fragment_id, not recognised.")
            }
        }
    }

    private fun isProcessing(processing: Boolean){
        if (processing){
            isProcessingBol = true
            mLoginActivity?.displayProgressSpinner(true)
        } else {
            isProcessingBol = false
            mLoginActivity?.displayProgressSpinner(false)
        }
    }
}