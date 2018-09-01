package com.rbiggin.a2do2gether.ui.login.fragments

interface IntLoginFragmentCallbacks {
    fun clearViews()

    fun clearViews(email: Boolean = false, passwordOne: Boolean = false, passwordTwo: Boolean = false)
}