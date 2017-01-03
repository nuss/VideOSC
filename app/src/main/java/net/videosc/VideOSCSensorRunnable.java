package net.videosc;

import android.os.StrictMode;
import android.util.Log;

import oscP5.OscMessage;

/**
 * Created by stefan on 24.12.16.
 */

public class VideOSCSensorRunnable implements Runnable {
    private static OscMessage oscOri;
    private static OscMessage oscAcc;
    private static OscMessage oscMag;
    private static OscMessage oscGrav;
    private static OscMessage oscProx;
    private static OscMessage oscLight;
    private static OscMessage oscPress;
    private static OscMessage oscTemp;
    private static OscMessage oscLinAcc;
    private static OscMessage oscHum;

    private static Thread sensorThread;
    private final String TAG = "VideOSCSensorRunnable";

    @Override
    public void run() {
//        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        //noinspection InfiniteLoopStatement
        while (true) {
            try {
                if (VideOSCSensors.useOri && VideOSC.sensors.isOrientationAvailable()) {
                    oscOri = VideOSCOscHandling.makeMessage(oscOri, "/" + VideOSC.rootCmd + "/ori");
                    oscOri.add(VideOSCSensors.oriX).add(VideOSCSensors.oriY).add(VideOSCSensors.oriZ).add(VideOSCSensors.oriTime).add(VideOSCSensors.oriAcc);
                    VideOSC.oscP5.send(oscOri, VideOSC.broadcastLoc);
                }
                if (VideOSCSensors.useAcc && VideOSC.sensors.isAccelerometerAvailable()) {
                    oscAcc = VideOSCOscHandling.makeMessage(oscAcc, "/" + VideOSC.rootCmd + "/acc");
                    oscAcc.add(VideOSCSensors.accX).add(VideOSCSensors.accY).add(VideOSCSensors.oriZ).add(VideOSCSensors.accTime).add(VideOSCSensors.accAcc);
                    VideOSC.oscP5.send(oscAcc, VideOSC.broadcastLoc);
                }
                if (VideOSCSensors.useMag && VideOSC.sensors.isMagenticFieldAvailable()) {
                    oscMag = VideOSCOscHandling.makeMessage(oscMag, "/" + VideOSC.rootCmd + "/mag");
                    oscMag.add(VideOSCSensors.magX).add(VideOSCSensors.magY).add(VideOSCSensors.magZ).add(VideOSCSensors.magTime).add(VideOSCSensors.magAcc);
                    VideOSC.oscP5.send(oscMag, VideOSC.broadcastLoc);
                }
                if (VideOSCSensors.useGrav) { // no test if gravity is available
                    oscGrav = VideOSCOscHandling.makeMessage(oscGrav, "/" + VideOSC.rootCmd + "/grav");
                    oscGrav.add(VideOSCSensors.gravX).add(VideOSCSensors.gravY).add(VideOSCSensors.gravZ).add(VideOSCSensors.gravTime).add(VideOSCSensors.gravAcc);
                    VideOSC.oscP5.send(oscGrav, VideOSC.broadcastLoc);
                }
                if (VideOSCSensors.useLinAcc && VideOSC.sensors.isLinearAccelerationAvailable()) {
                    oscLinAcc = VideOSCOscHandling.makeMessage(oscLinAcc, "/" + VideOSC.rootCmd + "/lin_acc");
                    oscLinAcc.add(VideOSCSensors.linAccX).add(VideOSCSensors.linAccY).add(VideOSCSensors.linAccZ).add(VideOSCSensors.linAccTime).add(VideOSCSensors.linAccAcc);
                    VideOSC.oscP5.send(oscLinAcc, VideOSC.broadcastLoc);
                }
                if (VideOSCSensors.useProx && VideOSC.sensors.isProximityAvailable()) {
                    oscProx = VideOSCOscHandling.makeMessage(oscProx, "/" + VideOSC.rootCmd + "/prox");
                    oscProx.add(VideOSCSensors.proxDist).add(VideOSCSensors.proxTime).add(VideOSCSensors.proxAcc);
                    VideOSC.oscP5.send(oscProx, VideOSC.broadcastLoc);
                }
                if (VideOSCSensors.useLight && VideOSC.sensors.isLightAvailable()) {
                    oscLight = VideOSCOscHandling.makeMessage(oscLight, "/" + VideOSC.rootCmd + "/light");
                    oscLight.add(VideOSCSensors.lightIntensity).add(VideOSCSensors.lightTime).add(VideOSCSensors.lightAcc);
                    VideOSC.oscP5.send(oscLight, VideOSC.broadcastLoc);
                }
                if (VideOSCSensors.usePress && VideOSC.sensors.isPressureAvailable()) {
                    oscPress = VideOSCOscHandling.makeMessage(oscPress, "/" + VideOSC.rootCmd + "/press");
                    oscPress.add(VideOSCSensors.pressIntensity).add(VideOSCSensors.pressTime).add(VideOSCSensors.pressAcc);
                    VideOSC.oscP5.send(oscPress, VideOSC.broadcastLoc);
                }
                if (VideOSCSensors.useTemp && VideOSC.sensors.isAmbientTemperatureAvailable()) {
                    oscTemp = VideOSCOscHandling.makeMessage(oscTemp, "/" + VideOSC.rootCmd + "/temp");
//                    oscTemp.add(VideOSCSensors.tempCels).add(VideOSCSensors.tempTime).add(VideOSCSensors.tempAcc);
                    oscTemp.add(VideOSCSensors.tempCels);
                    VideOSC.oscP5.send(oscTemp, VideOSC.broadcastLoc);
                }
                if (VideOSCSensors.useHum && VideOSC.sensors.isRelativeHumidityAvailable()) {
                    oscHum = VideOSCOscHandling.makeMessage(oscHum, "/" + VideOSC.rootCmd + "/hum");
                    oscHum.add(VideOSCSensors.humVal);
                    VideOSC.oscP5.send(oscHum, VideOSC.broadcastLoc);
                }
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[]) {
        if (sensorThread == null) {
            sensorThread = new Thread(new VideOSCSensorRunnable());
            sensorThread.start();
        }
    }
}
