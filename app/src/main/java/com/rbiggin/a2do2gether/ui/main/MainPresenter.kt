package com.rbiggin.a2do2gether.ui.main

import android.graphics.Bitmap
import com.rbiggin.a2do2gether.repository.*
import com.rbiggin.a2do2gether.utils.Constants
import javax.inject.Inject

/**
 * Main Presenter
 */
class MainPresenter @Inject constructor(private val constants: Constants,
                                        private val authRepo: IntAuthRepository,
                                        private val userRepo: IntUserRepositoryActivity) :
                                        IntMainPresenter,
                                        IntAuthRepositoryListener,
                                        IntUserRepositoryOnChangeListener {

    /** Main Activity */
    private var mActivity: IntMainActivity? = null

    /** Current Fragment */
    private var currentFragment: Constants.FragmentType? = null

    /**
     * Set View
     */
    override fun setView(mainActivity: IntMainActivity) {
        mActivity = mainActivity

    }

    /**
     * View Will Show
     */
    override fun onViewWillShow(email: String) {
        var profilePicture: Bitmap?
        mActivity?.setupActivity(email)
        mActivity?.updateActionBar(constants.fragmentTypeToDo())
        mActivity?.launchFragment(constants.fragmentTypeToDo(), false)
        currentFragment = constants.fragmentTypeToDo()

        userRepo.onSetActivity(this)

        authRepo.setup(this)
        authRepo.userId()?.let {
            userRepo.setup(it)
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
    override fun onNavDrawerItemSelected(type: Constants.FragmentType, backStackCount: Int) {
        if (isCurrentFragmentDifferent(type)){
            mActivity?.updateActionBar(type)
            if (backStackCount == 1 && type != currentFragment){
                mActivity?.popBackStack()
            }
            when(type){
                constants.fragmentTypeToDo() -> {
                    mActivity?.popBackStack()
                }
                constants.fragmentTypeChecklists(), constants.fragmentTypeConnections(),
                constants.fragmentTypeProfile(), constants.fragmentTypeSettings() -> {
                    mActivity?.launchFragment(type, true)
                }
                else -> {
                    throw IllegalArgumentException("Main Presenter, navDrawerItemSelected: has been supplied with an illegal input.")
                }
            }
            currentFragment = type
        }
    }

    /**
     * Back Pressed
     */
    override fun onBackPressed() {
        if (currentFragment != constants.fragmentTypeToDo()){
            mActivity?.updateActionBar(constants.fragmentTypeToDo())
            mActivity?.updateNavigationDrawer(constants.fragmentTypeToDo())
            currentFragment = constants.fragmentTypeToDo()
        }
    }

    /**
     * Is Current Fragment Different?
     */
    private fun isCurrentFragmentDifferent(selectedType: Constants.FragmentType): Boolean {
        return currentFragment != selectedType
    }

    /**
     * View will Hide
     */
    override fun onViewWillHide() {
        mActivity = null
    }

    /**
     * User Details Changed
     */
    override fun onUserDetailsChanged(userName: String) {
        mActivity?.updateUsersName(userName)
    }

    /**
     * Profile Picture Changed
     */
    override fun onProfilePictureChanged(image: Bitmap) {
        mActivity?.updateProfilePicture(image)
        authRepo.userId()?.let { mActivity?.saveProfilePicture(image, it) } ?: throw ExceptionInInitializerError()
    }

    /**
     * Auth Command Result
     */
    override fun onAuthCommandResult(response_id: Int, message: String?) {
        // Not relevant to Main Activity
    }

    /**
     * Auth State Change
     */
    override fun onAuthStateChange(response_id: Int, message: String?) {
        // Not relevant to Main Activity
    }
}