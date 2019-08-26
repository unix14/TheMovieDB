package com.unix14.android.themoviedb.features.splash

import androidx.lifecycle.ViewModel
import com.unix14.android.themoviedb.common.ProgressData
import com.unix14.android.themoviedb.common.SingleLiveEvent
import com.unix14.android.themoviedb.models.GuestAuthResponse
import com.unix14.android.themoviedb.network.ApiService
import com.unix14.android.themoviedb.network.ApiSettings
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashViewModel(private val apiService: ApiService, private val apiSettings: ApiSettings) : ViewModel() {

    enum class NavigationEvent{
        GO_TO_MAIN_ACTIVITY
    }

    enum class ErrorEvent{
        NO_ERROR,
        AUTH_FAILED_ERROR,
        CONNECTION_FAILED_ERROR
    }

    val progressData = ProgressData()
    val navigationEvent = SingleLiveEvent<NavigationEvent>()
    val errorEvent = SingleLiveEvent<ErrorEvent>()

    private fun createGuestSession() {
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
                        navigationEvent.postValue(NavigationEvent.GO_TO_MAIN_ACTIVITY)
                        errorEvent.postValue(ErrorEvent.NO_ERROR)
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

    fun startSplashActivity() {
        if(apiSettings.isValidUser()){
            navigationEvent.postValue(NavigationEvent.GO_TO_MAIN_ACTIVITY)
            errorEvent.postValue(ErrorEvent.NO_ERROR)
        }else{
            createGuestSession()
        }
    }

}