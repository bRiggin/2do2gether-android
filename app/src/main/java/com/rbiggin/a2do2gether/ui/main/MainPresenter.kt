package com.rbiggin.a2do2gether.ui.main

import android.graphics.Bitmap
import com.rbiggin.a2do2gether.repository.*
import com.rbiggin.a2do2gether.utils.Constants
import javax.inject.Inject

/**
 * Main Presenter
 */
class MainPresenter @Inject constructor(private val authRepo: IntAuthRepository,
                                        private val userRepo: IntUserRepositoryActivity,
                                        private val connectionsRepository: IntConnectionsRepository) :
                                        IntMainPresenter,
                                        IntAuthRepositoryListener,
                                        IntUserRepositoryOnChangeListener {

    /** Main Activity */
    private var mActivity: IntMainActivity? = null

    /** Current Fragment */
    private var currentFragment: Constants.Fragment? = null

    override fun setView(mainActivity: IntMainActivity) {
        mActivity = mainActivity

    }

    override fun onViewWillShow(email: String) {
        var profilePicture: Bitmap?
        mActivity?.setupActivity(email)
        mActivity?.updateActionBar(Constants.Fragment.TODO)
        mActivity?.launchFragment(Constants.Fragment.TODO, false)
        currentFragment = Constants.Fragment.TODO

        setupRepositories()

        authRepo.userId()?.let {
            profilePicture = mActivity?.getProfilePicture(it)

            if (profilePicture == null){
                userRepo.getProfilePicture()
            } else {
                mActivity?.updateProfilePicture(profilePicture as Bitmap)
            }

        } ?: throw ExceptionInInitializerError("MainPresenter, onViewWillSHow: authentication " +
                "repository return null user uid and therefore unable to setup user repository.")
        userRepo.getUsersName()
    }

    /**
     * Navigation Drawer Item Selected
     *
     * Fragments are not kept on back stack, behaviour has been decided to be:
     * - First back press takes user back to 2do lists.
     * - Second back pressed kills apps.
     */
    override fun onNavDrawerItemSelected(type: Constants.Fragment, backStackCount: Int) {
        if (isCurrentFragmentDifferent(type)){
            mActivity?.updateActionBar(type)
            if (backStackCount == 1 && type != currentFragment){
                mActivity?.popBackStack()
            }
            when(type){
                Constants.Fragment.TODO -> {
                    mActivity?.popBackStack()
                }
                Constants.Fragment.CHECKLIST, Constants.Fragment.MY_CONNECTIONS,
                Constants.Fragment.MY_PROFILE, Constants.Fragment.SETTINGS -> {
                    mActivity?.launchFragment(type, true)
                }
                else -> {
                    throw IllegalArgumentException("Main Presenter, navDrawerItemSelected: has been supplied with an illegal input.")
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

    override fun onBackPressed() {
        if (currentFragment != Constants.Fragment.TODO){
            mActivity?.updateActionBar(Constants.Fragment.TODO)
            mActivity?.updateNavigationDrawer(Constants.Fragment.TODO)
            currentFragment = Constants.Fragment.TODO
        }
    }

    private fun isCurrentFragmentDifferent(selectedType: Constants.Fragment): Boolean {
        return currentFragment != selectedType
    }

    override fun onViewWillHide() {
        mActivity = null
    }

    override fun onUserDetailsChanged(userName: String) {
        mActivity?.updateUsersName(userName)
    }

    override fun onProfilePictureChanged(image: Bitmap) {
        mActivity?.updateProfilePicture(image)
        authRepo.userId()?.let { mActivity?.saveProfilePicture(image, it) } ?: throw ExceptionInInitializerError()
    }

    override fun onAuthStateChange(response_id: Int, message: String?) {
        // Not relevant to Main Activity
    }
}