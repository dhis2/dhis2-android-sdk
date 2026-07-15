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
package org.hisp.dhis.android.core.sms.data.smsrepository.internal

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.util.Log
import org.hisp.dhis.android.core.sms.data.smsrepository.internal.Utility.timeLeft
import org.hisp.dhis.android.core.sms.data.smsrepository.internal.Utility.unregisterReceiver
import java.util.Objects

internal class SendingStateReceiver(
    private val timeStarted: Long,
    private val timeoutSeconds: Int,
    private val sendSmsAction: String,
) : BroadcastReceiver() {
    private val smsResultsWaiting: MutableSet<String?> = HashSet<String?>()
    var isError: Boolean = false
        private set
    var errorCode: Int = 0
        private set

    fun addSmsKey(smsKey: String?) {
        smsResultsWaiting.add(smsKey)
    }

    fun smsResultsWaiting(): Int {
        return smsResultsWaiting.size
    }

    @Suppress("ReturnCount")
    override fun onReceive(context: Context, intent: Intent) {
        if (timeLeft(timeStarted, timeoutSeconds) < 0 || this.isError) {
            // not interested, killing receiver
            unregisterReceiver(context, this)
            return
        }

        Log.d(TAG, Objects.requireNonNull<String>(intent.getAction()))
        if (sendSmsAction != intent.getAction() || smsResultsWaiting.isEmpty()) {
            Log.w(TAG, "Received an unexpected action. Ignoring...")
            return
        }

        val callingUid = Binder.getCallingUid()
        if (callingUid != context.getApplicationInfo().uid) {
            Log.w(
                TAG,
                "Broadcast received from an untrusted source (UID=" + callingUid + "). Ignoring...",
            )
            return
        }

        val smsKey = intent.getStringExtra(SmsRepositoryImpl.SMS_KEY)
        if (smsKey == null || !smsResultsWaiting.contains(smsKey)) {
            Log.d(TAG, "Received SMS result for a different dataset or missing key. Ignoring...")
            return
        }

        val resultCode = getResultCode()
        if (resultCode == Activity.RESULT_OK) {
            smsResultsWaiting.remove(smsKey)
        } else {
            errorCode = resultCode
            this.isError = true
        }
    }

    companion object {
        private val TAG: String = SendingStateReceiver::class.java.getSimpleName()
    }
}
