package com.aviary.android.feather.effects;

import org.json.JSONException;
import android.R.attr;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.aviary.android.feather.R;
import com.aviary.android.feather.graphics.CropCheckboxDrawable;
import com.aviary.android.feather.graphics.DefaultGalleryCheckboxDrawable;
import com.aviary.android.feather.library.filters.NativeFilterProxy;
import com.aviary.android.feather.library.moa.MoaAction;
import com.aviary.android.feather.library.moa.MoaActionFactory;
import com.aviary.android.feather.library.moa.MoaActionList;
import com.aviary.android.feather.library.moa.MoaPointParameter;
import com.aviary.android.feather.library.moa.MoaResult;
import com.aviary.android.feather.library.services.ConfigService;
import com.aviary.android.feather.library.services.EffectContext;
import com.aviary.android.feather.library.utils.SystemUtils;
import com.aviary.android.feather.utils.UIUtils;
import com.aviary.android.feather.widget.AdapterView;
import com.aviary.android.feather.widget.CropImageView;
import com.aviary.android.feather.widget.CropImageView.OnHighlightSingleTapUpConfirmedListener;
import com.aviary.android.feather.widget.Gallery;
import com.aviary.android.feather.widget.Gallery.OnItemsScrollListener;

// TODO: Auto-generated Javadoc
/**
 * The Class CropPanel.
 */
public class CropPanel extends AbstractContentPanel implements OnHighlightSingleTapUpConfirmedListener {

	Gallery mGallery;
	String[] mCropNames, mCropValues;
	View mSelected;
	int mSelectedPosition = 0;
	boolean mInverted;

	/**
	 * Instantiates a new crop panel.
	 *
	 * @param context the context
	 */
	public CropPanel( EffectContext context ) {
		super( context );
	}

	/* (non-Javadoc)
	 * @see com.aviary.android.feather.effects.AbstractEffectPanel#onCreate(android.graphics.Bitmap)
	 */
	@Override
	public void onCreate( Bitmap bitmap ) {
		super.onCreate( bitmap );
		ConfigService config = getContext().getService( ConfigService.class );
		
		mCropNames = config.getStringArray( R.array.feather_crop_names );
		mCropValues = config.getStringArray( R.array.feather_crop_values );
		
		mSelectedPosition = config.getInteger( R.integer.feather_crop_selected_value );

		mImageView = (CropImageView) getContentView().findViewById( R.id.crop_image_view );
		mImageView.setDoubleTapEnabled( false );
		
		int minAreaSize = config.getInteger( R.integer.feather_crop_min_size );
		((CropImageView) mImageView).setMinCropSize( minAreaSize );
		
		final boolean invert_enabled = config.getBoolean( R.integer.feather_crop_allow_inverse );
		
		if( invert_enabled ){
			((CropImageView) mImageView).setOnHighlightSingleTapUpConfirmedListener( this );
		}

		mGallery = (Gallery) getOptionView().findViewById( R.id.gallery );
		mGallery.setCallbackDuringFling( false );
		mGallery.setSpacing( 0 );
		mGallery.setAdapter( new GalleryAdapter( getContext().getBaseContext(), mCropNames ) );
		mGallery.setOnItemsScrollListener( new OnItemsScrollListener() {

			@Override
			public void onScrollFinished( AdapterView<?> parent, View view, int position, long id ) {
				mInverted = false;
				updateSelection( view, position );

				double ratio = calculateAspectRatio( position, false );
				setCustomRatio( ratio, ratio != 0 );
				mInverted = false;
			}

			@Override
			public void onScrollStarted( AdapterView<?> parent, View view, int position, long id ) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll( AdapterView<?> parent, View view, int position, long id ) {
				// TODO Auto-generated method stub

			}
		} );

		mGallery.setSelection( mSelectedPosition, false );
	}

	/* (non-Javadoc)
	 * @see com.aviary.android.feather.effects.AbstractEffectPanel#onActivate()
	 */
	@Override
	public void onActivate() {
		super.onActivate();

		int position = mGallery.getSelectedItemPosition();
		double ratio = calculateAspectRatio( position, false );
		createCropView( ratio, ratio != 0 );
		
		updateSelection( (View) mGallery.getSelectedView(), mGallery.getSelectedItemPosition() );
		
		setIsChanged( true );
		contentReady();
		mInverted = false;
	}

	/**
	 * Calculate aspect ratio.
	 *
	 * @param position the position
	 * @param inverted the inverted
	 * @return the double
	 */
	private double calculateAspectRatio( int position, boolean inverted ) {

		String value = mCropValues[position];
		String[] values = value.split( ":" );

		if ( values.length == 2 ) {
			int aspectx = Integer.parseInt( inverted ? values[1] : values[0] );
			int aspecty = Integer.parseInt( inverted ? values[0] : values[1] );

			if ( aspectx == -1 ) {
				aspectx = inverted ? mBitmap.getHeight() : mBitmap.getWidth();
			}

			if ( aspecty == -1 ) {
				aspecty = inverted ? mBitmap.getWidth() : mBitmap.getHeight();
			}

			double ratio = 0;

			if ( aspectx != 0 && aspecty != 0 ) {
				ratio = (double) aspectx / (double) aspecty;
			}
			return ratio;
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.aviary.android.feather.effects.AbstractEffectPanel#onDestroy()
	 */
	@Override
	public void onDestroy() {
		mImageView.clear();
		((CropImageView) mImageView).setOnHighlightSingleTapUpConfirmedListener( null );
		super.onDestroy();
	}

	/* (non-Javadoc)
	 * @see com.aviary.android.feather.effects.AbstractEffectPanel#onDeactivate()
	 */
	@Override
	public void onDeactivate() {
		super.onDeactivate();
	}

	/**
	 * Creates the crop view.
	 *
	 * @param aspectRatio the aspect ratio
	 */
	private void createCropView( double aspectRatio, boolean isFixed ) {
		( (CropImageView) mImageView ).setImageBitmap( mBitmap, aspectRatio, isFixed );
	}

	/**
	 * Sets the custom ratio.
	 *
	 * @param aspectRatio the aspect ratio
	 * @param isFixed the is fixed
	 */
	private void setCustomRatio( double aspectRatio, boolean isFixed ) {
		( (CropImageView) mImageView ).setAspectRatio( aspectRatio, isFixed );
	}

	/**
	 * Update selection.
	 *
	 * @param newSelection the new selection
	 * @param position the position
	 */
	protected void updateSelection( View newSelection, int position ) {
		//mLogger.info( "updateSelection: " + newSelection + ", position: " + position );
		
		if ( mSelected != null ) {
			
			String label = (String) mSelected.getTag();
			if( label != null ){
				View textview = mSelected.findViewById( R.id.text );
				if( null != textview ){
					((TextView) textview).setText( getString( label ) );
				}
			}
			
			mSelected.setSelected( false );
		}

		mSelected = newSelection;
		mSelectedPosition = position;

		if ( mSelected != null ) {
			mSelected = newSelection;
			mSelected.setSelected( true );
		}
	}

	/* (non-Javadoc)
	 * @see com.aviary.android.feather.effects.AbstractEffectPanel#onGenerateResult()
	 */
	@Override
	protected void onGenerateResult() {
		Rect crop_rect = ( (CropImageView) mImageView ).getHighlightView().getCropRect();
		GenerateResultTask task = new GenerateResultTask( crop_rect );
		task.execute( mBitmap );
	}

	/**
	 * Generate bitmap.
	 *
	 * @param bitmap the bitmap
	 * @param cropRect the crop rect
	 * @return the bitmap
	 */
	@SuppressWarnings("unused")
	private Bitmap generateBitmap( Bitmap bitmap, Rect cropRect ) {
		Bitmap croppedImage;

		int width = cropRect.width();
		int height = cropRect.height();

		croppedImage = Bitmap.createBitmap( width, height, Bitmap.Config.RGB_565 );
		Canvas canvas = new Canvas( croppedImage );
		Rect dstRect = new Rect( 0, 0, width, height );
		canvas.drawBitmap( mBitmap, cropRect, dstRect, null );
		return croppedImage;
	}

	/**
	 * The Class GenerateResultTask.
	 */
	class GenerateResultTask extends AsyncTask<Bitmap, Void, Bitmap> {

		/** The m crop rect. */
		Rect mCropRect;
		MoaActionList mActionList;

		/**
		 * Instantiates a new generate result task.
		 *
		 * @param rect the rect
		 */
		public GenerateResultTask( Rect rect ) {
			mCropRect = rect;
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPreExecute()
		 */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			onProgressModalStart();
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Bitmap doInBackground( Bitmap... arg0 ) {

			final Bitmap bitmap = arg0[0];
			
			//mLogger.info( "input: " + bitmap );
			mActionList = MoaActionFactory.actionList( "crop" );
			final MoaAction action = mActionList.get( 0 );

			MoaPointParameter topLeft = new MoaPointParameter();
			topLeft.setValue( (double) mCropRect.left / bitmap.getWidth(), (double) mCropRect.top / bitmap.getHeight() );

			MoaPointParameter size = new MoaPointParameter();
			size.setValue( (double) mCropRect.width() / bitmap.getWidth(), (double) mCropRect.height() / bitmap.getHeight() );

			action.setValue( "upperleftpoint", topLeft );
			action.setValue( "size", size );

			try {
				MoaResult result = NativeFilterProxy.prepareActions( mActionList, arg0[0], null, -1, -1 );
				result.execute();
				return result.outputBitmap;
			} catch ( JSONException e ) {
				e.printStackTrace();
			}
			mLogger.warning( "returning input bitmap!" );
			return arg0[0];
		}

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute( Bitmap result ) {
			super.onPostExecute( result );
			
			mLogger.info( "result: " + result );
			mLogger.log( "result.size: " + result.getWidth() + "x" + result.getHeight() );

			onProgressModalEnd();
			
			( (CropImageView) mImageView ).setImageBitmap( result, ( (CropImageView) mImageView ).getAspectRatio(), ( (CropImageView) mImageView ).getAspectRatioIsFixed() );
			( (CropImageView) mImageView ).setHighlightView( null );
			
			onComplete( result, mActionList );
		}
	}

	/* (non-Javadoc)
	 * @see com.aviary.android.feather.effects.AbstractContentPanel#generateContentView(android.view.LayoutInflater)
	 */
	@Override
	protected View generateContentView( LayoutInflater inflater ) {
		View view = inflater.inflate( R.layout.feather_crop_content, null );
		
		if( SystemUtils.isHoneyComb() ){
			// Honeycomb bug with canvas clip
			view.setLayerType( View.LAYER_TYPE_SOFTWARE, null );
		}
		
		return view;
	}

	/* (non-Javadoc)
	 * @see com.aviary.android.feather.effects.AbstractOptionPanel#generateOptionView(android.view.LayoutInflater, android.view.ViewGroup)
	 */
	@Override
	protected ViewGroup generateOptionView( LayoutInflater inflater, ViewGroup parent ) {
		return (ViewGroup) inflater.inflate( R.layout.feather_crop_panel, parent, false );
	}
	
	/* (non-Javadoc)
	 * @see com.aviary.android.feather.effects.AbstractContentPanel#getContentDisplayMatrix()
	 */
	@Override
	public Matrix getContentDisplayMatrix() {
		return mImageView.getDisplayMatrix();
	}
	
	private String getString( String input ){
		int id = getContext().getBaseContext().getResources().getIdentifier( input, "string", getContext().getBaseContext().getPackageName() );
		if( id > 0 ){
			return getContext().getBaseContext().getResources().getString( id );
		}
		return input;
	}

	/**
	 * The Class GalleryAdapter.
	 */
	class GalleryAdapter extends BaseAdapter {

		/** The m strings. */
		private String[] mStrings;
		
		/** The m layout inflater. */
		LayoutInflater mLayoutInflater;
		
		/** The m res. */
		Resources mRes;

		/**
		 * Instantiates a new gallery adapter.
		 *
		 * @param context the context
		 * @param values the values
		 */
		public GalleryAdapter( Context context, String[] values ) {
			mLayoutInflater = UIUtils.getLayoutInflater();
			mStrings = values;
			mRes = getContext().getBaseContext().getResources();
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			return mStrings.length;
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public Object getItem( int position ) {
			return mStrings[position];
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId( int position ) {
			return 0;
		}
		
		/* (non-Javadoc)
		 * @see android.widget.BaseAdapter#getViewTypeCount()
		 */
		@Override
		public int getViewTypeCount() {
			return 2;
		}
		
		/* (non-Javadoc)
		 * @see android.widget.BaseAdapter#getItemViewType(int)
		 */
		@Override
		public int getItemViewType( int position ) {
			final boolean valid = position >= 0 && position < getCount();
			return valid ? 0 : 1;
		}

		/* (non-Javadoc)
		 * @see android.widget.Adapter#getView(int, android.view.View, android.view.ViewGroup)
		 */
		@Override
		public View getView( int position, View convertView, ViewGroup parent ) {
			
			final boolean valid = position >= 0 && position < getCount();
			View view;
			
			if ( convertView == null ) {
				if( valid ) {
					// use the default crop checkbox view
					view = mLayoutInflater.inflate( R.layout.feather_crop_button, mGallery, false );
					StateListDrawable st = new StateListDrawable();
					Drawable unselectedBackground = new CropCheckboxDrawable( mRes, false, null, 1.0f, 0, 0 );
					Drawable selectedBackground = new CropCheckboxDrawable( mRes, true, null, 1.0f, 0, 0 );
					st.addState( new int[] { -attr.state_selected }, unselectedBackground );
					st.addState( new int[] { attr.state_selected }, selectedBackground );
					view.setBackgroundDrawable( st );
				} else {
					// use the blank view
					view = mLayoutInflater.inflate( R.layout.feather_checkbox_button, mGallery, false );
					Drawable unselectedBackground = new DefaultGalleryCheckboxDrawable( mRes, false );
					view.setBackgroundDrawable( unselectedBackground );
				}
			} else {
				view = convertView;
			}
			
			if( valid ){
				view.setTag( getItem( position ) );
				((TextView) view.findViewById( R.id.text )).setText( getString( mStrings[position] ) );
			}
			view.setSelected( mSelectedPosition == position );
			return view;
		}
	}

	/* (non-Javadoc)
	 * @see com.aviary.android.feather.widget.CropImageView.OnHighlightSingleTapUpConfirmedListener#onSingleTapUpConfirmed()
	 */
	@Override
	public void onSingleTapUpConfirmed() {
		if( mSelected != null ){
			mInverted = !mInverted;
			double originalRatio = calculateAspectRatio( mSelectedPosition, mInverted );
			double ratio = originalRatio;
			
			if( ratio == 0 ){
				CropImageView view = (CropImageView)mImageView;
				if( view.getHighlightView() != null ){
					RectF rect = view.getHighlightView().getCropRectF();
					final float w = rect.width();
					final float h = rect.height();
					
					mLogger.log( "inverted: " + mInverted + ", rect: " + (int)w + "x" + (int)h );
					
					if( mInverted ){
						if( w > h )
							ratio = h / w;
						else
							ratio = w / h;
					} else {
						if( w > h )
							ratio = w / h;
						else
							ratio = h / w;
					}
				}
			}
			
			String currentLabel = mCropNames[mSelectedPosition];
			((TextView) mSelected.findViewById( R.id.text )).setText( getString(currentLabel) + ( mInverted ? "*" : "" ) );
			setCustomRatio( ratio, originalRatio != 0 );
		}
	}
}
