package com.unix14.android.themoviedb.features

import androidx.lifecycle.ViewModel
import com.unix14.android.themoviedb.common.ProgressData
import com.unix14.android.themoviedb.common.SingleLiveEvent
import com.unix14.android.themoviedb.models.Movie
import com.unix14.android.themoviedb.models.MovieListResponse
import com.unix14.android.themoviedb.network.ApiService
import com.unix14.android.themoviedb.network.ApiSettings
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val apiService: ApiService, private val apiSettings: ApiSettings) : ViewModel() {

    enum class NavigationEvent{
        SHOW_MOVIE_LIST_SCREEN,
        SHOW_SPLASH_SCREEN,
        SHOW_SIGN_IN_SCREEN
    }

    enum class ErrorEvent{
        NO_ERROR,
        AUTH_FAILED_ERROR,
        FETCH_RATED_MOVIE_LIST_ERROR,
        CONNECTION_FAILED_ERROR
    }

    val progressData = ProgressData()
    val navigationEvent = SingleLiveEvent<NavigationEvent>()
    val errorEvent = SingleLiveEvent<ErrorEvent>()
    val ratedMovieList : ArrayList<Movie> =  arrayListOf()

    private fun getRatedMoviesList() {
        progressData.startProgress()
        if(apiSettings.requestToken == null){
            navigationEvent.postValue(NavigationEvent.SHOW_SPLASH_SCREEN)
            errorEvent.postValue(ErrorEvent.FETCH_RATED_MOVIE_LIST_ERROR)
            return
        }

        apiService.getRatedMoviesForGuest(apiSettings.requestToken!!,apiSettings.API_KEY).enqueue(object : Callback<MovieListResponse>{
            override fun onResponse(call: Call<MovieListResponse>, response: Response<MovieListResponse>) {
                progressData.endProgress()
                if(response.isSuccessful){
                    val movieListResponse = response.body()
                    movieListResponse?.let{
                        val movieList = it.results
                        ratedMovieList.addAll(movieList)
                        apiSettings.setRatedMovieList(movieList)
                    }

                    navigationEvent.postValue(NavigationEvent.SHOW_MOVIE_LIST_SCREEN)
                    errorEvent.postValue(ErrorEvent.NO_ERROR)
                }else{
                    errorEvent.postValue(ErrorEvent.FETCH_RATED_MOVIE_LIST_ERROR)
                }
            }

            override fun onFailure(call: Call<MovieListResponse>, t: Throwable) {
                errorEvent.postValue(ErrorEvent.CONNECTION_FAILED_ERROR)
                progressData.endProgress()
            }
        })
    }

    fun startMainActivity() {
        if(apiSettings.isValidUser() || ratedMovieList.isEmpty()){
            getRatedMoviesList()
        }else{
            navigationEvent.postValue(NavigationEvent.SHOW_MOVIE_LIST_SCREEN)
            errorEvent.postValue(ErrorEvent.AUTH_FAILED_ERROR)

            ratedMovieList.addAll(apiSettings.getRatedMovieList())
        }
    }

    fun getMovieRating(desiredMovieId : Int) : Float {
        for (movie in ratedMovieList) {
            if (movie.id == desiredMovieId)
                return movie.rating
        }
        return 0f
    }

    fun addLocalRatedMovie(movie: Movie) {
        ratedMovieList.add(movie)
    }
}