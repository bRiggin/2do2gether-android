package com.rbiggin.a2do2gether.ui.login.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rbiggin.a2do2gether.R
import kotlinx.android.synthetic.main.fragment_password.*

/**
 * Fragment instance the manages the user's input of their account password during login
 */
class PasswordFragment : MasterFragment(), IntLoginFragmentCallbacks {

    /**
     * Companion object to provide access to newInstance.
     */
    companion object {
        /**
         * Create a new instance of this fragment using the provided parameters.
         *
         * @param id ID handed to Fragment.
         * @return A new instance of fragment: PasswordFragment.
         */
        fun newInstance(id: Int): PasswordFragment {
            val fragment = PasswordFragment()
            val args = Bundle()
            args.putInt("FRAGMENT_ID", id)
            fragment.arguments = args
            return fragment
        }
    }

    /**
     * onCreateView
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_password, container, false)
    }

    /**
     * onResume
     */
    override fun onResume() {
        super.onResume()
        password_login_btn.setOnClickListener {
            presenter.loginWithPassword(password_password_et.text.toString())
            clearViews()
        }

        password_forgotten_btn.setOnClickListener {
            presenter.passwordForgotten()
        }
    }

    /**
     * Clears all UI views within fragment
     */
    override fun clearViews() {
        password_password_et.text.clear()
    }

    /**
     *
     */
    override fun clearViews(email: Boolean, passwordOne: Boolean, passwordTwo: Boolean) {
        if (passwordOne) {
            clearViews()
        }
    }
}