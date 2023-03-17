package com.example.textview_detect

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent

class SlideButton(context: Context?, attrs: AttributeSet?) :
    androidx.appcompat.widget.AppCompatSeekBar(context!!, attrs) {
    private var thumb: Drawable? = null
    private var listener: SlideButtonListener? = null
    override fun setThumb(thumb: Drawable?) {
        super.setThumb(thumb)
        this.thumb = thumb
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.getAction() === MotionEvent.ACTION_DOWN) {
            if (thumb?.getBounds()?.contains(event.getX().toInt(), event.getY().toInt()) == true) {
                super.onTouchEvent(event)
            } else return false
        } else if (event.getAction() === MotionEvent.ACTION_UP) {
            if (getProgress() > 0){
                handleSlide(event)
                Log.d("points",event.x.toString())
                setProgress(getProgress())
            }
        } else super.onTouchEvent(event)
        return true
    }

    private fun handleSlide(event: MotionEvent) {
        listener!!.handleSlide(event)
    }

    fun setSlideButtonListener(listener: SlideButtonListener?) {
        this.listener = listener
    }
}

interface SlideButtonListener {
    fun handleSlide(event: MotionEvent)
}