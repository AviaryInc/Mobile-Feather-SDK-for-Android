package com.aviary.launcher;

import java.io.IOException;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Images.ImageColumns;
import com.aviary.android.feather.library.media.ExifInterfaceWrapper;
import com.aviary.android.feather.library.utils.CameraUtils;
import com.aviary.android.feather.library.utils.IOUtils;
import com.aviary.android.feather.library.utils.StringUtils;


public class ImageInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static final int INDEX_CAPTION = 1;
	private static final int INDEX_LATITUDE = 3;
	private static final int INDEX_LONGITUDE = 4;
	private static final int INDEX_DATA = 8;
	private static final int INDEX_ORIENTATION = 9;
	private static final int INDEX_SIZE_ID = 11;
	private static final int INDEX_BUCKET_TITLE = 12;

	public static final String[] PROJECTION = { 
		ImageColumns._ID, // 0
		ImageColumns.TITLE, // 1
		ImageColumns.MIME_TYPE, // 2
		ImageColumns.LATITUDE, // 3
		ImageColumns.LONGITUDE, // 4
		ImageColumns.DATE_TAKEN, // 5
		ImageColumns.DATE_ADDED, // 6
		ImageColumns.DATE_MODIFIED, // 7
		ImageColumns.DATA, // 8
		ImageColumns.ORIENTATION, // 9
		ImageColumns.BUCKET_ID, // 10
		ImageColumns.SIZE, // 11
		ImageColumns.BUCKET_DISPLAY_NAME, // 12
	};
	
	
	private String aperture;
	private String dateTime;
	private String exposureTime;
	private String iso;
	private String maker;
	private String model;
	private String focalLength;
	private int flash;
	private int whiteBalance;
	private double altitude;
	private int width, height;
	private Address address;
	private String artist;
	private String brightness;
	private String copyright;
	private String digitalZoom;
	private int exposureMode;
	private int exposureProgram;
	private int lightSource;
	private int meteringMode;
	private String software;
	private String distance;
	private int distanceRange;
	private int rotation = 0;
	private float latitude = INVALID_LATLNG;
	private float longitude = INVALID_LATLNG;
	private String caption;
	private long fileSize = 0L;
	private String filePath;
	private String bucketDisplayName;	

	public static final float INVALID_LATLNG = 0f;

	public ImageInfo() {}

	public ImageInfo( Context context, Uri uri ) throws IOException {

		String path;
		path = IOUtils.getRealFilePath( context, uri );
		
		onLoadFromUri( context, uri );
		
		if( null != path ){
			onLoadExifData( path );
			onLoadImageSize( path, rotation );
		}
	}
	
	private void onLoadFromUri( Context context, Uri imageUri ){
		Uri uri = Images.Media.EXTERNAL_CONTENT_URI;
		Cursor cursor;
		
		if( ContentResolver.SCHEME_CONTENT.equals( imageUri.getScheme()) ){
			cursor = context.getContentResolver().query( imageUri, PROJECTION, null, null, null );
		} else {
			cursor = context.getContentResolver().query( uri, PROJECTION, ImageColumns.DATA + " LIKE '%" + imageUri.toString() + "%'", null, null );
		}
		
		if( null != cursor ){
			if( cursor.moveToFirst() ){
				onLoadFromCursor( cursor );
			}
			cursor.close();
		}
	}
	
	protected void onLoadFromCursor( Cursor cursor ) {
		caption = cursor.getString( INDEX_CAPTION );
		latitude = cursor.getFloat( INDEX_LATITUDE );
		longitude = cursor.getFloat( INDEX_LONGITUDE );
		filePath = cursor.getString( INDEX_DATA );
		rotation = cursor.getInt( INDEX_ORIENTATION );
		fileSize = cursor.getLong( INDEX_SIZE_ID );
		bucketDisplayName = cursor.getString( INDEX_BUCKET_TITLE );
	}	

	private void onLoadExifData( final String path ) throws IOException {

		ExifInterfaceWrapper exif = new ExifInterfaceWrapper( path );

		altitude = exif.getAltitude( -1 );
		if ( rotation == 0 ) rotation = exif.getOrientation();

		aperture = exif.getAttribute( ExifInterfaceWrapper.TAG_APERTURE );
		artist = exif.getAttribute( ExifInterfaceWrapper.TAG_ARTIST );
		brightness = exif.getAttribute( ExifInterfaceWrapper.TAG_BRIGHTNESS_VALUE );
		copyright = exif.getAttribute( ExifInterfaceWrapper.TAG_COPYRIGHT );
		dateTime = exif.getAttribute( ExifInterfaceWrapper.TAG_DATETIME );
		digitalZoom = exif.getAttribute( ExifInterfaceWrapper.TAG_DIGITAL_ZOOM );
		exposureTime = exif.getAttribute( ExifInterfaceWrapper.TAG_EXPOSURE_TIME );
		exposureMode = exif.getAttributeInt( ExifInterfaceWrapper.TAG_EXPOSURE_MODE, -1 );
		exposureProgram = exif.getAttributeInt( ExifInterfaceWrapper.TAG_EXPOSURE_PROGRAM, -1 );
		flash = exif.getAttributeInt( ExifInterfaceWrapper.TAG_FLASH, 0 );
		focalLength = exif.getAttribute( ExifInterfaceWrapper.TAG_FOCAL_LENGTH );
		iso = exif.getAttribute( ExifInterfaceWrapper.TAG_ISO );
		lightSource = exif.getAttributeInt( ExifInterfaceWrapper.TAG_LIGHTSOURCE, -1 );
		maker = exif.getAttribute( ExifInterfaceWrapper.TAG_MAKE );
		model = exif.getAttribute( ExifInterfaceWrapper.TAG_MODEL );
		meteringMode = exif.getAttributeInt( ExifInterfaceWrapper.TAG_METERING_MODE, -1 );
		software = exif.getAttribute( ExifInterfaceWrapper.TAG_SOFTWARE );
		distance = exif.getAttribute( ExifInterfaceWrapper.TAG_SUBJECT_DISTANCE );
		distanceRange = exif.getAttributeInt( ExifInterfaceWrapper.TAG_SUBJECT_DISTANCE_RANGE, -1 );
		whiteBalance = exif.getAttributeInt( ExifInterfaceWrapper.TAG_WHITE_BALANCE, -1 );

		float[] lat = new float[] { INVALID_LATLNG, INVALID_LATLNG };
		exif.getLatLong( lat );

		if ( lat[0] != INVALID_LATLNG ) {
			latitude = lat[0];
			longitude = lat[1];
		}
	}

	void onLoadImageSize( String path, int orientation ) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;

		try {
			BitmapFactory.decodeFile( path, options );
		} catch ( Throwable t ) {
			return;
		}

		width = options.outWidth;
		height = options.outHeight;

		if ( orientation == 90 || orientation == 270 ) {
			width = options.outHeight;
			height = options.outWidth;
		}
	}
	
	public void getLatLong( float[] latlong ) {
		latlong[0] = latitude;
		latlong[1] = longitude;
	}
	
	public void setAddress( Address value ) {
		address = value;
	}	

	public String getAddressRepr() {
		if ( null != address ) {
			List<String> lines = new ArrayList<String>();
			if ( null != address.getThoroughfare() ) lines.add( address.getThoroughfare() );
			if ( null != address.getPostalCode() ) lines.add( address.getPostalCode() );
			if ( null != address.getLocality() ) lines.add( address.getLocality() );
			if ( null != address.getAdminArea() ) lines.add( address.getAdminArea() );
			if ( null != address.getCountryCode() ) lines.add( address.getCountryCode() );

			return StringUtils.join( lines, ", " );
		}
		return null;
	}

	public List<Info> getInfo() {
		List<Info> result = new ArrayList<Info>();
		double dValue;
		String sValue;
		NumberFormat decimalFormatter = DecimalFormat.getNumberInstance();

		if ( null != caption ) result.add( new Info( "Title", caption ) );
		if ( null != bucketDisplayName ) result.add( new Info( "Album", bucketDisplayName ) );
		if ( null != dateTime ) result.add( new Info( "Date Modified", dateTime ) );
		
		if ( width > 0 && height > 0 )
			result.add( new Info( "Dimension", width + "x" + height + " (" + CameraUtils.getMegaPixels( width, height ) + "MP)" ) );
		
		Info addressInfo = new Info( "Address", "" );
		boolean shouldAdd = false;
		
		float[] latlong = new float[] { INVALID_LATLNG, INVALID_LATLNG };
		getLatLong( latlong );
		if ( latlong[0] != INVALID_LATLNG ) {
			shouldAdd = true;
			addressInfo.rawData = latlong;
			addressInfo.value = latlong[0] + ", " + latlong[1];
		
			if ( null != address ) {
				String value = getAddressRepr();
				if ( null != value ) {
					shouldAdd = true;
					addressInfo.value = value;
				}
			}
		}
				
		if( shouldAdd ){
			result.add( addressInfo );
		}
		
		
		if( altitude != -1 ) result.add( new Info( "Altitude", String.valueOf( altitude ) + " mt" ));
		if ( rotation != 0 ) result.add( new Info( "Orientation", String.valueOf( rotation ) ) );
		if ( fileSize > 0 ) result.add( new Info( "Size", readableFileSize( fileSize ) ) );
		if ( null != maker ) result.add( new Info( "Camera", maker ) );
		if ( null != model ) result.add( new Info( "Model", model ) );
		if ( flash != 0 ) {
			String flashMode = computeFlash( flash );
			if( null != flashMode ){
				result.add( new Info( "Flash", flashMode ) );
			}
		}
		
		if( whiteBalance > -1 ){
			sValue = computeWhiteBalance( whiteBalance );
			if( null != sValue ){
				result.add( new Info( "White balance", sValue ) );
			}
		}
		
		StringBuilder sb = new StringBuilder();
		
		if ( null != aperture ){
			dValue = ExifInterfaceWrapper.convertRationalToDouble( aperture );
			if( dValue > 0 ){
				decimalFormatter.setMaximumFractionDigits( 1 );
				sb.append( "F/" + decimalFormatter.format( dValue ) + " " );
			}
		}
		
		if ( null != focalLength ) {
			dValue = ExifInterfaceWrapper.convertRationalToDouble( focalLength );
			if( dValue > 0 ){
				sb.append( dValue + "mm " );
			}
		}		
		
		if( null != exposureTime ){
			result.add( new Info("Exposure speed", exposureTime ));
		}
		
		if ( null != iso ){
			sb.append( "ISO-" + iso );
		}
		
		if( sb.length() > 0 ){
			result.add( new Info( "EXIF", sb.toString() ) );
		}
		
		if( exposureMode > -1 ){
			sValue = computeExposureMode( exposureMode );
			if( null != sValue ){
				result.add( new Info("Exposure Mode", sValue ) );
			}
		}
		
		if( exposureProgram > -1 ){
			sValue = computeExposureProgram( exposureProgram );
			if( null != sValue ){
				result.add( new Info("Exposure Program", sValue ) );
			}
		}
		
		if( lightSource > 0 ){
			sValue = computeLightSource( lightSource );
			if( null != sValue ){
				result.add( new Info("LightSource", sValue));
			}
		}
		
		if( null != artist ){
			result.add( new Info( "Artist", artist ));
		}
		
		if( null != copyright ){
			result.add( new Info("Copyright", copyright ) );
		}
		
		if( null != software ){
			result.add( new Info("Software", software ) );
		}
		
		if( null != brightness ){
			dValue = ExifInterfaceWrapper.convertRationalToDouble( brightness );
			if( dValue != 0 ){
				decimalFormatter.setMaximumFractionDigits( 2 );
				result.add( new Info("Brightness", decimalFormatter.format( dValue ) ));
			}
		}
		
		if( meteringMode != 0 ){
			sValue = computeMeteringMode( meteringMode );
			if( null != sValue ){
				result.add( new Info( "Metering Mode", sValue ) );
			}
		}
		
		if( null != digitalZoom ){
			dValue = ExifInterfaceWrapper.convertRationalToDouble( digitalZoom );
			if( dValue > 0 ){
				result.add( new Info("Digital zoom", (int)dValue + "X") );
			}
		}
		
		if( null != distance ){
			dValue = ExifInterfaceWrapper.convertRationalToDouble( distance );
			if( dValue > 0 ){
				result.add( new Info("Subject distance", decimalFormatter.format( dValue ) + "mt"));
			}
		}
		
		if( distanceRange > 0 ){
			sValue = computeDistanceRangeType( distanceRange );
			if( null != sValue ){
				result.add( new Info("Subject distance range", sValue ) );
			}
		}
		
		if ( null != filePath ) result.add( new Info( "Path", filePath ) );
		return result;
	}
	
	private String computeDistanceRangeType( int value ){
		switch( value ){
			case ExifInterfaceWrapper.SUBJECT_DISTANCE_RANGE_CLOSE:
				return "Close";
			case ExifInterfaceWrapper.SUBJECT_DISTANCE_RANGE_DISTANT:
				return "Distant";
			case ExifInterfaceWrapper.SUBJECT_DISTANCE_RANGE_MACRO:
				return "Macro";
		}
		return null;
	}
	
	private String computeMeteringMode( int value ){
		switch( value ){
			case ExifInterfaceWrapper.METERING_AVERAGE:
				return "Average";
			case ExifInterfaceWrapper.METERING_CENTER_WEIGHT_AVERAGE:
				return "Center weight average";
			case ExifInterfaceWrapper.METERING_MULTI_SPOT:
				return "Multi spot";
			case ExifInterfaceWrapper.METERING_OTHER:
				return "Other";
			case ExifInterfaceWrapper.METERING_PARTIAL:
				return "Partial";
			case ExifInterfaceWrapper.METERING_PATTERN:
				return "Pattern";
			case ExifInterfaceWrapper.METERING_SPOT:
				return "Spot";
		}
		return null;
	}
	
	private String computeLightSource( int value ) {
		switch ( value ) {
			case ExifInterfaceWrapper.LIGHTSOURCE_DAYLIGHT:
				return "Day light";
			case ExifInterfaceWrapper.LIGHTSOURCE_FLUORESCENT:
				return "Fluorescent";
			case ExifInterfaceWrapper.LIGHTSOURCE_TUNGSTEN:
				return "Tungsten";
			case ExifInterfaceWrapper.LIGHTSOURCE_FLASH:
				return "Flash";
			case ExifInterfaceWrapper.LIGHTSOURCE_FINEWEATHER:
				return "Fine weather";
			case ExifInterfaceWrapper.LIGHTSOURCE_CLOUDYWEATHER:
				return "Cloudy weather";
			case ExifInterfaceWrapper.LIGHTSOURCE_SHADE:
				return "Shade";
			case ExifInterfaceWrapper.LIGHTSOURCE_DAYLIGHT_FLUORESCENT:
				return "Day light fluorescent";
			case ExifInterfaceWrapper.LIGHTSOURCE_DAYWHITE_FLUORESCENT:
				return "Day white fluorescent";
			case ExifInterfaceWrapper.LIGHTSOURCE_COOLWHITE_FLUORESCENT:
				return "Cool white";
			case ExifInterfaceWrapper.LIGHTSOURCE_WHITE_FLUORESCENT:
				return "White fluorescent";
			case ExifInterfaceWrapper.LIGHTSOURCE_STANDARD_LIGHTA:
				return "Standard light A";
			case ExifInterfaceWrapper.LIGHTSOURCE_STANDARD_LIGHTB:
				return "Standard light B";
			case ExifInterfaceWrapper.LIGHTSOURCE_STANDARD_LIGHTC:
				return "Standard light C";
			case ExifInterfaceWrapper.LIGHTSOURCE_D55:
				return "D55";
			case ExifInterfaceWrapper.LIGHTSOURCE_D65:
				return "D65";
			case ExifInterfaceWrapper.LIGHTSOURCE_D75:
				return "D75";
			case ExifInterfaceWrapper.LIGHTSOURCE_D50:
				return "D50";
			case ExifInterfaceWrapper.LIGHTSOURCE_ISOSTUDIO_TUNGSTEN:
				return "Tungsten";
			case ExifInterfaceWrapper.LIGHTSOURCE_OTHER_LIGHTSOURCE:
				return "Light source";
		}
		return null;
	}
	
	private String computeExposureProgram( int value ){
		switch( value ){
			case ExifInterfaceWrapper.EXPOSURE_PROGRAM_ACTION:
				return "Action";
			case ExifInterfaceWrapper.EXPOSURE_PROGRAM_APERTURE_PRIORITY:
				return "Priority";
			case ExifInterfaceWrapper.EXPOSURE_PROGRAM_CREATIVE:
				return "Creative";
			case ExifInterfaceWrapper.EXPOSURE_PROGRAM_LANDSCAPE:
				return "Landscape";
			case ExifInterfaceWrapper.EXPOSURE_PROGRAM_MANUAL:
				return "Manual";
			case ExifInterfaceWrapper.EXPOSURE_PROGRAM_NORMAL:
				return "Normal";
			case ExifInterfaceWrapper.EXPOSURE_PROGRAM_PORTRAIT:
				return "Portrait";
			case ExifInterfaceWrapper.EXPOSURE_PROGRAM_SHUTTER_PRIORITY:
				return "Shutter Priority";
			default:
				return "Custom";	
		}
	}
	
	private String computeExposureMode( int value ){
		switch( value ){
			case ExifInterfaceWrapper.EXPOSURE_MODE_AUTO_EXPOSURE:
				return "Auto Exposure";
			case ExifInterfaceWrapper.EXPOSURE_MODE_AUTO_BRACKET:
				return "Auto Bracket";
			case ExifInterfaceWrapper.EXPOSURE_MODE_MANUAL_EXPOSURE:
				return "Manual";
		}
		return null;
	}
	
	private String computeWhiteBalance( int value ){
		switch( value ){
			case ExifInterfaceWrapper.WHITE_BALANCE_AUTO:
				return "Auto";
			case ExifInterfaceWrapper.WHITE_BALANCE_MANUAL:
				return "Manual";
		}
		return null;
	}
	
	private String computeFlash( int value ){
		switch( value ){
			case 0:
				return "No flash";
			case 0x01:
				return "Flash fired";
			case 0x05:
				return "Strobe return light not detected";
			case 0x07:
				return "Strobe return light detected";
			case 0x09:
				return "Compulsory flash";
			case 0x0D:
				return "Compulsory flash, light not detected";
			case 0x0F:
				return "Compulsory flash, light detected";
			case 0x10:
				return "Flash not fired, compulsory flash";
			case 0x18:
				return "Flash not fired, auto";
			case 0x19:
				return "Flash fired, auto";
			case 0x1D:
				return "Flash fired, auto, light not detected";
			case 0x1F:
				return "Flash fired, auto, light detected";
			case 0x20:
				return "No flash function";
			case 0x41:
				return "Flash fired, red-eye reduction";
			case 0x45:
				return "Red-eye reduction, light not detected";
			case 0x47:
				return "Red-eye reduction, light detected";
			case 0x49:
				return "Compulsory flash, red-eye reduction";
			case 0x4D:
				return "Compulsory flash, red-eye reduction, light not detected";
			case 0x4F:
				return "Compulsory flash, red-eye reduction, light detected";
			case 0x59:
				return "Flash fired, auto, red-eye reduction";
			case 0x5D:
				return "Flash fired, auto, light not detected, red-eye reduction";
			case 0x5F:
				return "Flash fired, auto, light detected, red-eye reduction";
		}
		return null;
	}
	
	/**
	 * Transform a long into a reabable human file size
	 * @param size
	 * @return
	 */
	public static String readableFileSize( long size ) {
		if ( size <= 0 ) return "0";
		final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
		int digitGroups = (int) ( Math.log10( size ) / Math.log10( 1024 ) );
		return new DecimalFormat( "#,##0.#" ).format( size / Math.pow( 1024, digitGroups ) ) + " " + units[digitGroups];
	}

	public static final class Info {

		private final String tag;
		private String value;
		private Object rawData;

		public Info( String t, String v ) {
			tag = t;
			value = v;
		}
		
		public String getValue(){
			return value;
		}
		
		public String getTag(){
			return tag;
		}
		
		public Object getRawData(){
			return rawData;
		}
	}
}
