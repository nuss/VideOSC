package net.videosc.runnable;

import net.videosc.VideOSC;
import net.videosc.VideOSCOscHandling;
import net.videosc.VideOSCSensors;

import oscP5.OscMessage;

/**
 * Created by stefan on 07.01.17.
 */

public class VideOSCProximityRunnable implements Runnable {
    private static Thread proximityThread;
    public final static Object proximityLock = new Object();

    private OscMessage oscProx;

    private final static String TAG = "VideOSCProximityRunnable";

    @Override
    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            synchronized (proximityLock) {
                try {
                    if (VideOSC.printSensors) {
                        String proximity = "distance: " + VideOSCSensors.proxDist;
                        String time = ", timestamp: " + VideOSCSensors.proxTime;
                        String accuracy = ", accuracy: " + VideOSCSensors.proxAcc;
                        VideOSCSensors.sensorsInUse.put("prox", "proximity sensor - " + proximity + time + accuracy);
                    }
                    oscProx = VideOSCOscHandling.makeMessage(oscProx, "/" + VideOSC.rootCmd + "/prox");
                    oscProx.add(VideOSCSensors.proxDist).add(VideOSCSensors.proxTime).add(VideOSCSensors.proxAcc);
                    VideOSC.oscP5.send(oscProx, VideOSC.broadcastLoc);
                    proximityLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String args[]) {
        if (proximityThread == null) {
            proximityThread = new Thread(new VideOSCProximityRunnable());
            proximityThread.start();
        }
    }
}
