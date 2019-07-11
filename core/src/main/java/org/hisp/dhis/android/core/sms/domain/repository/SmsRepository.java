package org.hisp.dhis.android.core.sms.domain.repository;

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
