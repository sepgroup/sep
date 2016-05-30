package com.sepgroup.sep.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jeremybrown on 2016-05-22.
 */
public class DateUtils {
    /**
     * Makes Date object to the String format which is easier to put in database
     * @param input Date object that we want to cast to String
     * @return String format of Date
     */
    public static String castDateToString(Date input) {
        SimpleDateFormat temp = new SimpleDateFormat("yyyy-MM-dd");
        String date = temp.format(input);
        return date;
    }

    /**
     * Makes String to Date object
     * @param input this is a String format variable
     * @return Date object
     */
    public static Date castStringToDate(String input) throws ParseException {
        SimpleDateFormat temp = new SimpleDateFormat("yyyy-MM-dd");
        Date date=temp.parse(input);
        return date;
    }
}
