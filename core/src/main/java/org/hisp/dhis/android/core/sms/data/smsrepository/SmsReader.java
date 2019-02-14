package org.hisp.dhis.android.core.sms.data.smsrepository;

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

import java.util.Collection;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

class SmsReader {
    private final static String TAG = SmsReader.class.getSimpleName();
    private final Context context;

    SmsReader(Context context) {
        this.context = context;
    }

    Completable waitToReceiveConfirmationSms(int waitingTimeoutSeconds, String requiredSender,
                                             Collection<String> requiredStrings) {
        AtomicReference<BroadcastReceiver> receiver = new AtomicReference<>();
        return Completable.fromPublisher(s -> {
                    receiver.set(new BroadcastReceiver() {
                        @Override
                        public void onReceive(Context context, Intent intent) {
                            if (isAwaitedMessage(intent, requiredSender, requiredStrings)) {
                                s.onComplete();
                            }
                        }
                    });
                    context.registerReceiver(receiver.get(),
                            new IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION));
                }
        ).timeout(waitingTimeoutSeconds, TimeUnit.SECONDS, Schedulers.newThread(),
                Completable.error(new SmsRepository.TimeoutException())
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

    Single<Boolean> findConfirmationSms(String requiredSender,
                                        Collection<String> requiredStrings) {
        return Single.fromCallable(() -> {
            ContentResolver cr = context.getContentResolver();
            Cursor c = cr.query(Telephony.Sms.CONTENT_URI, null, null,
                    null, Telephony.Sms.DATE + " DESC");
            if (c == null || !c.moveToFirst()) {
                return false;
            }
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
                (sender == null || !sender.toLowerCase(Locale.ROOT)
                        .contains(requiredSender.toLowerCase(Locale.ROOT)))) {
            return false;
        }
        if (requiredStrings != null) {
            for (String requiredString : requiredStrings) {
                if (!message.contains(requiredString)) {
                    return false;
                }
            }
        }
        return true;
    }
}
