package com.unix14.android.themoviedb.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

private const val TAG = "auth_interceptor"
class AuthInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        request = addApiKeyToRequest(request)

        return chain.proceed(request)
    }

    private fun addApiKeyToRequest(request: Request): Request {
        var req = request
        val apiKey = ApiSettings.API_KEY
        Log.d(TAG, "add api Key to Request: $apiKey")

        val originalHttpUrl = req.url()
        val url = originalHttpUrl.newBuilder().addQueryParameter("api_key", apiKey).build()

        val requestBuilder = req.newBuilder().url(url)
        req = requestBuilder.build()
        return req
    }
}