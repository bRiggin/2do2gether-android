package com.rbiggin.a2do2gether.ui.login.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import com.rbiggin.a2do2gether.application.MyApplication
import com.rbiggin.a2do2gether.ui.login.LoginPresenter
import javax.inject.Inject

open class MasterFragment : Fragment() {

    @Inject lateinit var presenter: LoginPresenter

    var mFragmentNumber: Int? = null

    private var mContext: Context? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (mContext?.applicationContext as MyApplication).daggerComponent.inject(this)
        arguments?.let {
            mFragmentNumber = it.getInt("FRAGMENT_ID")
        }
    }
}