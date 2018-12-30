package org.hisp.dhis.android.core.sms.domain.repository;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface LocalDbRepository {

    Single<String> getUserName();

    Single<String> getGatewayNumber();

    Completable setGatewayNumber(String number);

    Single<String> getConfirmationSenderNumber();

    Completable setConfirmationSenderNumber(String number);
}
