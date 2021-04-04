package com.coming.customer.util;

import android.text.format.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Created by Vishwa on 30/11/17.
 */

public class DateTimeFormatter {

    private static Date date;
    private TimeZone outTimeZone = TimeZone.getDefault();

    private DateTimeFormatter() {

    }

    /**
     * @param date :- Date as String object , Please also provide inFormat if not in format of "yyyy-MM-dd hh:mm:ss"
     * @return class Object
     */
    public static DateTimeFormatter date(String date, String pattern) {
        DateFormat timeFormater = new SimpleDateFormat(pattern, Locale.US);
        timeFormater.setTimeZone(TimeZone.getDefault());
        try {
            DateTimeFormatter.date = timeFormater.parse(date);
        } catch (ParseException e) {
            DateTimeFormatter.date = null;
        }
        return new DateTimeFormatter();
    }

    /**
     * @param date :- Date as Date object
     * @return class Object
     */
    public static DateTimeFormatter date(Date date) {
        DateTimeFormatter.date = date;
        return new DateTimeFormatter();
    }

    public static String dateAndTimeGet(String convertDateTime, String convertFormate) {
        String time = "";
        try {
            long now = System.currentTimeMillis();
            String datetime1 = convertDateTime;
            SimpleDateFormat dateFormat = new SimpleDateFormat(convertFormate);
            Date convertedDate = dateFormat.parse(datetime1);
            CharSequence relavetime1 = DateUtils.getRelativeTimeSpanString(convertedDate.getTime(), now, DateUtils.SECOND_IN_MILLIS);
            time = "" + relavetime1;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static String dateTimeConvertLocalToLocal(String timeConvert, String input, String output) {
        DateFormat timeFormater = new SimpleDateFormat(input);
        try {
            Date time;
            timeFormater.setTimeZone(TimeZone.getDefault());
            time = timeFormater.parse(timeConvert);
            DateFormat timeFormaterSecond = new SimpleDateFormat(output);
            return timeFormaterSecond.format(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String ordinal_suffix_of(String a) {

        int i;
        try {
            i = Integer.parseInt(a);
        } catch (Exception e) {
            return a;
        }

        int j = i % 10, k = i % 100;

        if (j == 1 && k != 11) {
            return i + "<sup><small>st</small></sup>";/*"st";*/
        }
        if (j == 2 && k != 12) {
            return i + "<sup><small>nd</small></sup>";/*"nd";*/
        }
        if (j == 3 && k != 13) {
            return i + "<sup><small>rd</small></sup>";/*"rd";*/
        }

        return i + "<sup><small>th</small></sup>";/*"th";*/

    }

    public DateTimeFormatter timeZoneToConvert(String timezone) {
        outTimeZone = TimeZone.getTimeZone(timezone);
        return this;
    }

    public DateTimeFormatter timeZoneToConvert(TimeZone timezone) {
        outTimeZone = timezone;
        return this;
    }

    public String formatDateToLocalTimeZone(String format) {
        return new SimpleDateFormat(format, Locale.US).format(date);
    }

    public String formatDateToTimeZone(String format) {
        TimeZone utc = outTimeZone;
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.US);
        formatter.setTimeZone(utc);
        return formatter.format(date);
    }

    public String timeIn12Hoursformat() {
        TimeZone utc = outTimeZone;
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.US);
        formatter.setTimeZone(utc);
        return formatter.format(date);
    }

    public String timeIn24HoursformatWithoutAmPm() {
        TimeZone utc = outTimeZone;
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.US);
        formatter.setTimeZone(utc);
        return formatter.format(date);
    }

    public String timeIn24Hoursformat() {
        TimeZone utc = outTimeZone;
        SimpleDateFormat formatter = new SimpleDateFormat("kk:mm", Locale.US);
        formatter.setTimeZone(utc);
        return formatter.format(date);
    }
}