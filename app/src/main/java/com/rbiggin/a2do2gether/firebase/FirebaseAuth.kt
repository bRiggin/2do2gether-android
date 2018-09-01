package com.rbiggin.a2do2gether.firebase

import com.google.firebase.auth.FirebaseAuth
import com.rbiggin.a2do2gether.utils.Constants

object FirebaseAuth{

    fun createAccount(auth: FirebaseAuth?, listener: IntFirebaseAuthListener?, email: String,
                      password: String, type: Constants.Auth) {
        auth?.createUserWithEmailAndPassword(email, password)?.addOnCompleteListener {
            if (it.isSuccessful) {
                listener?.apiResult(type, true, null)
            } else {
                listener?.apiResult(type, false, it.exception?.message)
            }
        }
    }

    fun login(auth: FirebaseAuth?, listener: IntFirebaseAuthListener?, email: String,
              password: String, type: Constants.Auth) {
        auth?.signInWithEmailAndPassword(email, password)?.addOnCompleteListener {
            if (it.isSuccessful) {
                listener?.apiResult(type, true, null)
            } else {
                listener?.apiResult(type, false, it.exception?.message)
            }
        }
    }

    fun sendRestEmail(auth: FirebaseAuth?, listener: IntFirebaseAuthListener?, email: String,
                      type: Constants.Auth) {
        auth?.sendPasswordResetEmail(email)?.addOnCompleteListener {
            if (it.isSuccessful) {
                listener?.apiResult(type, true, null)
            } else {
                listener?.apiResult(type, false, it.exception?.message)
            }
        }
    }
}