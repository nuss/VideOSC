package net.videosc;

import android.hardware.Camera;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ketai.camera.KetaiCamera;
import ketai.data.KetaiSQLite;
import ketai.sensors.KetaiSensor;
import ketai.ui.KetaiAlertDialog;
import ketai.ui.KetaiList;
import netP5.NetAddress;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * Package: net.videosc
 * Created by Stefan Nussbaumer on Okt 05 2016, 12:02.
 */
public class VideOSC extends PApplet {
	private static String TAG = "VideOSC";

	static String curOptions = "";

	static PImage pImg;

	// Ketai Sensor Library for Android: http://KetaiProject.org

	static KetaiCamera cam;
	public static OscP5 oscP5;
	public static NetAddress broadcastLoc;
	static NetAddress feedbackAddr;

	// OSC messages to be sent to the remote client
	private static OscMessage oscR;
	private static OscMessage oscG;
	private static OscMessage oscB;

	// dimensions = width * height of the user defined resolution
	static int dimensions;
	static int resW = 6;
	static int resH = 4;
	static int pxWidth;
	static int pxHeight;
	static int listenPort = 32000;
	static int broadcastPort = 57120;

	static boolean play = false;
	static Enum rgbMode = RGBModes.RGB;
	static boolean negative = false;

	// framerate & calculation period
	static int framerate = 120;
	static int calcsPerPeriod = 1;
	static boolean normalize;
	private static float[] curInput = new float[3];
	private static ArrayList<float[]> lastInputList = new ArrayList<float[]>();
	private static ArrayList<float[]> curInputList = new ArrayList<float[]>();
	private static float[] slope = new float[3];
	private static ArrayList<float[]> slopes = new ArrayList<float[]>();

	static boolean uiHidden = false;
	static boolean showHide = false;
	static boolean showFB = false;
	static boolean displayRGBselector = false;
	static boolean printFramerate = true;
	static boolean displayFramerate = false;

	static int backKeyState;
	static ArrayList<String> optionsList = new ArrayList<String>();
	static boolean preferencesListInvisible;

	static String sendAddr = "192.168.1.2";
	public static String rootCmd = "vosc";
	static String r = "/" + rootCmd + "/red";
	static String g = "/" + rootCmd + "/green";
	static String b = "/" + rootCmd + "/blue";

	// feedback messages for each pixel in the three color channels
	static Set<String> rCmds = new HashSet<String>();
	static Set<String> gCmds = new HashSet<String>();
	static Set<String> bCmds = new HashSet<String>();

	KetaiSQLite db;

	static float density;

	static Enum mode;
	static Enum gestureMode = GestureModes.SWAP;

	// lock the state of a pixel after changing its state, otherwise pixels would constantly
	// change their state as long as they're hoevered
	static ArrayList<Boolean[]> lockList = new ArrayList<Boolean[]>();
	// store the states of all pixels
	static ArrayList<Boolean[]> offPxls = new ArrayList<Boolean[]>();

	// the pixel that's currently hovered
	static int hoverPixel;

	// predefined poolean tripplets, to be added to the default lockList, offPxls
	static Boolean[] falses = {false, false, false};
	static Boolean[] trues = {true, true, true};

	static boolean showSnapshots = false;
	static long numSnapshots;

	public static volatile boolean printSensors = false;
	static boolean sensorsPrinting = false;

	public void setup() {
		boolean querySuccess;

		background(0);

		// detect smallest possible preview size as that's totally sufficient for our purposes
		Camera camera = Camera.open();
		List<Camera.Size> camResList = camera.getParameters().getSupportedPreviewSizes();

		Camera.Size res  = camResList.get(0);
		for (int i = 1; i < camResList.size(); i++) {
			if (camResList.get(i).width * camResList.get(i).width < camResList.get(i-1)
					.width * camResList.get(i-1).height) {
				res = camResList.get(i);
			}
		}
		camera.release();

		VideOSCSensors.initSensors(this);
		
		imageMode(CENTER);

		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		density = dm.density;

		mode = InteractionModes.BASIC;
//		mode = InteractionModes.SINGLE_PIXEL;

		// for some reason setup() often (but not always) seems to be executed twice
		// hence, we check if variables have already been initialized

		if (optionsList.indexOf("Network Settings") < 0) {
			optionsList.add("Network Settings");
		}
		if (optionsList.indexOf("Resolution Settings") < 0) {
			optionsList.add("Resolution Settings");
		}
		if (optionsList.indexOf("Sensors") < 0) {
			optionsList.add("Sensors");
		}
		if (optionsList.indexOf("About VideOSC") < 0) {
			optionsList.add("About VideOSC");
		}

		db = new KetaiSQLite(this); // open database file

		querySuccess = VideOSCDB.setUpNetworkSettings(this, db);
		if (!querySuccess) {
			KetaiAlertDialog.popup(this, "SQL Error", "The network settings could not be " +
					"determined");
		}

		r = "/" + rootCmd + "/red";
		g = "/" + rootCmd + "/green";
		b = "/" + rootCmd + "/blue";

		querySuccess = VideOSCDB.setUpResolutionSettings(this, db);
		if (!querySuccess) {
			KetaiAlertDialog.popup(this, "SQL Error", "The resolution settings could not be " +
					"determined");
		}

		querySuccess = VideOSCDB.setUpSensors(this, db);
		if (!querySuccess) {
			KetaiAlertDialog.popup(this, "SQL Error", "The sensors settings could not be determined");
		}

		VideOSCDB.setUpSnapshots(this, db);
		numSnapshots = VideOSCDB.countSnapshots(this, db);

		pxWidth = width / resW;
		pxHeight = height / resH;

		for (int i = 0; i < resH * resW; i++) {
			lockList.add(falses.clone());
			offPxls.add(falses.clone());
		}

		if (oscP5 == null) {
			oscP5 = new OscP5(this, listenPort);
		}

		if (broadcastLoc == null) {
			broadcastLoc = new NetAddress(sendAddr, broadcastPort);
		}

		if (feedbackAddr == null) {
			feedbackAddr = new NetAddress(sendAddr, listenPort);
		}

		if (cam == null) {
			cam = new KetaiCamera(this, res.width, res.height, 30); // works
		}

		VideOSCUI.loadUIImages(this);

		VideOSCUI.uiYtop = 80;
		VideOSCUI.uiXright = width - 130;
		VideOSCUI.uiYbottom = height - 90;

		backKeyState = 0;

		frameRate(framerate);
	}

	public void draw() {
		float rval, gval, bval;

		if (frameCount % calcsPerPeriod == 0) {
			// wipe out anything that's still on screen from the previous cycle
			// e.g. text from preferences dialogs...
			background(0);
			if (!cam.isStarted()) {
				new VideOSCPreload(this, width / 2, height / 2 + 170, 12, 5);
				textAlign(CENTER);
				fill(255);
				textSize(40);
				text("TAP SCREEN TO OPEN CAMERA", width / 2, height / 2 + 280);
			}
			textAlign(LEFT);

			pImg = cam.get();
			pImg.loadPixels();
			pImg.updatePixels();
			image(pImg, width / 2, height / 2, width, height);
			pImg.resize(resW, resH);
			pImg.loadPixels();
			dimensions = pImg.width * pImg.height;

			lastInputList.clear();
			slopes.clear();

			for (float[] val : curInputList)
				lastInputList.add(val.clone());

			curInputList.clear();

			// offPxls needs to be at least of the size of dimensions after changing the resolution
			if (offPxls.size() < dimensions) {
				// pad up offPxls with 'falses' triplets
				int sizeDiff = dimensions - offPxls.size();
				for (int i = 0; i < sizeDiff; i++) {
					offPxls.add(falses.clone());
					lockList.add(falses.clone());
				}
			}

			for (int i = 0; i < dimensions; i++) {
				// only the downsampled image gets inverted as inverting the original would slow
				// down the application considerably
				int rVal = (negative) ? 0xFF - ((pImg.pixels[i] >> 16) & 0xFF)
						: (pImg.pixels[i] >> 16) & 0xFF;
				int gVal = (negative) ? 0xFF - ((pImg.pixels[i] >> 8) & 0xFF)
						: (pImg.pixels[i] >> 8) & 0xFF;
				int bVal = (negative) ? 0xFF - (pImg.pixels[i] & 0xFF)
						: pImg.pixels[i] & 0xFF;

				if (negative)
					pImg.pixels[i] = color(rVal, gVal, bVal, 255);

				// compose basic OSC message for slot
				oscR = VideOSCOscHandling.makeMessage(oscR, r + str(i + 1));
				oscG = VideOSCOscHandling.makeMessage(oscG, g + str(i + 1));
				oscB = VideOSCOscHandling.makeMessage(oscB, b + str(i + 1));

				if (rgbMode.equals(RGBModes.RGB)) {
					if (offPxls.get(i)[0] && !offPxls.get(i)[1] && !offPxls.get(i)[2]) {
						// r
						pImg.pixels[i] = color(0, gVal, bVal, 255 / 3);
					} else if (!offPxls.get(i)[0] && offPxls.get(i)[1] && !offPxls.get(i)[2]) {
						// g;
						pImg.pixels[i] = color(rVal, 0, bVal, 255 / 3);
					} else if (!offPxls.get(i)[0] && !offPxls.get(i)[1] && offPxls.get(i)[2]) {
						// b;
						pImg.pixels[i] = color(rVal, gVal, 0, 255 / 3);
					} else if (offPxls.get(i)[0] && offPxls.get(i)[1] && !offPxls.get(i)[2]) {
						// rg;
						pImg.pixels[i] = color(0, 0, bVal, 255 / 3 * 2);
					} else if (offPxls.get(i)[0] && !offPxls.get(i)[1] && offPxls.get(i)[2]) {
						// rb;
						pImg.pixels[i] = color(0, gVal, 0, 255 / 3 * 2);
					} else if (!offPxls.get(i)[0] && offPxls.get(i)[1] && offPxls.get(i)[2]) {
						// bg;
						pImg.pixels[i] = color(rVal, 0, 0, 255 / 3 * 2);

					} else if (offPxls.get(i)[0] && offPxls.get(i)[1] && offPxls.get(i)[2]) {
						// rgb
						pImg.pixels[i] = color(0, 0);
					}
				} else if (rgbMode.equals(RGBModes.R)) {
					if (offPxls.get(i)[0])
						pImg.pixels[i] = color(rVal, 255, 255);
					else
						pImg.pixels[i] = color(rVal, 0, 0);
				} else if (rgbMode.equals(RGBModes.G)) {
					if (offPxls.get(i)[1])
						pImg.pixels[i] = color(255, gVal, 255);
					else
						pImg.pixels[i] = color(0, gVal, 0);
				} else if (rgbMode.equals(RGBModes.B)) {
					if (offPxls.get(i)[2])
						pImg.pixels[i] = color(255, 255, bVal);
					else
						pImg.pixels[i] = color(0, 0, bVal);
				}

				if (play) {
					if (calcsPerPeriod == 1) {
						if (normalize) {
							rval = (float) rVal / 255;
							gval = (float) gVal / 255;
							bval = (float) bVal / 255;
						} else {
							rval = rVal;
							gval = gVal;
							bval = bVal;
						}

						if (!offPxls.get(i)[0]) {
							oscR.add(rval);
							oscP5.send(oscR, broadcastLoc);
						}
						if (!offPxls.get(i)[1]) {
							oscR.add(gval);
							oscP5.send(oscG, broadcastLoc);
						}
						if (!offPxls.get(i)[2]) {
							oscR.add(bval);
							oscP5.send(oscB, broadcastLoc);
						}
					} else {
						curInput[0] = (float) rVal;
						curInput[1] = (float) gVal;
						curInput[2] = (float) bVal;

						curInputList.add(curInput.clone());

						if (lastInputList.size() >= dimensions) {
							rval = lastInputList.get(i)[0];
							gval = lastInputList.get(i)[1];
							bval = lastInputList.get(i)[2];

							if (normalize) {
								rval = rval / 255;
								gval = gval / 255;
								bval = bval / 255;
							}

							if (!offPxls.get(i)[0]) {
								oscR.add(rval);
								oscP5.send(oscR, broadcastLoc);
							}
							if (!offPxls.get(i)[1]) {
								oscG.add(gval);
								oscP5.send(oscG, broadcastLoc);
							}
							if (!offPxls.get(i)[2]) {
								oscB.add(bval);
								oscP5.send(oscB, broadcastLoc);
							}

							float lastInputR = lastInputList.get(i)[0];
							float lastInputG = lastInputList.get(i)[1];
							float lastInputB = lastInputList.get(i)[2];

							slope[0] = (curInput[0] - lastInputR) / calcsPerPeriod;
							slope[1] = (curInput[1] - lastInputG) / calcsPerPeriod;
							slope[2] = (curInput[2] - lastInputB) / calcsPerPeriod;

							slopes.add(slope.clone());
						}
					}
				}
			}

			image(pImg, width / 2, height / 2, width, height);
			VideOSCUI.drawTools(this, db);
			if (showFB)
				VideOSCUI.printOSC(this);
			VideOSCUI.drawRGBUI(this);
			if (displayRGBselector)
				VideOSCUI.drawRGBModeSelector(this);
			if (displayFramerate)
				VideOSCUI.printFramerate(this, printFramerate);
			VideOSCPreferences.darkenBackground(this);
			if (sensorsPrinting) {
				VideOSCSensors.updatePrintedSensors();
			}
		} else {
			if (play && lastInputList.size() >= dimensions) {
				int index = frameCount % calcsPerPeriod;
				for (int i = 0; i < dimensions; i++) {
					if (!offPxls.get(i)[0]) {
						oscR = VideOSCOscHandling.makeMessage(oscR, r + str(i + 1));
						float lastInputR = lastInputList.get(i)[0];
						float slopeR = slopes.get(i)[0];
						if (normalize)
							oscR.add((slopeR * index + lastInputR) / 255);
						else
							oscR.add(slopeR * index + lastInputR);
						oscP5.send(oscR, broadcastLoc);
					}
					if (!offPxls.get(i)[1]) {
						oscG = VideOSCOscHandling.makeMessage(oscG, g + str(i + 1));
						float lastInputG = lastInputList.get(i)[1];
						float slopeG = slopes.get(i)[1];
						if (normalize)
							oscG.add((slopeG * index + lastInputG) / 255);
						else
							oscG.add(slopeG * index + lastInputG);
						oscP5.send(oscG, broadcastLoc);
					}
					if (!offPxls.get(i)[2]) {
						oscB = VideOSCOscHandling.makeMessage(oscB, b + str(i + 1));
						float lastInputB = lastInputList.get(i)[2];
						float slopeB = slopes.get(i)[2];
						if (normalize)
							oscB.add((slopeB * index + lastInputB) / 255);
						else
							oscB.add(slopeB * index + lastInputB);
						oscP5.send(oscB, broadcastLoc);
					}
				}
			}
		}
		if (showHide)
			VideOSCUI.setShowHideMenus(this);
	}

	// http://www.sojamo.de/libraries/oscP5/examples/oscP5sendReceive/oscP5sendReceive.pde
	private void oscEvent(OscMessage fbMessage) {
		VideOSCOscHandling.prepareFeedbackStrings(fbMessage);
	}

	// which pixel am I currently hovering?
	private int getHoverPixel(int x, int y) {
		int hIndex = x / pxWidth;
		int vIndex = y / pxHeight;

		return vIndex * resW + hIndex;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	public void onCameraPreviewEvent() {
		cam.read();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		Boolean[] lockPixel;
		Boolean[] offPixel;
		Boolean[] locks;

		int x = (int) event.getX();                    // get x/y coords of touch event
		int y = (int) event.getY();

		int action = event.getActionMasked();          // get code for action
		double pressure = event.getPressure();         // get pressure and size

		switch (action) {                              // let us know which action code shows up
			case MotionEvent.ACTION_DOWN:
				hoverPixel = getHoverPixel(x, y);
				VideOSCUI.processUIClicks(this, x, y, db);
				break;
			case MotionEvent.ACTION_UP:
				hoverPixel = getHoverPixel(x, y);
				Collections.fill(lockList, falses.clone());
				break;
			case MotionEvent.ACTION_MOVE:
				if (mode.equals(InteractionModes.SINGLE_PIXEL) && pressure > 0.65) {
					hoverPixel = getHoverPixel(x, y);
					lockPixel = lockList.get(hoverPixel);
					offPixel = offPxls.get(hoverPixel);
					if (rgbMode.equals(RGBModes.RGB)) {
						if (!lockPixel[0] && !lockPixel[1] && !lockPixel[2]) {
							if (gestureMode.equals(GestureModes.SWAP)) {
								offPxls.get(hoverPixel)[0] = !offPixel[0];
								offPxls.get(hoverPixel)[1] = !offPixel[1];
								offPxls.get(hoverPixel)[2] = !offPixel[2];
							} else if (gestureMode.equals(GestureModes.ERASE)) {
								offPxls.set(hoverPixel, falses.clone());
							}
							lockList.set(hoverPixel, trues.clone());
						}
					} else if (rgbMode.equals(RGBModes.R)) {
						if (!lockPixel[0]) {
							offPxls.get(hoverPixel)[0] = !offPixel[0];
							locks = new Boolean[]{true, lockList.get(hoverPixel)[1], lockList.get(hoverPixel)
									[2]};
							lockList.set(hoverPixel, locks);
						}
					} else if (rgbMode.equals(RGBModes.G)) {
						if (!lockPixel[1]) {
							offPxls.get(hoverPixel)[1] = !offPixel[1];
							locks = new Boolean[]{lockList.get(hoverPixel)[0], true, lockList.get
									(hoverPixel)
									[2]};
							lockList.set(hoverPixel, locks);
						}
					} else if (rgbMode.equals(RGBModes.B)) {
						if (!lockPixel[2]) {
							offPxls.get(hoverPixel)[2] = !offPixel[2];
							locks = new Boolean[]{lockList.get(hoverPixel)[0], lockList.get
									(hoverPixel)
									[1], true};
							lockList.set(hoverPixel, locks);
						}
					}
				}
				break;
			default:
		}

		return super.dispatchTouchEvent(event);        // pass data along when done!
	}

	public void onKetaiListSelection(KetaiList klist) {
		VideOSCUI.processKetaiListClicks(this, klist, db);
	}

/*
	public void keyPressed() {
//		Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
		Log.d(TAG, "is finishing: " + isFinishing());
//		Context context = getApplicationContext();
//		Log.d(TAG, "context: " + context);
		if (key == CODED) {
			Log.d(TAG, "key == CODED");
//			restartServiceIntent.setPackage(getPackageName());
			if (keyCode == MENU && curOptions.equals("")*/
/* && preferencesListInvisible*//*
) {
				showHide = true;
			} */
/*else if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
				return true;
//				Log.d(TAG, "back key hit");
			}*//*

		}
	}
*/

/*
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
		Log.d(TAG, "onKeyDown called: " + keyCode + ", " + event + ", " + isFinishing());
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getRepeatCount() == 0) {
			Log.d(TAG, "back button hit");
			onBackPressed();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
*/

/*
	@Override
	public void onBackPressed() {
		Log.d("CDA", "onBackPressed Called: " + isFinishing());
		if (!curOptions.equals(""))
			curOptions = "";
		Intent setIntent = new Intent(Intent.ACTION_MAIN);
		Log.d(TAG, "intent: " + setIntent);
		setIntent.addCategory(Intent.CATEGORY_HOME);
		setIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(setIntent);
	}
*/

	// OSC sensor events
	public void onOrientationEvent(float x, float y, float z, long time, int accuracy) {
		if (play && VideOSCSensors.useOri && oscP5 != null)
			VideOSCSensors.orientationEvent(x, y, z, time, accuracy);
	}

	public void onAccelerometerEvent(float x, float y, float z, long time, int accuracy) {
		if (play && VideOSCSensors.useAcc && oscP5 != null)
			VideOSCSensors.accelerometerEvent(x, y, z, time, accuracy);
	}

	public void onMagneticFieldEvent(float x, float y, float z, long time, int accuracy) {
		if (play && VideOSCSensors.useMag && oscP5 != null)
			VideOSCSensors.magneticFieldEvent(x, y, z, time, accuracy);
	}

	public void onGravityEvent(float x, float y, float z, long time, int accuracy) {
		if (play && VideOSCSensors.useGrav && oscP5 != null)
			VideOSCSensors.gravityEvent(x, y, z, time, accuracy);
	}

	public void onLinearAccelerationEvent(float x, float y, float z, long time, int accuracy) {
		if (play && VideOSCSensors.useLinAcc && oscP5 != null)
			VideOSCSensors.linearAccelerationEvent(x, y, z, time, accuracy);
	}

	public void onProximityEvent(float distance, long time, int accuracy) {
		if (play && VideOSCSensors.useProx && oscP5 != null)
			VideOSCSensors.proximityEvent(distance, time, accuracy);
	}

	public void onLightEvent(float intensity, long time, int accuracy) {
		if (play && VideOSCSensors.useLight && oscP5 != null)
			VideOSCSensors.lightEvent(intensity, time, accuracy);
	}

	public void onPressureEvent(float pressure, long time, int accuracy) {
		if (play && VideOSCSensors.usePress && oscP5 != null)
			VideOSCSensors.pressureEvent(pressure, time, accuracy);
	}

	public void onAmbientTemperatureEvent(float temperature) {
		if (play && VideOSCSensors.useTemp && oscP5 != null)
			VideOSCSensors.temperatureEvent(temperature);
	}

	public void onRelativeHumidityEvent(float humidity) {
		if (play && VideOSCSensors.useHum && oscP5 != null)
			VideOSCSensors.humidityEvent(humidity);
	}

	public void onLocationEvent(double latitude, double longitude, double altitude, float accuracy) {
		if (play && VideOSCSensors.useLoc && oscP5 != null)
			VideOSCSensors.gpsEvent(latitude, longitude, altitude, accuracy);
	}
}
