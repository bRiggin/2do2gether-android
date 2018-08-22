package com.rbiggin.a2do2gether.ui.main

import android.graphics.Bitmap
import android.util.Log
import com.rbiggin.a2do2gether.repository.*
import com.rbiggin.a2do2gether.utils.Constants
import javax.inject.Inject

class MainPresenter @Inject constructor(private val authRepo: AuthRepository,
                                        private val userRepo: UserProfileRepository,
                                        private val connectionsRepository: ConnectionsRepository) :
                                        AuthRepository.Listener,
                                        UserProfileRepository.ActivityListener {

    private var mActivity: MainPresenter.View? = null

    private var currentFragment: Constants.Fragment? = null

    private val tag = Constants.MAIN_PRESENTER_TAG

    fun onViewAttached(view: MainActivity, fragment: String?) {
        Log.w(tag, "onViewAttach: view = $view, fragment = $fragment")
        mActivity = view

        if (!authRepo.isUserLoggedIn()) {
            mActivity?.launchLoginActivity()
        } else {
            setupRepositories()

            var profilePicture: Bitmap?
            authRepo.getEmail()?.let {
                mActivity?.setupActivity(it)
            } ?: throw ExceptionInInitializerError("MainPresenter, onViewAttached: authentication " +
                    "repository return null user uid and therefore unable to setup user repository.")

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

            mActivity?.updateActionBar(Constants.Fragment.TODO)
            mActivity?.launchFragment(Constants.Fragment.TODO, false)
            currentFragment = Constants.Fragment.TODO

            loadFragmentOnLoad(fragment)
        }
    }

    fun onViewDetached() {
        mActivity = null
    }

    private fun loadFragmentOnLoad(fragment: String?){
        fragment?.let {
            val fragmentToLoad: Constants.Fragment = when(it) {
                Constants.Fragment.TODO.toString() -> {
                    Constants.Fragment.TODO
                }
                Constants.Fragment.CHECKLIST.toString() -> {
                    Constants.Fragment.CHECKLIST
                }
                Constants.Fragment.MY_CONNECTIONS.toString() -> {
                    Constants.Fragment.MY_CONNECTIONS
                }
                Constants.Fragment.MY_PROFILE.toString() -> {
                    Constants.Fragment.MY_PROFILE
                }
                Constants.Fragment.SETTINGS.toString() -> {
                    Constants.Fragment.SETTINGS
                } else -> {
                    //todo add data
                    throw IllegalArgumentException()
                }
            }
            onNavDrawerItemSelected(fragmentToLoad, 0)
        }
    }

    fun reloadMenuButtons(){
        currentFragment?.let {
            mActivity?.updateActionBar(it)
        }
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
            }
            currentFragment = type
        }
    }

    private fun setupRepositories() {
        authRepo.setListener(this)
        userRepo.initialise()
        connectionsRepository.initialise()
        userRepo.setActivity(this)
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