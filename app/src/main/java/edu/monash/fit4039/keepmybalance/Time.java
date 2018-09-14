package edu.monash.fit4039.keepmybalance;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by nathan on 16/5/17.
 */

public class Time {
    //date format received from server
    public static final String JSON_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZZZZZ";

    //transfer string to date
    public static Date toDate(String stringDate, String formatString)
    {
        SimpleDateFormat format= new SimpleDateFormat(formatString);
        Date date = null;
        try {
            date = format.parse(stringDate);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    //get current text date
    public static String getCurrentTextDate()
    {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int date = c.get(Calendar.DAY_OF_MONTH);
        return String.format("%02d", date) + "/" + String.format("%02d", month) + "/" + year;
    }

    //get current date
    public static Date getCurrentDate()
    {
        return toDate(getCurrentTextDate(), "dd/MM/yyyy");
    }

    //get current text time
    public static String getCurrentTextTime()
    {
        Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        return hour + ":" + minute;
    }

    //transfer date to string
    public static String toText(Date date, String formatString)
    {   SimpleDateFormat format = new SimpleDateFormat(formatString);
        String string = format.format(date);
        return string;
    }

    //get calendar year
    public static int getCalendarYear() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.YEAR);
    }

    //get calendar month (normal month - 1)
    public static int getCalendarMonth() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.MONTH);
    }

    //get calendar day
    public static int getCalendarDay() {
        Calendar c = Calendar.getInstance();
        return c.get(Calendar.DAY_OF_MONTH);
    }

    //get current date time
    public static Date getCurrentDateTime() {
        String textDateTime = getCurrentTextDate() + " " + getCurrentTextTime();
        return toDate(textDateTime, "dd/MM/yyyy' 'HH:mm");
    }
}

