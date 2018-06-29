package com.rbiggin.a2do2gether.ui.login.fragments

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import com.rbiggin.a2do2gether.application.MyApplication
import com.rbiggin.a2do2gether.ui.login.IntLoginPresenter
import javax.inject.Inject

/**
 * Master fragment, all login fragments are subclasses for this fragment.
 */
open class MasterFragment : Fragment() {

    /** Injected Presenter instance */
    @Inject lateinit var presenter: IntLoginPresenter

    /** The ID number of fragment */
    var mFragmentNumber: Int? = null

    /** The context of the attached Activity */
    private var mContext: Context? = null

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
        (mContext?.applicationContext as MyApplication).daggerComponent.inject(this)
        arguments?.let {
            mFragmentNumber = it.getInt("FRAGMENT_ID")
        }
    }
}