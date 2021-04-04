package com.coming.customer.util;


import android.text.format.DateUtils;
import android.util.Log;

import com.coming.customer.core.Common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


/**
 * Created by  on 2/7/16.
 */
public class TimeConvertUtils {

    /**
     * @param timeConvert: Time to be Converted
     * @param input:       Input format
     * @param output:      Output format
     * @return formated date time
     */
    public static String dateTimeConvertServertToLocal(String timeConvert, String input, String output) {

        //  DebugLog.e("Convert Time" + timeConvert);

        DateFormat timeFormater = new SimpleDateFormat(input, Locale.US);

        try {

            Date time;
            timeFormater.setTimeZone(TimeZone.getDefault());

            // if (URLFactory.isLocal)
            timeFormater.setTimeZone(TimeZone.getTimeZone(Common.SERVER_TIMEZONE));


            time = timeFormater.parse(timeConvert);

            DateFormat timeFormaterSecond = new SimpleDateFormat(output, Locale.US); //HH for hour of the day (0 - 23)

            timeFormaterSecond.setTimeZone(TimeZone.getDefault());


            return timeFormaterSecond.format(time);


        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("", e.getMessage());
        }

        return "";
    }

    public static String dateTimeConvertLocalToLocalDisplay(String timeConvert, String input, String output) {

        DateFormat timeFormater = new SimpleDateFormat(input, Locale.US);

        try {

            Date time;

            timeFormater.setTimeZone(TimeZone.getDefault());

            time = timeFormater.parse(timeConvert);

            DateFormat timeFormaterSecond = new SimpleDateFormat(output, Locale.US);

            return timeFormaterSecond.format(time);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String dateTimeConvertLocalToServerOrg(String timeConvert, String input, String output) {
  /*      //This code for only local to local

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
*/
        //Change code for local to server

        DateFormat timeFormater = new SimpleDateFormat(input, Locale.US);

        try {

            Date time;

            timeFormater.setTimeZone(TimeZone.getDefault());

            time = timeFormater.parse(timeConvert);

            DateFormat timeFormaterSecond = new SimpleDateFormat(output, Locale.US);

            timeFormaterSecond.setTimeZone(TimeZone.getTimeZone(Common.SERVER_TIMEZONE_UTC));

            return timeFormaterSecond.format(time);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }


    /**
     * @param timeConvert: Time to be Converted
     * @param input:       Input format
     * @param output:      Output format
     * @return formated date time
     */
    public static String dateTimeConvertLocalToServer(String timeConvert, String input, String output) {


        DateFormat timeFormater = new SimpleDateFormat(input);

        try {

            Date time;

            timeFormater.setTimeZone(TimeZone.getDefault());

            time = timeFormater.parse(timeConvert);

            DateFormat timeFormaterSecond = new SimpleDateFormat(output);

            timeFormaterSecond.setTimeZone(TimeZone.getTimeZone(Common.SERVER_TIMEZONE));

            Log.e("", timeFormaterSecond.format(time));

            return timeFormaterSecond.format(time);


        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("", e.getMessage());
        }

        return "";
    }

    public static String dateTimeConvertServertToLocalOrg(String timeConvert, String input, String output) {


        //  DebugLog.e("Convert Time" + timeConvert);

        DateFormat timeFormater = new SimpleDateFormat(input, Locale.US);

        try {

            Date time;


            timeFormater.setTimeZone(TimeZone.getTimeZone(Common.SERVER_TIMEZONE_UTC));


            time = timeFormater.parse(timeConvert);

            DateFormat timeFormaterSecond = new SimpleDateFormat(output, Locale.US); //HH for hour of the day (0 - 23)

            timeFormaterSecond.setTimeZone(TimeZone.getDefault());


            return timeFormaterSecond.format(time);


        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * @param timeConvert: Time to be Converted
     * @param input:       Input format
     * @param output:      Output format
     * @return formated date time
     */
    public static String dateTimeConvertLocalToLocal(String timeConvert, String input, String output) {


        DateFormat timeFormater = new SimpleDateFormat(input, Locale.US);

        try {

            Date time;

            timeFormater.setTimeZone(TimeZone.getDefault());

            time = timeFormater.parse(timeConvert);

            DateFormat timeFormaterSecond = new SimpleDateFormat(output, Locale.US);


            Log.e("++++++++", timeFormaterSecond.format(time));

            return timeFormaterSecond.format(time);


        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("", e.getMessage());
        }

        return "";
    }


    /**
     * @param convertDateTime: Time to be Converted
     * @param convertFormate:  Input format
     * @return formated date time
     */
    public static String dateAndTimeGet(String convertDateTime, String convertFormate) {
        String time = "";
        try {
            long now = System.currentTimeMillis();
            String datetime1 = convertDateTime;
            SimpleDateFormat dateFormat = new SimpleDateFormat(convertFormate, Locale.US);
            Date convertedDate = dateFormat.parse(datetime1);

            CharSequence relavetime1 = DateUtils.getRelativeTimeSpanString(
                    convertedDate.getTime(),
                    now,
                    DateUtils.SECOND_IN_MILLIS);
            time = "" + relavetime1;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

}