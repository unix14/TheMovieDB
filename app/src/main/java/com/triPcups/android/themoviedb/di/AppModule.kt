package com.triPcups.android.themoviedb.di

import com.triPcups.android.themoviedb.features.MainViewModel
import com.triPcups.android.themoviedb.features.movie_details.MovieDetailsViewModel
import com.triPcups.android.themoviedb.features.movie_list.*
import com.triPcups.android.themoviedb.features.movie_list.SearchViewModel
import com.triPcups.android.themoviedb.features.splash.SplashViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    //Splash screen
    viewModel { SplashViewModel(get(), get()) }

    //Main screen
    viewModel { MainViewModel(get(), get()) }

    //Movie List screen
    viewModel { AllMoviesViewModel(get()) }

    //Movie Details screen
    viewModel { MovieDetailsViewModel(get(), get()) }

    //Rated Movies screen
    viewModel { RatedMoviesViewModel(get(), get()) }

    //Most rated Movies list
    viewModel { MostRatedMoviesViewModel(get())}

    //UpComing movies list
    viewModel { UpComingMoviesViewModel(get()) }

    //Popular movies list
    viewModel { PopularMoviesViewModel(get()) }

    //Search Dialog Fragment
    viewModel { SearchViewModel(get()) }
}