package com.aviary.android.feather.effects;

import org.json.JSONException;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import com.aviary.android.feather.R;
import com.aviary.android.feather.library.filters.AdjustFilter;
import com.aviary.android.feather.library.filters.FilterLoaderFactory.Filters;
import com.aviary.android.feather.library.filters.FilterService;
import com.aviary.android.feather.library.moa.MoaActionList;
import com.aviary.android.feather.library.services.ConfigService;
import com.aviary.android.feather.library.services.EffectContext;
import com.aviary.android.feather.widget.AdjustImageView;
import com.aviary.android.feather.widget.AdjustImageView.FlipType;
import com.aviary.android.feather.widget.AdjustImageView.OnResetListener;

// TODO: Auto-generated Javadoc
/**
 * The Class AdjustEffectPanel.
 */
public class AdjustEffectPanel extends AbstractContentPanel implements OnClickListener, OnResetListener {

	private AdjustImageView mView;
	private int animDuration = 400;
	private int resetAnimDuration = 200;
	boolean isClosing;
	
	/** The enable3 d animation. */
	boolean enable3DAnimation;
	boolean enableFreeRotate;

	/**
	 * Instantiates a new adjust effect panel.
	 *
	 * @param context the context
	 * @param adjust the adjust
	 */
	public AdjustEffectPanel( EffectContext context, Filters adjust ) {
		super( context );

		FilterService service = context.getService( FilterService.class );
		mFilter = service.load( adjust );
	}

	/* (non-Javadoc)
	 * @see com.aviary.android.feather.effects.AbstractEffectPanel#onCreate(android.graphics.Bitmap)
	 */
	@Override
	public void onCreate( Bitmap bitmap ) {
		super.onCreate( bitmap );
		
		ConfigService config = getContext().getService( ConfigService.class );
		if( null != config ){
			animDuration = config.getInteger( R.integer.feather_adjust_tool_anim_time );
			resetAnimDuration = config.getInteger( R.integer.feather_adjust_tool_reset_anim_time );
			enable3DAnimation = config.getBoolean( R.integer.feather_adjust_tool_enable_3d_flip );
			enableFreeRotate = config.getBoolean( R.integer.feather_rotate_enable_free_rotate );
		} else {
			enable3DAnimation = false;
			enableFreeRotate = false;
		}
		
		mView = (AdjustImageView) getContentView().findViewById( R.id.image );
		mView.setResetAnimDuration( resetAnimDuration );
		mView.setCameraEnabled( enable3DAnimation );
		mView.setEnableFreeRotate( enableFreeRotate );
	}

	/* (non-Javadoc)
	 * @see com.aviary.android.feather.effects.AbstractEffectPanel#onActivate()
	 */
	@Override
	public void onActivate() {
		super.onActivate();

		mView.setImageBitmap( mBitmap );
		mView.setOnResetListener( this );

		View v = getOptionView();
		v.findViewById( R.id.button1 ).setOnClickListener( this );
		v.findViewById( R.id.button2 ).setOnClickListener( this );
		v.findViewById( R.id.button3 ).setOnClickListener( this );
		v.findViewById( R.id.button4 ).setOnClickListener( this );
		contentReady();
	}

	/* (non-Javadoc)
	 * @see com.aviary.android.feather.effects.AbstractEffectPanel#onDeactivate()
	 */
	@Override
	public void onDeactivate() {
		mView.setOnResetListener( null );
		super.onDeactivate();
	}

	/* (non-Javadoc)
	 * @see com.aviary.android.feather.effects.AbstractEffectPanel#onDestroy()
	 */
	@Override
	public void onDestroy() {
		mView.setImageBitmap( null );
		super.onDestroy();
	}

	/* (non-Javadoc)
	 * @see com.aviary.android.feather.effects.AbstractOptionPanel#generateOptionView(android.view.LayoutInflater, android.view.ViewGroup)
	 */
	@Override
	protected ViewGroup generateOptionView( LayoutInflater inflater, ViewGroup parent ) {
		return (ViewGroup) inflater.inflate( R.layout.feather_adjust_panel, parent, false );
	}

	/* (non-Javadoc)
	 * @see com.aviary.android.feather.effects.AbstractContentPanel#generateContentView(android.view.LayoutInflater)
	 */
	@Override
	protected View generateContentView( LayoutInflater inflater ) {
		return inflater.inflate( R.layout.feather_adjust_content, null );
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick( View v ) {

		if ( !isActive() || !isEnabled() ) return;

		final int id = v.getId();

		if ( id == R.id.button1 ) {
			mView.rotate90( false, animDuration );
		} else if ( id == R.id.button2 ) {
			mView.rotate90( true, animDuration );
		} else if ( id == R.id.button3 ) {
			mView.flip( true, animDuration );
		} else if ( id == R.id.button4 ) {
			mView.flip( false, animDuration );
		}
	}

	/* (non-Javadoc)
	 * @see com.aviary.android.feather.effects.AbstractEffectPanel#getIsChanged()
	 */
	@Override
	public boolean getIsChanged() {
		mLogger.info( "getIsChanged" );

		final int rotation = (int) mView.getRotation();
		final int flip_type = mView.getFlipType();
		return rotation != 0 || ( flip_type != FlipType.FLIP_NONE.nativeInt );
	}

	/* (non-Javadoc)
	 * @see com.aviary.android.feather.effects.AbstractContentPanel#getContentDisplayMatrix()
	 */
	@Override
	public Matrix getContentDisplayMatrix() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.aviary.android.feather.effects.AbstractEffectPanel#onGenerateResult()
	 */
	@Override
	protected void onGenerateResult() {
		mLogger.info( "onGenerateResult" );

		final int rotation = (int) mView.getRotation();
		final boolean horizontal = mView.getHorizontalFlip();
		final boolean vertical = mView.getVerticalFlip();
		
		AdjustFilter filter = (AdjustFilter) mFilter;
		filter.setRotation( rotation );
		filter.setFlip( horizontal, vertical );
		
		try {
			final Bitmap result = filter.execute( mBitmap, null, -1, -1 );
			mView.setImageBitmap( result );
			onComplete( result, (MoaActionList) filter.getActions().clone() );
		} catch ( JSONException e ) {
			e.printStackTrace();
			onGenericError( e );
		}
		
	}

	/* (non-Javadoc)
	 * @see com.aviary.android.feather.effects.AbstractEffectPanel#onCancel()
	 */
	@Override
	public boolean onCancel() {
		if ( isClosing ) return true;

		isClosing = true;
		setEnabled( false );

		final int rotation = (int) mView.getRotation();
		final boolean hflip = mView.getHorizontalFlip();
		final boolean vflip = mView.getVerticalFlip();

		if ( rotation != 0 || hflip || vflip ) {
			mView.reset();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Reset animation is complete, now it is safe
	 * to call the parent {@link EffectContext#cancel()} method
	 * and close the panel.
	 */
	@Override
	public void onResetComplete() {
		getContext().cancel();
	}
}
