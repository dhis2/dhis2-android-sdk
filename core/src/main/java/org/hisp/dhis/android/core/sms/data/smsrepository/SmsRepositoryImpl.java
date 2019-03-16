package org.hisp.dhis.android.core.sms.data.smsrepository;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.util.Log;

import org.hisp.dhis.android.core.sms.domain.repository.SmsRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

public class SmsRepositoryImpl implements SmsRepository {

    private static final String TAG = SmsRepository.class.getSimpleName();
    static final String SMS_KEY = "sms_key";
    private final Context context;
    private final String sendSmsAction;
    private Boolean smsCountAccepted;

    public SmsRepositoryImpl(Context context) {
        this.context = context;
        sendSmsAction = context.getPackageName() + ".SEND_SMS";
        smsCountAccepted = null;
    }

    @Override
    public void acceptSMSCount(boolean accept) {
        smsCountAccepted = accept;
    }

    @Override
    public Observable<SmsSendingState> sendSms(String number, String contents,
                                               int sendingTimeoutSeconds) {
        return Observable.create(
                (ObservableOnSubscribe<SmsSendingState>) e ->
                        executeSmsSending(e, number, contents, sendingTimeoutSeconds)
        ).doOnError(throwable ->
                Log.e(TAG, throwable.getClass().getSimpleName(), throwable)
        ).subscribeOn(Schedulers.newThread());
    }

    @Override
    public Completable listenToConfirmationSms(int waitingTimeoutSeconds, String requiredSender,
                                               Collection<String> requiredStrings) {
        SmsReader smsReceiver = new SmsReader(context);
        return smsReceiver.findConfirmationSms(requiredSender, requiredStrings).flatMapCompletable(
                found -> found ?
                        Completable.complete() :
                        smsReceiver.waitToReceiveConfirmationSms(waitingTimeoutSeconds, requiredSender,
                                requiredStrings));
    }

    @SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops", "PMD.CyclomaticComplexity"})
    private void executeSmsSending(ObservableEmitter<SmsSendingState> e, String number,
                                   String contents, int timeoutSeconds) {
        List<String> parts = generateSmsParts(contents);
        int totalMessages = parts.size();
        if (!askSMSCountAcceptance(e, totalMessages)) {
            e.onError(new SMSCountException(totalMessages));
            return;
        }

        final long timeStarted = System.currentTimeMillis();
        SendingStateReceiver stateReceiver = new SendingStateReceiver(timeStarted,
                timeoutSeconds, sendSmsAction);
        context.registerReceiver(stateReceiver, new IntentFilter(sendSmsAction));
        int sentNumber = 0;
        sendSmsToOS(stateReceiver, number, contents, parts);
        e.onNext(new SmsSendingState(State.SENDING, 0, totalMessages));

        while (stateReceiver.smsResultsWaiting() > 0 && !stateReceiver.isError() &&
                Utility.timeLeft(timeStarted, timeoutSeconds) > 0 && !e.isDisposed()) {
            // wait until timeout passes, response comes, or request disposed
            try {
                Thread.sleep(500);
            } catch (InterruptedException ie) {
                e.onError(ie);
                Utility.unregisterReceiver(context, stateReceiver);
                return;
            }
            int currentSentNumber = totalMessages - stateReceiver.smsResultsWaiting();
            if (currentSentNumber != sentNumber) {
                sentNumber = currentSentNumber;
                e.onNext(new SmsSendingState(State.SENDING, sentNumber, totalMessages));
            }
        }
        Utility.unregisterReceiver(context, stateReceiver);

        if (e.isDisposed()) {
            return;
        }
        if (stateReceiver.smsResultsWaiting() == 0 && !stateReceiver.isError()) {
            e.onNext(new SmsSendingState(State.ALL_SENT, totalMessages, totalMessages));
            e.onComplete();
        } else if (stateReceiver.isError()) {
            e.onError(new ReceivedErrorException(stateReceiver.getErrorCode()));
        } else {
            e.onError(new TimeoutException());
        }
    }

    private List<String> generateSmsParts(String text) {
        SmsManager sms = SmsManager.getDefault();
        return sms.divideMessage(text);
    }

    /**
     * @return true if should continue execution
     */
    private boolean askSMSCountAcceptance(ObservableEmitter<SmsSendingState> e,
                                          int totalMessages) {
        e.onNext(new SmsSendingState(State.WAITING_SMS_COUNT_ACCEPT,
                0, totalMessages));
        while (smsCountAccepted == null && !e.isDisposed()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ie) {
                return false;
            }
        }
        return smsCountAccepted;
    }

    /**
     * Sends an SMS
     *
     * @param number   String The phone number the sms should be sent to.
     * @param contents String The message that should be sent.
     */
    @SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops"})
    private void sendSmsToOS(SendingStateReceiver stateReceiver, String number, String contents,
                             List<String> parts) {
        SmsManager sms = SmsManager.getDefault();
        int uniqueIntentId = contents.hashCode();
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
}
