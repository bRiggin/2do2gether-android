package com.rbiggin.a2do2gether.repository

import android.graphics.Bitmap

/**
 * Defines User Repository calls from Fragment
 */
interface IntUserRepositoryFragment {
    fun onSetFragment(listener: IntUserProfileRepositoryListener)

    fun onDetachFragment()

    fun writeNewUserDetails (firstName: Any, secondName: Any, nickname: Any)

    fun uploadNewProfilePicture(image: Bitmap?, uid: String)

    fun geUsersFirstName(): String

    fun getUsersSecondName(): String

    fun getUsersNickname(): String

    fun isUserDiscoverable(): Boolean

    fun getProfilePictureForMainActivity()
}

/**
 * Defines User Repository calls from Activity
 */
interface IntUserRepositoryActivity {
    fun setActivity(listener: IntUserRepositoryOnChangeListener)

    fun setup(uid: String)

    fun onDetachActivity()

    fun getProfilePicture()

    fun getUsersName()
}

/**
 * Defines callbacks from User Repository to Activity
 */
interface IntUserProfileRepositoryListener {
    fun onUserDetailsChanged(firstName: String, secondName: String, nickname: String)

    fun onWriteUserDetailsResult(success: Boolean, errorMessage: String?)

    fun onPictureUploadProgressUpdate(progress: Int)

    fun onPictureUploadResult(success: Boolean, errorMessage: String?)
}

/**
 * Defines callbacks from User Repository to Fragment
 */
interface IntUserRepositoryOnChangeListener{
    fun onUserDetailsChanged(userName: String)

    fun onProfilePictureChanged(image: Bitmap)
}