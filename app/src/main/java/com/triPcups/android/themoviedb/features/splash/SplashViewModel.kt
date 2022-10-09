package com.triPcups.android.themoviedb.features.splash

import androidx.lifecycle.ViewModel
import com.triPcups.android.themoviedb.common.ProgressData
import com.triPcups.android.themoviedb.common.SingleLiveEvent
import com.triPcups.android.themoviedb.models.GenreRequest
import com.triPcups.android.themoviedb.models.GuestAuthResponse
import com.triPcups.android.themoviedb.models.Language
import com.triPcups.android.themoviedb.network.ApiService
import com.triPcups.android.themoviedb.network.AppSettings
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashViewModel(private val apiService: ApiService, private val appSettings: AppSettings) : ViewModel() {

    enum class NavigationEvent {
        GO_TO_MAIN_ACTIVITY
    }

    enum class ErrorEvent {
        NO_ERROR,
        AUTH_FAILED_ERROR,
        FETCH_LANGUAGES_ERROR,
        FETCH_GENRES_ERROR,
        CONNECTION_FAILED_ERROR
    }

    val progressData = ProgressData()
    val navigationEvent = SingleLiveEvent<NavigationEvent>()
    val errorEvent = SingleLiveEvent<ErrorEvent>()

    //Step 1
    private fun createGuestSession() {
        progressData.startProgress()

        apiService.createGuestSession().enqueue(object : Callback<GuestAuthResponse> {
            override fun onResponse(call: Call<GuestAuthResponse>, response: Response<GuestAuthResponse>) {
                val authResponse = response.body()
                progressData.endProgress()

                authResponse?.let {
                    if (response.isSuccessful && authResponse.success) {
                        val guestSessionId = authResponse.guest_session_id

                        appSettings.guestSessionId = guestSessionId
                        appSettings.lastExpirationDate = authResponse.expiresAt
                        getLanguagesList()
                    } else {
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

    //Step 2
    fun getLanguagesList() {
        progressData.startProgress()

        apiService.getLanguagesList().enqueue(object : Callback<ArrayList<Language>> {
            override fun onResponse(call: Call<ArrayList<Language>>, response: Response<ArrayList<Language>>) {
                progressData.endProgress()

                if (response.isSuccessful) {
                    val langList = response.body()
                    langList?.let {
                        appSettings.setLanguageList(it)
                        getGenresList()
                    }
                } else {
                    errorEvent.postValue(ErrorEvent.FETCH_LANGUAGES_ERROR)
                }
            }

            override fun onFailure(call: Call<ArrayList<Language>>, t: Throwable) {
                progressData.endProgress()
                errorEvent.postValue(ErrorEvent.CONNECTION_FAILED_ERROR)
            }

        })
    }

    //Step 3
    fun getGenresList(){
        progressData.startProgress()

        apiService.getGenresList().enqueue(object : Callback<GenreRequest>{
            override fun onResponse(call: Call<GenreRequest>, response: Response<GenreRequest>) {
                progressData.endProgress()
                if(response.isSuccessful){
                    val genresResponse = response.body()
                    genresResponse?.let {
                        appSettings.setGenresList(it.genres)

                        navigationEvent.postValue(NavigationEvent.GO_TO_MAIN_ACTIVITY)
                        errorEvent.postValue(ErrorEvent.NO_ERROR)
                    }
                }else{
                    errorEvent.postValue(ErrorEvent.FETCH_GENRES_ERROR)
                }
            }

            override fun onFailure(call: Call<GenreRequest>, t: Throwable) {
                progressData.endProgress()
                errorEvent.postValue(ErrorEvent.CONNECTION_FAILED_ERROR)
            }
        })
    }

    fun startSplashActivity() {
        if (appSettings.isValidUser()) {
            getLanguagesList()
        } else {
            createGuestSession()
        }
    }
}