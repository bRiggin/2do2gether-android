package com.rbiggin.a2do2gether.firebase

/**
 * Defines callbacks from Firebase storage API calls
 */
interface IntFirebaseStorageListener {
    fun onPictureUploadResult(success: Boolean, errorMessage: String?)

    fun onPictureUploadProgress(progress: Int)


    fun onPictureDownloadResult(path: String?, success: Boolean, errorMessage: String?)
}