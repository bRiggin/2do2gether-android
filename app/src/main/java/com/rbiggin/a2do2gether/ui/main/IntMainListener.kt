package com.rbiggin.a2do2gether.ui.main

import com.rbiggin.a2do2gether.utils.Constants

interface IntMainListener {
    fun menuItemPressed(item: Constants.MenuBarItem)

    fun onBackPressed(): Boolean
}