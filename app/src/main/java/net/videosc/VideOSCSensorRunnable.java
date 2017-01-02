package net.videosc;

import android.os.StrictMode;
import android.util.Log;

import oscP5.OscMessage;

/**
 * Created by stefan on 24.12.16.
 */

public class VideOSCSensorRunnable implements Runnable {
    static volatile boolean runSensors = false;

    private static OscMessage oscOri;
    private static OscMessage oscAcc;
    private static OscMessage oscMag;
    private static OscMessage oscGrav;
    private static OscMessage oscProx;
    private static OscMessage oscLight;
    private static OscMessage oscPress;
    private static OscMessage oscTemp;
    private static OscMessage oscLinAcc;
    private static OscMessage oscHum;
    private static OscMessage oscGeo;

    private static Thread sensorThread;
    private final String TAG = "VideOSCSensorRunnable";

    @Override
    public void run() {
//        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        //noinspection InfiniteLoopStatement
        while (true) {
            try {
//                Log.d(TAG, "oriX: " + VideOSCSensors.oriX + ", oriY: " + VideOSCSensors.oriY + ", oriZ: " + VideOSCSensors.oriZ);
                oscOri = VideOSCOscHandling.makeMessage(oscOri, "/" + VideOSC.rootCmd + "/ori");
                oscOri.add(VideOSCSensors.oriX).add(VideOSCSensors.oriY).add(VideOSCSensors.oriZ).add(VideOSCSensors.oriTime).add(VideOSCSensors.oriAcc);
                VideOSC.oscP5.send(oscOri, VideOSC.broadcastLoc);
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[]) {
        if (sensorThread == null) {
            sensorThread = new Thread(new VideOSCSensorRunnable());
            sensorThread.start();
        }
    }
}
