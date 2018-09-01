package com.rbiggin.a2do2gether.ui.login.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rbiggin.a2do2gether.R
import com.rbiggin.a2do2gether.utils.Constants
import kotlinx.android.synthetic.main.fragment_address.*

class LandingFragment : MasterFragment(), IntLoginFragmentCallbacks{

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_address, container, false)
    }

    override fun onResume() {
        super.onResume()
        address_create_account_btn.setOnClickListener {
            presenter.newAccountBtnPressed(this)
        }

        address_next_btn.setOnClickListener {
            presenter.emailSubmitted(address_email_et.text.toString(), this)
        }
    }

    override fun clearViews() {
        address_email_et.text.clear()
    }

    override fun clearViews(email: Boolean, passwordOne: Boolean, passwordTwo: Boolean) {
        if (email) {
            clearViews()
        }
    }

    companion object {
        fun newInstance(id: String): LandingFragment {
            val fragment = LandingFragment()
            val args = Bundle()
            args.putString(Constants.FRAGMENT_ID, id)
            fragment.arguments = args
            return fragment
        }
    }
}