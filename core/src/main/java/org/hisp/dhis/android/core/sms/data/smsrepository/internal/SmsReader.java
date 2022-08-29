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

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import org.hisp.dhis.android.core.sms.domain.repository.SmsRepository;
import org.hisp.dhis.android.core.sms.domain.repository.internal.SubmissionType;

import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

import static org.hisp.dhis.android.core.sms.domain.repository.SmsRepository.ResultResponseIssue.RECEIVED_ERROR;

class SmsReader {
    private final static String TAG = SmsReader.class.getSimpleName();
    private final Context context;

    SmsReader(Context context) {
        this.context = context;
    }

    Completable waitToReceiveConfirmationSms(int waitingTimeoutSeconds,
                                             String requiredSender,
                                             int submissionId,
                                             SubmissionType submissionType) {
        AtomicReference<BroadcastReceiver> receiver = new AtomicReference<>();
        return Completable.fromPublisher(s -> {
                    receiver.set(new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            try {
                                if (isAwaitedSuccessMessage(intent, requiredSender,
                                        submissionId, submissionType)) {
                                    s.onComplete();
                                }
                            } catch (Exception ex) {
                                s.onError(ex);
                            }
                        }
                    });
                    context.registerReceiver(receiver.get(),
                            new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));
                }
        ).timeout(waitingTimeoutSeconds, TimeUnit.SECONDS, Schedulers.newThread(),
                Completable.error(new SmsRepository.ResultResponseException(
                        SmsRepository.ResultResponseIssue.TIMEOUT))
        ).doFinally(() -> {
            if (receiver.get() != null) {
                try {
                    context.unregisterReceiver(receiver.get());
                } catch (Throwable t) {
                    Log.d(TAG, t.getClass().getSimpleName() + " " + t.getMessage());
                }
            }
        });
    }

    @SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops"})
    Single<Boolean> findConfirmationSms(Date fromDate, String requiredSender,
                                        int submissionId, SubmissionType submissionType) {
        return Single.fromCallable(() -> {
            ContentResolver cr = context.getContentResolver();
            Cursor c = cr.query(Telephony.Sms.CONTENT_URI, null, null,
                    null, Telephony.Sms.DATE + " DESC");
            if (c == null || !c.moveToFirst()) {
                return false;
            }
            do {
                String number;
                String body;
                Date dateReceived;
                try {
                    number = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                    body = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.BODY));
                    dateReceived = new Date(c.getLong(c.getColumnIndexOrThrow(Telephony.Sms.DATE)));
                } catch (Exception e) {
                    // failed reading this message, go to the next one
                    continue;
                }
                if (isAwaitedSuccessMessage(
                        number, body, requiredSender, submissionId, submissionType)
                        && dateReceived.after(fromDate)) {
                    c.close();
                    return true;
                }
            } while (c.moveToNext());
            c.close();
            return false;
        });
    }

    private boolean isAwaitedSuccessMessage(Intent intent, String requiredSender,
                                            int submissionId, SubmissionType submissionType)
            throws SmsRepository.ResultResponseException {
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return false;
        }
        // get sms objects
        Object[] pdus = (Object[]) bundle.get("pdus");
        if (pdus == null || pdus.length == 0) {
            return false;
        }
        // large message might be broken into many
        SmsMessage[] messages = new SmsMessage[pdus.length];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pdus.length; i++) {
            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
            sb.append(messages[i].getMessageBody());
        }
        String sender = messages[0].getOriginatingAddress();
        String message = sb.toString();
        return isAwaitedSuccessMessage(sender, message, requiredSender,
                submissionId, submissionType);
    }

    @SuppressWarnings({"PMD.UnusedFormalParameter"})
    public boolean isAwaitedSuccessMessage(String sender, String message, String requiredSender,
                                           int submissionId, SubmissionType submissionType)
            throws SmsRepository.ResultResponseException {
        if (requiredSender != null &&
                (sender == null || !sender.toLowerCase(Locale.ROOT)
                        .contains(requiredSender.toLowerCase(Locale.ROOT)))) {
            return false;
        }
        int firstSeparator = message.indexOf(':');
        if (firstSeparator < 0 || firstSeparator >= message.length() - 2) {
            return false;
        }
        int secondSeparator = message.indexOf(':', firstSeparator + 1);
        if (secondSeparator < 0) {
            return false;
        }
        if (!message.substring(0, firstSeparator).equals(Integer.toString(submissionId))) {
            return false;
        }

        // it's awaited message
        if (message.substring(firstSeparator + 1, secondSeparator).equals("0")) {
            return true;
        } else {
            throw new SmsRepository.ResultResponseException(RECEIVED_ERROR);
        }
    }
}
