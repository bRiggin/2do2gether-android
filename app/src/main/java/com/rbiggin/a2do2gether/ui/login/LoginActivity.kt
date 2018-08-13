package com.rbiggin.a2do2gether.ui.login

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.rbiggin.a2do2gether.R
import com.rbiggin.a2do2gether.application.MyApplication
import com.rbiggin.a2do2gether.ui.main.MainActivity
import com.rbiggin.a2do2gether.ui.login.fragments.CreateAccountFragment
import com.rbiggin.a2do2gether.ui.login.fragments.LandingFragment
import com.rbiggin.a2do2gether.ui.login.fragments.PasswordFragment
import com.rbiggin.a2do2gether.utils.Constants
import com.rbiggin.a2do2gether.utils.Utilities

import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : AppCompatActivity(), IntLoginActivity{

    @Inject lateinit var presenter: LoginPresenter

    @Inject lateinit var utilities: Utilities

    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    private lateinit var TAG: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        (application as MyApplication).daggerComponent.inject(this)
        TAG = Constants.LOGIN_ACTIVITY_TAG

        presenter.onViewAttached(this)

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        loginViewPager.adapter = mSectionsPagerAdapter

        createNotificationChannel()
    }

    override fun onResume() {
        super.onResume()
        presenter.onViewWillShow()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onViewDetached()
    }

    override fun displayDialogMessage(message_id: Int, message: String?) {
        var dialogMessage = getString(R.string.error_unknown)
        message?.let{ dialogMessage = message }

        val messageString: String = when (message_id){
            Constants.ERROR_BLANK_EMAIL_STRING -> { getString(R.string.error_blank_email) }
            Constants.ERROR_BLANK_PASSWORD_STRING -> { getString(R.string.error_blank_password) }
            Constants.ERROR_MISSING_PASSWORD -> { getString(R.string.error_missing_password) }
            Constants.ERROR_PASSWORDS_DO_NOT_MATCH -> { getString(R.string.error_password_mismatch) }
            Constants.AUTH_LOGIN_FAILED -> { dialogMessage }
            Constants.AUTH_PASSWORD_RESET_SUCCESSFUL -> { getString(R.string.password_reset_successful) }
            Constants.AUTH_PASSWORD_RESET_UNSUCCESSFUL -> { dialogMessage}
            Constants.AUTH_CREATE_ACCOUNT_SUCCESSFUL -> { getString(R.string.create_account_successful)}
            Constants.AUTH_CREATE_ACCOUNT_UNSUCCESSFUL -> { dialogMessage }
            else -> { getString(R.string.error_unknown) }
        }
        utilities.showOKDialog(this, getString(R.string.app_name), messageString!!)
    }

    override fun displayFunctionalDialog(type_id: Int) {
        Log.w(TAG, "displayFunctionalDialog called from presenter with ID: $type_id")
        val lambda: () -> Unit = when(type_id){
            Constants.DIALOG_FORGOT_PASSWORD -> {
                { presenter.sendPasswordReset() }
            } else -> {
                {}
                //todo throw exception
            }
        }
        utilities.showFunctionDialog(this, getString(R.string.app_name),
                                    getString(R.string.forgot_password_prompt), positiveCode = lambda)
    }

    override fun displayFragment(fragment_id: Int) {
        when(fragment_id){
            Constants.REGISTER_FRAGMENT_ID -> {
                loginViewPager.currentItem = 0
            }
            Constants.ADDRESS_FRAGMENT_ID -> {
                loginViewPager.currentItem = 1
            }
            Constants.PASSWORD_FRAGMENT_ID -> {
                loginViewPager.currentItem = 2
            }
        }
    }

    override fun onBackPressed() {
        when (loginViewPager.currentItem){
            0 -> {
                presenter.backPressedInFragment(Constants.REGISTER_FRAGMENT_ID)
            }
            1 -> {
                super.onBackPressed()
            }
            2 -> {
                presenter.backPressedInFragment(Constants.PASSWORD_FRAGMENT_ID)
            }
            else -> {
                super.onBackPressed()
            }

        }
    }

    override fun displayProgressSpinner(show: Boolean) {
        if (show) {
            hideKeyboard()
            login_progress_spinner.visibility = View.VISIBLE
        } else {
            login_progress_spinner.visibility = View.GONE
        }
    }

    private fun hideKeyboard(){
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun launchMainActivity(email: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("email", email)
        startActivity(intent)
        finish()
    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return when(position){
                0 -> {
                    CreateAccountFragment.newInstance(Constants.REGISTER_FRAGMENT_ID)
                } 1 -> {
                    LandingFragment.newInstance(Constants.ADDRESS_FRAGMENT_ID)
                } 2 -> {
                    PasswordFragment.newInstance(Constants.PASSWORD_FRAGMENT_ID)
                } else -> {
                    LandingFragment.newInstance(Constants.ADDRESS_FRAGMENT_ID)
                }
            }
        }

        override fun getCount(): Int {
            return Constants.NUMBER_OF_LOGIN_FRAGMENTS
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notification_channel_name)
            val description = getString(R.string.notification_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(getString(R.string.notification_channel_id), name, importance)
            channel.description = description
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager!!.createNotificationChannel(channel)
        }
    }
}
