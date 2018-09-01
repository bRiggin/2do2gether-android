package com.rbiggin.a2do2gether.ui.login

import com.rbiggin.a2do2gether.utils.Constants

interface IntLoginActivity {
    fun displayDialogMessage(message_id: Int, message: String?)

    fun displayFunctionalDialog(type_id: Int)

    fun displayFragment(fragment_id: Constants.Id)

    fun displayProgressSpinner(show: Boolean)

    fun launchMainActivity(toFragment: Constants.Fragment)
}