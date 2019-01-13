package com.rbiggin.a2do2gether.ui.todo

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.storage.FirebaseStorage
import com.rbiggin.a2do2gether.R
import com.rbiggin.a2do2gether.application.MyApplication
import com.rbiggin.a2do2gether.model.ToDoList
import com.rbiggin.a2do2gether.model.ToDoListItem
import com.rbiggin.a2do2gether.ui.base.BaseFragment
import com.rbiggin.a2do2gether.ui.todo.item.ToDoListItemLayout
import com.rbiggin.a2do2gether.utils.Constants
import kotlinx.android.synthetic.main.fragment_to_do_list.*
import timber.log.Timber
import javax.inject.Inject

class ToDoListFragment
    : BaseFragment(), ToDoListPresenter.View, ToDoListAdapter.Listener, ToDoListItemLayout.Listener {

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

    override fun onTitleChanged(listTitle: String) {
        toDoListTitle.text = listTitle
    }

    override fun onToDoListUpdate(toDoList: ToDoList,
                                  cachedUiState: HashMap<String, ToDoListPresenter.CachedItem>) {
        val newState = presenter.sortToDoListItems(toDoList)
        when (toDoListItems.isEmpty()) {
            true -> toDoListItems.addAll(presenter.sortToDoListItems(toDoList))
            else -> updateToDoListItems(newState)
        }
        (toDoListRv.adapter as? ToDoListAdapter)?.updateCachedUiData(cachedUiState)
        toDoListRv.adapter?.notifyDataSetChanged() ?: run {
            context?.let { context ->
                toDoListRv.adapter = ToDoListAdapter(context, FirebaseStorage.getInstance().reference,
                        toDoListItems, cachedUiState, this, this)
            }
        }
    }

    private fun updateToDoListItems(newState: ArrayList<Pair<String, ToDoListItem>>) {
        for ((index, item) in newState.withIndex()) {
            if (toDoListItems.size >= index + 1) {
                if (toDoListItems[index] != newState[index])
                    toDoListItems[index] = newState[index]
            } else {
                toDoListItems[index] = item
            }
        }

    }

    override fun onProgressChanged(progress: Int) {
        toDoListProgressBar.progress = progress
        toDoListProgressTextView.text = "$progress%"
    }

    override fun onItemCompleted(itemId: String) {
        mFragmentId?.let { presenter.onItemCompleted(itemId, it) }
    }

    override fun onItemDeleted(itemId: String) {
        mFragmentId?.let {listId ->
            var indexToRemove: Int? = null
            for ((index, item) in toDoListItems.withIndex()) {
                if (item.first == itemId)
                    indexToRemove = index
            }
            indexToRemove?.let { toDoListItems.removeAt(it) }
            presenter.onItemDeleted(itemId, listId)
        }
    }

    override fun onItemPriorityChanged(itemId: String, priority: ToDoListItem.Priority) {
        mFragmentId?.let { presenter.onItemPriorityChanged(itemId, it, priority) }
    }

    override fun onItemUiTickStatusChanged(itemId: String, status: Boolean) {
        presenter.updateCachedUi(itemId, ToDoListPresenter.CachedUiType.COMPLETED, status)
    }

    override fun onItemExpanded(id: String, expanded: Boolean) {
        presenter.updateCachedUi(id, ToDoListPresenter.CachedUiType.EXPANDED, expanded)
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