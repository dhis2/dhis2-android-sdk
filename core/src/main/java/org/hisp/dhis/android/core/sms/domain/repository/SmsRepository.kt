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
package org.hisp.dhis.android.core.sms.domain.repository

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.hisp.dhis.android.core.sms.domain.repository.internal.SubmissionType
import java.util.Date

interface SmsRepository {

    /**
     * Sends given text by sms
     *
     * @param number                Recipient phone number
     * @param smsParts              Text data to send, returned from generateSmsParts
     * @param sendingTimeoutSeconds After this time error will be returned.
     * @return Observable that emits current status of sending
     */
    fun sendSms(number: String, smsParts: List<String>, sendingTimeoutSeconds: Int): Observable<SmsSendingState>

    /**
     * @param value text to send
     * @return contents for multiple sms parts
     */
    fun generateSmsParts(value: String): Single<List<String>>

    /**
     * Starts process of listening to result confirmation sms
     *
     * @param fromDate              don't check messages older than this
     * @param waitingTimeoutSeconds after this time error will be returned
     * @param requiredSender        messages from other senders will not be read
     * @param submissionId          submission ID to recognize message
     * @param submissionType        submission type to recognize message
     * @return completed when found
     */
    fun listenToConfirmationSms(
        fromDate: Date,
        waitingTimeoutSeconds: Int,
        requiredSender: String?,
        submissionId: Int,
        submissionType: SubmissionType,
    ): Completable

    /**
     * Check if a message is the expected one or not
     *
     * @param sender                number of the sender
     * @param message               received message
     * @param requiredSender        messages from other senders will not be read
     * @param submissionId          submission ID to recognize message
     * @param submissionType        submission type to recognize message
     * @return single with true if the message is the response for the current submit case; false otherwise. Returns
     * the error RECEIVED_ERROR is the message is the awaited one but it contains an error.
     */
    fun isAwaitedSuccessMessage(
        sender: String?,
        message: String,
        requiredSender: String?,
        submissionId: Int,
        submissionType: SubmissionType,
    ): Single<Boolean>

    /**
     * Returned when sms sending error is returned from OS.
     */
    class ReceivedErrorException(val errorCode: Int) : Exception()

    /**
     * Returned when not received successful response message
     */
    class ResultResponseException(val reason: ResultResponseIssue) : Exception()

    enum class ResultResponseIssue {
        TIMEOUT,
        RECEIVED_ERROR,
        OTHER,
    }

    /**
     * Shows the current status of sending task.
     *
     * @property sent Amount of messages sent to this moment
     * @property total Total number of messages that will be sent
     */
    class SmsSendingState(val sent: Int, val total: Int)
}
