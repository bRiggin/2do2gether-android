package com.rbiggin.a2do2gether.ui.login

import android.content.Context
import android.content.Intent
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

/**
 * Activity for app login.
 */
class LoginActivity : AppCompatActivity(), IntLoginActivity{
    /** injected instance of Activity's presenter. */
    @Inject lateinit var presenter: IntLoginPresenter

    /** injected instance of Constants. */
    @Inject lateinit var constants: Constants

    /** injected instance of Constants. */
    @Inject lateinit var utilities: Utilities

    /** Instance of inner class: SectionsPagerAdapter. */
    private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

    /** Activity's logging TAG */
    private lateinit var TAG: String

    /**
     * onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        (application as MyApplication).daggerComponent.inject(this)
        TAG = constants.LOGIN_ACTIVITY_TAG

        presenter.setView(this)

        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)

        loginViewPager.adapter = mSectionsPagerAdapter

        presenter.onViewWillShow()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onViewWillHide()
    }

    /**
     * Use utility's "OkDialog" function to display a error message to the user.
     */
    override fun displayDialogMessage(message_id: Int, message: String?) {
        var dialogMessage = getString(R.string.error_unknown)
        message?.let{ dialogMessage = message }

        val messageString: String = when (message_id){
            constants.ERROR_BLANK_EMAIL_STRING -> { getString(R.string.error_blank_email) }
            constants.ERROR_BLANK_PASSWORD_STRING -> { getString(R.string.error_blank_password) }
            constants.ERROR_MISSING_PASSWORD -> { getString(R.string.error_missing_password) }
            constants.ERROR_PASSWORDS_DO_NOT_MATCH -> { getString(R.string.error_password_mismatch) }
            constants.AUTH_LOGIN_FAILED -> { dialogMessage }
            constants.AUTH_PASSWORD_RESET_SUCCESSFUL -> { getString(R.string.password_reset_successful) }
            constants.AUTH_PASSWORD_RESET_UNSUCCESSFUL -> { dialogMessage}
            constants.AUTH_CREATE_ACCOUNT_SUCCESSFUL -> { getString(R.string.create_account_successful)}
            constants.AUTH_CREATE_ACCOUNT_UNSUCCESSFUL -> { dialogMessage }
            else -> { getString(R.string.error_unknown) }
        }
        utilities.showOKDialog(this, getString(R.string.app_name), messageString!!)
    }

    /**
     * Use utility's "FunctionalDialog" function to display a error message to the user.
     */
    override fun displayFunctionalDialog(type_id: Int) {
        Log.w(TAG, "displayFunctionalDialog called from presenter with ID: $type_id")
        val lambda: () -> Unit = when(type_id){
            constants.DIALOG_FORGOT_PASSWORD -> {
                { presenter.sendPasswordReset() }
            } else -> {
                {}
                //todo throw exception
            }
        }
        utilities.showFunctionDialog(this, getString(R.string.app_name),
                                    getString(R.string.forgot_password_prompt), positiveCode = lambda)
    }

    /**
     * Instruction from presenter to display specific fragment.
     */
    override fun displayFragment(fragment_id: Int) {
        when(fragment_id){
            constants.REGISTER_FRAGMENT_ID -> {
                loginViewPager.currentItem = 0
            }
            constants.ADDRESS_FRAGMENT_ID -> {
                loginViewPager.currentItem = 1
            }
            constants.PASSWORD_FRAGMENT_ID -> {
                loginViewPager.currentItem = 2
            }
        }
    }

    /**
     * onBackPressed, allows user to intuitively navigate through fragments.
     */
    override fun onBackPressed() {
        when (loginViewPager.currentItem){
            0 -> {
                presenter.backPressedInFragment(constants.REGISTER_FRAGMENT_ID)
            }
            1 -> {
                super.onBackPressed()
            }
            2 -> {
                presenter.backPressedInFragment(constants.PASSWORD_FRAGMENT_ID)
            }
            else -> {
                super.onBackPressed()
                //todo throw exception
            }

        }
    }

    /**
     * Display/hide progress spinner to the user.
     */
    override fun displayProgressSpinner(show: Boolean) {
        if (show) {
            hideKeyboard()
            login_progress_spinner.visibility = View.VISIBLE
        } else {
            login_progress_spinner.visibility = View.GONE
        }
    }

    /**
     * Hide keyboard
     */
    private fun hideKeyboard(){
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    /**
     * Launch main activity and destroy login activity.
     */
    override fun launchMainActivity(email: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("email", email)
        startActivity(intent)
        finish()
    }

    /**
     * Returns a fragment corresponding to one of the pages.
     */
    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return when(position){
                0 -> {
                    CreateAccountFragment.newInstance(constants.REGISTER_FRAGMENT_ID)
                } 1 -> {
                    LandingFragment.newInstance(constants.ADDRESS_FRAGMENT_ID)
                } 2 -> {
                    PasswordFragment.newInstance(constants.PASSWORD_FRAGMENT_ID)
                } else -> {
                    LandingFragment.newInstance(constants.ADDRESS_FRAGMENT_ID)
                }
            }
        }

        override fun getCount(): Int {
            return constants.NUMBER_OF_LOGIN_FRAGMENTS
        }
    }
}
