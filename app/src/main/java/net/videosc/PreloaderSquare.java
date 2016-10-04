package net.videosc;

import processing.core.PApplet;

/**
 * Created by Stefan Nussbaumer on 09.02.16.
 */
class PreloaderSquare {
	private String TAG = "PreloaderSquare";

	private int x;
	private int y;
	private int alpha;

	PreloaderSquare(PApplet applet, int x, int y, int alpha) {
		this.x = x;
		this.y = y;
		this.alpha = alpha;

		applet.noStroke();
		applet.fill((int) applet.random(256), (int) applet.random(256), (int) applet.random(256),
				this.alpha);
		applet.rect(this.x, this.y, 50, 50);
	}
}
