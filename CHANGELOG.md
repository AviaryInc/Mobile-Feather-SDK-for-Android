# Aviary Android SDK Changelog

[Subscribe to updates](https://github.com/AviaryInc/Mobile-Feather-SDK-for-Android/commits/master.atom)

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
