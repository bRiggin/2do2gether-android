package com.rbiggin.a2do2gether.repository

import javax.inject.Inject

class UidProvider @Inject constructor(private val authRepository: AuthRepository) {

    private var privateUid: String? = null

    fun getUid(): String? {
        return authRepository.userId()
    }
}