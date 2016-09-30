package net.videosc;

import ketai.ui.KetaiAlertDialog;
import processing.core.PApplet;

/**
 * Created by stefan on 04.02.16.
 */
public class VideOSCDB extends VideOSC {
	private static String TAG = "VideOSCDB";

	private static String CREATE_NETWORK_SETTINGS_SQL = "CREATE TABLE vosc_connect_data ("
			+ "host_ip TEXT NOT NULL DEFAULT '" + sendAddr + "', "
			+ "host_port INTEGER NOT NULL DEFAULT '" + broadcastPort + "', "
			+ "receive_port INTEGER NOT NULL DEFAULT '" + listenPort + "', "
			+ "root_cmd TEXT NOT NULL DEFAULT '" + rootCmd + "');";
	private static String CREATE_RESOLUTION_SETUP_SQL = "CREATE TABLE vosc_resolution_setup ("
			+ "res_w INTEGER NOT NULL DEFAULT '" + resW + "', "
			+ "res_h INTEGER NOT NULL DEFAULT '" + resH + "', "
			+ "framerate INTEGER NOT NULL DEFAULT '" + framerate + "', "
			+ "calc_period INTEGER NOT NULL DEFAULT '" + calcsPerPeriod + "',"
			+ "normalisation INTEGER NOT NULL DEFAULT '" + normalize + "');";

	static boolean setUpNetworkSettings(PApplet applet) {
		boolean success = false;

		if (db.connect()) {
//			if (db.execute("DROP TABLE vosc_connect_data"))
//				Log.d(TAG, "Successfully deleted table 'vosc_connect_data'");
			if (!db.tableExists("vosc_connect_data")) {
				success = db.execute(CREATE_NETWORK_SETTINGS_SQL);

				if (success) {
					success = db.execute("INSERT into vosc_connect_data (`host_ip`, `host_port`, " +
							"`receive_port`, `root_cmd` ) VALUES ('"
							+ sendAddr
							+ "', '"
							+ broadcastPort
							+ "', '"
							+ listenPort
							+ "', '"
							+ rootCmd
							+ "');");
					if (!success) {
						KetaiAlertDialog.popup(applet, "SQL Error", "Entering initial network " +
								"settings into database failed");
					}
				} else {
					KetaiAlertDialog.popup(applet, "SQL Error", "Creating database table for " +
							"network settings failed");
				}
			} else {
				success = db.query("SELECT * FROM vosc_connect_data;");
				if (success) {
					while (db.next()) {
						listenPort = db.getInt("receive_port");
						sendAddr = db.getString("host_ip");
						broadcastPort = db.getInt("host_port");
						rootCmd = db.getString("root_cmd");
					}
				}
			}

		}

		return success;
	}

	static boolean setUpResolutionSettings(PApplet applet) {
		boolean success = false;

		if (db.connect()) {
//			db.execute("DROP TABLE vosc_resolution_setup;");
			if (!db.tableExists("vosc_resolution_setup")) {
				success = db.execute(CREATE_RESOLUTION_SETUP_SQL);

				if (success) {
					success = db.execute("INSERT into vosc_resolution_setup (`res_w`, `res_h`, " +
							"`framerate`, " +
							"`calc_period`, `normalisation`) VALUES ('"
							+ resW
							+ "', '"
							+ resH
							+ "', '"
							+ framerate
							+ "', '"
							+ calcsPerPeriod
							+ "', '"
							+ (normalize ? 1 : 0) + "');");
					if (!success) {
						KetaiAlertDialog.popup(applet, "SQL Error", "Entering initial resolution " +
								"settings into database failed");
					}
				} else {
					KetaiAlertDialog.popup(applet, "SQL Error", "Creating database table for " +
							"resolution settings failed");
				}
			} else {
				success = db.query("SELECT * FROM vosc_resolution_setup;");
				if (success) {
					while (db.next()) {
						resW = db.getInt("res_w");
						resH = db.getInt("res_h");
						framerate = db.getInt("framerate");
						calcsPerPeriod = db.getInt("calc_period");
						normalize = db.getInt("normalisation") > 0;
					}
				}
			}
		}

		return success;
	}

	static void setUpSnapshots(PApplet applet) {
		boolean success = false;

		if (db.connect()) {
//			if (db.execute("DROP TABLE vosc_snapshots;"))
//				Log.d(TAG, "Successfully deleted table 'vosc_snapshots'");
			String CREATE_SNAPSHOTS_TABLE = "CREATE TABLE vosc_snapshots (" +
					"date TEXT NOT NULL, pattern TEXT NOT NULL);";

			if (!db.tableExists("vosc_snapshots")) {
				success = db.execute(CREATE_SNAPSHOTS_TABLE);
				if (!success) {
					KetaiAlertDialog.popup(applet, "SQL Error", "Creating database table for " +
							"snapshots failed");
				}
			}
		}
	}

	static boolean updateNetworkSettings() {
		boolean success = false;

		if (db.connect()) {
			success = db.execute("UPDATE vosc_connect_data SET host_ip='" + sendAddr
					+ "', host_port=" + broadcastPort + ", receive_port="
					+ listenPort + ", root_cmd='" + rootCmd + "';");
		}

		return success;
	}

	static boolean updateResolutionSettings() {
		boolean success = false;

		if (db.connect()) {
			success = db.execute("UPDATE vosc_resolution_setup " +
					"SET res_w=" + resW
					+ ", res_h=" + resH + ", framerate=" + framerate
					+ ", calc_period=" + calcsPerPeriod + ", normalisation=" + (normalize ? 1 : 0)
					+ ";");
		}

		return success;
	}

	static boolean addSnapshot(PApplet applet) {
		boolean success = false;

		if (db.connect()) {
			// we simply store a snapshot as a string in the database. e.g.: "010110111010"
			// the size of the string must be a multiple of 3 (RGB)
			String offs = "";

			for (Boolean[] slot : offPxls) {
				for (int i = 0; i < 3; i++)
					offs += slot[i] ? 1 : 0;
			}

			// store the snapshot under a key represented by the current datetime
			success = db.execute("INSERT INTO vosc_snapshots (`date`, `pattern`) VALUES" +
						"(datetime(),'" + offs + "');");

			if (!success) {
				KetaiAlertDialog.popup(applet, "SQL Error", "Writing the snapshot to the database" +
						" failed");
			} else {
				numSnapshots++;
			}
		}

		return success;
	}

	static String[] getSnapshotKeys() {
		boolean success;
		String[] snapshotKeys = {};

		if (db.connect()) {
			success = db.query("SELECT date FROM vosc_snapshots");

			if (success) {
				while (db.next()) {
					snapshotKeys = append(snapshotKeys, db.getString("date"));
				}
			}
		}

		return snapshotKeys;
	}

	static void selectSnapshot(PApplet applet, String snapshot) {
		boolean success;
		String res;
		int slot = 0;

		if (db.connect()) {
			success = db.query("SELECT pattern FROM vosc_snapshots WHERE date='" + snapshot + "'");

			if (success) {
				while (db.next()) {
					res = db.getString("pattern");
					for (int i = 0; i < res.length(); i++) {
						if (i % 3 == 0) {
							// a snapshot is an ArrayList of boolean tripplets
							// we simply add the result of a non-equality check applied on the
							// values stored in the db
							// 0 is unique code number 48
							offPxls.get(slot)[0] = (int) res.charAt(i) != 48;
							offPxls.get(slot)[1] = (int) res.charAt(i + 1) != 48;
							offPxls.get(slot)[2] = (int) res.charAt(i + 2) != 48;
						} else if (i % 3 == 2) slot++;
					}
				}
			} else {
				KetaiAlertDialog.popup(applet, "SQL Error", "The snapshot couldn't be determined");
			}
		}
	}

	static void resetSnapshots(PApplet applet) {
		boolean success;
		String query;

		if (db.connect()) {
			query = "DELETE FROM vosc_snapshots";
			success = db.execute(query);
			if (success) db.execute("VACUUM");

			if (!success) {
				KetaiAlertDialog.popup(applet, "SQL Error", "Deleting snapshots failed");
			} else {
				numSnapshots = 0;
			}
		}
	}

	static void countSnapshots(PApplet applet) {
		if (db.connect()) {
			numSnapshots = db.getRecordCount("vosc_snapshots");
		} else {
			numSnapshots = 0;
			KetaiAlertDialog.popup(applet, "SQL Error", "The number of snapshots could not be " +
					"determined");
		}
	}
}
