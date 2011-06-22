package com.googlecode.android.widgets.DateSlider.labeler;

import java.util.Calendar;

import com.googlecode.android.widgets.DateSlider.TimeObject;

/**
 * A bunch of static helpers for manipulating dates and times. There are two
 * types of methods -- add*() methods that add a number of units to a time
 * and return the result as a Calendar, and get*() objects that take a
 * Calendar object and a format string and produce the appropriate TimeObject.
 */
class Util {
    public static Calendar addYears(long time, int years) {
        return add(time, years, Calendar.YEAR);
    }

    public static Calendar addMonths(long time, int months) {
        return add(time, months, Calendar.MONTH);
    }

    public static Calendar addWeeks(long time, int days) {
        return add(time, days, Calendar.WEEK_OF_YEAR);
    }

    public static Calendar addDays(long time, int days) {
        return add(time, days, Calendar.DAY_OF_MONTH);
    }

    public static Calendar addHours(long time, int hours) {
        return add(time, hours, Calendar.HOUR_OF_DAY);
    }

    public static Calendar addMinutes(long time, int minutes) {
        return add(time, minutes, Calendar.MINUTE);
    }
    
    public static Calendar addMinutes(long time, int minutes, int minInterval) {
        return add(time, minutes*minInterval, Calendar.MINUTE);
    }

    public static TimeObject getYear(Calendar c, String formatString) {
        int year = c.get(Calendar.YEAR);
        // set calendar to first millisecond of the year
        c.set(year, 0, 1, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        long startTime = c.getTimeInMillis();
        // set calendar to last millisecond of the year
        c.set(year, 11, 31, 23, 59, 59);
        c.set(Calendar.MILLISECOND, 999);
        long endTime = c.getTimeInMillis();
        return new TimeObject(String.format(formatString, c,c), startTime, endTime);
    }

    public static TimeObject getMonth(Calendar c, String formatString) {
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        // set calendar to first millisecond of the month
        c.set(year, month, 1, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        long startTime = c.getTimeInMillis();
        // set calendar to last millisecond of the month
        c.set(year, month, c.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);
        c.set(Calendar.MILLISECOND, 999);
        long endTime = c.getTimeInMillis();
        return new TimeObject(String.format(formatString,c,c), startTime, endTime);
    }

    public static TimeObject getDay(Calendar c, String formatString) {
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        // set calendar to first millisecond of the day
        c.set(year, month, day, 0, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        long startTime = c.getTimeInMillis();
        // set calendar to last millisecond of the day
        c.set(year, month, day, 23, 59, 59);
        c.set(Calendar.MILLISECOND, 999);
        long endTime = c.getTimeInMillis();
        return new TimeObject(String.format(formatString,c,c), startTime, endTime);
    }

    public static TimeObject getHour(Calendar c, String formatString) {
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        // get the first millisecond of that hour
        c.set(year, month, day, hour, 0, 0);
        c.set(Calendar.MILLISECOND, 0);
        long startTime = c.getTimeInMillis();
        // get the last millisecond of that hour
        c.set(year, month, day, hour, 59, 59);
        c.set(Calendar.MILLISECOND, 999);
        long endTime = c.getTimeInMillis();
        return new TimeObject(String.format(formatString, c,c), startTime, endTime);
    }

    public static TimeObject getMinute(Calendar c, String formatString) {
    	return getMinute(c, formatString, 1);
    }
    
    public static TimeObject getMinute(Calendar c, String formatString, int minInterval) {
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        // get the last millisecond of that minute interval (cap it at 59)
        c.set(year, month, day, hour, Math.min(59,minute+minInterval-1), 59);
        c.set(Calendar.MILLISECOND, 999);
        long endTime = c.getTimeInMillis();
        // get the first millisecond of that minute interval
        c.set(year, month, day, hour, minute, 0);
        c.set(Calendar.MILLISECOND, 0);
        long startTime = c.getTimeInMillis();
        return new TimeObject(String.format(formatString, c,c), startTime, endTime);
    }

    public static TimeObject getTime(Calendar c, String formatString, int minuteInterval) {
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE)/minuteInterval*minuteInterval;
        // get the last millisecond of that 15 minute block
        c.set(year, month, day, hour, minute+minuteInterval-1, 59);
        c.set(Calendar.MILLISECOND, 999);
        long endTime = c.getTimeInMillis();
        // get the first millisecond of that 15 minute block
        c.set(year, month, day, hour, minute, 0);
        c.set(Calendar.MILLISECOND, 0);
        long startTime = c.getTimeInMillis();
        return new TimeObject(String.format(formatString, c,c), startTime, endTime);
    }

    private static Calendar add(long time, int val, int field) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        c.add(field, val);
        return c;
    }
}
