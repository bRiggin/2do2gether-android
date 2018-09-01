package com.rbiggin.a2do2gether.ui.base

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v4.app.Fragment
import com.rbiggin.a2do2gether.utils.Constants

open class BaseFragment : Fragment(){

    var mFragmentId: Int? = null

    var mContext: Context? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mFragmentId = it.getInt(Constants.FRAGMENT_ID)
        }
    }

    fun hasNetworkConnection(): Boolean {
        val cm = mContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }
}