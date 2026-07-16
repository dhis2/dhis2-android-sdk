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

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.hisp.dhis.android.core.sms.domain.repository.SmsRepository
import org.hisp.dhis.android.core.sms.domain.repository.SmsRepository.ResultResponseIssue.RECEIVED_ERROR
import org.hisp.dhis.android.core.sms.domain.repository.internal.SubmissionType
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

internal class SmsReader(private val context: Context) {

    @Suppress("TooGenericExceptionCaught")
    fun waitToReceiveConfirmationSms(
        waitingTimeoutSeconds: Int,
        requiredSender: String,
        submissionId: Int,
        submissionType: SubmissionType,
    ): Completable {
        val receiver = AtomicReference<BroadcastReceiver>()

        return Completable.create { emitter ->
            val broadcastReceiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    try {
                        if (isAwaitedSuccessMessage(intent, requiredSender, submissionId, submissionType)) {
                            emitter.onComplete()
                        }
                    } catch (ex: Exception) {
                        if (!emitter.isDisposed) {
                            emitter.onError(ex)
                        }
                    }
                }
            }
            receiver.set(broadcastReceiver)
            context.registerReceiver(
                broadcastReceiver,
                IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION),
            )
        }.timeout(
            waitingTimeoutSeconds.toLong(),
            TimeUnit.SECONDS,
            Schedulers.newThread(),
            Completable.error(SmsRepository.ResultResponseException(SmsRepository.ResultResponseIssue.TIMEOUT)),
        ).doFinally {
            receiver.get()?.let {
                try {
                    context.unregisterReceiver(it)
                } catch (t: Throwable) {
                    Log.d(TAG, "${t.javaClass.simpleName} ${t.message}")
                }
            }
        }
    }

    @Suppress("TooGenericExceptionCaught")
    fun findConfirmationSms(
        fromDate: Date,
        requiredSender: String,
        submissionId: Int,
        submissionType: SubmissionType,
    ): Single<Boolean> {
        return Single.fromCallable {
            val cr = context.contentResolver

            cr.query(
                Telephony.Sms.CONTENT_URI,
                null,
                null,
                null,
                "${Telephony.Sms.DATE} DESC",
            )?.use { c ->
                if (!c.moveToFirst()) return@fromCallable false

                val addressIndex = c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)
                val bodyIndex = c.getColumnIndexOrThrow(Telephony.Sms.BODY)
                val dateIndex = c.getColumnIndexOrThrow(Telephony.Sms.DATE)

                do {
                    try {
                        val number = c.getString(addressIndex)
                        val body = c.getString(bodyIndex)
                        val dateReceived = Date(c.getLong(dateIndex))

                        if (isAwaitedSuccessMessage(number, body, requiredSender, submissionId, submissionType) &&
                            dateReceived.after(fromDate)
                        ) {
                            return@fromCallable true
                        }
                    } catch (e: Exception) {
                        // failed reading this message, go to the next one
                        continue
                    }
                } while (c.moveToNext())
            }
            return@fromCallable false
        }
    }

    @Suppress("ReturnCount")
    @Throws(SmsRepository.ResultResponseException::class)
    private fun isAwaitedSuccessMessage(
        intent: Intent,
        requiredSender: String?,
        submissionId: Int,
        submissionType: SubmissionType,
    ): Boolean {
        val bundle = intent.extras ?: return false

        val pdus = bundle.get("pdus") as? Array<*> ?: return false
        if (pdus.isEmpty()) return false

        var sender: String? = null

        val message = buildString {
            for (i in pdus.indices) {
                val pdu = pdus[i] as? ByteArray ?: continue
                val smsMessage = SmsMessage.createFromPdu(pdu)

                if (i == 0) {
                    sender = smsMessage.originatingAddress
                }
                append(smsMessage.messageBody)
            }
        }

        return sender != null && isAwaitedSuccessMessage(
            sender, message, requiredSender, submissionId, submissionType,
        )
    }

    @Suppress("ReturnCount")
    @Throws(SmsRepository.ResultResponseException::class)
    fun isAwaitedSuccessMessage(
        sender: String?,
        message: String,
        requiredSender: String?,
        submissionId: Int,
        submissionType: SubmissionType,
    ): Boolean {
        if (
            requiredSender != null &&
            (sender == null || !sender.lowercase(Locale.ROOT).contains(requiredSender.lowercase(Locale.ROOT)))
        ) {
            return false
        }

        val firstSeparator = message.indexOf(':')
        if (firstSeparator < 0 || firstSeparator >= message.length - 2) {
            return false
        }

        val secondSeparator = message.indexOf(':', firstSeparator + 1)
        if (secondSeparator < 0) {
            return false
        }

        if (message.substring(0, firstSeparator) != submissionId.toString()) {
            return false
        }

        // it's awaited message
        return if (message.substring(firstSeparator + 1, secondSeparator) == "0") {
            true
        } else {
            throw SmsRepository.ResultResponseException(RECEIVED_ERROR)
        }
    }

    companion object {
        private val TAG = SmsReader::class.java.simpleName
    }
}
