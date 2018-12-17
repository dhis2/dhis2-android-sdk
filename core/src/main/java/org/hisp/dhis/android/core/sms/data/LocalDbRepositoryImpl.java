package org.hisp.dhis.android.core.sms.data;

import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;

import io.reactivex.Single;

public class LocalDbRepositoryImpl implements LocalDbRepository {
    @Override
    public Single<String> getUserName() {
        return null;
    }

    @Override
    public Single<String> getNumber() {
        return null;
    }

    @Override
    public Single<String> setNumber(String number) {
        return null;
    }
}
