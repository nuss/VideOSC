[![Build Status](https://travis-ci.org/nuss/VideOSC.svg?branch=master)](https://travis-ci.org/nuss/VideOSC)

VideOSC is an experimental OSC-controller for Android that uses RGB data for generating OSC-messages. The phones camera input (video-stream, smallest possible preview size) first gets downsampled to a user-defined size (e.g. 8 x 6 px) and the RGB-information of each pixel of the downsampled preview gets sent to an arbitrary IP address in the network. Hence, a preview of 8 x 6 px gives 144 separate values (0-255) at the interval defined by the framerate of the app.

Website: http://videosc.net

# Installation #

[VideOSC is available from Google Play](https://play.google.com/store/apps/details?id=net.videosc). It can, however, be installed from source as well, using Android Studio. The Android developer tools, including the necessary compiler, are needed. These can be obtained freely from https://developer.android.com. The application should be compatible with Android 4.0.3 and higher.

Furthermore the following external libraries are needed:

* oscP5 (comes with processing)
* Ketai - https://github.com/ketai/ketai
* APwidgets - https://code.google.com/p/apwidgets/
 
The application must be compiled with the following phone-permissions:

* CAMERA
* WAKE_LOCK
* INTERNET

# Usage #

OSC-messages coming from VideOSC are provided within the default namespace (can be set to a different name in the preferences)
```
/vosc
```
amended by the command-name related to color and the pixel that is used. E.g.:
```
/vosc/red1
```
will contain the current value of the first pixel's red channel. The numbering within the command name refers to the position of the pixel within the image's pixel-array: The first pixel is the pixel in the upper left corner of the image, the last pixel is the pixel in the bottom right corner of the image. The pixels are read and processed line by line, beginning in the upper left corner and ending in bottom right corner. Hence an image of e.g. 7 x 4 pixels will send 28 messages for each channel (red, green, blue):
```
/vosc/red1, /vosc/red2 ... /vosc/red28
/vosc/green1, /vosc/green2 ... /vosc/green28
/vosc/blue1, /vosc/blue2 ... /vosc/blue28
```
The resolution of the preview image (the down-scaled preview of the incoming video stream) can be changed by the user at any time while the app is running and the number of outgoing OSC messages will be changed accordingly.

Also, OSC-messages sent to the app under the same command name as the outgoing messages may be displayed if the user selects the regarding channel-preview (hidden behind the RGB symbol in the app's menu). 

##### Preview, RGB-Mode #####
![VideOSC: RGB-mode] (https://github.com/nuss/VideOSC/master/layout_data/Nexus-VideOSC-Montage_RGB.png "Preview RGB-mode")

##### Preview, green channel only, with toolbars hidden, displaying incoming OSC feedback messages #####
![VideOSC: channel-mode (red)] (https://github.com/nuss/VideOSC/master/layout_data/Nexus-VideOSC-Montage_G.png "Preview channel-mode, channel green, hidden toolbars, displaying incoming OSC") 
