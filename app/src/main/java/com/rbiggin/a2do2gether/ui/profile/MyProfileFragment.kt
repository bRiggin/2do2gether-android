package com.rbiggin.a2do2gether.ui.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rbiggin.a2do2gether.R
import com.rbiggin.a2do2gether.application.MyApplication
import com.rbiggin.a2do2gether.ui.base.BaseFragment
import com.rbiggin.a2do2gether.utils.Constants
import com.rbiggin.a2do2gether.utils.Utilities
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import javax.inject.Inject

import kotlinx.android.synthetic.main.fragment_my_profile.*

/**
 * Fragment to allow the user to update/edit their public facing profile.
 */
class MyProfileFragment : BaseFragment(), IntMyProfileFragment {

    /** Injected Presenter instance */
    @Inject lateinit var presenter: IntMyProfilePresenter

    /** injected instance of Constants. */
    @Inject lateinit var utilities: Utilities

    /** injected instance of Constants. */
    @Inject lateinit var constants: Constants

    /**
     * Companion object to provide access to newInstance.
     */
    companion object {
        /**
         * @param id ID handed to Fragment.
         * @return A new instance of fragment: AddressFragment.
         */
        fun newInstance(id: Int): MyProfileFragment {
            val fragment = MyProfileFragment()
            val args = Bundle()
            args.putInt("FRAGMENT_ID", id)
            fragment.arguments = args
            return fragment
        }
    }

    /**
     * onCreateView
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_profile, container, false)
    }

    /**
     * onAttach
     */
    override fun onAttach(context: Context?) {
        (context?.applicationContext as MyApplication).daggerComponent.inject(this)
        super.onAttach(context)
    }

    /**
     * onResume
     */
    override fun onResume() {
        super.onResume()

        presenter.onViewAttached(this)

        presenter.onViewWillShow()

        profilePictureBtn.setOnClickListener {
            presenter.onProfilePictureButtonPressed()
        }

        profileSubmitBtn.setOnClickListener {
            presenter.onUpdateUserDetailsButtonPressed(
                    myProfileFirstNameEt.text.toString(),
                    myProfileSecondNameEt.text.toString(),
                    myProfileNicknameEt.text.toString()
            )
        }
    }

    /**
     *
     */
    override fun onDestroy() {
        super.onDestroy()
        presenter.onViewWillHide()

    }

    /**
     *
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                val bitmap = MediaStore.Images.Media.getBitmap(context!!.contentResolver, result.uri)
                presenter.uploadProfilePicture(bitmap, false, null)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                presenter.uploadProfilePicture(null, true, result.error)
            }
        }
    }

    /**
     *
     */
    override fun onDisplayDialogMessage(message_id: Int, message: String?) {
        var dialogMessage = getString(R.string.error_unknown)
        message?.let{ dialogMessage = message }

        val messageString: String = when (message_id){
            constants.ERROR_IMAGE_CROPPING_ACTIVITY_EXCEPTION -> { dialogMessage }
            constants.STORAGE_PROFILE_UPLOAD_SUCCESSFUL -> {getString(R.string.upload_profile_picture_success)}
            constants.STORAGE_PROFILE_UPLOAD_UNSUCCESSFUL -> {dialogMessage}
            constants.DB_WRITE_USER_DETAILS_SUCCESSFUL -> {getString(R.string.upload_profile_details_success)}
            constants.DB_WRITE_USER_DETAILS_UNSUCCESSFUL -> {dialogMessage}
            constants.ERROR_PROFILE_PICTURE_NO_NETWORK_CONNECTION -> {getString(R.string.upload_profile_no_network)}
            constants.ERROR_PROFILE_DETAILS_BLANK -> {getString(R.string.error_missing_field)}
            constants.ERROR_NICKNAME_STRUCTURE_ERROR -> {getString(R.string.nickname_error, constants.NUMBER_OF_CHARACTERS_IN_NICKNAME)}
            else -> {
                throw IllegalArgumentException("MyConnectionsFragment, onDisplayDialogMessage: An " +
                        "unknown error has been handed to this function. Error ID: $message_id")
            }
        }
        utilities.showOKDialog(activity as Context, getString(R.string.app_name), messageString!!)
    }

    /**
     *
     */
    override fun onUpdateProgressBar(isVisible: Boolean, progress: Int) {
        myProfileProgressBar.progress = progress
        if (isVisible) {
            myProfileProgressBar.visibility = View.VISIBLE
        } else {
            myProfileProgressBar.visibility = View.GONE
        }
    }

    override fun onUpdateDetails(firstName: String, secondName: String, nickname: String) {
        myProfileFirstNameEt.setText(firstName)
        myProfileSecondNameEt.setText(secondName)
        myProfileNicknameEt.setText(nickname)
    }

    /**
     *
     */
    override fun onLaunchImageCropActivity() {
        CropImage.activity().
                setGuidelines(CropImageView.Guidelines.ON).
                setCropShape(CropImageView.CropShape.OVAL).
                setAspectRatio(1,1).
                start(context!!, this)
    }
}
