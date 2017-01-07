package net.videosc.runnable;

import net.videosc.VideOSC;
import net.videosc.VideOSCOscHandling;
import net.videosc.VideOSCSensors;

import oscP5.OscMessage;

/**
 * Created by stefan on 07.01.17.
 */

public class VideOSCHumidityRunnable implements Runnable {
    private static Thread humidityThread;
    public static final Object humidityLock = new Object();

    private static OscMessage oscHum;

	private final static String TAG = "VideOSCHumidityRunnable";

    @Override
    public void run() {
	    //noinspection InfiniteLoopStatement
	    while (true) {
		    synchronized (humidityLock) {
			    try {
				    if (VideOSCSensors.useHum && VideOSC.sensors.isRelativeHumidityAvailable()) {
					    oscHum = VideOSCOscHandling.makeMessage(oscHum, "/" + VideOSC.rootCmd + "/hum");
					    oscHum.add(VideOSCSensors.humVal);
					    VideOSC.oscP5.send(oscHum, VideOSC.broadcastLoc);
				    }
				    humidityLock.wait();
			    } catch (InterruptedException e) {
				    e.printStackTrace();
			    }
		    }
	    }
    }

    public static void main(String args[]) {
	    if (humidityThread == null) {
		    humidityThread = new Thread(new VideOSCHumidityRunnable());
		    humidityThread.start();
	    }
    }
}
