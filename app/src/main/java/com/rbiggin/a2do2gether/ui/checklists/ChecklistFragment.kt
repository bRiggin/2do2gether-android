package com.rbiggin.a2do2gether.ui.checklists

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.rbiggin.a2do2gether.R
import com.rbiggin.a2do2gether.application.MyApplication
import com.rbiggin.a2do2gether.model.Checklist
import com.rbiggin.a2do2gether.ui.base.BaseFragment
import com.rbiggin.a2do2gether.utils.Constants
import kotlinx.android.synthetic.main.fragment_checklist.*
import javax.inject.Inject

class ChecklistFragment : BaseFragment(), ChecklistPresenter.View, ChecklistAdapter.Listener {

    @Inject
    lateinit var presenter: ChecklistPresenter

    private var checklistItems: ArrayList<Pair<String, String>> = ArrayList()

    private lateinit var mLayoutManager: LinearLayoutManager

    override fun onAttach(context: Context?) {
        (context?.applicationContext as MyApplication).daggerComponent.inject(this)
        super.onAttach(context)
        presenter.onViewAttached(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_checklist, container, false)
    }

    override fun onStart() {
        super.onStart()

        mLayoutManager = LinearLayoutManager(mContext)
        checklistRv.layoutManager = mLayoutManager
        checklistRv.adapter = ChecklistAdapter(checklistItems, this)
    }

    override fun onResume() {
        super.onResume()
        presenter.onViewWillShow()
        mFragmentId?.let {
            presenter.onIdSupplied(it)
        }
    }

    override fun onPause() {
        super.onPause()
        presenter.onViewWillHide()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onViewDetached()
    }

    override fun onDisplayDialogMessage(message_id: Int, message: String?) {
        //todo
    }

    override fun onChecklistUpdate(checklist: Checklist) {
        checklistTitle.text = checklist.title
        checklistItems.clear()
        checklistItems.addAll(checklist.items)
        checklistRv.adapter.notifyDataSetChanged()
    }

    override fun itemDeleted(itemId: String) {
        mFragmentId?.let { presenter.onItemDeleted(itemId, it) }

    }

    companion object {

        fun newInstance(viewId: String): ChecklistFragment {
            val fragment = ChecklistFragment()
            val args = Bundle()
            args.putString(Constants.FRAGMENT_ID, viewId)
            fragment.arguments = args
            return fragment
        }
    }
}