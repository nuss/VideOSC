package net.videosc;

import processing.core.PApplet;

/**
 * Package: ${PACKAGE_NAME}
 * Last modified by Stefan Nussbaumer on 09.02.16, 11:41.
 */
class PreloaderSquare {
	private String TAG = "PreloaderSquare";

	PreloaderSquare(PApplet applet, int x, int y, int alpha) {
		applet.noStroke();
		applet.fill((int) applet.random(256), (int) applet.random(256), (int) applet.random(256),
				alpha);
		applet.rect(x, y, VideOSCUI.dc(50), VideOSCUI.dc(50));
	}
}