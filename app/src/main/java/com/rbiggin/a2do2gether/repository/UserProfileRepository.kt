package com.rbiggin.a2do2gether.repository

import android.graphics.Bitmap
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.rbiggin.a2do2gether.model.UserDetails
import com.rbiggin.a2do2gether.utils.Constants
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import java.io.File
import android.graphics.BitmapFactory
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.rbiggin.a2do2gether.firebase.*
import com.rbiggin.a2do2gether.utils.Utilities

class UserProfileRepository @Inject constructor(private val databaseApi: IntFirebaseDatabase,
                                                private val storageApi: IntFirebaseStorage,
                                                private val utilities: Utilities,
                                                private val uidProvider: UidProvider) :
                                                IntUserRepositoryActivity,
                                                IntUserRepositoryFragment,
                                                IntFirebaseDatabaseListener,
                                                IntFirebaseStorageListener{

    private var mFragmentListener: FragmentListener? = null

    private var mActivityListener: ActivityListener? = null

    private var user: UserDetails? = null

    private var mDatabase: DatabaseReference? = null

    private var mStorage: FirebaseStorage? = null

    private var mStorageRef: StorageReference? = null

    private val tag = Constants.USER_REPOSITORY_TAG

    override fun initialise(){
        val uid = uidProvider.getUid()
        uid?.let {
            user = UserDetails("", "", "", uid, false)
            mDatabase = com.google.firebase.database.FirebaseDatabase.getInstance().reference
            mDatabase?.let {
                databaseApi.doRead(it, uid, this, Constants.DatabaseApi.READ_USER_DETAILS)
            } ?: throw ExceptionInInitializerError()

            mStorage = FirebaseStorage.getInstance()
            mStorageRef = mStorage?.reference
        } ?: throw NullPointerException("Uid provided by UidProvider has returned null")
    }

    override fun setActivity(listener: ActivityListener) {
        mActivityListener = listener
    }

    override fun onSetFragment(listener: FragmentListener) {
        mFragmentListener = listener
    }

    override fun onDetachActivity() {
        mActivityListener = null
        //todo destroy everything here, DESTROY!!!!!!
    }

    override fun onDetachFragment() {
        mFragmentListener = null
        storageApi.cancelTasks()
    }

    override fun onDatabaseResult(type: Constants.DatabaseApi, data: DataSnapshot?, success: Boolean, message: String?) {
        when (type){
            Constants.DatabaseApi.WRITE_USER_DETAILS -> {
                if (success){
                    mFragmentListener?.onWriteUserDetailsResult(true, null)
                } else {
                    mFragmentListener?.onWriteUserDetailsResult(false, message)
                }
            }
            Constants.DatabaseApi.READ_USER_DETAILS -> {
                data?.apply { handleUserDetailsResult(data) }
            } else -> {
                //todo throw a cheeky exception, or not?
            }
        }
    }

    private fun handleUserDetailsResult(dataSnapshot: DataSnapshot){
        dataSnapshot.value?.apply {
            val userDetails = hashMapOf<String, String>()
            for (item in dataSnapshot.children) {
                item.key?.let { userDetails[it] = item.value.toString() }
            }
            user?.firstName = userDetails["first_name"] ?: ""
            user?.secondName = userDetails["second_name"] ?: ""
            user?.nickname = userDetails["nickname"] ?: ""
            user?.discoverable = userDetails["discoverable"]?.toBoolean() ?: false
            mFragmentListener?.onUserDetailsChanged(user?.firstName!!, user?.secondName!!, user?.nickname!!)
            mActivityListener?.onUserDetailsChanged("${user?.firstName} ${user?.secondName}")
        }
    }

    override fun writeNewUserDetails(firstName: Any, secondName: Any, nickname: Any) {
        val newDetails = hashMapOf("first_name" to firstName,
                                   "second_name" to secondName,
                                   "nickname" to nickname,
                                   "discoverable" to "true")
        val path = "/user_profile/${user?.uid}"

        mDatabase?.let {
            databaseApi.doWrite(it, path, newDetails, this, Constants.DatabaseApi.WRITE_USER_DETAILS)
        } ?: throw ExceptionInInitializerError()
    }

    override fun uploadNewProfilePicture(image: Bitmap?, uid: String) {
        val resizedBitmap = Bitmap.createScaledBitmap(image, 150, 150, false)
        val baos = ByteArrayOutputStream()
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        mStorageRef?.let {
            storageApi.uploadProfilePicture(it, data, uid, this)
        } ?: throw ExceptionInInitializerError("UserProfileRepository, uploadNewProfilePicture:" +
                " mStorageRef has not yet been initialised.")
    }

    override fun onPictureUploadResult(success: Boolean, errorMessage: String?) {
        mFragmentListener?.onPictureUploadResult(success, errorMessage)
    }

    override fun onPictureDownloadResult(path: String?, success: Boolean, errorMessage: String?) {
        if (success){
            var newProfilePicture: Bitmap? = null
            path?.let { newProfilePicture = getLocalImage(path) }
            newProfilePicture?.let { mActivityListener?.onProfilePictureChanged(it) }
        } else {
            Log.d(tag, "Profile picture download failed with message: $errorMessage" )
        }
    }

    override fun onPictureUploadProgress(progress: Int) {
        mFragmentListener?.onPictureUploadProgressUpdate(progress)
    }

    override fun getProfilePicture() {
        val reference = mStorageRef?.child("profile_pictures/${user?.uid}.jpg")
        val localFile = File.createTempFile("${user?.uid}", "jpg")
        val profilePicture = getLocalImage(localFile.path)
        if (profilePicture == null){
            storageApi.downlaodProfilePicture(reference, localFile, this)
        } else {
            mActivityListener?.onProfilePictureChanged(profilePicture)
        }
    }

    override fun getProfilePictureForMainActivity() {
        getProfilePicture()
    }

    private fun getLocalImage(path: String): Bitmap?{
        val imageFile = File(path)
        if (imageFile.exists()) {
            return BitmapFactory.decodeFile(imageFile.absolutePath)
        }
        return null
    }

    override fun getUsersName() {
        mDatabase?.let {
            val path = "${Constants.FB_USER_PROFILE}/${user?.uid}"
            databaseApi.doRead(it, path, this, Constants.DatabaseApi.READ_USER_DETAILS)
        } ?: throw ExceptionInInitializerError()
    }

    override fun isUserDiscoverable(): Boolean {
        return user?.discoverable ?: false
    }

    override fun geUsersFirstName(): String {
        return user?.firstName ?: ""
    }

    override fun getUsersSecondName(): String {
        return user?.secondName ?: ""
    }

    override fun getUsersNickname(): String {
        return user?.nickname ?: ""
    }

    interface FragmentListener {
        fun onUserDetailsChanged(firstName: String, secondName: String, nickname: String)

        fun onWriteUserDetailsResult(success: Boolean, errorMessage: String?)

        fun onPictureUploadProgressUpdate(progress: Int)

        fun onPictureUploadResult(success: Boolean, errorMessage: String?)
    }

    interface ActivityListener{
        fun onUserDetailsChanged(userName: String)

        fun onProfilePictureChanged(image: Bitmap)
    }
}