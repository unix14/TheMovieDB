package com.unix14.android.themoviedb.di

import com.unix14.android.themoviedb.features.MainViewModel
import com.unix14.android.themoviedb.features.movie_details.MovieDetailsViewModel
import com.unix14.android.themoviedb.features.movie_list.AllMoviesViewModel
import com.unix14.android.themoviedb.features.movie_list.RatedMoviesViewModel
import com.unix14.android.themoviedb.features.sign_in.SignInViewModel
import com.unix14.android.themoviedb.features.splash.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    //Splash screen
    viewModel { SplashViewModel(get(), get()) }

    //Main screen
    viewModel { MainViewModel(get(), get()) }

    //Movie List screen
    viewModel { AllMoviesViewModel(get()) }

    //Sign in screen
    viewModel { SignInViewModel(get(), get()) }

    //Movie Details screen
    viewModel { MovieDetailsViewModel(get(), get()) }

    //Rated Movies screen
    viewModel { RatedMoviesViewModel(get(), get()) }

}