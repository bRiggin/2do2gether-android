package com.rbiggin.a2do2gether.ui.profile

import com.rbiggin.a2do2gether.ui.base.IntBaseFragment

/**
 * Insert class/object/interface/file description...
 */
interface IntMyProfileFragment: IntBaseFragment {
    fun onLaunchImageCropActivity()

    fun onUpdateProgressBar(isVisible: Boolean, progress: Int)

    fun onUpdateDetails(firstName: String, secondName: String, nickname: String)
}