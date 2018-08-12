package com.rbiggin.a2do2gether.ui.login

import android.util.Log
import com.rbiggin.a2do2gether.ui.login.fragments.IntLoginFragmentCallbacks
import com.rbiggin.a2do2gether.repository.IntAuthRepository
import com.rbiggin.a2do2gether.repository.IntAuthRepositoryActiveListener
import com.rbiggin.a2do2gether.utils.Constants
import javax.inject.Inject


/**
 * Interface responsible for the Login Activity
 */
class LoginPresenter @Inject constructor(private val authRepo: IntAuthRepository) :
                                         IntAuthRepositoryActiveListener{

    /** Instance Login Activity Interface */
    private var mLoginActivity: IntLoginActivity? = null

    /** Used if user has forgotten their account password */
    private var tempEmail: String = ""

    /**  */
    private var tempPassword: String = ""

    /** Boolean used to determine if user actions should be ignored or acted on */
    private var isProcessingBol: Boolean = false

    /** Logging tag */
    private val tag = Constants.LOGIN_PRESENTER_TAG

    /**
     * Initialise presenter
     */
    private fun setup() {
        authRepo.setup(this)
    }

    /**
     * Called form activity and provides login activity interface
     */
    fun setView(loginActivity: IntLoginActivity) {
        mLoginActivity = loginActivity
        setup()
    }

    fun onViewWillHide() {
        authRepo.detach()
        mLoginActivity = null
    }

    /**
     * Called form activity. Where all required logic for loading activity is performed
     */
    fun onViewWillShow() {
        mLoginActivity?.displayFragment(Constants.ADDRESS_FRAGMENT_ID)
        if (authRepo.isUserLoggedIn()) {
            enterMainActivity()
        }
    }

    /**
     * Enter MainPresenter Activity
     */
    private fun enterMainActivity(){
        authRepo.storeUid()
        authRepo.updateFcmToken()
        val email = authRepo.getEmail() ?: "Unknown"
        mLoginActivity?.launchMainActivity(email)
    }

    /**
     * User submitted email address to login with
     */
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

    /**
     * User requested to create new account with provided email address.
     */
    fun newAccountBtnPressed(reference: IntLoginFragmentCallbacks) {
        if (!isProcessingBol) {
            mLoginActivity?.displayFragment(Constants.REGISTER_FRAGMENT_ID)
            reference.clearViews()
        }
    }

    /**
     * User submitted new password to create account with.
     */
    fun createNewAccount(email: String, password_one: String, password_two: String,
                                  reference: IntLoginFragmentCallbacks) {
        if (!isProcessingBol) {
            isProcessing(true)
            if (password_one.isEmpty() || password_two.isEmpty()) {
                isProcessing(false)
                mLoginActivity?.displayDialogMessage(Constants.ERROR_MISSING_PASSWORD, null)
                reference.clearViews(false, true, true)
            } else if (email.isEmpty()){
                isProcessing(false)
                mLoginActivity?.displayDialogMessage(Constants.ERROR_BLANK_EMAIL_STRING, null)
                reference.clearViews(true, false, false)
            }
            else if (password_one != password_two) {
                isProcessing(false)
                mLoginActivity?.displayDialogMessage(Constants.ERROR_PASSWORDS_DO_NOT_MATCH, null)
                reference.clearViews(false, true, true)
            } else {
                mLoginActivity?.displayFragment(Constants.ADDRESS_FRAGMENT_ID)
                authRepo.createAccount(email, password_one)
                tempPassword = password_one
                tempEmail = email
                reference.clearViews()
            }

        }
    }

    /**
     * User submitted password to create account with.
     */
    fun loginWithPassword(password: String) {
        if (!isProcessingBol) {
            isProcessing(true)
            if (password.isEmpty()) {
                isProcessing(false)
                mLoginActivity?.displayDialogMessage(Constants.ERROR_BLANK_PASSWORD_STRING, null)
            } else {
                isProcessing(true)
                authRepo.login(tempEmail, password)
            }
        }
    }

    /**
     * User has forgotten the password to their account.
     */
    fun passwordForgotten() {
        if (!isProcessingBol) {
            mLoginActivity?.displayFunctionalDialog(Constants.DIALOG_FORGOT_PASSWORD)
        }
    }

    /**
     * Instructs repo to send a reset password email to user.
     */
    fun sendPasswordReset() {
        if (!isProcessingBol) {
            mLoginActivity?.displayFragment(Constants.ADDRESS_FRAGMENT_ID)
            isProcessing(true)
            authRepo.sendPasswordReset(tempEmail)
        }
    }

    /**
     * Handles result from UserRepo regarding recently sent AuthCommand
     */
    override fun onAuthCommandResult(response_id: Int, message: String?) {
        isProcessing(false)
        mLoginActivity?.displayFragment(Constants.ADDRESS_FRAGMENT_ID)
        when(response_id){
            Constants.AUTH_LOGIN_SUCCESSFUL -> {
                enterMainActivity()
            }
            Constants.AUTH_LOGIN_FAILED -> {
                mLoginActivity?.displayDialogMessage(Constants.AUTH_LOGIN_FAILED, message)
            }
            Constants.AUTH_CREATE_ACCOUNT_SUCCESSFUL -> {
                authRepo.login(tempEmail, tempPassword)
            }
            Constants.AUTH_CREATE_ACCOUNT_UNSUCCESSFUL -> {
                mLoginActivity?.displayDialogMessage(Constants.AUTH_CREATE_ACCOUNT_UNSUCCESSFUL, message)
            }
            Constants.AUTH_PASSWORD_RESET_SUCCESSFUL -> {
                mLoginActivity?.displayDialogMessage(Constants.AUTH_PASSWORD_RESET_SUCCESSFUL, null)
            }
            Constants.AUTH_PASSWORD_RESET_UNSUCCESSFUL -> {
                mLoginActivity?.displayDialogMessage(Constants.AUTH_PASSWORD_RESET_UNSUCCESSFUL, message)
            }
        }
    }

    /**
     * Callback from userRepository notifying changes in auth state.
     */
    override fun onAuthStateChange(response_id: Int, message: String?) {
        // not currently used within login activity.
    }

    /**
     * User has pressed back within login activity.
     */
    fun backPressedInFragment(fragment_id: Int) {
        Log.d(tag, "Interface notified of back press. Fragment ID: $fragment_id")
        when(fragment_id){
            Constants.REGISTER_FRAGMENT_ID -> {
                mLoginActivity?.displayFragment(Constants.ADDRESS_FRAGMENT_ID)
            }
            Constants.ADDRESS_FRAGMENT_ID ->{
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

    /**
     * Maintain presenter boolean to avoid user from sending requesting while one is already ongoing
     */
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