package gps;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import net.videosc.VideOSC;
import net.videosc.VideOSCSensors;

/**
 * Created by stefan on 05.01.17.
 */

public class GpsLocationReceiver extends BroadcastReceiver implements LocationListener {
    private final static String TAG = "GpsLocationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive - context: " + context + ", intent: " + intent);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "provider: " + location.getProvider() + ", latitude: " + location.getLatitude() + ", longitude: " + location.getLongitude() + ", altitude: " + location.getAltitude() + ", accuracy: " + location.getAltitude() + ", bearing: " + location.getBearing() + ", extras: " + location.getExtras());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "status changed - provider: " + provider + ", extras: " + extras);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "provider enabled - provider: " + provider);
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "provider disabled - provider: " + provider);
    }
}
