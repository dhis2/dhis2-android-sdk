/*
 *  Copyright (c) 2015, University of Oslo
 *  * All rights reserved.
 *  *
 *  * Redistribution and use in source and binary forms, with or without
 *  * modification, are permitted provided that the following conditions are met:
 *  * Redistributions of source code must retain the above copyright notice, this
 *  * list of conditions and the following disclaimer.
 *  *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *  * this list of conditions and the following disclaimer in the documentation
 *  * and/or other materials provided with the distribution.
 *  * Neither the name of the HISP project nor the names of its contributors may
 *  * be used to endorse or promote products derived from this software without
 *  * specific prior written permission.
 *  *
 *  * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */

package org.hisp.dhis.android.sdk.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.hisp.dhis.android.sdk.controllers.Dhis2;
import org.hisp.dhis.android.sdk.network.http.ApiRequestCallback;
import org.hisp.dhis.android.sdk.network.http.Response;
import org.hisp.dhis.android.sdk.network.managers.NetworkManager;
import org.hisp.dhis.android.sdk.utils.APIException;

/**
 *	This class can be activated to periodically synchronize with a DHIS 2 server to fetch newly updated meta data and
 *	data values. 
 */
public class PeriodicSynchronizer extends BroadcastReceiver {

    public static final int FREQUENCY_ONE_MINUTE = 0;
    public static final int FREQUENCY_15_MINTUES = 1;
    public static final int FREQUENCY_ONE_HOUR = 2;
    public static final int FREQUENCY_ONE_DAY = 3;
    public static final int FREQUENCY_DISABLED = 4;
    public static final int DEFAULT_UPDATE_FREQUENCY = FREQUENCY_ONE_HOUR;

    public static final String CLASS_TAG = "PeriodicSynchronizer";

    private static PeriodicSynchronizer periodicSynchronizer;
    private int currentInterval = 15;

    public static PeriodicSynchronizer getInstance() {
        if(periodicSynchronizer == null) periodicSynchronizer = new PeriodicSynchronizer();
        return periodicSynchronizer;
    }

	@Override
	public void onReceive(Context context, Intent intent) {
        String serverUrl = Dhis2.getInstance().getServer(context);
        String credentials = Dhis2.getInstance().getCredentials(context);
        Log.d(CLASS_TAG, "serverUrl: " + serverUrl);
        if(serverUrl == null || credentials == null) {
            cancelPeriodicSynchronizer(context);
            return;
        }
        NetworkManager.getInstance().setServerUrl(serverUrl);
        NetworkManager.getInstance().setCredentials(credentials);
        ApiRequestCallback callback = new ApiRequestCallback() {
            @Override
            public void onSuccess(Response response) {
                //do nothing
            }

            @Override
            public void onFailure(APIException exception) {
                //do nothing
            }
        };
        Dhis2.synchronize(context, callback);
	}

	/**
	 * Activates the PeriodicSynchronizer to run on a schedule time interval
	 * @param context
	 * @param minutes the time in minutes between each time the synchronizer runs.
	 */
	public static void activatePeriodicSynchronizer(Context context, int minutes) {
        if(minutes <= 0) return;
		Log.d(CLASS_TAG, "activate periodic synchronizer " + minutes);
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, PeriodicSynchronizer.class);
        PendingIntent existingPi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_NO_CREATE);
        if(existingPi == null) {
            PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                    1000 * 60 * minutes, pi); // Millisec * Second * Minute
        }
	}

	/**
	 * Cancels the PeriodicSynchronizer
	 * @param context
	 */
	public static void cancelPeriodicSynchronizer(Context context) {
		Log.d(CLASS_TAG, "cancel periodic synchronizer");
		Intent intent = new Intent(context, PeriodicSynchronizer.class);
		PendingIntent sender = PendingIntent
				.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE);
        if(sender!=null) {
            AlarmManager alarmManager = (AlarmManager) context
                    .getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(sender);
            sender.cancel();
        }
	}

    /**
     * Returns the set update interval in minutes
     * @param context
     * @return
     */
    public static int getInterval(Context context) {
        int frequencyIndex = Dhis2.getUpdateFrequency(context);
        int minutes;
        switch (frequencyIndex) {
            case FREQUENCY_ONE_MINUTE: //1 minutes
                minutes = 1;
                break;
            case FREQUENCY_15_MINTUES: //15 minutes
                minutes = 15;
                break;
            case FREQUENCY_ONE_HOUR: //1 hour
                minutes = 1 * 60;
                break;
            case FREQUENCY_ONE_DAY:// 1 day
                minutes = 1 * 60 * 24;
                break;
            case FREQUENCY_DISABLED: //disabled
                minutes = 0;
                break;
            default:
                minutes = DEFAULT_UPDATE_FREQUENCY;
        }
        return minutes;
    }

    /**
     * Returns the given update interval in minutes
     * @param context
     * @return
     */
    public static int getInterval(Context context, int index) {
        int frequencyIndex = index;
        int minutes;
        switch (frequencyIndex) {
            case FREQUENCY_ONE_MINUTE: //1 minutes
                minutes = 1;
                break;
            case FREQUENCY_15_MINTUES: //15 minutes
                minutes = 15;
                break;
            case FREQUENCY_ONE_HOUR: //1 hour
                minutes = 1 * 60;
                break;
            case FREQUENCY_ONE_DAY:// 1 day
                minutes = 1 * 60 * 24;
                break;
            case FREQUENCY_DISABLED: //disabled
                minutes = 0;
                break;
            default:
                minutes = DEFAULT_UPDATE_FREQUENCY;
        }
        return minutes;
    }

    /**
     * ReActivates the PeriodicSyncronizer if the time interval has changed.
     * @return
     */
    public static void reActivate(Context context) {
        int interval = getInterval(context);
        if (interval != getInstance().currentInterval) {
            getInstance().cancelPeriodicSynchronizer(context);
            getInstance().activatePeriodicSynchronizer(context, interval);
            getInstance().currentInterval = interval;
        }
    }
}