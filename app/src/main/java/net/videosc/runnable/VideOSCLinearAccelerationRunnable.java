package net.videosc.runnable;

import net.videosc.VideOSC;
import net.videosc.VideOSCOscHandling;
import net.videosc.VideOSCSensors;

import oscP5.OscMessage;

/**
 * Created by stefan on 07.01.17.
 */

public class VideOSCLinearAccelerationRunnable implements Runnable {
	private static Thread linearAccelerationThread;
	public final static Object linearAccelerationLock = new Object();
	//	print values to screen
	public volatile static String info;

	private static OscMessage oscLinAcc;

	private final static String TAG = "VideOSCLinearAccelerationRunnable";

    @Override
    public void run() {
	    //noinspection InfiniteLoopStatement
	    while (true) {
		    synchronized (linearAccelerationLock) {
			    try {
				    if (VideOSC.printSensors) {
					    String xVal = "x: " + VideOSCSensors.linAccX;
					    String yVal = ", y: " + VideOSCSensors.linAccY;
					    String zVal = ", z: " + VideOSCSensors.linAccZ;
					    String time = ", timestamp: " + VideOSCSensors.linAccTime;
					    String accuracy = ", accuracy: " + VideOSCSensors.linAccAcc;
					    VideOSCSensors.sensorsInUse.put("linAcc", "linear acceleration - " + xVal + yVal + zVal + time + accuracy);
//					    info = "linear acceleration - " + xVal + yVal + zVal + time + accuracy;
				    }
				    oscLinAcc = VideOSCOscHandling.makeMessage(oscLinAcc, "/" + VideOSC.rootCmd + "/lin_acc");
				    oscLinAcc.add(VideOSCSensors.linAccX).add(VideOSCSensors.linAccY).add(VideOSCSensors.linAccZ).add(VideOSCSensors.linAccTime).add(VideOSCSensors.linAccAcc);
				    VideOSC.oscP5.send(oscLinAcc, VideOSC.broadcastLoc);
				    linearAccelerationLock.wait();
			    } catch (InterruptedException e) {
				    e.printStackTrace();
			    }
		    }
	    }
    }

    public static void main(String args[]) {
	    if (linearAccelerationThread == null) {
		    linearAccelerationThread = new Thread(new VideOSCLinearAccelerationRunnable());
		    linearAccelerationThread.start();
	    }
    }
}
