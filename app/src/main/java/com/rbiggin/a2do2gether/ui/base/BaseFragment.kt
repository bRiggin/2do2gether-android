package com.rbiggin.a2do2gether.ui.base

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.graphics.Bitmap
import android.content.ContextWrapper
import android.graphics.BitmapFactory
import java.io.*


/**
 * Base class to be subclassed by all fragments
 */
open class BaseFragment : Fragment(){

    /** The ID number of fragment */
    var mFragmentId: Int? = null

    /** The context of the attached Activity */
    var mContext: Context? = null

    /**
     * onAttach
     */
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context
    }

    /**
     * onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mFragmentId = it.getInt("FRAGMENT_ID")
        }
    }

    /**
     * Has Network Connection
     */
    fun hasNetworkConnection(): Boolean {
        val cm = mContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }
}