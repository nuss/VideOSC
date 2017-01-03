package net.videosc;

import android.util.Log;

import oscP5.OscMessage;

/**
 * Created by stefan on 03.01.17.
 */
public class VideOSCLocationRunnable implements Runnable{
    private static OscMessage oscGeo;
    private static Thread locationThread;
    private final static String TAG = "VideOSCLocationRunnable";

    @Override
    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                if (VideOSCSensors.useLoc && !VideOSC.provider.equals("none")) {
                    Log.d(TAG, "latitude: " + VideOSCSensors.locLat + ", longitude: " + VideOSCSensors.locLong + ", altitude: " + VideOSCSensors.locAlt);
                    oscGeo = VideOSCOscHandling.makeMessage(oscGeo, "/" + VideOSC.rootCmd + "/loc");
                    oscGeo.add(VideOSCSensors.locLat).add(VideOSCSensors.locLong).add(VideOSCSensors.locAlt).add(VideOSCSensors.locAcc);
                    VideOSC.oscP5.send(oscGeo, VideOSC.broadcastLoc);
                }
                Thread.sleep(2000);
            } catch(InterruptedException e) {
                e.printStackTrace();
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
