/*
 * Copyright (C) 2011 Daniel Berndt - Codeus Ltd  -  DateSlider
 * 
 * This class contains all the scrolling logic of the slidable elements
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

import com.googlecode.android.widgets.DateSlider.DateSlider.TimeView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Scroller;


public class ScrollLayout extends LinearLayout {

	private static String TAG = "SCROLLLAYOUT";
	
	private Scroller mScroller;
	private boolean mDragMode;
	private int mLastX, mLastScroll,mFirstElemOffset,childrenWidth,mScrollX;
	private VelocityTracker mVelocityTracker;
	private int mMinimumVelocity, mMaximumVelocity, mInitialOffset;
	private long currentTime;
	private int objWidth, objHeight;
	
	private DateSlider.Labeler labeler;
	private OnScrollListener listener;
	private TimeView mCenterView;
	
	
	public ScrollLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		mScroller = new Scroller(getContext());
		setGravity(Gravity.CENTER_VERTICAL);
		final ViewConfiguration configuration = ViewConfiguration.get(getContext());
		mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
		// as mMaximumVelocity does not exist in API<4
		float density = getContext().getResources().getDisplayMetrics().density;
		mMaximumVelocity = (int)(4000 * 0.5f * density);
	}
	/**
	 * This method is called usually after a ScrollLayout is instanciated, it provides the scroller
	 * with all necessary information
	 *  
	 * @param labeler the labeler instance which will provide the ScrollLayout with time 
	 * unit information 
	 * @param time the start time as timestamp representation
	 * @param objwidth the width of an TimeTextView in dps
	 * @param objheight the height of an TimeTextView in dps
	 */
	public void setLabeler(DateSlider.Labeler labeler, long time, int objwidth, int objheight) {
		this.labeler = labeler;
		currentTime = time;
		objWidth = (int)(objwidth*getContext().getResources().getDisplayMetrics().density);
		objHeight = (int)(objheight*getContext().getResources().getDisplayMetrics().density);
		
		// TODO: make it not dependent on the display width but rather on the layout width 
		Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		int displayWidth = display.getWidth();
		while (displayWidth>childrenWidth-0*objWidth && labeler!=null) {
			LayoutParams lp = new LayoutParams(objWidth, objHeight);
			if (childrenWidth==0) {
				TimeView ttv = labeler.createView(getContext(),true);
				ttv.setVals(labeler.getElem(currentTime));
				addView((View)ttv,lp);
				mCenterView = ttv;
				childrenWidth += objWidth;
			}
			TimeView ttv = labeler.createView(getContext(),false);
			ttv.setVals(labeler.add(((TimeView)getChildAt(getChildCount()-1)).getEndTime(),1));
			addView((View)ttv,lp);
			ttv = labeler.createView(getContext(),false);
			ttv.setVals(labeler.add(((TimeView)getChildAt(0)).getEndTime(),-1));
			addView((View)ttv,0,lp);
			childrenWidth += objWidth+objWidth;
		}		
	}
	
	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mInitialOffset = (childrenWidth-w)/2;
		super.scrollTo(mInitialOffset, 0);
		mScrollX = mInitialOffset;
		setTime(currentTime,0);
	}
	
	/**
	 * this element will position the TimeTextViews such that they correspond to the given time
	 * @param time
	 * @param loops prevents setTime getting called too often, if loop is > 2 the procedure will be
	 * stopped
	 */
	public void setTime(long time, int loops) {
		currentTime = time;
		if (!mScroller.isFinished()) mScroller.abortAnimation();
		int pos = getChildCount()/2;
		TimeView currelem = (TimeView)getChildAt(pos);
		if (loops>2 || currelem.getStartTime() <= time && currelem.getEndTime() >= time) {
			if (loops>2) {
				Log.d(TAG,String.format("time: %d, start: %d, end: %d", time, currelem.getStartTime(), currelem.getStartTime()));
				return;
			}
			double center = getWidth()/2.0;
			int left = (getChildCount()/2)*objWidth-getScrollX();
			double currper = (center-left)/objWidth;
			double goalper = (time-currelem.getStartTime())/(double)(currelem.getEndTime()-currelem.getStartTime());
			int shift = (int)Math.round((currper-goalper)*objWidth);
			mScrollX-=shift;
			reScrollTo(mScrollX,0,false);
		}
		else {
			double diff = currelem.getEndTime() - currelem.getStartTime();
			int steps = (int)Math.round(((time-(currelem.getStartTime()+diff/2))/diff));
			moveElements(-steps);
			setTime(time, loops+1);
		}
	}
	
	

	/**
	 * scroll the element when the mScroller is still scrolling
	 */
	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			mScrollX = mScroller.getCurrX();
			reScrollTo(mScrollX,0, true);
			// Keep on drawing until the animation has finished.
			postInvalidate();
		}
	}
	
	@Override
	public void scrollTo(int x, int y) {
		if (!mScroller.isFinished()) {
			mScroller.abortAnimation();
		}
		reScrollTo(x, y, true);
	}
	
	/**
	 * core scroll function which will replace and move TimeTextViews so that they don't get 
	 * scrolled out of the layout
	 * 
	 * @param x
	 * @param y
	 * @param notify if false, the listeners won't be called
	 */
	protected void reScrollTo(int x, int y, boolean notify) {
		if (getChildCount()>0) {
			mFirstElemOffset += x - mLastScroll;
			if (mFirstElemOffset - mInitialOffset > objWidth/2) {
				int stepsRight = (mFirstElemOffset-mInitialOffset+objWidth/2)/objWidth;
				moveElements(-stepsRight);
				mFirstElemOffset = ((mFirstElemOffset-mInitialOffset-objWidth/2) % objWidth)+mInitialOffset-objWidth/2;
			} else if (mInitialOffset - mFirstElemOffset > objWidth/2) {
				int stepsLeft = (mInitialOffset+objWidth/2-mFirstElemOffset)/objWidth;
				moveElements(stepsLeft);
				mFirstElemOffset = (mInitialOffset + objWidth/2 - ((mInitialOffset+objWidth/2-mFirstElemOffset)%objWidth));
			}
		}
		super.scrollTo(mFirstElemOffset,y);
		if (listener!=null && notify) {

			double center = getWidth()/2.0;
			int left = (getChildCount()/2)*objWidth-mFirstElemOffset;
			double f = (center-left)/objWidth;			
			long newTime = (long)(mCenterView.getStartTime()+(mCenterView.getEndTime()-mCenterView.getStartTime())*f);
			listener.onScroll(newTime);
			};
		mLastScroll = x;
	}
	
	/**
	 * when the scrolling procedure causes "steps" elements to fall out of the visible layout,
	 * all TimeTextViews swap their contents so that it appears that there happens an endless 
	 * scrolling with a very limited amount of views
	 * 
	 * @param steps
	 */
	protected void moveElements(int steps) {
		if (steps<0) {
			for (int i=0;i<getChildCount()+steps;i++) {
				((TimeView)getChildAt(i)).setVals((TimeView)getChildAt(i-steps));
			}
			for (int i=getChildCount()+steps;i>0 && i<getChildCount();i++) {
				DateSlider.TimeObject newTo = labeler.add(((TimeView)getChildAt(i-1)).getEndTime(),1);
				((TimeView)getChildAt(i)).setVals(newTo);
			}
			if (getChildCount() + steps <= 0) {
				for (int i=0;i<getChildCount();i++) {
					DateSlider.TimeObject newTo = labeler.add(((TimeView)getChildAt(i)).getEndTime(),-steps);
					((TimeView)getChildAt(i)).setVals(newTo);
				}
			}
		} else if (steps > 0) {
			for (int i=getChildCount()-1;i>=steps;i--) {
				((TimeView)getChildAt(i)).setVals((TimeView)getChildAt(i-steps));
			}
			for (int i=steps-1;i>=0 && i<getChildCount()-1;i--) {
				DateSlider.TimeObject newTo = labeler.add(((TimeView)getChildAt(i+1)).getEndTime(),-1);
				((TimeView)getChildAt(i)).setVals(newTo);
			}
			if (steps>=getChildCount()) {
				for (int i=0;i<getChildCount();i++) {
					DateSlider.TimeObject newTo = labeler.add(((TimeView)getChildAt(i)).getEndTime(),-steps);
					((TimeView)getChildAt(i)).setVals(newTo);
				}
			}
		}
	}
	
	/**
	 * finding whether to scroll or not
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		final int x = (int) ev.getX();
		if (action == MotionEvent.ACTION_DOWN) {
			mDragMode = true;
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
		}

		if (!mDragMode)
			return super.onTouchEvent(ev);
		
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);
		
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			mScrollX += mLastX - x;
			reScrollTo(mScrollX, 0, true);
			break;
		case MotionEvent.ACTION_UP:
			final VelocityTracker velocityTracker = mVelocityTracker;
			velocityTracker.computeCurrentVelocity(1000);
			int initialVelocity = (int) Math.min(velocityTracker.getXVelocity(), mMaximumVelocity);

			if (getChildCount() > 0 && Math.abs(initialVelocity) > mMinimumVelocity) {
				fling(-initialVelocity);
			}
		case MotionEvent.ACTION_CANCEL:
		default:
			mDragMode = false;
			
		}
		mLastX = x;
		
		return true;
	}
	
	/**
	 * causes the underlying mScroller to do a fling action which will be recovered in the
	 * computeScroll method
	 * @param velocityX
	 */
	private void fling(int velocityX) {
		if (getChildCount() > 0) {
			mScroller.fling(mScrollX, 0, velocityX, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0, 0);
			invalidate();
		}
	}
	
	public void setOnScrollListener(OnScrollListener l) {
		listener = l;
	}
	
	public interface OnScrollListener {
		public void onScroll(long x);
	}
}
