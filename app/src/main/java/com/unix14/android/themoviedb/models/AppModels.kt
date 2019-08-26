package com.unix14.android.themoviedb.models

import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.ArrayList

class Movie(
    @SerializedName("id") var id: Int,

    @SerializedName("poster_path") val image: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("release_date") val realeseDate: Date,
    @SerializedName("genre_ids") var genreIds: ArrayList<Int>,

    @SerializedName("original_title") val originalTitle: String,
    @SerializedName("original_language") val originalLang: String,
    @SerializedName("title") val name: String,
    @SerializedName("backdrop_path") val backdropPath: String,
    @SerializedName("imdb_id") val imdbId: String,
    @SerializedName("popularity") var popularity: Float,
    @SerializedName("vote_count") var voteCount: Int,
    @SerializedName("vote_average") var voteAvg: Float,
    @SerializedName("adult") val adult: Boolean

)

class MovieListResponse(
    var page: Int,
    var results: ArrayList<Movie>,
    var total_results: Int,
    var total_pages: Int
)