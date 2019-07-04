package org.hisp.dhis.android.core.sms.mockrepos;

import org.hisp.dhis.android.core.sms.domain.repository.DeviceStateRepository;

import io.reactivex.Single;

public class MockDeviceStateRepository implements DeviceStateRepository {

    @Override
    public Single<Boolean> isNetworkConnected() {
        return Single.fromCallable(() -> true);
    }

    @Override
    public Single<Boolean> hasCheckNetworkPermission() {
        return Single.fromCallable(() -> true);
    }

    @Override
    public Single<Boolean> hasSendSMSPermission() {
        return Single.fromCallable(() -> true);
    }

    @Override
    public Single<Boolean> hasReceiveSMSPermission() {
        return Single.fromCallable(() -> true);
    }
}
