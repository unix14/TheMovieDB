package com.unix14.android.themoviedb.features.splash

import androidx.lifecycle.ViewModel
import com.unix14.android.themoviedb.common.ProgressData
import com.unix14.android.themoviedb.common.SingleLiveEvent
import com.unix14.android.themoviedb.models.GuestAuthResponse
import com.unix14.android.themoviedb.models.Language
import com.unix14.android.themoviedb.network.ApiService
import com.unix14.android.themoviedb.network.ApiSettings
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashViewModel(private val apiService: ApiService, private val apiSettings: ApiSettings) : ViewModel() {

    enum class NavigationEvent {
        GO_TO_MAIN_ACTIVITY
    }

    enum class ErrorEvent {
        NO_ERROR,
        AUTH_FAILED_ERROR,
        FETCH_LANGUAGES_ERROR,
        CONNECTION_FAILED_ERROR
    }

    val progressData = ProgressData()
    val navigationEvent = SingleLiveEvent<NavigationEvent>()
    val errorEvent = SingleLiveEvent<ErrorEvent>()

    //Step 1
    private fun createGuestSession() {
        progressData.startProgress()

        apiService.createGuestSession(apiSettings.API_KEY).enqueue(object : Callback<GuestAuthResponse> {
            override fun onResponse(call: Call<GuestAuthResponse>, response: Response<GuestAuthResponse>) {
                val authResponse = response.body()
                progressData.endProgress()

                authResponse?.let {
                    if (response.isSuccessful && authResponse.success) {
                        val guestSessionId = authResponse.guest_session_id

                        apiSettings.requestToken = guestSessionId
                        apiSettings.lastExpirationDate = authResponse.expiresAt
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

        apiService.getLanguagesList(apiSettings.API_KEY).enqueue(object : Callback<ArrayList<Language>> {
            override fun onResponse(call: Call<ArrayList<Language>>, response: Response<ArrayList<Language>>) {
                progressData.endProgress()

                if (response.isSuccessful) {
                    val langList = response.body()
                    langList?.let {
                        apiSettings.setLanguageList(it)
                        navigationEvent.postValue(NavigationEvent.GO_TO_MAIN_ACTIVITY)
                        errorEvent.postValue(ErrorEvent.NO_ERROR)
                    }
                } else {
                    errorEvent.postValue(ErrorEvent.AUTH_FAILED_ERROR)
                }
            }

            override fun onFailure(call: Call<ArrayList<Language>>, t: Throwable) {
                progressData.endProgress()
                errorEvent.postValue(ErrorEvent.CONNECTION_FAILED_ERROR)
            }

        })
    }

    fun startSplashActivity() {
        if (apiSettings.isValidUser()) {
            getLanguagesList()
        } else {
            createGuestSession()
        }
    }

}