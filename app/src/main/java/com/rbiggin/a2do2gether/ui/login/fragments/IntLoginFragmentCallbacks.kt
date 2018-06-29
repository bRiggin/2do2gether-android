package com.rbiggin.a2do2gether.ui.login.fragments

/**
 * Insert class/object/interface/file description...
 */
interface IntLoginFragmentCallbacks {
    fun clearViews()

    fun clearViews(email: Boolean = false, passwordOne: Boolean = false, passwordTwo: Boolean = false)
}