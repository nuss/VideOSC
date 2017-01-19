package net.videosc;

import java.util.ArrayList;

import oscP5.OscMessage;
import processing.core.PApplet;

/**
 * Created by stefan on 20.01.17.
 */

public class VideOSCImageHandling extends VideOSC {
	// OSC messages to be sent to the remote client
	private static OscMessage oscR;
	private static OscMessage oscG;
	private static OscMessage oscB;

	// pixel bookkeeping
	private static float[] curInput = new float[3];
	static ArrayList<float[]> lastInputList = new ArrayList<float[]>();
	static ArrayList<float[]> curInputList = new ArrayList<float[]>();
	private static float[] slope = new float[3];
	static ArrayList<float[]> slopes = new ArrayList<float[]>();

	static void drawFrame(PApplet applet) {
		float rval, gval, bval, alpha;

		for (int i = 0; i < dimensions; i++) {
			// only the downsampled image gets inverted as inverting the original would slow
			// down the application considerably
			int rVal = (negative) ? 0xFF - ((pImg.pixels[i] >> 16) & 0xFF)
					: (pImg.pixels[i] >> 16) & 0xFF;
			int gVal = (negative) ? 0xFF - ((pImg.pixels[i] >> 8) & 0xFF)
					: (pImg.pixels[i] >> 8) & 0xFF;
			int bVal = (negative) ? 0xFF - (pImg.pixels[i] & 0xFF)
					: pImg.pixels[i] & 0xFF;

			if (negative)
				pImg.pixels[i] = applet.color(rVal, gVal, bVal, 255);

			// compose basic OSC message for slot
			oscR = VideOSCOscHandling.makeMessage(oscR, r + str(i + 1));
			oscG = VideOSCOscHandling.makeMessage(oscG, g + str(i + 1));
			oscB = VideOSCOscHandling.makeMessage(oscB, b + str(i + 1));

			if (rgbMode.equals(RGBModes.RGB)) {
				if (offPxls.get(i)[0] && !offPxls.get(i)[1] && !offPxls.get(i)[2]) {
					// r
					alpha = cam.isStarted() ? 255 / 3 : 0;
					pImg.pixels[i] = applet.color(0, gVal, bVal, alpha);
				} else if (!offPxls.get(i)[0] && offPxls.get(i)[1] && !offPxls.get(i)[2]) {
					// g;
					alpha = cam.isStarted() ? 255 / 3 : 0;
					pImg.pixels[i] = applet.color(rVal, 0, bVal, alpha);
				} else if (!offPxls.get(i)[0] && !offPxls.get(i)[1] && offPxls.get(i)[2]) {
					// b;
					alpha = cam.isStarted() ? 255 / 3 : 0;
					pImg.pixels[i] = applet.color(rVal, gVal, 0, alpha);
				} else if (offPxls.get(i)[0] && offPxls.get(i)[1] && !offPxls.get(i)[2]) {
					// rg;
					alpha = cam.isStarted ? 255 / 3 * 2 : 0;
					pImg.pixels[i] = applet.color(0, 0, bVal, alpha);
				} else if (offPxls.get(i)[0] && !offPxls.get(i)[1] && offPxls.get(i)[2]) {
					// rb;
					alpha = cam.isStarted() ? 255 / 3 * 2 : 0;
					pImg.pixels[i] = applet.color(0, gVal, 0, alpha);
				} else if (!offPxls.get(i)[0] && offPxls.get(i)[1] && offPxls.get(i)[2]) {
					// bg;
					alpha = cam.isStarted() ? 255 / 3 * 2 : 0;
					pImg.pixels[i] = applet.color(rVal, 0, 0, alpha);
				} else if (offPxls.get(i)[0] && offPxls.get(i)[1] && offPxls.get(i)[2]) {
					// rgb
					pImg.pixels[i] = applet.color(0, 0);
				}
			} else if (rgbMode.equals(RGBModes.R)) {
				if (offPxls.get(i)[0])
					pImg.pixels[i] = applet.color(rVal, 255, 255);
				else
					pImg.pixels[i] = applet.color(rVal, 0, 0);
			} else if (rgbMode.equals(RGBModes.G)) {
				if (offPxls.get(i)[1])
					pImg.pixels[i] = applet.color(255, gVal, 255);
				else
					pImg.pixels[i] = applet.color(0, gVal, 0);
			} else if (rgbMode.equals(RGBModes.B)) {
				if (offPxls.get(i)[2])
					pImg.pixels[i] = applet.color(255, 255, bVal);
				else
					pImg.pixels[i] = applet.color(0, 0, bVal);
			}

			if (play) {
				if (calcsPerPeriod == 1) {
					if (normalize) {
						rval = (float) rVal / 255;
						gval = (float) gVal / 255;
						bval = (float) bVal / 255;
					} else {
						rval = rVal;
						gval = gVal;
						bval = bVal;
					}

					if (!offPxls.get(i)[0]) {
						oscR.add(rval);
						oscP5.send(oscR, broadcastLoc);
					}
					if (!offPxls.get(i)[1]) {
						oscR.add(gval);
						oscP5.send(oscG, broadcastLoc);
					}
					if (!offPxls.get(i)[2]) {
						oscR.add(bval);
						oscP5.send(oscB, broadcastLoc);
					}
				} else {
					curInput[0] = (float) rVal;
					curInput[1] = (float) gVal;
					curInput[2] = (float) bVal;

					curInputList.add(curInput.clone());

					if (lastInputList.size() >= dimensions) {
						rval = lastInputList.get(i)[0];
						gval = lastInputList.get(i)[1];
						bval = lastInputList.get(i)[2];

						if (normalize) {
							rval = rval / 255;
							gval = gval / 255;
							bval = bval / 255;
						}

						if (!offPxls.get(i)[0]) {
							oscR.add(rval);
							oscP5.send(oscR, broadcastLoc);
						}
						if (!offPxls.get(i)[1]) {
							oscG.add(gval);
							oscP5.send(oscG, broadcastLoc);
						}
						if (!offPxls.get(i)[2]) {
							oscB.add(bval);
							oscP5.send(oscB, broadcastLoc);
						}

						float lastInputR = lastInputList.get(i)[0];
						float lastInputG = lastInputList.get(i)[1];
						float lastInputB = lastInputList.get(i)[2];

						slope[0] = (curInput[0] - lastInputR) / calcsPerPeriod;
						slope[1] = (curInput[1] - lastInputG) / calcsPerPeriod;
						slope[2] = (curInput[2] - lastInputB) / calcsPerPeriod;

						slopes.add(slope.clone());
					}
				}
			}
		}

	}

	static void interpolatedFrames(int index) {
		for (int i = 0; i < dimensions; i++) {
			if (!offPxls.get(i)[0]) {
				oscR = VideOSCOscHandling.makeMessage(oscR, r + str(i + 1));
				float lastInputR = lastInputList.get(i)[0];
				float slopeR = slopes.get(i)[0];
				if (normalize)
					oscR.add((slopeR * index + lastInputR) / 255);
				else
					oscR.add(slopeR * index + lastInputR);
				oscP5.send(oscR, broadcastLoc);
			}
			if (!offPxls.get(i)[1]) {
				oscG = VideOSCOscHandling.makeMessage(oscG, g + str(i + 1));
				float lastInputG = lastInputList.get(i)[1];
				float slopeG = slopes.get(i)[1];
				if (normalize)
					oscG.add((slopeG * index + lastInputG) / 255);
				else
					oscG.add(slopeG * index + lastInputG);
				oscP5.send(oscG, broadcastLoc);
			}
			if (!offPxls.get(i)[2]) {
				oscB = VideOSCOscHandling.makeMessage(oscB, b + str(i + 1));
				float lastInputB = lastInputList.get(i)[2];
				float slopeB = slopes.get(i)[2];
				if (normalize)
					oscB.add((slopeB * index + lastInputB) / 255);
				else
					oscB.add(slopeB * index + lastInputB);
				oscP5.send(oscB, broadcastLoc);
			}
		}

	}
 }
