package com.unix14.android.themoviedb.common

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Analyze RecyclerView and LayoutManger state and request new data in advance
 * Created by eyalya94 on 25/08/2019.
 */

abstract class InfiniteRecyclerViewScrollListener(private val mLayoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener() {


    /**
     * Number of lines treshold to start loading next portion of data
     */
    private val mLinesThreshold = 5
    private val mItemsThreshold = mLinesThreshold

    /**
     * True when the data was requested
     */
    private var isPendingData = false
    /**
     * False when the end of endless data reached
     */
    private var mHaveMoreData = true

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        val totalItemCount = mLayoutManager.itemCount
        val lastVisibleItem = mLayoutManager.findLastVisibleItemPosition()

        if (mHaveMoreData && !isPendingData && lastVisibleItem + mItemsThreshold >= totalItemCount) {
            //We are close to bottom, time to load next portion

            requestData(totalItemCount)
            isPendingData = true
        }

        if (mHaveMoreData && totalItemCount == lastVisibleItem + 1) {
            //We are at the bottom, but data didn't arrived yet
            onDataHunger()
        }
    }

    fun setHaveMoreData(haveMoreData: Boolean) {
        this.mHaveMoreData = haveMoreData
    }

    fun notifyDataLoaded() {
        isPendingData = false
    }

    internal abstract fun requestData(offset: Int)

    internal abstract fun onDataHunger()

}