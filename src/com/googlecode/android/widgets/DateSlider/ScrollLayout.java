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

import java.lang.reflect.Constructor;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
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

import com.googlecode.android.widgets.DateSlider.labeler.Labeler;
import com.googlecode.android.widgets.DateSlider.timeview.TimeView;

/**
 * This is where most of the magic happens. This is a subclass of LinearLayout
 * that display a collection of TimeViews and handles the scrolling, shuffling
 * the TimeViews around to keep the display up-to-date, and managing the Labelers
 * to populate the TimeViews with the correct data.
 *
 * This class is configured via xml attributes that specify the class of the
 * labeler to use to generate views, the format string for the labeler to use
 * to populate the views, and optionally width and height values to override
 * the default width and height of the views.
 */
public class ScrollLayout extends LinearLayout {

    private static String TAG = "SCROLLLAYOUT";

    private Scroller mScroller;
    /**
     * Indicates if we are currently tracking touch events that are dragging
     * (scrolling) us.
     */
    private boolean mDragMode;
    /**
     * The aggregate width of all of our children
     */
    private int childrenWidth;
    /**
     * The aggregate width of our children is very likely to be wider than the
     * bounds of our view. Since we keep everything centered, we need to keep
     * our view scrolled by enough to center our children, rather than
     * left-aligning them. This variable tracks how much to scroll to achieve this.
     */
    private int mInitialOffset;
    private int mLastX, mLastScroll, mScrollX;
    private VelocityTracker mVelocityTracker;
    private int mMinimumVelocity, mMaximumVelocity;
    /**
     * The time that we are currently displaying
     */
    private long currentTime = System.currentTimeMillis();
    private long minTime = -1, maxTime = -1;
    private int minuteInterval = 1;
    
    /**
     * The width of each child
     */
    private int objWidth;
    /**
     * The height of each child
     */
    private int objHeight;
    
    private Drawable rightShadow, leftShadow;

    private Labeler mLabeler;
    private OnScrollListener listener;
    private TimeView mCenterView;

    public ScrollLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setWillNotDraw(false);
        rightShadow = getContext().getResources().getDrawable(R.drawable.right_shadow);
        leftShadow = getContext().getResources().getDrawable(R.drawable.left_shadow);
        mScroller = new Scroller(getContext());
        setGravity(Gravity.CENTER_VERTICAL);
        setOrientation(HORIZONTAL);
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();
        // as mMaximumVelocity does not exist in API<4
        float density = getContext().getResources().getDisplayMetrics().density;
        mMaximumVelocity = (int)(4000 * 0.5f * density);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ScrollLayout,
                0, 0);

        // Get the labeler class and construct an instance
        String className = a.getNonResourceString(R.styleable.ScrollLayout_labelerClass);
        if (className == null) {
            throw new RuntimeException("Must specify labeler class at " + a.getPositionDescription());
        }

        String labelerFormat = a.getString(R.styleable.ScrollLayout_labelerFormat);
        if (labelerFormat == null) {
            throw new RuntimeException("Must specify labelerFormat at " + a.getPositionDescription());
        }

        try {
            Class<?> klazz = Class.forName(className);
            Constructor<?> ctor = klazz.getConstructor(String.class);
            mLabeler = (Labeler)ctor.newInstance(labelerFormat);
        } catch (Exception e) {
            throw new RuntimeException("Failed to construct labeler at " + a.getPositionDescription(), e);
        }

        // Determine the width and height of our children, using the labelers preferred
        // values as defaults
        objWidth = a.getDimensionPixelSize(R.styleable.ScrollLayout_childWidth,
                mLabeler.getPreferredViewWidth(context));
        objHeight = a.getDimensionPixelSize(R.styleable.ScrollLayout_childHeight,
                mLabeler.getPreferredViewHeight(context));

        a.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        //
        // We need to generate enough children to fill all of our desired space, and
        // it needs to be an odd number of children because we treat the center view
        // specially. So, first compute how many children we will need.
        //
        final Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        final int displayWidth = display.getWidth();
        int childCount = displayWidth / objWidth;
        // Make sure to round up
        if (displayWidth % objWidth != 0) {
            childCount++;
        }
        // Now make sure we have an odd number of children
        if (childCount % 2 == 0) {
            childCount++;
        }

        // We have an odd number of children, so childCount / 2 will round down to the
        // index just before the center in 1-based indexing, meaning that it will be the
        // center index in 0-based indexing.
        final int centerIndex = (childCount / 2);

        // Make sure we weren't inflated with any views for some odd reason
        removeAllViews();

        // Now add all of the child views, making sure to make the center view as such.
        for (int i = 0; i < childCount; i++) {
            LayoutParams lp = new LayoutParams(objWidth, objHeight);
            TimeView ttv = mLabeler.createView(getContext(), i == centerIndex);
            addView((View)ttv, lp);
        }

        // Now we need to set the times on all of the TimeViews. We start with the center
        // view, work our way to the end, then starting from the center again, work our
        // way back to the beginning.
        mCenterView = (TimeView)getChildAt(centerIndex);
        mCenterView.setVals(mLabeler.getElem(currentTime));
        
        
        Log.v(TAG, "mCenter: " + mCenterView.getTimeText() + " minInterval " + minuteInterval);

        // TODO: Do I need to use endTime, or can I just use the point time?
        for (int i = centerIndex + 1; i < childCount; i++) {
            TimeView lastView = (TimeView)getChildAt(i - 1);
            TimeView thisView = (TimeView)getChildAt(i);
            thisView.setVals(mLabeler.add(lastView.getEndTime(), 1));
        }
        for (int i = centerIndex - 1; i >= 0; i--) {
            TimeView lastView = (TimeView)getChildAt(i + 1);
            TimeView thisView = (TimeView)getChildAt(i);
            thisView.setVals(mLabeler.add(lastView.getEndTime(), -1));
        }

        // Finally, set our actual children width
        childrenWidth = childCount * objWidth;
    }

    @Override
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // In order to keep our children centered, the initial offset has to
        // be half the difference between our childrens' width and our width.
        mInitialOffset = (childrenWidth-w)/2;
        // Now scroll to that offset
        super.scrollTo(mInitialOffset, 0);
        mScrollX = mInitialOffset;
        mLastScroll = mInitialOffset;
        setTime(currentTime,0);
    }
    
    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        rightShadow.setBounds(getScrollX()+getWidth()-50, 0, getWidth()+getScrollX()+1, getHeight());
        rightShadow.draw(canvas);
        
        leftShadow.setBounds(getScrollX(), 0, getScrollX()+50, getHeight());
        leftShadow.draw(canvas);
    }

    public void setMinTime(long time) {
    	minTime = time;
    }
    
    public void setMaxTime(long time) {
    	maxTime = time;
    }
    
    
    public void setTime(long time) {
    	this.setTime(time, 0);
    }
    
    
    /**
     * sets a new minuteInterval
     * this requires us to update all the views, because they are still working with the old
     * minuteInterval
     * 
     * @param minInterval
     */
    public void setMinuteInterval(int minInterval) {
    	this.minuteInterval = minInterval;
    	mLabeler.setMinuteInterval(minInterval);
    	if (minInterval>1) {
    		final int centerIndex = (getChildCount() / 2);
            for (int i = centerIndex + 1; i < getChildCount(); i++) {
                TimeView lastView = (TimeView)getChildAt(i - 1);
                TimeView thisView = (TimeView)getChildAt(i);
                thisView.setVals(mLabeler.add(lastView.getEndTime(), 1));
            }
            for (int i = centerIndex - 1; i >= 0; i--) {
                TimeView lastView = (TimeView)getChildAt(i + 1);
                TimeView thisView = (TimeView)getChildAt(i);
                thisView.setVals(mLabeler.add(lastView.getEndTime(), -1));
            }
    	}
    }
    
    /**
     * this element will position the TimeTextViews such that they correspond to the given time
     * @param time
     * @param loops prevents setTime getting called too often, if loop is > 2 the procedure will be
     * stopped
     */
    private void setTime(long time, int loops) {
        currentTime = time;
        if (!mScroller.isFinished()) mScroller.abortAnimation();
        int pos = getChildCount()/2;
        TimeView currelem = (TimeView)getChildAt(pos);
        if (loops>2 || currelem.getStartTime() <= time && currelem.getEndTime() >= time) {
            if (loops>2) {
                Log.d(TAG,String.format("time: %d, start: %d, end: %d", time, currelem.getStartTime(), currelem.getEndTime()));
                return;
            }
            double center = getWidth()/2.0;
            int left = (getChildCount()/2)*objWidth-getScrollX();
            double currper = (center-left)/objWidth;
            double goalper = (time-currelem.getStartTime())/(double)(currelem.getEndTime()-currelem.getStartTime());
            int shift = (int)Math.round((currper-goalper)*objWidth);
            mScrollX-=shift;
            reScrollTo(mScrollX,0,false);
        } else {
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
    	if (notify) Log.d(TAG,String.format("scroll to " + x));
    	int scrollX = getScrollX();
    	int scrollDiff = x - mLastScroll;
    	
    	// estimate whether we are going to reach the lower limit
    	if (minTime!=-1 && notify && scrollDiff<0) {
            double center = getWidth()/2.0;
            int left = (getChildCount()/2)*objWidth-scrollX;
            double f = (center-left)/objWidth;
            
    		long esp_time = (long) (mCenterView.getStartTime() + (f - ((double)-scrollDiff)/objWidth) * (mCenterView.getEndTime() - mCenterView.getStartTime()));
    		
    		// if we reach it, prevent surpassing it
    		if (esp_time<minTime) {
	    		int deviation = scrollDiff - (int) Math.round(((double) (currentTime - minTime))/(currentTime - esp_time) * scrollDiff);
	    		mScrollX -= deviation;
	    		x -= deviation;
	    		scrollDiff -= deviation;
	    		if (!mScroller.isFinished()) mScroller.abortAnimation();
    		}
    	}
    	// estimate whether we are going to reach the upper limit
    	else if (maxTime!=-1 && notify && scrollDiff>0) {
    		double center = getWidth()/2.0;
            int left = (getChildCount()/2)*objWidth-scrollX;
            double f = (center-left)/objWidth;
            
    		long esp_time = (long) (mCenterView.getStartTime() + (f - ((double)-scrollDiff)/objWidth) * (mCenterView.getEndTime() - mCenterView.getStartTime()));
    		
    		// if we reach it, prevent surpassing it
    		if (esp_time>maxTime) {
	    		int deviation = scrollDiff - (int) Math.round(((double) (currentTime - maxTime))/(currentTime - esp_time) * scrollDiff);
	    		mScrollX -= deviation;
	    		x -= deviation;
	    		scrollDiff -= deviation;
	    		if (!mScroller.isFinished()) mScroller.abortAnimation();
    		}
    	}    	
        
        if (getChildCount()>0) {
            // Determine the absolute x-value for where we are being asked to scroll
            scrollX += scrollDiff;
            // If we've scrolled more than half of a view width in either direction, then
            // a different time is the "current" time, and we need to shuffle our views around.
            // Each additional full view's width on top of the initial half view's width is
            // another position that we need to move our elements. So, we need to add half the
            // width to the amount we've scrolled and then compute how many full multiples of
            // the view width that encompasses to determine how far to move our elements.
            if (scrollX - mInitialOffset > objWidth/2) {
                // Our scroll target relative to our initial offset
                int relativeScroll = scrollX - mInitialOffset;
                int stepsRight = (relativeScroll + (objWidth/2)) / objWidth;
                moveElements(-stepsRight);
                // Now modify the scroll target based on our view shuffling.
                scrollX = ((relativeScroll-objWidth/2) % objWidth)+mInitialOffset-objWidth/2;
            } else if (mInitialOffset - scrollX > objWidth/2) {
                int relativeScroll = mInitialOffset - scrollX;
                int stepsLeft = (relativeScroll + (objWidth / 2)) / objWidth;
                moveElements(stepsLeft);
                scrollX = (mInitialOffset + objWidth/2 - ((mInitialOffset+objWidth/2-scrollX)%objWidth));
            }
        }
        super.scrollTo(scrollX,y);
        if (listener!=null && notify) {
            double center = getWidth()/2.0;
            int left = (getChildCount()/2)*objWidth-scrollX;
            double f = (center-left)/objWidth;
            currentTime = (long)(mCenterView.getStartTime()+(mCenterView.getEndTime()-mCenterView.getStartTime())*f);
            if (notify) Log.d(TAG,String.format("real time " + currentTime));
            if (notify) Log.d(TAG,String.format(""));
            listener.onScroll(currentTime);
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
        if (steps == 0) {
            return;
        }

        // We need to make each TimeView reflect a value that is -steps units
        // from its current value. As an optimization, we will see if this
        // value is already present in another child (by looking to see if there
        // is a child at an index -steps offset from the target child's index).
        // Since this method is most often called with steps equal to 1 or -1,
        // this is a valuable optimization. However, when doing this we need to
        // make sure that we don't overwrite the value of the other child before
        // we copy the value out. So, when steps is negative, we will be pulling
        // values from children with larger indexes and we want to iterate forwards.
        // When steps is positive, we will be pulling values from children with
        // smaller indexes, and we want to iterate backwards.

        int start;
        int end;
        int incr;
        if (steps < 0) {
            start = 0;
            end = getChildCount();
            incr = 1;
        } else {
            start = getChildCount() - 1;
            end = -1;
            incr = -1;
        }
        for (int i = start; i != end; i += incr) {
            TimeView tv = (TimeView)getChildAt(i);
            int index = i - steps;
            if (index >= 0 && index < getChildCount()) {
                tv.setVals((TimeView)getChildAt(index));
            } else {
                tv.setVals(mLabeler.add(tv.getEndTime(), -steps));
            }
            if (minTime != -1 && tv.getEndTime() < minTime) {
            	if (!tv.isOutOfBounds()) tv.setOutOfBounds(true);
            } else if (maxTime != -1 && tv.getStartTime() > maxTime) {
            	if (!tv.isOutOfBounds()) tv.setOutOfBounds(true);
            } else if (tv.isOutOfBounds()) {
            	tv.setOutOfBounds(false);
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
