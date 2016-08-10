package com.sepgroup.sep.tests.ut.utils;

import com.sepgroup.sep.utils.DateUtils;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by jeremybrown on 2016-08-08.
 */
public class DateUtilsTest {

    @Test
    public void testCastDateToString() throws Exception {
        Date epochZero = new Date(0);
        String expected = "1969-12-31"; // because timezones
        String actual = DateUtils.castDateToString(epochZero);
        assertThat(actual, equalTo(expected));
    }

    @Test
    public void testCastDateToStringNullInput() throws Exception {
        assertThat(DateUtils.castDateToString(null), equalTo(null));
    }

    @Test
    public void testCastStringToDate() throws Exception {
        String date = "2016-10-20";
        Date actual = DateUtils.castStringToDate(date);
        Date expected = new Date(1476936000000L);
        assertThat(expected, equalTo(actual));
    }

    @Test
    public void testFilterDateToMidnight() throws Exception {
        Date epochZero = new Date(1470813421000L); // august 10th 03:01am
        Date expected = new Date(1470801600000L); // august 10th 00:00am
        Date actual = DateUtils.filterDateToMidnight(epochZero);

        assertThat(expected, equalTo(actual));
    }

    @Test
    public void testFilterDateToMidnightNullInput() throws Exception {
        assertThat(DateUtils.filterDateToMidnight(null), equalTo(null));
    }
}
