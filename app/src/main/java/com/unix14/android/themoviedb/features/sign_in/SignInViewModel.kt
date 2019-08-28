package com.unix14.android.themoviedb.features.sign_in

import androidx.lifecycle.ViewModel
import com.unix14.android.themoviedb.common.ProgressData
import com.unix14.android.themoviedb.common.SingleLiveEvent
import com.unix14.android.themoviedb.models.AuthResponse
import com.unix14.android.themoviedb.network.ApiService
import com.unix14.android.themoviedb.network.ApiSettings
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInViewModel(private val apiService: ApiService, private val apiSettings:ApiSettings) : ViewModel() {


    sealed class NavigationEvent {
        data class NavigateToConfirmScreenEvent(val requestToken: String) : NavigationEvent()
    }

    val progressData = ProgressData()
    val navigationEvent = SingleLiveEvent<NavigationEvent>()


    fun generateSessionId() {
        progressData.startProgress()

        apiService.getRequestToken().enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                progressData.endProgress()

                val authResponse = response.body()

                authResponse?.let{
                    if (response.isSuccessful && authResponse.success) {
                        val requestToken = authResponse.requestToken

//                        apiSettings.requestToken
//                        navigationEvent.postValue(
//                            NavigationEvent.NavigateToConfirmScreenEvent(
//                                requestToken
//                            )
//                        )
                    }
                }

            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                progressData.endProgress()
            }

        })

    }
}
