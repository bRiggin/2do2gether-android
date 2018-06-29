package com.rbiggin.a2do2gether.ui.login.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rbiggin.a2do2gether.R
import kotlinx.android.synthetic.main.fragment_register.*

/**
 * Fragment instance the manages the user's input of their new password while creating account.
 */
class CreateAccountFragment : MasterFragment(), IntLoginFragmentCallbacks {

    /**
     * Companion object to provide access to newInstance.
     */
    companion object {
        /**
         * Create a new instance of this fragment using the provided parameters.
         *
         * @param id ID handed to Fragment.
         * @return A new instance of fragment: RegisterFragment.
         */
        fun newInstance(id: Int): CreateAccountFragment {
            val fragment = CreateAccountFragment()
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
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    /**
     * onResume
     */
    override fun onResume() {
        super.onResume()
        register_create_btn.setOnClickListener {
            presenter.createNewAccount(register_email_et.text.toString(),
                                       register_password_et.text.toString(),
                                       register_re_password_et.text.toString(), this)
        }
    }

    /**
     * Clears all UI views within fragment
     */
    override fun clearViews() {
        register_email_et.text.clear()
        register_password_et.text.clear()
        register_re_password_et.text.clear()
    }

    /**
     *
     */
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
}