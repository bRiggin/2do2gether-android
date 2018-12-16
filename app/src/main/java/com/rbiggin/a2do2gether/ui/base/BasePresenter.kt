package com.rbiggin.a2do2gether.ui.base

import androidx.annotation.CallSuper
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BasePresenter<T> {

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

    interface View {
        fun onDisplayDialogMessage(message_id: Int, message: String?)
    }
}