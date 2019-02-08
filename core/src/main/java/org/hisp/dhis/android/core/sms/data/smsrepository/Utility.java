package org.hisp.dhis.android.core.sms.data.smsrepository;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.util.Log;

final class Utility {
    private static final String TAG = Utility.class.getSimpleName();

    private Utility() {
    }

    static long timeLeft(long timeStarted, int timeoutSeconds) {
        return timeoutSeconds * 1000L + timeStarted - System.currentTimeMillis();
    }

    static void unregisterReceiver(Context context, BroadcastReceiver receiver) {
        try {
            context.unregisterReceiver(receiver);
        } catch (Exception e) {
            Log.w(TAG, "Unnecessarily unregistered broadcast receiver. Nothing to see here.", e);
        }
    }
}
