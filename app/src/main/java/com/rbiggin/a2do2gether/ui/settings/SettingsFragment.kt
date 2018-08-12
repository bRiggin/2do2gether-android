package com.rbiggin.a2do2gether.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rbiggin.a2do2gether.R
import com.rbiggin.a2do2gether.application.MyApplication
import com.rbiggin.a2do2gether.ui.base.BaseFragment
import com.rbiggin.a2do2gether.ui.login.LoginActivity
import javax.inject.Inject

import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : BaseFragment(), IntSettingsFragment {

    @Inject lateinit var presenter: SettingsPresenter

    companion object {
        /**
         * @param id ID handed to Fragment.
         * @return A new instance of fragment: AddressFragment.
         */
        fun newInstance(id: Int): SettingsFragment {
            val fragment = SettingsFragment()
            val args = Bundle()
            args.putInt("FRAGMENT_ID", id)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context?) {
        (context?.applicationContext as MyApplication).daggerComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.onViewAttached(this)
    }

    override fun onResume() {
        super.onResume()
        presenter.onViewWillShow()

        logout_btn.setOnClickListener {
            presenter.logout()
        }
    }

    override fun onPause() {
        super.onPause()
        presenter.onViewWillHide()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onViewDetached()
    }

    override fun launchLoginActivity() {
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

    override fun onDisplayDialogMessage(message_id: Int, message: String?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
