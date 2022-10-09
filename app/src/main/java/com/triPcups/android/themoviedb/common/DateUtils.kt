package com.triPcups.android.themoviedb.common

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
            val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US)
            val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US)

            val fromGmt = getConvertedUtcDate(date)

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

        fun getYear(date:Date?) : String {
            val cal = Calendar.getInstance()
            cal.time = date

            return cal.get(Calendar.YEAR).toString()
        }

    }
}