package com.rbiggin.a2do2gether.ui.main

import android.content.SharedPreferences
import android.graphics.Bitmap
import com.rbiggin.a2do2gether.repository.*
import com.rbiggin.a2do2gether.ui.base.BasePresenter
import com.rbiggin.a2do2gether.utils.Constants
import com.rbiggin.a2do2gether.utils.Utilities
import javax.inject.Inject

/**
 * MainPresenter Interface
 */
class MainPresenter @Inject constructor(private val authRepo: IntAuthRepository,
                                        private val userRepo: IntUserRepositoryActivity,
                                        private val connectionsRepository: IntConnectionsRepository,
                                        utilities: Utilities,
                                        sharedPreferences: SharedPreferences) :
        BasePresenter<MainActivity>(sharedPreferences, utilities),
        IntAuthRepositoryListener,
        IntUserRepositoryOnChangeListener {

    private var mActivity: MainPresenter.View? = null

    private var currentFragment: Constants.Fragment? = null

    override fun onViewAttached(view: MainActivity) {
        super.onViewAttached(view)
        mActivity = view

        setupRepositories()
        if (!authRepo.isUserLoggedIn()) {
            mActivity?.launchLoginActivity()
        }
    }

    override fun onViewWillShow() {
        var profilePicture: Bitmap?
        authRepo.getEmail()?.let {
            mActivity?.setupActivity(it)
        } ?: throw ExceptionInInitializerError("MainPresenter, onViewWillSHow: authentication " +
                "repository return null user uid and therefore unable to setup user repository.")

        mActivity?.updateActionBar(Constants.Fragment.TODO)
        mActivity?.launchFragment(Constants.Fragment.TODO, false)
        currentFragment = Constants.Fragment.TODO

        authRepo.userId()?.let {
            profilePicture = mActivity?.getProfilePicture(it)

            if (profilePicture == null) {
                userRepo.getProfilePicture()
            } else {
                mActivity?.updateProfilePicture(profilePicture as Bitmap)
            }

        } ?: throw ExceptionInInitializerError("MainPresenter, onViewWillSHow: authentication " +
                "repository return null user uid and therefore unable to setup user repository.")
        userRepo.getUsersName()
    }

    override fun onViewWillHide() {
        super.onViewWillHide()
        mActivity = null
    }

    override fun onViewDetached() {
        super.onViewDetached()
        mActivity = null
    }

    /**
     * Navigation Drawer Item Selected
     *
     * Fragments are not kept on back stack, behaviour has been decided to be:
     * - First back press takes user back to 2do lists.
     * - Second back pressed kills apps.
     */
    fun onNavDrawerItemSelected(type: Constants.Fragment, backStackCount: Int) {
        if (isCurrentFragmentDifferent(type)) {
            mActivity?.updateActionBar(type)
            if (backStackCount == 1 && type != currentFragment) {
                mActivity?.popBackStack()
            }
            when (type) {
                Constants.Fragment.TODO -> {
                    mActivity?.popBackStack()
                }
                Constants.Fragment.CHECKLIST, Constants.Fragment.MY_CONNECTIONS,
                Constants.Fragment.MY_PROFILE, Constants.Fragment.SETTINGS -> {
                    mActivity?.launchFragment(type, true)
                }
                else -> {
                    throw IllegalArgumentException("MainPresenter Interface, navDrawerItemSelected: has been supplied with an illegal input.")
                }
            }
            currentFragment = type
        }
    }

    private fun setupRepositories() {
        userRepo.setActivity(this)
        authRepo.setup(this)
        authRepo.userId()?.let {
            userRepo.setup(it)
            connectionsRepository.setup(it)
        }
    }

    fun onBackPressed() {
        if (currentFragment != Constants.Fragment.TODO) {
            mActivity?.updateActionBar(Constants.Fragment.TODO)
            mActivity?.updateNavigationDrawer(Constants.Fragment.TODO)
            currentFragment = Constants.Fragment.TODO
        }
    }

    private fun isCurrentFragmentDifferent(selectedType: Constants.Fragment): Boolean {
        return currentFragment != selectedType
    }


    override fun onUserDetailsChanged(userName: String) {
        mActivity?.updateUsersName(userName)
    }

    override fun onProfilePictureChanged(image: Bitmap) {
        mActivity?.updateProfilePicture(image)
        authRepo.userId()?.let { mActivity?.saveProfilePicture(image, it) }
                ?: throw ExceptionInInitializerError()
    }

    override fun onAuthStateChange(response_id: Int, message: String?) {
        // Not relevant to MainPresenter Activity
    }

    interface View {
        fun launchLoginActivity()

        fun setupActivity(email: String)

        fun updateActionBar(type: Constants.Fragment)

        fun updateNavigationDrawer(type: Constants.Fragment)

        fun launchFragment(type: Constants.Fragment, toBackStack: Boolean)

        fun updateProfilePicture(image: Bitmap)

        fun updateUsersName(name: String)

        fun popBackStack()

        fun getProfilePicture(uid: String): Bitmap?

        fun saveProfilePicture(image: Bitmap, uid: String)
    }
}