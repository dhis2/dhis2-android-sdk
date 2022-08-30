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

package org.hisp.dhis.android.core.sms.data.internal;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;

import org.hisp.dhis.android.core.sms.domain.repository.internal.DeviceStateRepository;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@SuppressLint("MissingPermission")
public class DeviceStateRepositoryImpl implements DeviceStateRepository {
    private final Context context;

    public DeviceStateRepositoryImpl(Context context) {
        this.context = context;
    }

    @Override
    @SuppressLint("MissingPermission")
    public Single<Boolean> isNetworkConnected() {
        //permission should be checked earlier
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager == null) {
            return Single.just(false);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ServiceState serviceState = telephonyManager.getServiceState();
            if (serviceState != null) {
                return Single.just(serviceState.getState() == ServiceState.STATE_IN_SERVICE);
            }
        }

        // When failed to get current status or too low sdk version
        // Have to register listener
        AtomicReference<PhoneStateListener> listener = new AtomicReference<>();
        return Single.create((SingleOnSubscribe<Boolean>) emitter -> {
            if (emitter.isDisposed()) {
                return;
            }
            // Set a listener on a telephony manager to get
            listener.set(new PhoneStateListener() {
                @Override
                public void onServiceStateChanged(ServiceState serviceState) {
                    if (listener.get() == null || emitter.isDisposed()) {
                        return;
                    }
                    telephonyManager.listen(listener.get(), PhoneStateListener.LISTEN_NONE);
                    listener.set(null);
                    emitter.onSuccess(serviceState.getState() == ServiceState.STATE_IN_SERVICE);
                }
            });
            telephonyManager.listen(listener.get(), PhoneStateListener.LISTEN_SERVICE_STATE);
        }).subscribeOn(AndroidSchedulers.mainThread()
        ).timeout(3, TimeUnit.SECONDS, Schedulers.newThread(), Single.fromCallable(() -> {
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
                context.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE));
    }

    @Override
    public Single<Boolean> hasSendSMSPermission() {
        return Single.just(PackageManager.PERMISSION_GRANTED ==
                context.checkCallingOrSelfPermission(Manifest.permission.SEND_SMS));
    }

    @Override
    public Single<Boolean> hasReceiveSMSPermission() {
        return Single.just(PackageManager.PERMISSION_GRANTED ==
                context.checkCallingOrSelfPermission(Manifest.permission.RECEIVE_SMS) &&
                PackageManager.PERMISSION_GRANTED ==
                        context.checkCallingOrSelfPermission(Manifest.permission.READ_SMS));
    }
}
