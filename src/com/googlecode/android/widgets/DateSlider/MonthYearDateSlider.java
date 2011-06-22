/*
 * Copyright (C) 2011 Daniel Berndt - Codeus Ltd  -  DateSlider
 *
 * DateSlider which allows for an easy selection of only a month and a year
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

public class MonthYearDateSlider extends DateSlider {

	public MonthYearDateSlider(Context context, OnDateSetListener l, Calendar calendar) {
		this(context, l, calendar, null, null);
	}
    public MonthYearDateSlider(Context context, OnDateSetListener l, Calendar calendar, 
    		Calendar minDate, Calendar maxDate) {
        super(context, R.layout.monthyeardateslider, l, calendar, minDate, maxDate);
    }

    /**
     * override the setTitle method so that only the month and the year are shown.
     */
    @Override
    protected void setTitle() {
        if (mTitleText != null) {
            final Calendar c = getTime();
            mTitleText.setText(getContext().getString(R.string.dateSliderTitle) +
                    String.format(": %tB %tY",c,c));
        }
    }

}
