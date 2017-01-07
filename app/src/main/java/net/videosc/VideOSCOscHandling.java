package net.videosc;

import oscP5.OscMessage;

/**
 * Created by stefan on 30.12.16.
 */

public class VideOSCOscHandling extends VideOSC {
    public static OscMessage makeMessage(OscMessage msg, String cmd) {

        if (msg == null)
            msg = new OscMessage(cmd);
        else {
            msg.clear();
            msg.setAddrPattern(cmd);
        }

        return msg;
    }

    static void prepareFeedbackStrings(OscMessage fbMessage) {
        if (!rgbMode.equals(RGBModes.RGB)) {
            String[] msg = {fbMessage.get(0).stringValue()};
            String chanSlot;
            // make as sure as possible no incoming messages except the ones
            // coming from VideOSC are checked
            if (showFB && match(fbMessage.addrPattern(),
                    "^/[a-zA-Z0-9_/]+/(red|green|blue)[0-9]+") != null) {
                if (match(fbMessage.addrPattern().split("/")[2],
                        "(red)([0-9]+)") != null) {
                    chanSlot = join(
                            concat(msg,
                                    match(fbMessage.addrPattern().split("/")[2],
                                            "(red)([0-9]+)")), ";");
                    rCmds.add(chanSlot);
                } else if (match(fbMessage.addrPattern().split("/")[2],
                        "(green)([0-9]+)") != null) {
                    chanSlot = join(
                            concat(msg,
                                    match(fbMessage.addrPattern().split("/")[2],
                                            "(green)([0-9]+)")), ";");
                    gCmds.add(chanSlot);
                } else if (match(fbMessage.addrPattern().split("/")[2],
                        "(blue)([0-9]+)") != null) {
                    chanSlot = join(
                            concat(msg,
                                    match(fbMessage.addrPattern().split("/")[2],
                                            "(blue)([0-9]+)")), ";");
                    bCmds.add(chanSlot);
                }
            }
        }
    }
}
