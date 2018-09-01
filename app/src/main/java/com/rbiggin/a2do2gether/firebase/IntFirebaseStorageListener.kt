package com.rbiggin.a2do2gether.firebase

interface IntFirebaseStorageListener {
    fun onPictureUploadResult(success: Boolean, errorMessage: String?)

    fun onPictureUploadProgress(progress: Int)


    fun onPictureDownloadResult(path: String?, success: Boolean, errorMessage: String?)
}