package com.unix14.android.themoviedb.features.movie_details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.unix14.android.themoviedb.common.ProgressData
import com.unix14.android.themoviedb.common.SingleLiveEvent
import com.unix14.android.themoviedb.models.Movie
import com.unix14.android.themoviedb.network.ApiService
import com.unix14.android.themoviedb.network.ApiSettings
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MovieDetailsViewModel(private val apiService: ApiService, private val apiSettings:ApiSettings) : ViewModel() {

    enum class ErrorEvent{
        NO_ERROR,
        FETCH_DATA_ERROR,
        SERVER_CONNECTION_ERROR
    }

    val progressData = ProgressData()
    val movieDetailsData = MutableLiveData<Movie>()
    val errorEvent = SingleLiveEvent<ErrorEvent>()

    fun getMovieDetails(movieId: String) {
        progressData.startProgress()
        apiService.getMovieDetails(movieId,apiSettings.API_KEY).enqueue(object : Callback<Movie> {
            override fun onResponse(call: Call<Movie>, response: Response<Movie>) {
                progressData.endProgress()

                if(response.isSuccessful){
                    errorEvent.postValue(ErrorEvent.NO_ERROR)
                    var movie = response.body()

                    movie?.let{
                        movieDetailsData.postValue(it)
                    }

                }else{
                    errorEvent.postValue(ErrorEvent.FETCH_DATA_ERROR)
                }
            }

            override fun onFailure(call: Call<Movie>, t: Throwable) {
                progressData.endProgress()
                errorEvent.postValue(ErrorEvent.SERVER_CONNECTION_ERROR)
            }
        })
    }
}
