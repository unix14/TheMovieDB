package com.unix14.android.themoviedb.features.movie_details.trailers

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.unix14.android.themoviedb.R
import com.unix14.android.themoviedb.common.Constants
import com.unix14.android.themoviedb.models.Video
import kotlinx.android.synthetic.main.video_item.*


private const val VIDEO_KEY = "video_key"
private const val THUMBNAIL_KEY = "thumbnail_key"

class VideoThumbnailFragment : Fragment() {


    interface VideoThumbnailFragmentListener {
        fun onVideoIdClick(videoId: String)
    }

    private var thumbnail: String? = null
    private var video: Video? = null
    private var listener: VideoThumbnailFragmentListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            video = it.getSerializable(VIDEO_KEY) as Video?
            thumbnail = it.getString(THUMBNAIL_KEY)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.video_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initVideo()
    }

    private fun initVideo() {
        video?.let { vid ->
            val videoThumbnailUrl = Constants.YOUTUBE_IMAGE_BASE_URL + vid.key + Constants.YOUTUBE_IMAGE_BASE_URL_SFFIX
            Glide.with(context).load(videoThumbnailUrl).into(videoItemThumbnail)

            videoItemType.text = vid.type

            view?.setOnClickListener {
                listener?.onVideoIdClick(vid.key)
            }
        }
        thumbnail?.let {
            Glide.with(context).load(it).into(videoItemThumbnail)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is VideoThumbnailFragmentListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement VideoThumbnailFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }


    companion object {
        /**
         * @param video the video object we try to initialize inside the ViewPager
         * @return A new instance of fragment VideoThumbnailFragment.
         */
        @JvmStatic
        fun newInstance(video: Video) =
            VideoThumbnailFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(VIDEO_KEY, video)
                }
            }

        /**
         * @param thumbnail is the first image which is also the backdrop image we get from response
         * @return A new instance of fragment VideoThumbnailFragment.
         */
        @JvmStatic
        fun newInstance(thumbnail: String) =
            VideoThumbnailFragment().apply {
                arguments = Bundle().apply {
                    putString(THUMBNAIL_KEY, thumbnail)
                }
            }
    }
}
