package com.rbiggin.a2do2gether.ui.todo

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.AnimatedVectorDrawable
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.storage.StorageReference
import com.rbiggin.a2do2gether.R
import com.rbiggin.a2do2gether.model.ToDoListItem
import com.rbiggin.a2do2gether.ui.todo.item.ToDoListItemLayout
import com.rbiggin.a2do2gether.utils.inflate
import kotlinx.android.synthetic.main.row_item_to_do_list_layout.view.*
import timber.log.Timber

class ToDoListAdapter(private val context: Context,
                      private val storageReference: StorageReference,
                      private val toDoListItems: ArrayList<Pair<String, ToDoListItem>>,
                      private val cachedUiData: HashMap<String, Pair<Boolean, Boolean>>,
                      private val expansionListener: ToDoListItemLayout.Listener,
                      private val listener: Listener)
    : RecyclerView.Adapter<ToDoListAdapter.ItemHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val inflatedView = parent.inflate(R.layout.row_item_to_do_list_layout, false)
        return ItemHolder(context, inflatedView, expansionListener,  listener)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.setViewId(toDoListItems[position].first)

        cachedUiData[toDoListItems[position].first]?.let {
            when {
                it.second && toDoListItems[position].second.status ->
                    holder.setStatus(true, true)
                !it.second && toDoListItems[position].second.status ->
                    holder.setStatus(true, false)
                else -> holder.setStatus(false, true)
            }
            holder.setViewExpansion(it.first)
        } ?: run {
            holder.setStatus(toDoListItems[position].second.status, false)
            holder.setViewExpansion(false)
        }

        holder.setDescription(toDoListItems[position].second.description)

        val ref = storageReference.child("profile_pictures/${toDoListItems[position].second.creator}.jpg")
        holder.setItemImage(ref)

        holder.setDateCreated(toDoListItems[position].second.dateCreated)

        toDoListItems[position].second.completedBy?.let {
            holder.setCompletedBy(it)
        } ?: run {
            holder.hideCompletedByLayout()
        }

        holder.setPriority(toDoListItems[position].second.priority)
    }

    override fun getItemCount(): Int = toDoListItems.size

    class ItemHolder(private val context: Context,
                     private val view: View,
                     expansionListener: ToDoListItemLayout.Listener,
                     private val listener: Listener) : RecyclerView.ViewHolder(view), View.OnClickListener {

        private var itemKey: String? = null
        private var currentPriority: ToDoListItem.Priority? = null

        init {
            view.listItemHeader.detailsView = view.listItemDetailsLayout
            view.listItemDetailsLayout.listener = expansionListener
            view.toDoPriorityBtn.setOnClickListener(this)
            view.importantPriorityBtn.setOnClickListener(this)
            view.criticalPriorityBtn.setOnClickListener(this)
            view.toDoListItemCompletedBtn.setOnClickListener(this)
        }

        fun setViewId(id: String){
            itemKey = id
            view.listItemDetailsLayout.itemId = id
        }

        fun setViewExpansion(expanded: Boolean) {
            view.listItemDetailsLayout.initialStaticExpansionState = expanded
        }

        fun setDescription(text: String) {
            view.toDoListItemDescription.text = text
        }

        fun setItemImage(ref: StorageReference) {
            Glide.with(context)
                    .using(FirebaseImageLoader())
                    .load(ref)
                    .error(R.drawable.profile_default)
                    .into(view.toDoListItemImage)
        }

        fun setDateCreated(date: String) {
            view.toDoListItemCreatedTextView.text = date
        }

        fun setCompletedBy(name: String) {
            view.completedByLayout.visibility = View.VISIBLE
            view.toDoListItemCompletedByTextView.text = name
        }

        fun hideCompletedByLayout() {
            view.completedByLayout.visibility = View.GONE
        }

        fun setStatus(status: Boolean, setStatically: Boolean) {
            itemKey?.let { uid ->
                when (status) {
                    true -> {
                        view.toDoListItemDescription.paintFlags =
                                view.toDoListItemDescription.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                        view.toDoListItemCompletedBtn.apply {
                            if (setStatically) {
                                setImageDrawable(context.getDrawable(R.drawable.tick_static))
                            } else {
                                setImageDrawable(context.getDrawable(R.drawable.tick_animation))
                            }
                            (drawable as? AnimatedVectorDrawable)?.start()
                        }
                        listener.onItemUiTickStatusChanged(uid, true)
                    }
                    false -> {
                        view.toDoListItemDescription.paintFlags =
                                view.toDoListItemDescription.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                        listener.onItemUiTickStatusChanged(uid, false)
                    }
                }
            }
        }

        fun setPriority(priority: ToDoListItem.Priority) {
            currentPriority = priority
            val white = context.resources.getColor(R.color.white)
            var colour = 0
            resetPriorityTextStyles()
            when (priority) {
                ToDoListItem.Priority.TODO -> {
                    colour = context.resources.getColor(R.color.blue_300)
                    view.toDoPriorityBtn.apply {
                        setTypeface(view.toDoPriorityBtn.typeface, Typeface.BOLD_ITALIC)
                        setTextColor(white)
                    }
                }
                ToDoListItem.Priority.IMPORTANT -> {
                    colour = context.resources.getColor(R.color.blue_700)
                    view.importantPriorityBtn.apply {
                        setTypeface(view.importantPriorityBtn.typeface, Typeface.BOLD_ITALIC)
                        setTextColor(white)
                    }
                }
                ToDoListItem.Priority.CRITICAL -> {
                    colour = context.resources.getColor(R.color.blue_900)
                    view.criticalPriorityBtn.apply {
                        setTypeface(view.criticalPriorityBtn.typeface, Typeface.BOLD_ITALIC)
                        setTextColor(white)
                    }
                }
            }
            view.toDoListItemPriorityIndicator.setBackgroundColor(colour)
        }

        private fun resetPriorityTextStyles() {
            val colour = context.resources.getColor(R.color.grey_300)
            view.toDoPriorityBtn.apply {
                setTypeface(view.toDoPriorityBtn.typeface, Typeface.NORMAL)
                setTextColor(colour)
            }
            view.importantPriorityBtn.apply {
                setTypeface(view.toDoPriorityBtn.typeface, Typeface.NORMAL)
                setTextColor(colour)
            }
            view.criticalPriorityBtn.apply {
                setTypeface(view.toDoPriorityBtn.typeface, Typeface.NORMAL)
                setTextColor(colour)
            }
        }

        override fun onClick(view: View?) {
            itemKey?.let { uid ->
                when (view?.id) {
                    R.id.toDoPriorityBtn ->
                        if (currentPriority != ToDoListItem.Priority.TODO)
                            listener.onItemPriorityChanged(uid, ToDoListItem.Priority.TODO)
                    R.id.importantPriorityBtn ->
                        if (currentPriority != ToDoListItem.Priority.IMPORTANT)
                            listener.onItemPriorityChanged(uid, ToDoListItem.Priority.IMPORTANT)
                    R.id.criticalPriorityBtn ->
                        if (currentPriority != ToDoListItem.Priority.CRITICAL)
                            listener.onItemPriorityChanged(uid, ToDoListItem.Priority.CRITICAL)
                    R.id.toDoListItemCompletedBtn -> listener.onItemCompleted(uid)
                    else -> Timber.i("")
                }
            }
        }
    }

    interface Listener {
        fun onItemDeleted(itemId: String)
        fun onItemCompleted(itemId: String)
        fun onItemPriorityChanged(itemId: String, priority: ToDoListItem.Priority)
        fun onItemUiTickStatusChanged(itemId: String, status: Boolean)
    }
}