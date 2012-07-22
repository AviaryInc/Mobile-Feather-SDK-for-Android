package com.aviary.android.feather.effects;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.aviary.android.feather.effects.AbstractEffectPanel.OptionPanel;
import com.aviary.android.feather.library.services.EffectContext;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractOptionPanel.
 */
abstract class AbstractOptionPanel extends AbstractEffectPanel implements OptionPanel {

	/** The current option view. */
	protected ViewGroup mOptionView;

	/**
	 * Instantiates a new abstract option panel.
	 *
	 * @param context the context
	 */
	public AbstractOptionPanel( EffectContext context ) {
		super( context );
	}

	/* (non-Javadoc)
	 * @see com.aviary.android.feather.effects.AbstractEffectPanel.OptionPanel#getOptionView(android.view.LayoutInflater, android.view.ViewGroup)
	 */
	@Override
	public final ViewGroup getOptionView( LayoutInflater inflater, ViewGroup parent ) {
		mOptionView = generateOptionView( inflater, parent );
		return mOptionView;
	}

	/**
	 * Gets the panel option view.
	 *
	 * @return the option view
	 */
	public final ViewGroup getOptionView() {
		return mOptionView;
	}

	/* (non-Javadoc)
	 * @see com.aviary.android.feather.effects.AbstractEffectPanel#onDispose()
	 */
	@Override
	protected void onDispose() {
		mOptionView = null;
		super.onDispose();
	}
	
	/* (non-Javadoc)
	 * @see com.aviary.android.feather.effects.AbstractEffectPanel#setEnabled(boolean)
	 */
	@Override
	public void setEnabled( boolean value ) {
		getOptionView().setEnabled( value );
		super.setEnabled( value );
	}

	/**
	 * Generate option view.
	 *
	 * @param inflater the inflater
	 * @param parent the parent
	 * @return the view group
	 */
	protected abstract ViewGroup generateOptionView( LayoutInflater inflater, ViewGroup parent );

}
