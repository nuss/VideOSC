package net.videosc;

import oscP5.OscMessage;

/**
 * Created by stefan on 30.12.16.
 */

public class VideOSCOscHandling extends VideOSC {
    static OscMessage makeMessage(OscMessage msg, String cmd) {

        if (msg == null)
            msg = new OscMessage(cmd);
        else {
            msg.clear();
            msg.setAddrPattern(cmd);
        }

        return msg;
    }

    static void oscAdd(OscMessage msg, float val) {
        msg.add(val);
    }

    static void oscAdd(OscMessage msg, String val) {
        msg.add(val);
    }

    static void oscAdd(OscMessage msg, int val) {
        msg.add(val);
    }

    static void oscAdd(OscMessage msg, long val) {
        msg.add(val);
    }
}
