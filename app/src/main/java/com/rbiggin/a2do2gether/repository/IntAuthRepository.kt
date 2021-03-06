package com.rbiggin.a2do2gether.repository

/**
 * Defines Authentication Repository calls
 */
interface IntAuthRepository{
    fun setup(listener: IntAuthRepositoryListener)

    fun isUserLoggedIn(): Boolean

    fun userId(): String?

    fun getEmail(): String?

    fun createAccount(email: String, password: String)

    fun login(email: String, password: String)

    fun logout()

    fun sendPasswordReset(email: String)

    fun updateFcmToken()

    fun storeUid()

    fun detach()
}

/**
 * Defines callbacks from Authentication Repository
 */
interface IntAuthRepositoryListener{
    fun onAuthCommandResult(response_id: Int, message: String?)

    fun onAuthStateChange(response_id: Int, message: String?)
}