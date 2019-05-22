package org.hisp.dhis.android.core.sms.domain.repository;

import java.util.Collection;
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
     * @param searchReceived        should also search previously received messages
     * @param waitingTimeoutSeconds After this time error will be returned.
     * @return Completable that is completed when result sms is successfully received
     */
    Completable listenToConfirmationSms(boolean searchReceived,
                                        int waitingTimeoutSeconds,
                                        String requiredSender,
                                        Collection<String> requiredStrings);

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
     * Returned when timeout occurs
     */
    class TimeoutException extends Exception {
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
