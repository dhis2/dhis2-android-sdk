package org.hisp.dhis.android.sdk.controllers;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

public final class GpsController implements LocationListener {
    private final String TAG = GpsController.class.getSimpleName();

    private static GpsController mManager;

    // Variables for caching latest
    // latitude and longitude values
    private double mLatitude;
    private double mLongitude;

    // private Location location;
    private LocationManager mLocationManager;

    private GpsController(Context context) {
        mLocationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
    }

    public static void activateGps(Context context) {
        GpsController.init(context);
        GpsController.getInstance().requestLocationUpdates();
    }

    public static void disableGps() {
        try {
            GpsController.getInstance().removeUpdates();
        } catch (IllegalArgumentException e) {
            //if this is called then probably the Gps hasnt been started, so we don't need to disable
        }
    }

    public static void init(Context context) {
        if (mManager == null) {
            mManager = new GpsController(context);
        }
    }

    public static GpsController getInstance() {
        if (mManager == null) {
            throw new IllegalArgumentException("You have to call init() method first");
        }

        return mManager;
    }

    public void requestLocationUpdates() {
        List<String> providers = mLocationManager.getProviders(true);
        for (String provider : providers) {
            Log.d(TAG, provider);
            mLocationManager.requestLocationUpdates(provider, 0, 0, this);
        }
    }

    public boolean isGpsAvailable() {
        return mLocationManager != null;
    }

    public static Location getLocation() {
        getInstance().requestLocationUpdates();

        Criteria criteria = new Criteria();
        String provider = getInstance().mLocationManager.getBestProvider(criteria, false);
        Location location = getInstance().mLocationManager.getLastKnownLocation(provider);
        if (location != null) {
            getInstance().mLatitude = location.getLatitude();
            getInstance().mLongitude = location.getLongitude();
        }

        location = new Location(provider);
        location.setLatitude(getInstance().getLatitude());
        location.setLongitude(getInstance().getLongitude());

        return location;
    }

    public void removeUpdates() {
        mLocationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        mLatitude = location.getLatitude();
        mLongitude = location.getLongitude();
    }

    @Override
    public void onProviderDisabled(String provider) {
        // stub implementation
        Log.d(TAG, "Provider disabled");
    }

    @Override
    public void onProviderEnabled(String provider) {
        // stub implementation
        Log.d(TAG, "Provider enabled");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // stub implementation
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }
}