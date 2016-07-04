package com.sepgroup.sep.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by jeremybrown on 2016-07-02.
 */
public class CurrencyUtils {

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double roundToTwoDecimals(double value) {
        return round(value, 2);
    }
}
