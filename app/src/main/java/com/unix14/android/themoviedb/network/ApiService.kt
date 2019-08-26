package com.unix14.android.themoviedb.network

import com.unix14.android.themoviedb.models.AuthResponse
import com.unix14.android.themoviedb.models.GuestAuthResponse
import com.unix14.android.themoviedb.models.Movie
import com.unix14.android.themoviedb.models.MovieListResponse
import retrofit2.Call
import retrofit2.http.*


interface ApiService {

    //==============Authentication===========
    @GET("authentication/requestToken/new")
    fun getRequestToken(@Query("api_key") apiKey: String): Call<AuthResponse>

    @GET("authentication/guest_session/new")
    fun createGuestSession(@Query("api_key") apiKey: String): Call<GuestAuthResponse>



    //==============Movie===========

    @GET("movie/top_rated")
    fun getTopRatedMovies(@Query("api_key") apiKey: String, @Query("page") page:Int = 1): Call<MovieListResponse>

    @GET("movie/{movie_id}")
    fun getMovieDetails(@Path("movie_id") movieId: String, @Query("api_key") apiKey: String): Call<Movie>

}