Aviary Android SDK Setup Guide
==============================

Contents
--------

* [1 Introduction](#introduction)
	* [1.1 Prerequisites](#prerequisites)
* [2 Workspace setup](#workspace-setup)
* [3 Sample Application](#sample-app)
* [4 Include Aviary-SDK in a new Application](#include)
	* [4.1 Create a new Android project](#create-new)
	* [4.2 API-KEY](#api_key)
	* [4.3 AndroidManifest.xml](#manifest)
	* [4.4 Themes and Styles](#themes)
	* [4.5 More customizations](#morecustomizations)
* [5 Invoke the Editor](#invoke)
	* [5.1 Intent parameters](#intent-parameters)
	* [5.2 Result parameters](#result-parameters)
* [6 Localization](#localization)
* [7 Proguard](#proguard)
* [8 Hi-Resolution editing](#hires)
* [9 Retain EXIF Tags](#exif)
* [10 Using Aviary without the SDK](#no-sdk)

<a name="introduction"></a>
1 Introduction
------------

This document will guide you through the creation of a sample application using the `Aviary SDK` Android library.

<a name="prerequisites"></a>
### 1.1 Prerequisites

Download the Aviary-SDK from the github page: https://github.com/AviaryInc/Mobile-Feather-SDK-for-Android

The Aviary Android SDK supports Android 2.3+ ( API level 9 ) as the 
[minSdkVersion](http://developer.android.com/guide/topics/manifest/uses-sdk-element.html#min), 
but it must be compiled using Android 4.2 ( API level 17 ) as the target sdk. 
This means that your application must have "Android 4.2" selected  in the "Project Build Target" Eclipse panel.

> Note: ~~we will deprecate android 2.2 as soon as its usage statistic will drop under 5% (according to the [Google stats](http://developer.android.com/about/dashboards/index.html))~~

This guide assumes that you already have the Android environment installed 
on your system and Eclipse with the required ADT plugin.
See the Android documentation for 
[installing](http://developer.android.com/sdk/installing.html) and 
[Eclipse](http://developer.android.com/sdk/eclipse-adt.html) 
if you need instructions on how to set up the Android environment.

You will also need an Aviary API key/secret pair to access the 
remote effect API. To sign up or learn more, please visit 
[http://www.aviary.com/android-key](http://www.aviary.com/android-key).

<a name="workspace-setup"></a>
2 Workspace setup
---------------

First, we'll need to import the 2 Eclipse projects into our workspace.

Open Eclipse and select "Import" from the file menu.

![import project in eclipse](http://testassets.aviary.com.s3.amazonaws.com/android/docs/sdk/img-3.png)


The import dialog will appear. From the list of import options, 
select "Existing Projects into Workspace," and then click "Next."

![import project in eclipse](http://testassets.aviary.com.s3.amazonaws.com/android/docs/sdk/img-4.png)


In the new dialog, click on the "Select archive file" radio button 
and then click the "Browse" button on the right. From here, select 
the `Avoary-SDK-xxx.zip` file included with this document ( xxx is the version number ).
Click on the "Finish" button at the bottom of the dialog. 
A new Android library project called `Aviary-SDK` will be created in your current workspace. 
This is the required library project which you must include in your application if you want to use Aviary to manipulate images.

![import project in eclipse](http://testassets.aviary.com.s3.amazonaws.com/android/docs/sdk/img-1.png)



<a name="sample-app"></a>
3 Sample Application
------------------

Next, we need to create an Android application in order to use the Aviary editor. 
You can see a real example of how to use the Aviary editor by opening the [sample app](https://github.com/AviaryInc/Mobile-Feather-SDK-for-Android/raw/master/sample-app.zip).

Just import the sample application by following the same procedures 
described above, but select `sample-app.zip` at step 3. 

A new project called "AviaryLauncher" will be created in your workspace. 
You can inspect this app to see a sample usage of the Aviary sdk.

The imported application should have all the references already set and 
it should be ready to use. If you want to include AviaryFeather in a 
different Android project or add it to a new one, follow the instructions 
in step 4; otherwise you can skip to step 5.

<a name="include"></a>
4 Include Aviary-SDK in a new Application
------------------------------------------

If you don't want to use the included sample application to test Aviary, 
here's a step by step guide on how to include it in a new Android application.

<a name="create-new"></a>
### 4.1 Create a new Android project

Just create a new Android project as usual from Eclipse and select 
Android 4.2 in the Build Target Panel.

![new eclipse project](http://testassets.aviary.com.s3.amazonaws.com/android/docs/sdk/img-5.png)

Once the new project has been created, open the project properties 
and navigate to the "Android" section. Click the "Add..." button of 
the "Library" subsection and select `Aviary-SDK` from the dialog.

![project setup](http://testassets.aviary.com.s3.amazonaws.com/android/docs/sdk/img-6.png)

<a name="api_key"></a>
### 4.2 API-KEY

Starting from 2.2.1 of the SDK, the way to pass the developer api key to the FeatherActivity has changed.<br />
The API KEY should not be passed in the calling Intent anymore.<br />
Now you must save your api-key string inside a file called "aviary-credentials.txt" placed inside your "assets" folder ( See the  sample application for an example ).

It is extremely important that you update your application according to this change, as future versions of the Aviary SDK will throw an error if this file cannot be found or the api-key is not valid.

**Grab your api key from http://aviary.com/android**

<a name="manifest"></a>
### 4.3 AndroidManifest.xml

Add some entries to the manifest file of your application.

**Permissions**

AviaryFeather requires internet and write access to external storage. 
To grant these permissions, add these entries inside the AndroidManifest.xml &lt;manifest&gt; tag:

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />


An additional permission is necessary if you want to turn on vibration feedback, but the vibration is optional:

    <uses-permission android:name="android.permission.VIBRATE" />
	
This permission will enable the vibration feedback on some tool components, 
for a better user experience. Omit this permission if you don't want the vibration feedback.


**Activity declaration**

As mentioned above, the Aviary sdk supports Android 2.3 ( api level 9 ) as the minimum Android version, 
so the "uses-sdk" xml node of your manifest should look like this:
    
	<uses-sdk
        android:minSdkVersion="9"
        android:targetSdkVersion="17" />

Then, inside the &lt;application&gt; tag, add a reference to the FeatherActivity:

    <activity
        android:name="com.aviary.android.feather.FeatherActivity"
        android:configChanges="orientation|keyboardHidden|screenSize"
        android:screenOrientation="unspecified"
        android:hardwareAccelerated="true"
        android:largeHeap="true"
        android:theme="@style/AviaryTheme" />
        
It's important that you specify **@style/AviaryTheme** as default theme for this Activity.

>Note: If your application already uses a lot of memory then you should consider to run the FeatherActivity in a separate process. <br />
>Just add the `android:process=":your_name"` in previous xml entry.

And a reference to the plugins receiver is also required:

    <receiver
        android:name="com.aviary.android.feather.library.receivers.AviarySystemReceiver"
        android:exported="true"
        android:process=":feather_system_receiver" >
            <intent-filter>
                <action android:name="android.intent.action.PACKAGE_ADDED" />
                <action android:name="android.intent.action.PACKAGE_REMOVED" />
                <action android:name="android.intent.action.PACKAGE_REPLACED" />
                <data android:scheme="package" />
            </intent-filter>
    </receiver>

<a name="theme"></a>
### 4.4 Theme and Styles

As said before, the FeatherActivity entry in your manifest file must be associated with the `AviaryTheme`.
You can always modify almost all the visual aspects of the UI by editing the style entries (`aviary_styles.xml`), colors (`aviary_colors.xml`), dimensions (`aviary_dimens.xml`). 
Most of the custom attributes you'll find inside the `aviary_theme.xml` and `aviary_styles.xml` files are documented inside the `aviary_attrs.xml` file.

<a name="morecustomizations"></a>
### 4.5 More customizations
The `aviary_config.xml` file, instead, contains all the customizable behaviors of the SDK, like the colors to show inside the text tool or the drawing tool, the sizes of the brush tools, the custom crop ratios for the crop tool, the default font used in the meme tool, etc..
Inside the aviary_config.xml file you'll find a detailed description of every entry.

<a name="invoke"></a>
5 Invoke FeatherActivity
--------------

If you're calling Aviary from a new application, you'll need to add the below code 
in order to start the editor. Otherwise (if you're using the demo application), you 
can find this code inside the `MainActivity.java` file.

In order to invoke Aviary from your activity, you need to pass some parameters to the 
FeatherActivity. Here's an example of how to invoke the new activity:


    Intent newIntent = new Intent( this, FeatherActivity.class );
    // set the source image uri
    newIntent.setData( uri );
    

This is the minimum required Intent you need to use in order to open the Aviary SDK. 
<br/>The image [Uri](http://developer.android.com/reference/android/net/Uri.html) is **mandatory** and it can have the following scheme:
* [ContentResolver.SCHEME_FILE](http://developer.android.com/reference/android/content/ContentResolver.html#SCHEME_FILE): absolute local file path ( file:///mnt/.../image.jpg )
* No scheme: absolute local file path ( /mnt/sdcard.../image.jpg )
* [ContentResolver.SCHEME_CONTENT](http://developer.android.com/reference/android/content/ContentResolver.html#SCHEME_CONTENT): database driven file location ( content:/.../112232 )
* "http" or "https": remote files.


The passed Intent can also have these optional parameters:


    // Uri of the destination image file
    // This will be the same uri you will receive in the onActivityResult
    newIntent.putExtra( "output", Uri.parse( "file://" + mOutputFile.getAbsolutePath() ) );
    
    // Image format of the destination file
    newIntent.putExtra( "output-format", Bitmap.CompressFormat.JPEG.name() );
    
    // Output format quality (optional)
    newIntent.putExtra( "output-quality", 85 );
    
    // By default all the tools are shown, you can force to display only a certain tools.
    // The list of all the available tools is defined by this enum:
    // com.aviary.android.feather.library.filters.FilterLoaderFactory.Filters
    newIntent.putExtra( "tools-list", new String[]{"ADJUST", "BRIGHTNESS" } );

	// enable fast rendering preview
	newIntent.putExtra( "effect-enable-fast-preview", true );
	
	// by default FeatherActivity will choose the preview image size depending on the current
	// device. If you want to use a different image size, you can pass this extra param ( this
	// does not affect the hi-res processing, but only the preview image.
	newIntent.putExtra( "max-image-size", 800 );
	
	// HI-RES
	// You need to generate a new session id key to pass to Aviary feather
	// this is the key used to operate with the hi-res image ( and must be unique for every new instance of FeatherActivity )
	// The session-id key must be 64 char length ( full description of the hi-res processing in the Hi-Res paragraph of this file )
	String mSessionId = StringUtils.getSha256( System.currentTimeMillis() + API_KEY );
	newIntent.putExtra( "output-hires-session-id", mSessionId );	
	
    // you want to hide the exit alert dialog shown when back is pressed
    // without saving image first. By default the user will be notified by an alert if he tries to exit
    // the editor with unsaved changes.
    newIntent.putExtra( "hide-exit-unsave-confirmation", true );
    
	// -- VIBRATION --
	// Some aviary tools use the device vibration in order to give a better experience
	// to the final user. But if you want to disable this feature, just pass
	// any value with the key "tools-vibration-disabled" in the calling intent.
	// This option has been added to version 2.1.5 of the Aviary SDK
	newIntent.putExtra( Constants.EXTRA_TOOLS_DISABLE_VIBRATION, true );
    

	// === NO CHANGES TO THE IMAGE ==
	// With this extra param you can tell to FeatherActivity how to manage
	// the press on the Done button even when no real changes were made to
	// the image.
	// If the value is true then the image will be always saved, a RESULT_OK
	// will be returned to your onActivityResult and the result Bundle will 
	// contain an extra value "EXTRA_OUT_BITMAP_CHANGED" indicating if the
	// image was changed during the session.
	// If "false" is passed then a RESULT_CANCEL will be sent when an user will
	// hit the 'Done' button without any modifications ( also the EXTRA_OUT_BITMAP_CHANGED
	// extra will be sent back to the onActivityResult.
	// By default this value is true ( even if you omit it )
	newIntent.putExtra( Constants.EXTRA_IN_SAVE_ON_NO_CHANGES, true );

    // ..and start feather
    startActivityForResult( newIntent, ACTION_REQUEST_FEATHER );



<a name="intent-parameters"></a>
### 5.1 Intent parameters

Here's a description of the parameters:

**Uri**

(intent data) This is the source [Uri](http://developer.android.com/reference/android/net/Uri.html) of the image to be used as input by Aviary ( required ). "content", "file", "http" and "https" schemas are supported ( no schema means an absolute file path ).


**output**

This is the uri of the destination file where Aviary will write the result image.


**output-format**

Format of the output file (jpg or png).


**output-quality**

Quality of the output image (required only if output-format is jpeg), from 0 to 100.


**effect-enable-fast-preview**

Depending on the current image size and the current user device, some effects can 
take longer than expected to render the image. Passing in the caller intent this 
flag as boolean "true" the effect panel will no longer use the default progress modal 
dialog while rendering an effect but instead will use a small "loading" view while rendering 
a small image preview. User will almost immediately see the small preview while the full size 
image is being processed in background. Once the full size image is processed, it will replace 
the small preview image. The default behavior is to enable this feature only on fast devices 
(fast enough to allow the small preview to be rendered immediately). Pass "false" if you want 
to force the "progress modal" rendering model. No small preview will appear with this model, only a modal progress bar while 
rendering the image.


**tools-list**

If specified in the extras of the passed intent, it will tell Aviary to display only certain tools. 
The value must be a String[] array and the available values are: 

    SHARPNESS, BRIGHTNESS, CONTRAST, SATURATION, EFFECTS, RED_EYE, CROP, WHITEN, DRAWING, STICKERS, TEXT, BLEMISH, MEME, ADJUST, ENHANCE, COLORTEMP, BORDERS, COLOR_SPLASH, TILT_SHIFT


**hide-exit-unsave-confirmation**

When the user clicks the back button and the image contains unsaved data, a confirmation 
dialog appears by default. Setting this flag to true will hide that confirmation and the 
application will terminate without a warning to the user.


**effect-enable-external-pack**

By default, the Aviary editor allows users to download and install external filter packs from the Android Market.
If you want to disable this feature, you can pass this extra boolean to the launching intent as "false".
The default behavior is to enable the external filters.

**frames-enable-external-pack**

(added in 2.2.1) By default, the Aviary editor allows users to download and install external frames packs from the Android Market.
If you want to disable this feature, you can pass this extra boolean to the launching intent as "false".
The default behavior is to enable the external packs. 
Note that if you pass "false" the frames tool will be hidden.


**stickers-enable-external-pack**

By default, the Aviary editor allows users to download and install external sticker packs from the Android Market.
If you want to disable this feature, you can pass this extra boolean to the launching intent as "false".
The default behavior is to enable the external stickers.


**max-image-size**

By default, the Aviary editor will resize the loaded image according to the device memory. If you want to change the maximum image size limit, you can pass this key to the extra bundle. But keep in mind that the available memory for the current application is shared between your host application and the Aviary editor, so don't use too big an image size, otherwise the system will throw an OutOfMemoryError. If you're planning to enable the hi-res output as well, we strongly suggest that you set the preview image size to as small as possible.


**output-hires-session-id**

If you want to enable the high resolution image processing, once FeatherActivity has completed (but eventually also during its execution), you need to pass a unique session id to the calling Intent. The session id string must be unique and must be 64 chars in length.


**tools-vibration-disabled**
Passing this key in the calling intent, with any value, will disable the haptic vibration used in certain tools

**save-on-no-changes**
A boolean to indicate how to manage the hit on the "done" button even when no changes were made to the image.<br />
If true is passed then the image will be saved, a RESULT_OK will be returned to your
**onActivityResult** and in the returned Intent there will be the extra `Constants.EXTRA_OUT_BITMAP_CHANGED` with
the value "false".<br />
If false is passed then you will receive a RESULT_CANCELED and the image will not
be saved at all. The returned Intent will contain the `Constants.EXTRA_OUT_BITMAP_CHANGED` extra.<br />
By default this value is "true"

<a name="result-parameters"></a>
### 5.2 Result parameters

Once the user clicks "Done" (save) in the Feather activity, the "onActivityResult" of your Activity 
will be invoked, passing back `ACTION_REQUEST_FEATHER` as requestCode.
The Uri data of the returned intent will be the output path of the resulting image:

    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent data ) {
        if( resultCode == RESULT_OK ) {
            switch( requestCode ) {
                case ACTION_REQUEST_FEATHER:
                	// modified preview image
                    Uri mImageUri = data.getData();
                    
					Bundle extra = data.getExtras();
					if( null != extra ) {
						// image was changed by the user?
						changed = extra.getBoolean( Constants.EXTRA_OUT_BITMAP_CHANGED );
					}
                    break;
           }
        }
    }


<a name="localization"></a>
6 Localization
--------------

Android is really smart regarding localization. Localizing resources and strings is very easy.

Here are the instructions to create a new language for all the label messages of Aviary 
(let's say we want to add Italian support):

* Go into the **Aviary-SDK/res** folder
* Create a new folder "values-it". 
* Copy the file `res/values/strings.xml` into the **res/values-it** folder.
* Open the `res/values-it/strings.xml` file with any text editor and translate all the strings within the &lt;string&gt;&lt;/string&gt; tag. For instance, the original version of the string "Save" is:

    
	`<string name="save">Save</string>`

* in your localized `strings.xml` file it will be:

    `<string name="save">Salva</string>`

Now just clean and recompile your application. If your device has set Italian 
as the default language, you will see Aviary in Italian.

For a more detailed tutorial about Android localization, you can refer to 
[this tutorial](http://developer.android.com/resources/tutorials/localization/index.html).

The current version of the SDK comes with a bunch of localized languages, you can find them inside the "res" folder of the Aviary-SDK project. If you need a language that isn't yet available, you can contact us at [api@aviary.com](mailto:api@aviary.com) and we'll work with you to add it!

<a name="proguard"></a>
7 Proguard
------

If your application is compiled using [proguard](http://developer.android.com/guide/developing/tools/proguard.html), you need to update your `proguard-project.txt` file according to the proguard-project.txt file included in the sample application.

If you're using ant to compile your project.
First be sure your project is up to date:

    $ android update project -p .

Then check your `project.properties` file should look like:

    proguard.config=${sdk.dir}/tools/proguard/proguard-android.txt:proguard-project.txt
    
And then content of your `proguard-project.txt` file should include the contents of [this file](https://github.com/sephiroth74/AviaryLauncher/blob/master/proguard-project.txt).


<a name="hires"></a>
8 Hi-Resolution Image Editing
------

By default, the Aviary editor works on a medium resolution image, to speed up the performance of the editor. 
But you can also enable hi-res saving of images, (up to 3MP for the free version of the SDK ). 

>If you need higher resolution output, please contact us at [partners@aviary.com](mailto:partners@aviary.com).


When you enable the High Resolution image processing, you first need to include this entry in the AndroidManifest:

        <!-- Required for the hi-res image processing -->
        <!-- authorities can have the value you prefer -->
        <provider 
            android:name="com.aviary.android.feather.library.providers.FeatherContentProvider"
            android:exported="false"
            android:authorities="com.aviary.launcher.HiResProvider">
        </provider>
		
**IMPORTANT**: **Do not use the sample `android:authorities` value included in this code. You MUST use the package name of your own app as the string value**.<br/> 
If you leave the sample package name (com.aviary.launcher) in your code, users may have trouble installing your app from Google Play.


1. In the calling Intent you must pass an extra string, the hi-res session id, like this:
	
		final String session_name = StringUtils.getSha256( System.currentTimeMillis() + API_KEY );
		newIntent.putExtra( "output-hires-session-id", session_name );
		
	The session string must be unique and must be 64 char in length.
	Once Aviary starts, it will start collecting the information of every action performed on the image and will store those
	actions in the internal ContentProvider (remember to add the provider tag to the AndroidManifest first!).
	
2. Once your activity calls the "onActivityResult" method, you can process the HD image.

 2.1 First create a new file where the HD image will be stored
	
		File destination = new File(…);
		
 2.2 Initialize the session instance

		FeatherContentProvider.SessionsDbColumns.Session session = null;
	
	session_name is the same session string you passed in the calling intent
		
		Uri sessionUri = FeatherContentProvider.SessionsDbColumns.getContentUri( this, session_name );
		
 2.3 This query will return a cursor with the information about the given session
		
		Cursor cursor = getContentResolver().query( sessionUri, null, null, null, null );
		
		if ( null != cursor ) {
			session = FeatherContentProvider.SessionsDbColumns.Session.Create( cursor );
			cursor.close();
		}
		
 At this point, you will have a Session object with the following information:
	
 session.id, the internal id of the session<br/>
 session.name, the session 64 char wide value<br/>
 session.ctime, the session creation time<br/>
 session.file_name, the original image used for editing (the same you passed in the calling Intent)<br/>
	
		
 2.4 Now you must query the ContentProvider to get the list of actions to be applied on the original image:
	
    	Uri actionsUri = FeatherContentProvider.ActionsDbColumns.getContentUri( this, session.session );
    	Cursor cursor = getContentResolver().query( actionsUri, null, null, null, null );
	
3. And finally the steps to load, apply the actions and save the HD image (these steps should be performed in a separate thread, like an AsyncTask):
	
	Create an instance of MoaHD class
	
		// This must be invoked if the FeatherActivity runs in a separate process (this depends
		// on your AndroidManifest.xml configuration)
		try {
			NativeFilterProxy.init( getBaseContext(), API_KEY );
		} catch( AviaryInitializationException e ) {
			// an error occurred!
			e.printStackTrace();
			Log.d( LOG_TAG, "message: " + e.getMessage() );
			return;
		}
		
		MoaHD moa = new MoaHD();
		

	> Premium version only: by default the High resolution image will be resized 13mp if the image exceeds this limit. You can increase this limit using this method (before loading the image):<br />
	moa.setMaxMegaPixels( MegaPixels.Mp30 );
		
4. Now load the image in memory, note that the srcPath can be either a string absolute path or an int ( int if you're passing a filedescriptor - see [ParcelFileDescriptor.getFd()](http://developer.android.com/reference/android/os/ParcelFileDescriptor.html#getFd()))
	
 Both `load` and `save` methods of the MoaHD class will throw an exception if there is an error. The exception is an instance of the `AviaryExecutionException` class.<br />
	You can inspect the error code of the exception to get more informations about the error ( using the `getCode` method of the AviaryExecutionException class).<br />
	possible exceptions are listed below:
	
 `AviaryExecutionException.FILE_NOT_FOUND_ERROR` = 1<br/>
`AviaryExecutionException.FILE_EXCEED_MAX_SIZE_ERROR` = 2<br/>
`AviaryExecutionException.FILE_NOT_LOADED_ERROR` = 3<br/>
`AviaryExecutionException.INVALID_CONTEXT_ERROR` = 4<br/>
`AviaryExecutionException.FILE_ALREADY_LOADED_ERROR` = 5<br/>
`AviaryExecutionException.DECODER_NOT_FOUND_ERROR` = 6<br/>
`AviaryExecutionException.ENCODER_NOT_FOUND_ERROR` = 7<br/>
`AviaryExecutionException.DECODE_ERROR` = 8<br/>
`AviaryExecutionException.ENCODE_ERROR` = 9<br/>
`AviaryExecutionException.INSTANCE_NULL_ERROR` = 10<br/>
`AviaryExecutionException.UNKNOWN_ERROR` = 11<br/>

			try {
				moa.load( srcPath );
			} catch( AviaryExecutionException e ) {
				// an error occurred
				e.printStackTrace();
				Log.d( LOG_TAG, "error code: " + e.getCode() );
				return;
			}

		
5. Then, for every row in the actions cursor ( the Cursor you created at point *2.4* ), apply the action to the moa instance:
	
		do {
			// utility to get the action object from the current cursor
			Action action = Action.Create( cursor );
			moa.applyActions( action.getActions() );
		} while( cursor.moveToNext() );

	
6. Finally you can save the output image. `dstPath` must be an absolute string path:
	
		try {
			moa.save( dstPath );
		} catch( AviaryExecutionException e ) {
			// error
			e.printStackTrace();
			Log.d( LOG_TAG, "code: " + e.getCode() );
		} finally {
			// remember to call this method once you've done with the moa instance
			moa.dispose();
		}
		
7. ...And remember to close the cursor:
	
		cursor.close();


<a name="exif"></a>
9 Retain EXIF Tags
------

By default Aviary will retain the original image's EXIF data and save it into the edited image. But when you process the hi-res image after Aviary is closed, you'll need to do some additional configuration to retain the EXIF data:

* Use the included **ExifInterfaceWrapper** class (inside the com.aviary.android.feather.library.media package), which can handle a larger number of tags, rather than the Android default ExifInterface class

* Create a new instance of ExifInterfaceWrapper, passing the original source image:<br />

		ExifInterfaceWrapper originalExif = new ExifInterfaceWrapper( srcPath );

* When the hi-res process is complete, create a new ExifInterfaceWrapper instance passing the path of the just created image:<br />

		newExif = new ExifInterfaceWrapper( dstPath );

* Copy the original exif tags into the destination exif:<br/>

		// remember the current image width an height
		int imageWidth = newExif.getAttributeInt( ExifInterfaceWrapper.TAG_IMAGE_WIDTH, 0 );
		int imageLength = newExif.getAttributeInt( ExifInterfaceWrapper.TAG_IMAGE_LENGTH, 0 );

		// This method will copy all the attributes of the original EXIF
		// into the new EXIF instance
		originalExif.copyTo( newExif );
		
		// restore the correct tags
		newExif.setAttribute( ExifInterfaceWrapper.TAG_IMAGE_WIDTH, String.valueOf( imageWidth ) );
		newExif.setAttribute( ExifInterfaceWrapper.TAG_IMAGE_LENGTH, String.valueOf( imageLength ) );
		
		// the editor already auto rotate the image pixels
		newExif.setAttribute( ExifInterfaceWrapper.TAG_ORIENTATION, "0" );
		
		// let's update the software tag too
		newExif.setAttribute( ExifInterfaceWrapper.TAG_SOFTWARE, "Aviary " + FeatherActivity.SDK_VERSION );
		
		// update the datetime
		newExif.setAttribute( ExifInterfaceWrapper.TAG_DATETIME, ExifInterfaceWrapper.getExifFormattedDate( new Date() ) );
		
</code>

* Save the new exif tags:

				try {
					newExif.saveAttributes();
				} catch ( IOException e ) {
					e.printStackTrace();
				}


<a href="no-sdk"></a>
10 Using Aviary without the SDK
-----

You can still use the Aviary Editor and all its features without embedding the SDK inside your application. 
You just need to check if the Aviary Editor is installed on the device, you can use this snippet:

	public boolean isAviaryInstalled(){
		Intent intent = new Intent( "aviary.intent.action.EDIT" );
		intent.setType( "image/*" );
		List<ResolveInfo> list = getPackageManager().queryIntentActivities( intent, PackageManager.MATCH_DEFAULT_ONLY );  
		return list.size() > 0; 
	}
	
If Aviary is not installed then you can prompt your user to download the application from the Google play store using this Intent:

	Intent intent = new Intent( Intent.ACTION_VIEW );
	intent.setData( Uri.parse( "market://details?id=com.aviary.android.feather" ) );
	startActivity( intent );

Once the Editor is installed on the user's device, you can start the Aviary Editor using this Intent:

	Intent newIntent = new Intent( "aviary.intent.action.EDIT" );
	newIntent.setDataAndType( uri, "image/*" ); // required
	newIntent.putExtra( "app-id", getPackageName() ); // required ( it's your app unique package name )

as you can see the Intent is slightly different from the one used previously (using the embedded Aviary-SDK), but all the extras described in the previous paragraphs are still valid.
