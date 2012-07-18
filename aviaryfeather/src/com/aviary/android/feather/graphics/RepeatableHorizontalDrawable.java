package com.aviary.android.feather.graphics;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Draw a bitmap repeated horizontally and scaled vertically.
 *
 * @author alessandro
 */
public class RepeatableHorizontalDrawable extends Drawable {
	private Paint mPaint;
	private Rect mRect;
	private Matrix mMatrix;
	private Shader mShader;
	private Bitmap mBitmap = null;
	
	/**
	 * Construct a new {@link RepeatableHorizontalDrawable} instance.
	 *
	 * @param resources the current Context {@link Resources} used to extract the resource
	 * @param resId the {@link BitmapDrawable} resource used to draw
	 */
	public RepeatableHorizontalDrawable( Resources resources, int resId ) {
		try {
			mBitmap = ( (BitmapDrawable) resources.getDrawable( resId ) ).getBitmap();
		} catch( Exception e ){}
		
		mPaint = new Paint();
		mRect = new Rect();
		mMatrix = new Matrix();
		
		if( mBitmap != null ){
			mShader = new BitmapShader( mBitmap, TileMode.REPEAT, TileMode.CLAMP );
			mPaint.setShader( mShader );
		}
	}
	
	/* (non-Javadoc)
	 * @see android.graphics.drawable.Drawable#draw(android.graphics.Canvas)
	 */
	@Override
	public void draw( Canvas canvas ) {
		copyBounds( mRect );
		canvas.drawPaint( mPaint );
	}
	
	/* (non-Javadoc)
	 * @see android.graphics.drawable.Drawable#onBoundsChange(android.graphics.Rect)
	 */
	@Override
	protected void onBoundsChange( Rect bounds ) {
		super.onBoundsChange( bounds );
		mMatrix.setScale( 1, (float)bounds.height() / mBitmap.getHeight() );
		mShader.setLocalMatrix( mMatrix );
	}

	/* (non-Javadoc)
	 * @see android.graphics.drawable.Drawable#getOpacity()
	 */
	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}

	/* (non-Javadoc)
	 * @see android.graphics.drawable.Drawable#setAlpha(int)
	 */
	@Override
	public void setAlpha( int alpha ) {
		mPaint.setAlpha( alpha );
	}

	/* (non-Javadoc)
	 * @see android.graphics.drawable.Drawable#setColorFilter(android.graphics.ColorFilter)
	 */
	@Override
	public void setColorFilter( ColorFilter cf ) {
		mPaint.setColorFilter( cf );
	}
}
