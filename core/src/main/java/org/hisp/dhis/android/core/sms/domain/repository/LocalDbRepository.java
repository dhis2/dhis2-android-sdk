package org.hisp.dhis.android.core.sms.domain.repository;

import io.reactivex.Single;

public interface LocalDbRepository {

    Single<String> getUserName();

    Single<String> getNumber();

    Single<String> setNumber(String number);
}
