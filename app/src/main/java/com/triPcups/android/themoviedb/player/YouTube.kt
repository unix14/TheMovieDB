package com.triPcups.android.themoviedb.player

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer


interface YouTubeCallback {
    fun onVideoStarted()
    fun onVideoPaused()
    fun onVideoFail()
    fun onVideoFinished()
}


interface YouTube {


    interface Builder {

    //    val listener: YouTubePlayerCallback
        fun setListener(listener: YouTubeCallback)
        fun setPlayerView(listener: YouTubePlayer)
        fun setLoop(isLoop: Boolean = true)
        fun setAutoPlay(isAutoPlay: Boolean = true)
        fun build(): YouTube
    }


    fun playVideo(videoId: String)
    fun stopVideo()
    fun pauseVideo()

}