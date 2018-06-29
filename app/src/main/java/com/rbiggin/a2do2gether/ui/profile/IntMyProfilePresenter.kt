package com.rbiggin.a2do2gether.ui.profile

import android.graphics.Bitmap
import com.rbiggin.a2do2gether.ui.base.IntBasePresenter

/**
 * Insert class/object/interface/file description...
 */
interface IntMyProfilePresenter : IntBasePresenter<MyProfileFragment> {
    fun onUpdateUserDetailsButtonPressed(fName: String, sName: String, nName: String)

    fun onProfilePictureButtonPressed()

    fun uploadProfilePicture(image: Bitmap?, cropActivityErrorOccurred: Boolean, errorMessage: Exception?)
}