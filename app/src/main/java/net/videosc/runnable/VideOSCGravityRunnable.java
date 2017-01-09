package net.videosc.runnable;

import net.videosc.VideOSC;
import net.videosc.VideOSCOscHandling;
import net.videosc.VideOSCSensors;

import oscP5.OscMessage;

/**
 * Created by stefan on 07.01.17.
 */

public class VideOSCGravityRunnable implements Runnable {
    private static Thread gravityThread;
    public static final Object gravityLock = new Object();
	//	print values to screen
	public volatile static String info;

    private static OscMessage oscGrav;

    private final static String TAG = "VideOSCGravityRunnable";

    @Override
    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            synchronized (gravityLock) {
                try {
	                if (VideOSC.printSensors) {
		                String xVal = "x: " + VideOSCSensors.gravX;
		                String yVal = ", y: " + VideOSCSensors.gravY;
		                String zVal = ", z: " + VideOSCSensors.gravZ;
		                String time = ", timestamp: " + VideOSCSensors.gravTime;
		                String accuracy = ", accuracy: " + VideOSCSensors.gravAcc;
		                VideOSCSensors.sensorsInUse.put("grav", "gravity sensor - " + xVal + yVal + zVal + time + accuracy);
//		                info = "gravity sensor - " + xVal + yVal + zVal + time + accuracy;
	                }
                    oscGrav = VideOSCOscHandling.makeMessage(oscGrav, "/" + VideOSC.rootCmd + "/grav");
                    oscGrav.add(VideOSCSensors.gravX).add(VideOSCSensors.gravY).add(VideOSCSensors.gravZ).add(VideOSCSensors.gravTime).add(VideOSCSensors.gravAcc);
	                VideOSC.oscP5.send(oscGrav, VideOSC.broadcastLoc);
                    gravityLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String args[]) {
        if (gravityThread == null) {
            gravityThread = new Thread(new VideOSCGravityRunnable());
            gravityThread.start();
        }
    }
}
