package com.rbiggin.a2do2gether.ui.profile

import android.graphics.Bitmap
import android.util.Log
import com.bumptech.glide.Glide
import com.rbiggin.a2do2gether.repository.*
import com.rbiggin.a2do2gether.ui.base.BasePresenter
import com.rbiggin.a2do2gether.utils.Constants
import timber.log.Timber
import javax.inject.Inject

class MyProfilePresenter @Inject constructor(private val userRepo: IntUserRepositoryFragment,
                                             private val uidProvider: UidProvider) :
        BasePresenter<MyProfileFragment>(),
        IntAuthRepositoryListener,
        UserProfileRepository.FragmentListener {

    override fun onViewWillShow() {
        userRepo.onSetFragment(this)
        view?.onUpdateDetails(userRepo.geUsersFirstName(), userRepo.getUsersSecondName(), userRepo.getUsersNickname())
    }

    override fun onViewWillHide() {
        super.onViewWillHide()
        userRepo.onDetachFragment()
    }

    fun onProfilePictureButtonPressed() {
        view?.onLaunchImageCropActivity()
    }

    fun onUpdateUserDetailsButtonPressed(fName: String, sName: String, nName: String) {
        if (fName.trim().isEmpty() || sName.trim().isEmpty() || nName.trim().isEmpty()) {
            view?.onDisplayDialogMessage(Constants.ERROR_PROFILE_DETAILS_BLANK, null)
        } else if (nName.trim().length < Constants.NUMBER_OF_CHARACTERS_IN_NICKNAME ||
                nName.trim().contains(" ")) {
            view?.onDisplayDialogMessage(Constants.ERROR_NICKNAME_STRUCTURE_ERROR, null)
        } else {
            userRepo.writeNewUserDetails(fName.trim(), sName.trim(), nName.trim())
            view?.onDisplayDialogMessage(Constants.DB_WRITE_USER_DETAILS_SUCCESSFUL, null)
        }
    }

    override fun onWriteUserDetailsResult(success: Boolean, errorMessage: String?) {
        if (success) {
            Timber.d("User's profile details have been successfully written to database.")
        } else {
            Timber.d("User's profile details have been unsuccessfully written to database.")
            view?.onDisplayDialogMessage(Constants.DB_WRITE_USER_DETAILS_UNSUCCESSFUL, errorMessage)
        }
    }

    fun uploadProfilePicture(image: Bitmap?, cropActivityErrorOccurred: Boolean, errorMessage: Exception?) {
        if (cropActivityErrorOccurred) {
            view?.onDisplayDialogMessage(Constants.ERROR_IMAGE_CROPPING_ACTIVITY_EXCEPTION,
                    errorMessage?.message)
        } else if (view?.hasNetworkConnection()!!) {
            uidProvider.getUid()?.let {
                userRepo.uploadNewProfilePicture(image, it)
            } ?: throw ExceptionInInitializerError()
        } else {
            view?.onDisplayDialogMessage(Constants.ERROR_PROFILE_PICTURE_NO_NETWORK_CONNECTION,
                    null)
        }
    }

    override fun onPictureUploadProgressUpdate(progress: Int) {
        view?.onUpdateProgressBar(true, progress)
    }

    override fun onPictureUploadResult(success: Boolean, errorMessage: String?) {
        view?.onUpdateProgressBar(false, 0)
        if (success) {
            view?.onDisplayDialogMessage(Constants.STORAGE_PROFILE_UPLOAD_SUCCESSFUL, null)
            userRepo.getProfilePictureForMainActivity()
        } else {
            view?.onDisplayDialogMessage(Constants.STORAGE_PROFILE_UPLOAD_SUCCESSFUL, errorMessage)
        }
    }

    override fun onUserDetailsChanged(firstName: String, secondName: String, nickname: String) {
        view?.onUpdateDetails(firstName, secondName, nickname)
    }

    override fun onAuthStateChange(response_id: Int, message: String?) {
        // not currently used within MyProfilePresenter
    }

    interface View : BasePresenter.View {
        fun onLaunchImageCropActivity()

        fun onUpdateProgressBar(isVisible: Boolean, progress: Int)

        fun onUpdateDetails(firstName: String, secondName: String, nickname: String)
    }
}