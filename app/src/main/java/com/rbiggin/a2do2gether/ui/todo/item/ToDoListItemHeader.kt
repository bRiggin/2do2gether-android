package com.rbiggin.a2do2gether.ui.todo.item

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.github.florent37.expansionpanel.ExpansionHeader
import com.rbiggin.a2do2gether.R

class ToDoListItemHeader(context: Context, attrs: AttributeSet? = null)
    : FrameLayout(context, attrs) {

    var detailsView: ToDoListItemDetailsView? = null

    init {
        setOnClickListener{
            detailsView?.onExpandToggle()
        }
    }

    interface ToDoListItemDetailsView {
        fun onExpandToggle()
    }
}