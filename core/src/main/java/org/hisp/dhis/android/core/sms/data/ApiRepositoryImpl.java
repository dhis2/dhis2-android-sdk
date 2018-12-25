package org.hisp.dhis.android.core.sms.data;

import org.hisp.dhis.android.core.sms.domain.repository.ApiRepository;

import io.reactivex.Single;

public class ApiRepositoryImpl implements ApiRepository {
    @Override
    public Single<String> getGatewayNumber() {
        return null;
    }
}
