package com.unix14.android.themoviedb.network

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.unix14.android.themoviedb.common.DateUtils
import com.unix14.android.themoviedb.models.Genre
import com.unix14.android.themoviedb.models.Language
import com.unix14.android.themoviedb.models.Movie
import java.util.*


class ApiSettings(private val sharedPreferences: SharedPreferences) {

    companion object {
        private const val TOKEN_KEY = "token_key"
        private const val NEXT_EXPIRATION_DATE = "next_expiration_date"
        private const val RATED_MOVIE_LIST = "rated_movie_list"
        private val LANGUAGES_LIST = "languages_list"
        private const val GENRES_LIST = "genres_list"
        const val API_KEY = "b56640566d2644075c08a4adc089b927"
    }

    val gson = Gson()

    var guestSessionId: String
        get() = sharedPreferences.getString(TOKEN_KEY, "") as String
        set(token) = sharedPreferences.edit().putString(TOKEN_KEY, token).apply()

    var lastExpirationDate: Date?
        get() = DateUtils.getDateFromString(sharedPreferences.getString(NEXT_EXPIRATION_DATE, null))
        set(date) = sharedPreferences.edit().putString(NEXT_EXPIRATION_DATE, DateUtils.formatDate(date)).apply()

    fun isValidUser(): Boolean {
        lastExpirationDate?.let {
            //we return a boolean statement checking if lastExpirationDate has passed
            return it.after(Date())
        }
        return false
    }


    fun <T> setRatedMovieList(list: List<T>) {
        val json = gson.toJson(list)
        sharedPreferences.edit().putString(RATED_MOVIE_LIST, json).apply()
    }

    fun getRatedMovieList(): ArrayList<Movie> {
        val jsonPreferences = sharedPreferences.getString(RATED_MOVIE_LIST, "")
        val movieList: ArrayList<Movie> = ArrayList()

        val type = object : TypeToken<List<Movie>>() {}.type
        val json = gson.fromJson<List<Movie>>(jsonPreferences, type)
        if(json != null && json.isNotEmpty()){
            movieList.addAll(json)
        }

        return movieList
    }

    fun <T> setLanguageList(list: List<T>) {
        val json = gson.toJson(list)
        sharedPreferences.edit().putString(LANGUAGES_LIST, json).apply()
    }

    fun getLanguageList(): ArrayList<Language> {
        val jsonPreferences = sharedPreferences.getString(LANGUAGES_LIST, "")
        val langList: ArrayList<Language> = ArrayList()

        val type = object : TypeToken<List<Language>>() {}.type
        val json = gson.fromJson<List<Language>>(jsonPreferences, type)
        if(json != null && json.isNotEmpty()) {
            langList.addAll(json)
        }
        return langList
    }

    fun <T> setGenresList(list: List<T>) {
        val json = gson.toJson(list)
        sharedPreferences.edit().putString(GENRES_LIST, json).apply()
    }

    fun getGenresList(): ArrayList<Genre> {
        val jsonPreferences = sharedPreferences.getString(GENRES_LIST, "")
        val langList: ArrayList<Genre> = ArrayList()

        val type = object : TypeToken<List<Genre>>() {}.type
        langList.addAll(gson.fromJson<List<Genre>>(jsonPreferences, type))

        return langList
    }
}
