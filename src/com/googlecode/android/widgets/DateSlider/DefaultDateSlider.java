/*
 * Copyright (C) 2011 Daniel Berndt - Codeus Ltd  -  DateSlider
 * 
 * Default DateSlider which allows for an easy selection of a date 
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

public class DefaultDateSlider extends DateSlider {
	
	/**
	 * initialise the DateSlider
	 * 
	 * @param context
	 * @param l
	 * @param calendar calendar set with the date that should appear at start up
	 */
	public DefaultDateSlider(Context context, OnDateSetListener l, Calendar calendar) {
		super(context, l, calendar);
	}
	
	/**
	 * Create the month and the dayscroller and feed them with their labelers
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
		mMonthScroller.setLabeler(monthLabeler, mTime.getTimeInMillis(),90,60);
		mLayout.addView(mMonthScroller, 0,lp);
		mScrollerList.add(mMonthScroller);
		
		// create the month scroller and assign its labeler and add it to the layout
		ScrollLayout mDayScroller = (ScrollLayout) inflater.inflate(R.layout.scroller, null);
		mDayScroller.setLabeler(dayLabeler, mTime.getTimeInMillis(),45,60);
		mLayout.addView(mDayScroller, 1, lp);
		mScrollerList.add(mDayScroller);
		
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
	
	public static class DayTimeLayoutView extends TimeLayoutView {

		protected boolean isSunday=false;
		
		public DayTimeLayoutView(Context context, boolean isCenterView,
				int topTextSize, int bottomTextSize, float lineHeight) {
			super(context, isCenterView, topTextSize, bottomTextSize, lineHeight);
		}
		
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
		
		protected void colorMeSunday() {
			if (isCenter) {
				bottomView.setTextColor(0xFF773333);
				topView.setTextColor(0xFF553333);
			}
			else {
				bottomView.setTextColor(0xFF442222);
				topView.setTextColor(0xFF553333);					
			}
		}
		
		protected void colorMeWorkday() {
			if (isCenter) {
				topView.setTextColor(0xFF333333);
				bottomView.setTextColor(0xFF444444);
			} else {
				topView.setTextColor(0xFF666666);
				bottomView.setTextColor(0xFF666666);					
			}			
		}
		
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
}
