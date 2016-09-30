package net.videosc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import processing.core.PApplet;

/**
 * Created by stefan on 08.02.16.
 */
public class VideOSCPreload {
	String TAG = "VideOSCPreload";

	static ArrayList<int[][]> squareRows = new ArrayList<int[][]>();
	static int width;

	public VideOSCPreload(PApplet applet, int x, int y, int size, float probability) {
		width = size * 50;

		if (squareRows.size() > 0) {
			// create rows for each screen
			// a row is defined by the properties x, y, alpha of each square
			// color is a random color
			for (int[][] row : squareRows) {
				int l = row.length;
				List<int[]> asList;
				Set<int[]> nullSet;

				for (int i = 0; i < l; i++) {
					if (row[i] != null) {
						int newY = row[i][1] - 50;
						int newAlpha = row[i][2] - 25;
						if (newY < -50) {
							row[i] = null;
						} else {
							new PreloaderSquare(applet, row[i][0], newY, newAlpha);
							row[i][1] = newY;
							row[i][2] = newAlpha;
						}
					}
				}

				asList = Arrays.asList(row);
				nullSet = new HashSet<int[]>(asList);

				// nullSet.size() == 1 means only one unique object is in doneRow
				// which should only be the case if all slots are null
				if (nullSet.size() == 1) {
					int index = squareRows.indexOf(row);
					squareRows.remove(index);
				}
			}
		}

		int[][] row = createRow(applet, x, y, size, probability);
		squareRows.add(0, row);

		for (int i = 0; i < size; i++) {
			if (row[i] != null) {
				new PreloaderSquare(applet, row[i][0], row[i][1], row[i][2]);
			}
		}

	}

	private static int[][] createRow(PApplet applet, int x, int y, int size, float probability) {
		int[][] row = new int[size][];

		for (int i = 0; i < size; i++) {
			if (applet.random(10) > probability) {
				// positions for each square in the row
				int[] args = {50 * i + x - (width / 2), y, 255};
				row[i] = args;
			} else  {
				// don't create square
				row[i] = null;
			}
		}
		
		return row;
	}
}
