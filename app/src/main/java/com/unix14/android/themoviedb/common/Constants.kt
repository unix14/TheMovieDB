package com.unix14.android.themoviedb.common

class Constants{
    companion object{
        const val MOVIE_LIST_FRAGMENT = "movie_list_fragment"
        const val SIGN_IN_FRAGMENT = "sign_in_fragment"

        const val AUTHENTICATION_URL = "https://www.themoviedb.org/authenticate/"
        const val SMALL_POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185/"
        const val BIG_POSTER_BASE_URL = "http://image.tmdb.org/t/p/original"
        const val IMDB_BASE_URL = "https://www.imdb.com/title/"
        const val YOUTUBE_VIDEO_BASE_URL = "https://www.youtube.com/watch?v="
        const val YOUTUBE_IMAGE_BASE_URL = "https://img.youtube.com/vi/"
        const val YOUTUBE_IMAGE_BASE_URL_SFFIX = "/0.jpg"
        const val YOUTUBE_TAG = "YouTube"

        const val POSTER_ROUNDED_CORNERS_RADIUS = 30

        const val MOVIE_LIST_ALL_MOVIES_TYPE = 1
        const val MOVIE_LIST_RATED_MOVIES_TYPE = 2

        const val DEFAULT_ALPHA_DURATION_IN_MS = 1500L
    }
}