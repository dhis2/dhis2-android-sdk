package org.hisp.dhis.android.core.sms.domain.repository;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
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

    Single<Metadata> getMetadataIds();

    Completable setMetadataIds(Metadata metadata);

    Single<Event> getEventToSubmit(String eventUid, String teiUid);

    Single<TrackedEntityInstance> getTeiEnrollmentToSubmit(String enrollmentUid, String teiUid);

    Completable updateEventSubmissionState(String eventUid, State state);

    Completable updateEnrollmentSubmissionState(String enrollmentUid, State state);

    Completable setMetadataDownloadConfig(WebApiRepository.GetMetadataIdsConfig metadataIdsConfig);

    Single<WebApiRepository.GetMetadataIdsConfig> getMetadataDownloadConfig();

    Completable setModuleEnabled(boolean enabled);

    Single<Boolean> isModuleEnabled();
}
