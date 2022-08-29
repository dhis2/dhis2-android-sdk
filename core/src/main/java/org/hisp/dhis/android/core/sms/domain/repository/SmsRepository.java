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

package org.hisp.dhis.android.core.sms.domain.repository;

import org.hisp.dhis.android.core.sms.domain.repository.internal.SubmissionType;

import java.util.Date;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

public interface SmsRepository {

    /**
     * Sends given text by sms
     *
     * @param number                Recipient phone number
     * @param smsParts              Text data to send, returned from generateSmsParts
     * @param sendingTimeoutSeconds After this time error will be returned.
     * @return Observable that emits current status of sending
     */
    Observable<SmsSendingState> sendSms(String number, List<String> smsParts, int sendingTimeoutSeconds);

    /**
     * @param value text to send
     * @return contents for multiple sms parts
     */
    Single<List<String>> generateSmsParts(String value);

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
    Completable listenToConfirmationSms(Date fromDate,
                                        int waitingTimeoutSeconds,
                                        String requiredSender,
                                        int submissionId,
                                        SubmissionType submissionType);

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
    Single<Boolean> isAwaitedSuccessMessage(String sender,
                                            String message,
                                            String requiredSender,
                                            int submissionId,
                                            SubmissionType submissionType);

    /**
     * Returned when sms sending error is returned from OS.
     */
    class ReceivedErrorException extends Exception {
        private final int errorCode;

        public ReceivedErrorException(int errorCode) {
            this.errorCode = errorCode;
        }

        public int getErrorCode() {
            return errorCode;
        }
    }

    /**
     * Returned when not received successful response message
     */
    class ResultResponseException extends Exception {
        final ResultResponseIssue reason;

        public ResultResponseException(ResultResponseIssue reason) {
            this.reason = reason;
        }

        public ResultResponseIssue getReason() {
            return reason;
        }
    }

    enum ResultResponseIssue {
        TIMEOUT, RECEIVED_ERROR, OTHER
    }

    /**
     * Shows the current status of sending task.
     */
    class SmsSendingState {
        private final int sent;
        private final int total;

        public SmsSendingState(int sent, int total) {
            this.sent = sent;
            this.total = total;
        }

        /**
         * @return Amount of messages sent to this moment
         */
        public int getSent() {
            return sent;
        }

        /**
         * @return Total number of messages that will be sent
         */
        public int getTotal() {
            return total;
        }
    }
}
