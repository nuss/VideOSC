package net.videosc.runnable;

import net.videosc.VideOSC;
import net.videosc.VideOSCOscHandling;
import net.videosc.VideOSCSensors;

import oscP5.OscMessage;

/**
 * Created by stefan on 07.01.17.
 */

public class VideOSCMagnetismRunnable implements Runnable {
    private static Thread magnetismThread;
	public static final Object magnetismLock = new Object();

	private static OscMessage oscMag;

	private final static String TAG = "VideOSCMagnetismRunnable";

    @Override
    public void run() {
	    //noinspection InfiniteLoopStatement
	    while (true) {
		    synchronized (magnetismLock) {
			    try {
				    if (VideOSCSensors.useMag && VideOSC.sensors.isMagenticFieldAvailable()) {
					    oscMag = VideOSCOscHandling.makeMessage(oscMag, "/" + VideOSC.rootCmd + "/mag");
					    oscMag.add(VideOSCSensors.magX).add(VideOSCSensors.magY).add(VideOSCSensors.magZ).add(VideOSCSensors.magTime).add(VideOSCSensors.magAcc);
					    VideOSC.oscP5.send(oscMag, VideOSC.broadcastLoc);
				    }
				    magnetismLock.wait();
			    } catch (InterruptedException e) {
				    e.printStackTrace();
			    }
		    }
	    }
    }

    public static void main(String args[]) {
	    if (magnetismThread == null) {
		    magnetismThread = new Thread(new VideOSCMagnetismRunnable());
		    magnetismThread.start();
	    }
    }
}
