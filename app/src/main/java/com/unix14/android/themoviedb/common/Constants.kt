package com.unix14.android.themoviedb.common

class Constants{
    companion object{
        const val MOVIE_LIST_FRAGMENT = "movie_list_fragment"
        const val MOVIE_DETAILS_FRAGMENT = "movie_details_fragment"
        const val SEARCH_DIALOG_FRAGMENT = "search_dialog_fragment"

        const val SMALL_POSTER_BASE_URL = "http://image.tmdb.org/t/p/w185/"
        const val BIG_POSTER_BASE_URL = "http://image.tmdb.org/t/p/original"
        const val IMDB_BASE_URL = "https://www.imdb.com/title/"
        const val NETFLIX_SEARCH_URL = "https://www.netflix.com/search?q="
        const val GOOGLE_SEARCH_URL = "http://www.google.com/search?q="
        const val YOUTUBE_VIDEO_BASE_URL = "https://www.youtube.com/watch?v="
        const val YOUTUBE_IMAGE_BASE_URL = "https://img.youtube.com/vi/"
        const val YOUTUBE_IMAGE_BASE_URL_SUFFIX = "/0.jpg"
        const val YOUTUBE_TAG = "YouTube"

        const val POSTER_ROUNDED_CORNERS_RADIUS = 30

        const val MOVIE_LIST_ALL_MOVIES_TYPE = 1
        const val MOVIE_LIST_RATED_MOVIES_TYPE = 2
        const val MOVIE_LIST_MOST_RATED_MOVIES_TYPE = 3
        const val MOVIE_LIST_UPCOMING_MOVIES_TYPE = 4
        const val MOVIE_LIST_POPULAR_MOVIES_TYPE = 5

        const val DEFAULT_ALPHA_DURATION_IN_MS = 1500L
        const val FASTER_ALPHA_DURATION_IN_MS = 800L

        const val API_PAGINATION_ITEMS_PER_PAGE = 20

        const val TOTAL_RATING_STARS = 5
        const val MINIMUM_DESCRIPTION_LINES = 4

        const val GOOGLE_STORE_BASE_URL = "https://play.google.com/store/apps/details?id="

    }
}