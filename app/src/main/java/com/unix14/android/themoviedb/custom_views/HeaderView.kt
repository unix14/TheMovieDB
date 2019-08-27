package com.unix14.android.themoviedb.custom_views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.unix14.android.themoviedb.R
import kotlinx.android.synthetic.main.header_view.view.*

private const val LAYOUT_HEIGHT_SIZE_IN_DP = 50
class HeaderView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr) {

    private fun Int.toPx(): Int = (this * resources.displayMetrics.density).toInt()

    var listener: HeaderViewListener? = null

    interface HeaderViewListener {
        fun onHeaderRatedMoviesClick() {}
        fun onHeaderAllMoviesClick() {}
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.header_view, this, true)
        hideAll()

        attrs?.let {
            val attrArray = context.obtainStyledAttributes(attrs, R.styleable.HeaderView)
            if (attrArray.hasValue(R.styleable.HeaderView_title)) {
                val title = attrArray.getString(R.styleable.HeaderView_title)
                headerViewTitle.text = title
                setTitleVisibility(true)
            }
            if (attrArray.hasValue(R.styleable.HeaderView_showAllMoviesButton)) {
                val showButton = attrArray.getBoolean(R.styleable.HeaderView_showAllMoviesButton, false)
                setAllMoviesButtonVisibility(showButton)
            }
            if (attrArray.hasValue(R.styleable.HeaderView_showRatedMoviesButton)) {
                val showButton = attrArray.getBoolean(R.styleable.HeaderView_showRatedMoviesButton, false)
                setRatedMoviesButtonVisibility(showButton)
            }
            if (attrArray.hasValue(R.styleable.HeaderView_showTitle)) {
                val showTitle = attrArray.getBoolean(R.styleable.HeaderView_showTitle, true)
                setTitleVisibility(showTitle)
            }
            attrArray.recycle()
        }

        initClicks()
    }

    fun setAllMoviesButtonVisibility(showBackBtn: Boolean) {
        if (showBackBtn) {
            headerViewAllMoviesBtn.visibility = View.VISIBLE
        } else {
            headerViewAllMoviesBtn.visibility = View.GONE
        }
    }

    fun setTitleVisibility(showTitle: Boolean) {
        if (showTitle) {
            headerViewTitle.visibility = View.VISIBLE
        } else {
            headerViewTitle.visibility = View.GONE
        }
    }

    fun setRatedMoviesButtonVisibility(showCloseBtn: Boolean) {
        if (showCloseBtn) {
            headerViewRatedMoviesBtn.visibility = View.VISIBLE
        } else {
            headerViewRatedMoviesBtn.visibility = View.GONE
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, LAYOUT_HEIGHT_SIZE_IN_DP.toPx())
    }

    fun setTitle(title: String?) {
        setTitleVisibility(true)
        headerViewTitle.text = title
    }

    private fun initClicks() {
        headerViewRatedMoviesBtn.setOnClickListener {
            listener?.onHeaderRatedMoviesClick()
        }
        headerViewAllMoviesBtn.setOnClickListener {
            listener?.onHeaderAllMoviesClick()
        }
    }

    private fun hideAll() {
        headerViewTitle.visibility = View.GONE
        headerViewAllMoviesBtn.visibility = View.GONE
        headerViewRatedMoviesBtn.visibility = View.GONE
    }
}