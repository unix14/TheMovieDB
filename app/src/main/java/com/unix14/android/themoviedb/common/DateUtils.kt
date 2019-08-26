package com.unix14.android.themoviedb.common

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class DateUtils {
    companion object {


        fun getDateFromString(dateStr: String?): Date? {
            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.getDefault())

            var date: Date? = null

            if (!dateStr.isNullOrEmpty()) {
                try {
                    date = formatter.parse(dateStr)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }

            }

            return date
        }

        fun formatDate(date: Date?): String {
            if(date == null){
                return ""
            }
            val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.getDefault())

            val fromGmt = getConvertedUtcDate(date)     //TODO :: Remove??

            var formattedTime = ""
            val expirationDate: Date

            try {
                expirationDate = inputFormat.parse(fromGmt.toString())
                formattedTime = outputFormat.format(expirationDate)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return formattedTime
        }

        private fun getConvertedUtcDate(date: Date): Date {
            val localTime = Date()

            return Date(date.time + TimeZone.getDefault().getOffset(localTime.time))
        }


    }
}