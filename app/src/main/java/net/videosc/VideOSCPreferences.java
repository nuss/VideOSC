package net.videosc;

import java.util.Calendar;

import apwidgets.APButton;
import apwidgets.APCheckBox;
import apwidgets.APEditText;
import apwidgets.APWidget;
import apwidgets.APWidgetContainer;
import apwidgets.OnClickWidgetListener;
import ketai.data.KetaiSQLite;
import ketai.net.KetaiNet;
import ketai.ui.KetaiAlertDialog;
import netP5.NetAddress;
import processing.core.PApplet;

/**
 * Package: net.videosc
 * Created by Stefan Nussbaumer on Okt 05, 11:51.
 */

public class VideOSCPreferences extends VideOSC {
	private static String TAG = "VideOSCPreferences";

	static void createPreferences(final PApplet applet, String select, final KetaiSQLite db) {
		final APWidgetContainer apContainer = new APWidgetContainer(applet);

		if (select.equals("Network Settings")) {
//			Log.d(TAG, "select equals Network Settings");

			final APText ipText = new APText(50, 50, (applet.width - 100) / 2 - 60, 50 * (int)
					density);
			ipText.setText("send OSC to IP:");
			ipText.setTextSize(20);
			final APEditText ipField = new APEditText(50, 50 + ((int) density * 30), (applet.width -
					100) / 2 - 60, 50 * (int) density);
			ipField.setInputType(1);
			ipField.setText(sendAddr);

			final APText portText = new APText((applet.width - 95) / 2 - 10, 50, (applet.width - 95)
					/ 2
					- 70, 50 * (int) density);
			portText.setText("send OSC to port:");
			portText.setTextSize(20);
			final APEditText portField = new APEditText((applet.width - 95) / 2 - 10, 50 + ((int)
					density * 30), (applet.width - 95) / 2 - 70, 50 * (int) density);
			portField.setInputType(2);
			portField.setText(str(broadcastPort));

			final APText deviceIPText = new APText(50, 50 + ((int) density * 90), (applet.width -
					100)
					/ 2 - 60, 50 * (int) density);
			deviceIPText.setText("device IP address:");
			deviceIPText.setTextSize(20);
			final APText deviceIP = new APText(70, 50 + ((int) density *
					140), (applet.width - 95) / 2 - 85, 50 * (int) density);
			deviceIP.setTextSize(20);
			deviceIP.setText(KetaiNet.getIP());

			final APText receivePortText = new APText((applet.width - 95) / 2 - 10, 50 + ((int)
					density
					* 90), (applet.width - 95) / 2 - 70, 50 * (int) density);
			receivePortText.setText("receive OSC-feedback on port:");
			receivePortText.setTextSize(20);
			final APEditText receivePortField = new APEditText((applet.width - 95) / 2 - 10, 50 + (
					(int) density * 140), (applet.width - 95) / 2 - 70, 50 * (int) density);
			receivePortField.setInputType(2);
			receivePortField.setText(str(listenPort));

			final APText cmdNameText = new APText(50, 50 + 200 * (int) density, applet.width -
					300, 50
					* (int) density);
			cmdNameText.setText("command name:");
			cmdNameText.setTextSize(20);
			final APText cmdNameTextPre = new APText(50, 50 + 240 * (int) density, 20, 50 * (int)
					density);
			cmdNameTextPre.setTextSize(20);
			cmdNameTextPre.setText("/");
			final APEditText cmdNameSpace = new APEditText(70, 50 + 230 * (int) density, (applet
					.width
					- 95) / 2 - 85, 50 * (int) density);
			cmdNameSpace.setInputType(1);
			cmdNameSpace.setText(rootCmd);
			final APText cmdNameTextPost = new APText((applet.width - 95) / 2 - 10, 50 + 240 * (int)
					density, 500, 50 * (int) density);
			cmdNameTextPost.setTextSize(20);
			cmdNameTextPost.setText("/<colorN>");

			final APButton cancel = new APButton(50, 50 + 290 * (int) density, (applet.width -
					220) /
					2, 60 * (int) density, "Cancel");
			final APButton setNetwork = new APButton(50 + (applet.width - 220) / 2, 50 + 290 * (int)
					density, (applet.width - 220) / 2, 60 * (int) density, "Save Settings");

			apContainer.addWidget(ipText);
			apContainer.addWidget(portText);
			apContainer.addWidget(receivePortText);
			apContainer.addWidget(deviceIPText);
			apContainer.addWidget(deviceIP);
			apContainer.addWidget(cmdNameText);
			apContainer.addWidget(cmdNameTextPre);
			apContainer.addWidget(cmdNameTextPost);

			apContainer.addWidget(ipField);
			apContainer.addWidget(portField);
			apContainer.addWidget(receivePortField);
			apContainer.addWidget(cmdNameSpace);
			apContainer.addWidget(cancel);
			cancel.getView().setBackgroundColor(0);
			apContainer.addWidget(setNetwork);

			setNetwork.addOnClickWidgetListener(new OnClickWidgetListener() {
				@Override
				public void onClickWidget(APWidget apWidget) {
					sendAddr = ipField.getText();
					broadcastPort = parseInt(portField.getText());
					listenPort = parseInt(receivePortField.getText());
					broadcastLoc = new NetAddress(sendAddr, broadcastPort);
					feedbackAddr = new NetAddress(sendAddr, listenPort);
					rootCmd = cmdNameSpace.getText();

					if (match(rootCmd, "^[a-zA-Z0-9]+[a-zA-Z0-9_/]*[a-zA-Z0-9]+$") != null) {
						boolean querySuccess = VideOSCDB.updateNetworkSettings(db);
						if (querySuccess) {
							apContainer.removeWidget(ipText);
							apContainer.removeWidget(portText);
							apContainer.removeWidget(receivePortText);
							apContainer.removeWidget(deviceIPText);
							apContainer.removeWidget(deviceIP);
							apContainer.removeWidget(cmdNameText);
							apContainer.removeWidget(cmdNameTextPre);
							apContainer.removeWidget(cmdNameTextPost);

							apContainer.removeWidget(ipField);
							apContainer.removeWidget(portField);apContainer.removeWidget(receivePortField);
							apContainer.removeWidget(setNetwork);
							apContainer.removeWidget(cmdNameSpace);
							apContainer.removeWidget(cancel);
							curOptions = "";
						} else {
							KetaiAlertDialog.popup(applet, "SQL Error", "Updating network settings failed.");
						}
					} else {
						KetaiAlertDialog.popup(applet, "malformed commandname", "The command-namespace " +
								"must begin	and	end with a letter or a number and may only " +
								"contain letters, numbers, _ and /.");
					}
				}
			});
			cancel.addOnClickWidgetListener(new OnClickWidgetListener() {
				@Override
				public void onClickWidget(APWidget apWidget) {
					apContainer.removeWidget(ipText);
					apContainer.removeWidget(portText);
					apContainer.removeWidget(receivePortText);
					apContainer.removeWidget(deviceIPText);
					apContainer.removeWidget(deviceIP);
					apContainer.removeWidget(cmdNameText);
					apContainer.removeWidget(cmdNameTextPre);
					apContainer.removeWidget(cmdNameTextPost);

					apContainer.removeWidget(ipField);
					apContainer.removeWidget(portField);apContainer.removeWidget(receivePortField);
					apContainer.removeWidget(setNetwork);
					apContainer.removeWidget(cmdNameSpace);
					apContainer.removeWidget(cancel);
					curOptions = "";
				}
			});
			curOptions = "network";
		} else if (select.equals("Resolution Settings")) {
			final APText setResWText = new APText(50, 50, (applet.width -100) /2 - 60, 50 * (int)
					density);
			setResWText.setTextSize(20);
			setResWText.setText("horizontal resolution:");
			final APEditText setResW = new APEditText(50, 50 + ((int) density * 30), (applet.width -
					100) / 2 - 60, 50 * (int) density);
			setResW.setInputType(2);
			setResW.setText(str(resW));

			final APText setResHText = new APText((applet.width - 95) / 2 - 10, 50, (applet.width -
					100) / 2 - 60, 50 * (int) density);
			setResHText.setTextSize(20);
			setResHText.setText("vertical resolution:");
			final APEditText setResH = new APEditText((applet.width - 95) / 2 - 10, 50 + ((int)
					density
					* 30), (applet.width - 100) / 2 - 60, 50 * (int) density);
			setResH.setInputType(2);
			setResH.setText(str(resH));

			final APText setFrameRateText = new APText(50, 50 + ((int) density * 90), (applet
					.width -
					100) / 4 - 30, 70 * (int) density);
			setFrameRateText.setTextSize(20);
			setFrameRateText.setText("set framerate in frames per second:");
			final APEditText setFrameRate = new APEditText(50, 50 + ((int) density * 170), (applet
					.width - 100) / 4 - 30, 50 * (int) density);
			setFrameRate.setInputType(2);
			setFrameRate.setText(str(framerate));

			final APText setCalcPeriodText = new APText((applet.width - 100) / 4 + 40, 50 + ((int)
					density * 90), (applet.width - 100) / 4 - 50, 70 * (int) density);
			setCalcPeriodText.setTextSize(20);
			setCalcPeriodText.setText("set calculation period:");
			final APEditText setCalcPeriod = new APEditText((applet.width - 100) / 4 + 20, 50 + (
					(int)
					density * 170), (applet.width - 100) / 4 - 30, (int) density * 50);
			setCalcPeriod.setInputType(2);
			setCalcPeriod.setText(str(calcsPerPeriod));

			final APCheckBox setNormalize = new APCheckBox((applet.width - 95) / 2 - 10, 50 + ((int)
					density * 180), "normalize output (0.0-1.0)");
			if (normalize)
				setNormalize.setChecked(true);
			else setNormalize.setChecked(false);

			final APButton cancel = new APButton(50, 50 + ((int) density * 230), (applet.width -
					220) /
					2, 60 * (int) density, "Cancel");
			final APButton setResolution = new APButton(50 + (applet.width - 220) / 2, 50 + ((int)
					density * 230), (applet.width - 220) / 2, 60 * (int) density, "Save Settings");
//			Log.d(TAG, "Resolution Settings selected");
			apContainer.addWidget(setResWText);
			apContainer.addWidget((setResHText));
			apContainer.addWidget(setFrameRateText);
			apContainer.addWidget(setCalcPeriodText);

			apContainer.addWidget(setResW);
			apContainer.addWidget(setResH);
			apContainer.addWidget(setFrameRate);
			apContainer.addWidget(setCalcPeriod);
			apContainer.addWidget(setNormalize);
			apContainer.addWidget(cancel);
			cancel.getView().setBackgroundColor(0);
			apContainer.addWidget(setResolution);

			setResolution.addOnClickWidgetListener(new OnClickWidgetListener() {
				@Override
				public void onClickWidget(APWidget apWidget) {
					// some values may be set at runtime but they must be > 0
					int tResW = parseInt(setResW.getText());
					int tResH = parseInt(setResH.getText());
					int tCalcsPerPeriod = parseInt(setCalcPeriod.getText());

					if (tResW > 0) {
						resW = tResW;
						pxWidth = applet.width / resW;
					}
					if (tResH > 0) {
						resH = tResH;
						pxHeight = applet.height / resH;
					}
					if (tCalcsPerPeriod > 0) calcsPerPeriod = tCalcsPerPeriod;

					int oldFramerate = framerate;
					framerate = parseInt(setFrameRate.getText());
					normalize = setNormalize.isChecked();

					if (framerate > 0 && tResW > 0 && tResH > 0 && tCalcsPerPeriod > 0) {
						boolean querySuccess = VideOSCDB.updateResolutionSettings(db);
						if (querySuccess) {
							apContainer.removeWidget(setResWText);
							apContainer.removeWidget(setResHText);
							apContainer.removeWidget(setFrameRateText);
							apContainer.removeWidget(setCalcPeriodText);

							apContainer.removeWidget(setResW);
							apContainer.removeWidget(setResH);
							apContainer.removeWidget(setFrameRate);
							apContainer.removeWidget(setCalcPeriod);
							apContainer.removeWidget(setNormalize);
							apContainer.removeWidget(setResolution);
							apContainer.removeWidget(cancel);
							curOptions = "";

							if (oldFramerate != framerate) {
								KetaiAlertDialog.popup(applet, "Framerate set to " + framerate + " fps.", "Setting " +
										"the " +
										"framerate requires an " +
										"application " +
										"restart!");
							}
						} else {
							KetaiAlertDialog.popup(applet, "SQL Error", "Updating resolution settings " +
									"failed.");
						}
					} else {
						KetaiAlertDialog.popup(applet, "invalid values set", "The values for horizontal / " +
								"vertical resolution, framerate and calculation period must be greater " +
								"than 0.");
					}
				}
			});
			cancel.addOnClickWidgetListener(new OnClickWidgetListener() {
				@Override
				public void onClickWidget(APWidget apWidget) {
					apContainer.removeWidget(setResWText);
					apContainer.removeWidget(setResHText);
					apContainer.removeWidget(setFrameRateText);
					apContainer.removeWidget(setCalcPeriodText);

					apContainer.removeWidget(setResW);
					apContainer.removeWidget(setResH);
					apContainer.removeWidget(setFrameRate);
					apContainer.removeWidget(setCalcPeriod);
					apContainer.removeWidget(setNormalize);
					apContainer.removeWidget(setResolution);
					apContainer.removeWidget(cancel);
					curOptions = "";
				}
			});
			curOptions = "resolution";
		} else if (select.equals("About VideOSC")) {
			final APText about = new APText(50, 50, applet.width - 230, (int) (applet.height * 4.2));
			about.setTextSize(16);
			about.setText("VideOSC is an experimental OSC controller, using the color " +
					"information retrieved from the video stream of the inbuilt backside " +
					"camera of an Android based smartphone. VideOSC has been created " +
					"using the following freely " +
					"available libraries:\n\n\nProcessing\n\nProcessing is a Java based " +
					"programming" +
					" environment for creative expression for desktop computers and hand-held " +
					"devices. VideOSC uses its core library (processing.core).\n\n" +
					"Processing is Open Source Software. The PDE (Processing Development " +
					"Environment) is released under the\nGNU GPL (General Public License). The " +
					"export libraries (processing.core) are released under the GNU LGPL (Lesser " +
					"General Public License).\n\nMore information can be found " +
					"at\nhttp://processing.org\n\n\nOscP5 and NetP5\n\nNetP5 is a utility that helps " +
					"with network communication. OscP5 is an implementation of the OSC protocol " +
					"(Open Sound Control) that allows sending control messages between IP based " +
					"devices over the network (cable or wireless)\n\nBoth, OscP5 and NetP5, are " +
					"Open Source licensed under the\nGNU LGPL\n\nMore information about OscP5" +
					" can be found at\nhttp://www.sojamo.de/libraries/oscp5/\n\n" +
					"Documentation for NetP5:\nhttp://www.sojamo.de/libraries/oscp5/reference/netP5/package-summary.html\n\n\nKetai " +
					"Library\n\nKetai Library is good at everything an Android device does, and the desktop " +
					"doesn’t. It’s a extensive library that gives you straight-forward " +
					"access to sensors, cameras, and networking hardware.\n" +
					"\nKetai Library is Open Source licensed under the GNU LGPL\n\nMore " +
					"information about Ketai Library can be found at\nhttp://ketai" +
					".org/\n\n\nAPWidgets\n\nAPWidgets is a library that allows the creation of " +
					"dialogs with textfields buttons and other elements " +
					"(e.g. used in the preferences dialogs)\n\nAPWidgets " +
					"are Open Source licensed under the\nGNU General Public License\n\nMore " +
					"information about APWidgets can be found at\nhttps://code.google" +
					".com/archive/p/apwidgets/\n\n\nMore information about VideOSC can be " +
					"found at\nhttp://videosc.net\n\nThe full application source code can be " +
					"downloaded from\nhttps://github.com/nuss/VideOSC\n\n" +
					"VideOSC is Open Source licensed under the\nGNU" +
					" General Public License.\n\n(c) Stefan Nussbaumer 2014-" + Calendar
					.getInstance().get(Calendar.YEAR));
			apContainer.addWidget(about);
			about.setTextCentered();

			final APButton cancel = new APButton((applet.width - 220) / 4 + 50, about.getHeight()
					- 50 * (int) density, (applet.width - 220) / 2, 50 * (int) density, "Close");
			apContainer.addWidget(cancel);

			cancel.addOnClickWidgetListener(new OnClickWidgetListener() {
				@Override
				public void onClickWidget(APWidget apWidget) {
					apContainer.removeWidget(about);
					apContainer.removeWidget(cancel);
					curOptions = "";
				}
			});
			curOptions = "libs";
		} else {
			curOptions = "";
		}
	}

	static void darkenBackground(PApplet applet) {
		if (curOptions.length() > 0) {
			applet.fill(0, 153);
			applet.rect(0, 0, applet.width, applet.height);
		}
	}

}