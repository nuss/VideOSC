package net.videosc;

import android.util.Log;

import net.videosc.APWidgetExtend.APText;
import net.videosc.runnable.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import apwidgets.APButton;
import apwidgets.APWidget;
import apwidgets.APWidgetContainer;
import apwidgets.OnClickWidgetListener;
import ketai.data.KetaiSQLite;
import ketai.sensors.KetaiLocation;
import ketai.sensors.KetaiSensor;
import processing.core.PApplet;

/**
 * Created by stefan on 13.12.16.
 */

public class VideOSCSensors extends VideOSC {
	private final static String TAG = "VideOSCSensors";

	// common sensors
	static KetaiSensor sensors;

	// orientation sensor values
	public static volatile float oriX;
	public static volatile float oriY;
	public static volatile float oriZ;
	public static volatile long oriTime;
	public static volatile int oriAcc;

	public static boolean useOri = false;

	// acceleration sensor values
	public static volatile float accX;
	public static volatile float accY;
	public static volatile float accZ;
	public static volatile long accTime;
	public static volatile int accAcc;

	public static boolean useAcc = false;

	// magnetic field sensor values
	public static volatile float magX;
	public static volatile float magY;
	public static volatile float magZ;
	public static volatile long magTime;
	public static volatile int magAcc;

	public static boolean useMag = false;

	// gravity sensor values
	public static volatile float gravX;
	public static volatile float gravY;
	public static volatile float gravZ;
	public static volatile long gravTime;
	public static volatile int gravAcc;

	public static boolean useGrav = false;

	// proximity sensor
	public static volatile float proxDist;
	public static volatile long proxTime;
	public static volatile int proxAcc;

	public static boolean useProx = false;

	// light sensor
	public static volatile float lightIntensity;
	public static volatile long lightTime;
	public static volatile int lightAcc;

	public static boolean useLight = false;

	// air pressure sensor
	public static volatile float pressIntensity;
	public static volatile long pressTime;
	public static volatile int pressAcc;

	public static boolean usePress = false;

	// temperature sensor
	public static volatile float tempCels;

	public static boolean useTemp = false;

	// linear acceleration sensor: acceleration force in m/s^2, minus gravity
	public static volatile float linAccX;
	public static volatile float linAccY;
	public static volatile float linAccZ;
	public static volatile long linAccTime;
	public static volatile int linAccAcc;

	public static boolean useLinAcc = false;

	// humidity sensor
	public static volatile float humVal;

	public static boolean useHum = false;

	// geolocation support
	public static volatile KetaiLocation location;
	public static volatile String provider;
	public static volatile double locLong;
	public static volatile double locLat;
	public static volatile double locAlt;
	public static volatile float locAcc;
	static int locCount = 0;

	public static boolean useLoc = false;

	// strings to be printed to screen
	public volatile static Map<String, String> sensorsInUse = new HashMap<String, String>();
	static HashMap<String, APText> texts = new HashMap<String, APText>();

	static void initSensors(PApplet applet) {
		// common sensors
		sensors = new KetaiSensor(applet);
		sensors.start();
		sensors.enableAllSensors();
		// location
		location = new KetaiLocation(applet);
		location.start();
		provider = location.getProvider();
		location.setUpdateRate(2000, 1);
	}

	static void availableSensors() {
		Log.d(TAG, "is orientation available: " + sensors.isOrientationAvailable());
		Log.d(TAG, "is acceleration available: " + sensors.isAccelerometerAvailable());
		Log.d(TAG, "is magnetic field available: " + sensors.isMagenticFieldAvailable());
		Log.d(TAG, "is ambient temperature available: " + sensors.isAmbientTemperatureAvailable());
		Log.d(TAG, "is temperature available: " + sensors.isTemperatureAvailable());
		Log.d(TAG, "is pressure available: " + sensors.isPressureAvailable());
		Log.d(TAG, "is light sensor available: " + sensors.isLightAvailable());
		Log.d(TAG, "is proximity sensor available: " + sensors.isProximityAvailable());
		Log.d(TAG, "is humidity sensor available: " + sensors.isRelativeHumidityAvailable());
		Log.d(TAG, "is linear acceleration available: " + sensors.isLinearAccelerationAvailable());
	}

	static void orientationEvent(float x, float y, float z, long time, int accuracy) {
		oriX = x;
		oriY = y;
		oriZ = z;
		oriTime = time;
		oriAcc = accuracy;

		synchronized (VideOSCOrientationRunnable.orientationLock) {
			VideOSCOrientationRunnable.main(null);
			VideOSCOrientationRunnable.orientationLock.notify();
		}
	}

	static void accelerometerEvent(float x, float y, float z, long time, int accuracy) {
		accX = x;
		accY = y;
		accZ = z;
		accTime = time;
		accAcc = accuracy;

		synchronized (VideOSCAccelerationRunnable.accelerometerLock) {
			VideOSCAccelerationRunnable.main(null);
			VideOSCAccelerationRunnable.accelerometerLock.notify();
		}
	}

	static void magneticFieldEvent(float x, float y, float z, long time, int accuracy) {
		magX = x;
		magY = y;
		magZ = z;
		magTime = time;
		magAcc = accuracy;

		synchronized (VideOSCMagnetismRunnable.magnetismLock) {
			VideOSCMagnetismRunnable.main(null);
			VideOSCMagnetismRunnable.magnetismLock.notify();
		}
	}

	static void gravityEvent(float x, float y, float z, long time, int accuracy) {
		gravX = x;
		gravY = y;
		gravZ = z;
		gravTime = time;
		gravAcc = accuracy;

		synchronized (VideOSCGravityRunnable.gravityLock) {
			VideOSCGravityRunnable.main(null);
			VideOSCGravityRunnable.gravityLock.notify();
		}
	}

	static void linearAccelerationEvent(float x, float y, float z, long time, int accuracy) {
		linAccX = x;
		linAccY = y;
		linAccZ = z;
		linAccTime = time;
		linAccAcc = accuracy;

		synchronized (VideOSCLinearAccelerationRunnable.linearAccelerationLock) {
			VideOSCLinearAccelerationRunnable.main(null);
			VideOSCLinearAccelerationRunnable.linearAccelerationLock.notify();
		}
	}

	static void proximityEvent(float dist, long time, int accuracy) {
		proxDist = dist;
		proxTime = time;
		proxAcc = accuracy;

		synchronized (VideOSCProximityRunnable.proximityLock) {
			VideOSCProximityRunnable.main(null);
			VideOSCProximityRunnable.proximityLock.notify();
		}
	}

	static void lightEvent(float intensity, long time, int accuracy) {
		lightIntensity = intensity;
		lightTime = time;
		lightAcc = accuracy;

		synchronized (VideOSCLightsensorRunnable.lightsensorLock) {
			VideOSCLightsensorRunnable.main(null);
			VideOSCLightsensorRunnable.lightsensorLock.notify();
		}
	}

	public static void pressureEvent(float pressure, long time, int accuracy) {
		pressIntensity = pressure;
		pressTime = time;
		pressAcc = accuracy;

		synchronized (VideOSCPressureRunnable.pressureLock) {
			VideOSCPressureRunnable.main(null);
			VideOSCPressureRunnable.pressureLock.notify();
		}
	}

	public static void temperatureEvent(float temperature/*, long time, int accuracy*/) {
		tempCels = temperature;

		synchronized (VideOSCTemperatureRunnable.temperatureLock) {
			VideOSCTemperatureRunnable.main(null);
			VideOSCTemperatureRunnable.temperatureLock.notify();
		}
	}

	public static void humidityEvent(float humidity) {
		humVal = humidity;

		synchronized (VideOSCHumidityRunnable.humidityLock) {
			VideOSCHumidityRunnable.main(null);
			VideOSCHumidityRunnable.humidityLock.notify();
		}
	}

	public static void gpsEvent(double latitude, double longitude, double altitude, float accuracy) {
		locLat = latitude;
		locLong = longitude;
		locAlt = altitude;
		locAcc = accuracy;
		locCount++;

		synchronized (VideOSCLocationRunnable.locationLock) {
			VideOSCLocationRunnable.main(null);
			VideOSCLocationRunnable.locationLock.notify();
		}
	}

	private static HashMap<String, String> keyNameAssociations() {
		HashMap<String, String> keyNameAssociations = new HashMap<String, String>();

		keyNameAssociations.put("ori", "orientation sensor");
		keyNameAssociations.put("acc", "accelerometer");
		keyNameAssociations.put("mag", "magnetic field sensor");
		keyNameAssociations.put("grav", "gravity sensor");
		keyNameAssociations.put("linAcc", "linear acceleration");
		keyNameAssociations.put("prox", "proximity sensor");
		keyNameAssociations.put("press", "air pressure sensor");
		keyNameAssociations.put("temp", "temperature sensor");
		keyNameAssociations.put("light", "light sensor");
		keyNameAssociations.put("hum", "humidity sensor");
		keyNameAssociations.put("loc", "location (provider: " + provider + ")");

		return keyNameAssociations;
	}

	static String keyToName(String key) {
		return keyNameAssociations().get(key);
	}

	private static void completeSensorsInUse(KetaiSQLite db) {
		ArrayList sensorsInUse = VideOSCDB.listSensorsInUse(db);

		for (Object key : sensorsInUse) {
			if (!VideOSCSensors.sensorsInUse.keySet().contains(key.toString())) {
				VideOSCSensors.sensorsInUse.put((String) key, keyToName((String) key) + " - no value yet");
			}
		}
	}

	static boolean printSensors(final PApplet applet, KetaiSQLite db) {
		final APWidgetContainer container = new APWidgetContainer(applet);
		final APButton close = new APButton((applet.width - (int) VideOSCUI.dc(220)) / 4 + (int) VideOSCUI.dc(50),
				(int) VideOSCUI.dc(50), (applet.width - (int) VideOSCUI.dc(220)) / 2, (int) VideOSCUI.dc(50) * 3, "Close");
		APText text;
		int nextYPos = (int) VideOSCUI.dc(50);

		// don't only rely on info coming from runnables - ask the database
		completeSensorsInUse(db);

		for (String key : texts.keySet()) {
			container.removeWidget(texts.get(key));
		}
		texts.clear();

		for (String key : VideOSCSensors.sensorsInUse.keySet()) {
			Log.d(TAG, VideOSCSensors.sensorsInUse.get(key));

			text = new APText((int) VideOSCUI.dc(50), nextYPos, applet.width - (int) VideOSCUI.dc(230), (int) VideOSCUI.dc(120));
			text.setText(VideOSCSensors.sensorsInUse.get(key));
			text.setTextSize(15);
			texts.put(key, text);
			nextYPos = text.getY() + text.getHeight() + (int) VideOSCUI.dc(10);
		}
		close.setPosition((applet.width - (int) VideOSCUI.dc(220)) / 4 + (int) VideOSCUI.dc(50), nextYPos);
		close.addOnClickWidgetListener(new OnClickWidgetListener() {
			@Override
			public void onClickWidget(APWidget apWidget) {
				for (String key : texts.keySet()) {
					container.removeWidget(texts.get(key));
				}
				container.removeWidget(close);
				VideOSC.sensorsPrinting = false;
				VideOSC.printSensors = false;
			}
		});

		for (String key : texts.keySet()) {
			container.addWidget(texts.get(key));
		}
		container.addWidget(close);

		return true;
	}

	static void updatePrintedSensors() {
		for (String key : VideOSCSensors.sensorsInUse.keySet()) {
			texts.get(key).setText(VideOSCSensors.sensorsInUse.get(key));
		}
	}
}