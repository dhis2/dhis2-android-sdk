package org.hisp.dhis.android.core.sms.data.smsrepository;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

class SendingStateReceiver extends BroadcastReceiver {
    private final static String TAG = SendingStateReceiver.class.getSimpleName();
    private final Set<String> smsResultsWaiting = new HashSet<>();
    private final long timeStarted;
    private final String sendSmsAction;
    private final int timeoutSeconds;
    private boolean error;
    private int errorCode;

    public SendingStateReceiver(long timeStarted, int timeoutSeconds, String sendSmsAction) {
        this.timeStarted = timeStarted;
        this.timeoutSeconds = timeoutSeconds;
        this.sendSmsAction = sendSmsAction;
        error = false;
    }

    void addSmsKey(String smsKey) {
        smsResultsWaiting.add(smsKey);
    }

    int smsResultsWaiting() {
        return smsResultsWaiting.size();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Utility.timeLeft(timeStarted, timeoutSeconds) < 0 || error) {
            // not interested, killing receiver
            Utility.unregisterReceiver(context, this);
            return;
        }

        Log.d(TAG, intent.getAction());
        if (!sendSmsAction.equals(intent.getAction()) || smsResultsWaiting.isEmpty()) {
            return;
        }
        String smsKey = intent.getStringExtra(SmsRepositoryImpl.SMS_KEY);
        if (smsKey == null) {
            return;
        }

        if (!smsResultsWaiting.contains(smsKey)) {
            Log.d(TAG, "Received sms result for different dataset");
            return;
        }
        int resultCode = getResultCode();
        if (resultCode == Activity.RESULT_OK) {
            smsResultsWaiting.remove(smsKey);
        } else {
            errorCode = resultCode;
            error = true;
        }
    }

    public boolean isError() {
        return error;
    }

    public int getErrorCode() {
        return errorCode;
    }
}