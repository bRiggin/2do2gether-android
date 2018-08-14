package com.rbiggin.a2do2gether.repository

import android.graphics.Bitmap

interface IntUserRepositoryFragment {
    fun onSetFragment(listener: UserProfileRepository.FragmentListener)

    fun onDetachFragment()

    fun writeNewUserDetails (firstName: Any, secondName: Any, nickname: Any)

    fun uploadNewProfilePicture(image: Bitmap?, uid: String)

    fun geUsersFirstName(): String

    fun getUsersSecondName(): String

    fun getUsersNickname(): String

    fun isUserDiscoverable(): Boolean

    fun getProfilePictureForMainActivity()
}

interface IntUserRepositoryActivity {
    fun initialise()

    fun setActivity(listener: UserProfileRepository.ActivityListener)

    fun onDetachActivity()

    fun getProfilePicture()

    fun getUsersName()
}