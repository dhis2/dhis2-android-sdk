package org.hisp.dhis.android.core.sms.domain.repository;

import io.reactivex.Completable;
import io.reactivex.Observable;

public interface SmsRepository {

    Observable<SmsSendingState> sendSms(String number, String value, int sendingTimeoutSeconds);

    Completable listenToConfirmationSms(int waitingTimeoutSeconds);

    void confirmTotalCount();

    class ReceivedErrorException extends Exception {
        private int errorCode;

        public ReceivedErrorException(int errorCode) {
            this.errorCode = errorCode;
        }

        public int getErrorCode() {
            return errorCode;
        }
    }

    class TimeoutException extends Exception {
    }

    class SmsSendingState {
        private State state;
        private int sent;
        private int total;

        public SmsSendingState(State state, int sent, int total) {
            this.state = state;
            this.sent = sent;
            this.total = total;
        }

        public int getSent() {
            return sent;
        }

        public int getTotal() {
            return total;
        }

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
