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

class AuthRepository @Inject constructor(private val authApi: FirebaseAuth,
                                         private val sharedPrefs: SharedPreferences,
                                         private val fbDatabaseApi: IntFirebaseDatabase,
                                         private val utilities: Utilities) :
                     IntFirebaseAuthListener, com.google.firebase.auth.FirebaseAuth.AuthStateListener{

    private val falseString: String = "false"

    private var user: User? = null

    private var mAuth: com.google.firebase.auth.FirebaseAuth? = null

    private var mListener: Listener? = null

    private var mActivityListener: ActiveListener? = null

    private var mDatabase: DatabaseReference? = null

    init {
        mAuth = com.google.firebase.auth.FirebaseAuth.getInstance()
        mAuth?.addAuthStateListener(this)
        user = User(mAuth?.currentUser)
        mDatabase = com.google.firebase.database.FirebaseDatabase.getInstance().reference
    }

    fun setListener(listener: Listener) {
        mListener = listener
        if (listener is ActiveListener) {
            mActivityListener = listener
        }
    }

    fun isUserLoggedIn(): Boolean {
        return when(user?.firebaseUser){
            null -> { false }
            else -> { true }
        }
    }

    fun userId(): String? {
        return user?.firebaseUser?.uid
    }

    fun getEmail(): String? {
        return user?.firebaseUser?.email
    }

    fun createAccount(email: String, password: String) {
        authApi.createAccount(mAuth, this, email, password, Constants.Auth.CREATE_ACCOUNT)
    }

    fun login(email: String, password: String) {
        authApi.login(mAuth, this, email, password, Constants.Auth.LOGIN)
    }

    fun sendPasswordReset(email: String) {
        authApi.sendRestEmail(mAuth, this, email, Constants.Auth.RESET_PASSWORD)
    }

    fun updateFcmToken() {
        val currentFcmToken = sharedPrefs.getString(utilities.encode(Constants.SP_FCM_TOKEN), falseString)
        if (currentFcmToken != falseString){
            val decodedToken = utilities.decode(currentFcmToken)
            user?.firebaseUser?.uid?.let {
                val data = hashMapOf(it to decodedToken as Any)
                mDatabase?.let { fbDatabaseApi.doWrite(it, Constants.FB_FCM_TOKENS, data) }
            }
        }
    }

    fun removeFcmToken() {
        val currentFcmToken = sharedPrefs.getString(utilities.encode(Constants.SP_FCM_TOKEN), "false")
        if (currentFcmToken != "false"){
            user?.firebaseUser?.uid?.let {
                val mUid = it
                mDatabase?.let { fbDatabaseApi.doDelete(it, "${Constants.FB_FCM_TOKENS}/$mUid") }
            }
        }
    }

    fun storeUid() {
        user?.firebaseUser?.uid?.let {
            sharedPrefs.edit().putString(utilities.encode(Constants.SP_UID),
                    utilities.encode(it)).apply()
        }
    }

    override fun apiResult(type: Constants.Auth, success: Boolean, message: String?) {
        val errorMessage = message ?: "Undefined Error"
        when (type) {
            Constants.Auth.CREATE_ACCOUNT -> {
                when (success) {
                    true -> {
                        user?.firebaseUser = mAuth?.currentUser
                        mActivityListener?.onAuthCommandResult(Constants.AUTH_CREATE_ACCOUNT_SUCCESSFUL, null)
                    }
                    false -> {
                        mActivityListener?.onAuthCommandResult(Constants.AUTH_CREATE_ACCOUNT_UNSUCCESSFUL, errorMessage)
                    }
                }
            }
            Constants.Auth.LOGIN -> {
                when (success) {
                    true -> {
                        user?.firebaseUser = mAuth?.currentUser
                        mActivityListener?.onAuthCommandResult(Constants.AUTH_LOGIN_SUCCESSFUL, null)
                    }
                    false -> {
                        mActivityListener?.onAuthCommandResult(Constants.AUTH_LOGIN_FAILED, errorMessage)
                    }
                }
            }
            Constants.Auth.RESET_PASSWORD -> {
                when (success) {
                    true -> {
                        mActivityListener?.onAuthCommandResult(Constants.AUTH_PASSWORD_RESET_SUCCESSFUL, null)
                    }
                    false -> {
                        mActivityListener?.onAuthCommandResult(Constants.AUTH_PASSWORD_RESET_UNSUCCESSFUL, errorMessage)
                    }
                }
            }
            Constants.Auth.LOGOUT -> {
                when (success) {
                    true -> {

                    }
                    false -> {

                    }
                }
            }
        }
    }

    fun logout() {
        mAuth?.signOut()
    }

    fun detach() {
        mListener = null
        mActivityListener = null
    }

    override fun onAuthStateChanged(auth: com.google.firebase.auth.FirebaseAuth) {
        user = User(auth.currentUser)
        if (auth.currentUser == null){
            mListener?.onAuthStateChange(Constants.AUTH_STATE_LOGGED_OUT, "user logged out")
        } else {
            mListener?.onAuthStateChange(Constants.AUTH_STATE_LOGGED_IN, "user logged in")
        }
    }

    interface Listener{
        fun onAuthStateChange(response_id: Int, message: String?)
    }

    interface ActiveListener: Listener{
        fun onAuthCommandResult(response_id: Int, message: String?)
    }
}