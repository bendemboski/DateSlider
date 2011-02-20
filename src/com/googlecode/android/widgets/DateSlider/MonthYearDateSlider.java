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

import com.googlecode.android.widgets.DateSlider.R;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout.LayoutParams;

public class MonthYearDateSlider extends DateSlider {

	public MonthYearDateSlider(Context context, OnDateSetListener l, Calendar calendar) {
		super(context, l, calendar);
	}
	
	/**
	 * Create the year and the monthscroller and feed them with their labelers
	 * and place them on the layout.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// this needs to be called before everything else to set up the main layout of the DateSlider  
		super.onCreate(savedInstanceState);		
		
		LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		
		// create the year scroller and assign its labeler and add it to the layout
		ScrollLayout mYearScroller = (ScrollLayout) inflater.inflate(R.layout.scroller, null);
		mYearScroller.setLabeler(yearLabeler, mTime.getTimeInMillis(),200,60);
		mLayout.addView(mYearScroller, 0,lp);
		mScrollerList.add(mYearScroller);
		
		// create the month scroller and assign its labeler and add it to the layout
		ScrollLayout mMonthScroller = (ScrollLayout) inflater.inflate(R.layout.scroller, null);
		mMonthScroller.setLabeler(monthLabeler, mTime.getTimeInMillis(),150,60);
		mLayout.addView(mMonthScroller, 1,lp);
		mScrollerList.add(mMonthScroller);
		
		// this method _has_ to be called to set the onScrollListeners for all the Scrollers
		// in the mScrollerList.
		setListeners();
	}
	
	// the year labeler  takes care of providing each TimeTextView element in the yearScroller
	// with the right label and information about its time representation
	protected Labeler yearLabeler = new Labeler() {

		/**
		 * add "val" year to the month object that contains "time" and returns the new TimeObject
		 */
		@Override
		public TimeObject add(long time, int val) {
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(time);
			c.add(Calendar.YEAR, val);
			return timeObjectfromCalendar(c);
		}
		
		/**
		 * creates an TimeObject from a CalendarInstance
		 */
		@Override
		protected TimeObject timeObjectfromCalendar(Calendar c) {
			int year = c.get(Calendar.YEAR);
			// set calendar to first millisecond of the year
			c.set(year, 0, 1, 0, 0, 0);
			c.set(Calendar.MILLISECOND, 0);
			long startTime = c.getTimeInMillis();
			// set calendar to last millisecond of the year
			c.set(year, 11, 31, 23, 59, 59);
			c.set(Calendar.MILLISECOND, 999);
			long endTime = c.getTimeInMillis();
			return new TimeObject(String.valueOf(year), startTime, endTime);
		}
		
	};
	
	// the month labeler  takes care of providing each TimeTextView element in the monthScroller
	// with the right label and information about its time representation
	protected Labeler monthLabeler = new Labeler() {

		/**
		 * add "val" months to the month object that contains "time" and returns the new TimeObject
		 */
		@Override
		public TimeObject add(long time, int val) {
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(time);
			c.add(Calendar.MONTH, val);
			return timeObjectfromCalendar(c);
		}
		
		/**
		 * creates an TimeObject from a CalendarInstance
		 */
		@Override
		protected TimeObject timeObjectfromCalendar(Calendar c) {
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
			return new TimeObject(String.format("%tB",c), startTime, endTime);
		}
		
	};
	
	/**
	 * override the setTitle method so that only the month and the year are shown.
	 */
	@Override
	protected void setTitle() {
		if (mTitleText != null) {
			mTitleText.setText(getContext().getString(R.string.dateSliderTitle) + 
					String.format(": %tB %tY",mTime,mTime)); 
		}
	}

}
