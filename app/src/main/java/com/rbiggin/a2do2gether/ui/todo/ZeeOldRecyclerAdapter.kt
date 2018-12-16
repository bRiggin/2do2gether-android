package com.rbiggin.a2do2gether.ui.homepage

import android.content.Context
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import kotlinx.android.synthetic.main.recyclerview_item_row.*
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.rbiggin.a2do2gether.R
import com.rbiggin.a2do2gether.utils.inflate
import kotlinx.android.synthetic.main.recyclerview_item_row.view.*

/**
 * Insert class/object/interface/file description...
 *
class ZeeOldRecyclerAdapter(private val toDoLists: ArrayList<String>,
                      private val appContext: Context) : RecyclerView.Adapter<ZeeOldRecyclerAdapter.ItemHolder>() {

    override fun onBindViewHolder(holder: RecyclerAdapter.ItemHolder, position: Int) {
        val item = toDoLists[position]
        holder.bindPhoto(item)  }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val inflatedView = parent.inflate(R.layout.recyclerview_item_row, false)
        return ItemHolder(inflatedView, appContext)
    }

    override fun getItemCount(): Int {
        return toDoLists.size
    }

    /** 1 Made the class extend RecyclerView.ViewHolder, allowing it to be used as a ViewHolder for
     * the adapter. */
    class ItemHolder(v: View, appContext: Context) : RecyclerView.ViewHolder(v), View.OnClickListener {
        /** 2 Added a reference to the lifecycle of the object to allow the ViewHolder to hang on to
         * your View, so it can access the ImageView and TextView as an extension property. Kotlin
         * Android Extensions plugin adds in hidden caching functions and fields so that views are
         * not constantly queried. */
        private var View: View = v
        private var animation: Drawable = appContext.getDrawable(R.drawable.animation_tick)
        //private var photo: Photo? = null
        /** 3 Initialized the View.OnClickListener. */
        init {
            v.setOnClickListener(this)
            v.toDoItemBtn.setOnClickListener(this)

        }
        /** 4 Implemented the required method for View.OnClickListener since ViewHolders are
         * responsible for their own event handling.
         */
        override fun onClick(v: View) {
            when (v.id){
                R.id.toDoItemBtn -> {
                    View.toDoItemBtn.setImageDrawable(animation)
                    val drawable = View.toDoItemBtn.drawable as AnimatedVectorDrawable
                    drawable.start()
                }
                else -> {

                }
            }
            val context = itemView.context
            //val showPhotoIntent = Intent(context, PhotoActivity::class.java)
            //showPhotoIntent.putExtra(PHOTO_KEY, photo)
            //context.startActivity(showPhotoIntent)
        }

        fun bindPhoto(photo: String) {
            View.toDoItem.text = photo
        }

        companion object {
            /** 5 Added a key for easier reference to the particular item being used to launch your
             * RecyclerView. */
            private val PHOTO_KEY = "PHOTO"
        }
    }
}*/