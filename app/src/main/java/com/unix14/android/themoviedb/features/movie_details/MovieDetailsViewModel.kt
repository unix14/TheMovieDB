package com.unix14.android.themoviedb.features.movie_details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.unix14.android.themoviedb.common.Constants
import com.unix14.android.themoviedb.common.ProgressData
import com.unix14.android.themoviedb.common.SingleLiveEvent
import com.unix14.android.themoviedb.models.*
import com.unix14.android.themoviedb.network.ApiService
import com.unix14.android.themoviedb.network.ApiSettings
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieDetailsViewModel(private val apiService: ApiService, private val apiSettings: ApiSettings) : ViewModel() {

    enum class ErrorEvent {
        NO_ERROR,
        FETCH_DATA_ERROR,
        SERVER_CONNECTION_ERROR
    }

    enum class RatingEvent {
        RATED,
        RATING_ERROR
    }

    val progressData = ProgressData()
    val movieDetailsData = MutableLiveData<Movie>()
    val trailerVideosData = MutableLiveData<ArrayList<Video>>()
    val errorEvent = SingleLiveEvent<ErrorEvent>()
    val ratingMovieEvent = SingleLiveEvent<RatingEvent>()

    fun getMovieDetails(movieId: String) {
        progressData.startProgress()
        apiService.getMovieDetails(movieId, apiSettings.API_KEY).enqueue(object : Callback<Movie> {
            override fun onResponse(call: Call<Movie>, response: Response<Movie>) {
                progressData.endProgress()

                if (response.isSuccessful) {
                    errorEvent.postValue(ErrorEvent.NO_ERROR)
                    val movie = response.body()

                    movie?.let {
                        movieDetailsData.postValue(it)
                    }
                } else {
                    errorEvent.postValue(ErrorEvent.FETCH_DATA_ERROR)
                }
            }

            override fun onFailure(call: Call<Movie>, t: Throwable) {
                progressData.endProgress()
                errorEvent.postValue(ErrorEvent.SERVER_CONNECTION_ERROR)
            }
        })
    }

    fun sendRating(movieId: String, rating: Float) {
        progressData.startProgress()
        val movieRate = MovieRate(rating)
        apiService.rateMovie(movieId, apiSettings.API_KEY, apiSettings.requestToken!!, movieRate)
            .enqueue(object : Callback<MovieRatingResponse> {
                override fun onResponse(call: Call<MovieRatingResponse>, response: Response<MovieRatingResponse>) {
                    progressData.endProgress()

                    if (response.isSuccessful) {
                        ratingMovieEvent.postValue(RatingEvent.RATED)
                    } else {
                        ratingMovieEvent.postValue(RatingEvent.RATING_ERROR)
                    }
                }

                override fun onFailure(call: Call<MovieRatingResponse>, t: Throwable) {
                    progressData.endProgress()
                    errorEvent.postValue(ErrorEvent.SERVER_CONNECTION_ERROR)
                    ratingMovieEvent.postValue(RatingEvent.RATING_ERROR)
                }
            })
    }

    fun getVideosForMovie(id: String) {
        progressData.startProgress()

        apiService.getVideosForMovieId(id, apiSettings.API_KEY).enqueue(object : Callback<MovieVideosResponse> {
            override fun onResponse(call: Call<MovieVideosResponse>, response: Response<MovieVideosResponse>) {
                progressData.endProgress()

                if (response.isSuccessful) {
                    errorEvent.postValue(ErrorEvent.NO_ERROR)
                    val videos = response.body()


                    videos?.let {
                        val results: java.util.ArrayList<Video> = arrayListOf()
                        for (video in it.results) {
                            if (video.site == Constants.YOUTUBE_TAG) {
                                //we want to filter youtube videos ONLY
                                results.add(video)
                            }
                        }
                        trailerVideosData.postValue(results)
                    }
                } else {
                    errorEvent.postValue(ErrorEvent.FETCH_DATA_ERROR)
                }
            }

            override fun onFailure(call: Call<MovieVideosResponse>, t: Throwable) {
                progressData.endProgress()
                errorEvent.postValue(ErrorEvent.SERVER_CONNECTION_ERROR)
            }
        })
    }
}
