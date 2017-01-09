package net.videosc.runnable;

import net.videosc.VideOSC;
import net.videosc.VideOSCOscHandling;
import net.videosc.VideOSCSensors;

import oscP5.OscMessage;

/**
 * Created by stefan on 07.01.17.
 */

public class VideOSCTemperatureRunnable implements Runnable {
	private static Thread temperatureThread;
	public final static Object temperatureLock = new Object();

	private static OscMessage oscTemp;

	private final static String TAG = "VideOSCTemperatureRunnable";

	@Override
	public void run() {
		//noinspection InfiniteLoopStatement
		while (true) {
			synchronized (temperatureLock) {
				try {
					if (VideOSC.printSensors) {
						String temperature = "temperature: " + VideOSCSensors.tempCels;
						VideOSCSensors.sensorsInUse.put("temp", "temperature sensor - " + temperature + "°C");
					}
					oscTemp = VideOSCOscHandling.makeMessage(oscTemp, "/" + VideOSC.rootCmd + "/temp");
					oscTemp.add(VideOSCSensors.tempCels);
					VideOSC.oscP5.send(oscTemp, VideOSC.broadcastLoc);
					temperatureLock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String args[]) {
		if (temperatureThread == null) {
			temperatureThread = new Thread(new VideOSCTemperatureRunnable());
			temperatureThread.start();
		}
	}
}
