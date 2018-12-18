package com.rbiggin.a2do2gether.ui.todo

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rbiggin.a2do2gether.R
import com.rbiggin.a2do2gether.application.MyApplication
import com.rbiggin.a2do2gether.model.ToDoList
import com.rbiggin.a2do2gether.model.ToDoListItem
import com.rbiggin.a2do2gether.ui.base.BaseFragment
import com.rbiggin.a2do2gether.utils.Constants
import kotlinx.android.synthetic.main.fragment_to_do_list.*
import javax.inject.Inject

class ToDoListFragment : BaseFragment(), ToDoListPresenter.View, ToDoListAdapter.Listener {

    @Inject
    lateinit var presenter: ToDoListPresenter

    private var toDoListItems: ArrayList<Pair<String, ToDoListItem>> = ArrayList()

    private lateinit var layoutManager: LinearLayoutManager

    override fun onAttach(context: Context) {
        (context.applicationContext as MyApplication).daggerComponent.inject(this)
        super.onAttach(context)
        presenter.onViewAttached(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_to_do_list, container, false)

    override fun onStart() {
        super.onStart()
        layoutManager = LinearLayoutManager(mContext)
        toDoListRv.layoutManager = layoutManager
        toDoListRv.adapter = ToDoListAdapter(toDoListItems, this)
    }

    override fun onResume() {
        super.onResume()
        presenter.onViewWillShow()
        mFragmentId?.let { presenter.onIdSupplied(it) }
    }

    override fun onPause() {
        super.onPause()
        presenter.onViewWillHide()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onViewDetached()
    }

    override fun onToDoListUpdate(toDoList: ToDoList) {
        toDoListItems.clear()
        //toDoListItems.addAll(toDoList.items)
        toDoListRv.adapter?.notifyDataSetChanged()
    }

    override fun itemDeleted(itemId: String) {
        mFragmentId?.let { presenter.onItemDeleted(itemId, it) }
    }

    override fun onDisplayDialogMessage(message_id: Int, message: String?) {}

    companion object {
        fun newInstance(viewId: String): ToDoListFragment {
            val fragment = ToDoListFragment()
            val args = Bundle()
            args.putString(Constants.FRAGMENT_ID, viewId)
            fragment.arguments = args
            return fragment
        }
    }
}