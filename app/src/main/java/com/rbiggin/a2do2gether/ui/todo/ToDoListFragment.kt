package com.rbiggin.a2do2gether.ui.todo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rbiggin.a2do2gether.R
import com.rbiggin.a2do2gether.application.MyApplication
import com.rbiggin.a2do2gether.ui.base.BaseFragment
import javax.inject.Inject

/**
 * Insert class/object/interface/file description...
 */
class ToDoListFragment : BaseFragment() {

    /** Injected Interface instance */
    @Inject
    lateinit var presenter: ToDoListPresenter

    /**
     * Companion object to provide access to newInstance.
     */
    companion object {
        /**
         * Create a new instance of this fragment using the provided parameters.
         *
         * @param id ID handed to Fragment.
         * @return A new instance of fragment: AddressFragment.
         */
        fun newInstance(id: Int): ToDoListFragment {
            val fragment = ToDoListFragment()
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
        return inflater.inflate(R.layout.fragment_to_do_list, container, false)
    }

    /**
     *
     */
    override fun onDestroy() {
        super.onDestroy()
        presenter.onViewWillHide()

    }
}