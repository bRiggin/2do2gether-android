package com.rbiggin.a2do2gether.repository

import javax.inject.Inject

class UidProvider @Inject constructor(private val authRepository: AuthRepository) {

    fun getUid(): String? {
        return authRepository.userId()
    }
}