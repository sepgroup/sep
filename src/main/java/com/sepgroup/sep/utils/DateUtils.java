package com.sepgroup.sep.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jeremybrown on 2016-05-22.
 */
public class DateUtils {

    private static Logger logger = LoggerFactory.getLogger(DateUtils.class);

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
        Date date = null;
        if (input != null && input != "") {
            SimpleDateFormat temp = new SimpleDateFormat("yyyy-MM-dd");
            date = temp.parse(input);
        }
        else {
            logger.debug("Inputed null or empty date, leaving as null");
        }
        return date;
    }

    public static Date filterDateToMidnight(Date date) {
        Date newDate = null;
        if (date != null) {
            DateFormat justDay = new SimpleDateFormat("yyyy-MM-dd");
            try {
                newDate = justDay.parse(justDay.format(date));
            } catch (ParseException e) {
                logger.error("Parse exception on Date object generated by Java. This really shouldn't happen. " +
                        "Deadline will be left as null.");
            }
        }
        return newDate;
    }
}
