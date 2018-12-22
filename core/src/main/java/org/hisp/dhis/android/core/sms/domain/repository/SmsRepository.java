package org.hisp.dhis.android.core.sms.domain.repository;

import io.reactivex.Completable;
import io.reactivex.Observable;

public interface SmsRepository {

    /**
     * Sends given text by sms
     *
     * @param number                Recipient phone number
     * @param value                 Text data to send
     * @param sendingTimeoutSeconds After this time error will be returned.
     * @return Observable that emits current status of sending
     */
    Observable<SmsSendingState> sendSms(String number, String value, int sendingTimeoutSeconds);

    /**
     * Starts process of listening to result confirmation sms
     *
     * @param waitingTimeoutSeconds After this time error will be returned.
     * @return Completable that is completed when result sms is successfully received
     */
    Completable listenToConfirmationSms(int waitingTimeoutSeconds);

    /**
     * Sending status Observable may emit WAITING_TOTAL_CONFIRMATION, as a protection for sending too
     * many messages. Then this method has to be called to resume sms sending.
     */
    void confirmTotalCount();

    /**
     * Returned when sms sending error is returned from OS.
     */
    class ReceivedErrorException extends Exception {
        private int errorCode;

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
        private State state;
        private int sent;
        private int total;

        public SmsSendingState(State state, int sent, int total) {
            this.state = state;
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

        /**
         * @return Current sending state enum
         */
        public State getState() {
            return state;
        }
    }

    enum State {
        SENDING,
        WAITING_TOTAL_CONFIRMATION,
        ALL_SENT
    }
}
