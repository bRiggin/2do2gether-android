package com.rbiggin.a2do2gether.firebase

import com.rbiggin.a2do2gether.utils.Constants

interface IntFirebaseAuthListener {
    fun apiResult(type: Constants.Auth, success: Boolean, message: String?)
}