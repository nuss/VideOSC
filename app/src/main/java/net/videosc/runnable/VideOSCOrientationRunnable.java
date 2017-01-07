package net.videosc.runnable;

import net.videosc.VideOSC;
import net.videosc.VideOSCOscHandling;
import net.videosc.VideOSCSensors;

import oscP5.OscMessage;

/**
 * Created by stefan on 07.01.17.
 */

public class VideOSCOrientationRunnable implements Runnable {
    private static Thread orientationThread;
    public static final Object orientationLock = new Object();

    private static OscMessage oscOri;

    private final static String TAG = "VideOSCOrientationRunnable";

    @Override
    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            synchronized (orientationLock) {
                try {
                    if (VideOSCSensors.useOri && VideOSC.sensors.isOrientationAvailable()) {
                        oscOri = VideOSCOscHandling.makeMessage(oscOri, "/" + VideOSC.rootCmd + "/ori");
                        oscOri.add(VideOSCSensors.oriX).add(VideOSCSensors.oriY).add(VideOSCSensors.oriZ).add(VideOSCSensors.oriTime).add(VideOSCSensors.oriAcc);
                        VideOSC.oscP5.send(oscOri, VideOSC.broadcastLoc);
                    }
                    orientationLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String args[]) {
        if (orientationThread == null) {
            orientationThread = new Thread(new VideOSCOrientationRunnable());
            orientationThread.start();
        }
    }
}
