package net.videosc;

import net.videosc.APWidgetExtend.APText;

import java.util.Calendar;

import apwidgets.APButton;
import apwidgets.APCheckBox;
import apwidgets.APEditText;
import apwidgets.APWidget;
import apwidgets.APWidgetContainer;
import apwidgets.OnClickWidgetListener;
import ketai.data.KetaiSQLite;
import ketai.net.KetaiNet;
import ketai.sensors.KetaiLocation;
import ketai.ui.KetaiAlertDialog;
import netP5.NetAddress;
import processing.core.PApplet;

/**
 * Package: net.videosc
 * Created by Stefan Nussbaumer on Okt 05, 11:51.
 */

public class VideOSCPreferences extends VideOSC {
	private final static String TAG = "VideOSCPreferences";

	static void createPreferences(final PApplet applet, String select, final KetaiSQLite db) {
		final APWidgetContainer apContainer = new APWidgetContainer(applet);

		if (select.equals("Network Settings")) {
			final APText ipText = new APText((int) VideOSCUI.dc(50), (int) VideOSCUI.dc(50),
					(applet.width - (int) VideOSCUI.dc(100)) / 2 - (int) VideOSCUI.dc(60), (int) VideOSCUI.dc(50));
			ipText.setText("send OSC to IP:");
			ipText.setTextSize((int) VideOSCUI.dc(30));
			final APEditText ipField = new APEditText((int) VideOSCUI.dc(50), (int) VideOSCUI.dc(80), (applet.width -
					(int) VideOSCUI.dc(100)) / 2 - (int) VideOSCUI.dc(60), (int) VideOSCUI.dc(150));
			ipField.setInputType(1);
			ipField.setText(sendAddr);

			final APText portText = new APText((applet.width - (int) VideOSCUI.dc(95)) / 2 - (int) VideOSCUI.dc(10),
					(int) VideOSCUI.dc(50), (applet.width - (int) VideOSCUI.dc(95)) / 2 - (int) VideOSCUI.dc(70),
					(int) VideOSCUI.dc(50));
			portText.setText("send OSC to port:");
			portText.setTextSize((int) VideOSCUI.dc(30));
			final APEditText portField = new APEditText((applet.width - (int) VideOSCUI.dc(95)) / 2 - (int) VideOSCUI.dc(10),
					(int) VideOSCUI.dc(80), (applet.width - (int) VideOSCUI.dc(95)) / 2 - (int) VideOSCUI.dc(70),
					(int) VideOSCUI.dc(150));
			portField.setInputType(2);
			portField.setText(str(broadcastPort));

			final APText deviceIPText = new APText((int) VideOSCUI.dc(50), (int) VideOSCUI.dc(250),
					(applet.width - (int) VideOSCUI.dc(100)) / 2 - (int) VideOSCUI.dc(60),
					(int) VideOSCUI.dc(50));
			deviceIPText.setText("device IP address:");
			deviceIPText.setTextSize((int) VideOSCUI.dc(30));
			final APText deviceIP = new APText((int) VideOSCUI.dc(70), (int) VideOSCUI.dc(320),
					(applet.width - (int) VideOSCUI.dc(95)) / 2 - (int) VideOSCUI.dc(85),
					(int) VideOSCUI.dc(100));
			deviceIP.setTextSize((int) VideOSCUI.dc(40));
			deviceIP.setText(KetaiNet.getIP());

			final APText receivePortText = new APText((applet.width - (int) VideOSCUI.dc(95)) / 2 - (int) VideOSCUI.dc(10),
					(int) VideOSCUI.dc(250), (applet.width - (int) VideOSCUI.dc(100)) / 2 - (int) VideOSCUI.dc(60),
					(int) VideOSCUI.dc(50));
			receivePortText.setText("receive OSC-feedback on port:");
			receivePortText.setTextSize((int) VideOSCUI.dc(30));
			final APEditText receivePortField = new APEditText((applet.width - (int) VideOSCUI.dc(95)) / 2 - (int) VideOSCUI.dc(10),
					(int) VideOSCUI.dc(280), (applet.width - (int) VideOSCUI.dc(95)) / 2 - (int) VideOSCUI.dc(70),
					(int) VideOSCUI.dc(150));
			receivePortField.setInputType(2);
			receivePortField.setText(str(listenPort));

			final APText cmdNameText = new APText((int) VideOSCUI.dc(50), (int) VideOSCUI.dc(450), applet.width -
					(int) VideOSCUI.dc(320), (int) VideOSCUI.dc(50));
			cmdNameText.setText("command name:");
			cmdNameText.setTextSize((int) VideOSCUI.dc(30));
			final APText cmdNameTextPre = new APText((int) VideOSCUI.dc(50), (int) VideOSCUI.dc(530),
					(int) VideOSCUI.dc(20), (int) VideOSCUI.dc(50));
			cmdNameTextPre.setTextSize((int) VideOSCUI.dc(30));
			cmdNameTextPre.setText("/");
			final APEditText cmdNameSpace = new APEditText((int) VideOSCUI.dc(70), (int) VideOSCUI.dc(480),
					(applet.width - (int) VideOSCUI.dc(95)) / 2 - (int) VideOSCUI.dc(85), (int) VideOSCUI.dc(150));
			cmdNameSpace.setInputType(1);
			cmdNameSpace.setText(rootCmd);
			final APText cmdNameTextPost = new APText((applet.width - (int) VideOSCUI.dc(95)) / 2 - (int) VideOSCUI.dc(10),
					(int) VideOSCUI.dc(530), (int) VideOSCUI.dc(500), (int) VideOSCUI.dc(50));
			cmdNameTextPost.setTextSize((int) VideOSCUI.dc(30));
			cmdNameTextPost.setText("/<colorN>");

			final APButton cancel = new APButton((int) VideOSCUI.dc(50), (int) VideOSCUI.dc(650),
					(applet.width - (int) VideOSCUI.dc(220)) / 2, (int) VideOSCUI.dc(50) * 3, "Cancel");
			final APButton setNetwork = new APButton((int) VideOSCUI.dc(50) + (applet.width - (int) VideOSCUI.dc(220)) / 2,
					(int) VideOSCUI.dc(650), (applet.width - (int) VideOSCUI.dc(220)) / 2, (int) VideOSCUI.dc(50) * 3, "Save Settings");

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
							apContainer.removeWidget(portField);
							apContainer.removeWidget(receivePortField);
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
					apContainer.removeWidget(portField);
					apContainer.removeWidget(receivePortField);
					apContainer.removeWidget(setNetwork);
					apContainer.removeWidget(cmdNameSpace);
					apContainer.removeWidget(cancel);
					curOptions = "";
				}
			});
			curOptions = "network";
		} else if (select.equals("Resolution Settings")) {
			final APText setResWText = new APText(50, 50, (applet.width - 100) / 2 - 60, 50 * (int)
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
					density * 180), "normalize output (0.0-1.0, does not apply to sensor values)");
			setNormalize.setSize((applet.width - 220) / 2, 120);
			if (normalize)
				setNormalize.setChecked(true);
			else setNormalize.setChecked(false);

			final APCheckBox rememberSnapshotOnClose = new APCheckBox((applet.width - 95) / 2 - 10, 50 + ((int) density * 220), "remember activated/deactivated pixels on quit");
			rememberSnapshotOnClose.setSize((applet.width - 220) / 2, 120);
			if (saveSnapshotOnClose)
				rememberSnapshotOnClose.setChecked(true);
			else rememberSnapshotOnClose.setChecked(false);

			final APButton cancel = new APButton(50, 50 + ((int) density * 270), (applet.width -
					220) /
					2, 60 * (int) density, "Cancel");
			final APButton setResolution = new APButton(50 + (applet.width - 220) / 2, 50 + ((int)
					density * 270), (applet.width - 220) / 2, 60 * (int) density, "Save Settings");

			apContainer.addWidget(setResWText);
			apContainer.addWidget((setResHText));
			apContainer.addWidget(setFrameRateText);
			apContainer.addWidget(setCalcPeriodText);

			apContainer.addWidget(setResW);
			apContainer.addWidget(setResH);
			apContainer.addWidget(setFrameRate);
			apContainer.addWidget(setCalcPeriod);
			apContainer.addWidget(setNormalize);
			apContainer.addWidget(rememberSnapshotOnClose);
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
					saveSnapshotOnClose = rememberSnapshotOnClose.isChecked();

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
							apContainer.removeWidget(rememberSnapshotOnClose);
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
					apContainer.removeWidget(rememberSnapshotOnClose);
					apContainer.removeWidget(setResolution);
					apContainer.removeWidget(cancel);
					curOptions = "";
				}
			});
			curOptions = "resolution";
		} else if (select.equals("Sensors")) {
			int nextYPos = 50;
			final APCheckBox useOri, useAcc, useLinAcc, useMag, useGrav, useProx, useLight, usePress, useTemp, useHum, useLoc;

			useOri = new APCheckBox(50, nextYPos, "orientation sensor: rotation in degrees - '/" + VideOSC.rootCmd + "/ori', x, y, z, timestamp, accuracy");
			useOri.setTextSize(16);
			useOri.setSize(applet.width - 220, (int) (45 * density));
			if (!VideOSCSensors.sensors.isOrientationAvailable())
				useOri.setText(useOri.getText() + " (not available)");
			useOri.setChecked(VideOSCSensors.useOri);
			useOri.addOnClickWidgetListener(new OnClickWidgetListener() {
				@Override
				public void onClickWidget(APWidget apWidget) {
					if (!VideOSCSensors.sensors.isOrientationAvailable())
						useOri.setChecked(false);
				}
			});
			apContainer.addWidget(useOri);
			nextYPos += 50 * density;

			useAcc = new APCheckBox(50, nextYPos, "accelerometer: force in m/s^2 - '/" + VideOSC.rootCmd + "/acc', x, y, z, timestamp, accuracy");
			useAcc.setTextSize(16);
			useAcc.setSize(applet.width - 220, (int) (45 * density));
			if (!VideOSCSensors.sensors.isAccelerometerAvailable())
				useAcc.setText(useAcc.getText() + " (not available)");
			useAcc.setChecked(VideOSCSensors.useAcc);
			useAcc.addOnClickWidgetListener(new OnClickWidgetListener() {
				@Override
				public void onClickWidget(APWidget apWidget) {
					if (!VideOSCSensors.sensors.isAccelerometerAvailable())
						useAcc.setChecked(false);
				}
			});
			apContainer.addWidget(useAcc);
			nextYPos += 50 * density;

			useLinAcc = new APCheckBox(50, nextYPos, "linear acceleration sensor: m/s^2 minus gravity - '/" + VideOSC.rootCmd + "/lin_acc', x, y, z, timestamp, accuracy");
			useLinAcc.setTextSize(16);
			useLinAcc.setSize(applet.width - 220, (int) (45 * density));
			if (!VideOSCSensors.sensors.isLinearAccelerationAvailable())
				useLinAcc.setText(useLinAcc.getText() + " (not available)");
			useLinAcc.setChecked(VideOSCSensors.useLinAcc);
			useLinAcc.addOnClickWidgetListener(new OnClickWidgetListener() {
				@Override
				public void onClickWidget(APWidget apWidget) {
					if (!VideOSCSensors.sensors.isLinearAccelerationAvailable())
						useLinAcc.setChecked(false);
				}
			});
			apContainer.addWidget(useLinAcc);
			nextYPos += 50 * density;

			useMag = new APCheckBox(50, nextYPos, "magnetic field sensor: geomagnetic field in uT - '/" + VideOSC.rootCmd + "/mag', x, y, z, timestamp, accuracy");
			useMag.setTextSize(16);
			useMag.setSize(applet.width - 220, (int) (45 * density));
			if (!VideOSCSensors.sensors.isMagenticFieldAvailable())
				useMag.setText(useMag.getText() + " (not available)");
			useMag.setChecked(VideOSCSensors.useMag);
			useMag.addOnClickWidgetListener(new OnClickWidgetListener() {
				@Override
				public void onClickWidget(APWidget apWidget) {
					if (!VideOSCSensors.sensors.isMagenticFieldAvailable())
						useMag.setChecked(false);
				}
			});
			apContainer.addWidget(useMag);
			nextYPos += 50 * density;

			useGrav = new APCheckBox(50, nextYPos, "gravity sensor: force of gravity in m/s^2 - '/" + VideOSC.rootCmd + "/grav', x, y, z, timestamp, accuracy");
			useGrav.setTextSize(16);
			useGrav.setSize(applet.width - 220, (int) (45 * density));
			if ((!VideOSCSensors.sensors.isGyroscopeAvailable() && !VideOSCSensors.sensors.isAccelerometerAvailable() && !VideOSCSensors.sensors.isMagenticFieldAvailable()) ||
					(VideOSCSensors.sensors.isGyroscopeAvailable() && !VideOSCSensors.sensors.isAccelerometerAvailable()))
				useGrav.setText(useGrav.getText() + " (not available)");
			useGrav.setChecked(VideOSCSensors.useGrav);
			useGrav.addOnClickWidgetListener(new OnClickWidgetListener() {
				@Override
				public void onClickWidget(APWidget apWidget) {
					if (
							(!VideOSCSensors.sensors.isGyroscopeAvailable() && !VideOSCSensors.sensors.isAccelerometerAvailable() && !VideOSCSensors.sensors.isMagenticFieldAvailable()) ||
									(VideOSCSensors.sensors.isGyroscopeAvailable() && !VideOSCSensors.sensors.isAccelerometerAvailable())
							)
						useGrav.setChecked(false);
				}
			});
			apContainer.addWidget(useGrav);
			nextYPos += 50 * density;

			useProx = new APCheckBox(50, nextYPos, "proximity sensor: distance from sensor, typically 0, 1 - '/" + VideOSC.rootCmd + "/prox', distance, timestamp, accuracy");
			useProx.setTextSize(16);
			useProx.setSize(applet.width - 220, (int) (45 * density));
			if (!VideOSCSensors.sensors.isProximityAvailable())
				useProx.setText(useProx.getText() + " (not available)");
			useProx.setChecked(VideOSCSensors.useProx);
			useProx.addOnClickWidgetListener(new OnClickWidgetListener() {
				@Override
				public void onClickWidget(APWidget apWidget) {
					if (!VideOSCSensors.sensors.isProximityAvailable())
						useProx.setChecked(false);
				}
			});
			apContainer.addWidget(useProx);
			nextYPos += 50 * density;

			useLight = new APCheckBox(50, nextYPos, "light sensor: illumination from sensor in lx - '/" + VideOSC.rootCmd + "/light', illumination, timestamp, accuracy");
			useLight.setTextSize(16);
			useLight.setSize(applet.width - 220, (int) (45 * density));
			if (!VideOSCSensors.sensors.isLightAvailable())
				useLight.setText(useLight.getText() + " (not available)");
			useLight.setChecked(VideOSCSensors.useLight);
			useLight.addOnClickWidgetListener(new OnClickWidgetListener() {
				@Override
				public void onClickWidget(APWidget apWidget) {
					if (!VideOSCSensors.sensors.isLightAvailable())
						useLight.setChecked(false);
				}
			});
			apContainer.addWidget(useLight);
			nextYPos += 50 * density;

			usePress = new APCheckBox(50, nextYPos, "air pressure sensor: ambient pressure in hPa or mbar - '/" + VideOSC.rootCmd + "/press', pressure, timestamp, accuracy");
			usePress.setTextSize(16);
			usePress.setSize(applet.width - 220, (int) (45 * density));
			if (!VideOSCSensors.sensors.isPressureAvailable())
				usePress.setText(usePress.getText() + " (not available)");
			usePress.setChecked(VideOSCSensors.usePress);
			usePress.addOnClickWidgetListener(new OnClickWidgetListener() {
				@Override
				public void onClickWidget(APWidget apWidget) {
					if (!VideOSCSensors.sensors.isPressureAvailable())
						usePress.setChecked(false);
				}
			});
			apContainer.addWidget(usePress);
			nextYPos += 50 * density;

			useTemp = new APCheckBox(50, nextYPos, "temperature sensor: temperature in degrees in °C - '/" + VideOSC.rootCmd + "/temp', temperature");
			useTemp.setTextSize(16);
			useTemp.setSize(applet.width - 220, (int) (45 * density));
			if (!VideOSCSensors.sensors.isAmbientTemperatureAvailable())
				useTemp.setText(useTemp.getText() + " (not available)");
			useTemp.setChecked(VideOSCSensors.useTemp);
			useTemp.addOnClickWidgetListener(new OnClickWidgetListener() {
				@Override
				public void onClickWidget(APWidget apWidget) {
					if (!VideOSCSensors.sensors.isAmbientTemperatureAvailable())
						useTemp.setChecked(false);
				}
			});
			apContainer.addWidget(useTemp);
			nextYPos += 50 * density;

			useHum = new APCheckBox(50, nextYPos, "humidity sensor: ambient humidity in % - '/" + VideOSC.rootCmd + "/hum', humidity");
			useHum.setTextSize(16);
			useHum.setSize(applet.width - 220, (int) (45 * density));
			if (!VideOSCSensors.sensors.isRelativeHumidityAvailable())
				useHum.setText(useHum.getText() + " (not available)");
			useHum.setChecked(VideOSCSensors.useHum);
			useHum.addOnClickWidgetListener(new OnClickWidgetListener() {
				@Override
				public void onClickWidget(APWidget apWidget) {
					if (!VideOSCSensors.sensors.isRelativeHumidityAvailable())
						useHum.setChecked(false);
				}
			});
			apContainer.addWidget(useHum);
			nextYPos += 50 * density;

			useLoc = new APCheckBox(50, nextYPos, "geo location: '/" + VideOSC.rootCmd + "/loc', latitude, longitude, altitude");
			useLoc.setTextSize(16);
			useLoc.setSize(applet.width - 220, (int) (45 * density));
			useLoc.setChecked(VideOSCSensors.useLoc);

			apContainer.addWidget(useLoc);
			nextYPos += 50 * density;
			final APButton cancel = new APButton(50, 50 + nextYPos, (applet.width -
					220) /
					2, 60 * (int) density, "Cancel");
			final APButton save = new APButton(50 + (applet.width - 220) / 2, 50 + nextYPos, (applet.width - 220) / 2, 60 * (int) density, "Save Settings");
			apContainer.addWidget(cancel);
			cancel.getView().setBackgroundColor(0);
			apContainer.addWidget(save);

			save.addOnClickWidgetListener(new OnClickWidgetListener() {
				@Override
				public void onClickWidget(APWidget apWidget) {
					VideOSCSensors.useOri = useOri.isChecked() && VideOSCSensors.sensors.isOrientationAvailable();
					if (!useOri.isChecked()) VideOSCSensors.sensorsInUse.remove("ori");
					else
						VideOSCSensors.sensorsInUse.put("ori", "orientation sensors - no values yet");
					VideOSCSensors.useAcc = useAcc.isChecked() && VideOSCSensors.sensors.isAccelerometerAvailable();
					if (!useAcc.isChecked()) VideOSCSensors.sensorsInUse.remove("acc");
					else VideOSCSensors.sensorsInUse.put("acc", "accelerometer - no values yet");
					VideOSCSensors.useMag = useMag.isChecked() && VideOSCSensors.sensors.isMagenticFieldAvailable();
					if (!useMag.isChecked()) VideOSCSensors.sensorsInUse.remove("mag");
					else
						VideOSCSensors.sensorsInUse.put("mag", "magnetic field sensor - no values yet");
					VideOSCSensors.useGrav = useGrav.isChecked() && (
							(!VideOSCSensors.sensors.isGyroscopeAvailable() && VideOSCSensors.sensors.isAccelerometerAvailable() && VideOSCSensors.sensors.isMagenticFieldAvailable()) ||
									(VideOSCSensors.sensors.isGyroscopeAvailable() && VideOSCSensors.sensors.isAccelerometerAvailable()));
					if (!useGrav.isChecked()) VideOSCSensors.sensorsInUse.remove("grav");
					else VideOSCSensors.sensorsInUse.put("grav", "gravity sensor - no values yet");
					VideOSCSensors.useProx = useProx.isChecked() && VideOSCSensors.sensors.isProximityAvailable();
					if (!useProx.isChecked()) VideOSCSensors.sensorsInUse.remove("prox");
					else
						VideOSCSensors.sensorsInUse.put("prox", "proximity sensor - no values yet");
					VideOSCSensors.useLight = useLight.isChecked() && VideOSCSensors.sensors.isLightAvailable();
					if (!useLight.isChecked()) VideOSCSensors.sensorsInUse.remove("light");
					else VideOSCSensors.sensorsInUse.put("light", "light sensor - no values yet");
					VideOSCSensors.usePress = usePress.isChecked() && VideOSCSensors.sensors.isPressureAvailable();
					if (!usePress.isChecked()) VideOSCSensors.sensorsInUse.remove("press");
					else
						VideOSCSensors.sensorsInUse.put("press", "air pressure sensor - no values yet");
					VideOSCSensors.useTemp = useTemp.isChecked() && VideOSCSensors.sensors.isAmbientTemperatureAvailable();
					if (!useTemp.isChecked()) VideOSCSensors.sensorsInUse.remove("temp");
					else
						VideOSCSensors.sensorsInUse.put("temp", "temperature sensor - no values yet");
					VideOSCSensors.useLinAcc = useLinAcc.isChecked() && VideOSCSensors.sensors.isLinearAccelerationAvailable();
					if (!useLinAcc.isChecked()) VideOSCSensors.sensorsInUse.remove("linAcc");
					else
						VideOSCSensors.sensorsInUse.put("linAcc", "linear acceleration sensor - no values yet");
					VideOSCSensors.useHum = useHum.isChecked() && VideOSCSensors.sensors.isRelativeHumidityAvailable();
					if (!useHum.isChecked()) VideOSCSensors.sensorsInUse.remove("hum");
					else VideOSCSensors.sensorsInUse.put("hum", "humidity sensor - no values yet");
					VideOSCSensors.useLoc = useLoc.isChecked();
					if (useLoc.isChecked()) {
						if (VideOSCSensors.location.isStarted()) VideOSCSensors.location.stop();
						VideOSCSensors.location = new KetaiLocation(applet);
						VideOSCSensors.location.setUpdateRate(2000, 1);
						VideOSCSensors.provider = VideOSCSensors.location.getProvider();
						VideOSCSensors.location.start();
						VideOSCSensors.sensorsInUse.put("loc", "location (provider: " + VideOSCSensors.provider + ") - no values yet");
						if (!VideOSCSensors.provider.equals("gps") && !VideOSCSensors.provider.equals("none"))
							KetaiAlertDialog.popup(applet, "Geo Location Notice", "Your geo location provider is currently set to '" + VideOSCSensors.provider + "'. Enable GPS for your device to get most accurate results.");
						else if (VideOSCSensors.provider.equals("none"))
							KetaiAlertDialog.popup(applet, "Geo Location Notice", "Your geo location provider is currently set to 'none'. Please activate GPS for your device and restart the app.");
					} else {
						VideOSCSensors.sensorsInUse.remove("loc");
					}

					boolean querySuccess = VideOSCDB.updateSensorsSettings(applet, db);
					if (querySuccess) {
						apContainer.removeWidget(useOri);
						apContainer.removeWidget(useAcc);
						apContainer.removeWidget(useMag);
						apContainer.removeWidget(useGrav);
						apContainer.removeWidget(useProx);
						apContainer.removeWidget(useLight);
						apContainer.removeWidget(usePress);
						apContainer.removeWidget(useTemp);
						apContainer.removeWidget(useLinAcc);
						apContainer.removeWidget(useHum);
						apContainer.removeWidget(useLoc);
						apContainer.removeWidget(save);
						apContainer.removeWidget(cancel);
						curOptions = "";
					}
				}
			});

			cancel.addOnClickWidgetListener(new OnClickWidgetListener() {
				@Override
				public void onClickWidget(APWidget apWidget) {
					apContainer.removeWidget(useOri);
					apContainer.removeWidget(useAcc);
					apContainer.removeWidget(useLinAcc);
					apContainer.removeWidget(useMag);
					apContainer.removeWidget(useGrav);
					apContainer.removeWidget(useProx);
					apContainer.removeWidget(useLight);
					apContainer.removeWidget(usePress);
					apContainer.removeWidget(useTemp);
					apContainer.removeWidget(useHum);
					apContainer.removeWidget(useLoc);
					apContainer.removeWidget(save);
					apContainer.removeWidget(cancel);
					curOptions = "";
				}
			});

			curOptions = "sensors";
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
		if (curOptions.length() > 0 || sensorsPrinting) {
			applet.fill(0, 204);
			applet.rect(0, 0, applet.width, applet.height);
		}
	}

}