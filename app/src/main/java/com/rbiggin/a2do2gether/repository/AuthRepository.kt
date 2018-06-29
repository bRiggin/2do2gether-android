package com.rbiggin.a2do2gether.repository

import android.content.SharedPreferences
import com.google.firebase.database.DatabaseReference
import com.rbiggin.a2do2gether.firebase.FirebaseAuth
import com.rbiggin.a2do2gether.firebase.IntFirebaseAuthListener
import com.rbiggin.a2do2gether.firebase.IntFirebaseDatabase
import com.rbiggin.a2do2gether.model.User
import com.rbiggin.a2do2gether.utils.Constants
import com.rbiggin.a2do2gether.utils.Utilities
import javax.inject.Inject

/**
 * Handles the auth state of the user
 */
class AuthRepository @Inject constructor(private val authApi: FirebaseAuth,
                                         private val sharedPrefs: SharedPreferences,
                                         private val fbDatabseApi: IntFirebaseDatabase,
                                         private val utilities: Utilities,
                                         private val constants: Constants) :
                     IntAuthRepository, IntFirebaseAuthListener, com.google.firebase.auth.FirebaseAuth.AuthStateListener{

    /** User model data class */
    private var user: User? = null

    /** Firebase authentication */
    private var mAuth: com.google.firebase.auth.FirebaseAuth? = null

    /** Firebase authentication listener */
    private var mListener: IntAuthRepositoryListener? = null

    /** Database Reference */
    private var mDatabase: DatabaseReference? = null

    /**
     * Setup
     */
    override fun setup(listener: IntAuthRepositoryListener) {
        mListener = listener
        mAuth = com.google.firebase.auth.FirebaseAuth.getInstance()
        mAuth?.addAuthStateListener(this)
        user = User(mAuth?.currentUser)
        mDatabase = com.google.firebase.database.FirebaseDatabase.getInstance().reference
    }

    /**
     * Is User Logged in
     */
    override fun isUserLoggedIn(): Boolean {
        return when(user?.firebaseUser){
            null -> { false }
            else -> { true }
        }
    }

    /**
     * User ID
     */
    override fun userId(): String? {
        return user?.firebaseUser?.uid
    }

    /**
     * get Email
     */
    override fun getEmail(): String? {
        return user?.firebaseUser?.email
    }

    /**
     * Create Account
     */
    override fun createAccount(email: String, password: String) {
        authApi.createAccount(mAuth, this, email, password, constants.authApiCreateAccount())
    }

    /**
     * Login
     */
    override fun login(email: String, password: String) {
        authApi.login(mAuth, this, email, password, constants.authApiLogin())
    }

    /**
     * Send Password Reset
     */
    override fun sendPasswordReset(email: String) {
        authApi.sendRestEmail(mAuth, this, email, constants.authApiResetPassword())
    }

    /**
     * Update FCM Token
     */
    override fun updateFcmToken() {
        val currentFcmToken = sharedPrefs.getString(utilities.encode(constants.SP_FCM_TOKEN), "false")
        if (currentFcmToken != "false"){
            val decodedToken = utilities.decode(currentFcmToken)
            user?.firebaseUser?.uid?.let {
                val data = hashMapOf(it to decodedToken as Any)
                mDatabase?.let { fbDatabseApi.doWrite(it, constants.FB_FCM_TOKENS, data) }
            }
        }
    }

    /**
     * Stored Uid
     */
    override fun storeUid() {
        user?.firebaseUser?.uid?.let {
            sharedPrefs.edit().putString(utilities.encode(constants.SP_UID),
                    utilities.encode(it)).apply()
        }
    }

    /**
     * API Result
     */
    override fun apiResult(type: Constants.AuthApiType, success: Boolean, message: String?) {
        val errorMessage = message ?: "Undefined Error"
        when(type){
            constants.authApiCreateAccount() -> {
                when(success){
                    true -> {
                        user?.firebaseUser = mAuth?.currentUser
                        mListener?.onAuthCommandResult(constants.AUTH_CREATE_ACCOUNT_SUCCESSFUL, null)
                    }
                    false ->{
                        mListener?.onAuthCommandResult(constants.AUTH_CREATE_ACCOUNT_UNSUCCESSFUL, errorMessage)
                    }
                }
            }
            constants.authApiLogin() -> {
                when(success){
                    true -> {
                        user?.firebaseUser = mAuth?.currentUser
                        mListener?.onAuthCommandResult(constants.AUTH_LOGIN_SUCCESSFUL , null)
                    }
                    false ->{
                        mListener?.onAuthCommandResult(constants.AUTH_LOGIN_FAILED, errorMessage)
                    }
                }
            }
            constants.authApiResetPassword() -> {
                when(success){
                    true -> {
                        mListener?.onAuthCommandResult(constants.AUTH_PASSWORD_RESET_SUCCESSFUL, null)
                    }
                    false ->{
                        mListener?.onAuthCommandResult(constants.AUTH_PASSWORD_RESET_UNSUCCESSFUL, errorMessage)
                    }
                }
            }
            constants.authApiLogout() -> {
                when(success){
                    true -> {

                    }
                    false ->{

                    }
                }
            }
        }
    }

    /**
     * Logout
     */
    override fun logout() {
        mAuth?.signOut()
    }

    /**
     * Detach
     */
    override fun detach() {
        mListener = null
    }

    /**
     * Authentication State Changed
     */
    override fun onAuthStateChanged(auth: com.google.firebase.auth.FirebaseAuth) {
        user = User(auth.currentUser)
        if (auth.currentUser == null){
            mListener?.onAuthStateChange(constants.AUTH_STATE_LOGGED_OUT, "user logged out")
        } else {
            mListener?.onAuthStateChange(constants.AUTH_STATE_LOGGED_IN, "user logged in")
        }
    }
}