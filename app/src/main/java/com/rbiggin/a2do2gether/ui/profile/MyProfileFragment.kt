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

class MyProfileFragment : BaseFragment(), MyProfilePresenter.View {

    @Inject lateinit var presenter: MyProfilePresenter

    @Inject lateinit var utilities: Utilities

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_my_profile, container, false)
    }

    override fun onAttach(context: Context?) {
        (context?.applicationContext as MyApplication).daggerComponent.inject(this)
        super.onAttach(context)

        presenter.onViewAttached(this)
    }

    override fun onResume() {
        super.onResume()

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

    override fun onPause() {
        super.onPause()
        presenter.onViewWillHide()

    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onViewDetached()
    }

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

    override fun onDisplayDialogMessage(message_id: Int, message: String?) {
        var dialogMessage = getString(R.string.error_unknown)
        message?.let{ dialogMessage = message }

        val messageString: String = when (message_id){
            Constants.ERROR_IMAGE_CROPPING_ACTIVITY_EXCEPTION -> { dialogMessage }
            Constants.STORAGE_PROFILE_UPLOAD_SUCCESSFUL -> {getString(R.string.upload_profile_picture_success)}
            Constants.STORAGE_PROFILE_UPLOAD_UNSUCCESSFUL -> {dialogMessage}
            Constants.DB_WRITE_USER_DETAILS_SUCCESSFUL -> {getString(R.string.upload_profile_details_success)}
            Constants.DB_WRITE_USER_DETAILS_UNSUCCESSFUL -> {dialogMessage}
            Constants.ERROR_PROFILE_PICTURE_NO_NETWORK_CONNECTION -> {getString(R.string.upload_profile_no_network)}
            Constants.ERROR_PROFILE_DETAILS_BLANK -> {getString(R.string.error_missing_field)}
            Constants.ERROR_NICKNAME_STRUCTURE_ERROR -> {getString(R.string.nickname_error, Constants.NUMBER_OF_CHARACTERS_IN_NICKNAME)}
            else -> {
                throw IllegalArgumentException("MyConnectionsFragment, onDisplayDialogMessage: An " +
                        "unknown error has been handed to this function. Error ID: $message_id")
            }
        }
        utilities.showOKDialog(activity as Context, getString(R.string.app_name), messageString!!)
    }

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

    override fun onLaunchImageCropActivity() {
        CropImage.activity().
                setGuidelines(CropImageView.Guidelines.ON).
                setCropShape(CropImageView.CropShape.OVAL).
                setAspectRatio(1,1).
                start(context!!, this)
    }

    companion object {
        fun newInstance(id: Int): MyProfileFragment {
            val fragment = MyProfileFragment()
            val args = Bundle()
            args.putInt(Constants.FRAGMENT_ID, id)
            fragment.arguments = args
            return fragment
        }
    }
}
