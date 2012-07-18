package com.aviary.android.feather.widget;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ScaleGestureDetector;
import it.sephiroth.android.library.imagezoom.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import com.aviary.android.feather.library.graphics.drawable.IBitmapDrawable;
import com.aviary.android.feather.library.utils.UIConfiguration;

// TODO: Auto-generated Javadoc
/**
 * The Class CropImageView.
 */
public class CropImageView extends ImageViewTouch {

	/**
	 * The listener interface for receiving onLayout events.
	 * The class that is interested in processing a onLayout
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addOnLayoutListener<code> method. When
	 * the onLayout event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see OnLayoutEvent
	 */
	public interface OnLayoutListener {
		
		/**
		 * On layout changed.
		 *
		 * @param changed the changed
		 * @param left the left
		 * @param top the top
		 * @param right the right
		 * @param bottom the bottom
		 */
		void onLayoutChanged( boolean changed, int left, int top, int right, int bottom );
	}
	
	/**
	 * The listener interface for receiving onHighlightSingleTapUpConfirmed events.
	 * The class that is interested in processing a onHighlightSingleTapUpConfirmed
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addOnHighlightSingleTapUpConfirmedListener<code> method. When
	 * the onHighlightSingleTapUpConfirmed event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see OnHighlightSingleTapUpConfirmedEvent
	 */
	public interface OnHighlightSingleTapUpConfirmedListener {
		
		/**
		 * On single tap up confirmed.
		 */
		void onSingleTapUpConfirmed();
	}

	/** The Constant GROW. */
	public static final int GROW = 0;
	
	/** The Constant SHRINK. */
	public static final int SHRINK = 1;
	
	/** The m motion edge. */
	private int mMotionEdge = HighlightView.GROW_NONE;
	
	/** The m highlight view. */
	private HighlightView mHighlightView;
	
	/** The m layout listener. */
	private OnLayoutListener mLayoutListener;
	
	/** The m highlight single tap up listener. */
	private OnHighlightSingleTapUpConfirmedListener mHighlightSingleTapUpListener;
	
	/** The m motion highlight view. */
	private HighlightView mMotionHighlightView;
	
	/** The m crop min size. */
	private int mCropMinSize = 10;

	/**
	 * Instantiates a new crop image view.
	 *
	 * @param context the context
	 * @param attrs the attrs
	 */
	public CropImageView( Context context, AttributeSet attrs ) {
		super( context, attrs );
	}
	
	/**
	 * Sets the on highlight single tap up confirmed listener.
	 *
	 * @param listener the new on highlight single tap up confirmed listener
	 */
	public void setOnHighlightSingleTapUpConfirmedListener( OnHighlightSingleTapUpConfirmedListener listener ){
		mHighlightSingleTapUpListener = listener;
	}
	
	/**
	 * Sets the min crop size.
	 *
	 * @param value the new min crop size
	 */
	public void setMinCropSize( int value ){
		mCropMinSize = value;
		if( mHighlightView != null ){
			mHighlightView.setMinSize( value );
		}
	}

	/* (non-Javadoc)
	 * @see it.sephiroth.android.library.imagezoom.ImageViewTouch#init()
	 */
	@Override
	protected void init() {
		super.init();
		mGestureDetector = null;
		mScaleDetector = null;
		mGestureListener = null;
		mScaleListener = null;

		mScaleDetector = new ScaleGestureDetector( getContext(), new CropScaleListener() );
		mGestureDetector = new GestureDetector( getContext(), new CropGestureListener(), null, true );
		mGestureDetector.setIsLongpressEnabled( false );

		//mTouchSlop = 20 * 20;
	}

	/**
	 * Sets the on layout listener.
	 *
	 * @param listener the new on layout listener
	 */
	public void setOnLayoutListener( OnLayoutListener listener ) {
		mLayoutListener = listener;
	}

	/* (non-Javadoc)
	 * @see it.sephiroth.android.library.imagezoom.ImageViewTouchBase#setImageBitmap(android.graphics.Bitmap, boolean)
	 */
	@Override
	public void setImageBitmap( Bitmap bitmap, boolean reset ) {
		mMotionHighlightView = null;
		super.setImageBitmap( bitmap, reset );
	}

	/* (non-Javadoc)
	 * @see it.sephiroth.android.library.imagezoom.ImageViewTouchBase#onLayout(boolean, int, int, int, int)
	 */
	@Override
	protected void onLayout( boolean changed, int left, int top, int right, int bottom ) {
		super.onLayout( changed, left, top, right, bottom );

		if ( mLayoutListener != null ) mLayoutListener.onLayoutChanged( changed, left, top, right, bottom );

		final Drawable drawable = getDrawable();

		if ( drawable != null && ( (IBitmapDrawable) drawable ).getBitmap() != null ) {
			if ( mHighlightView != null ) {
				mHighlightView.getMatrix().set( getImageMatrix() );
				mHighlightView.invalidate();
			}
		}
	}

	/* (non-Javadoc)
	 * @see it.sephiroth.android.library.imagezoom.ImageViewTouchBase#postTranslate(float, float)
	 */
	@Override
	protected void postTranslate( float deltaX, float deltaY ) {
		super.postTranslate( deltaX, deltaY );

		if ( mHighlightView != null ) {

			if ( getScale() != 1 ) {
				float[] mvalues = new float[9];
				getImageMatrix().getValues( mvalues );
				final float scale = mvalues[Matrix.MSCALE_X];
				mHighlightView.getCropRectF().offset( -deltaX / scale, -deltaY / scale );
			}

			mHighlightView.getMatrix().set( getImageMatrix() );
			mHighlightView.invalidate();
		}
	}

	/* (non-Javadoc)
	 * @see it.sephiroth.android.library.imagezoom.ImageViewTouchBase#postScale(float, float, float)
	 */
	@Override
	protected void postScale( float scale, float centerX, float centerY ) {
		if ( mHighlightView != null ) {

			RectF cropRect = mHighlightView.getCropRectF();
			Rect rect1 = mHighlightView.getDisplayRect( getImageViewMatrix(), mHighlightView.getCropRectF() );

			super.postScale( scale, centerX, centerY );

			Rect rect2 = mHighlightView.getDisplayRect( getImageViewMatrix(), mHighlightView.getCropRectF() );

			float[] mvalues = new float[9];
			getImageViewMatrix().getValues( mvalues );
			final float currentScale = mvalues[Matrix.MSCALE_X];

			cropRect.offset( ( rect1.left - rect2.left ) / currentScale, ( rect1.top - rect2.top ) / currentScale );
			cropRect.right += -( rect2.width() - rect1.width() ) / currentScale;
			cropRect.bottom += -( rect2.height() - rect1.height() ) / currentScale;

			mHighlightView.getMatrix().set( getImageMatrix() );
			mHighlightView.getCropRectF().set( cropRect );
			mHighlightView.invalidate();
		} else {
			super.postScale( scale, centerX, centerY );
		}
	}

	/**
	 * Ensure visible.
	 *
	 * @param hv the hv
	 */
	private void ensureVisible( HighlightView hv ) {
		Rect r = hv.getDrawRect();
		int panDeltaX1 = Math.max( 0, getLeft() - r.left );
		int panDeltaX2 = Math.min( 0, getRight() - r.right );
		int panDeltaY1 = Math.max( 0, getTop() - r.top );
		int panDeltaY2 = Math.min( 0, getBottom() - r.bottom );
		int panDeltaX = panDeltaX1 != 0 ? panDeltaX1 : panDeltaX2;
		int panDeltaY = panDeltaY1 != 0 ? panDeltaY1 : panDeltaY2;

		if ( panDeltaX != 0 || panDeltaY != 0 ) {
			panBy( panDeltaX, panDeltaY );
		}
	}

	/* (non-Javadoc)
	 * @see android.widget.ImageView#onDraw(android.graphics.Canvas)
	 */
	@Override
	protected void onDraw( Canvas canvas ) {
		super.onDraw( canvas );
		if ( mHighlightView != null ) mHighlightView.draw( canvas );
	}

	/**
	 * Sets the highlight view.
	 *
	 * @param hv the new highlight view
	 */
	public void setHighlightView( HighlightView hv ) {
		if ( mHighlightView != null ) {
			mHighlightView.dispose();
		}

		mMotionHighlightView = null;
		mHighlightView = hv;
		invalidate();
	}

	/**
	 * Gets the highlight view.
	 *
	 * @return the highlight view
	 */
	public HighlightView getHighlightView() {
		return mHighlightView;
	}

	/* (non-Javadoc)
	 * @see it.sephiroth.android.library.imagezoom.ImageViewTouch#onTouchEvent(android.view.MotionEvent)
	 */
	@Override
	public boolean onTouchEvent( MotionEvent event ) {
		int action = event.getAction() & MotionEvent.ACTION_MASK;

		mScaleDetector.onTouchEvent( event );
		if ( !mScaleDetector.isInProgress() ) mGestureDetector.onTouchEvent( event );

		switch ( action ) {
			case MotionEvent.ACTION_UP:

				if ( mHighlightView != null ) {
					mHighlightView.setMode( HighlightView.Mode.None );
				}

				mMotionHighlightView = null;
				mMotionEdge = HighlightView.GROW_NONE;

				if ( getScale() < 1f ) {
					zoomTo( 1f, 50 );
				}
				break;

		}

		return true;
	}

	/**
	 * Distance.
	 *
	 * @param x2 the x2
	 * @param y2 the y2
	 * @param x1 the x1
	 * @param y1 the y1
	 * @return the float
	 */
	static float distance( float x2, float y2, float x1, float y1 ) {
		return (float) Math.sqrt( Math.pow( x2 - x1, 2 ) + Math.pow( y2 - y1, 2 ) );
	}

	/* (non-Javadoc)
	 * @see it.sephiroth.android.library.imagezoom.ImageViewTouch#onDoubleTapPost(float, float)
	 */
	@Override
	protected float onDoubleTapPost( float scale, float maxZoom ) {
		return super.onDoubleTapPost( scale, maxZoom );
	}

	/**
	 * The listener interface for receiving cropGesture events.
	 * The class that is interested in processing a cropGesture
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addCropGestureListener<code> method. When
	 * the cropGesture event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see CropGestureEvent
	 */
	class CropGestureListener extends GestureDetector.SimpleOnGestureListener {

		/* (non-Javadoc)
		 * @see android.view.GestureDetector.SimpleOnGestureListener#onDown(android.view.MotionEvent)
		 */
		@Override
		public boolean onDown( MotionEvent e ) {
			mMotionHighlightView = null;
			HighlightView hv = mHighlightView;

			if ( hv != null ) {

				int edge = hv.getHit( e.getX(), e.getY() );
				if ( edge != HighlightView.GROW_NONE ) {
					mMotionEdge = edge;
					mMotionHighlightView = hv;
					mMotionHighlightView.setMode( ( edge == HighlightView.MOVE ) ? HighlightView.Mode.Move : HighlightView.Mode.Grow );
				}
			}
			return super.onDown( e );
		}

		/* (non-Javadoc)
		 * @see android.view.GestureDetector.SimpleOnGestureListener#onSingleTapConfirmed(android.view.MotionEvent)
		 */
		@Override
		public boolean onSingleTapConfirmed( MotionEvent e ) {
			mMotionHighlightView = null;
			
			
			return super.onSingleTapConfirmed( e );
		}

		/* (non-Javadoc)
		 * @see android.view.GestureDetector.SimpleOnGestureListener#onSingleTapUp(android.view.MotionEvent)
		 */
		@Override
		public boolean onSingleTapUp( MotionEvent e ) {
			mMotionHighlightView = null;
			
			if( mHighlightView != null && mMotionEdge == HighlightView.MOVE ){
				
				if( mHighlightSingleTapUpListener != null ){
					mHighlightSingleTapUpListener.onSingleTapUpConfirmed();
				}
			}
			return super.onSingleTapUp( e );
		}

		/* (non-Javadoc)
		 * @see android.view.GestureDetector.SimpleOnGestureListener#onDoubleTap(android.view.MotionEvent)
		 */
		@Override
		public boolean onDoubleTap( MotionEvent e ) {
			if ( mDoubleTapEnabled ) {
				mMotionHighlightView = null;

				float scale = getScale();
				float targetScale = scale;
				targetScale = CropImageView.this.onDoubleTapPost( scale, getMaxZoom() );
				targetScale = Math.min( getMaxZoom(), Math.max( targetScale, 1 ) );
				mCurrentScaleFactor = targetScale;
				zoomTo( targetScale, e.getX(), e.getY(), 200 );
				// zoomTo( targetScale, e.getX(), e.getY() );
				invalidate();
			}
			return super.onDoubleTap( e );
		}

		/* (non-Javadoc)
		 * @see android.view.GestureDetector.SimpleOnGestureListener#onScroll(android.view.MotionEvent, android.view.MotionEvent, float, float)
		 */
		@Override
		public boolean onScroll( MotionEvent e1, MotionEvent e2, float distanceX, float distanceY ) {
			if ( e1 == null || e2 == null ) return false;
			if ( e1.getPointerCount() > 1 || e2.getPointerCount() > 1 ) return false;
			if ( mScaleDetector.isInProgress() ) return false;

			if ( mMotionHighlightView != null && mMotionEdge != HighlightView.GROW_NONE ) {
				mMotionHighlightView.handleMotion( mMotionEdge, -distanceX, -distanceY );
				ensureVisible( mMotionHighlightView );
				return true;
			} else {
				scrollBy( -distanceX, -distanceY );
				invalidate();
				return true;
			}
		}

		/* (non-Javadoc)
		 * @see android.view.GestureDetector.SimpleOnGestureListener#onFling(android.view.MotionEvent, android.view.MotionEvent, float, float)
		 */
		@Override
		public boolean onFling( MotionEvent e1, MotionEvent e2, float velocityX, float velocityY ) {
			if ( e1.getPointerCount() > 1 || e2.getPointerCount() > 1 ) return false;
			if ( mScaleDetector.isInProgress() ) return false;
			if ( mMotionHighlightView != null ) return false;

			float diffX = e2.getX() - e1.getX();
			float diffY = e2.getY() - e1.getY();

			if ( Math.abs( velocityX ) > 800 || Math.abs( velocityY ) > 800 ) {
				scrollBy( diffX / 2, diffY / 2, 300 );
				invalidate();
			}
			return super.onFling( e1, e2, velocityX, velocityY );
		}
	}

	/**
	 * The listener interface for receiving cropScale events.
	 * The class that is interested in processing a cropScale
	 * event implements this interface, and the object created
	 * with that class is registered with a component using the
	 * component's <code>addCropScaleListener<code> method. When
	 * the cropScale event occurs, that object's appropriate
	 * method is invoked.
	 *
	 * @see CropScaleEvent
	 */
	class CropScaleListener extends SimpleOnScaleGestureListener {

		/* (non-Javadoc)
		 * @see it.sephiroth.android.library.imagezoom.ScaleGestureDetector.SimpleOnScaleGestureListener#onScaleBegin(it.sephiroth.android.library.imagezoom.ScaleGestureDetector)
		 */
		@Override
		public boolean onScaleBegin( ScaleGestureDetector detector ) {
			return super.onScaleBegin( detector );
		}

		/* (non-Javadoc)
		 * @see it.sephiroth.android.library.imagezoom.ScaleGestureDetector.SimpleOnScaleGestureListener#onScaleEnd(it.sephiroth.android.library.imagezoom.ScaleGestureDetector)
		 */
		@Override
		public void onScaleEnd( ScaleGestureDetector detector ) {
			super.onScaleEnd( detector );
		}

		/* (non-Javadoc)
		 * @see it.sephiroth.android.library.imagezoom.ScaleGestureDetector.SimpleOnScaleGestureListener#onScale(it.sephiroth.android.library.imagezoom.ScaleGestureDetector)
		 */
		@Override
		public boolean onScale( ScaleGestureDetector detector ) {
			float targetScale = mCurrentScaleFactor * detector.getScaleFactor();
			if ( true ) {
				targetScale = Math.min( getMaxZoom(), Math.max( targetScale, 1 ) );
				zoomTo( targetScale, detector.getFocusX(), detector.getFocusY() );
				mCurrentScaleFactor = Math.min( getMaxZoom(), Math.max( targetScale, 1 ) );
				mDoubleTapDirection = 1;
				invalidate();
			}
			return true;
		}
	}

	/** The m aspect ratio. */
	protected double mAspectRatio = 0;
	
	/** The m aspect ratio fixed. */
	private boolean mAspectRatioFixed;

	/**
	 * Set the new image display and crop view. If both aspect
	 * 
	 * @param bitmap
	 *           Bitmap to display
	 * @param aspectRatio
	 *           aspect ratio for the crop view. If 0 is passed, then the crop rectangle can be free transformed by the user,
	 *           otherwise the width/height are fixed according to the aspect ratio passed.
	 */
	public void setImageBitmap( Bitmap bitmap, double aspectRatio, boolean isFixed ) {
		mAspectRatio = aspectRatio;
		mAspectRatioFixed = isFixed;
		setImageBitmap( bitmap, true, null, UIConfiguration.IMAGE_VIEW_MAX_ZOOM );
	}

	/**
	 * Sets the aspect ratio.
	 *
	 * @param value the value
	 * @param isFixed the is fixed
	 */
	public void setAspectRatio( double value, boolean isFixed ) {
		if ( getDrawable() != null ) {
			mAspectRatio = value;
			mAspectRatioFixed = isFixed;
			setImageDrawable( getDrawable() );
		}
	}

	/* (non-Javadoc)
	 * @see it.sephiroth.android.library.imagezoom.ImageViewTouch#onBitmapChanged(android.graphics.drawable.Drawable)
	 */
	@Override
	protected void onBitmapChanged( Drawable drawable ) {
		super.onBitmapChanged( drawable );
		updateCropView();
	}

	/**
	 * Update crop view.
	 */
	private void updateCropView() {
		if ( getHighlightView() != null ) {
			setHighlightView( null );
		}

		if ( getDrawable() != null ) {
			HighlightView hv = new HighlightView( this );
			hv.setMinSize( mCropMinSize );
			updateAspectRatio( mAspectRatio, hv );
			setHighlightView( hv );
		}
	}

	/**
	 * Update aspect ratio.
	 *
	 * @param aspectRatio the aspect ratio
	 * @param hv the hv
	 */
	private void updateAspectRatio( double aspectRatio, HighlightView hv ) {

		float width = getDrawable().getIntrinsicWidth();
		float height = getDrawable().getIntrinsicHeight();

		Rect imageRect = new Rect( 0, 0, (int) width, (int) height );

		float cropWidth = Math.min( width, height ) * 0.8f;
		float cropHeight = cropWidth;

		if ( aspectRatio != 0 ) {
			if ( aspectRatio > 1 ) {
				cropHeight = cropHeight / (float) aspectRatio;
			} else {
				cropWidth = cropWidth * (float) aspectRatio;
			}
		}

		float x = ( width - cropWidth ) / 2.0f;
		float y = ( height - cropHeight ) / 2.0f;
		RectF cropRect = new RectF( x, y, x + cropWidth, y + cropHeight );
		Matrix mImageMatrix = getImageMatrix();
		hv.setup( mImageMatrix, imageRect, cropRect, mAspectRatioFixed );
	}
	
	/**
	 * Gets the aspect ratio.
	 *
	 * @return the aspect ratio
	 */
	public double getAspectRatio(){
		return mAspectRatio;
	}
	
	public boolean getAspectRatioIsFixed(){
		return mAspectRatioFixed;
	}
}
