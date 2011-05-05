/*
 * Copyright (C) 2011 Daniel Berndt - Codeus Ltd  -  DateSlider
 *
 * Class for setting up the dialog and initialsing the underlying
 * ScrollLayouts
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.googlecode.android.widgets.DateSlider;

import java.util.Calendar;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.googlecode.android.widgets.DateSlider.SliderContainer.OnTimeChangeListener;

/**
 * A Dialog subclass that hosts a SliderContainer and a couple of buttons,
 * displays the current time in the header, and notifies an observer
 * when the user selectes a time.
 */
public class DateSlider extends Dialog {

//	private static String TAG = "DATESLIDER";

    protected OnDateSetListener onDateSetListener;
    protected Calendar mTime;
    protected int mLayoutID;
    protected TextView mTitleText;
    protected SliderContainer mContainer;

    public DateSlider(Context context, int layoutID, OnDateSetListener l,
            Calendar calendar) {
        super(context);
        this.onDateSetListener = l;
        mTime = Calendar.getInstance();
        mTime.setTimeInMillis(calendar.getTimeInMillis());
        mLayoutID = layoutID;
    }

    /**
     * Set up the dialog with all the views and their listeners
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState!=null) {
            long time = savedInstanceState.getLong("time", mTime.getTimeInMillis());
            mTime.setTimeInMillis(time);
        }

        this.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(mLayoutID);
        this.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.dialogtitle);

        mTitleText = (TextView) this.findViewById(R.id.dateSliderTitleText);
        mContainer = (SliderContainer) this.findViewById(R.id.dateSliderContainer);

        mContainer.setOnTimeChangeListener(onTimeChangeListener);

        Button okButton = (Button) findViewById(R.id.dateSliderOkButton);
        okButton.setOnClickListener(okButtonClickListener);

        Button cancelButton = (Button) findViewById(R.id.dateSliderCancelButton);
        cancelButton.setOnClickListener(cancelButtonClickListener);
    }

    private android.view.View.OnClickListener okButtonClickListener = new android.view.View.OnClickListener() {
        public void onClick(View v) {
            if (onDateSetListener!=null)
                onDateSetListener.onDateSet(DateSlider.this, mTime);
            dismiss();
        }
    };

    private android.view.View.OnClickListener cancelButtonClickListener = new android.view.View.OnClickListener() {
        public void onClick(View v) {
            dismiss();
        }
    };

    private OnTimeChangeListener onTimeChangeListener = new OnTimeChangeListener() {
        @Override
        public void onTimeChange(Calendar time) {
            mTime.setTimeInMillis(time.getTimeInMillis());
            setTitle();
        }
    };

    @Override
    public Bundle onSaveInstanceState() {
        Bundle savedInstanceState = super.onSaveInstanceState();
        if (savedInstanceState==null) savedInstanceState = new Bundle();
        savedInstanceState.putLong("time", mTime.getTimeInMillis());
        return savedInstanceState;
    }

    /**
     * This method sets the title of the dialog
     */
    protected void setTitle() {
        if (mTitleText != null) {
            mTitleText.setText(getContext().getString(R.string.dateSliderTitle) +
                    String.format(": %te. %tB %tY", mTime,mTime,mTime));
        }
    }


    /**
     * Defines the interface which defines the methods of the OnDateSetListener
     */
    public interface OnDateSetListener {
        /**
         * this method is called when a date was selected by the user
         * @param view			the caller of the method
         *
         */
        public void onDateSet(DateSlider view, Calendar selectedDate);
    }
}
