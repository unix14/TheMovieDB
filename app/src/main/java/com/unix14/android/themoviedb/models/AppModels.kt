package com.unix14.android.themoviedb.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

class Movie : Serializable{

    @SerializedName("id") var id: Int = 0

    @SerializedName("poster_path") val image: String = ""
    @SerializedName("overview") val overview: String = ""
    @SerializedName("release_date") val realeseDate: Date? = null
    @SerializedName("genre_ids")  lateinit var genreIds: ArrayList<Int>

    @SerializedName("original_title") val originalTitle: String = ""
    @SerializedName("original_language") val originalLang: String = ""
    @SerializedName("title") val name: String = ""
    @SerializedName("backdrop_path") val backdropPath: String = ""
    @SerializedName("imdb_id") val imdbId: String = ""
    @SerializedName("popularity") var popularity: Float = 0.0f
    @SerializedName("vote_count") var voteCount: Int = 0
    @SerializedName("vote_average") var voteAvg: Float = 0.0f
    @SerializedName("adult") val adult: Boolean = false
    @SerializedName("rating") var rating: Float = 0.0f
}

class MovieListResponse(
    var page: Int,
    var results: ArrayList<Movie>,
    var total_results: Int,
    var total_pages: Int
)

class MovieRate(
    @SerializedName("value") var rating: Float
)