package com.rbiggin.a2do2gether.repository

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

    fun removeFcmToken()

    fun storeUid()

    fun detach()
}

interface IntAuthRepositoryListener{
    fun onAuthStateChange(response_id: Int, message: String?)
}

interface IntAuthRepositoryActiveListener: IntAuthRepositoryListener{
    fun onAuthCommandResult(response_id: Int, message: String?)
}