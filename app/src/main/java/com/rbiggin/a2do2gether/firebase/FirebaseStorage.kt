package com.rbiggin.a2do2gether.firebase

import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import java.io.File

object FirebaseStorage : IntFirebaseStorage{

    private var uploadTask: UploadTask? = null

    private var downloadTask: StorageTask<FileDownloadTask.TaskSnapshot>? = null

    override fun downlaodProfilePicture(reference: StorageReference?, template: File, listener: IntFirebaseStorageListener) {
        downloadTask = reference?.getFile(template)?.addOnSuccessListener{
            listener.onPictureDownloadResult(template.path, true, null)
        }?.addOnFailureListener{
            listener.onPictureDownloadResult(null, false, it.message)
        }
    }

    override fun uploadProfilePicture(storageRef: StorageReference, data: ByteArray, uid: String,
                             listener: IntFirebaseStorageListener){
        val mSpecificRef = storageRef.child("profile_pictures/$uid.jpg")

        uploadTask = mSpecificRef.putBytes(data)
        uploadTask?.addOnFailureListener {
            listener.onPictureUploadResult(false, it.message)
        }
        uploadTask?.addOnSuccessListener{
            listener.onPictureUploadResult(true, null)
        }
        uploadTask?.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
            listener.onPictureUploadProgress(progress)
        }
    }

    override fun cancelTasks(){
        uploadTask?.cancel()
        downloadTask?.cancel()
    }
}