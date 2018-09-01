package com.rbiggin.a2do2gether.ui.checklists

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.rbiggin.a2do2gether.R
import com.rbiggin.a2do2gether.utils.inflate
import kotlinx.android.synthetic.main.row_item_checklist.view.*

class ChecklistAdapter(private val itemValues: ArrayList<String>,
                       private val listener: Listener)
                       : RecyclerView.Adapter<ChecklistAdapter.ItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val inflatedView = parent.inflate(R.layout.row_item_checklist, false)
        return ItemHolder(inflatedView, listener)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.setText(itemValues[position])
        holder.mPosition = position
    }

    override fun getItemCount(): Int {
        return itemValues.size
    }

    class ItemHolder(private val view: View,
                     private val listener: Listener,
                     var mPosition : Int? = null) : RecyclerView.ViewHolder(view), View.OnClickListener {
        init {
            view.checklistItemDeleteBtn.setOnClickListener(this)
        }

        fun setText(text: String){
            view.checklistItemDescription.text = text
        }

        override fun onClick(view: View?) {
            mPosition?.let {
                listener.itemDeleted(it)
            }
        }
    }

    interface Listener {
        fun itemDeleted(index: Int)
    }
}