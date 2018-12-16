package com.rbiggin.a2do2gether.ui.login.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.rbiggin.a2do2gether.application.MyApplication
import com.rbiggin.a2do2gether.ui.login.LoginPresenter
import com.rbiggin.a2do2gether.utils.Constants
import javax.inject.Inject

open class MasterFragment : Fragment() {

    @Inject lateinit var presenter: LoginPresenter

    var mFragmentNumber: String? = null

    private var mContext: Context? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (mContext?.applicationContext as MyApplication).daggerComponent.inject(this)
        arguments?.let {
            mFragmentNumber = it.getString(Constants.FRAGMENT_ID)
        }
    }
}