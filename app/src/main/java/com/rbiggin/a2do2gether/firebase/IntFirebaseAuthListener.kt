package com.rbiggin.a2do2gether.firebase

import com.rbiggin.a2do2gether.utils.Constants

/**
 * Defines callbacks from Firebase Auth API calls
 */
interface IntFirebaseAuthListener {
    fun apiResult(type: Constants.AuthApiType, success: Boolean, message: String?)
}