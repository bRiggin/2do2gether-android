package com.rbiggin.a2do2gether.ui.base

import android.content.SharedPreferences
import android.support.annotation.CallSuper
import com.rbiggin.a2do2gether.utils.Constants
import com.rbiggin.a2do2gether.utils.Utilities
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BasePresenter<T> (private val sharedPreferences: SharedPreferences,
                                 private val utilities: Utilities) {

    private val attachedDisposables: CompositeDisposable = CompositeDisposable()
    private val visibleDisposables: CompositeDisposable = CompositeDisposable()

    var view: T? = null

    @CallSuper
    open fun onViewAttached(view: T) {
        if (isViewAttached()){
            throw IllegalStateException("Fragment " + this.view + " is already attached.")
        }
        this.view = view
    }

    open fun onViewWillShow() {}

    @CallSuper
    open fun onViewWillHide() {
        visibleDisposables.clear()
    }

    @CallSuper
    open fun onViewDetached() {
        if (!isViewAttached()){
            throw IllegalStateException("Fragment is already detached.")
        }
        view = null

        attachedDisposables.clear()
    }

    @CallSuper
    fun disposeOnViewWillHide(disposable: Disposable) {
        visibleDisposables.add(disposable)
    }

    @CallSuper
    fun disposeOnViewWillDetach(disposable: Disposable) {
        attachedDisposables.add(disposable)
    }

    fun isViewAttached(): Boolean {
        return view != null
    }

    fun getUid(): String?{
        val encodedUid = sharedPreferences.getString(utilities.encode(Constants.SP_UID), null)
        return encodedUid?.let { utilities.decode(encodedUid) }
    }
}