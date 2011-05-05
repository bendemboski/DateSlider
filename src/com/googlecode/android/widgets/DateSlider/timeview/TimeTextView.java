package com.googlecode.android.widgets.DateSlider.timeview;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;

import com.googlecode.android.widgets.DateSlider.TimeObject;

/**
 * This is a simple implementation of a TimeView which is implemented
 * as a TextView. It is aware of whether or not it is the center view
 * in the ScrollLayout so that it can alter its appearance to indicate
 * that it is currently selected.
 */
public class TimeTextView extends TextView implements TimeView {
    private long endTime, startTime;

    /**
     * constructor
     * @param context
     * @param isCenterView true if the element is the centered view in the ScrollLayout
     * @param textSize text size in dps
     */
    public TimeTextView(Context context, boolean isCenterView, int textSize) {
        super(context);
        setupView(isCenterView, textSize);
    }

    /**
     * this method should be overwritten by inheriting classes to define its own look and feel
     * @param isCenterView true if the element is in the center of the scrollLayout
     * @param textSize textSize in dps
     */
    protected void setupView(boolean isCenterView, int textSize) {
        setGravity(Gravity.CENTER);
        setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
        if (isCenterView) {
            setTypeface(Typeface.DEFAULT_BOLD);
            setTextColor(0xFF333333);
        } else {
            setTextColor(0xFF666666);
        }
    }

    @Override
    public void setVals(TimeObject to) {
        setText(to.text);
        this.startTime = to.startTime;
        this.endTime = to.endTime;
    }

    @Override
    public void setVals(TimeView other) {
        setText(other.getTimeText());
        startTime = other.getStartTime();
        endTime = other.getEndTime();
    }

    @Override
    public long getStartTime() {
        return this.startTime;
    }

    @Override
    public long getEndTime() {
        return this.endTime;
    }

    @Override
    public String getTimeText() {
        return getText().toString();
    }
}