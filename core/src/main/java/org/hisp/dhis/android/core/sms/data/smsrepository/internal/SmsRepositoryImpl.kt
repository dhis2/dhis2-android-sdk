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

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.telephony.SmsManager
import android.util.Log
import androidx.core.content.ContextCompat
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.hisp.dhis.android.core.sms.domain.repository.SmsRepository
import org.hisp.dhis.android.core.sms.domain.repository.SmsRepository.ReceivedErrorException
import org.hisp.dhis.android.core.sms.domain.repository.SmsRepository.ResultResponseException
import org.hisp.dhis.android.core.sms.domain.repository.SmsRepository.ResultResponseIssue
import org.hisp.dhis.android.core.sms.domain.repository.SmsRepository.SmsSendingState
import org.hisp.dhis.android.core.sms.domain.repository.internal.SubmissionType
import java.util.Date
import java.util.UUID

class SmsRepositoryImpl(private val context: Context) : SmsRepository {

    private val sendSmsAction: String = context.packageName + ".SEND_SMS"

    override fun sendSms(
        number: String,
        smsParts: List<String>,
        sendingTimeoutSeconds: Int,
    ): Observable<SmsSendingState> {
        return Observable.create { e: ObservableEmitter<SmsSendingState> ->
            executeSmsSending(e, number, smsParts, sendingTimeoutSeconds)
        }.doOnError { throwable ->
            Log.e(TAG, throwable.javaClass.simpleName, throwable)
        }.subscribeOn(Schedulers.newThread())
    }

    @Suppress("TooGenericExceptionCaught", "ComplexCondition")
    private fun executeSmsSending(
        e: ObservableEmitter<SmsSendingState>,
        number: String,
        smsParts: List<String>,
        timeoutSeconds: Int,
    ) {
        val timeStarted = System.currentTimeMillis()
        val stateReceiver = SendingStateReceiver(timeStarted, timeoutSeconds, sendSmsAction)
        ContextCompat.registerReceiver(
            context,
            stateReceiver,
            IntentFilter(sendSmsAction),
            ContextCompat.RECEIVER_NOT_EXPORTED,
        )
        var sentNumber = 0
        sendSmsToOS(stateReceiver, number, smsParts)
        val totalMessages = smsParts.size
        e.onNext(SmsSendingState(0, totalMessages))

        while (stateReceiver.smsResultsWaiting() > 0 && !stateReceiver.isError &&
            Utility.timeLeft(timeStarted, timeoutSeconds) > 0 && !e.isDisposed
        ) {
            // wait until timeout passes, response comes, or request disposed
            try {
                Thread.sleep(SENDING_CHECK_PERIOD_MS)
            } catch (ie: InterruptedException) {
                if (!e.isDisposed) {
                    e.onError(ie)
                }
                Utility.unregisterReceiver(context, stateReceiver)
                return
            }
            val currentSentNumber = totalMessages - stateReceiver.smsResultsWaiting()
            if (currentSentNumber != sentNumber) {
                sentNumber = currentSentNumber
                e.onNext(SmsSendingState(sentNumber, totalMessages))
            }
        }
        Utility.unregisterReceiver(context, stateReceiver)

        if (e.isDisposed) {
            return
        }
        if (stateReceiver.smsResultsWaiting() == 0 && !stateReceiver.isError) {
            e.onNext(SmsSendingState(totalMessages, totalMessages))
            e.onComplete()
        } else if (stateReceiver.isError) {
            e.onError(ReceivedErrorException(stateReceiver.errorCode))
        } else {
            e.onError(ResultResponseException(ResultResponseIssue.TIMEOUT))
        }
    }

    override fun generateSmsParts(value: String): Single<List<String>> {
        return Single.fromCallable<List<String>> {
            val sms = SmsManager.getDefault()
            sms.divideMessage(value)
        }
    }

    override fun listenToConfirmationSms(
        fromDate: Date,
        waitingTimeoutSeconds: Int,
        requiredSender: String,
        submissionId: Int,
        submissionType: SubmissionType,
    ): Completable {
        val smsReceiver = SmsReader(context)
        return smsReceiver.findConfirmationSms(
            fromDate,
            requiredSender,
            submissionId,
            submissionType,
        ).flatMapCompletable { found ->
            if (found) {
                Completable.complete()
            } else {
                smsReceiver.waitToReceiveConfirmationSms(
                    waitingTimeoutSeconds,
                    requiredSender,
                    submissionId,
                    submissionType,
                )
            }
        }
    }

    override fun isAwaitedSuccessMessage(
        sender: String,
        message: String,
        requiredSender: String,
        submissionId: Int,
        submissionType: SubmissionType,
    ): Single<Boolean> {
        val smsReceiver = SmsReader(context)
        return Single.defer {
            Single.just(
                smsReceiver.isAwaitedSuccessMessage(
                    sender,
                    message,
                    requiredSender,
                    submissionId,
                    submissionType,
                ),
            )
        }
    }

    /**
     * Sends an SMS
     *
     * @param number The phone number the sms should be sent to.
     * @param parts  The message that should be sent.
     */
    private fun sendSmsToOS(stateReceiver: SendingStateReceiver, number: String, parts: List<String>) {
        val sms = SmsManager.getDefault()
        var uniqueIntentId = parts.joinToString(separator = "").hashCode()
        val uniqueKeyPrefix = uniqueIntentId.toString() + "_" + UUID.randomUUID().toString()
        val sentMessagePIs = ArrayList<PendingIntent>()
        for (i in parts.indices) {
            val smsKey = uniqueKeyPrefix + '_' + i
            stateReceiver.addSmsKey(smsKey)
            val sentPI = PendingIntent.getBroadcast(
                context,
                uniqueIntentId,
                Intent(sendSmsAction).putExtra(SMS_KEY, smsKey),
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE,
            )
            sentMessagePIs.add(sentPI)
            uniqueIntentId++
        }

        sms.sendMultipartTextMessage(number, null, ArrayList(parts), sentMessagePIs, null)
    }

    companion object {
        private val TAG: String = SmsRepository::class.java.simpleName
        private const val SENDING_CHECK_PERIOD_MS = 500L
        internal const val SMS_KEY = "sms_key"
    }
}
