package net.videosc;


import ketai.sensors.KetaiSensor;

/**
 * Created by stefan on 13.12.16.
 */

public class VideOSCSensors extends VideOSC {
    public KetaiSensor init(VideOSC applet) {
        return new KetaiSensor(applet);
    }
}
