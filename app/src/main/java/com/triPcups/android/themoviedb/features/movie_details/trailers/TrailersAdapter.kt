package com.triPcups.android.themoviedb.features.movie_details.trailers

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.triPcups.android.themoviedb.models.Video

class TrailersAdapter(fm: FragmentManager, private val thumbnail: String) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private var videos: ArrayList<Video> = arrayListOf()

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                VideoThumbnailFragment.newInstance(thumbnail)
            }
            else -> {
                val video = videos[position - 1]
                VideoThumbnailFragment.newInstance(video)
            }
        }
    }

    override fun getCount(): Int = videos.size + 1

    override fun getPageTitle(position: Int): CharSequence? = videos[position - 1].type

    override fun getItemPosition(`object`: Any): Int = PagerAdapter.POSITION_NONE

    fun updateList(videos: ArrayList<Video>) {
        Log.d("wow", "updateList: ${videos.size}")
        this.videos = videos
        notifyDataSetChanged()
    }
}