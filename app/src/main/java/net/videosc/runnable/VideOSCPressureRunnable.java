package net.videosc.runnable;

import net.videosc.VideOSC;
import net.videosc.VideOSCOscHandling;
import net.videosc.VideOSCSensors;

import oscP5.OscMessage;

/**
 * Created by stefan on 07.01.17.
 */

public class VideOSCPressureRunnable implements Runnable {
	private static Thread pressureThread;
	public final static Object pressureLock = new Object();

	private static OscMessage oscPress;

	private final static String TAG = "VideOSCPressureRunnable";

	@Override
	public void run() {
		//noinspection InfiniteLoopStatement
		while (true) {
			synchronized (pressureLock) {
				try {
					if (VideOSC.printSensors) {
						String pressure = "pressure: " + VideOSCSensors.pressIntensity;
						String time = ", timestamp: " + VideOSCSensors.pressTime;
						String accuracy = ", accuracy: " + VideOSCSensors.pressAcc;
						VideOSCSensors.sensorsInUse.put("press", "air pressure sensor - " + pressure + time + accuracy + "hPa/mBar");
					}
					oscPress = VideOSCOscHandling.makeMessage(oscPress, "/" + VideOSC.rootCmd + "/press");
					oscPress.add(VideOSCSensors.pressIntensity).add(VideOSCSensors.pressTime).add(VideOSCSensors.pressAcc);
					VideOSC.oscP5.send(oscPress, VideOSC.broadcastLoc);
					pressureLock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String args[]) {
		if (pressureThread == null) {
			pressureThread = new Thread(new VideOSCPressureRunnable());
			pressureThread.start();
		}
	}
}
