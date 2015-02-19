package org.hisp.dhis2.android.sdk.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PeriodicSynchronizerAutoStarter extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e("ddd", "PeriodicSynchronizerAutoStarter onReceive");
		if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			Log.e("ddd", "PeriodicSynchronizerAutoStarter onReceive BOOT_COMPLETED");
			context.startService(new Intent(context, StartPeriodicSynchronizerService.class));
		}
	}

}
