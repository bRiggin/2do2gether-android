package com.rbiggin.a2do2gether.ui.login

import com.rbiggin.a2do2gether.ui.login.fragments.IntLoginFragmentCallbacks

/**
 * Insert class/object/interface/file description...
 */
interface IntLoginPresenter {
    fun setView(loginActivity: IntLoginActivity)

    fun onViewWillShow()

    fun onViewWillHide()

    fun emailSubmitted(email: String?, reference: IntLoginFragmentCallbacks)

    fun newAccountBtnPressed(reference: IntLoginFragmentCallbacks)

    fun passwordForgotten()

    fun sendPasswordReset()

    fun createNewAccount(email: String, password_one: String, password_two: String,
                         reference: IntLoginFragmentCallbacks)

    fun loginWithPassword(password: String, reference: IntLoginFragmentCallbacks)

    fun backPressedInFragment(fragment_id: Int)
}