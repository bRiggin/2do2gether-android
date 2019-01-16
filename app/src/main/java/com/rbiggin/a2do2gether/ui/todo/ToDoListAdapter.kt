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
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import kotlinx.android.synthetic.main.row_item_to_do_list_layout.view.*
import timber.log.Timber
import java.lang.IllegalStateException

class ToDoListAdapter(private val context: Context,
                      private val storageReference: StorageReference,
                      private val toDoListItems: ArrayList<String>,
                      private var itemObservables: HashMap<String, Observable<ToDoListItem>>,
                      private val expansionListener: ToDoListItemLayout.Listener,
                      private val listener: Listener)
    : RecyclerView.Adapter<ToDoListAdapter.ItemHolder>() {

    private val onCachedUiSateChanged: BehaviorSubject<HashMap<String, ToDoListPresenter.CachedItem>> = BehaviorSubject.create()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val inflatedView = parent.inflate(R.layout.row_item_to_do_list_layout, false)
        return ItemHolder(context, inflatedView, onCachedUiSateChanged, expansionListener, listener)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.setViewId(toDoListItems[position])
        holder.itemPosition = position
        itemObservables[toDoListItems[position]]?.let {
            holder.onObservableChanged(it)
        } ?: run {
            throw IllegalStateException("no observable available for to do list item: ${toDoListItems[position]}")
        }
    }

    fun updateCachedUiData(newData: HashMap<String, ToDoListPresenter.CachedItem>) {
        onCachedUiSateChanged.onNext(newData)
    }

    fun updateObservables(newObservables: HashMap<String, Observable<ToDoListItem>>) {
        itemObservables = newObservables
    }

    private fun updateCompletedBy(item: ToDoListItem, holder: ItemHolder) {
        item.completedBy?.let {
            holder.setCompletedBy(it)
        } ?: run {
            holder.hideCompletedByLayout()
        }
    }

    private fun updateItemImage(item: ToDoListItem, holder: ItemHolder) {
        val ref = storageReference.child("profile_pictures/${item.creator}.jpg")
        holder.setItemImage(ref)
    }

    override fun getItemCount(): Int = toDoListItems.size

    class ItemHolder(private val context: Context,
                     private val view: View,
                     private val cachedUiData: Observable<HashMap<String, ToDoListPresenter.CachedItem>>,
                     expansionListener: ToDoListItemLayout.Listener,
                     private val listener: Listener) : RecyclerView.ViewHolder(view), View.OnClickListener {

        var itemPosition: Int? = null
        private var itemKey: String? = null
        private var currentPriority: ToDoListItem.Priority? = null
        private var currentU

        private var itemDisposable: Disposable? = null
        private var uiDisposable: Disposable? = null

        init {
            view.listItemHeader.detailsView = view.listItemDetailsLayout
            view.listItemDetailsLayout.listener = expansionListener
            view.toDoPriorityBtn.setOnClickListener(this)
            view.importantPriorityBtn.setOnClickListener(this)
            view.criticalPriorityBtn.setOnClickListener(this)
            view.toDoListItemCompletedBtn.setOnClickListener(this)
            view.deleteToDoListItemBtn.setOnClickListener(this)
        }

        fun onObservableChanged(observable: Observable<ToDoListItem>){
            itemDisposable?.dispose()
            itemDisposable = observable.distinctUntilChanged().subscribe { item ->
                setDescription(item.description)
                setDateCreated(item.dateCreated)
                setPriority(item.priority)

                itemPosition?.let { updateCompletionState(item.status, it) }
                updateItemImage(item, holder)
                updateCompletedBy(item, holder)
            }
        }

        fun setViewId(id: String) {
            itemKey = id
            view.listItemDetailsLayout.itemId = id

            uiDisposable?.dispose()
            uiDisposable = cachedUiData
                    .map {
                        it[id]?.let { newData ->
                            newData
                        } ?: run {
                            throw IllegalStateException()
                        }
                    }
                    .distinctUntilChanged()
                    .subscribe {

                    }
        }

        fun setViewExpansion(expanded: Boolean) {
            view.listItemDetailsLayout.initialStaticExpansionState = expanded
        }

        private fun setDescription(text: String) {
            view.toDoListItemDescription.text = text
        }

        fun setItemImage(ref: StorageReference) {
            Glide.with(context)
                    .using(FirebaseImageLoader())
                    .load(ref)
                    .error(R.drawable.profile_default)
                    .into(view.toDoListItemImage)
        }

        private fun setDateCreated(date: String) {
            view.toDoListItemCreatedTextView.text = date
        }

        fun setCompletedBy(name: String) {
            view.completedByLayout.visibility = View.VISIBLE
            view.toDoListItemCompletedByTextView.text = name
        }

        fun hideCompletedByLayout() {
            view.completedByLayout.visibility = View.GONE
        }

        private fun updateCompletionState(state: Boolean, position: Int) {
            cachedUiData[toDoListItems[position]]?.let { cachedItemUiState ->
                when {
                    cachedItemUiState.completed && state -> holder.setStatus(true, true)
                    !cachedItemUiState.completed && state -> holder.setStatus(true, false)
                    cachedItemUiState.completed && !state -> holder.setStatus(false, false)
                    !cachedItemUiState.completed && !state -> holder.setStatus(false, true)
                }
                holder.setViewExpansion(cachedItemUiState.expanded)
            } ?: run {
                holder.setStatus(state, false)
                holder.setViewExpansion(false)
            }
        }

        private fun setStatus(status: Boolean, setStatically: Boolean) {
            itemKey?.let { uid ->
                view.toDoListItemDescription.paintFlags = when (status) {
                    true -> view.toDoListItemDescription.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    false -> view.toDoListItemDescription.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }
                view.toDoListItemCompletedBtn.setImageDrawable(when (status) {
                    true -> when (setStatically) {
                        true -> context.getDrawable(R.drawable.tick_static)
                        false -> context.getDrawable(R.drawable.tick_forward_animation)
                    }
                    false -> when (setStatically) {
                        true -> context.getDrawable(R.drawable.icon_blank)
                        false -> context.getDrawable(R.drawable.tick_reverse_animation)
                    }
                })
                (view.toDoListItemCompletedBtn.drawable as? AnimatedVectorDrawable)?.start()
                listener.onItemUiTickStatusChanged(uid, status)
            }
        }

        private fun setPriority(priority: ToDoListItem.Priority) {
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
                    R.id.deleteToDoListItemBtn -> listener.onItemDeleted(uid)
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