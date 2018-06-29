package com.rbiggin.a2do2gether.ui.base

import android.content.SharedPreferences
import com.rbiggin.a2do2gether.utils.Constants
import com.rbiggin.a2do2gether.utils.Utilities

/**
 * Base class to be subclassed by all fragment presenters
 */
abstract class BasePresenter<T> (private val sharedPreferences: SharedPreferences,
                                 private val utilities: Utilities,
                                 private val constants: Constants): IntBasePresenter<T> {

    /** Instance Fragment Interface */
    var mFragment: T? = null

    /**
     * Provides instance of relevant fragment.
     */
    override fun setView(fragment: T) {
        mFragment = fragment
    }

    override fun onViewWillHide() {
        mFragment = null
    }

    fun getUid(): String?{
        val encodedUid = sharedPreferences.getString(utilities.encode(constants.SP_UID), null)
        return encodedUid?.let { utilities.decode(encodedUid) }
    }
}