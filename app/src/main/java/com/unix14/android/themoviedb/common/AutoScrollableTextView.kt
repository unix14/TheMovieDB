package com.unix14.android.themoviedb.common

import android.content.Context
import android.graphics.Rect
import android.text.TextUtils
import android.util.AttributeSet
import android.widget.TextView

class AutoScrollableTextView : TextView {

    override fun isFocused(): Boolean {
        return true
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        ellipsize = TextUtils.TruncateAt.MARQUEE
        marqueeRepeatLimit = -1
        setSingleLine()
        setHorizontallyScrolling(true)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        ellipsize = TextUtils.TruncateAt.MARQUEE
        marqueeRepeatLimit = -1
        setSingleLine()
        setHorizontallyScrolling(true)
    }

    constructor(context: Context) : super(context) {
        ellipsize = TextUtils.TruncateAt.MARQUEE
        marqueeRepeatLimit = -1
        setSingleLine()
        setHorizontallyScrolling(true)
    }

    override fun onFocusChanged(focused: Boolean, direction: Int, previouslyFocusedRect: Rect) {
        if (focused)
            super.onFocusChanged(focused, direction, previouslyFocusedRect)
    }

    override fun onWindowFocusChanged(focused: Boolean) {
        if (focused)
            super.onWindowFocusChanged(focused)
    }
}