package org.hisp.dhis.android.core.sms.data;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;

import org.hisp.dhis.android.core.sms.domain.repository.DeviceStateRepository;
import org.reactivestreams.Publisher;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Single;

public class DeviceStateRepositoryImpl implements DeviceStateRepository {
    private Context context;

    public DeviceStateRepositoryImpl(Context context) {
        this.context = context;
    }

    @Override
    public Single<Boolean> isNetworkConnected() {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager == null) return Single.just(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("MissingPermission")
            ServiceState serviceState = telephonyManager.getServiceState();
            if (serviceState != null)
                return Single.just(serviceState.getState() == ServiceState.STATE_IN_SERVICE);
        }

        // When failed to get current status or too low sdk version
        // Have to register listener
        AtomicReference<PhoneStateListener> listener = new AtomicReference<>();
        return Single.fromPublisher((Publisher<Boolean>) s -> {
            // Set a listener on a telephony manager to get
            listener.set(new PhoneStateListener() {
                @Override
                public void onServiceStateChanged(ServiceState serviceState) {
                    if (listener.get() == null) return;
                    s.onNext((serviceState.getState() == ServiceState.STATE_IN_SERVICE));
                    telephonyManager.listen(listener.get(), PhoneStateListener.LISTEN_NONE);
                    listener.set(null);
                    s.onComplete();
                }
            });
            telephonyManager.listen(listener.get(), PhoneStateListener.LISTEN_SERVICE_STATE);
        }).timeout(3, TimeUnit.SECONDS, Single.fromCallable(() -> {
            // If information did not come quickly, remove listener and try other method
            return telephonyManager.getNetworkType() != TelephonyManager.NETWORK_TYPE_UNKNOWN;
        })).doFinally(() -> {
            if (listener.get() != null) {
                telephonyManager.listen(listener.get(), PhoneStateListener.LISTEN_NONE);
                listener.set(null);
            }
        });
    }

    @Override
    public Single<Boolean> hasCheckNetworkPermission() {
        return Single.just(PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE));
    }

    @Override
    public Single<Boolean> hasSendSMSPermission() {
        return Single.just(PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS));
    }

    @Override
    public Single<Boolean> hasReceiveSMSPermission() {
        return Single.just(PackageManager.PERMISSION_GRANTED ==
                ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) &&
                PackageManager.PERMISSION_GRANTED ==
                        ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS));
    }
}
