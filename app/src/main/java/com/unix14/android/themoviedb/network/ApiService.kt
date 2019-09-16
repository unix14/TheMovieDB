package com.unix14.android.themoviedb.network

import com.unix14.android.themoviedb.models.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    //==============Authentication===========

    @GET("authentication/guest_session/new")
    fun createGuestSession(): Call<GuestAuthResponse>


    //==============Movie Lists calls===========

    @GET("movie/top_rated")
    fun getTopRatedMovies(@Query("page") page: Int? = 1): Call<MovieListResponse>

    @GET("movie/now_playing")
    fun getNowInCinema(@Query("page") page: Int? = 1): Call<MovieListResponse>


    @GET("guest_session/{guest_session_id}/rated/movies")
    fun getRatedMoviesForGuest(@Path("guest_session_id") guestSessionId: String , @Query("page") page: Int? = 1): Call<MovieListResponse>

    @GET("movie/{movie_id}/videos")
    fun getVideosForMovieId(@Path("movie_id") movieId: String): Call<MovieVideosResponse>

    @GET("movie/upcoming")
    fun getUpComingMovies(@Query("page") page: Int? = 1): Call<MovieListResponse>

    @GET("movie/popular")
    fun getPopularMovies(@Query("page") page: Int? = 1): Call<MovieListResponse>

    //==============Movie Actions===========

    @GET("movie/{movie_id}")
    fun getMovieDetails(@Path("movie_id") movieId: String): Call<Movie>

    @POST("movie/{movie_id}/rating")
    fun rateMovieAsGuest(@Path("movie_id") movieId: String, @Query("guest_session_id") guestSessionId: String, @Body rating: MovieRate): Call<MovieRatingResponse>


    @GET("configuration/languages")
    fun getLanguagesList(): Call<ArrayList<Language>>

    @GET("genre/movie/list")
    fun getGenresList(): Call<GenreRequest>
}