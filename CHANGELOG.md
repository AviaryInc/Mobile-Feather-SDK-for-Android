# Aviary Android SDK Changelog

[Subscribe to updates](https://github.com/AviaryInc/Mobile-Feather-SDK-for-Android/commits/master.atom)

## Version 2.0.6, March 28, 2012
* Updated so Android SDK-17 ( http://developer.android.com/sdk/index.html )
* Rotation tool. Added free rotation using fingers (see [README](http://www.aviary.com/android-documentation) for more info)
* Added support for arm-5 devices
* updated redeye, whiten and blemish tools
* updated brightness and contrast tool to a better algorithm
* updated sharpness tool
* stickers can now be dragged

## Version 2.0.5
* Fixed an error with the chinese translations
* Fixed an error which prevented to load huge images

## Version 2.0.4 - March 2, 2012
* fixed a bug in the crop panel when using custom crop values
* fixed adjust panel after changing device orientation
* fixed an orientation bug on some devices
* fixed hires image load on honeycomb tablets
* added the possibility to disable borders from the effects (see [README](http://www.aviary.com/android-documentation) for more info)
* fixed a bug which kept the cursor from being hidden correctly after clicking apply from the text panel
* fixed loading images from remote urls. now redirects are supported too.

## Version 2.0.3 - February 17, 2012
* added "stickers-enable-external-pack" in the calling Intent to disable the purchasable stickers
* fixed crashes on ldpi devices
* added HI-RES support, please see the [README](http://www.aviary.com/android-documentation) for more information
* added localized strings in various languages
* fixed issue in the effects panel on screen rotation
* fixed update issue on honeycomb system

## Version 2.0.2 - January 30, 2012
* fixed an issue in the Meme tool displaying an incorrect text size
* In the passed intent, added "max-image-size" key to allow developers to change the maximum allowed image size at runtime. ( see the README for more info)

## Version 2.0.1 - January 19, 2012
* fixed a couple of issues on honeycomb devices ( in the effects and ehnance panel )

## Version 2.0
* major version release, now the SDK_VERSION is a String saying "2.0".
* added optional permission &lt;uses-permission android:name="android.permission.VIBRATE" /&gt; to enhance user experience with some components
* changed all the style elements
* changed most of the configuration items ( see config.xml )
* The Project Build Target must be set to "Android 4.0" (API level 14), even if the minSdkVersion still remains Android 2.2 (API level 8).
* Purchasable content. Added the possibility to download more stickers. For this reason you need to add a new entry (a receiver) in the manifest xml file. Please refer to the readme for more informations.
* Removed the InfoScreenActivity, so you need to remove that entry from the manifest file too.
* Changed the default effects
* Removed the "sharpen" and "blur" panels. Now there's only 1 "sharpness" panel
* Red-eye, Whiten and Blemish panels now support drawing paths instead of a single touch.
* Added the "Enhance" panel, with 4 different auto-enhance filters.
* Added "Orientation" panel with rotation and flip transformations.
* Crop panel. Now user has the possibility to chose between a custom crop rect or different "fixed" ratio crop rectangles. Read the readme file for more informations on how to add more default values.
* brigthness, contrast and saturation now use a wheel component instead of the 4 thumbnails.
* Removed the big aviary logo at top.
* Changed the way stickers work. Added the possibility to download more stickers from the market. Follow the README file for more informations.
* Added purchasable effects
* Purchasable effects can be disabled using the launching Intent ( see the README for more info )

---

## Version 42

* Added "effect-enable-fast-preview" in the input Intent to enable the fast preview rendering.
* Added "enable-more-stickers" in the input Intent to enable/disable the new sticker panel.
* If you enable the new stickers panel you also must add a new entry in your AndroidManifest.xml file
* Now when passing the string array "tools-list" in the input Intent, tools will be shown in the exact order.
* Removed unused resources
* Changed image size depending on the cpu speed, memory and screen size
* Added confirmation dialog when trying to cancel sticker panel with unsaved changes

---

## Version 41
* Fixed a bug with the negative effect

---

## Version 40
* Fixed a bug with the external plugins installation/removal notification
* Fixed a bug in the sticker panel. When after removing an external pack the view did not update correctly.

---

## Version 39
* New sticker panel with option to download more stickers. In order to enable this new panel you MUST pass in the AviaryFeather activity intent a "enable-more-stickers" boolean extra. i.e.: 
	newIntent.putExtra( "enable-more-stickers", true );
	
Moreover you must enable a new receiver in your application manifest file. i.e:
		<receiver 
		    android:name="com.aviary.android.feather.receivers.FeatherSystemReceiver" 
		    android:exported="true" 
		    android:process=":feather_system_receiver">
			<intent-filter>
    			<action android:name="android.intent.action.PACKAGE_ADDED" />
    			<action android:name="android.intent.action.PACKAGE_REMOVED" />
    			<action android:name="android.intent.action.PACKAGE_REPLACED" />
    			<data android:scheme="package" />
			</intent-filter>
		</receiver>	
	
See the sample app for an example of these changes.

* Added confirmation dialog before leave the stickers panel with unsaved changes ( both using back button or cancel button )
	
---		

## Version 38
* fixed bug with the Negative effect
* Effects panel: removed the modal progress dialog. Now the preview will be updated in 2 steps. First a very low resolution image is being presented to the user while the current image is rendered in background. The first low resolution image size will depend on the device cpu speed.
* Stickers. Removed the "Add Another" and "Clear All" panel while editing the single sticker. Now every time a sticker is clicked from the stickers list the current active sticker is being flattened with the back image and a new sticker is added. Also the back button is handled in a different way. If the user has one sticker active and selected it will be deselected. Otherwise the back button will be handled like in the previous versions.

---

## Version 37
* Reading the exif information from the input image. Now the pictures should be loaded using the correct orientation
* Fixed tools display on tablets. Now the number of tools ( also effects and stickers ) is dynamic, depending on the device screen size
* Fixed crash on some devices on applying filters
* Fixed an issue with the meme tool not correctly hiding the cursor when generating the final image
* Added support for screen orientation changes ( feather is optimized to work only in portrait mode, but you can remove the android:screenOrientation in
		your manifest file if you want to use the current device orientation ).

---
	
## Version 36
* Fixed compatibility issue with latest Google ADT 14.0
