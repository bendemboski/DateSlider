/*
 * Copyright (C) 2011 Daniel Berndt - Codeus Ltd  -  DateSlider
 * 
 * This is a small demo application which demonstrates the use of the
 * dateSelector
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

import com.googlecode.android.widgets.DateSlider.R;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * Small Demo activity which demonstrates the use of the DateSlideSelector 
 * 
 * @author Daniel Berndt - Codeus Ltd
 *
 */
public class Demo extends Activity {
    
static final int DEFAULTDATESELECTOR_ID = 0;
static final int ALTERNATIVEDATESELECTOR_ID = 1;
static final int CUSTOMDATESELECTOR_ID = 2;
static final int MONTHYEARDATESELECTOR_ID = 3;
static final int TIMESELECTOR_ID = 4;
static final int DATETIMESELECTOR_ID = 5;
	
	private TextView dateText;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	// load and initialise the Activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        dateText = (TextView) this.findViewById(R.id.selectedDateLabel);
        Button defaultButton = (Button) this.findViewById(R.id.defaultDateSelectButton);
        
        // set up a listener for when the button is pressed 
        defaultButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// call the internal showDialog method using the predefined ID
				showDialog(DEFAULTDATESELECTOR_ID);
			}        	
        });

        Button alternativeButton = (Button) this.findViewById(R.id.alternativeDateSelectButton);
        // set up a listener for when the button is pressed 
        alternativeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// call the internal showDialog method using the predefined ID
				showDialog(ALTERNATIVEDATESELECTOR_ID);
			}        	
        });

        Button customButton = (Button) this.findViewById(R.id.customDateSelectButton);
        // set up a listener for when the button is pressed 
        customButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// call the internal showDialog method using the predefined ID
				showDialog(CUSTOMDATESELECTOR_ID);
			}        	
        });

        Button monthYearButton = (Button) this.findViewById(R.id.monthYearDateSelectButton);        
        // set up a listener for when the button is pressed 
        monthYearButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// call the internal showDialog method using the predefined ID
				showDialog(MONTHYEARDATESELECTOR_ID);
			}        	
        });

        Button timeButton = (Button) this.findViewById(R.id.timeSelectButton);        
        // set up a listener for when the button is pressed 
        timeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// call the internal showDialog method using the predefined ID
				showDialog(TIMESELECTOR_ID);
			}        	
        });
        
        Button dateTimeButton = (Button) this.findViewById(R.id.dateTimeSelectButton);        
        // set up a listener for when the button is pressed 
        dateTimeButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// call the internal showDialog method using the predefined ID
				showDialog(DATETIMESELECTOR_ID);
			}        	
        });
    }

    // define the listener which is called once a user selected the date.
    private DateSlider.OnDateSetListener mDateSetListener =
        new DateSlider.OnDateSetListener() {
            public void onDateSet(DateSlider view, Calendar selectedDate) {
            	// update the dateText view with the corresponding date
                dateText.setText(String.format("The chosen date:%n%te. %tB %tY", selectedDate, selectedDate, selectedDate));
            }
    };    
    
    private DateSlider.OnDateSetListener mMonthYearSetListener =
        new DateSlider.OnDateSetListener() {
            public void onDateSet(DateSlider view, Calendar selectedDate) {
            	// update the dateText view with the corresponding date
                dateText.setText(String.format("The chosen date:%n%tB %tY", selectedDate, selectedDate));
            }
    };
    
    private DateSlider.OnDateSetListener mTimeSetListener =
        new DateSlider.OnDateSetListener() {
            public void onDateSet(DateSlider view, Calendar selectedDate) {
            	// update the dateText view with the corresponding date
                dateText.setText(String.format("The chosen time:%n%tR", selectedDate));
            }
    };

    private DateSlider.OnDateSetListener mDateTimeSetListener =
        new DateSlider.OnDateSetListener() {
            public void onDateSet(DateSlider view, Calendar selectedDate) {
            	// update the dateText view with the corresponding date
            	int minute = selectedDate.get(Calendar.MINUTE) / 
            			DateTimeSlider.MINUTEINTERVAL*DateTimeSlider.MINUTEINTERVAL;
                dateText.setText(String.format("The chosen date and time:%n%te. %tB %tY%n%tH:%02d",
                		selectedDate, selectedDate, selectedDate, selectedDate, minute));
            }
    };
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	// this method is called after invoking 'showDialog' for the first time
    	// here we initiate the corresponding DateSlideSelector and return the dialog to its caller

    	// get todays date and the time
        final Calendar c = Calendar.getInstance();
        switch (id) {
        case DEFAULTDATESELECTOR_ID:
            return new DefaultDateSlider(this,mDateSetListener,c);
        case ALTERNATIVEDATESELECTOR_ID:
            return new AlternativeDateSlider(this,mDateSetListener,c);
        case CUSTOMDATESELECTOR_ID:
            return new CustomDateSlider(this,mDateSetListener,c);
        case MONTHYEARDATESELECTOR_ID:
            return new MonthYearDateSlider(this,mMonthYearSetListener,c);
        case TIMESELECTOR_ID:
            return new TimeSlider(this,mTimeSetListener,c);
        case DATETIMESELECTOR_ID:
            return new DateTimeSlider(this,mDateTimeSetListener,c);
        }
        return null;
    }
}