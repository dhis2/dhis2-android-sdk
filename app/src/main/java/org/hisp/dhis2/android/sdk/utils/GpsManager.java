package org.hisp.dhis2.android.sdk.utils;

import java.util.List;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class GpsManager implements LocationListener
{
	private final String CLASS_TAG = "GpsManager";
	
	private double latitude;
	
	private double longitude;
	
	private Location location;
	
	private LocationManager locationManager;
	
	private LocationListener locationListener;
	
	public GpsManager()
	{

	}
	
	public void requestLocationUpdates(Context context)
	{
        locationListener = this;
        if(locationManager == null)
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		List<String> providers = locationManager.getProviders( true );
        for ( String provider : providers )
        {
            Log.d(CLASS_TAG, provider);
            locationManager.requestLocationUpdates( provider, 0, 0, this );
        }
	}

	public boolean isGpsAvailable()
	{
		if(locationManager != null)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public Location getLocation(Context context)
	{
        if(locationManager == null) requestLocationUpdates(context);
		Criteria criteria = new Criteria();

        String provider = locationManager.getBestProvider( criteria, false );

        location = locationManager.getLastKnownLocation( provider );
        if ( location != null )
        {
            setLatitude( location.getLatitude() );
            setLongitude( location.getLongitude() );
        }

        Location location = new Location(provider);
        location.setLatitude( getLatitude() );
        location.setLongitude( getLongitude() );

        return location;
	}

	public double getLatitude() 
	{
		return latitude;
	}

	public void setLatitude(double latitude) 
	{
		this.latitude = latitude;
	}

	public double getLongitude() 
	{
		return longitude;
	}

	public void setLongitude(double longitude) 
	{
		this.longitude = longitude;
	}

	public LocationManager getLocationManager() 
	{
		return locationManager;
	}

	public void setLocationManager(LocationManager locationManager) 
	{
		this.locationManager = locationManager;
	}

	public LocationListener getLocationListener() 
	{
		return locationListener;
	}

	public void setLocationListener(LocationListener locationListener) 
	{
		this.locationListener = locationListener;
	}
	
	public void removeUpdates()
	{
        if(locationManager!=null)
		    locationManager.removeUpdates(this);
	}

	public void setLocation(Location location) 
	{
		this.location = location;
	}

	public void onLocationChanged(Location location) {
        setLatitude( location.getLatitude() );
        setLongitude( location.getLongitude() );
	}

	public void onProviderDisabled(String provider) {
		Log.d(CLASS_TAG, "Provider disabled");
	}

	public void onProviderEnabled(String provider) {
		Log.d(CLASS_TAG, "Provider enabled");
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

}
