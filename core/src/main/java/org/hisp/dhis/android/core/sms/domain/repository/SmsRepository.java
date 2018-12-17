package org.hisp.dhis.android.core.sms.domain.repository;

import io.reactivex.Observable;

public interface SmsRepository {

    Observable<SmsSendingStatus> sendSms(String number, String value);

    class SmsSendingStatus {
        private int sentMessages;
        private int totalMessages;
        private boolean smsReceptionConfirmed;

        public SmsSendingStatus(int sentMessages, int totalMessages, boolean smsReceptionConfirmed) {
            this.sentMessages = sentMessages;
            this.totalMessages = totalMessages;
            this.smsReceptionConfirmed = smsReceptionConfirmed;
        }

        public int getSentMessages() {
            return sentMessages;
        }

        public int getTotalMessages() {
            return totalMessages;
        }

        public boolean isSmsReceptionConfirmed() {
            return smsReceptionConfirmed;
        }
    }
}
