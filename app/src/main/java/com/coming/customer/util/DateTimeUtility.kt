package com.coming.customer.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by hlink21 on 31/8/17.
 */

object DateTimeUtility {

    const val UTCFormatNode = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    const val YYYYMMDD = "yyyy-MM-dd"

    fun from(y: Int, m: Int, d: Int): Date {
        val instance = Calendar.getInstance()
        instance.set(y, m, d, 0, 0, 0)
        return instance.time
    }

    fun from(h: Int, m: Int): Date {
        val instance = Calendar.getInstance()
        instance.set(Calendar.HOUR_OF_DAY, h)
        instance.set(Calendar.MINUTE, m)
        return instance.time
    }

    fun convert24To12(time: Date?): String? {
        return if (time == null) null else SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(time)
    }

    fun convert24To12_UTC(time: Date?): String? {
        if (time == null) return null
        val simpleDateFormat = SimpleDateFormat("hh : mm aa", Locale.getDefault())
        simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return simpleDateFormat.format(time)
    }

    fun formatDate(date: Date?): String? {
        return if (date == null) null else SimpleDateFormat("dd MMM, yyyy ", Locale.getDefault()).format(date)
    }

    fun formatDateInvoice(date: Date?): String? {
        return if (date == null) null else SimpleDateFormat("dd MMM yyyy ", Locale.getDefault()).format(date)
    }

    fun formatDateTransaction(date: Date?): String? {
        return if (date == null) null else SimpleDateFormat("dd-MM-yyyy ", Locale.getDefault()).format(date)
    }

    fun formatDateMonth(date: Date?): String? {
        return if (date == null) null else SimpleDateFormat("MMM dd, yyyy ", Locale.getDefault()).format(date)
    }

    fun formatDate(date: Date?, pattern: String): String? {
        return if (date == null) null else SimpleDateFormat(pattern, Locale.getDefault()).format(date)
    }

    fun formatDateTime(date: Date?): String? {
        return if (date == null) null else SimpleDateFormat("dd MMM, yyyy hh:mm aa", Locale.getDefault()).format(date)
    }

    fun format_UTC(date: Date, pattern: String): String {
        val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return simpleDateFormat.format(date)
    }

    fun format(date: Date, pattern: String): String {
        val simpleDateFormat = SimpleDateFormat(pattern, Locale.getDefault())
        return simpleDateFormat.format(date)
    }

    fun parseTime(time: String): Date? {
        try {
            val simpleDateFormat = SimpleDateFormat("HH:mm:ss")


            return simpleDateFormat.parse(time)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return null
    }

    fun parseDate(date: String): Date? {
        try {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")
            return simpleDateFormat.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return null
    }

    fun parseDateUTC(date: String, pattern: String): Date? {
        return try {
            val simpleDateFormat = SimpleDateFormat(pattern)
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            simpleDateFormat.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }

    }

    fun parseDate(date: String, pattern: String): Date? {
        return try {
            val simpleDateFormat = SimpleDateFormat(pattern)
            //simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            simpleDateFormat.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }

    }

    fun convertDateFormat(date: String, patternRequest: String, patternResponse: String): String {
        return try {
            if (date.isEmpty()) {
                date
            } else {
                val simpleDateFormat = SimpleDateFormat(patternRequest, Locale.getDefault())
                //simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                format(simpleDateFormat.parse(date), patternResponse)
            }
//            return simpleDateFormat.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
            date
        }

    }

    fun convertDateFormat(date: String, patternRequest: String, patternResponse: String, local: Locale): String {
        return try {

            if (date.isEmpty()) {
                date
            } else {
                val simpleDateFormat = SimpleDateFormat(patternRequest, local)
                //simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                format(simpleDateFormat.parse(date), patternResponse)
//            return simpleDateFormat.parse(date)
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            date
        }

    }

    fun convertDateFormatUTC(date: String, patternRequest: String, patternResponse: String): String {
        return try {
            if (date.isEmpty()) {
                date
            } else {
                val simpleDateFormat = SimpleDateFormat(patternRequest)
                simpleDateFormat.timeZone = TimeZone.getTimeZone("UTC")

                format(simpleDateFormat.parse(date), patternResponse)
//            return simpleDateFormat.parse(date)
            }
        } catch (e: ParseException) {
            e.printStackTrace()
            date
        }

    }

    fun getDayOfMonthSuffix(n: Int?): String {
        if (n == null) {
            return ""
        }

        if (n in 11..13) {
            return ""
        }

        val modResult = n % 10
        return when (modResult) {
            1 -> "st"
            2 -> "nd"
            3 -> "rd"
            else -> "th"
        }
    }

/*
    fun getDifferentBetweenTwoDate(context: Context, prev: Date, current: Date): String {
        val diff = current.time - prev.time
//        Log.e("currentTime: ", current.time.toString())
//        Log.e("timeFromServer: ", prev.time.toString())
//        Log.e("DiffBetweenTwoDate: ", diff.toString())
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        val week = days / 7
        val month = days / 30
        val year = days / 365

        if (year > 0) {
            if (year >= 2) {
                return year.toString() + " " + context.getString(R.string.Notification_Time_years_ago)

            } else if (year >= 1) {
                return year.toString() + " " + context.getString(R.string.Notification_Time_year_ago)

            }
        } else if (month > 0) {
            if (month >= 2) {
                return month.toString() + " " + context.getString(R.string.Notification_Time_months_ago)

            } else if (month >= 1) {
                return month.toString() + " " + context.getString(R.string.Notification_Time_month_ago)

            }
        } else if (week > 0) {
            if (week >= 2) {
                return week.toString() + " " + context.getString(R.string.Notification_Time_weeks_ago)

            } else if (week >= 1) {
                return week.toString() + " " + context.getString(R.string.Notification_Time_week_ago)

            }
        } else if (days > 0) {
            if (days >= 2) {
                return days.toString() + " " + context.getString(R.string.Notification_Time_days_ago)

            } else if (days >= 1) {
                return days.toString() + " " + context.getString(R.string.Notification_Time_day_ago)

            }
        } else if (hours > 0) {
            if (hours >= 2) {
                return hours.toString() + " " + context.getString(R.string.Notification_Time_hours_ago)

            } else if (hours >= 1) {
                return hours.toString() + " " + context.getString(R.string.Notification_Time_hour_ago)

            }
        } else if (minutes > 0) {
            if (minutes >= 2) {
                return minutes.toString() + " " + context.getString(R.string.Notification_Time_minutes_ago)

            } else if (minutes >= 1) {
                return minutes.toString() + " " + context.getString(R.string.Notification_Time_minute_ago)

            }
        } else if (seconds >= 0) {
            return if (seconds >= 3) {
                seconds.toString() + " " + context.getString(R.string.Notification_Time_seconds_ago)
            } else {
                context.getString(R.string.Notification_Time_just_now)
            }
        }
        return ""
    }
*/

    object DateFormat {
        const val DD_MM_YYYY = "dd-MM-yyyy"
        const val YYYY_MM_DD = "yyyy-MM-dd"
        const val DD_MMM_YYYY = "dd MMM, yyyy"
        const val DD_MMMM_YYYY = "dd MMMM, yyyy"
        const val FULL_DATE = "yyyy-MM-dd HH:mm:ss"
        const val DD_MMM_YYYY_HH_MM = "dd MMM, yyyy-hh:mm a"
        const val DAY = "EEEE"
    }

}
