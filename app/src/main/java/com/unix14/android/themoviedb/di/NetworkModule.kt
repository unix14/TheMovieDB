package com.unix14.android.themoviedb.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.unix14.android.themoviedb.network.ApiService
import com.unix14.android.themoviedb.network.ApiSettings
import com.unix14.android.themoviedb.network.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


val networkModule = module {

    single { provideSharedPreferences(get()) }
    factory { ApiSettings(get()) }

    single { provideDefaultOkHttpClient() }
    single { provideRetrofit(get()) }
    single { provideApiService(get()) }
}

const val BASE_SERVER_URL = "https://api.themoviedb.org/3/"

fun provideSharedPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences("User", Context.MODE_PRIVATE)
}

fun provideDefaultOkHttpClient(): OkHttpClient {
    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.BODY

    val authInterceptor = AuthInterceptor()

    val httpClient = OkHttpClient.Builder().addInterceptor(logging).addInterceptor(authInterceptor)
    return httpClient.build()
}

fun provideRetrofit(client: OkHttpClient): Retrofit {

    val gson = GsonBuilder()
        .setDateFormat("yyyy-MM-dd HH:mm:ss Z")
        .create()

    return Retrofit.Builder()
        .baseUrl(BASE_SERVER_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
}

fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)
