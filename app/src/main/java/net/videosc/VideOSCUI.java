package net.videosc;

import android.util.Log;

import ketai.data.KetaiSQLite;
import ketai.ui.KetaiList;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * Package: net.videosc
 * Created by Stefan Nussbaumer on Okt 05 2016, 12:01.
 */
public class VideOSCUI extends VideOSC {
	private static String TAG = "VideOSCUI";

	// vertical right toolbar
	private static PImage startBut;
	private static PImage stopBut;
	private static PImage lightBut;
	private static PImage interaction;
	private static PImage interactionplus;
	private static PImage infoBut;
	private static PImage settingsBut;

	// rgb-mode buttons
	private static PImage rgbBut;
	private static PImage rBut;
	private static PImage gBut;
	private static PImage bBut;
	private static PImage rgbButNeg;
	private static PImage rButNeg;
	private static PImage gButNeg;
	private static PImage bButNeg;

	// private static int rgbButInMenuX;
	private static int rgbButInMenuY;

	// toolbar buttons at the right top of the screen
	private static PImage ying;
	private static PImage yang;
	private static PImage erase;
	private static PImage eraseActive;
	private static PImage snapshot;
	private static PImage snapshotClick;
	private static PImage snapshotSelect;

	// buttons indicating if feedback messages are activated or not
	private static PImage fbOn;
	private static PImage fbOff;

	// button to show/hide UI elements
	private static PImage showMenu;
	private static PImage hideMenu;

	// list active sensors
	private static PImage sensors;

	// highlight snapshot saving button for a short moment
	private static boolean snapshotProcessing = false;

	// deactivate UI elements if screen	is overlayed by some selection list (preferences,
	// snapshots, etc.)
	static boolean selectionListActive = false;

	static int uiXright;
	static int uiYtop;
	static int uiYbottom;

	static void loadUIImages(PApplet applet) {
		startBut = applet.loadImage("start.png");
		stopBut = applet.loadImage("stop.png");
		lightBut = applet.loadImage("light.png");
		infoBut = applet.loadImage("i.png");
		interaction = applet.loadImage("interaction.png");
		interactionplus = applet.loadImage("interactionplus.png");

		rgbBut = applet.loadImage("rgb.png");
		rBut = applet.loadImage("r.png");
		gBut = applet.loadImage("g.png");
		bBut = applet.loadImage("b.png");

		rgbButNeg = applet.loadImage("rgb_neg.png");
		rButNeg = applet.loadImage("r_neg.png");
		gButNeg = applet.loadImage("g_neg.png");
		bButNeg = applet.loadImage("b_neg.png");

		ying = applet.loadImage("ying.png");
		yang = applet.loadImage("yang.png");
		erase = applet.loadImage("erase.png");
		eraseActive = applet.loadImage("erase_a.png");
		snapshot = applet.loadImage("snapshot.png");
		snapshotClick = applet.loadImage("snapshot_click.png");
		snapshotSelect = applet.loadImage("snapshot_select.png");

		fbOn = applet.loadImage("fb_on.png");
		fbOff = applet.loadImage("fb_off.png");

		hideMenu = applet.loadImage("hide_menu.png");
		showMenu = applet.loadImage("show_menu.png");

		sensors = applet.loadImage("sensors.png");

		settingsBut = applet.loadImage("settings.png");
	}

	static void drawRGBUI(PApplet applet) {
		PImage localRGB, localR, localG, localB;
		int rgbButInMenuX;

		applet.pushStyle();
		applet.stroke(0, 0);
		applet.fill(0, 153);
		applet.rect(uiXright, 0, 180, applet.height);
		if (play) {
			applet.image(stopBut, uiXright + (stopBut.width / 2) + 20, applet.height / 12);
		} else {
			applet.image(startBut, uiXright + (startBut.width / 2) + 20, applet.height / 12);
		}
		applet.image(lightBut, uiXright + (lightBut.width / 2) + 20, applet.height / 6 + applet.height /
				12);

		if (negative) {
			localRGB = rgbButNeg;
			localR = rButNeg;
			localG = gButNeg;
			localB = bButNeg;
		} else {
			localRGB = rgbBut;
			localR = rBut;
			localG = gBut;
			localB = bBut;
		}

		rgbButInMenuX = uiXright + (rgbBut.width / 2) + 20;
		rgbButInMenuY = applet.height / 6 * 2 + applet.height / 12;
		if (rgbMode.equals(RGBModes.RGB)) {
			applet.image(localRGB, rgbButInMenuX, rgbButInMenuY);
		} else if (rgbMode.equals(RGBModes.R)) {
			applet.image(localR, rgbButInMenuX, rgbButInMenuY);
		} else if (rgbMode.equals(RGBModes.G)) {
			applet.image(localG, rgbButInMenuX, rgbButInMenuY);
		} else if (rgbMode.equals(RGBModes.B)) {
			applet.image(localB, rgbButInMenuX, rgbButInMenuY);
		}
		if (mode.equals(InteractionModes.BASIC))
			applet.image(interaction, uiXright + (infoBut.width / 2) + 20, applet.height / 6 * 3 +
					applet.height / 12);
		else if (mode.equals(InteractionModes.SINGLE_PIXEL))
			applet.image(interactionplus, uiXright + (infoBut.width / 2) + 20, applet.height / 6 * 3 +
					applet.height / 12);
		applet.image(infoBut, uiXright + (infoBut.width / 2) + 20, applet.height / 6 * 4 + applet.height
				/ 12);
		applet.image(settingsBut, uiXright + (settingsBut.width / 2) + 20, applet.height / 6 * 5
				+ applet.height / 12);
	}

	static void processUIClicks(PApplet applet, int x, int y, KetaiSQLite db) {
		String[] snapshotsList;
		KetaiList snapshotsSelect;
		KetaiList preferencesList;

		if (!selectionListActive && !sensorsPrinting) {
			// only allow interaction if screen is not overlayed by some selection list
			if (x > 100 && y > 100 && x < applet.width - 130 && !displayRGBselector && curOptions.equals
					("")) {
				if (!cam.isStarted()) cam.start();
			} else if (y >= rgbButInMenuY && y < rgbButInMenuY + 140
					&& displayRGBselector && curOptions.equals("")) {
				if (y >= rgbButInMenuY && y < rgbButInMenuY + 140) {
					if (x > applet.width - 780 && x <= applet.width - 640) {
						rgbMode = RGBModes.RGB;
						negative = false;
						displayRGBselector = false;
					} else if (x > applet.width - 640 && x <= applet.width - 500) {
						rgbMode = RGBModes.RGB;
						negative = true;
						displayRGBselector = false;
					} else if (x > applet.width - 500 && x <= applet.width - 360) {
						rgbMode = RGBModes.R;
						displayRGBselector = false;
					} else if (x > applet.width - 360 && x <= applet.width - 220) {
						rgbMode = RGBModes.G;
						displayRGBselector = false;
					} else if (x > applet.width - 220 && x <= applet.width - 180) {
						rgbMode = RGBModes.B;
						displayRGBselector = false;
					}
					rCmds.clear();
					gCmds.clear();
					bCmds.clear();
				}
			} else if (x >= applet.width - 130) {
				if (!uiHidden) {
					if (y < applet.height / 6 && curOptions.equals("")) {
						play = !play;
					} else if (y >= applet.height / 6 && y < applet.height /
							6 * 2 && curOptions.equals("")) {
						if (cam.isFlashEnabled())
							cam.disableFlash();
						else
							cam.enableFlash();
					} else if (y >= applet.height / 6 * 2 && y < applet
							.height / 6 * 3 && curOptions.equals("")) {
						displayRGBselector = !displayRGBselector;
					} else if (y >= applet.height / 6 * 3 && y < applet
							.height / 6 * 4 && curOptions.equals("")) {
						if (mode.equals(InteractionModes.BASIC))
							mode = InteractionModes.SINGLE_PIXEL;
						else mode = InteractionModes.BASIC;
					} else if (y >= applet.height / 6 * 4 && y < applet
							.height / 6 * 5 && curOptions.equals("")) {
						displayFramerate = !displayFramerate;
					} else if (y >= applet.height / 6 * 5 && curOptions.equals("")) {
						preferencesList = new KetaiList(applet, optionsList);
						preferencesList.setAlpha(0.6f);
						selectionListActive = true;
						backKeyState = 1;
						// println("backKeyState: "+backKeyState);
					}
				}
			} else if (x <= 100 && y <= 100 && curOptions.equals("")) {
				showHide = true;
			} else if (x > 100 && x <= 350 && y <= 100 && curOptions.equals("")) {
//				Log.d(TAG, "clicked: sensors printed? " + sensorsPrinting + printSensors);
				printSensors = true;
				if (!sensorsPrinting && printSensors) {
					sensorsPrinting = VideOSCSensors.printSensors(applet, db);
				}
			}

			if (mode.equals(InteractionModes.SINGLE_PIXEL)) {
				if (y <= uiYtop + 50) {
					if (x <= applet.width - 190 && x >= applet.width - 290) {
						// swap currently blocked pixels
						gestureMode = GestureModes.SWAP;
					} else if (x <= applet.width - 340 && x >= applet.width - 440) {
						// reset currently blocked pixels
						gestureMode = GestureModes.ERASE;
					} else if (x <= applet.width - 490 && x >= applet.width - 590) {
						// add a new snapshot
						VideOSCDB.addSnapshot(applet, db);
						snapshotProcessing = true;
					} else if (x <= applet.width - 640 && x >= applet.width - 740) {
						// show list of selectable saved snapshots
						showSnapshots = true;
						snapshotsList = VideOSCDB.getSnapshotKeys(db);
						snapshotsList = append(snapshotsList, "Reset Snapshots");
						snapshotsSelect = new KetaiList(applet, snapshotsList);
						snapshotsSelect.setAlpha(0.6f);
						selectionListActive = true;
					}
				}
			}

			if (!rgbMode.equals(RGBModes.RGB) && x <= applet.width - 245 && x >=
					applet.width - 345 && y <= uiYtop + 50) {
				showFB = !showFB;
			}
		}
	}

	static void drawRGBModeSelector(PApplet applet) {
		PImage localR, localG, localB;
		applet.pushStyle();
		applet.stroke(0, 0);
		applet.fill(0, 153);
		applet.rect(applet.width - 760, rgbButInMenuY - 70, 620, 140);
		applet.image(rgbBut, applet.width - 740 + (rgbBut.width / 2), rgbButInMenuY, rgbBut.width,
				rgbBut.height);
		applet.image(rgbButNeg, applet.width - 620 + (rgbButNeg.width / 2), rgbButInMenuY,
				rgbButNeg.width, rgbButNeg.height);
		if (negative) {
			localR = rButNeg;
			localG = gButNeg;
			localB = bButNeg;
		} else {
			localR = rBut;
			localG = gBut;
			localB = bBut;
		}
		applet.image(localR, applet.width - 500 + (localR.width / 2), rgbButInMenuY, localR.width,
				localR.height);
		applet.image(localG, applet.width - 380 + (localG.width / 2), rgbButInMenuY, localG.width,
				localG.height);
		applet.image(localB, applet.width - 260 + (localB.width / 2), rgbButInMenuY, localB.width,
				localB.height);
	}

	static void drawTools(PApplet applet, KetaiSQLite db) {
		if (uiHidden)
			applet.image(showMenu, 70, 70, 62, 62);
		else
			applet.image(hideMenu, 70, 70, 62, 62);

//		Log.d(TAG, "number of active sensors: " + VideOSCDB.listSensorsInUse(db).size());
		if (VideOSCDB.listSensorsInUse(db).size() > 0)
			applet.image(sensors, 250, uiYtop - 10, 229, 62);

		if (!rgbMode.equals(RGBModes.RGB)) {
			if (showFB)
				applet.image(fbOff, applet.width - 345, uiYtop, 96, 50);
			else
				applet.image(fbOn, applet.width - 345, uiYtop, 96, 50);
		}

		if (mode.equals(InteractionModes.SINGLE_PIXEL)) {

			if (snapshotProcessing) {
				applet.image(snapshotClick, applet.width - 540, uiYtop, 150, 150);
				snapshotProcessing = false;
			} else {
				applet.image(snapshot, applet.width - 540, uiYtop, 100, 100);
			}

			if (numSnapshots > 0) {
				applet.image(snapshotSelect, applet.width - 690, uiYtop, 125, 96);
				applet.fill(255);
				applet.textAlign(CENTER);
				applet.textSize(35);
				applet.text((int) numSnapshots, applet.width - 657, uiYtop + 32);
				applet.textAlign(LEFT);
			}

			if (rgbMode.equals(RGBModes.RGB)) {
				if (gestureMode.equals(GestureModes.SWAP)) {
					applet.image(yang, applet.width - 240, uiYtop, 100, 100);
					applet.image(erase, applet.width - 390, uiYtop, 100, 100);
				} else {
					applet.image(ying, applet.width - 240, uiYtop, 100, 100);
					applet.image(eraseActive, applet.width - 390, uiYtop, 100, 100);
				}
			}
		}
	}

	static void printFramerate(PApplet applet, boolean print) {
		if (print) {
			applet.pushStyle();
			applet.stroke(0, 0);
			applet.fill(0, 153);
			applet.rect(30, uiYbottom, 470, 60);
			applet.textSize(40);
			applet.fill(255);
			applet.text("FPS: " + str((float) round(applet.frameRate * 10) / 10)
					+ ", calc.-period: " + str(calcsPerPeriod), 45, uiYbottom + 45);
		}
	}

	static void setShowHideMenus(PApplet applet) {
		if (uiHidden) {
			if (uiXright > applet.width - 130)
				uiXright -= 30;
			if (uiYtop < 80)
				uiYtop += 30;
			if (uiYbottom > applet.height - 90)
				uiYbottom -= 30;

			if (uiXright <= applet.width - 130 && uiYtop >= 80 && uiYbottom <= applet.height - 90) {
				uiHidden = false;
				showHide = false;
			}
		} else {
			if (uiXright < applet.width)
				uiXright += 30;
			if (uiYtop > -60)
				uiYtop -= 30;
			if (uiYbottom < applet.height + 50)
				uiYbottom += 30;

			if (uiXright >= applet.width && uiYtop <= -60 && uiYbottom >= applet.height + 50) {
				uiHidden = true;
				showHide = false;
			}
		}
	}

	static void processKetaiListClicks(PApplet applet, KetaiList klist, KetaiSQLite db) {
		String select = klist.getSelection();

		if (select.equals("Network Settings") || select.equals("Resolution Settings") || select.equals("Sensors") || select
				.equals("About VideOSC")) {
			VideOSCPreferences.createPreferences(applet, select, db);
		} else if (select.equals("Reset Snapshots")) {
			VideOSCDB.resetSnapshots(applet, db);
		} else {
			VideOSCDB.selectSnapshot(applet, select, db);
		}

		VideOSCUI.selectionListActive = false;
		klist.setAdapter(null);
	}

}

enum InteractionModes { BASIC, SINGLE_PIXEL }
enum GestureModes { SWAP, ERASE }
enum RGBModes { RGB, R, G, B }