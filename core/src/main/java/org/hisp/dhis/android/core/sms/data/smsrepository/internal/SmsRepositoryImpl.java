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

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.util.Log;

import org.hisp.dhis.android.core.sms.domain.repository.SmsRepository;
import org.hisp.dhis.android.core.sms.domain.repository.internal.SubmissionType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class SmsRepositoryImpl implements SmsRepository {

    private static final String TAG = SmsRepository.class.getSimpleName();
    static final String SMS_KEY = "sms_key";
    private final Context context;
    private final String sendSmsAction;

    public SmsRepositoryImpl(Context context) {
        this.context = context;
        sendSmsAction = context.getPackageName() + ".SEND_SMS";
    }

    @Override
    public Observable<SmsSendingState> sendSms(String number, List<String> smsParts,
                                               int sendingTimeoutSeconds) {
        return Observable.create(
                (ObservableOnSubscribe<SmsSendingState>) e ->
                        executeSmsSending(e, number, smsParts, sendingTimeoutSeconds)
        ).doOnError(throwable ->
                Log.e(TAG, throwable.getClass().getSimpleName(), throwable)
        ).subscribeOn(Schedulers.newThread());
    }

    @SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops", "PMD.CyclomaticComplexity"})
    private void executeSmsSending(ObservableEmitter<SmsSendingState> e, String number,
                                   List<String> smsParts, int timeoutSeconds) {
        final long timeStarted = System.currentTimeMillis();
        SendingStateReceiver stateReceiver = new SendingStateReceiver(timeStarted,
                timeoutSeconds, sendSmsAction);
        context.registerReceiver(stateReceiver, new IntentFilter(sendSmsAction));
        int sentNumber = 0;
        sendSmsToOS(stateReceiver, number, smsParts);
        int totalMessages = smsParts.size();
        e.onNext(new SmsSendingState(0, totalMessages));

        while (stateReceiver.smsResultsWaiting() > 0 && !stateReceiver.isError() &&
                Utility.timeLeft(timeStarted, timeoutSeconds) > 0 && !e.isDisposed()) {
            // wait until timeout passes, response comes, or request disposed
            try {
                Thread.sleep(500);
            } catch (InterruptedException ie) {
                if (!e.isDisposed()) {
                    e.onError(ie);
                }
                Utility.unregisterReceiver(context, stateReceiver);
                return;
            }
            int currentSentNumber = totalMessages - stateReceiver.smsResultsWaiting();
            if (currentSentNumber != sentNumber) {
                sentNumber = currentSentNumber;
                e.onNext(new SmsSendingState(sentNumber, totalMessages));
            }
        }
        Utility.unregisterReceiver(context, stateReceiver);

        if (e.isDisposed()) {
            return;
        }
        if (stateReceiver.smsResultsWaiting() == 0 && !stateReceiver.isError()) {
            e.onNext(new SmsSendingState(totalMessages, totalMessages));
            e.onComplete();
        } else if (stateReceiver.isError()) {
            e.onError(new ReceivedErrorException(stateReceiver.getErrorCode()));
        } else {
            e.onError(new ResultResponseException(ResultResponseIssue.TIMEOUT));
        }
    }

    @Override
    public Single<List<String>> generateSmsParts(String value) {
        return Single.fromCallable(() -> {
            SmsManager sms = SmsManager.getDefault();
            return sms.divideMessage(value);
        });
    }

    @Override
    public Completable listenToConfirmationSms(Date fromDate,
                                               int waitingTimeoutSeconds,
                                               String requiredSender,
                                               int submissionId,
                                               SubmissionType submissionType) {
        SmsReader smsReceiver = new SmsReader(context);
        return smsReceiver.findConfirmationSms(
                fromDate, requiredSender, submissionId, submissionType
        ).flatMapCompletable(found -> found ? Completable.complete() :
                smsReceiver.waitToReceiveConfirmationSms(
                        waitingTimeoutSeconds, requiredSender, submissionId, submissionType)
        );
    }

    @Override
    public Single<Boolean> isAwaitedSuccessMessage(String sender,
                                                   String message,
                                                   String requiredSender,
                                                   int submissionId,
                                                   SubmissionType submissionType) {
        SmsReader smsReceiver = new SmsReader(context);
        return Single.defer(() ->
                Single.just(smsReceiver.isAwaitedSuccessMessage(sender, message, requiredSender,
                        submissionId, submissionType))
        );
    }

    /**
     * Sends an SMS
     *
     * @param number The phone number the sms should be sent to.
     * @param parts  The message that should be sent.
     */
    @SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops"})
    private void sendSmsToOS(SendingStateReceiver stateReceiver, String number, List<String> parts) {
        SmsManager sms = SmsManager.getDefault();
        int uniqueIntentId = joinStrings(parts).hashCode();
        String uniqueKeyPrefix = uniqueIntentId + "_" + UUID.randomUUID().toString();
        ArrayList<PendingIntent> sentMessagePIs = new ArrayList<>();
        for (int i = 0; i < parts.size(); i++) {
            String smsKey = uniqueKeyPrefix + '_' + i;
            stateReceiver.addSmsKey(smsKey);
            PendingIntent sentPI = PendingIntent.getBroadcast(
                    context,
                    uniqueIntentId,
                    new Intent(sendSmsAction).putExtra(SMS_KEY, smsKey),
                    PendingIntent.FLAG_ONE_SHOT);
            sentMessagePIs.add(sentPI);
            uniqueIntentId++;
        }

        sms.sendMultipartTextMessage(number, null, new ArrayList<>(parts),
                sentMessagePIs, null);
    }

    private String joinStrings(Collection<String> parts) {
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            sb.append(part);
        }
        return sb.toString();
    }
}
