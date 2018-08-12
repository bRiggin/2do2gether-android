package com.rbiggin.a2do2gether.main

import com.nhaarman.mockito_kotlin.*
import com.rbiggin.a2do2gether.repository.AuthRepository
import com.rbiggin.a2do2gether.repository.UserProfileRepository
import com.rbiggin.a2do2gether.ui.main.MainActivity
import com.rbiggin.a2do2gether.ui.main.MainPresenter
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(JUnitParamsRunner::class)
class MainPresenterTest {
    private lateinit var presenter: MainPresenter
    private lateinit var activity: MainActivity
    private lateinit var authRepo: AuthRepository
    private lateinit var userRepo: UserProfileRepository

    private val emailHandedFromMainActivity = "testEmail@test.com"
    private val uidFromAuthRepo = "demonstrationUid"

    @Before
    fun `configure Main Presenter`() {
        authRepo = mock()
        userRepo = mock()

        whenever(authRepo.userId()).doReturn(uidFromAuthRepo)

        activity = mock()
    }
}