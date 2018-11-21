package com.rbiggin.a2do2gether.ui.todo.item

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.github.florent37.expansionpanel.ExpansionHeader
import com.rbiggin.a2do2gether.R

class ToDoListItemHeader(layout: ToDoListItemLayout, context: Context, attrs: AttributeSet? = null)
    : ExpansionHeader(context, attrs) {

    init{
        isToggleOnClick = true
        View.inflate(context, R.layout.row_item_to_do_list_header, this as ViewGroup)
        setExpansionLayout(layout)
    }
}