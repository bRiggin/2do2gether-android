package com.rbiggin.a2do2gether.firebase

import com.google.firebase.database.DataSnapshot
import com.rbiggin.a2do2gether.utils.Constants

/**
 * Defines callbacks from Firebase database API calls
 */
interface IntFirebaseDatabaseListener {
    fun onDatabaseResult(type: Constants.DatabaseApi, data: DataSnapshot?, success: Boolean, message: String?)
}