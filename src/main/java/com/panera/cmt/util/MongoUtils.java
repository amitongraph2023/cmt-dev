package com.panera.cmt.util;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MongoUtils {

    public static Date addToCurrentDate(TimeUnit unit, long amount) {
        return addToDate(new Date(), unit, amount);
    }

    public static Date addToDate(Date date, TimeUnit unit, long amount) {
        return new Date(date.getTime() + unit.toMillis(amount));
    }

    public static Date subtractFromCurrentDate(TimeUnit unit, long amount) {
        return subtractFromDate(new Date(), unit, amount);
    }

    public static Date subtractFromDate(Date date, TimeUnit unit, long amount) {
        return new Date(date.getTime() - unit.toMillis(amount));
    }
}
