package com.rbiggin.a2do2gether.ui.login.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rbiggin.a2do2gether.R
import kotlinx.android.synthetic.main.fragment_address.*

/**
 * Fragment instance the manages the user's input of their email address during login
 */
class LandingFragment : MasterFragment(), IntLoginFragmentCallbacks{

    /**
     * Companion object to provide access to newInstance.
     */
    companion object {
        /**
         * Create a new instance of this fragment using the provided parameters.
         *
         * @param id ID handed to Fragment.
         * @return A new instance of fragment: AddressFragment.
         */
        fun newInstance(id: Int): LandingFragment {
            val fragment = LandingFragment()
            val args = Bundle()
            args.putInt("FRAGMENT_ID", id)
            fragment.arguments = args
            return fragment
        }
    }

    /**
     * onCreateViewrhy
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_address, container, false)
    }

    /**
     * onResume
     */
    override fun onResume() {
        super.onResume()
        address_create_account_btn.setOnClickListener {
            presenter.newAccountBtnPressed(this)
        }

        address_next_btn.setOnClickListener {
            presenter.emailSubmitted(address_email_et.text.toString(), this)
        }
    }

    /**
     * Clears all UI views within fragment
     */
    override fun clearViews() {
        address_email_et.text.clear()
    }

    /**
     *
     */
    override fun clearViews(email: Boolean, passwordOne: Boolean, passwordTwo: Boolean) {
        if (email) {
            clearViews()
        }
    }
}