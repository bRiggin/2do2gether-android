package com.rbiggin.a2do2gether.ui.login

/**
 * Insert class/object/interface/file description...
 */
interface IntLoginActivity {
    fun displayDialogMessage(message_id: Int, message: String?)

    fun displayFunctionalDialog(type_id: Int)

    fun displayFragment(fragment_id: Int)

    fun displayProgressSpinner(show: Boolean)

    fun launchMainActivity(email: String)
}