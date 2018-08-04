package com.rbiggin.a2do2gether.ui.main

import android.graphics.Bitmap
import com.rbiggin.a2do2gether.utils.Constants

/**
 * Insert class/object/interface/file description...
 */
interface IntMainActivity {
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