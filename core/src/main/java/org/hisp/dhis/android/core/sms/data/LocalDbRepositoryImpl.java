package org.hisp.dhis.android.core.sms.data;

import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;
import org.hisp.dhis.android.core.user.UserModule;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Single;

public class LocalDbRepositoryImpl implements LocalDbRepository {

    private final UserModule userModule;

    @Inject
    public LocalDbRepositoryImpl(UserModule userModule) {
        this.userModule = userModule;
    }

    @Override
    public Single<String> getDefaultCategoryOptionCombo() {
        return null;
    }

    @Override
    public Single<String> getUserName() {
        return Single.fromCallable(() -> userModule.authenticatedUser.get().user());
    }

    @Override
    public Single<String> getGatewayNumber() {
        return null;
    }

    @Override
    public Single<String> getConfirmationSenderNumber() {
        return null;
    }

    @Override
    public Completable setConfirmationSenderNumber(String number) {
        return null;
    }

    @Override
    public Completable updateSubmissionState(BaseDataModel event, State sentViaSms) {
        return null;
    }

    @Override
    public Completable setGatewayNumber(String number) {
        return null;
    }
}
