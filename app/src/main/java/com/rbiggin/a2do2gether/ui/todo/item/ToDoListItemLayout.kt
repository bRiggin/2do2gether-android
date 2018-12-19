package com.rbiggin.a2do2gether.ui.todo.item

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout

class ToDoListItemLayout(context: Context, attrs: AttributeSet? = null) :
        FrameLayout(context, attrs), ToDoListItemHeader.ToDoListItemDetailsView {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet, ignore: Int) : this(context, attrs)

    private var expanded = false
    private var viewHeight = 0

    private val valueAnimator: ValueAnimator = ValueAnimator.ofFloat()

    init {
        valueAnimator.addUpdateListener {
            setHeight(it.animatedValue as Float)
        }
    }

    override fun addView(child: View?, params: ViewGroup.LayoutParams?) {
        super.addView(child, params)
        onChildViewAdded()
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        super.addView(child, index, params)
        onChildViewAdded()
    }

    private fun onChildViewAdded() {
        if (childCount == 1){
            getChildAt(0).viewTreeObserver?.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    viewTreeObserver.removeOnPreDrawListener(this)
                    viewHeight = height
                    setHeight(0f)
                    return false
                }
            })
        }
    }

    private fun setHeight(height: Float) {
        val layoutParams = layoutParams
        if (layoutParams != null) {
            layoutParams.height = height.toInt()
            setLayoutParams(layoutParams)
        }
    }

    private fun expand(){
        valueAnimator.setFloatValues(0f, 1f * viewHeight)
        expanded = true
        valueAnimator.start()
    }

    private fun collapse(){
        valueAnimator.setFloatValues(1f * viewHeight, 0f)
        expanded = false
        valueAnimator.start()
    }

    override fun onExpandToggle() {
        when {
            expanded -> collapse()
            else -> expand()
        }
    }
}