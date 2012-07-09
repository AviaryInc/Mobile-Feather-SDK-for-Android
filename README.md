Aviary Android SDK Setup Guide
==============================

Contents
--------

* [1 Introduction](#introduction)
	* [1.1 Prerequisites](#prerequisites)
* [2 Workspace setup](#workspace-setup)
* [3 Sample Application](#sample-app)
* [4 Include AviaryFeather in a new Application](#include)
	* [4.1 Create a new Android project](#create-new)
	* [4.2 AndroidManifest.xml](#manifest)
	* [4.3 themes.xml](#themes)
* [5 Invoke the Editor](#invoke)
	* [5.1 Intent parameters](#intent-parameters)
	* [5.2 Result parameters](#result-parameters)
* [6 Extras](#extras)
	* [6.1 Stickers](#stickers)
	* [6.2 Other configurations](#other-configurations)
	* [6.3 UI Customization](#customization)
* [7 Localization](#localization)
* [8 Proguard](#proguard)
* [9 Crash Report](#crash_report)
* [10 Hi-Resolution editing](#hires)
* [11 Retain EXIF Tags](#exif)

<a name="introduction"></a>
1 Introduction
------------

This document will guide you through the creation of a 
sample application using the Aviary Android library (codename: Feather).

<a name="prerequisites"></a>
### 1.1 Prerequisites

The Aviary Android SDK supports Android 2.2+ as the 
[minSdkVersion](http://developer.android.com/guide/topics/manifest/uses-sdk-element.html#min), 
but it must be compiled using Android 4.0 (API level 14) as the target sdk. 
This means that your application must have "Android 4.0" selected 
in the "Project Build Target" Eclipse panel.

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

![import project in eclipse](http://labs.sephiroth.it/tmp/android/1.png)


The import dialog will appear. From the list of import options, 
select "Existing Projects into Workspace," and then click "Next."

![import project in eclipse](http://labs.sephiroth.it/tmp/android/2.png)


In the new dialog, click on the "Select archive file" radio button 
and then click the "Browse" button on the right. From here, select 
the `aviaryfeather.zip` file included with this document.
Click on the "Finish" button at the bottom of the dialog. 
A new Android library project called "Android-Feather" will be created 
in your current workspace. This is the required library project which 
you must include in your application if you want to use Aviary to manipulate images.

![import project in eclipse](http://labs.sephiroth.it/tmp/android/3.png)



<a name="sample-app"></a>
3 Sample Application
------------------

Next, we need to create an Android application in order to use the Aviary editor. 
You can see a real example of how to use the Aviary editor by opening 
the included `sample-app.zip` project.

Just import the sample application by following the same procedures 
described above, but select `sample-app.zip` at step 3. 

A new project called "AviaryLauncher" will be created in your workspace. 
You can inspect this app to see a sample usage of the Aviary sdk.

The imported application should have all the references already set and 
it should be ready to use. If you want to include AviaryFeather in a 
different Android project or add it to a new one, follow the instructions 
in step 4; otherwise you can skip to step 5.

<a name="include"></a>
4 Include AviaryFeather in a new Application
------------------------------------------

If you don't want to use the included sample application to test Aviary, 
here's a step by step guide on how to include it in a new Android application.

<a name="create-new"></a>
### 4.1 Create a new Android project

Just create a new Android project as usual from Eclipse and select 
Android 4.0 in the Build Target Panel.

![new eclipse project](http://labs.sephiroth.it/tmp/android/4.png)

Once the new project has been created, open the project properties 
and navigate to the "Android" section. Click the "Add..." button of 
the "Library" subsection and select "AviaryFeather" from the dialog.

![project setup](http://labs.sephiroth.it/tmp/android/6.png)


Next, navigate to the "Java Build Path" section of the project properties 
dialog and click on "Add JARs..." button of the "Libraries" subsection.

![project setup](http://labs.sephiroth.it/tmp/android/7.png)

You need to add the `android-support-v4.jar` library. 
Please go to the official android [support package page](http://developer.android.com/sdk/compatibility-library.html#Downloading) in order to obtain your copy of the jar file.

<a name="manifest"></a>
### 4.2 AndroidManifest.xml

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

As mentioned above, the Aviary sdk supports Android 2.2 as the minimum Android version, 
so the "uses-sdk" xml node of your manifest should look like this:
    
	<uses-sdk android:minSdkVersion="8" />

Then, inside the &lt;application&gt; tag, add a reference to the FeatherActivity:

    <activity
        android:name="com.aviary.android.feather.FeatherActivity"
        android:configChanges="orientation|keyboardHidden"
        android:screenOrientation="unspecified"
        android:hardwareAccelerated="true"
        android:largeHeap="true"
        android:theme="@style/FeatherDefaultTheme.Custom" />

And a reference to the plugins receiver is also necessary:

    <receiver
        android:name="com.aviary.android.feather.receivers.FeatherSystemReceiver"
        android:exported="true"
        android:process=":feather_system_receiver" >
            <intent-filter>
                <action android:name="android.intent.action.PACKAGE_ADDED" />
                <action android:name="android.intent.action.PACKAGE_REMOVED" />
                <action android:name="android.intent.action.PACKAGE_REPLACED" />
                <data android:scheme="package" />
            </intent-filter>
    </receiver>
	
If you plan to enable the High Resolution image processing (see [section 10](#hires)), you also need to include this entry in the AndroidManifest:

        <!-- Required for the hi-res image processing -->
        <!-- authorities can have the value you prefer -->
        <provider 
            android:name="com.aviary.android.feather.library.providers.FeatherContentProvider"
            android:exported="false"
            android:authorities="com.aviary.launcher.HiResProvider">
        </provider>
		
Note that the `android:authorities` is arbitrary; you can use any string value you prefer.

<a name="theme"></a>
### 4.3 Theme and Styles

The `android:theme` entry in the manifest file is also required for Aviary to work properly, 
so add an entry to your `themes.xml` file (if you don't have one, create a new file called 
`themes.xml` in your res/values folder):

    <?xml version="1.0" encoding="utf-8"?>
    <resources>
        <style name="FeatherDefaultTheme.Custom" parent="FeatherDefaultTheme" />
    </resources>


By default, this entry will use the default Aviary theme. If you'd like to 
customize the editor's UI, you can do that simply by adding entries to your 
"FeatherDefaultTheme.Custom" style. Check out the `styles.xml` file included in 
AviaryFeather/res/values for the list of available styles.

Note that many UI elements depend on both the `styles.xml` and `config.xml` files included. 
The `styles.xml` file declares the UI components' general appearance, while in the 
`config.xml` file you'll find component specific dimensions (like for text or lists) 
and most of the properties for customizing Aviary's panel behavior.

<a name="invoke"></a>
5 Invoke Feather
--------------

If you're calling Aviary from a new application, you'll need to add the below code 
in order to start the editor. Otherwise (if you're using the demo application), you 
can find this code inside the `MainActivity.java` file.

In order to invoke Aviary from your activity, you need to pass some parameters to the 
FeatherActivity. Here's an example of how to invoke the new activity:


    // Create the intent needed to start feather
    Intent newIntent = new Intent( this, FeatherActivity.class );
    // set the source image uri
    newIntent.setData( uri );
    // pass the required api key ( http://developers.aviary.com/ )
    newIntent.putExtra( "API_KEY", "xxx" );
    // pass the uri of the destination image file (optional)
    // This will be the same uri you will receive in the onActivityResult
    newIntent.putExtra( "output", Uri.parse( "file://" + mOutputFile.getAbsolutePath() ) );
    // format of the destination image (optional)
    newIntent.putExtra( "output-format", Bitmap.CompressFormat.JPEG.name() );
    // output format quality (optional)
    newIntent.putExtra( "output-quality", 85 );
    // you can force feather to display only a certain tools
    // newIntent.putExtra( "tools-list", new String[]{"ADJUST", "BRIGHTNESS" } );

	// enable fast rendering preview
	newIntent.putExtra( "effect-enable-fast-preview", true );
	
	// limit the image size
	// You can pass the current display size as max image size because after
	// the execution of Aviary you can save the HI-RES image so you don't need a big
	// image for the preview
	// newIntent.putExtra( "max-image-size", 800 );
	
	// HI-RES
	// You need to generate a new session id key to pass to Aviary feather
	// this is the key used to operate with the hi-res image ( and must be unique for every new instance of Feather )
	// The session-id key must be 64 char length
	String mSessionId = StringUtils.getSha256( System.currentTimeMillis() + API_KEY );
	newIntent.putExtra( "output-hires-session-id", mSessionId );	
	
    // you want to hide the exit alert dialog shown when back is pressed
    // without saving image first
    // newIntent.putExtra( "hide-exit-unsave-confirmation", true );
    
    // ..and start feather
    startActivityForResult( newIntent, ACTION_REQUEST_FEATHER );


<a name="intent-parameters"></a>
### 5.1 Intent parameters

Here's a description of the required parameters:

**Uri**

(intent data) This is the source URI of the image to be used as input by Aviary.

**API_KEY**

An api key IS REQUIRED to use remote filters. Please visit 
[http://www.aviary.com/android-key](http://www.aviary.com/android-key) 
for more information on how to obtain your api key and secret.


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
to force the "progress modal" rendering model. No small preview, only a modal progress bar while 
rendering the image.


**tools-list**

If specified in the extras of the passed intent, it will tell Aviary to display only certain tools. 
The value must be a String[] array and the available values are: 

    SHARPNESS, BRIGHTNESS, CONTRAST, SATURATION, EFFECTS, RED_EYE, CROP, WHITEN, DRAWING, 
    STICKERS, TEXT, BLEMISH, MEME, ADJUST, ENHANCE, COLORTEMP


**hide-exit-unsave-confirmation**

When the user clicks the back button and the image contains unsaved data, a confirmation 
dialog appears by default. Setting this flag to true will hide that confirmation and the 
application will terminate without a warning to the user.


**effect-enable-external-pack**

By default, the Aviary editor allows users to download and install external filter packs from the Android Market.
If you want to disable this feature, you can pass this extra boolean to the launching intent as "false".
The default behavior is to enable the external filters.


**stickers-enable-external-pack**

By default, the Aviary editor allows users to download and install external sticker packs from the Android Market.
If you want to disable this feature, you can pass this extra boolean to the launching intent as "false".
The default behavior is to enable the external stickers.


**max-image-size**

By default, the Aviary editor will resize the loaded image according to the device memory. If you want to change the maximum image size limit, you can pass this key to the extra bundle. But keep in mind that the available memory for the current application is shared between your host application and the Aviary editor, so don't use too big an image size, otherwise the system will throw an OutOfMemoryError. If you're planning to enable the hi-res output as well, we strongly suggest that you set the preview image size to as small as possible.


**output-hires-session-id**

If you want to enable the high resolution image processing, once FeatherActivity has completed (but eventually also during its execution), you need to pass a unique session id to the calling Intent. The session id string must be unique and must be 64 chars in length.


**effect-enable-borders**

By default, most of the filters (those in the Effects tool) come with additional borders. If you want to disable the default borders you can pass this extra with a boolean false value and it will turn off ALL borders.
It is true by default.


**tools-vibration-disabled**
Passing this key in the calling intent, with any value, will disable the haptic vibration used in certain tools


<a name="result-parameters"></a>
### 5.2 Result parameters


Once the user clicks "Done" (save) in the Feather activity, the "onActivityResult" of your Activity 
will be invoked, passing back **"ACTION_REQUEST_FEATHER"** as requestCode.
The Uri data of the returned intent will be the output path of the resulting image:

    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent data ) {
        if( resultCode == RESULT_OK ) {
            switch( requestCode ) {
                case ACTION_REQUEST_FEATHER:
                    Uri mImageUri = data.getData();
                    break;
           }
        }
    }


<a name="extras"></a>
6 Extras
------

<a name="stickers"></a>
### 6.1 Stickers

The sample application includes a couple of demo stickers which will be shown as a default pack. The actual Aviary SDK comes with a default pack of stickers which users can install for free from the market (and in the future, there will be more packs to choose from). If you don't want to include the Aviary pack of stickers, just [disable the tool](#intent-parameters).

<a name="other-configurations"></a>
### 6.2 Other configurations

Inside the AviaryFeather/res/values folder is a `config.xml` file. This file contains some 
application default values and can be modified before compilation.

Here is the description for tool-specific configuration variables:

#### Orientation Tool
`feather_adjust_tool_anim_time` Defines the duration of the rotation/flip animation.

`feather_adjust_tool_reset_anim_time` Defines the reset animation duration 
(i.e., when the user clicks the cancel/back button).

`feather_adjust_tool_enable_3d_flip` If the device is running Android 4.x, 
you can enable a flip animation in 3D style by setting this param to 1.

`feather_rotate_enable_free_rotate` If set to `1` the adjust panel will enable the free rotation using fingers. Users will be able to rotate the image, other than using the panel's buttons, also using fingers.

`feather_rotate_highlight_stroke_color` (only with free rotate enabled) stroke color or the clipping rectangle 

`feather_rotate_highlight_stroke_width` (only with free rotate enabled) stroke width of the clipping rectangle

`feather_rotate_highlight_outside` (only with free rotate enabled) color of the masked area outside the clipping rectangle

`feather_rotate_highlight_grid_stroke_color` (only with free rotate enabled) color of the internal grid


`feather_rotate_highlight_grid_stroke_width` (only with free rotate enabled) stroke width of the internal grid lines




#### Brightness, Contrast, Saturation
`feather_brightness_live_preview` Enable/Disable the live preview while the wheel component is scrolling. Default is enabled.

#### Text Tool
`feather_text_minSize` Minimum text size allowed when user is resizing the text rect.

`feather_text_defaultSize` Initial text size when a new text is added to the canvas.

`feather_text_padding` Padding space between the text edges and the move/resize area rectangle.

`feather_text_highlight_stroke_width` Stroke with of the move/resize rect.

`feather_text_highlight_stroke` Stroke color of the move/resize rect.

`feather_text_highlight_stroke_down` Stroke color of the move/resize rect on pressed state.

`feather_text_highlight_ellipse` Move/resize round rectangle ellipse size.

`feather_text_selected_color` Fill color of the move/resize rectangle on pressed state.

`feather_text_fill_colors` An array of all the available colors available for the text tool.

`feather_text_stroke_colors` This array must have the same length of the `feather_text_fill_colors`. 
For every fill color you can specify a different stroke color.

#### Crop Tool
`feather_crop_min_size` Minimum area size while resizing the crop area.

`feather_crop_allow_inverse` If value is 1, allow user to invert the current crop 
area with a simple click on the crop rect itself.

`feather_crop_highlight` Stroke color of the crop area.

`feather_crop_highlight_down` Stroke color of the crop area when pressed.

`feather_crop_highlight_outside` Fill color of the inactive area. The one outside the crop rect.

`feather_crop_highlight_outside_down` Inactive area color when crop rect is pressed.

`feather_crop_highlight_stroke_width` Stroke size of the crop area.

`feather_crop_highlight_internal` Internal crop lines color.

`feather_crop_highlight_internal_down` Internal crop lines when crop rect is pressed.

Feather by default comes with a predefined number of crop ratios available
to the user (original, custom, square, 4:3, etc). If you want to change them, 
read this carefully. There are 2 xml entries responsible for this: `feather_crop_names` 
and `feather_crop_values`.

`feather_crop_values` Defines the crop predefined ratio for each button.

`feather_crop_names` Defines the labels for each button.

Every item in the `feather_crop_values` defines how the crop rect will be presented. For instance, the following item:
    
	<item>3:2</item>
	
will create a crop area restricted in its proportions to 3 by 2. Or the following one:

    <item>-1:-1</item>
	
will create a crop area restricted to the original image width and height.

All the previous examples will create a crop area with restricted proportions. 
If you want to allow the user to have a crop rect without limitations, just use an item like this:

    <item>0:0</item>
	
#### Red Eye, Whiten, Blemish and Draw Tool
`feather_brush_sizes` An array containing all the brush sizes available for the user.

#### Draw Panel
`feather_brush_softValue` Defines the softness value for the brush pen.

`feather_default_colors` defines the available brush colors.

#### Stickers
`feather_sticker_highlight_minsize` Minimum size of stickers while resizing.

`feather_sticker_highlight_padding` Padding of the highlight area from the sticker edges.

`feather_sticker_highlight_stroke_width` Stroke size of the highlight area.

`feather_sticker_highlight_ellipse` Ellipse size of the highlight area borders.

`feather_sticker_highlight_stroke` Highlight stroke color.

`feather_sticker_highlight_stroke_down` Highlight stroke color when pressed.

`feather_sticker_highlight_outline` Highlight fill color.

`feather_sticker_highlight_outline_down` Highlight fill color when pressed.

<a name="customization"></a>
### 6.3 UI Customization

You can customize almost every aspect of the application by editing the `styles.xml` 
file included in the res folder.

<a name="localization"></a>
7 Localization
--------------

Android is really smart regarding localization. Localizing resources and strings is very easy.

Here are the instructions to create a new language for all the label messages of Aviary 
(let's say we want to add Italian support):

* Go into the **AviaryFeather/res** folder
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

The current version of the SDK comes with a bunch of localized languages, you can find them inside the "res" folder of the Feather project. If you need a language that isn't yet available, you can contact us at [api@aviary.com](mailto:api@aviary.com) and we'll work with you to add it!

<a name="proguard"></a>
8 Proguard
------

If your application is compiled using [proguard](http://developer.android.com/guide/developing/tools/proguard.html), you need to update your `proguard.cfg` file according to the proguard.cfg file included in the sample application.


<a name="crash_report"></a>
9 Crash Report
------

The crash reporting tool provided with the standard Android Market is often useless due to the minimal amount of information provided. If you want to report crashes which occur in our application to us, we suggest that you include an external crash report tool like [ACRA](http://code.google.com/p/acra/) in your application.


<a name="hires"></a>
10 Hi-Resolution Image Editing
------

By default, the Aviary editor works on a medium resolution image, to speed up the performance of the editor. 
But you can also enable hi-res saving of images, up to 3MP. If you need higher resolution output, please contact us at [partners@aviary.com](mailto:partners@aviary.com).

* In the calling Intent you must pass an extra string, the hi-res session id, like this:
	
		final String session_name = StringUtils.getSha256( System.currentTimeMillis() + API_KEY );
		newIntent.putExtra( "output-hires-session-id", session_name );
		
	The session string must be unique and must be 64 char in length.
	Once Aviary starts, it will start collecting the information of every action performed on the image and will store those
	actions in the internal ContentProvider (remember to add the provider tag to the AndroidManifest first!).
	
* Once your activity calls the "onActivityResult" method, you can process the HD image.

	First create a new file where the HD image will be stored
	
		File destination = getNextFileName();
		
	Initialize the session instance

		FeatherContentProvider.SessionsDbColumns.Session session = null;
	
	session_name is the same session string you passed in the calling intent
		
		Uri sessionUri = FeatherContentProvider.SessionsDbColumns.getContentUri( session_name );
		
	This query will return a cursor with the information about the given session
		
		Cursor cursor = getContentResolver().query( sessionUri, null, null, null, null );
		
		if ( null != cursor ) {
			session = FeatherContentProvider.SessionsDbColumns.Session.Create( cursor );
			cursor.close();
		}
		
	At this point, you will have a Session object with the following information:
	
	* session.id, the internal id of the session
	* session.name, the session 64 char wide value
	* session.ctime, the session creation time
	* session.file_name, the original image used for editing (the same you passed in the calling Intent)
	
		
	Now you must query the ContentProvider to get the list of actions to be applied on the original image:
	
		Uri actionsUri = FeatherContentProvider.ActionsDbColumns.getContentUri( session.session );
		Cursor cursor = getContentResolver().query( actionsUri, null, null, null, null );
	
	And finally the steps to load, apply the actions and save the HD image (these steps should be performed in a separate thread, like an AsyncTask):
	
	Create an instance of MoaHD class
	
		MoaHD moa = new MoaHD();
		
	Load the image in memory, note that the srcPath can be either a string absolute path or an int (see `ParcelFileDescriptor.getFd()`)

		// result will be Error.NoError is load completed succesfully
		MoaHD.Error result = moa.load( srcPath );
		
	Then, for every row in the actions cursor, apply the action to the moa instance:
	
		if( result == MoaHD.Error.NoError ){
			do {
				// utility to get the action object from the current cursor
				Action action = Action.Create( cursor );
				moa.applyActions( action.getActions() );
			} while( cursor.moveToNext() );
		}
	
	Finally you can save the output image. `dstPath` must be an absolute string path:
	
		result = moa.save( dstPath );
		// if image was saved result will be Error.NoError
		if( result == Error.NoError ){
			// unload the image from memory
			moa.unload();
		}
		moa.dispose();
		
	...And remember to close the cursor:
	
		cursor.close();


<a name="exif"></a>
11 Retain EXIF Tags
------

By default Aviary will retain the original image exif and save them into the output image. But when you process the hi-res image after Aviary is closed, this is not true.
In order to retain the original image EXIF tags into the hi-res image follow these steps (you can also see a real working example inside the sample app provided):

* Use the included **ExifInterfaceWrapper** class ( inside com.aviary.android.feather.library.media package ), which can handle a larger number of tags rather than the Android default ExifInterface class

* Create a new instance of ExifInterfaceWrapper, passing the original source image:<br />

		ExifInterfaceWrapper originalExif = new ExifInterfaceWrapper( srcPath );

* When the hi-res process is complete, create a new ExifInterfaceWrapper instance passing the path of the just created image:<br />

		newExif = new ExifInterfaceWrapper( dstPath );

* Copy the original exif tags into the destination exif:<br/>

		originalExif.copyTo( newExif );<br />
		// the editor auto rotate the image pixels
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


