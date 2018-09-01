package com.rbiggin.a2do2gether.ui.todo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rbiggin.a2do2gether.R
import com.rbiggin.a2do2gether.application.MyApplication
import com.rbiggin.a2do2gether.ui.base.BaseFragment
import com.rbiggin.a2do2gether.utils.Constants
import javax.inject.Inject

class ToDoListFragment : BaseFragment(), ToDoListPresenter.View {

    @Inject
    lateinit var presenter: ToDoListPresenter

    companion object {
        fun newInstance(id: String): ToDoListFragment {
            val fragment = ToDoListFragment()
            val args = Bundle()
            args.putString(Constants.FRAGMENT_ID, id)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context?) {
        (context?.applicationContext as MyApplication).daggerComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_to_do_list, container, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onViewWillHide()
    }

    override fun onDisplayDialogMessage(message_id: Int, message: String?) {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}