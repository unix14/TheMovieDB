package com.unix14.android.themoviedb.di

import com.unix14.android.themoviedb.features.MainViewModel
import com.unix14.android.themoviedb.features.movie_list.MovieListViewModel
import com.unix14.android.themoviedb.features.sign_in.SignInViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    //Main ViewModel
    viewModel { MainViewModel(get(), get()) }

    //Movie List ViewModel
    viewModel { MovieListViewModel(get(), get()) }

    //Sign in screen
    viewModel { SignInViewModel(get(), get()) }

}