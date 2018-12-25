package org.hisp.dhis.android.core.sms.domain.repository;

import io.reactivex.Single;

public interface ApiRepository {

    Single<String> getGatewayNumber();

    /**
     * Returned when received result is empty or invalid
     */
    class IncorrectResultException extends Exception {
    }
}
