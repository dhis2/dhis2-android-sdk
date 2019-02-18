package org.hisp.dhis.android.core.sms;

import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;
import org.hisp.dhis.smscompression.models.Metadata;

import java.util.Date;

import io.reactivex.Completable;
import io.reactivex.Single;

public class TestRepositories {

    public static class TestLocalDbRepository implements LocalDbRepository {
        public static String userId = "AIK2aQOJIbj";
        private String gatewayNumber = null;
        private String confirmationSenderNumber = null;
        private Integer resultWaitingTimeout = 120;
        public Metadata metadata;

        public TestLocalDbRepository() {
            this(new Metadata());
        }

        public TestLocalDbRepository(Metadata metadata) {
            this.metadata = metadata;
            metadata.lastSyncDate = new Date();
        }

        @Override
        public Single<String> getUserName() {
            return Single.fromCallable(() -> userId);
        }

        @Override
        public Single<String> getGatewayNumber() {
            return Single.fromCallable(() -> gatewayNumber);
        }

        @Override
        public Completable setGatewayNumber(String number) {
            return Completable.fromAction(() -> gatewayNumber = number);
        }

        @Override
        public Single<Integer> getWaitingResultTimeout() {
            return Single.fromCallable(() -> resultWaitingTimeout);
        }

        @Override
        public Completable setWaitingResultTimeout(Integer timeoutSeconds) {
            return Completable.fromAction(() -> resultWaitingTimeout = timeoutSeconds);
        }

        @Override
        public Single<String> getConfirmationSenderNumber() {
            return Single.fromCallable(() -> confirmationSenderNumber);
        }

        @Override
        public Completable setConfirmationSenderNumber(String number) {
            return Completable.fromAction(() -> confirmationSenderNumber = number);
        }

        @Override
        public Completable updateSubmissionState(BaseDataModel event, State sentViaSms) {
            return Completable.complete();
        }

        @Override
        public Single<Metadata> getIdsLists() {
            return Single.fromCallable(() -> metadata);
        }
    }
}
