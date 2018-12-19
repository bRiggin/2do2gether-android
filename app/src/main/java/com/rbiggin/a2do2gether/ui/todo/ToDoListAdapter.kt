package com.rbiggin.a2do2gether.ui.todo

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.rbiggin.a2do2gether.R
import com.rbiggin.a2do2gether.model.ToDoListItem
import com.rbiggin.a2do2gether.utils.inflate
import kotlinx.android.synthetic.main.row_item_to_do_list_layout.view.*

class ToDoListAdapter(private val toDoListItems  : ArrayList<Pair<String, ToDoListItem>>,
                      private val listener: Listener)
    : RecyclerView.Adapter<ToDoListAdapter.ItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val inflatedView = parent.inflate(R.layout.row_item_to_do_list_layout, false)
        return ItemHolder(inflatedView, listener)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.setText(toDoListItems[position].second.description)
        holder.itemKey = toDoListItems[position].first
    }

    override fun getItemCount(): Int = toDoListItems.size

    class ItemHolder(private val view: View,
                     private val listener: Listener) : RecyclerView.ViewHolder(view), View.OnClickListener {

        var itemKey: String? = null

        init {
            view.listItemHeader.detailsView = view.listItemDetailsLayout
        }

        fun setText(text: String) {
            //view.checklistItemDescription.text = text
        }

        override fun onClick(view: View?) {
            itemKey?.let {
                listener.itemDeleted(it)
            }
        }
    }

    interface Listener {
        fun itemDeleted(itemId: String)
    }
}