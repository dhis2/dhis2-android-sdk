package org.hisp.dhis.android.sdk.utils;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

public class GpsManager implements LocationListener {
    private final String TAG = GpsManager.class.getSimpleName();

    private static GpsManager mManager;

    // Variables for caching latest
    // latitude and longitude values
    private double mLatitude;
    private double mLongitude;

    // private Location location;
    private LocationManager mLocationManager;

    private GpsManager(Context context) {
        mLocationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
    }

    public static void init(Context context) {
        if (mManager == null) {
            mManager = new GpsManager(context);
        }
    }

    public static GpsManager getInstance() {
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

    public Location getLocation() {
        requestLocationUpdates();

        Criteria criteria = new Criteria();
        String provider = mLocationManager.getBestProvider(criteria, false);
        Location location = mLocationManager.getLastKnownLocation(provider);
        if (location != null) {
            mLatitude = location.getLatitude();
            mLongitude = location.getLongitude();
        }

        location = new Location(provider);
        location.setLatitude(getLatitude());
        location.setLongitude(getLongitude());

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