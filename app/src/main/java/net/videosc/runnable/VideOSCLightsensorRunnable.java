package net.videosc.runnable;

import net.videosc.VideOSC;
import net.videosc.VideOSCOscHandling;
import net.videosc.VideOSCSensors;

import oscP5.OscMessage;

/**
 * Created by stefan on 07.01.17.
 */

public class VideOSCLightsensorRunnable implements Runnable {
	private static Thread lightsensorThread;
	public final static Object lightsensorLock = new Object();

	private static OscMessage oscLight;

	private final static String TAG = "VideOSCLightsensorRunnable";

    @Override
    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
	        synchronized (lightsensorLock) {
		        try {
			        if (VideOSCSensors.useLight && VideOSC.sensors.isLightAvailable()) {
				        oscLight = VideOSCOscHandling.makeMessage(oscLight, "/" + VideOSC.rootCmd + "/light");
				        oscLight.add(VideOSCSensors.lightIntensity).add(VideOSCSensors.lightTime).add(VideOSCSensors.lightAcc);
				        VideOSC.oscP5.send(oscLight, VideOSC.broadcastLoc);
			        }
			        lightsensorLock.wait();
		        } catch (InterruptedException e) {
			        e.printStackTrace();
		        }
	        }
        }
    }

    public static void main(String args[]) {
	    if (lightsensorThread == null) {
		    lightsensorThread = new Thread(new VideOSCLightsensorRunnable());
		    lightsensorThread.start();
	    }
    }
}
