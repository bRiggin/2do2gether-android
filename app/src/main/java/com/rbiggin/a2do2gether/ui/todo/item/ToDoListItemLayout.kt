package com.rbiggin.a2do2gether.ui.todo.item

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.github.florent37.expansionpanel.ExpansionLayout
import com.rbiggin.a2do2gether.R

class ToDoListItemLayout(layoutId: Int, context: Context, attrs: AttributeSet? = null): ExpansionLayout(context, attrs) {

    init{
        id = layoutId
        View.inflate(context, R.layout.row_item_to_do_list_layout, this as ViewGroup)
    }
}