package com.rbiggin.a2do2gether.ui.todo

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.animation.ValueAnimator
import com.rbiggin.a2do2gether.R
import kotlinx.android.synthetic.main.seek_bar.view.*


class CustomSeekBar(context: Context, attributeSet: AttributeSet):
        ConstraintLayout(context, attributeSet),
        View.OnTouchListener, ViewTreeObserver.OnGlobalLayoutListener{
    private var mListener: Listener? = null

    private var mThumbPosition: Float = 0.0f

    private var mWidth: Int? = null

    private var mHeight: Int? = null

    private var mThumbImage: ImageView? = null

    private val animationDuration: Long = 500

    init{
        setOnTouchListener(this)

        val viewTreeObserver = viewTreeObserver
        if (viewTreeObserver.isAlive) {
            viewTreeObserver.addOnGlobalLayoutListener(this)
        }
    }

    fun initialiseSeekBar(initialPosition: Position, listener: Listener){
        mThumbImage = findViewById(R.id.thumb)
        mListener = listener
    }

    fun setSeekBarPosition(position: Position){
        positionThumb(position)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        Log.i("whatever", "x value: ${event?.x}")
        val width = mWidth ?: throw IllegalStateException()


        event?.x?.let {
            mThumbPosition = it/width
            if (mThumbPosition > 1.0f){
                mThumbPosition = 1.0f
            } else if (mThumbPosition < 0.0f){
                mThumbPosition = 0.0f
            }

            val constraintSet = ConstraintSet()
            constraintSet.clone(this)
            constraintSet.setHorizontalBias(R.id.thumb, mThumbPosition)
            constraintSet.applyTo(this)

            if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL){
                positionThumb(mThumbPosition)
            }
        }
        return true
    }

    private fun positionThumb(position: Float) {
        val newPosition = when {
            position < 0.33 -> {
                updateListener(Position.TODO)
                0.1675f
            }
            0.33 < position && position <= 0.66 -> {
                updateListener(Position.IMPORTANT)
                0.5f
            }
            position > 0.66 -> {
                updateListener(Position.CRITICAL)
                0.8325f
            } else -> {
                0f
                /**throw IllegalArgumentException("SeekBar thumb has been positioned outside of valid bounds " +
                        "(0.0 - 1.0): set position = $position")*/
            }
        }
        animateThumb(position, newPosition)
    }

    private fun positionThumb(position: Position) {
        val value = when(position) {
            Position.TODO -> { 0.0f }
            Position.IMPORTANT -> { 0.5f }
            Position.CRITICAL -> { 1.0f }
        }

        animateThumb(mThumbPosition, value)
        updateListener(position)
    }

    private fun updateListener(position: Position){
        mListener?.onSeekBarUpdated(position)
    }

    private fun animateThumb(currentPosition: Float, newPosition: Float){
        val thumbAnimator = ValueAnimator.ofFloat(currentPosition, newPosition)
        thumbAnimator.addUpdateListener {
            val constraintSet = ConstraintSet()
            constraintSet.clone(this)
            constraintSet.setHorizontalBias(R.id.thumb, it.animatedValue as Float)
            constraintSet.applyTo(this)
            mThumbPosition = it.animatedValue as Float
        }
        thumbAnimator.duration = animationDuration
        thumbAnimator.start()
    }

    override fun onGlobalLayout() {
        viewTreeObserver.removeOnGlobalLayoutListener(this)
        mWidth = width
        mHeight = height
    }

    enum class Position{
        TODO,
        IMPORTANT,
        CRITICAL
    }

    interface Listener {
        fun onSeekBarUpdated(position: Position)
    }
}