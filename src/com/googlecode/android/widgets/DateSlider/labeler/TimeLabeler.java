package com.googlecode.android.widgets.DateSlider.labeler;

import java.util.Calendar;

import android.util.Log;

import com.googlecode.android.widgets.DateSlider.TimeObject;

/**
 * A Labeler that displays times in increments of {@value #MINUTEINTERVAL} minutes.
 */
public class TimeLabeler extends Labeler {
    public static int MINUTEINTERVAL = 15;

    private final String mFormatString;

    public TimeLabeler(String formatString) {
        super(80, 60);
        mFormatString = formatString;
    }

    @Override
    public TimeObject add(long time, int val) {
        return timeObjectfromCalendar(Util.addMinutes(time, val*MINUTEINTERVAL));
    }

    /**
     * override this method to set the inital TimeObject to a multiple of MINUTEINTERVAL
     */
    @Override
    public TimeObject getElem(long time) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(time);
        c.set(Calendar.MINUTE, c.get(Calendar.MINUTE)/MINUTEINTERVAL*MINUTEINTERVAL);
        Log.v("GETELEM","getelem: "+c.get(Calendar.MINUTE));
        return timeObjectfromCalendar(c);
    }

    @Override
    protected TimeObject timeObjectfromCalendar(Calendar c) {
        return Util.getTime(c, mFormatString, MINUTEINTERVAL);
    }
}