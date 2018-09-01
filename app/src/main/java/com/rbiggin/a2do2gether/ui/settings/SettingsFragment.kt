package com.rbiggin.a2do2gether.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.clicks
import com.rbiggin.a2do2gether.R
import com.rbiggin.a2do2gether.application.MyApplication
import com.rbiggin.a2do2gether.model.SettingsUpdate
import com.rbiggin.a2do2gether.ui.base.BaseFragment
import com.rbiggin.a2do2gether.ui.login.LoginActivity
import com.rbiggin.a2do2gether.utils.Constants
import com.rbiggin.a2do2gether.utils.Utilities
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject

import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : BaseFragment(), SettingsPresenter.View {

    @Inject
    lateinit var presenter: SettingsPresenter

    @Inject
    lateinit var utilities: Utilities

    val switcthSubject: PublishSubject<SettingsUpdate> = PublishSubject.create()

    companion object {
        fun newInstance(id: Int): SettingsFragment {
            val fragment = SettingsFragment()
            val args = Bundle()
            args.putInt(Constants.FRAGMENT_ID, id)
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

        doItemReorderSwitch.clicks().subscribeBy {
            switcthSubject.onNext(SettingsUpdate(Constants.Setting.LIST_REORDER, doItemReorderSwitch.isChecked))
        }

        profilePublicSwitch.clicks().subscribeBy {
            switcthSubject.onNext(SettingsUpdate(Constants.Setting.PROFILE_PRIVACY, profilePublicSwitch.isChecked))
        }

        connectionRequestSwitch.clicks().subscribeBy {
            switcthSubject.onNext(SettingsUpdate(Constants.Setting.CONNECTION_REQUEST, connectionRequestSwitch.isChecked))
        }

        newConnectionSwitch.clicks().subscribeBy {
            switcthSubject.onNext(SettingsUpdate(Constants.Setting.NEW_CONNECTIONS, newConnectionSwitch.isChecked))
        }

        newListSwitch.clicks().subscribeBy {
            switcthSubject.onNext(SettingsUpdate(Constants.Setting.NEW_LIST, newListSwitch.isChecked))
        }

        analyticsSwitch.clicks().subscribeBy {
            switcthSubject.onNext(SettingsUpdate(Constants.Setting.ANALYTICS, analyticsSwitch.isChecked))
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

    override fun updateSwitch(update: SettingsUpdate) {
        when (update.type) {
            Constants.Setting.LIST_REORDER -> {
                doItemReorderSwitch.isChecked = update.value
            }
            Constants.Setting.PROFILE_PRIVACY -> {
                profilePublicSwitch.isChecked = update.value
            }
            Constants.Setting.CONNECTION_REQUEST -> {
                connectionRequestSwitch.isChecked = update.value
            }
            Constants.Setting.NEW_CONNECTIONS -> {
                newConnectionSwitch.isChecked = update.value
            }
            Constants.Setting.NEW_LIST -> {
                newListSwitch.isChecked = update.value
            }
            Constants.Setting.ANALYTICS -> {
                analyticsSwitch.isChecked = update.value
            }
        }
    }

    override fun onDisplayDialogMessage(message_id: Int, message: String?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
