package com.rbiggin.a2do2gether.ui.base

import io.reactivex.disposables.Disposable

/**
 * Functions to be implemented by all fragment's presenters.
 */
interface IntBasePresenter<T> {
    fun onViewAttached(fragment: T)

    fun onViewWillShow()

    fun onViewWillHide()

    fun onViewDetached()

    fun disposeOnViewWillHide(disposable: Disposable)

    fun disposeOnViewWillDetach(disposable: Disposable)
}