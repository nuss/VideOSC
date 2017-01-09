package net.videosc.runnable;

import net.videosc.VideOSC;
import net.videosc.VideOSCOscHandling;
import net.videosc.VideOSCSensors;

import oscP5.OscMessage;

/**
 * Created by stefan on 07.01.17.
 */

public class VideOSCAccelerationRunnable implements Runnable {
    private static Thread accelerometerThread;
    public static final Object accelerometerLock = new Object();
	//	print values to screen;
	public static String info;

	private static OscMessage oscAcc;

	private final static String TAG = "VideOSCAccelerationRunnable";

    @Override
    public void run() {
        //noinspection InfiniteLoopStatement
        while (true) {
            synchronized (accelerometerLock) {
                try {
	                if (VideOSC.printSensors) {
		                String xVal = "x: " + VideOSCSensors.accX;
		                String yVal = ", y: " + VideOSCSensors.accY;
		                String zVal = ", z: " + VideOSCSensors.accZ;
		                String time = ", timestamp: " + VideOSCSensors.accTime;
		                String accuracy = ", accuracy: " + VideOSCSensors.accAcc;
		                VideOSCSensors.sensorsInUse.put("acc", "accelerometer - " + xVal + yVal + zVal + time + accuracy);
//		                info = "accelerometer - " + xVal + yVal + zVal + time + accuracy;
	                }
                    oscAcc = VideOSCOscHandling.makeMessage(oscAcc, "/" + VideOSC.rootCmd + "/acc");
                    oscAcc.add(VideOSCSensors.accX).add(VideOSCSensors.accY).add(VideOSCSensors.oriZ).add(VideOSCSensors.accTime).add(VideOSCSensors.accAcc);
                    VideOSC.oscP5.send(oscAcc, VideOSC.broadcastLoc);
                    accelerometerLock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String args[]) {
        if (accelerometerThread == null) {
            accelerometerThread = new Thread(new VideOSCAccelerationRunnable());
            accelerometerThread.start();
        }
    }}
