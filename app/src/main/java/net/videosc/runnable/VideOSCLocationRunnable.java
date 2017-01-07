package net.videosc.runnable;

import net.videosc.VideOSC;
import net.videosc.VideOSCOscHandling;
import net.videosc.VideOSCSensors;

import oscP5.OscMessage;

/**
 * Created by stefan on 07.01.17.
 */

public class VideOSCLocationRunnable implements Runnable {
    private static OscMessage oscGeo;
    private static Thread locationThread;
    // test
    public static volatile double latitude;
    public static volatile double longitude;
    public static volatile double altitude;

    public static final Object locationLock = new Object();

    private final static String TAG = "VideOSCLocationRunnable";

    @Override
    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            synchronized (locationLock) {
                try {
                    if (VideOSCSensors.useLoc && !VideOSC.provider.equals("none") && VideOSC.provider != null) {
                        // testing, testing...
                        latitude = VideOSCSensors.locLat;
                        longitude = VideOSCSensors.locLong;
                        altitude = VideOSCSensors.locAlt;

                        oscGeo = VideOSCOscHandling.makeMessage(oscGeo, "/" + VideOSC.rootCmd + "/loc");
                        oscGeo.add(VideOSCSensors.locLat).add(VideOSCSensors.locLong).add(VideOSCSensors.locAlt).add(VideOSCSensors.locAcc);
                        VideOSC.oscP5.send(oscGeo, VideOSC.broadcastLoc);
                    }
                    locationLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String args[]) {
        if (locationThread == null) {
            locationThread = new Thread(new VideOSCLocationRunnable());
            locationThread.start();
        }
    }
}
