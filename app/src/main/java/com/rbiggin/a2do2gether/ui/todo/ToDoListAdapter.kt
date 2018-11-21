package com.rbiggin.a2do2gether.ui.todo

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.rbiggin.a2do2gether.R
import com.rbiggin.a2do2gether.utils.inflate


class ToDoListAdapter(private val to  : ArrayList<Pair<String, String>>,
                      private val listener: Listener)
    : RecyclerView.Adapter<ToDoListAdapter.ItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val inflatedView = parent.inflate(R.layout.row_item_checklist, false)
        return ItemHolder(inflatedView, listener)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.setText(itemValues[position].second)
        holder.itemKey = itemValues[position].first
    }

    override fun getItemCount(): Int {
        return itemValues.size
    }

    class ItemHolder(private val view: View,
                     private val listener: Listener) : RecyclerView.ViewHolder(view), View.OnClickListener {

        var itemKey: String? = null

        init {
            view.checklistItemDeleteBtn.setOnClickListener(this)
        }

        fun setText(text: String) {
            view.checklistItemDescription.text = text
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