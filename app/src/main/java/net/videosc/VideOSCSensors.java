package net.videosc;


import ketai.sensors.KetaiSensor;

/**
 * Created by stefan on 13.12.16.
 */

public class VideOSCSensors extends VideOSC {
    // orientation sensor values
    static float oriX;
    static float oriY;
    static float oriZ;
    static long oriTime;
    static int oriAcc;

    // acceleration sensor values
    static float accX;
    static float accY;
    static float accZ;
    static long accTime;
    static int accAcc;

    // magnetic field sensor values
    static float magX;
    static float magY;
    static float magZ;
    static long magTime;
    static int magAcc;

    // gravity sensor values
    static float gravX;
    static float gravY;
    static float gravZ;
    static long gravTime;
    static int gravAcc;

    // proximity sensor
    static float proxDist;
    static long proxTime;
    static int proxAcc;

    // light sensor
    static float lightIntensity;
    static long lightTime;
    static int lightAcc;

    // air pressure sensor
    static float pressIntensity;
    static long pressTime;
    static int pressAcc;

    // temperature sensor
    static float tempCels;
    static long tempTime;
    static int tempAcc;

    // linear acceleration sensor: acceleration force in m/s^2, minus gravity
    static float linAccX;
    static float linAccY;
    static float linAccZ;
    static long linAccTime;
    static int linAccAcc;

    // humidity sensor
    static float humidity;

    // GPS support
    static float gpsLong;
    static float gpsLat;
    static int gpsAcc;
}
