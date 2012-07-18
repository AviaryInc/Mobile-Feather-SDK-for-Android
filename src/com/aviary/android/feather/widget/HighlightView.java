package com.aviary.android.feather.widget;

import android.graphics.BlurMaskFilter;
import android.graphics.BlurMaskFilter.Blur;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import com.aviary.android.feather.R;
import com.aviary.android.feather.library.utils.ReflectionUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class HighlightView.
 */
public class HighlightView {

	/** The Constant LOG_TAG. */
	@SuppressWarnings("unused")
	private static final String LOG_TAG = "hv";

	/** The Constant GROW_NONE. */
	static final int GROW_NONE = 1 << 0;
	
	/** The Constant GROW_LEFT_EDGE. */
	static final int GROW_LEFT_EDGE = 1 << 1;
	
	/** The Constant GROW_RIGHT_EDGE. */
	static final int GROW_RIGHT_EDGE = 1 << 2;
	
	/** The Constant GROW_TOP_EDGE. */
	static final int GROW_TOP_EDGE = 1 << 3;
	
	/** The Constant GROW_BOTTOM_EDGE. */
	static final int GROW_BOTTOM_EDGE = 1 << 4;
	
	/** The Constant MOVE. */
	static final int MOVE = 1 << 5;
	
	/** The m hidden. */
	private boolean mHidden;
	
	/** The m context. */
	private View mContext;

	/**
	 * The Enum Mode.
	 */
	enum Mode {
		
		/** The None. */
		None, 
 /** The Move. */
 Move, 
 /** The Grow. */
 Grow
	}

	/** The m min size. */
	private int mMinSize = 20;
	
	/** The m mode. */
	private Mode mMode;
	
	/** The m draw rect. */
	private Rect mDrawRect;
	
	/** The m image rect. */
	private RectF mImageRect;
	
	/** The m crop rect. */
	private RectF mCropRect;
	
	/** The m matrix. */
	private Matrix mMatrix;
	
	/** The m maintain aspect ratio. */
	private boolean mMaintainAspectRatio = false;
	
	/** The m initial aspect ratio. */
	private float mInitialAspectRatio;
	
	/** The m resize drawable. */
	private Drawable mResizeDrawable;
	
	/** The m outline paint. */
	private final Paint mOutlinePaint = new Paint();
	
	/** The m outline paint2. */
	private final Paint mOutlinePaint2 = new Paint();
	
	/** The m outline fill. */
	private final Paint mOutlineFill = new Paint();
	
	private Paint mLinesPaintShadow = new Paint();

	/** The highlight_color. */
	private int highlight_color;
	
	/** The highlight_down_color. */
	private int highlight_down_color;
	
	/** The highlight_outside_color. */
	private int highlight_outside_color;
	
	/** The highlight_outside_color_down. */
	private int highlight_outside_color_down;
	
	/** The internal_stroke_width. */
	private int stroke_width, internal_stroke_width;

	/** internal grid colors */
	private int highlight_internal_color, highlight_internal_color_down; 

	/** The d height. */
	private int dWidth, dHeight;
	
	final int grid_rows = 3;
	final int grid_cols = 3;	

	/**
	 * Instantiates a new highlight view.
	 *
	 * @param ctx the ctx
	 */
	public HighlightView( View ctx ) {
		mContext = ctx;
		highlight_color = mContext.getResources().getColor( R.color.feather_crop_highlight );
		highlight_down_color = mContext.getResources().getColor( R.color.feather_crop_highlight_down );
		highlight_outside_color = mContext.getResources().getColor( R.color.feather_crop_highlight_outside );
		highlight_outside_color_down = mContext.getResources().getColor( R.color.feather_crop_highlight_outside_down );
		stroke_width = mContext.getResources().getInteger( R.integer.feather_crop_highlight_stroke_width );
		internal_stroke_width = mContext.getResources().getInteger( R.integer.feather_crop_highlight_internal_stroke_width );
		highlight_internal_color = mContext.getResources().getColor( R.color.feather_crop_highlight_internal );
		highlight_internal_color_down = mContext.getResources().getColor( R.color.feather_crop_highlight_internal_down );
	}

	/**
	 * Inits the.
	 */
	private void init() {
		android.content.res.Resources resources = mContext.getResources();
		mResizeDrawable = resources.getDrawable( R.drawable.feather_highlight_crop_handle );

		double w = mResizeDrawable.getIntrinsicWidth();
		double h = mResizeDrawable.getIntrinsicHeight();

		dWidth = (int) Math.ceil( w / 2.0 );
		dHeight = (int) Math.ceil( h / 2.0 );
	}

	/**
	 * Dispose.
	 */
	public void dispose() {
		mContext = null;
	}
	
	/**
	 * Sets the min size.
	 *
	 * @param value the new min size
	 */
	public void setMinSize( int value ){
		mMinSize = value;
	}

	/**
	 * Sets the hidden.
	 *
	 * @param hidden the new hidden
	 */
	public void setHidden( boolean hidden ) {
		mHidden = hidden;
	}

	/** The m view drawing rect. */
	private Rect mViewDrawingRect = new Rect();
	
	/** The m path. */
	private Path mPath = new Path();
	
	/** The m lines path. */
	private Path mLinesPath = new Path();
	
	/** The m inverse path. */
	private Path mInversePath = new Path();

	/**
	 * Draw.
	 *
	 * @param canvas the canvas
	 */
	protected void draw( Canvas canvas ) {
		if ( mHidden ) return;

		//canvas.save();

		mPath.reset();
		mInversePath.reset();
		mLinesPath.reset();

		mContext.getDrawingRect( mViewDrawingRect );

		RectF tmpRect = new RectF();
		tmpRect.set( mDrawRect );
		
		mInversePath.addRect( new RectF( mViewDrawingRect ), Path.Direction.CW );
		mInversePath.addRect( tmpRect, Path.Direction.CCW );

		tmpRect.set( mDrawRect );
		mPath.addRect( tmpRect, Path.Direction.CW );
		mLinesPath.addRect( new RectF( mDrawRect ), Path.Direction.CW );

		float colStep = (float) mDrawRect.height() / grid_cols;
		float rowStep = (float) mDrawRect.width() / grid_rows;

		for ( int i = 1; i < grid_cols; i++ ) {
			mLinesPath.moveTo( (int)mDrawRect.left, (int)(mDrawRect.top + colStep * i) );
			mLinesPath.lineTo( (int)mDrawRect.right, (int)(mDrawRect.top + colStep * i) );
		}

		for ( int i = 1; i < grid_rows; i++ ) {
			mLinesPath.moveTo( (int)(mDrawRect.left + rowStep * i), (int)mDrawRect.top );
			mLinesPath.lineTo( (int)(mDrawRect.left + rowStep * i), (int)mDrawRect.bottom );
		}

		//canvas.restore();
		canvas.drawPath( mInversePath, mOutlineFill );
		//canvas.drawPath( mLinesPath, mLinesPaintShadow );
		canvas.drawPath( mLinesPath, mOutlinePaint2 );
		canvas.drawPath( mPath, mOutlinePaint );

		if ( true /* || mMode == Mode.Grow */) {
			int left = mDrawRect.left + 1;
			int right = mDrawRect.right + 1;
			int top = mDrawRect.top + 4;
			int bottom = mDrawRect.bottom + 3;
			if ( mResizeDrawable != null ) {

				mResizeDrawable.setBounds( left - dWidth, top - dHeight, left + dWidth, top + dHeight );
				mResizeDrawable.draw( canvas );
				mResizeDrawable.setBounds( right - dWidth, top - dHeight, right + dWidth, top + dHeight );
				mResizeDrawable.draw( canvas );
				mResizeDrawable.setBounds( left - dWidth, bottom - dHeight, left + dWidth, bottom + dHeight );
				mResizeDrawable.draw( canvas );
				mResizeDrawable.setBounds( right - dWidth, bottom - dHeight, right + dWidth, bottom + dHeight );
				mResizeDrawable.draw( canvas );
			}
		}
	}

	/**
	 * Sets the mode.
	 *
	 * @param mode the new mode
	 */
	public void setMode( Mode mode ) {
		if ( mode != mMode ) {
			mMode = mode;
			mOutlinePaint.setColor( mMode == Mode.None ? highlight_color : highlight_down_color );
			mOutlinePaint2.setColor( mMode == Mode.None ? highlight_internal_color : highlight_internal_color_down );
			mLinesPaintShadow.setAlpha( mMode == Mode.None ? 102 : 0 );
			mOutlineFill.setColor( mMode == Mode.None ? highlight_outside_color : highlight_outside_color_down );
			mContext.invalidate();
		}
	}

	/** The hysteresis. */
	final float hysteresis = 30F;

	/**
	 * Gets the hit.
	 *
	 * @param x the x
	 * @param y the y
	 * @return the hit
	 */
	public int getHit( float x, float y ) {
		Rect r = computeLayout( false );
		int retval = GROW_NONE;
		boolean verticalCheck = ( y >= r.top - hysteresis ) && ( y < r.bottom + hysteresis );
		boolean horizCheck = ( x >= r.left - hysteresis ) && ( x < r.right + hysteresis );
		if ( ( Math.abs( r.left - x ) < hysteresis ) && verticalCheck ) retval |= GROW_LEFT_EDGE;
		if ( ( Math.abs( r.right - x ) < hysteresis ) && verticalCheck ) retval |= GROW_RIGHT_EDGE;
		if ( ( Math.abs( r.top - y ) < hysteresis ) && horizCheck ) retval |= GROW_TOP_EDGE;
		if ( ( Math.abs( r.bottom - y ) < hysteresis ) && horizCheck ) retval |= GROW_BOTTOM_EDGE;
		if ( retval == GROW_NONE && r.contains( (int) x, (int) y ) ) retval = MOVE;
		return retval;
	}

	/**
	 * Handle motion.
	 *
	 * @param edge the edge
	 * @param dx the dx
	 * @param dy the dy
	 */
	void handleMotion( int edge, float dx, float dy ) {
		Rect r = computeLayout( false );
		if ( edge == GROW_NONE ) {
			return;
		} else if ( edge == MOVE ) {
			moveBy( dx * ( mCropRect.width() / r.width() ), dy * ( mCropRect.height() / r.height() ) );
		} else {
			if ( ( ( GROW_LEFT_EDGE | GROW_RIGHT_EDGE ) & edge ) == 0 ) dx = 0;
			if ( ( ( GROW_TOP_EDGE | GROW_BOTTOM_EDGE ) & edge ) == 0 ) dy = 0;
			// Convert to image space before sending to growBy().
			float xDelta = dx * ( mCropRect.width() / r.width() );
			float yDelta = dy * ( mCropRect.height() / r.height() );
			growBy( ( ( ( edge & GROW_LEFT_EDGE ) != 0 ) ? -1 : 1 ) * xDelta, ( ( ( edge & GROW_TOP_EDGE ) != 0 ) ? -1 : 1 ) * yDelta );
		}
	}

	/**
	 * Move by.
	 *
	 * @param dx the dx
	 * @param dy the dy
	 */
	void moveBy( float dx, float dy ) {
		Rect invalRect = new Rect( mDrawRect );
		mCropRect.offset( dx, dy );
		mCropRect.offset( Math.max( 0, mImageRect.left - mCropRect.left ), Math.max( 0, mImageRect.top - mCropRect.top ) );
		mCropRect.offset( Math.min( 0, mImageRect.right - mCropRect.right ), Math.min( 0, mImageRect.bottom - mCropRect.bottom ) );

		mDrawRect = computeLayout( false );

		invalRect.union( mDrawRect );
		invalRect.inset( -dWidth, -dHeight );
		mContext.invalidate( invalRect );
	}
	
	/**
	 * Gets the scale.
	 *
	 * @return the scale
	 */
	protected float getScale(){
		float values[] = new float[9];
		mMatrix.getValues( values );
		return values[Matrix.MSCALE_X];
	}

	/**
	 * Grow by.
	 *
	 * @param dx the dx
	 * @param dy the dy
	 */
	void growBy( float dx, float dy ) {
		if ( mMaintainAspectRatio ) {
			if ( dx != 0 ) {
				dy = dx / mInitialAspectRatio;
			} else if ( dy != 0 ) {
				dx = dy * mInitialAspectRatio;
			}
		}
		RectF r = new RectF( mCropRect );
		if ( dx > 0F && r.width() + 2 * dx > mImageRect.width() ) {
			float adjustment = ( mImageRect.width() - r.width() ) / 2F;
			dx = adjustment;
			if ( mMaintainAspectRatio ) {
				dy = dx / mInitialAspectRatio;
			}
		}
		if ( dy > 0F && r.height() + 2 * dy > mImageRect.height() ) {
			float adjustment = ( mImageRect.height() - r.height() ) / 2F;
			dy = adjustment;
			if ( mMaintainAspectRatio ) {
				dx = dy * mInitialAspectRatio;
			}
		}
		r.inset( -dx, -dy );
		
		// check minimum size and outset the rectangle as needed
		final float widthCap = (float)mMinSize / getScale();
		
		if( r.width() < widthCap ){
			r.inset( -( widthCap - r.width() ) / 2F, 0F );
		}
		
		float heightCap = mMaintainAspectRatio ? ( widthCap / mInitialAspectRatio ) : widthCap;
		if ( r.height() < heightCap ) {
			r.inset( 0F, -( heightCap - r.height() ) / 2F );
		}
		
		/*
		final float widthCap = (float)mMinSize;
		if ( r.width() < widthCap ) {
			r.inset( -( widthCap - r.width() ) / 2F, 0F );
		}
		float heightCap = mMaintainAspectRatio ? ( widthCap / mInitialAspectRatio ) : widthCap;
		if ( r.height() < heightCap ) {
			r.inset( 0F, -( heightCap - r.height() ) / 2F );
		}
		*/

		mCropRect.set( r );
		mDrawRect = computeLayout( true );
		mContext.invalidate();
	}

	/**
	 * Adjust crop rect.
	 *
	 * @param r the r
	 */
	private void adjustCropRect( RectF r ) {
		if ( r.left < mImageRect.left ) {
			r.offset( mImageRect.left - r.left, 0F );
		} else if ( r.right > mImageRect.right ) {
			r.offset( -( r.right - mImageRect.right ), 0 );
		}

		if ( r.top < mImageRect.top ) {
			r.offset( 0F, mImageRect.top - r.top );
		} else if ( r.bottom > mImageRect.bottom ) {
			r.offset( 0F, -( r.bottom - mImageRect.bottom ) );
		}

		if ( r.width() > r.height() ) {
			if ( r.width() > mImageRect.width() ) {
				if ( r.left < mImageRect.left ) {
					r.left += mImageRect.left - r.left;
				}
				if ( r.right > mImageRect.right ) {
					r.right += -( r.right - mImageRect.right );
				}
			}
		} else {
			if ( r.height() > mImageRect.height() ) {
				if ( r.top < mImageRect.top ) {
					r.top += mImageRect.top - r.top;
				}
				if ( r.bottom > mImageRect.bottom ) {
					r.bottom += -( r.bottom - mImageRect.bottom );
				}
			}
		}

		if ( mMaintainAspectRatio ) {
			if ( mInitialAspectRatio >= 1 ) { // width > height
				final float dy = r.width() / mInitialAspectRatio;
				r.bottom = r.top + dy;
			} else { // height >= width
				final float dx = r.height() * mInitialAspectRatio;
				r.right = r.left + dx;
			}
		}

		r.sort();
	}

	/**
	 * Adjust real crop rect.
	 *
	 * @param matrix the matrix
	 * @param rect the rect
	 * @param outsideRect the outside rect
	 * @return the rect f
	 */
	private RectF adjustRealCropRect( Matrix matrix, RectF rect, RectF outsideRect ) {
		RectF r = new RectF( rect.left, rect.top, rect.right, rect.bottom );
		matrix.mapRect( r );

		float[] mvalues = new float[9];
		matrix.getValues( mvalues );
		final float scale = mvalues[Matrix.MSCALE_X];

		if ( r.left < outsideRect.left )
			rect.offset( ( outsideRect.left - r.left ) / scale, 0 );
		else if ( r.right > outsideRect.right ) rect.offset( -( r.right - outsideRect.right ) / scale, 0 );

		if ( r.top < outsideRect.top )
			rect.offset( 0, ( outsideRect.top - r.top ) / scale );
		else if ( r.bottom > outsideRect.bottom ) rect.offset( 0, -( r.bottom - outsideRect.bottom ) / scale );

		r = new RectF( rect.left, rect.top, rect.right, rect.bottom );
		matrix.mapRect( r );

		if ( r.width() > outsideRect.width() ) {
			if ( r.left < outsideRect.left ) rect.left += ( outsideRect.left - r.left ) / scale;
			if ( r.right > outsideRect.right ) rect.right += -( r.right - outsideRect.right ) / scale;
		}

		if ( r.height() > outsideRect.height() ) {
			if ( r.top < outsideRect.top ) rect.top += ( outsideRect.top - r.top ) / scale;
			if ( r.bottom > outsideRect.bottom ) rect.bottom += -( r.bottom - outsideRect.bottom ) / scale;
		}

		if ( mMaintainAspectRatio ) {
			if ( mInitialAspectRatio >= 1 ) { // width > height
				final float dy = rect.width() / mInitialAspectRatio;
				rect.bottom = rect.top + dy;
			} else { // height >= width
				final float dx = rect.height() * mInitialAspectRatio;
				rect.right = rect.left + dx;
			}
		}

		rect.sort();
		return rect;
	}

	/**
	 * Compute layout.
	 *
	 * @param adjust the adjust
	 * @return the rect
	 */
	public Rect computeLayout( boolean adjust ) {
		if ( adjust ) {
			adjustCropRect( mCropRect );
			RectF cRect = new RectF( 0, 0, mContext.getWidth(), mContext.getHeight() );
			mCropRect = adjustRealCropRect( mMatrix, mCropRect, cRect );
		}

		return getDisplayRect( mMatrix, mCropRect );
	}

	/**
	 * Gets the display rect.
	 *
	 * @param m the m
	 * @param supportRect the support rect
	 * @return the display rect
	 */
	protected Rect getDisplayRect( Matrix m, RectF supportRect ) {
		RectF r = new RectF( supportRect.left, supportRect.top, supportRect.right, supportRect.bottom );
		m.mapRect( r );
		return new Rect( Math.round( r.left ), Math.round( r.top ), Math.round( r.right ), Math.round( r.bottom ) );
	}

	/**
	 * Invalidate.
	 */
	public void invalidate() {
		mDrawRect = computeLayout( true );
	}

	/**
	 * Setup.
	 *
	 * @param m the m
	 * @param imageRect the image rect
	 * @param cropRect the crop rect
	 * @param maintainAspectRatio the maintain aspect ratio
	 */
	public void setup( Matrix m, Rect imageRect, RectF cropRect, boolean maintainAspectRatio ) {
		mMatrix = new Matrix( m );
		mCropRect = cropRect;
		mImageRect = new RectF( imageRect );
		mMaintainAspectRatio = maintainAspectRatio;
		mInitialAspectRatio = mCropRect.width() / mCropRect.height();
		mDrawRect = computeLayout( true );

		mOutlinePaint.setStrokeWidth( stroke_width );
		mOutlinePaint.setStyle( Paint.Style.STROKE );
		mOutlinePaint.setAntiAlias( false );
		ReflectionUtils.invokeMethod( mOutlinePaint, "setHinting", new Class<?>[]{ int.class }, 0 );
		//mOutlinePaint.setHinting( Paint.HINTING_OFF );

		mOutlinePaint2.setStrokeWidth( internal_stroke_width );
		mOutlinePaint2.setStyle( Paint.Style.STROKE );
		mOutlinePaint2.setAntiAlias( false );
		mOutlinePaint2.setColor( highlight_internal_color );
		ReflectionUtils.invokeMethod( mOutlinePaint2, "setHinting", new Class<?>[]{ int.class }, 0 );
		//mOutlinePaint2.setHinting( Paint.HINTING_OFF );

		mOutlineFill.setColor( highlight_outside_color );
		mOutlineFill.setStyle( Paint.Style.FILL );
		mOutlineFill.setAntiAlias( false );
		ReflectionUtils.invokeMethod( mOutlineFill, "setHinting", new Class<?>[]{ int.class }, 0 );
		//mOutlineFill.setHinting( Paint.HINTING_OFF );
		
		
		mLinesPaintShadow.setStrokeWidth( internal_stroke_width );
		mLinesPaintShadow.setAntiAlias( true );
		mLinesPaintShadow.setColor( Color.BLACK );
		mLinesPaintShadow.setStyle( Paint.Style.STROKE );
		mLinesPaintShadow.setMaskFilter( new BlurMaskFilter( 2, Blur.NORMAL ) );
		
		setMode( Mode.None );
		init();
	}

	/**
	 * Sets the aspect ratio.
	 *
	 * @param value the new aspect ratio
	 */
	public void setAspectRatio( float value ) {
		mInitialAspectRatio = value;
	}

	/**
	 * Sets the maintain aspect ratio.
	 *
	 * @param value the new maintain aspect ratio
	 */
	public void setMaintainAspectRatio( boolean value ) {
		mMaintainAspectRatio = value;
	}

	/**
	 * Update.
	 *
	 * @param imageMatrix the image matrix
	 * @param imageRect the image rect
	 */
	public void update( Matrix imageMatrix, Rect imageRect ) {
		mMatrix = new Matrix( imageMatrix );
		mImageRect = new RectF( imageRect );
		mDrawRect = computeLayout( true );
		mContext.invalidate();
	}

	/**
	 * Gets the matrix.
	 *
	 * @return the matrix
	 */
	public Matrix getMatrix() {
		return mMatrix;
	}

	/**
	 * Gets the draw rect.
	 *
	 * @return the draw rect
	 */
	public Rect getDrawRect() {
		return mDrawRect;
	}

	/**
	 * Gets the crop rect f.
	 *
	 * @return the crop rect f
	 */
	public RectF getCropRectF() {
		return mCropRect;
	}

	/**
	 * Returns the cropping rectangle in image space.
	 *
	 * @return the crop rect
	 */
	public Rect getCropRect() {
		return new Rect( (int) mCropRect.left, (int) mCropRect.top, (int) mCropRect.right, (int) mCropRect.bottom );
	}

}
