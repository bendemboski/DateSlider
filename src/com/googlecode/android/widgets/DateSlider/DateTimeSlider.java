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
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout.LayoutParams;

import com.googlecode.android.widgets.DateSlider.TimeView.DayTimeLayoutView;
import com.googlecode.android.widgets.DateSlider.TimeView.TimeLayoutView;

public class DateTimeSlider extends DateSlider {

	public DateTimeSlider(Context context, OnDateSetListener l,	Calendar calendar) {
		super(context, l, calendar);
	}
	
	public static int MINUTEINTERVAL = 15;
	
	/**
	 * Create the month, day and the timescroller and feed them with their labelers
	 * and place them on the layout.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// this needs to be called before everything else to set up the main layout of the DateSlider  
		super.onCreate(savedInstanceState);		
		
		LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT);
		
		// create the month scroller and assign its labeler and add it to the layout
		ScrollLayout mMonthScroller = (ScrollLayout) inflater.inflate(R.layout.scroller, null);
		mMonthScroller.setLabeler(monthLabeler, mTime.getTimeInMillis(),180,60);
		mLayout.addView(mMonthScroller, 0,lp);
		mScrollerList.add(mMonthScroller);
		
		// create the month scroller and assign its labeler and add it to the layout
		ScrollLayout mDayScroller = (ScrollLayout) inflater.inflate(R.layout.scroller, null);
		mDayScroller.setLabeler(dayLabeler, mTime.getTimeInMillis(),120,60);
		mLayout.addView(mDayScroller, 1, lp);
		mScrollerList.add(mDayScroller);
		
		// create the minute scroller and assign its labeler and add it to the layout
		ScrollLayout mTimeScroller = (ScrollLayout) inflater.inflate(R.layout.scroller, null);
		mTimeScroller.setLabeler(timeLabeler, mTime.getTimeInMillis(),80,60);
		mLayout.addView(mTimeScroller, 2,lp);
		mScrollerList.add(mTimeScroller);
		
		// this method _has_ to be called to set the onScrollListeners for all the Scrollers
		// in the mScrollerList.
		setListeners();
	}
	
	// the month labeler  takes care of providing each TimeTextView element in the monthScroller
	// with the right label and information about its time representation
	protected Labeler monthLabeler = new Labeler() {

		/**
		 * add "val" months to the month object that contains "time" and returns the new TimeObject
		 */
		@Override
		public TimeObject add(long time, int val) {
			Calendar c = Calendar.getInstance(mTimeZone);
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
			return new TimeObject(String.format("%tb %tY",c,c), startTime, endTime);
		}
		
		@Override
		/**
		 * rather than a standard TextView this is returning a LimearLayout with two TextViews
		 */
		public TimeView createView(Context context, boolean isCenterView) {
			return new TimeLayoutView(context, isCenterView,25,8,0.95f);
		}
		
	};
	

	// the day labeler takes care of providing each TimeTextView element in the dayScroller
	// with the right label and information about its time representation
	protected Labeler dayLabeler = new Labeler() {

		/**
		 * add "val" days to the month object that contains "time" and returns the new TimeObject
		 */
		@Override
		public TimeObject add(long time, int val) {
			Calendar c = Calendar.getInstance(mTimeZone);
			c.setTimeInMillis(time);
			c.add(Calendar.DAY_OF_MONTH, val);
			return timeObjectfromCalendar(c);
		}
		
		/**
		 * creates an TimeObject from a CalendarInstance
		 */
		@Override
		protected TimeObject timeObjectfromCalendar(Calendar c) {
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			// set calendar to first millisecond of the day
			c.set(year, month, day, 0, 0, 0);
			c.set(Calendar.MILLISECOND, 0);
			long startTime = c.getTimeInMillis();
			// set calendar to last millisecond of the day
			c.set(year, month, day, 23, 59, 59);
			c.set(Calendar.MILLISECOND, 999);
			long endTime = c.getTimeInMillis();
			return new TimeObject(String.format("%td %ta",c,c), startTime, endTime);
		}
		
		@Override
		/**
		 * rather than a standard TextView this is returning a LimearLayout with two TextViews
		 */
		public TimeView createView(Context context, boolean isCenterView) {
			return new DayTimeLayoutView(context, isCenterView,30,8,0.8f);
		}
		
	};
	
	// the day labeler takes care of providing each TimeTextView element in the timeScroller
	// with the right label and information about its time representation
	protected Labeler timeLabeler = new Labeler() {

		/**
		 * add "val" days to the month object that contains "time" and returns the new TimeObject
		 */
		@Override
		public TimeObject add(long time, int val) {
			Calendar c = Calendar.getInstance(mTimeZone);
			c.setTimeInMillis(time);
			c.add(Calendar.MINUTE, val*MINUTEINTERVAL);
			return timeObjectfromCalendar(c);
		}
		
		/**
		 * override this method to set the inital TimeObject to a multiple of MINUTEINTERVAL
		 */
		@Override
		public TimeObject getElem(long time) { 
			Calendar c = Calendar.getInstance(mTimeZone);
			c.setTimeInMillis(time);
			c.set(Calendar.MINUTE, c.get(Calendar.MINUTE)/MINUTEINTERVAL*MINUTEINTERVAL);
			Log.v("GETELEM","getelem: "+c.get(Calendar.MINUTE));
			return timeObjectfromCalendar(c);
		}
		
		/**
		 * creates an TimeObject from a CalendarInstance
		 */
		@Override
		protected TimeObject timeObjectfromCalendar(Calendar c) {
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE)/MINUTEINTERVAL*MINUTEINTERVAL;
			// get the last millisecond of that 15 minute block
			c.set(year, month, day, hour, minute+MINUTEINTERVAL-1, 59);
			c.set(Calendar.MILLISECOND, 999);
			long endTime = c.getTimeInMillis();
			// get the first millisecond of that 15 minute block
			c.set(year, month, day, hour, minute, 0);
			c.set(Calendar.MILLISECOND, 0);
			long startTime = c.getTimeInMillis();
			return new TimeObject(String.format("%tR", c), startTime, endTime);
		}
	};
	
	@Override
	protected void setTitle() {
		if (mTitleText != null) {
			int minute = mTime.get(Calendar.MINUTE)/MINUTEINTERVAL*MINUTEINTERVAL;
			mTitleText.setText(String.format("Selected DateTime: %te/%tm/%ty %tH:%02d",
					mTime,mTime,mTime,mTime,minute)); 
		}
	}
}
