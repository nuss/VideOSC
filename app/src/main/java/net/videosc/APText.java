package net.videosc;

import android.view.Gravity;
import android.widget.TextView;

import apwidgets.APTextView;
import apwidgets.APWidgetContainer;
import processing.core.PApplet;

/**
 * Package: net.videosc
 * Created by Stefan Nussbaumer on Okt 05 2016, 12:01.
 */
class APText extends APTextView {
	private String TAG = "APText";

	/**
	 * Creates a new non-editable text field.
	 * @param x The text field's x position.
	 * @param y The text field's y position.
	 * @param width The text field's width.
	 * @param height The text field's height.
	 */
	APText(int x, int y, int width, int height) {
		super(x, y, width, height, "");
		this.shouldNotSetOnClickListener = true; //otherwise ime options done, next etc doesn't work
	}

	/**
	 * Initializes the text field. Is called by {@link APWidgetContainer}
	 * as it is added to it.
	 *
	 */
	public void init(PApplet pApplet) {
		this.pApplet = pApplet;

		if (view == null) {
			view = new TextView(pApplet);
		}

		super.init(pApplet);
	}

	/**
	 * Hack: set horizontal alignment
	 * Must be called after addWidget
	 */
	void setTextCentered() {
		((TextView) view).setGravity(Gravity.CENTER_HORIZONTAL);
	}
}
