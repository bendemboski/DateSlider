package com.googlecode.android.widgets.DateSlider.timeview;

import java.util.Calendar;

import android.content.Context;

import com.googlecode.android.widgets.DateSlider.TimeObject;

/**
 * This is a subclass of the TimeLayoutView that represents a day. It uses
 * a different color to distinguish Sundays from other days.
 */
public class DayTimeLayoutView extends TimeLayoutView {

    protected boolean isSunday=false;

    /**
     * Constructor
     * @param context
     * @param isCenterView true if the element is the centered view in the ScrollLayout
     * @param topTextSize	text size of the top TextView in dps
     * @param bottomTextSize	text size of the bottom TextView in dps
     * @param lineHeight	LineHeight of the top TextView
     */
    public DayTimeLayoutView(Context context, boolean isCenterView,
            int topTextSize, int bottomTextSize, float lineHeight) {
        super(context, isCenterView, topTextSize, bottomTextSize, lineHeight);
    }

    @Override
    public void setVals(TimeObject to) {
        super.setVals(to);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(to.endTime);
        if (c.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY && !isSunday) {
            isSunday=true;
            colorMeSunday();
        } else if (isSunday && c.get(Calendar.DAY_OF_WEEK)!=Calendar.SUNDAY) {
            isSunday=false;
            colorMeWorkday();
        }
    }

    /**
     * this method is called when the current View takes a Sunday as time unit
     */
    protected void colorMeSunday() {
    	if (isOutOfBounds) return;
        if (isCenter) {
            bottomView.setTextColor(0xFF773333);
            topView.setTextColor(0xFF553333);
        }
        else {
            bottomView.setTextColor(0xFF442222);
            topView.setTextColor(0xFF553333);
        }
    }


    /**
     * this method is called when the current View takes no Sunday as time unit
     */
    protected void colorMeWorkday() {
    	if (isOutOfBounds) return;
        if (isCenter) {
            topView.setTextColor(0xFF333333);
            bottomView.setTextColor(0xFF444444);
        } else {
            topView.setTextColor(0xFF666666);
            bottomView.setTextColor(0xFF666666);
        }
    }

    @Override
    public void setVals(TimeView other) {
        super.setVals(other);
        DayTimeLayoutView otherDay = (DayTimeLayoutView) other;
        if (otherDay.isSunday && !isSunday) {
            isSunday = true;
            colorMeSunday();
        } else if (isSunday && !otherDay.isSunday) {
            isSunday = false;
            colorMeWorkday();
        }
    }

}