package com.rbiggin.a2do2gether.ui.main

import com.rbiggin.a2do2gether.utils.Constants

interface IntMainPresenter {
    fun setView(mainActivity: IntMainActivity)

    fun onViewWillShow(email: String)

    fun onViewWillHide()

    fun onNavDrawerItemSelected(type: Constants.Fragment, backStackCount: Int)

    fun onBackPressed()
}