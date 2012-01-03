Aviary Feather Android Setup Guide
==============================

Contents
--------

* Introduction
	* Prerequisites
* Workspace setup
* Sample Application
* Include AviaryFeather in a new Application
	* Create a new Android project
	* Project references
	* AndroidManifest.xml
	* themes.xml
* Invoke Feather
	* Intent parameters
	* Result parameters
* Extras
	* Stickers
	* Other configurations
	* UI Customization
* Localization

## 1 Introduction

This document will guide you through the creation of a sample application using the AviaryFeather Android library.


### 1.1 Prerequisites

Aviary Android SDK supports Android 2.2+ as [minSdkVersion](http://developer.android.com/guide/topics/manifest/uses-sdk-element.html#min), but it must be compiled using Android 4.0 (API level 14) as target sdk. This means that your application must have selected "Android 4.0" in the "Project Build Target" eclipse panel.

I assume you already have the Android environment installed on your system and Eclipse with the required ADT plugin.
See http://developer.android.com/sdk/installing.html and http://developer.android.com/sdk/eclipse-adt.html if you need instructions on how to setup the Android environment.

You will also need an Aviary API key/secret pair to access the remove effect API. To sign up or learn more, please visit http://developers.aviary.com/geteffectskey.



## 2 Workspace setup

First, we’ll need to import the 2 Eclipse projects into our workspace.

Open Eclipse and select “Import” from the file menu.

![import project in eclipse](http://labs.sephiroth.it/tmp/android/1.png)


The import dialog will appear. From the list of import options, select “Existing Projects into Workspace,” and then click “Next.”

![import project in eclipse](http://labs.sephiroth.it/tmp/android/2.png)


In the new dialog, click on the “Select archive file” radio button and then click the “Browse” button on the right. From here, select the aviaryfeather.zip file included with this document.
Click on the “Finish” button at the bottom of the dialog. A new Android library project called “AviaryFeather” will be created in your current workspace. This is the required library project which you must include in your application if you want to use Feather to manipulate images.

![import project in eclipse](http://labs.sephiroth.it/tmp/android/3.png)




## 3 Sample Application

Next, we need to create an Android application in order to use Feather. You can see a real example of how to use Feather by opening the included sample-app.zip project.

Just import the sample application by following the same procedures described above, but select sample-app.zip at step 3. 

A new project called “AviaryLauncher” will be created in your workspace. You can inspect this app to see a sample usage of the aviary sdk.

The imported application should have all the references already set and it should be ready to use. If you want to include AviaryFeather in a different Android project or add it to a new one, follow the instructions in step 4; otherwise you can skip to step 5.


## 4.1 Include AviaryFeather in a new Application

If you don’t want to use the included sample application to test Feather, here’s a step by step guide on how to include Feather in a new Android application.


### 4.1 Create a new Android project

Just create a new Android project as usual from Eclipse and select Android 4.0 in the Build Target Panel.

![new eclipse project](http://labs.sephiroth.it/tmp/android/4.png)

Once the new project has been created, open the project properties and navigate to the “Android” section.
Click the “Add...” button of the “Library” subsection and select “AviaryFeather” from the dialog.

![project setup](http://labs.sephiroth.it/tmp/android/6.png)


Next, navigate to the “Java Build Path” section of the project properties dialog and click on “Add JARs...” button of the “Libraries” subsection.

![project setup](http://labs.sephiroth.it/tmp/android/7.png)

From here, select all the .jar file included in the “libs” folder of the AviaryFeather project (**aviaryfeatherlibrary.jar**).

![project setup](http://labs.sephiroth.it/tmp/android/8.png)


### 4.2 AndroidManifest.xml
Add some entries to the manifest file of your application.

**Permissions**
AviaryFeather requires internet and write access to external storage. To grant those permissions, add these entries inside the AndroidManifest.xml &lt;manifest&gt; tag:

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />


An additional permission is necessary, but not mandatory:

    <uses-permission android:name="android.permission.VIBRATE" />
	
This permission will enable the vibration feedback on some feather components, for a better user experience. Omit this permission if you dont want the vibration feedback.


**Activity declaration**

As said before, aviary sdk supports android 2.2 as minimum android version, for this reason the "uses-sdk" xml node of your manifest should look like this:
    <uses-sdk android:minSdkVersion="8" />

Then, inside the &lt;application&gt; tag, add a reference to the FeatherActivity:

    <activity
        android:name="com.aviary.android.feather.FeatherActivity"
        android:configChanges="orientation|keyboardHidden"
        android:screenOrientation="unspecified"
        android:hardwareAccelerated="true"
        android:largeHeap="true"
        android:theme="@style/FeatherDefaultTheme.Custom" />

And also a reference to the plugins receiver is necessary:

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


### 4.3 Theme and Styles

The android:theme entry in the manifest file is also required for Feather to work properly, so add an entry to your themes.xml file (if you don’t have one, create a new file called themes.xml in your res/values folder):

    <?xml version="1.0" encoding="utf-8"?>
    <resources>
        <style name="FeatherTheme.Custom" parent="FeatherDefaultTheme" />
    </resources>


By default, this entry will use the default Feather theme.
If you’d like to customize the Feather UI, you can do that simply by adding entries to your “Feather.Custom” style. Check out
the **styles.xml** file included in AviaryFeather/res/values for the list of available styles.

Note that many ui elements depends both on the styles.xml and on the config.xml file included. The styles.xml declares the ui components general appearance, while in the config.xml you'll find component specific dimensions ( like for text or lists ) and most of the properties for customize feather's panels behavior.

## 5. Invoke Feather

If you’re calling Feather from a new application, you’ll need to add the below code in order to start Feather. Otherwise (if you’re using the demo application) you can find this code inside the MainActivity.java file.

In order to invoke Feather from your activity, you need to pass some parameters to the FeatherActivity. Here’s an example of how to invoke the new activity:


    // Create the intent needed to start feather
    Intent newIntent = new Intent( this, FeatherActivity.class );
    // set the source image uri
    newIntent.setData( uri );
    // pass the required api key ( http://developers.aviary.com/ )
    newIntent.putExtra( "API_KEY", “xxx” );
    // pass the uri of the destination image file (optional)
    // This will be the same uri you will receive in the onActivityResult
    newIntent.putExtra( “output”, Uri.parse( "file://" + mOutputFile.getAbsolutePath() ) );
    // format of the destination image (optional)
    newIntent.putExtra( “output-format”, Bitmap.CompressFormat.JPEG.name() );
    // output format quality (optional)
    newIntent.putExtra( “output-quality”, 85 );
    // you can force feather to display only a certain tools
    // newIntent.putExtra( "tools-list", new String[]{"ADJUST", "BRIGHTNESS" } );

	// enable fast rendering preview
	newIntent.putExtra( "effect-enable-fast-preview", true );
	
    // you want to hide the exit alert dialog shown when back is pressed
    // without saving image first
    // newIntent.putExtra( "hide-exit-unsave-confirmation", true );
    
    // ..and start feather
    startActivityForResult( newIntent, ACTION_REQUEST_FEATHER );



## 5.1 Intent parameters

Here’s a description of the required parameters:

* **Uri**

	( intent data ) This is the source uri of the image to be used as input by Feather


* **API_KEY**

	api key IS REQUIRED to use remote filters. Go to http://developers.aviary.com for more information on how to obtain your api key and secret


* **output**

	This is the uri of the destination file where Feather will write the result image


* **output-format**

	Format of the output file ( jpg or png )


* **output-quality**

	Quality of the output image ( required only if output-format is jpeg ). 0 to 100


* **effect-enable-fast-preview**

	Depending on the current image size and the current user device, some effects can take longer than expected to render the image.
Passing in the caller intent this flag as boolean "true" the effect panel will no longer use the default
progress modal dialog while rendering an effect but instead will use a small "loading" view while rendering
a small image preview. User will see "almost" immediately the small preview while the full size image is being
processed in background. Once the full size image is processed it will replace the small preview image.
Default behavior is to enable this feature only on fast devices ( fast enough to allow the small preview to be
rendered immediately ).
Pass "false" if you want to force the "progress modal" rendering model. No small preview, only a modal progress while rendering the image.


* **hide-exit-unsave-confirmation**

	If you want to hide the exit alert dialog shown when back key (or the top cancel button) is pressed without saving image first.


* **tools-list**

	If specified in the extras of the passed intent it will tell feather to display only certain tools. The value must be a String[] array and the available values are: 
	
    SHARPNESS, BRIGHTNESS, CONTRAST, SATURATION, EFFECTS, RED_EYE, CROP, WHITEN, DRAWING, STICKERS, TEXT, BLEMISH, MEME, ADJUST, ENHANCE,


* **hide-exit-unsave-confirmation**

	When the user click on the back-button and the image contains unsaved data a confirmation dialog appears by default. Setting this flag to true will hide that confirmation and the application will terminate.



## 5.2 Result parameters


Once the user clicks “save” in the Feather activity, the “onActivityResult” of your Activity will be invoked, passing back **“ACTION_REQUEST_FEATHER”** as requestCode.
The Uri data of the returned intent will be the output path of the result image:

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



## 6. Extras


### 6.1 Stickers

The sample application already includes a set of stickers which will be shown as default pack. In addition users can install from the market more packs and they will be added automatically into the stickers panel.
If you want to use those stickers just copy the folder "assets/stickers" into your application project. 
If you don't want to include default stickers you need to change a value in the file "plugins.xml" included in the res/values folder:

Change the line:

    <integer name="is_sticker">1</integer>

into:

    <integer name="is_sticker">0</integer>

In this way users won't see any default stickers pack, but instead only a link to download more packs.

![stickers](http://labs.sephiroth.it/tmp/android/9.png)


## 6.2 Other configurations

Inside the AviaryFeather/res/values is a config.xml file. This file contains some application default values and can be modified before compilation.



## 6.3 UI Customization

You can customize almost every aspect of the application by editing the "styles.xml" file included in the res folder.

## 7 Localization

Android is really smart regarding localization. Localizing resources and strings is very easy.

Here the instructions to create a new language for all the labels messages of feather (let's say we want to add italian support):

* Go into the **AviaryFeather/res** folder
* Create a new folder "values-it". 
* Copy the file **res/values/strings.xml** into the **res/values-it** folder.
* Open the **res/values-it/strings.xml** file with any text editor and translate all the strings within the &lt;string&gt;&lt;/string&gt; tag. For instance, the original version of the string "Save" is:

    
	`<string name="save">Save</string>`

* in your localized strings.xml file it will be:

    `<string name="save">Salva</string>`

Now just clean and recompile your application. If your device has set italian as default language you will see feather in italian.

For a more detailed tutorial about android localization look at this page: http://developer.android.com/resources/tutorials/localization/index.html

