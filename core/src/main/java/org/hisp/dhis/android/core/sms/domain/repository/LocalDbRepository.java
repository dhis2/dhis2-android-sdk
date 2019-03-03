package org.hisp.dhis.android.core.sms.domain.repository;

import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.smscompression.models.Metadata;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface LocalDbRepository {

    Single<String> getUserName();

    Single<String> getGatewayNumber();

    Completable setGatewayNumber(String number);

    Single<Integer> getWaitingResultTimeout();

    Completable setWaitingResultTimeout(Integer timeoutSeconds);

    Single<String> getConfirmationSenderNumber();

    Completable setConfirmationSenderNumber(String number);

    Completable updateSubmissionState(BaseDataModel event, State sentViaSms);

    Single<Metadata> getMetadataIds();

    Completable setMetadataIds(Metadata metadata);
}
