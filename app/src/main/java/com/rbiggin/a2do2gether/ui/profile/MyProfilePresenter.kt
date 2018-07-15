package com.rbiggin.a2do2gether.ui.profile

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.util.Log
import com.rbiggin.a2do2gether.repository.*
import com.rbiggin.a2do2gether.ui.base.BasePresenter
import com.rbiggin.a2do2gether.utils.Constants
import com.rbiggin.a2do2gether.utils.Utilities
import javax.inject.Inject

/**
 * Presenter responsible for the My Profile Fragment
 */
class MyProfilePresenter @Inject constructor(private val userRepo: IntUserRepositoryFragment,
                                             private val constants: Constants,
                                             utilities: Utilities,
                                             sharedPreferences: SharedPreferences) :
                                             BasePresenter<MyProfileFragment>(sharedPreferences, utilities, constants),
                                             IntMyProfilePresenter,
                                             IntAuthRepositoryListener,
                                             IntUserProfileRepositoryListener{
    /** Logging Tag */
    var tag: String? = null

    /**
     * View Will Show
     */
    override fun onViewWillShow() {
        userRepo.onSetFragment(this)
        mFragment?.onUpdateDetails(userRepo.geUsersFirstName(), userRepo.getUsersSecondName(), userRepo.getUsersNickname())

        tag = constants.PROFILE_PRESENTER_TAG
    }

    /**
     * View Will Hide
     */
    override fun onViewWillHide() {
        super.onViewWillHide()
        userRepo.onDetachFragment()
    }

    /**
     * Profile Picture Button Pressed
     */
    override fun onProfilePictureButtonPressed() {
        mFragment?.onLaunchImageCropActivity()
    }

    /**
     * Update User Details Button Pressed
     */
    override fun onUpdateUserDetailsButtonPressed(fName: String, sName: String, nName: String) {
        if (fName.trim().isEmpty() || sName.trim().isEmpty() || nName.trim().isEmpty()){
            mFragment?.onDisplayDialogMessage(constants.ERROR_PROFILE_DETAILS_BLANK, null)
        } else if (nName.trim().length < constants.NUMBER_OF_CHARACTERS_IN_NICKNAME) {
            mFragment?.onDisplayDialogMessage(constants.ERROR_NICKNAME_STRUCTURE_ERROR, null)
        }
        else {
            userRepo.writeNewUserDetails(fName.trim(), sName.trim(), nName.trim())
            mFragment?.onDisplayDialogMessage(constants.DB_WRITE_USER_DETAILS_SUCCESSFUL, null)
        }
    }

    /**
     * Write User Details Result
     */
    override fun onWriteUserDetailsResult(success: Boolean, errorMessage: String?) {
        if (success){
            Log.d(tag, "User's profile details have been successfully written to database.")
        } else {
            Log.d(tag, "User's profile details have been unsuccessfully written to database.")
            mFragment?.onDisplayDialogMessage(constants.DB_WRITE_USER_DETAILS_UNSUCCESSFUL, errorMessage)
        }
    }

    /**
     * Upload Profile Picture
     */
    override fun uploadProfilePicture(image: Bitmap?, cropActivityErrorOccurred: Boolean, errorMessage: Exception?) {
        if (cropActivityErrorOccurred){
            mFragment?.onDisplayDialogMessage(constants.ERROR_IMAGE_CROPPING_ACTIVITY_EXCEPTION,
                    errorMessage?.message)
        } else if (mFragment?.hasNetworkConnection()!!){
            getUid()?.let {
                userRepo.uploadNewProfilePicture(image, it)
            } ?: throw ExceptionInInitializerError()
        } else {
            mFragment?.onDisplayDialogMessage(constants.ERROR_PROFILE_PICTURE_NO_NETWORK_CONNECTION,
                    null)
        }
    }

    /**
     * Picture Upload Progress Update
     */
    override fun onPictureUploadProgressUpdate(progress: Int) {
        mFragment?.onUpdateProgressBar(true, progress)
    }

    /**
     * Picture Upload Result
     */
    override fun onPictureUploadResult(success: Boolean, errorMessage: String?) {
        mFragment?.onUpdateProgressBar(false, 0)
        if (success){
            mFragment?.onDisplayDialogMessage(constants.STORAGE_PROFILE_UPLOAD_SUCCESSFUL, null)
            userRepo.getProfilePictureForMainActivity()
        } else {
            mFragment?.onDisplayDialogMessage(constants.STORAGE_PROFILE_UPLOAD_SUCCESSFUL, errorMessage)
        }
    }

    /**
     * User Details Changed
     */
    override fun onUserDetailsChanged(firstName: String, secondName: String, nickname: String) {
        mFragment?.onUpdateDetails(firstName, secondName, nickname)
    }

    /**
     * Auth State Change
     */
    override fun onAuthStateChange(response_id: Int, message: String?) {
        // not currently used within MyProfilePresenter
    }
}