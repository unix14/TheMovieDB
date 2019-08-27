package com.unix14.android.themoviedb.network

import com.unix14.android.themoviedb.models.*
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
    fun getTopRatedMovies(@Query("api_key") apiKey: String, @Query("page") page: Int? = 1): Call<MovieListResponse>

    @GET("movie/{movie_id}")
    fun getMovieDetails(@Path("movie_id") movieId: String, @Query("api_key") apiKey: String): Call<Movie>

    @POST("movie/{movie_id}/rating")
    fun rateMovie(@Path("movie_id") movieId: String, @Query("api_key") apiKey: String, @Query("guest_session_id") guestSessionId: String, @Body rating: MovieRate): Call<MovieRatingResponse>

    @GET("guest_session/{guest_session_id}/rated/movies")
    fun getRatedMoviesForGuest(@Path("guest_session_id") guestSessionId: String, @Query("api_key") apiKey: String , @Query("page") page: Int? = 1): Call<MovieListResponse>

}