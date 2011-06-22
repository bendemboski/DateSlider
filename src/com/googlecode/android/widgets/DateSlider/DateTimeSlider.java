/*
 * Copyright (C) 2011 Daniel Berndt - Codeus Ltd  -  DateSlider
 *
 * DateSlider which allows for selecting of a date including a time
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

import android.content.Context;

import com.googlecode.android.widgets.DateSlider.labeler.TimeLabeler;

public class DateTimeSlider extends DateSlider {

	public DateTimeSlider(Context context, OnDateSetListener l,	Calendar calendar) {
		this(context, l, calendar, null, null);
	}
	
    public DateTimeSlider(Context context, OnDateSetListener l,	Calendar calendar, 
    		Calendar minDate, Calendar maxDate) {
        super(context, R.layout.datetimeslider, l, calendar, minDate, maxDate);
    }

    @Override
    protected void setTitle() {
        if (mTitleText != null) {
            final Calendar c = getTime();
            int minute = c.get(Calendar.MINUTE)/TimeLabeler.MINUTEINTERVAL*TimeLabeler.MINUTEINTERVAL;
            mTitleText.setText(String.format("Selected DateTime: %te/%tm/%ty %tH:%02d",
                    c,c,c,c,minute));
        }
    }
}
