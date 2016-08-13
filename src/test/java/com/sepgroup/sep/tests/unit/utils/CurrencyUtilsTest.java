package com.sepgroup.sep.tests.unit.utils;

import com.sepgroup.sep.utils.CurrencyUtils;
import org.junit.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by jeremybrown on 2016-08-08.
 */
public class CurrencyUtilsTest {
    @Test
    public void testRound() throws Exception {
        double input = 2354.395054;
        double actual = CurrencyUtils.round(input, 4);

        double expected = 2354.3951;
        assertThat(expected, equalTo(actual));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRoundZeroPlaces() throws Exception {
        CurrencyUtils.round(23.0394, -1);
    }

    @Test
    public void testRoundToTwoDecimals() throws Exception {
        double input = 2354.395054;
        double actual = CurrencyUtils.roundToTwoDecimals(input);

        double expected = 2354.40;
        assertThat(expected, equalTo(actual));
    }
}
