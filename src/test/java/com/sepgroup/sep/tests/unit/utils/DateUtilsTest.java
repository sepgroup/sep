package com.sepgroup.sep.tests.unit.utils;

import com.sepgroup.sep.utils.DateUtils;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by jeremybrown on 2016-08-08.
 */
public class DateUtilsTest {

    @Test
    public void testCastDateToStringNullInput() throws Exception {
        assertThat(DateUtils.castDateToString(null), equalTo(null));
    }

    @Test
    public void testFilterDateToMidnightNullInput() throws Exception {
        assertThat(DateUtils.filterDateToMidnight(null), equalTo(null));
    }
}
