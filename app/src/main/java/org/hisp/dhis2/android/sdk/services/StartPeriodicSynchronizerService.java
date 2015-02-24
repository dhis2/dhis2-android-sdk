package org.hisp.dhis2.android.sdk.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class StartPeriodicSynchronizerService extends Service {

    public static final String CLASS_TAG = "StartPeriodicSynchronizerService";
	
	int minutes = 1;

	PeriodicSynchronizer periodicSynchronizer = new PeriodicSynchronizer();
    public void onCreate()
    {
    	Log.e(CLASS_TAG, "startperiodicsyncservice oncreate");
        super.onCreate();       
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) 
	{
    	Log.e(CLASS_TAG, "startperiodicsyncservice onstartcommand");
         periodicSynchronizer.ActivatePeriodicSynchronizer(StartPeriodicSynchronizerService.this, minutes);
	     return START_STICKY;
	}

    public void onStart(Context context,Intent intent, int startId)
    {
    	Log.e(CLASS_TAG, "startperiodicsyncservice onstart");
        periodicSynchronizer.ActivatePeriodicSynchronizer(context, minutes);
    }
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.e(CLASS_TAG, "startperiodicsyncservice onbind");
		return null;
	}
}
