package com.unix14.android.themoviedb.network

import android.content.SharedPreferences
import com.unix14.android.themoviedb.common.DateUtils
import java.util.*

class ApiSettings(private val sharedPreferences: SharedPreferences) {

    private val TOKEN_KEY = "token_key"
    private val SESSION_ID = "session_id"
    private val NEXT_EXPIRATION_DATE = "next_expiration_date"
    val API_KEY = "b56640566d2644075c08a4adc089b927"

    var requestToken: String?
        get() = sharedPreferences.getString(TOKEN_KEY, null)
        set(token) = sharedPreferences.edit().putString(TOKEN_KEY, token).apply()

    var sessionId: String?
        get() = sharedPreferences.getString(SESSION_ID, null)
        set(token) = sharedPreferences.edit().putString(SESSION_ID, token).apply()

    var lastExpirationDate: Date?
        get()= DateUtils.getDateFromString(sharedPreferences.getString(NEXT_EXPIRATION_DATE, null))
        set(date) = sharedPreferences.edit().putString(NEXT_EXPIRATION_DATE, DateUtils.formatDate(date)).apply()

    fun isValidUser(): Boolean {
        lastExpirationDate?.let {
            //we return a boolean statement checking if lastExpirationDate has passed
            return it.after(Date())
        }
        return false
    }
}
