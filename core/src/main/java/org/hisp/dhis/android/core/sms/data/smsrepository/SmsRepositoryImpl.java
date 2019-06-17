package org.hisp.dhis.android.core.sms.data.smsrepository;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.util.Log;

import org.hisp.dhis.android.core.sms.domain.repository.SmsRepository;
import org.hisp.dhis.android.core.sms.domain.repository.SubmissionType;

import java.util.ArrayList;
import java.util.Collection;
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
            e.onError(new TimeoutException());
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
    public Completable listenToConfirmationSms(boolean searchReceived,
                                               int waitingTimeoutSeconds,
                                               String requiredSender,
                                               int submissionId,
                                               SubmissionType submissionType) {
        SmsReader smsReceiver = new SmsReader(context);
        Completable waitForSmsAction = smsReceiver.waitToReceiveConfirmationSms(
                waitingTimeoutSeconds, requiredSender, submissionId, submissionType);
        if (searchReceived) {
            return smsReceiver.findConfirmationSms(requiredSender, submissionId, submissionType)
                    .flatMapCompletable(
                            found -> found ? Completable.complete() : waitForSmsAction);
        } else {
            return waitForSmsAction;
        }
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
