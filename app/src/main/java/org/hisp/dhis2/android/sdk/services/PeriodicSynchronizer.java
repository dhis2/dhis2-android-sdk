package org.hisp.dhis2.android.sdk.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 *	This class can be activated to periodically synchronize with a DHIS 2 server to fetch newly updated meta data and
 *	data values. 
 */
public class PeriodicSynchronizer extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e("ddd", " onReceive periodique ");
	}

	/**
	 * Activates the PeriodicSynchronizer to run on a schedule time interval
	 * @param context
	 * @param minutes the time in minutes between each time the synchronizer runs.
	 */
	public void ActivatePeriodicSynchronizer(Context context, int minutes) {
		Log.e("ddd", "activate periodic synchronizer");
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, PeriodicSynchronizer.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
				1000 * 60 * minutes, pi); // Millisec * Second * Minute
	}

	/**
	 * Cancels the PeriodicSynchronizer
	 * @param context
	 */
	public void CancelPeriodicSynchronizer(Context context) {
		Log.e("ddd", "cancel periodic synchronizer");
		Intent intent = new Intent(context, PeriodicSynchronizer.class);
		PendingIntent sender = PendingIntent
				.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}
}
