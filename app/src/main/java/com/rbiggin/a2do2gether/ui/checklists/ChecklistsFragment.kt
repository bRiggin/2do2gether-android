package com.rbiggin.a2do2gether.ui.checklists

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rbiggin.a2do2gether.R
import com.rbiggin.a2do2gether.application.MyApplication
import com.rbiggin.a2do2gether.ui.base.BaseFragment
import com.rbiggin.a2do2gether.ui.settings.ChecklistsPresenter
import javax.inject.Inject

/**
 * Allow user to edit/update their private checklists
 */
class ChecklistsFragment : BaseFragment() {
    /** Injected Interface instance */
    @Inject lateinit var presenter: ChecklistsPresenter

    /**
     * Companion object to provide access to newInstance.
     */
    companion object {
        /**
         * @param id ID handed to Fragment.
         * @return A new instance of fragment: AddressFragment.
         */
        fun newInstance(id: Int): ChecklistsFragment {
            val fragment = ChecklistsFragment()
            val args = Bundle()
            args.putInt("FRAGMENT_ID", id)
            fragment.arguments = args
            return fragment
        }
    }

    /**
     * onAttach
     */
    override fun onAttach(context: Context?) {
        (context?.applicationContext as MyApplication).daggerComponent.inject(this)
        super.onAttach(context)
    }

    /**
     * onCreateView
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_checklists, container, false)
    }

    /**
     *
     */
    override fun onDestroy() {
        super.onDestroy()
        presenter.onViewWillHide()

    }
}
