package com.rbiggin.a2do2gether.ui.login.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rbiggin.a2do2gether.R
import com.rbiggin.a2do2gether.utils.Constants
import kotlinx.android.synthetic.main.fragment_password.*

class PasswordFragment : MasterFragment(), IntLoginFragmentCallbacks {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_password, container, false)
    }

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

    override fun clearViews() {
        password_password_et.text.clear()
    }

    override fun clearViews(email: Boolean, passwordOne: Boolean, passwordTwo: Boolean) {
        if (passwordOne) {
            clearViews()
        }
    }

    companion object {
        fun newInstance(id: String): PasswordFragment {
            val fragment = PasswordFragment()
            val args = Bundle()
            args.putString(Constants.FRAGMENT_ID, id)
            fragment.arguments = args
            return fragment
        }
    }
}