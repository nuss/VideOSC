package net.videosc;


import android.util.Log;

import ketai.sensors.KetaiSensor;

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
    static float accX;
    static float accY;
    static float accZ;
    static long accTime;
    static int accAcc;

    static boolean useAcc = false;

    // magnetic field sensor values
    static float magX;
    static float magY;
    static float magZ;
    static long magTime;
    static int magAcc;

    static boolean useMag = false;

    // gravity sensor values
    static float gravX;
    static float gravY;
    static float gravZ;
    static long gravTime;
    static int gravAcc;

    static boolean useGrav = false;

    // proximity sensor
    static float proxDist;
    static long proxTime;
    static int proxAcc;

    static boolean useProx = false;

    // light sensor
    static float lightIntensity;
    static long lightTime;
    static int lightAcc;

    static boolean useLight = false;

    // air pressure sensor
    static float pressIntensity;
    static long pressTime;
    static int pressAcc;

    static boolean usePress = false;

    // temperature sensor
    static float tempCels;
    static long tempTime;
    static int tempAcc;

    static boolean useTemp = false;

    // linear acceleration sensor: acceleration force in m/s^2, minus gravity
    static float linAccX;
    static float linAccY;
    static float linAccZ;
    static long linAccTime;
    static int linAccAcc;

    static boolean useLinAcc = false;

    // humidity sensor
    static float humidity;

    static boolean useHum = false;

    // GPS support
    static float gpsLong;
    static float gpsLat;
    static int gpsAcc;

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

        if (oscP5 != null) {
            VideOSCSensorRunnable.main(null);
        }
    }
}