package com.rbiggin.a2do2gether.ui.main

import android.content.Context
import android.graphics.Bitmap
import android.widget.ImageView
import com.rbiggin.a2do2gether.utils.Constants

/**
 * Insert class/object/interface/file description...
 */
interface IntMainActivity {
    fun setupActivity(email: String)

    fun updateActionBar(type: Constants.FragmentType)

    fun updateNavigationDrawer(type: Constants.FragmentType)

    fun launchFragment(type: Constants.FragmentType, toBackStack: Boolean)

    fun updateProfilePicture(image: Bitmap)

    fun updateUsersName(name: String)

    fun popBackStack()

    fun getProfilePicture(uid: String): Bitmap?

    fun saveProfilePicture(image: Bitmap, uid: String)
}