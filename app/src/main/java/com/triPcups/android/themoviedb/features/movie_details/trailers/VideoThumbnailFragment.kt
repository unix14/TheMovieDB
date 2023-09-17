package com.triPcups.android.themoviedb.features.movie_details.trailers

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.triPcups.android.themoviedb.common.Constants
import com.triPcups.android.themoviedb.databinding.VideoItemBinding
import com.triPcups.android.themoviedb.models.Video

private const val VIDEO_KEY = "video_key"
private const val THUMBNAIL_KEY = "thumbnail_key"

class VideoThumbnailFragment : Fragment() {

    interface VideoThumbnailFragmentListener {
        fun onVideoIdClick(videoId: String)
    }

    private var youTubePlayer: YouTubePlayer? = null

    private lateinit var binding: VideoItemBinding
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
        binding = VideoItemBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initThumbnail()
    }

    override fun onStart() {
        super.onStart()
//        if(video!= null)
            viewLifecycleOwner.lifecycle.addObserver(binding.youTubePlayerView)
    }

    override fun onDestroy() {
        super.onDestroy()
        if(video!= null) {
            view?.let {
                binding.apply {
                    youTubePlayer?.pause()
//        youTubePlayerView?.release()
                    youTubePlayerView.removeYouTubePlayerListener(ytpListener)
                    youTubePlayer?.removeListener(ytpListener)
                    viewLifecycleOwner.lifecycle.removeObserver(youTubePlayerView)
                }
            }
        }
    }

    private fun initThumbnail() = with(binding) {
        video?.apply {
            val videoThumbnailUrl = Constants.YOUTUBE_IMAGE_BASE_URL + key + Constants.YOUTUBE_IMAGE_BASE_URL_SUFFIX
            context?.let{
                Glide.with(it).load(videoThumbnailUrl).into(videoItemThumbnail)
            }
            Log.d("wow", "videoThumbnailUrl: $videoThumbnailUrl")

            videoItemType.visibility = View.VISIBLE
            videoItemType.text = type

//            if(video == null) return@with
            videoItemThumbnail.setOnClickListener {
                showVideoLayout(true)
                //                youTubePlayer?.loadVideo(key, 0f)
                Log.d("wow", "initThumbnail: $key")
                //                youTubePlayer?.play()

                youTubePlayerView.addYouTubePlayerListener(ytpListener)
                //todo add youtubePlayer
                youTubePlayerView.getYouTubePlayerWhenReady(object : YouTubePlayerCallback {
                    override fun onYouTubePlayer(youTubePlayer: YouTubePlayer) {
                        Log.d("wow", "onYouTubePlayer: $youTubePlayer")
                        (this@VideoThumbnailFragment).youTubePlayer = youTubePlayer
                        //                        youTubePlayer.play()

                        youTubePlayerView.getPlayerUiController().apply {
                            showDuration(true)
                            showYouTubeButton(true)
                            showBufferingProgress(true)
                            showCurrentTime(true)
                            showSeekBar(true)
                            showVideoTitle(false)
                        }
                        youTubePlayer.loadVideo(key, 0f)
                    }
                })
            }

            binding.videoItemType.setOnClickListener {
                listener?.onVideoIdClick(key)
            }
        }
        thumbnail?.let { thumbStr ->
            context?.let{
                Glide.with(it).load(thumbStr).into(videoItemThumbnail)
            }
            videoItemType.visibility = View.GONE
            videoItemPlayBtn.visibility = View.GONE
        }
    }

    private val ytpListener = object : YouTubePlayerListener {
        override fun onApiChange(youTubePlayer: YouTubePlayer) {}
        override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {}
        override fun onVideoDuration(youTubePlayer: YouTubePlayer, duration: Float) {}
        override fun onVideoId(youTubePlayer: YouTubePlayer, videoId: String) {}
        override fun onVideoLoadedFraction(
            youTubePlayer: YouTubePlayer,
            loadedFraction: Float
        ) {}
        override fun onPlaybackRateChange(
            youTubePlayer: YouTubePlayer,
            playbackRate: PlayerConstants.PlaybackRate
        ) {}
        override fun onPlaybackQualityChange(
            youTubePlayer: YouTubePlayer,
            playbackQuality: PlayerConstants.PlaybackQuality
        ) {}
        override fun onError(
            youTubePlayer: YouTubePlayer,
            error: PlayerConstants.PlayerError
        ) {
            Log.e("wow", "onError: $error")
            when(error) {
                PlayerConstants.PlayerError.INVALID_PARAMETER_IN_REQUEST,
                PlayerConstants.PlayerError.VIDEO_NOT_FOUND,
                PlayerConstants.PlayerError.VIDEO_NOT_PLAYABLE_IN_EMBEDDED_PLAYER -> {
                    youTubePlayer.pause()
                    listener?.onVideoIdClick(video?.key ?: "")
                    showVideoLayout(false)
                }

                else -> {}
            }
        }

        override fun onReady(youTubePlayer: YouTubePlayer) {
            Log.d("wow", "onReady: ")
            showVideoLayout()
            this@VideoThumbnailFragment.youTubePlayer = youTubePlayer
            youTubePlayer.loadVideo(video?.key ?: "", 0f)
        }

        override fun onStateChange(
            youTubePlayer: YouTubePlayer,
            state: PlayerConstants.PlayerState
        ) {
            Log.d("wow", "onStateChange: state is $state")
            when(state) {
                PlayerConstants.PlayerState.PAUSED,
                PlayerConstants.PlayerState.ENDED -> {
                    showVideoLayout(false)
                }
                PlayerConstants.PlayerState.UNSTARTED,
                PlayerConstants.PlayerState.PLAYING,
                PlayerConstants.PlayerState.BUFFERING
                -> {
                    showVideoLayout()
                }
                else -> {}
//                            PlayerConstants.PlayerState.VIDEO_CUED,
//                            PlayerConstants.PlayerState.UNKNOWN,
//                            PlayerConstants.PlayerState.BUFFERING -> {
//
//                            }
            }
        }
    }

    private fun showVideoLayout(isVideo: Boolean = true) {
        if(isVideo) {
            binding.videoItemThumbnail.visibility = View.INVISIBLE
            binding.videoItemPlayBtn.visibility = View.INVISIBLE
            binding.videoItemType.visibility = View.INVISIBLE
        } else {
            binding.videoItemThumbnail.visibility = View.VISIBLE
            binding.videoItemPlayBtn.visibility = View.VISIBLE
            binding.videoItemType.visibility = View.VISIBLE
        }
    }

    override fun onPause() {
        super.onPause()
        binding.videoItemThumbnail.visibility = View.VISIBLE
        youTubePlayer?.pause()
        showVideoLayout(false)
    }

    override fun onStop() {
        super.onStop()
        binding.youTubePlayerView.removeYouTubePlayerListener(ytpListener)
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
