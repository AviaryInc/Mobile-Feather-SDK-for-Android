package com.aviary.android.feather.widget;

import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Scroller;


class Fling8Runnable extends IFlingRunnable {

	private Scroller mScroller;

	public Fling8Runnable(FlingRunnableView parent, int animationDuration ) {
		super(parent, animationDuration);
		Log.i( LOG_TAG, "Fling8Runnable" );
		mScroller = new Scroller( ((View)parent).getContext(), new DecelerateInterpolator() );
	}

	@Override
	public boolean isFinished() {
		return mScroller.isFinished();
	}

	@Override
	protected void _startUsingVelocity( int initialX, int velocity ) {
		mScroller.fling( initialX, 0, velocity, 0, mParent.getMinX(), mParent.getMaxX(), 0, Integer.MAX_VALUE );
	}

	@Override
	protected void _startUsingDistance( int initialX, int distance ) {
		mScroller.startScroll( initialX, 0, distance, 0, mAnimationDuration );
	}

	@Override
	protected void forceFinished( boolean finished ) {
		mScroller.forceFinished( finished );
	}

	@Override
	protected boolean computeScrollOffset() {
		return mScroller.computeScrollOffset();
	}

	@Override
	protected int getCurrX() {
		return mScroller.getCurrX();
	}
}