package com.example.tiktok_analog.util

import android.content.Context
import android.util.AttributeSet
import android.widget.ScrollView


class ScrollViewExtended : ScrollView {
    private var scrollViewListener: ScrollViewListener? = null

    constructor(context: Context?) : super(context)

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    fun setScrollViewListener(scrollViewListener: ScrollViewListener?) {
        this.scrollViewListener = scrollViewListener
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        scrollViewListener?.onScrollChanged(this, l, t, oldl, oldt)
    }
}

interface ScrollViewListener {
    fun onScrollChanged(
        scrollView: ScrollViewExtended?,
        x: Int, y: Int, oldx: Int, oldy: Int
    )
}