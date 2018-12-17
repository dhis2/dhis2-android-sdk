package org.hisp.dhis.android.core.sms.data;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.SmsManager;
import android.util.Log;

import org.hisp.dhis.android.core.sms.domain.repository.SmsRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class SmsRepositoryImpl implements SmsRepository {

    private static final int SMS_SENDING_TIMEOUT_SECONDS = 5 * 60;
    private static final String TAG = SmsRepository.class.getSimpleName();
    private static final String SMS_KEY = "sms_key";
    private Context context;
    private String sendSmsAction;

    public SmsRepositoryImpl(Context context) {
        this.context = context;
        sendSmsAction = context.getPackageName() + ".SEND_SMS";
    }

    @Override
    public Observable<SmsSendingStatus> sendSms(String number, String contents) {
        return Observable.create(
                (ObservableOnSubscribe<SmsSendingStatus>) e -> executeSmsSending(e, number, contents)
        ).doOnError(throwable ->
                Log.e(TAG, throwable.getClass().getSimpleName(), throwable)
        );
    }

    private void executeSmsSending(ObservableEmitter<SmsSendingStatus> e, String number, String contents) {
        final long timeStarted = System.currentTimeMillis();
        StateReceiver stateReceiver = new StateReceiver(timeStarted);
        context.registerReceiver(stateReceiver, new IntentFilter(sendSmsAction));
        int sentNumber = 0;
        int totalMessages = sendSmsToOS(stateReceiver, number, contents);
        e.onNext(new SmsSendingStatus(sentNumber, totalMessages, false));

        //FIXME any maximum number of total messages? or popup to ask?
        //FIXME waiting loop will just finish when disposed (UI disconnected). Should also work on db in background?
        while (stateReceiver.smsResultsWaiting() > 0 && !stateReceiver.isError() && timeLeft(timeStarted) > 0 && !e.isDisposed()) {
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
                e.onNext(new SmsSendingStatus(sentNumber, totalMessages, false));
            }
        }
        unregisterReceiver(stateReceiver);
        if (stateReceiver.smsResultsWaiting() > 0 || stateReceiver.isError()) {
            e.onError(new RuntimeException("SMS sending failed"));
        } else {
            e.onComplete();
        }
        // TODO should also listen to the confirmation sms
    }

    private long timeLeft(long timeStarted) {
        return SMS_SENDING_TIMEOUT_SECONDS * 1000 + timeStarted - System.currentTimeMillis();
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
    private int sendSmsToOS(StateReceiver stateReceiver, String number, String contents) {
        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> parts = sms.divideMessage(contents);

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
        return parts.size();
    }

    private class StateReceiver extends BroadcastReceiver {
        private HashSet<String> smsResultsWaiting = new HashSet<>();
        private long timeStarted;
        private boolean error = false;

        public StateReceiver(long timeStarted) {
            this.timeStarted = timeStarted;
        }

        void addSmsKey(String smsKey) {
            smsResultsWaiting.add(smsKey);
        }

        int smsResultsWaiting() {
            return smsResultsWaiting.size();
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (timeLeft(timeStarted) < 0) {
                // checking in case if this broadcast receiver wasn't properly unregistered
                // and still receives intents
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
                    error = true;
            }
        }

        public boolean isError() {
            return error;
        }
    }
}
