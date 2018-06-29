package com.rbiggin.a2do2gether.firebase

import com.google.firebase.storage.StorageReference
import java.io.File

interface IntFirebaseStorage {
    fun uploadProfilePicture(storageRef: StorageReference, data: ByteArray, uid: String,
                             listener: IntFirebaseStorageListener)

    fun downlaodProfilePicture(reference: StorageReference?, template: File,
                               listener: IntFirebaseStorageListener)

    fun cancelTasks()
}