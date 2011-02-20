/*
 * Copyright (C) 2011 Daniel Berndt - Codeus Ltd  -  DateSlider
 * 
 * DateSlider which demonstrates the features of the DateSlider ond how
 * to adapt most parameters 
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
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout.LayoutParams;

import com.googlecode.android.widgets.DateSlider.TimeView.TimeTextView;

public class CustomDateSlider extends DateSlider {

	public CustomDateSlider(Context context, OnDateSetListener l, Calendar calendar) {
		super(context, l, calendar);
	}
	

	/**
	 * Create the year and the week and day of the week scrollers and feed them with their labelers
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
		ScrollLayout mWeekScroller = (ScrollLayout) inflater.inflate(R.layout.scroller, null);
		mWeekScroller.setLabeler(weekLabeler, mTime.getTimeInMillis(),100,40);
		mLayout.addView(mWeekScroller, 1,lp);
		mScrollerList.add(mWeekScroller);
		
		// create the month scroller and assign its labeler and add it to the layout
		ScrollLayout mDayScroller = (ScrollLayout) inflater.inflate(R.layout.scroller, null);
		mDayScroller.setLabeler(dayLabeler, mTime.getTimeInMillis(),150,60);
		mLayout.addView(mDayScroller, 2, lp);
		mScrollerList.add(mDayScroller);
		
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
	protected Labeler weekLabeler = new Labeler() {

		/**
		 * add "val" months to the month object that contains "time" and returns the new TimeObject
		 */
		@Override
		public TimeObject add(long time, int val) {
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(time);
			c.add(Calendar.WEEK_OF_YEAR, val);
			return timeObjectfromCalendar(c);
		}
		
		/**
		 * creates an TimeObject from a CalendarInstance
		 */
		@Override
		protected TimeObject timeObjectfromCalendar(Calendar c) {
			int week = c.get(Calendar.WEEK_OF_YEAR);
			int day_of_week = c.get(Calendar.DAY_OF_WEEK)-1;
			// set calendar to first millisecond of the week
			c.add(Calendar.DAY_OF_MONTH,-day_of_week);
			c.set(Calendar.HOUR_OF_DAY,0);c.set(Calendar.MINUTE,0);c.set(Calendar.SECOND,0);
			c.set(Calendar.MILLISECOND, 0);
			long startTime = c.getTimeInMillis();
			// set calendar to last millisecond of the week
			c.add(Calendar.DAY_OF_WEEK, 6);
			c.set(Calendar.HOUR_OF_DAY,23);c.set(Calendar.MINUTE,59);c.set(Calendar.SECOND,59);
			c.set(Calendar.MILLISECOND, 999);
			long endTime = c.getTimeInMillis();
			return new TimeObject(String.format("week %d",week), startTime, endTime);
		}
		
		/**
		 * create our costumised TimeTextView and return it
		 */
		public TimeView createView(Context context, boolean isCenterView) {
			return new CustomTimeTextView(context, isCenterView, 25);
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
			Calendar c = Calendar.getInstance();
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
			return new TimeObject(String.format("%tA",c), startTime, endTime);
		}
		
	};
	
	
	/**
	 * define our own title of the dialog
	 */
	@Override
	protected void setTitle() {
		if (mTitleText != null) {
			mTitleText.setText(getContext().getString(R.string.dateSliderTitle) + 
					String.format(": %tA, %te/%tm/%ty",mTime, mTime, mTime,mTime)); 
		}
	}
	
	/**
	 * Here we define our Custom TimeTextView which will display the fonts in its very own way.
	 */
	private static class CustomTimeTextView extends TimeTextView {

		public CustomTimeTextView(Context context, boolean isCenterView, int textSize) {
			super(context, isCenterView, textSize);
		}
		
		/**
		 * Here we set up the text characteristics for the TextView, i.e. red colour,
		 * serif font and semi-transparent white background for the centerView... and shadow!!!  
		 */
		@Override
		protected void setupView(boolean isCenterView, int textSize) {
			setGravity(Gravity.CENTER);
			setTextColor(0xFF883333);
			setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
			setTypeface(Typeface.SERIF);
			if (isCenterView) {
				setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));
				setBackgroundColor(0x55FFFFFF);
				setShadowLayer(2.5f, 3, 3, 0xFF999999);
			}
		}
		
	}

}
