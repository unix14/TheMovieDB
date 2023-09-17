package com.triPcups.android.themoviedb.custom_views

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import android.widget.TextView
import com.ferfalk.simplesearchview.SimpleSearchView
import com.triPcups.android.themoviedb.R
import com.triPcups.android.themoviedb.databinding.HeaderViewBinding

private const val LAYOUT_HEIGHT_SIZE_IN_DP = 50

class HeaderView @JvmOverloads
constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    FrameLayout(context, attrs, defStyleAttr), TextView.OnEditorActionListener {

    private fun Int.toPx(): Int = (this * resources.displayMetrics.density).toInt()

    private lateinit var binding: HeaderViewBinding
    var listener: HeaderViewListener? = null

    interface HeaderViewListener {
        fun onHeaderRatedMoviesClick() {}
        fun onHeaderAllMoviesClick() {}
        fun onHeaderMenuClick() {}
        fun onHeaderSearchSubmit(query: String?) {}
        fun onOpenKeyboard()
    }

    init {
        binding = HeaderViewBinding.inflate(LayoutInflater.from(context), this)
        hideAll()

        attrs?.let {
            val attrArray = context.obtainStyledAttributes(attrs, R.styleable.HeaderView)
            if (attrArray.hasValue(R.styleable.HeaderView_title)) {
                val title = attrArray.getString(R.styleable.HeaderView_title)
                binding.headerViewTitle.text = title
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
            if (attrArray.hasValue(R.styleable.HeaderView_showMenuButton)) {
                val showButton = attrArray.getBoolean(R.styleable.HeaderView_showMenuButton, true)
                setMenuButtonVisibility(showButton)
            }
            if (attrArray.hasValue(R.styleable.HeaderView_showSearchButton)) {
                val showButton = attrArray.getBoolean(R.styleable.HeaderView_showSearchButton, true)
                setSearchButtonVisibility(showButton)
            }
            attrArray.recycle()
        }
        initClicks()
        initSearchBtn()
    }

    private fun initSearchBtn() = with(binding) {
        headerViewSearchView.setKeepQuery(true)
        headerViewSearchView.searchEditText.setOnEditorActionListener(this@HeaderView)
        headerViewSearchView.searchEditText.imeOptions = EditorInfo.IME_ACTION_SEARCH
        headerViewSearchView.setOnQueryTextListener(object : SimpleSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                search(query)
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                search(query)
//                headerViewSearchView.searchEditText.setSelection(headerViewSearchView.searchEditText.length())
                return true
            }

            override fun onQueryTextCleared(): Boolean {
//                headerViewSearchView.closeSearch(true)
                listener?.onOpenKeyboard()
                headerViewTitle.text = "Search..."

                return true
            }
        })
    }

    override fun onEditorAction(tv: TextView?, actionId: Int, keyEvent: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_SEND) {
            tv?.let {
                search(it.text.toString())
            }
            binding.headerViewSearchView.closeSearch(true)
            return true
        }
        return false
    }

    private fun search(query: String?) = with(binding){
        listener?.onHeaderSearchSubmit(query)

        if (!query.isNullOrBlank()) {
            headerViewTitle.text = "Searching '$query'"
        }
    }

    private fun setAllMoviesButtonVisibility(showBtn: Boolean) = with(binding){
        if (showBtn) {
            headerViewAllMoviesBtn.visibility = View.VISIBLE
        } else {
            headerViewAllMoviesBtn.visibility = View.GONE
        }
    }

    private fun setMenuButtonVisibility(showBtn: Boolean)= with(binding) {
        if (showBtn) {
            headerViewMenuBtn.visibility = View.VISIBLE
        } else {
            headerViewMenuBtn.visibility = View.GONE
        }
    }

    private fun setSearchButtonVisibility(showBtn: Boolean) = with(binding){
        if (showBtn) {
            headerViewSearchBtn.visibility = View.VISIBLE
        } else {
            headerViewSearchBtn.visibility = View.GONE
        }
    }

    private fun setTitleVisibility(showTitle: Boolean) = with(binding){
        if (showTitle) {
            headerViewTitle.visibility = View.VISIBLE
        } else {
            headerViewTitle.visibility = View.GONE
        }
    }

    private fun setRatedMoviesButtonVisibility(showBtn: Boolean)= with(binding) {
        if (showBtn) {
            headerViewRatedMoviesBtn.visibility = View.VISIBLE
        } else {
            headerViewRatedMoviesBtn.visibility = View.GONE
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, LAYOUT_HEIGHT_SIZE_IN_DP.toPx())
    }

    fun setTitle(title: String?) = with(binding){
        setTitleVisibility(true)
        headerViewTitle.text = title
    }

    private fun initClicks() = with(binding){
        headerViewRatedMoviesBtn.setOnClickListener {
            listener?.onHeaderRatedMoviesClick()
        }
        headerViewAllMoviesBtn.setOnClickListener {
            listener?.onHeaderAllMoviesClick()
        }
        headerViewMenuBtn.setOnClickListener {
            listener?.onHeaderMenuClick()
        }
        headerViewSearchBtn.setOnClickListener {
            if (!headerViewSearchView.isSearchOpen) {
                headerViewSearchView.showSearch(true)
            } else {
                val query = headerViewSearchView.searchEditText.text.toString()
                search(query)
            }
        }
    }

    private fun hideAll() = with(binding){
        headerViewTitle.visibility = View.GONE
        headerViewAllMoviesBtn.visibility = View.GONE
        headerViewRatedMoviesBtn.visibility = View.GONE
        headerViewSearchBtn.visibility = View.GONE
    }

    fun openSearchField() = with(binding){
        if (!headerViewSearchView.isSearchOpen) {
            headerViewSearchView.showSearch(true)
        } else {
            headerViewSearchView.closeSearch(true)
        }
    }

    fun onBackPress(): Boolean= with(binding) {
        return headerViewSearchView.onBackPressed()
    }

    fun closeSearchField()= with(binding) {
        headerViewSearchView.closeSearch(true)
    }

    fun setVoiceQueryData(requestCode: Int, resultCode: Int, data: Intent?)= with(binding) {
        headerViewSearchView.onActivityResult(requestCode, resultCode, data)
    }
}