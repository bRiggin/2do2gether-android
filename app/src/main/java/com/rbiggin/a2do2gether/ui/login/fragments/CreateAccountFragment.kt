package com.rbiggin.a2do2gether.ui.login.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rbiggin.a2do2gether.R
import com.rbiggin.a2do2gether.utils.Constants
import kotlinx.android.synthetic.main.fragment_register.*

class CreateAccountFragment : MasterFragment(), IntLoginFragmentCallbacks {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onResume() {
        super.onResume()
        register_create_btn.setOnClickListener {
            presenter.createNewAccount(register_email_et.text.toString(),
                                       register_password_et.text.toString(),
                                       register_re_password_et.text.toString(), this)
        }
    }

    override fun clearViews() {
        register_email_et.text.clear()
        register_password_et.text.clear()
        register_re_password_et.text.clear()
    }

    override fun clearViews(email: Boolean, passwordOne: Boolean, passwordTwo: Boolean) {
        if (email){
            register_email_et.text.clear()
        }
        if (passwordOne){
            register_password_et.text.clear()
        }
        if (passwordTwo) {
            register_re_password_et.text.clear()
        }
    }

    companion object {
        fun newInstance(id: Int): CreateAccountFragment {
            val fragment = CreateAccountFragment()
            val args = Bundle()
            args.putInt(Constants.FRAGMENT_ID, id)
            fragment.arguments = args
            return fragment
        }
    }
}