# Aviary Android SDK Changelog

Subscribe to updates: [RSS](https://github.com/AviaryInc/Mobile-Feather-SDK-for-Android/commits/master.atom) | [Twitter](http://twitter.com/aviarychangelog)

## Version 3.0.2 - July 28, 2013
* Now the Aviary API key must be declared inside the AndroidManifest.xml file. See the "README_FIRST.html" file for more informations.
* Added the restore button. Now users can restore the original image inside the SDK.
* Added "pre-post". Inside the SDK touch the image to see the original version of the image.
* Added accessibility descriptions to images/tools/labels
* Added badges on tools when a new content is available for that specific tool
* Fixed featured order in the stickers panel

## Version 3.0.0 - June 25, 2013
* We dropped the support to Android 2.2 ( api level 8 ). Now the minimum version supported is Android 2.3 ( api level 9 ).
* Completely changed the UI and its styles. Moreover now all the attributes and styles have been renamed adding the "aviary" prefix, this to prevent conflict with your application.
* There's a new theme which must be associated with the `FeatherActivity` entry in the AndroidManifest.xml file: `AviaryTheme`. This theme is declared inside the `aviary_theme.xml` file.

* Added 1 new constant to the incoming Intent: `Constants.EXTRA_IN_SAVE_ON_NO_CHANGES` and a new extra will be passed to the result Intent: `Constants.EXTRA_OUT_BITMAP_CHANGED`.
These 2 new keys are used to manage the case when an user clicks on the "Done" button without any modifications to the image. Please read the README file for further informations about this topic.

* Text tool: now allows multiline text editing

* Premium Partners only: The limit for hi-res editing has been increased to 30 megapixels


## Version 2.4.2 - April 9, 2013
* The FeatherSystemReceiver has been moved into `com.aviary.android.feather.library.receivers.AviarySystemReceiver`. Please update your `AndroidManifest.xml` file accordingly to reflect this change.
* Minor bug fixes.

## Version 2.4.1 - March 25, 2013
* Enhance tool speed improvements
* Added brand new Focus Tool: adds a selective circular or rectangular blur to photos
* Added brand new Splash tool: selectively adds color back into photos.
* Fixed blur/sharpen issues on hi-res images
* Fixed android.media.process leak
* Text Tool improvements:
	* Text color can be changed even if the text is not selected
	* Removed white stroke from the font
* Fixed ImageView zoom/orientation issue
* Update proguard-project.txt settings
* Updated sample project: FeatherActivity now runs in a separate process
* Added Exceptions to the MoaHD class ( removed the Error enum )
* Updated translations and added some missing
* Tools now reset the imageview after opening
* Landscape orientation disabled on non tablets devices


## Version 2.2.1 - January 21, 2013
* Overhauled Effects and Stickers tools with completely redesigned UI
* Updated default effects: effects are now more subtle and generally usable, based on your feedback
* Added brand new Frames Tool
* Added support for x86
* **IMPORTANT**: Changed the way the SDK is passed the API-KEY (please read README.md for more information)
* Fixed a bug that was affecting the Orientation tool

## Version 2.1.91 - October, 2012
* Fixed stickers pack support for paid encrypted apps
* Fixed bug in orientation tool when rotating 90 and straightening
* Smoother animations
* Fixed a bug which was causing a crash in the stickers tool on small screen devices
* Added more crop options
* Fixed sharing from other apps (QuickPic)
* Fixed and error during drag and drop

## Version 2.1.8 - September, 2012
* Updated stickers and text zoom/pan behavior. Now while panning and zooming the content will be zoomed and pannel as well.
* The load of images that are just a bit bigger than the max size is improved.
* Added support for hi-res stickers.
* Fixed wrong orientation in some cases.
* Fixed png image load.
* Updated Exif library.
* Updated saturation filter to work like all the others wheel tools.
* Updated native library for ndk-r8b.
* Rotation panel: fixed reset animation when leaving the panel.
* Fixed crop rect sometimes appeared in the wrong place.
* Fixed a bug when the adding a sticker bigger than the current screen.
* Added the possibility to use custom fonts for all the ui elements.
* Fixed crop tool, now handles will stop at edges correctly.
* Better support for large screens ( tablets ).
* Updated crop tool, now only tapped side will be moved.
* vibration toggle now works in every tool.
* Fixed recycled bitmap crash on drawing tool.


## Version 2.1.5 - July 9, 2012
* Updated to Android SDK r20 (http://developer.android.com/sdk/index.html)
* Added the possibility to disable vibration feedback in certain tools (see the README for more info)
* Fixed a bug when pressing back button before the image was fully loaded
* EXIF data is retained by default within the editor. (see the README for information on how to retain EXIF data for hires images)
* Added EXIF load/save examples in the sample app
* Added B&W and Sepia effects
* Fixed backspace bug in text and meme tools
* Other general bug fixes and improvements

## Version 2.1.2 - May 31, 2012
* Fixed crash in the drawing panel and rotation panel on some devices
* Added Warmth tool (color temperature)

## Version 2.1.0 - May 21, 2012
* Updated to Android SDK-19 (http://developer.android.com/sdk/index.html)
* Native libraries compiled with NDK-r8
* Fixed a bug in the stickers panel
* updated redeye and whiten
* updated blemish
* removed unused code, improved backward compatibility using reflections
* reduced size of fast preview in effects panel ( for faster preview )
* deferred tools initialization
* updated enhance and sharpness actions name

## Version 2.0.6 - March 28, 2012
* Updated to Android SDK-17 (http://developer.android.com/sdk/index.html)
* Added support for arm-5 devices
* Updated redeye and whiten tools
* Updated brightness and contrast tools to a new algorithm with better results
* Updated sharpness tool; should be much faster
* Stickers can now be dragged onto the canvas

## Version 2.0.5
* Fixed an error with Chinese localization
* Fixed an error which prevented loading huge images

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
