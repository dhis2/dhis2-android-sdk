/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.sms.data.internal

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.ServiceState
import android.telephony.TelephonyManager
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.hisp.dhis.android.core.sms.domain.repository.internal.DeviceStateRepository
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

private const val NETWORK_CHECK_TIMEOUT_SECONDS = 3L

@SuppressLint("MissingPermission")
class DeviceStateRepositoryImpl(private val context: Context) : DeviceStateRepository {

    @SuppressLint("MissingPermission")
    override fun isNetworkConnected(): Single<Boolean> {
        // permission should be checked earlier
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
            ?: return Single.just(false)

        val serviceState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            telephonyManager.serviceState
        } else {
            null
        }

        return if (serviceState != null) {
            Single.just(serviceState.state == ServiceState.STATE_IN_SERVICE)
        } else {
            // When failed to get current status or too low sdk version
            // Have to register listener
            listenToServiceState(telephonyManager)
        }
    }

    private fun listenToServiceState(telephonyManager: TelephonyManager): Single<Boolean> {
        val listener = AtomicReference<PhoneStateListener?>()
        return Single.create<Boolean> { emitter ->
            if (emitter.isDisposed) {
                return@create
            }
            // Set a listener on a telephony manager to get
            listener.set(object : PhoneStateListener() {
                override fun onServiceStateChanged(serviceState: ServiceState) {
                    if (listener.get() == null || emitter.isDisposed) {
                        return
                    }
                    telephonyManager.listen(listener.get(), PhoneStateListener.LISTEN_NONE)
                    listener.set(null)
                    emitter.onSuccess(serviceState.state == ServiceState.STATE_IN_SERVICE)
                }
            })
            telephonyManager.listen(listener.get(), PhoneStateListener.LISTEN_SERVICE_STATE)
        }.subscribeOn(AndroidSchedulers.mainThread())
            .timeout(
                NETWORK_CHECK_TIMEOUT_SECONDS,
                TimeUnit.SECONDS,
                Schedulers.newThread(),
                Single.fromCallable {
                    // If information did not come quickly, remove listener and try other method
                    telephonyManager.networkType != TelephonyManager.NETWORK_TYPE_UNKNOWN
                },
            ).doFinally {
                if (listener.get() != null) {
                    telephonyManager.listen(listener.get(), PhoneStateListener.LISTEN_NONE)
                    listener.set(null)
                }
            }
    }

    override fun hasCheckNetworkPermission(): Single<Boolean> {
        return Single.just(
            PackageManager.PERMISSION_GRANTED ==
                context.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE),
        )
    }

    override fun hasSendSMSPermission(): Single<Boolean> {
        return Single.just(
            PackageManager.PERMISSION_GRANTED ==
                context.checkCallingOrSelfPermission(Manifest.permission.SEND_SMS),
        )
    }

    override fun hasReceiveSMSPermission(): Single<Boolean> {
        return Single.just(
            PackageManager.PERMISSION_GRANTED ==
                context.checkCallingOrSelfPermission(Manifest.permission.RECEIVE_SMS) &&
                PackageManager.PERMISSION_GRANTED ==
                context.checkCallingOrSelfPermission(Manifest.permission.READ_SMS),
        )
    }
}
