package com.rbiggin.a2do2gether.firebase

import android.provider.SyncStateContract
import com.google.firebase.auth.FirebaseAuth
import com.rbiggin.a2do2gether.repository.AuthRepository
import com.rbiggin.a2do2gether.utils.Constants

/**
 * Object that performs all Firebase authentication calls
 */
object FirebaseAuth{

    /**
     * Creates Account
     */
    fun createAccount(auth: FirebaseAuth?, listener: IntFirebaseAuthListener?, email: String,
                      password: String, type: Constants.AuthApiType) {
        auth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener {
            if (it.isSuccessful) {
                listener?.apiResult(type, true, null)
            } else {
                listener?.apiResult(type, false, it.exception?.message)
            }
        }
    }

    /**
     * Login
     */
    fun login(auth: FirebaseAuth?, listener: IntFirebaseAuthListener?, email: String,
              password: String, type: Constants.AuthApiType) {
        auth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener {
            if (it.isSuccessful) {
                listener?.apiResult(type, true, null)
            } else {
                listener?.apiResult(type, false, it.exception?.message)
            }
        }
    }

    /**
     * Send Reset Email
     */
    fun sendRestEmail(auth: FirebaseAuth?, listener: IntFirebaseAuthListener?, email: String,
                      type: Constants.AuthApiType) {
        auth?.sendPasswordResetEmail(email)?.addOnCompleteListener {
            if (it.isSuccessful) {
                listener?.apiResult(type, true, null)
            } else {
                listener?.apiResult(type, false, it.exception?.message)
            }
        }
    }
}