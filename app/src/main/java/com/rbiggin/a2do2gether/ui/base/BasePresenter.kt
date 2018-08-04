package com.rbiggin.a2do2gether.ui.base

import android.content.SharedPreferences
import android.support.annotation.CallSuper
import com.rbiggin.a2do2gether.utils.Constants
import com.rbiggin.a2do2gether.utils.Utilities
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Base class to be subclassed by all fragment presenters
 */
abstract class BasePresenter<T> (private val sharedPreferences: SharedPreferences,
                                 private val utilities: Utilities): IntBasePresenter<T> {

    private val attachedDisposables: CompositeDisposable = CompositeDisposable()
    private val visibleDisposables: CompositeDisposable = CompositeDisposable()

    /** Instance Fragment Interface */
    var mFragment: T? = null

    /**
     * Provides instance of relevant fragment.
     */
    @CallSuper
    override fun onViewAttached(fragment: T) {
        if (isViewAttached()){
            throw IllegalStateException("Fragment " + this.mFragment + " is already attached.")
        }
        mFragment = fragment
    }

    override fun onViewWillShow() {}

    @CallSuper
    override fun onViewWillHide() {
        visibleDisposables.clear()
    }

    @CallSuper
    override fun onViewDetached() {
        if (!isViewAttached()){
            throw IllegalStateException("Fragment is already detached.")
        }
        mFragment = null

        attachedDisposables.clear()
    }

    @CallSuper
    override fun disposeOnViewWillHide(disposable: Disposable) {
        visibleDisposables.add(disposable)
    }

    override fun disposeOnViewWillDetach(disposable: Disposable) {
        attachedDisposables.add(disposable)
    }

    fun isViewAttached(): Boolean {
        return mFragment != null
    }

    fun getUid(): String?{
        val encodedUid = sharedPreferences.getString(utilities.encode(Constants.SP_UID), null)
        return encodedUid?.let { utilities.decode(encodedUid) }
    }
}