package com.unix14.android.themoviedb.models

import com.google.gson.annotations.SerializedName
import java.util.*

class AuthResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("expires_at") val expiresAt: Date,
    @SerializedName("request_token") val requestToken: String
)

class GuestAuthResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("expires_at") val expiresAt: Date,
    @SerializedName("guest_session_id") val guest_session_id: String
)

class MovieRatingResponse(
    @SerializedName("status_code") val statusCode: Int,
    @SerializedName("status_message") val statusMsg: String
)