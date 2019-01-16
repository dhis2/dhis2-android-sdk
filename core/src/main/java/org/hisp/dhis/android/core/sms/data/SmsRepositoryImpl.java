package org.hisp.dhis.android.core.sms.data;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import org.hisp.dhis.android.core.sms.domain.repository.SmsRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;

public class SmsRepositoryImpl implements SmsRepository {

    private static final String TAG = SmsRepository.class.getSimpleName();
    private static final String SMS_KEY = "sms_key";
    private Context context;
    private String sendSmsAction;
    private boolean totalConfirmed = false;

    @Inject
    public SmsRepositoryImpl(Context context) {
        this.context = context;
        sendSmsAction = context.getPackageName() + ".SEND_SMS";
    }

    @Override
    public void confirmTotalCount() {
        totalConfirmed = true;
    }

    @Override
    public Observable<SmsSendingState> sendSms(String number, String contents,
                                               int sendingTimeoutSeconds) {
        return Observable.create(
                (ObservableOnSubscribe<SmsSendingState>) e ->
                        executeSmsSending(e, number, contents, sendingTimeoutSeconds)
        ).doOnError(throwable ->
                Log.e(TAG, throwable.getClass().getSimpleName(), throwable)
        );
    }

    @Override
    public Completable listenToConfirmationSms(int waitingTimeoutSeconds, String requiredSender,
                                               Collection<String> requiredStrings) {
        return findConfirmationSms(requiredSender, requiredStrings).flatMapCompletable(
                found -> found ?
                        Completable.complete() :
                        waitToReceiveConfirmationSms(waitingTimeoutSeconds, requiredSender,
                                requiredStrings));
    }

    private Completable waitToReceiveConfirmationSms(int waitingTimeoutSeconds, String requiredSender,
                                                     Collection<String> requiredStrings) {
        AtomicReference<BroadcastReceiver> receiver = new AtomicReference<>();
        return Completable.fromPublisher(s -> {
                    receiver.set(new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            if (isAwaitedMessage(intent, requiredSender, requiredStrings))
                                s.onComplete();
                        }
                    });
                    context.registerReceiver(receiver.get(),
                            new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));
                }
        ).timeout(
                waitingTimeoutSeconds, TimeUnit.SECONDS, Completable.error(new TimeoutException())
        ).doFinally(() -> {
            if (receiver.get() != null) try {
                context.unregisterReceiver(receiver.get());
            } catch (Throwable t) {
                // not interested in unregistering error
            }
        });
    }

    private Single<Boolean> findConfirmationSms(String requiredSender,
                                                Collection<String> requiredStrings) {
        return Single.fromCallable(() -> {
            ContentResolver cr = context.getContentResolver();
            Cursor c = cr.query(Telephony.Sms.CONTENT_URI, null, null,
                    null, Telephony.Sms.DATE + " DESC");
            if (c == null || !c.moveToFirst()) return false;
            do {
                String number = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS));
                String body = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.BODY));
                if (isAwaitedMessage(number, body, requiredSender, requiredStrings)) {
                    c.close();
                    return true;
                }
            } while (c.moveToNext());
            c.close();
            return false;
        }).onErrorResumeNext(throwable -> {
            Log.e(TAG, throwable.getClass().getSimpleName(), throwable);
            return Single.just(false);
        });
    }

    private boolean isAwaitedMessage(Intent intent, String requiredSender,
                                     Collection<String> requiredStrings) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
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
            return isAwaitedMessage(sender, message, requiredSender, requiredStrings);
        }
        return true;
    }

    private boolean isAwaitedMessage(String sender, String message, String requiredSender,
                                     Collection<String> requiredStrings) {
        if (requiredSender != null &&
                (sender == null || !sender.toLowerCase().contains(requiredSender.toLowerCase())))
            return false;
        if (requiredStrings != null) {
            for (String requiredString : requiredStrings) {
                if (!message.contains(requiredString)) return false;
            }
        }
        return true;
    }

    private void executeSmsSending(ObservableEmitter<SmsSendingState> e, String number,
                                   String contents, int timeoutSeconds) {
        ArrayList<String> parts = generateSmsParts(contents);
        int totalMessages = parts.size();
        if (!askForTotalCountConfirmation(e, totalMessages)) return;

        final long timeStarted = System.currentTimeMillis();
        StateReceiver stateReceiver = new StateReceiver(timeStarted, timeoutSeconds);
        context.registerReceiver(stateReceiver, new IntentFilter(sendSmsAction));
        int sentNumber = 0;
        sendSmsToOS(stateReceiver, number, contents, parts);
        e.onNext(new SmsSendingState(State.SENDING, 0, totalMessages));

        while (stateReceiver.smsResultsWaiting() > 0 && !stateReceiver.isError() &&
                timeLeft(timeStarted, timeoutSeconds) > 0 && !e.isDisposed()) {
            // wait until timeout passes, response comes, or request disposed
            try {
                Thread.sleep(500);
            } catch (InterruptedException ie) {
                e.onError(ie);
                unregisterReceiver(stateReceiver);
                return;
            }
            int currentSentNumber = totalMessages - stateReceiver.smsResultsWaiting();
            if (currentSentNumber != sentNumber) {
                sentNumber = currentSentNumber;
                e.onNext(new SmsSendingState(State.SENDING, sentNumber, totalMessages));
            }
        }
        unregisterReceiver(stateReceiver);

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

    private ArrayList<String> generateSmsParts(String text) {
        SmsManager sms = SmsManager.getDefault();
        return sms.divideMessage(text);
    }

    /**
     * @return true if should continue execution
     */
    private boolean askForTotalCountConfirmation(ObservableEmitter<SmsSendingState> e,
                                                 int totalMessages) {
        e.onNext(new SmsSendingState(SmsRepository.State.WAITING_TOTAL_CONFIRMATION,
                0, totalMessages));
        while (!totalConfirmed && !e.isDisposed()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ie) {
                e.onError(ie);
                return false;
            }
        }
        return totalConfirmed;
    }

    private long timeLeft(long timeStarted, int sendingTimeoutSeconds) {
        return sendingTimeoutSeconds * 1000 + timeStarted - System.currentTimeMillis();
    }

    private void unregisterReceiver(StateReceiver stateReceiver) {
        try {
            context.unregisterReceiver(stateReceiver);
        } catch (Exception e) {
            Log.w(TAG, "Unnecessarily unregistered broadcast receiver. Nothing to see here.", e);
        }
    }

    /**
     * Sends an SMS
     *
     * @param number   String The phone number the sms should be sent to.
     * @param contents String The message that should be sent.
     */
    private void sendSmsToOS(StateReceiver stateReceiver, String number, String contents,
                             ArrayList<String> parts) {
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

        sms.sendMultipartTextMessage(number, null, parts, sentMessagePIs, null);
    }

    private class StateReceiver extends BroadcastReceiver {
        private HashSet<String> smsResultsWaiting = new HashSet<>();
        private long timeStarted;
        private int timeoutSeconds;
        private boolean error = false;
        private int errorCode;

        public StateReceiver(long timeStarted, int timeoutSeconds) {
            this.timeStarted = timeStarted;
            this.timeoutSeconds = timeoutSeconds;
        }

        void addSmsKey(String smsKey) {
            smsResultsWaiting.add(smsKey);
        }

        int smsResultsWaiting() {
            return smsResultsWaiting.size();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (timeLeft(timeStarted, timeoutSeconds) < 0 || error) {
                // not interested, killing receiver
                unregisterReceiver(this);
                return;
            }

            Log.d(TAG, intent.getAction());
            if (!sendSmsAction.equals(intent.getAction()) || smsResultsWaiting.size() == 0)
                return;
            String smsKey = intent.getStringExtra(SMS_KEY);
            if (smsKey == null) return;

            if (!smsResultsWaiting.contains(smsKey)) {
                Log.d(TAG, "Received sms result for different dataset");
                return;
            }
            int resultCode = getResultCode();
            switch (resultCode) {
                case Activity.RESULT_OK:
                    smsResultsWaiting.remove(smsKey);
                    break;
                default:
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
}
