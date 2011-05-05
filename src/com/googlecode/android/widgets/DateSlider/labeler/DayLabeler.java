package com.googlecode.android.widgets.DateSlider.labeler;

import java.util.Calendar;

import com.googlecode.android.widgets.DateSlider.TimeObject;

/**
 * A Labeler that displays days
 */
public class DayLabeler extends Labeler {
    private final String mFormatString;

    public DayLabeler(String formatString) {
        super(150, 60);
        mFormatString = formatString;
    }

    @Override
    public TimeObject add(long time, int val) {
        return timeObjectfromCalendar(Util.addDays(time, val));
    }

    @Override
    protected TimeObject timeObjectfromCalendar(Calendar c) {
        return Util.getDay(c, mFormatString);
    }
}