package com.unix14.android.themoviedb.features

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.unix14.android.themoviedb.common.ProgressData
import com.unix14.android.themoviedb.common.SingleLiveEvent
import com.unix14.android.themoviedb.models.*
import com.unix14.android.themoviedb.network.ApiService
import com.unix14.android.themoviedb.network.ApiSettings
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val apiService: ApiService, private val apiSettings: ApiSettings) : ViewModel() {

    enum class NavigationEvent{
        SHOW_MOVIE_LIST_SCREEN,
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
    val localRatedMovieList : ArrayList<Movie> =  arrayListOf()

    //Step1
    private fun createGuestSession() {      //TODO:: Move to SplashActivity
        progressData.startProgress()

        apiService.createGuestSession(apiSettings.API_KEY).enqueue(object : Callback<GuestAuthResponse>{
            override fun onResponse(call: Call<GuestAuthResponse>, response: Response<GuestAuthResponse>) {
                val authResponse = response.body()
                progressData.endProgress()

                authResponse?.let{
                    if (response.isSuccessful && authResponse.success) {
                        val guestSessionId = authResponse.guest_session_id

                        apiSettings.requestToken = guestSessionId
                        apiSettings.lastExpirationDate = authResponse.expiresAt

                        getRatedMoviesList()
                    }else{
                        errorEvent.postValue(ErrorEvent.AUTH_FAILED_ERROR)
                    }
                }
            }

            override fun onFailure(call: Call<GuestAuthResponse>, t: Throwable) {
                progressData.endProgress()
                errorEvent.postValue(ErrorEvent.CONNECTION_FAILED_ERROR)
            }
        })

    }

    //Step2
    fun getRatedMoviesList() {
        progressData.startProgress()

        if(apiSettings.requestToken == null){
            createGuestSession()
            errorEvent.postValue(ErrorEvent.FETCH_RATED_MOVIE_LIST_ERROR)

            return
        }

        apiService.getRatedMoviesForGuest(apiSettings.requestToken!!,apiSettings.API_KEY).enqueue(object : Callback<MovieListResponse>{
            override fun onResponse(call: Call<MovieListResponse>, response: Response<MovieListResponse>) {
                progressData.endProgress()

                if(response.isSuccessful){
                    val ratedMoviesList = response.body()
                    ratedMoviesList?.let{


                        localRatedMovieList.addAll(it.results)
                    }

                    navigationEvent.postValue(NavigationEvent.SHOW_MOVIE_LIST_SCREEN)
                    errorEvent.postValue(ErrorEvent.NO_ERROR)
                }else{
                    errorEvent.postValue(ErrorEvent.FETCH_RATED_MOVIE_LIST_ERROR)
                }
            }

            override fun onFailure(call: Call<MovieListResponse>, t: Throwable) {
                progressData.endProgress()
                errorEvent.postValue(ErrorEvent.CONNECTION_FAILED_ERROR)
            }
        })
    }

    fun startMainActivity() {
        if(apiSettings.isValidUser()){
            getRatedMoviesList()
        }else{
            createGuestSession()
        }
    }

//    fun isMovieRated(desiredMovieId: Int) : Boolean {
//        localRatedMovieList?.let{
//            for (movie in it){
//                if(movie.id == desiredMovieId)
//                    return true
//            }
//        }
//        return false
//    }

    fun getMovieRating(desiredMovieId : Int) : Float {
        localRatedMovieList.let {
            for (movie in it) {
                if (movie.id == desiredMovieId)
                    return movie.rating
            }
        }
        return 0f
    }

    fun addLocalRatedMovie(movie: Movie) {
        localRatedMovieList.add(movie)
    }
}