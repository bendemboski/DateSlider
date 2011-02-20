/*
 * Copyright (C) 2011 Daniel Berndt - Codeus Ltd  -  DateSlider
 * 
 * This is a simple demo application which demonstrates the use of the
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
 * Simple Demo activity which demonstrates how to implement one DateSlider 
 *
 */
public class SimpleDemo extends Activity {
    
static final int DEFAULTDATESELECTOR_ID = 0;
	
	private TextView dateText;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	// load and initialise the Activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simplemain);
        dateText = (TextView) this.findViewById(R.id.selectedDateLabel);
        Button defaultButton = (Button) this.findViewById(R.id.defaultDateSelectButton);
        
        // set up a listener for when the button is pressed 
        defaultButton.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// call the internal showDialog method using the predefined ID
				showDialog(DEFAULTDATESELECTOR_ID);
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
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	// this method is called after invoking 'showDialog' for the first time
    	// here we initiate the corresponding DateSlideSelector and return the dialog to its caller

    	// get todays date and the time
        final Calendar c = Calendar.getInstance();
        switch (id) {
        case DEFAULTDATESELECTOR_ID:
            return new DefaultDateSlider(this,mDateSetListener,c);
        }
        return null;
    }
}