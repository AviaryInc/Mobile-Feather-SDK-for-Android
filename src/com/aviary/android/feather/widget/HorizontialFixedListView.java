/*
 * HorizontalListView.java v1.5
 *
 * 
 * The MIT License
 * Copyright (c) 2011 Paul Soucy (paul@dev-smart.com)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package com.aviary.android.feather.widget;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.Queue;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import com.aviary.android.feather.R;
import com.aviary.android.feather.library.log.LoggerFactory;
import com.aviary.android.feather.library.log.LoggerFactory.Logger;
import com.aviary.android.feather.library.log.LoggerFactory.LoggerType;
import com.aviary.android.feather.library.utils.ReflectionUtils;
import com.aviary.android.feather.widget.IFlingRunnable.FlingRunnableView;

// TODO: Auto-generated Javadoc
/**
 * The Class HorizontialFixedListView.
 */
public class HorizontialFixedListView extends AdapterView<ListAdapter> implements OnGestureListener, FlingRunnableView {

	/** The Constant LOG_TAG. */
	protected static final String LOG_TAG = "hv";

	/** The m always override touch. */
	public boolean mAlwaysOverrideTouch = true;

	/** The m adapter. */
	protected ListAdapter mAdapter;

	/** The m left view index. */
	private int mLeftViewIndex = -1;

	/** The m right view index. */
	private int mRightViewIndex = 0;

	/** The m gesture. */
	private GestureDetector mGesture;

	/** The m removed view queue. */
	private Queue<View> mRemovedViewQueue = new LinkedList<View>();

	/** The m on item selected. */
	private OnItemSelectedListener mOnItemSelected;

	/** The m on item clicked. */
	private OnItemClickListener mOnItemClicked;

	/** The m data changed. */
	private boolean mDataChanged = false;

	/** The m fling runnable. */
	private IFlingRunnable mFlingRunnable;

	/** The m force layout. */
	private boolean mForceLayout;
	
	private int mDragTolerance = 0;
	
	private boolean mDragScrollEnabled;
	
	static Logger logger = LoggerFactory.getLogger( "scroll", LoggerType.ConsoleLoggerType );
	

   /**
    * Interface definition for a callback to be invoked when an item in this
    * view has been clicked and held.
    */
   public interface OnItemDragListener {
       /**
        * Callback method to be invoked when an item in this view has been
        * dragged outside the vertical tolerance area.
        *
        * Implementers can call getItemAtPosition(position) if they need to access
        * the data associated with the selected item.
        *
        * @param parent The AbsListView where the click happened
        * @param view The view within the AbsListView that was clicked
        * @param position The position of the view in the list
        * @param id The row id of the item that was clicked
        *
        * @return true if the callback consumed the long click, false otherwise
        */
       boolean onItemStartDrag(AdapterView<?> parent, View view, int position, long id);
   }

   private OnItemDragListener mItemDragListener;
   
   public void setOnItemDragListener( OnItemDragListener listener ){
   	mItemDragListener = listener;
   }
   
   public OnItemDragListener getOnItemDragListener(){
   	return mItemDragListener;
   }

	/**
	 * Instantiates a new horizontial fixed list view.
	 * 
	 * @param context
	 *           the context
	 * @param attrs
	 *           the attrs
	 */
	public HorizontialFixedListView( Context context, AttributeSet attrs ) {
		super( context, attrs );
		initView();
	}

	/**
	 * Inits the view.
	 */
	private synchronized void initView() {

		if ( Build.VERSION.SDK_INT > 8 )
			mFlingRunnable = (IFlingRunnable) ReflectionUtils.newInstance( "com.aviary.android.feather.widget.Fling9Runnable",
					new Class<?>[] { FlingRunnableView.class, int.class }, this, mAnimationDuration );
		else
			mFlingRunnable = new Fling8Runnable( this, mAnimationDuration );

		mLeftViewIndex = -1;
		mRightViewIndex = 0;
		mMaxX = 0;
		mMinX = 0;
		mChildWidth = 0;
		mChildHeight = 0;
		mRightEdge = 0;
		mLeftEdge = 0;
		mGesture = new GestureDetector( getContext(), mGestureListener );
		mGesture.setIsLongpressEnabled( true );

		setFocusable( true );
		setFocusableInTouchMode( true );

		mDragTolerance = getContext().getResources().getInteger( R.integer.dragTolerance );
		mTouchSlop = ViewConfiguration.getTouchSlop();
	}

	@Override
	public void trackMotionScroll( int newX ) {
		scrollTo( newX, 0 );
		mCurrentX = getScrollX();
		removeNonVisibleItems( mCurrentX );
		fillList( mCurrentX );
		invalidate();
	}

	/**
	 * Set if a vertical scroll movement will trigger a long click event
	 * @param value
	 */
	public void setDragScrollEnabled( boolean value ){
		mDragScrollEnabled = value;
	}
	
	public boolean getDragScrollEnabled(){
		return mDragScrollEnabled;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView#setOnItemSelectedListener(android.widget.AdapterView.OnItemSelectedListener)
	 */
	@Override
	public void setOnItemSelectedListener( AdapterView.OnItemSelectedListener listener ) {
		mOnItemSelected = listener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView#setOnItemClickListener(android.widget.AdapterView.OnItemClickListener)
	 */
	@Override
	public void setOnItemClickListener( AdapterView.OnItemClickListener listener ) {
		mOnItemClicked = listener;
	}

	/** The m data observer. */
	private DataSetObserver mDataObserver = new DataSetObserver() {

		@Override
		public void onChanged() {
			synchronized ( HorizontialFixedListView.this ) {
				mDataChanged = true;
			}
			invalidate();
			mForceLayout = true;
			requestLayout();
		}

		@Override
		public void onInvalidated() {
			reset();
			invalidate();
			mForceLayout = true;
			requestLayout();
		}
	};

	/** The m height measure spec. */
	private int mHeightMeasureSpec;

	/** The m width measure spec. */
	private int mWidthMeasureSpec;

	/** The m is first scroll. */
	private boolean mIsFirstScroll;

	/** The m left edge. */
	private int mRightEdge, mLeftEdge;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView#getAdapter()
	 */
	@Override
	public ListAdapter getAdapter() {
		return mAdapter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView#getSelectedView()
	 */
	@Override
	public View getSelectedView() {
		// TODO: implement
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView#setAdapter(android.widget.Adapter)
	 */
	@Override
	public void setAdapter( ListAdapter adapter ) {

		if ( mAdapter != null ) {
			mAdapter.unregisterDataSetObserver( mDataObserver );
		}
		mAdapter = adapter;

		if ( mAdapter != null ) {
			mAdapter.registerDataSetObserver( mDataObserver );
		}
		reset();
	}

	/**
	 * Reset.
	 */
	private synchronized void reset() {
		initView();
		removeAllViewsInLayout();
		mRemovedViewQueue.clear();
		mForceLayout = true;
		requestLayout();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView#setSelection(int)
	 */
	@Override
	public void setSelection( int position ) {
		// TODO: implement
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#onMeasure(int, int)
	 */
	@Override
	protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec ) {
		super.onMeasure( widthMeasureSpec, heightMeasureSpec );

		mHeightMeasureSpec = heightMeasureSpec;
		mWidthMeasureSpec = widthMeasureSpec;
	}

	/**
	 * Adds the and measure child.
	 * 
	 * @param child
	 *           the child
	 * @param viewPos
	 *           the view pos
	 */
	private void addAndMeasureChild( final View child, int viewPos ) {
		LayoutParams params = child.getLayoutParams();

		if ( params == null ) {
			params = new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT );
		}

		addViewInLayout( child, viewPos, params );
		int childHeightSpec = ViewGroup.getChildMeasureSpec( mHeightMeasureSpec, getPaddingTop() + getPaddingBottom(), params.height );
		int childWidthSpec = ViewGroup.getChildMeasureSpec( mWidthMeasureSpec, getPaddingLeft() + getPaddingRight(), params.width );
		child.measure( childWidthSpec, childHeightSpec );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.AdapterView#onLayout(boolean, int, int, int, int)
	 */
	@Override
	protected void onLayout( boolean changed, int left, int top, int right, int bottom ) {
		super.onLayout( changed, left, top, right, bottom );

		if ( mAdapter == null ) {
			return;
		}

		if ( mDataChanged || mForceLayout || changed ) {
			mCurrentX = mOldX = 0;
			initView();
			removeAllViewsInLayout();
			mDataChanged = false;
			mForceLayout = false;
			trackMotionScroll( 0 );
		}
	}

	/**
	 * Fill list.
	 * 
	 * @param positionX
	 *           the position x
	 */
	private void fillList( final int positionX ) {
		int edge = 0;

		View child = getChildAt( getChildCount() - 1 );
		if ( child != null ) {
			edge = child.getRight();
		}
		fillListRight( mCurrentX, edge );

		edge = 0;
		child = getChildAt( 0 );
		if ( child != null ) {
			edge = child.getLeft();
		}
		fillListLeft( mCurrentX, edge );
	}

	/**
	 * Fill list left.
	 * 
	 * @param positionX
	 *           the position x
	 * @param leftEdge
	 *           the left edge
	 */
	private void fillListLeft( int positionX, int leftEdge ) {

		if ( mAdapter == null ) return;

		while ( ( leftEdge - positionX ) > mLeftEdge && mLeftViewIndex >= 0 ) {
			View child = mAdapter.getView( mLeftViewIndex, mRemovedViewQueue.poll(), this );
			addAndMeasureChild( child, 0 );

			int childTop = getPaddingTop();
			child.layout( leftEdge - mChildWidth, childTop, leftEdge, childTop + mChildHeight );
			leftEdge -= mChildWidth;
			mLeftViewIndex--;
		}
	}

	/**
	 * Fill list right.
	 * 
	 * @param positionX
	 *           the position x
	 * @param rightEdge
	 *           the right edge
	 */
	private void fillListRight( int positionX, int rightEdge ) {
		boolean firstChild = getChildCount() == 0;

		if ( mAdapter == null ) return;

		while ( ( rightEdge - positionX ) < mRightEdge || firstChild ) {

			if ( mRightViewIndex >= mAdapter.getCount() ) {
				break;
			}

			View child = mAdapter.getView( mRightViewIndex, mRemovedViewQueue.poll(), this );
			addAndMeasureChild( child, -1 );

			if ( firstChild ) {
				mChildWidth = child.getMeasuredWidth();
				mChildHeight = child.getMeasuredHeight();
				mRightEdge = getWidth() + mChildWidth;
				mLeftEdge = -mChildWidth;
				mMaxX = Math.max( mAdapter.getCount() * ( mChildWidth ) - ( getWidth() ) - ( mChildWidth / 2 ), 0 );
				mMinX = 0;
				firstChild = false;

				// Log.d( "hv", "original right: " + rightEdge );
				// Log.d( "hv", "left: " + mLeftEdge );
				// Log.d( "hv", "right: " + mRightEdge );
				// Log.d( "hv", "minX: " + mMinX );
				// Log.d( "hv", "maxX: " + mMaxX );
				// Log.d( "hv", "width: " + getWidth() + ", " + getMeasuredWidth() );

				if ( mMaxX == 0 ) {
					rightEdge += getWidth() - ( mAdapter.getCount() * mChildWidth );
					mLeftEdge = 0;
					mRightEdge = getWidth();
					// Log.d( "hv", "new right: " + rightEdge );
				}
			}

			int childTop = getPaddingTop();
			child.layout( rightEdge, childTop, rightEdge + mChildWidth, childTop + child.getMeasuredHeight() );
			rightEdge += mChildWidth;
			mRightViewIndex++;
		}

	}

	/**
	 * Removes the non visible items.
	 * 
	 * @param positionX
	 *           the position x
	 */
	private void removeNonVisibleItems( final int positionX ) {
		View child = getChildAt( 0 );

		// remove to left...
		while ( child != null && child.getRight() - positionX <= mLeftEdge ) {
			mRemovedViewQueue.offer( child );
			removeViewInLayout( child );
			mLeftViewIndex++;
			child = getChildAt( 0 );
		}

		// remove to right...
		child = getChildAt( getChildCount() - 1 );
		while ( child != null && child.getLeft() - positionX >= mRightEdge ) {
			mRemovedViewQueue.offer( child );
			removeViewInLayout( child );
			mRightViewIndex--;
			child = getChildAt( getChildCount() - 1 );
		}
	}

	private float mTestDragX, mTestDragY;
	private boolean mCanCheckDrag;
	private boolean mWasFlinging;
	private WeakReference<View> mOriginalDragItem;

	@Override
	public boolean onDown( MotionEvent event ) {
		mWasFlinging = !mFlingRunnable.isFinished();
		mFlingRunnable.stop( false );
		mIsFirstScroll = true;
		mTestDragX = event.getX();
		mTestDragY = event.getY();
		mCanCheckDrag = isLongClickable() && getDragScrollEnabled() && ( getOnItemDragListener() != null );
		
		if( mCanCheckDrag ){
			int i = getChildAtPosition( event.getX(), event.getY() );
			if( i > -1 ){
				mOriginalDragItem = new WeakReference<View>( getChildAt( i ) );
			}
		}
		return true;
	}

	/**
	 * On up.
	 */
	void onUp() {
		mCanCheckDrag = false;
		if ( mFlingRunnable.isFinished() ) {
			scrollIntoSlots();
		}
	}

	@Override
	public boolean onFling( MotionEvent event0, MotionEvent event1, float velocityX, float velocityY ) {
		if ( mMaxX == 0 ) return false;
		mCanCheckDrag = false;
		mWasFlinging = true;
		mFlingRunnable.startUsingVelocity( mCurrentX, (int) -velocityX );
		return true;
	}

	@Override
	public void onLongPress( MotionEvent e ) {

		if( mWasFlinging )
			return;
		
		OnItemLongClickListener listener = getOnItemLongClickListener();
		if ( null != listener ) {

			if ( !mFlingRunnable.isFinished() ) return;

			int i = getChildAtPosition( e.getX(), e.getY() );
			if( i > -1){
				View child = getChildAt( i );
				fireLongPress( child, mLeftViewIndex + 1 + i, mAdapter.getItemId( mLeftViewIndex + 1 + i ) );
			}
		}
	}
	
	private int getChildAtPosition( float x, float y ){
		Rect viewRect = new Rect();

		for ( int i = 0; i < getChildCount(); i++ ) {
			View child = getChildAt( i );
			int left = child.getLeft();
			int right = child.getRight();
			int top = child.getTop();
			int bottom = child.getBottom();
			viewRect.set( left, top, right, bottom );
			viewRect.offset( -mCurrentX, 0 );

			if ( viewRect.contains( (int) x, (int) y ) ) {
				return i;
			}
		}
		return -1;
	}
	
	private boolean fireLongPress( View item, int position, long id ){
		if( getOnItemLongClickListener().onItemLongClick( HorizontialFixedListView.this, item, position, id ) ){
			performHapticFeedback( HapticFeedbackConstants.LONG_PRESS );
			return true;
		}
		return false;
	}
	
	private boolean fireItemDragStart( View item, int position, long id ){
		if( mItemDragListener.onItemStartDrag( HorizontialFixedListView.this, item, position, id ) ){
			performHapticFeedback( HapticFeedbackConstants.LONG_PRESS );
			return true;
		}
		return false;
	}	

	@Override
	public boolean onScroll( MotionEvent event0, MotionEvent event1, float distanceX, float distanceY ) {

		if ( mAdapter == null ) return false;
		if ( mMaxX == 0 ) return false;

		getParent().requestDisallowInterceptTouchEvent( true );

		if ( mIsFirstScroll ) {
			//if ( distanceX > 0 )
				//distanceX -= mTouchSlop;
			//else
				//distanceX += mTouchSlop;
		}

		mIsFirstScroll = false;

		mToLeft = distanceX > 0; // finger direction

		if ( mToLeft ) {
			if ( mCurrentX + distanceX > mMaxX ) {
				distanceX /= 10;
			}
		} else {
			if ( mCurrentX + distanceX < mMinX ) {
				distanceX /= 10;
			}
		}

		if ( mCanCheckDrag ) {
			float x = event1.getX();
			float y = event1.getY();
			float dx = Math.abs( x - mTestDragX );
			float dy = Math.abs( y - mTestDragY );
			
			if ( dx > mDragTolerance ) {
				mCanCheckDrag = false;
			} else {
				if ( dy > mDragTolerance ) {
					
					if( mOriginalDragItem != null ){
						View view = mOriginalDragItem.get();
						int position = getItemIndex( view );
						if( null != view && position > -1 ){
							getParent().requestDisallowInterceptTouchEvent( false );
							if( mItemDragListener != null ){
								fireItemDragStart( view, mLeftViewIndex + 1 + position, mAdapter.getItemId( mLeftViewIndex + 1 + position ) );
								mCanCheckDrag = false;
							}
							return false;
						}
					}
					mCanCheckDrag = false;
				}
			}
		}
		trackMotionScroll( (int) ( mCurrentX + distanceX ) );
		return true;
	}
	
	private int getItemIndex( View view ){
		final int total = getChildCount();
		for( int i = 0; i < total; i++ ){
			if( view == getChildAt( i ) ){
				return i;
			}
		}
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.GestureDetector.OnGestureListener#onShowPress(android.view.MotionEvent)
	 */
	@Override
	public void onShowPress( MotionEvent arg0 ) {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.GestureDetector.OnGestureListener#onSingleTapUp(android.view.MotionEvent)
	 */
	@Override
	public boolean onSingleTapUp( MotionEvent arg0 ) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.ViewGroup#dispatchTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean dispatchTouchEvent( MotionEvent ev ) {
		boolean handled = mGesture.onTouchEvent( ev );

		if ( !handled ) {
			int action = ev.getAction();
			if ( action == MotionEvent.ACTION_UP ) {
				onUp();
			}
		}

		return handled;
	}

	/** The m animation duration. */
	int mAnimationDuration = 400;

	/** The m child height. */
	int mMaxX, mMinX, mChildWidth, mChildHeight;

	/** The m should stop fling. */
	boolean mShouldStopFling;

	/** The m to left. */
	boolean mToLeft;

	/** The m current x. */
	int mCurrentX = 0;

	/** The m old x. */
	int mOldX = 0;

	/** The m touch slop. */
	int mTouchSlop;

	@Override
	public void scrollIntoSlots() {
		if ( !mFlingRunnable.isFinished() ) {
			return;
		}

		// boolean greater_enough = mAdapter.getCount() * ( mChildWidth ) > getWidth();

		if ( mCurrentX > mMaxX || mCurrentX < mMinX ) {
			if ( mCurrentX > mMaxX ) {
				if ( mMaxX < 0 ) {
					mFlingRunnable.startUsingDistance( mCurrentX, mMinX - mCurrentX );
				} else {
					mFlingRunnable.startUsingDistance( mCurrentX, mMaxX - mCurrentX );
				}
				return;
			} else {
				mFlingRunnable.startUsingDistance( mCurrentX, mMinX - mCurrentX );
				return;
			}
		}
		onFinishedMovement();
	}

	/**
	 * On finished movement.
	 */
	protected void onFinishedMovement() {

	}

	/** The m gesture listener. */
	private OnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {

		@Override
		public boolean onDoubleTap( MotionEvent e ) {
			return false;
		};

		@Override
		public boolean onDown( MotionEvent e ) {
			return HorizontialFixedListView.this.onDown( e );
		};

		@Override
		public boolean onFling( MotionEvent e1, MotionEvent e2, float velocityX, float velocityY ) {
			return HorizontialFixedListView.this.onFling( e1, e2, velocityX, velocityY );
		};

		@Override
		public void onLongPress( MotionEvent e ) {
			HorizontialFixedListView.this.onLongPress( e );
		};

		@Override
		public boolean onScroll( MotionEvent e1, MotionEvent e2, float distanceX, float distanceY ) {
			return HorizontialFixedListView.this.onScroll( e1, e2, distanceX, distanceY );
		};

		@Override
		public void onShowPress( MotionEvent e ) {};

		@Override
		public boolean onSingleTapConfirmed( MotionEvent e ) {

			if ( !mFlingRunnable.isFinished() || mWasFlinging ) return false;

			Rect viewRect = new Rect();

			for ( int i = 0; i < getChildCount(); i++ ) {
				View child = getChildAt( i );
				int left = child.getLeft();
				int right = child.getRight();
				int top = child.getTop();
				int bottom = child.getBottom();
				viewRect.set( left, top, right, bottom );
				viewRect.offset( -mCurrentX, 0 );

				if ( viewRect.contains( (int) e.getX(), (int) e.getY() ) ) {
					if ( mOnItemClicked != null ) {
						mOnItemClicked.onItemClick( HorizontialFixedListView.this, child, mLeftViewIndex + 1 + i,
								mAdapter.getItemId( mLeftViewIndex + 1 + i ) );
					}
					if ( mOnItemSelected != null ) {
						mOnItemSelected.onItemSelected( HorizontialFixedListView.this, child, mLeftViewIndex + 1 + i,
								mAdapter.getItemId( mLeftViewIndex + 1 + i ) );
					}
					break;
				}
			}
			return true;
		}
	};

	@Override
	public int getMinX() {
		return mMinX;
	}

	@Override
	public int getMaxX() {
		return mMaxX;
	}

	public void setDragTolerance( int value ) {
		mDragTolerance = value;
	}
}
