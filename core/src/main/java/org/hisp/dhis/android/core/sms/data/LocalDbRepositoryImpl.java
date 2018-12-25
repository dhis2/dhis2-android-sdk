package org.hisp.dhis.android.core.sms.data;

import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;

import io.reactivex.Completable;
import io.reactivex.Single;

public class LocalDbRepositoryImpl implements LocalDbRepository {
    @Override
    public Single<String> getUserName() {
        return null;
    }

    @Override
    public Single<String> getGatewayNumber() {
        return null;
    }

    @Override
    public Completable setGatewayNumber(String number) {
        return null;
    }
}
