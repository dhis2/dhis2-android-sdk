package org.hisp.dhis.android.core.sms;

import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;

import io.reactivex.Completable;
import io.reactivex.Single;

public class TestRepositories {

    public static class TestLocalDbRepository implements LocalDbRepository {
        private String gatewayNumber = null;
        private String confirmationSenderNumber = null;

        @Override
        public Single<String> getDefaultCategoryOptionCombo() {
            return Single.just("testCategoryOptionCombo");
        }

        @Override
        public Single<String> getUserName() {
            return Single.just("testCategoryOptionCombo");
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
    }
}
