package net.videosc;


import android.util.Log;
import net.videosc.runnable.*;

/**
 * Created by stefan on 13.12.16.
 */

public class VideOSCSensors extends VideOSC {
    private final static String TAG = "VideOSCSensors";

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
    public static volatile long tempTime;
    public static volatile int tempAcc;

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
    public static volatile double locLong;
    public static volatile double locLat;
    public static volatile double locAlt;
    public static volatile float locAcc;
    static int locCount = 0;

    public static boolean useLoc = false;

    static void availableSensors() {
        Log.d(TAG, "is orientation available: " + sensors.isOrientationAvailable());
        Log.d(TAG, "is acceleration available: " + sensors.isAccelerometerAvailable());
        Log.d(TAG, "is magnetic field available: " + sensors.isMagenticFieldAvailable());
        sensors.enableAmibentTemperature();
        Log.d(TAG, "is ambient temperature available: " + sensors.isAmbientTemperatureAvailable());
        Log.d(TAG, "is temperature available: " + sensors.isTemperatureAvailable());
        Log.d(TAG, "is pressure available: " + sensors.isPressureAvailable());
        Log.d(TAG, "is light sensor available: " + sensors.isLightAvailable());
        Log.d(TAG, "is proximity sensor available: " + sensors.isProximityAvailable());
        sensors.enableRelativeHumiditySensor();
        Log.d(TAG, "is humidity sensor available: " + sensors.isRelativeHumidityAvailable());
        Log.d(TAG, "is linear acceleration available: " + sensors.isLinearAccelerationAvailable());
    }

    static void orientationEvent(float x, float y, float z, long time, int accuracy) {
        oriX = x;
        oriY = y;
        oriZ = z;
        oriTime = time;
        oriAcc = accuracy;

        if (oscP5 != null) {
            synchronized (VideOSCOrientationRunnable.orientationLock) {
                VideOSCOrientationRunnable.main(null);
                VideOSCOrientationRunnable.orientationLock.notify();
            }
        }
    }

    static void accelerometerEvent(float x, float y, float z, long time, int accuracy) {
        accX = x;
        accY = y;
        accZ = z;
        accTime = time;
        accAcc = accuracy;

        if (oscP5 != null) {
            synchronized (VideOSCAccelerationRunnable.accelerometerLock) {
                VideOSCAccelerationRunnable.main(null);
                VideOSCAccelerationRunnable.accelerometerLock.notify();
            }
        }
    }

    static void magneticFieldEvent(float x, float y, float z, long time, int accuracy) {
        magX = x;
        magY = y;
        magZ = z;
        magTime = time;
        magAcc = accuracy;

        if (oscP5 != null) {
            synchronized (VideOSCMagnetismRunnable.magnetismLock) {
	            VideOSCMagnetismRunnable.main(null);
	            VideOSCMagnetismRunnable.magnetismLock.notify();
            }
        }
    }

    static void gravityEvent(float x, float y, float z, long time, int accuracy) {
        gravX = x;
        gravY = y;
        gravZ = z;
        gravTime = time;
        gravAcc = accuracy;

        if (oscP5 != null) {
            synchronized (VideOSCGravityRunnable.gravityLock) {
                VideOSCGravityRunnable.main(null);
	            VideOSCGravityRunnable.gravityLock.notify();
            }
        }
    }

    static void linearAccelerationEvent(float x, float y, float z, long time, int accuracy) {
        linAccX = x;
        linAccY = y;
        linAccZ = z;
        linAccTime = time;
        linAccAcc = accuracy;

        if (oscP5 != null) {
            synchronized (VideOSCLinearAccelerationRunnable.linearAccelerationLock) {
	            VideOSCLinearAccelerationRunnable.main(null);
	            VideOSCLinearAccelerationRunnable.linearAccelerationLock.notify();
            }
        }
    }

    static void proximityEvent(float dist, long time, int accuracy) {
        proxDist = dist;
        proxTime = time;
        proxAcc = accuracy;

        if (oscP5 != null) {
            synchronized (VideOSCProximityRunnable.proximityLock) {
	            VideOSCProximityRunnable.main(null);
	            VideOSCProximityRunnable.proximityLock.notify();
            }
        }
    }

    static void lightEvent(float intensity, long time, int accuracy) {
        lightIntensity = intensity;
        lightTime = time;
        lightAcc = accuracy;

        if (oscP5 != null) {
            synchronized (VideOSCLightsensorRunnable.lightsensorLock) {
	            VideOSCLightsensorRunnable.main(null);
	            VideOSCLightsensorRunnable.lightsensorLock.notify();
            }
        }
    }

    public static void pressureEvent(float pressure, long time, int accuracy) {
        pressIntensity = pressure;
        pressTime = time;
        pressAcc = accuracy;

        if (oscP5 != null) {
            synchronized (VideOSCPressureRunnable.pressureLock) {
	            VideOSCPressureRunnable.main(null);
	            VideOSCPressureRunnable.pressureLock.notify();
            }
        }
    }

    public static void temperatureEvent(float temperature/*, long time, int accuracy*/) {
        tempCels = temperature;
//        tempTime = time;
//        tempAcc = accuracy;

        if (oscP5 != null) {
            synchronized (VideOSCTemperatureRunnable.temperatureLock) {
	            VideOSCTemperatureRunnable.main(null);
	            VideOSCTemperatureRunnable.temperatureLock.notify();
            }
        }
    }

    public static void humidityEvent(float humidity) {
        humVal = humidity;

        if (oscP5 != null) {
            synchronized (VideOSCHumidityRunnable.humidityLock) {
	            VideOSCHumidityRunnable.main(null);
	            VideOSCHumidityRunnable.humidityLock.notify();
            }
        }
    }

    public static void gpsEvent(double latitude, double longitude, double altitude, float accuracy) {
        locLat = latitude;
        locLong = longitude;
        locAlt = altitude;
        locAcc = accuracy;
        locCount++;

        if (oscP5 != null) {
            synchronized (VideOSCLocationRunnable.locationLock) {
                VideOSCLocationRunnable.main(null);
                VideOSCLocationRunnable.locationLock.notify();
            }
        }
    }
}