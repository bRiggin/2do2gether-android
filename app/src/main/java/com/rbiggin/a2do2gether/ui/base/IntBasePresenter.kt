package com.rbiggin.a2do2gether.ui.base

/**
 * Functions to be implemented by all fragment's presenters.
 */
interface IntBasePresenter<T> {
    fun setView(fragment: T)

    fun onViewWillShow()

    fun onViewWillHide()
}