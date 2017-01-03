package net.videosc;


import android.util.Log;

/**
 * Created by stefan on 13.12.16.
 */

public class VideOSCSensors extends VideOSC {
    private final static String TAG = "VideOSCSensors";

    // orientation sensor values
    static volatile float oriX;
    static volatile float oriY;
    static volatile float oriZ;
    static volatile long oriTime;
    static volatile int oriAcc;

    static boolean useOri = false;

    // acceleration sensor values
    static volatile float accX;
    static volatile float accY;
    static volatile float accZ;
    static volatile long accTime;
    static volatile int accAcc;

    static boolean useAcc = false;

    // magnetic field sensor values
    static volatile float magX;
    static volatile float magY;
    static volatile float magZ;
    static volatile long magTime;
    static volatile int magAcc;

    static boolean useMag = false;

    // gravity sensor values
    static volatile float gravX;
    static volatile float gravY;
    static volatile float gravZ;
    static volatile long gravTime;
    static volatile int gravAcc;

    static boolean useGrav = false;

    // proximity sensor
    static volatile float proxDist;
    static volatile long proxTime;
    static volatile int proxAcc;

    static boolean useProx = false;

    // light sensor
    static volatile float lightIntensity;
    static volatile long lightTime;
    static volatile int lightAcc;

    static boolean useLight = false;

    // air pressure sensor
    static volatile float pressIntensity;
    static volatile long pressTime;
    static volatile int pressAcc;

    static boolean usePress = false;

    // temperature sensor
    static volatile float tempCels;
    static volatile long tempTime;
    static volatile int tempAcc;

    static boolean useTemp = false;

    // linear acceleration sensor: acceleration force in m/s^2, minus gravity
    static volatile float linAccX;
    static volatile float linAccY;
    static volatile float linAccZ;
    static volatile long linAccTime;
    static volatile int linAccAcc;

    static boolean useLinAcc = false;

    // humidity sensor
    static volatile float humidity;

    static boolean useHum = false;

    // GPS support
    static volatile float gpsLong;
    static volatile float gpsLat;
    static volatile int gpsAcc;

    static boolean useGPS = false;

    static void availableSensors() {
        Log.d(TAG, "is orientation available: " + sensors.isOrientationAvailable());
        Log.d(TAG, "is acceleration available: " + sensors.isAccelerometerAvailable());
        Log.d(TAG, "is magnetic field available: " + sensors.isMagenticFieldAvailable());
        sensors.enableAmibentTemperature();
        Log.d(TAG, "is ambient temperature available: " + sensors.isAmbientTemperatureAvailable());
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

        if (oscP5 != null)
            VideOSCSensorRunnable.main(null);
    }

    static void accelerometerEvent(float x, float y, float z, long time, int accuracy) {
        accX = x;
        accY = y;
        accZ = z;
        accTime = time;
        accAcc = accuracy;

        if (oscP5 != null)
            VideOSCSensorRunnable.main(null);
    }

    static void magneticFieldEvent(float x, float y, float z, long time, int accuracy) {
        magX = x;
        magY = y;
        magZ = z;
        magTime = time;
        magAcc = accuracy;

        if (oscP5 != null)
            VideOSCSensorRunnable.main(null);
    }

    static void gravityEvent(float x, float y, float z, long time, int accuracy) {
        gravX = x;
        gravY = y;
        gravZ = z;
        gravTime = time;
        gravAcc = accuracy;

        if (oscP5 != null)
            VideOSCSensorRunnable.main(null);
    }

    static void linearAccelerationEvent(float x, float y, float z, long time, int accuracy) {
        linAccX = x;
        linAccY = y;
        linAccZ = z;
        linAccTime = time;
        linAccAcc = accuracy;

        if (oscP5 != null)
            VideOSCSensorRunnable.main(null);
    }
}