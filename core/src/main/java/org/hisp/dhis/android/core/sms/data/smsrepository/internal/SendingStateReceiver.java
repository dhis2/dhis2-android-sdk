/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.sms.data.smsrepository.internal;

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

    SendingStateReceiver(long timeStarted, int timeoutSeconds, String sendSmsAction) {
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