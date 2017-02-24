package net.videosc;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import ketai.data.KetaiSQLite;
import ketai.ui.KetaiAlertDialog;
import processing.core.PApplet;

/**
 * Package: net.videosc
 * Created by Stefan Nussbaumer on Okt 05 2016, 12:02.
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
			+ "normalisation INTEGER NOT NULL DEFAULT '" + normalize + "',"
			+ "saved_snapshot TEXT DEFAULT NULL,"
			+ "save_snapshot_on_close INTEGER NOT NULL DEFAULT 0);";

	static boolean setUpNetworkSettings(PApplet applet, KetaiSQLite db) {
		boolean success = false;

		if (db.connect()) {
//			if (db.execute("DROP TABLE vosc_connect_data"))
//				Log.d(TAG, "Successfully deleted table 'vosc_connect_data'");
			if (!db.tableExists("vosc_connect_data")) {
				success = db.execute(CREATE_NETWORK_SETTINGS_SQL);

				if (success) {
					success = db.execute("INSERT INTO vosc_connect_data (`host_ip`, `host_port`, " +
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

	static boolean setUpResolutionSettings(PApplet applet, KetaiSQLite db) {
		boolean success = false;

		if (db.connect()) {
//			db.execute("DROP TABLE vosc_resolution_setup;");
			if (!db.tableExists("vosc_resolution_setup")) {
				success = db.execute(CREATE_RESOLUTION_SETUP_SQL);

				if (success) {
					success = db.execute("INSERT INTO vosc_resolution_setup (`res_w`, `res_h`, " +
							"`framerate`, `calc_period`, `normalisation`," +
							"`save_snapshot_on_close`) VALUES ('"
							+ resW
							+ "', '"
							+ resH
							+ "', '"
							+ framerate
							+ "', '"
							+ calcsPerPeriod
							+ "', '"
							+ (normalize ? 1 : 0)
							+ "', '"
							+ 0
							+ "');");
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
						snapshotSavedOnClose = db.getString("saved_snapshot");
						saveSnapshotOnClose = db.getInt("save_snapshot_on_close") > 0;
					}
					// define preliminary dimensions
					dimensions = resH * resW;
				}
			}
		}

		return success;
	}

	static void addActiveSnapshotColumn(PApplet applet, KetaiSQLite db) {
		String[] result;
		boolean success;

		if (db.connect() && db.tableExists("vosc_resolution_setup")) {
			result = db.getFields("vosc_resolution_setup");

			if (Arrays.asList(result).indexOf("saved_snapshot") < 0 && Arrays.asList(result).indexOf("save_snapshot_on_close") < 0) {
				db.execute("ALTER TABLE vosc_resolution_setup ADD COLUMN saved_snapshot TEXT DEFAULT NULL;");
				db.execute("ALTER TABLE vosc_resolution_setup ADD COLUMN save_snapshot_on_close INTEGER DEFAULT 0;");
				success = db.execute("INSERT INTO vosc_resolution_setup (`save_snapshot_on_close`) VALUES(0);");
				if (!success)
					KetaiAlertDialog.popup(applet, "SQL Error", "Adding column for snapshot saved on quit failed!");
			}
		}

	}

	static void setUpSnapshots(PApplet applet, KetaiSQLite db) {
		boolean success;

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

	static boolean setUpSensors(PApplet applet, KetaiSQLite db) {
		boolean success = false;

		if (db.connect()) {
//			if (db.execute("DROP TABLE vosc_sensors;"))
//				Log.d(TAG, "Successfully deleted table 'vosc_sensors'");
			String CREATE_SENSORS_TABLE = "CREATE TABLE vosc_sensors (" +
					"sensor STRING NOT NULL," +
					"state INTEGER NOT NULL DEFAULT 0" +
			");";

			if (!db.tableExists("vosc_sensors")) {
				success = db.execute(CREATE_SENSORS_TABLE);

				if (success) {
					success = db.execute("INSERT INTO vosc_sensors (`sensor`, `state`) VALUES " +
							"('ori', 0), ('acc', 0), ('mag', 0), ('grav', 0), ('prox', 0), ('light', 0), ('temp', 0), ('press', 0), ('linAcc', 0), ('hum', 0), ('loc', 0);");
					if (!success)
						KetaiAlertDialog.popup(applet, "SQL Error", "Inserting initial sensor values failed!");
				} else {
					KetaiAlertDialog.popup(applet, "SQL Error", "Creating database table for " +
							"sensors failed");
				}
			} else {
				success = db.query("SELECT * FROM vosc_sensors;");
				if (success) {
					while (db.next()) {
						String sensor = db.getString("sensor");
						int state = db.getInt("state");
						if (sensor.equals("ori"))
							VideOSCSensors.useOri = state > 0;
						else if (sensor.equals("acc"))
							VideOSCSensors.useAcc = state > 0;
						else if (sensor.equals("mag"))
							VideOSCSensors.useMag = state > 0;
						else if (sensor.equals("grav"))
							VideOSCSensors.useGrav = state > 0;
						else if (sensor.equals("prox"))
							VideOSCSensors.useProx = state > 0;
						else if (sensor.equals("light"))
							VideOSCSensors.useLight = state > 0;
						else if (sensor.equals("press"))
							VideOSCSensors.usePress = state > 0;
						else if (sensor.equals("temp"))
							VideOSCSensors.useTemp = state > 0;
						else if (sensor.equals("linAcc"))
							VideOSCSensors.useLinAcc = state > 0;
						else if (sensor.equals("hum"))
							VideOSCSensors.useHum = state > 0;
						else if (sensor.equals("loc"))
							VideOSCSensors.useLoc = state > 0;
					}
				}
			}
		}

		return success;
	}

	static boolean updateNetworkSettings(KetaiSQLite db) {
		boolean success = false;

		if (db.connect()) {
			success = db.execute("UPDATE vosc_connect_data SET host_ip='" + sendAddr
					+ "', host_port=" + broadcastPort + ", receive_port="
					+ listenPort + ", root_cmd='" + rootCmd + "';");
		}

		return success;
	}

	static boolean updateResolutionSettings(KetaiSQLite db) {
		boolean success = false;

		if (db.connect()) {
			success = db.execute("UPDATE vosc_resolution_setup " +
					"SET res_w=" + resW
					+ ", res_h=" + resH + ", framerate=" + framerate
					+ ", calc_period=" + calcsPerPeriod + ", normalisation=" + (normalize ? 1 : 0)
					+ ", save_snapshot_on_close=" + (saveSnapshotOnClose ? 1 : 0) + ";");
		}

		return success;
	}

	static boolean updateSensorsSettings(PApplet applet, KetaiSQLite db) {
		boolean success = false;
		Map<String, Integer> sensors = new HashMap<String, Integer>();

		VideOSCSensors.numActiveSensors = 0;

		sensors.put("ori", VideOSCSensors.useOri ? 1 : 0);
		if (VideOSCSensors.useOri) VideOSCSensors.numActiveSensors++;
		sensors.put("acc", VideOSCSensors.useAcc ? 1 : 0);
		if (VideOSCSensors.useAcc) VideOSCSensors.numActiveSensors++;
		sensors.put("mag", VideOSCSensors.useMag ? 1 : 0);
		if (VideOSCSensors.useMag) VideOSCSensors.numActiveSensors++;
		sensors.put("grav", VideOSCSensors.useGrav ? 1 : 0);
		if (VideOSCSensors.useGrav) VideOSCSensors.numActiveSensors++;
		sensors.put("prox", VideOSCSensors.useProx ? 1 : 0);
		if (VideOSCSensors.useProx) VideOSCSensors.numActiveSensors++;
		sensors.put("light", VideOSCSensors.useLight ? 1 : 0);
		if (VideOSCSensors.useLight) VideOSCSensors.numActiveSensors++;
		sensors.put("press", VideOSCSensors.usePress ? 1 : 0);
		if (VideOSCSensors.usePress) VideOSCSensors.numActiveSensors++;
		sensors.put("temp", VideOSCSensors.useTemp ? 1 : 0);
		if (VideOSCSensors.useTemp) VideOSCSensors.numActiveSensors++;
		sensors.put("linAcc", VideOSCSensors.useLinAcc ? 1 : 0);
		if (VideOSCSensors.useLinAcc) VideOSCSensors.numActiveSensors++;
		sensors.put("hum", VideOSCSensors.useHum ? 1 : 0);
		if (VideOSCSensors.useHum) VideOSCSensors.numActiveSensors++;
		sensors.put("loc", VideOSCSensors.useLoc ? 1 : 0);
		if (VideOSCSensors.useLoc) VideOSCSensors.numActiveSensors++;

		if (db.connect()) {
			for (String key : sensors.keySet()) {
				success = db.execute("UPDATE vosc_sensors SET state=" + sensors.get(key) + " WHERE sensor='" + key + "';");
				if (!success) {
					KetaiAlertDialog.popup(applet, "SQL Error", "Updating sensor setting for '" + key + "' failed!");
					break;
				}
			}
		}

		return success;
	}

	static ArrayList<String> listSensorsInUse(KetaiSQLite db) {
		boolean success;
		String query = "SELECT * FROM vosc_sensors WHERE state > 0";
		ArrayList<String> result = new ArrayList<String>();

		if (db.connect()) {
			success = db.query(query);
			if (success) {
				while (db.next()) {
					result.add(db.getString("sensor"));
				}
			}
		}

		return result;
	}

	static boolean addSnapshot(PApplet applet, KetaiSQLite db, boolean onQuit) {
		boolean success = false;

		if (db.connect()) {
			// we simply store a snapshot as a string in the database. e.g.: "010110111010"
			// the size of the string must be a multiple of 3 (RGB)
			String offs = "";

			for (Boolean[] slot : offPxls) {
				for (int i = 0; i < 3; i++)
					offs += slot[i] ? 1 : 0;
			}

			if (!onQuit) {
				// store the snapshot under a key represented by the current datetime
				success = db.execute("INSERT INTO vosc_snapshots (`date`, `pattern`) VALUES" +
						"(datetime(),'" + offs + "');");
			} else {
				success = db.execute("UPDATE vosc_resolution_setup SET saved_snapshot='" + offs + "';");
			}

			if (!success) {
				KetaiAlertDialog.popup(applet, "SQL Error", "Writing the snapshot to the database" +
						" failed");
			} else {
				if (!onQuit) numSnapshots++;
			}
		}

		return success;
	}

	static String[] getSnapshotKeys(KetaiSQLite db) {
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

	static void selectSnapshot(PApplet applet, String snapshot, KetaiSQLite db) {
		boolean success;
		String res;
		int resl;
		int slot = 0;

		if (db.connect()) {
			if (snapshot != null)
				success = db.query("SELECT pattern FROM vosc_snapshots WHERE date='" + snapshot + "';");
			else
				success = db.query("SELECT saved_snapshot FROM vosc_resolution_setup;");

			if (success) {
				while (db.next()) {
					if (snapshot != null)
						res = db.getString("pattern");
					else res = db.getString("saved_snapshot");
					// make sure not to set any pixels that don't exist, e.g. if a snapshot has
					// been set for 24 pixels but the current setup only has 20
					if (res != null) {
						resl = res.length();
						if (resl > dimensions * 3) resl = dimensions * 3;
						for (int i = 0; i < resl; i++) {
							if (i % 3 == 0) {
								// a snapshot is an ArrayList of boolean tripplets
								// we simply add the result of a non-equality check applied on the
								// values stored in the db
								// 0 is unique code number 48
								// depending on if offPxls.get(slot) already exists
								// we have to either replace the existing value
								// or to add a triplet of booleans
								if (offPxls.size() <= slot) {
									Boolean[] sl = {(int) res.charAt(i) != 48, (int) res.charAt(i + 1) != 48, (int) res.charAt(i + 2) != 48};
									offPxls.add(sl);
								} else {
									offPxls.get(slot)[0] = (int) res.charAt(i) != 48;
									offPxls.get(slot)[1] = (int) res.charAt(i + 1) != 48;
									offPxls.get(slot)[2] = (int) res.charAt(i + 2) != 48;
								}
							} else if (i % 3 == 2) slot++;
						}
					}
				}
			} else {
				KetaiAlertDialog.popup(applet, "SQL Error", "The snapshot couldn't be determined");
			}
		}
	}

	static void resetSnapshots(PApplet applet, KetaiSQLite db) {
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

	static long countSnapshots(PApplet applet, KetaiSQLite db) {
		long num = 0;

		if (db.connect()) {
			num = db.getRecordCount("vosc_snapshots");
		} else {
			KetaiAlertDialog.popup(applet, "SQL Error", "The number of snapshots could not be " +
					"determined");
		}

		return num;
	}
}
