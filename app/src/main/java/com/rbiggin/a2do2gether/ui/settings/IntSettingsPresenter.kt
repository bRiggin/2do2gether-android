package com.rbiggin.a2do2gether.ui.settings

import com.rbiggin.a2do2gether.ui.base.IntBasePresenter

/**
 * Defines specific functions to be implemented be settings presenter.
 */
interface IntSettingsPresenter : IntBasePresenter<SettingsFragment> {
    fun logout()
}