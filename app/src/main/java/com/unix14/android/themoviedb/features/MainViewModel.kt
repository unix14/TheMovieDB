package com.unix14.android.themoviedb.features

import androidx.lifecycle.ViewModel
import com.unix14.android.themoviedb.common.ProgressData
import com.unix14.android.themoviedb.common.SingleLiveEvent
import com.unix14.android.themoviedb.models.AuthResponse
import com.unix14.android.themoviedb.models.GuestAuthResponse
import com.unix14.android.themoviedb.network.ApiService
import com.unix14.android.themoviedb.network.ApiSettings
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(private val apiSettings: ApiSettings) : ViewModel() {

    enum class NavigationEvent{
        SHOW_MOVIE_LIST_SCREEN,
        SHOW_SIGN_IN_SCREEN
    }

    enum class ErrorEvent{
        NO_ERROR,
        AUTH_FAILED_ERROR
    }

    val navigationEvent = SingleLiveEvent<NavigationEvent>()
    val errorEvent = SingleLiveEvent<ErrorEvent>()

    fun startMainActivity() {
        if(apiSettings.isValidUser()){
            navigationEvent.postValue(NavigationEvent.SHOW_MOVIE_LIST_SCREEN)
            errorEvent.postValue(ErrorEvent.NO_ERROR)
        }else{
            errorEvent.postValue(ErrorEvent.AUTH_FAILED_ERROR)
        }
    }

}