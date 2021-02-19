package org.hisp.dhis.android.core.sms.domain.repository.internal;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.sms.domain.model.internal.SMSDataValueSet;
import org.hisp.dhis.android.core.sms.domain.repository.WebApiRepository;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.smscompression.models.SMSMetadata;

import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface LocalDbRepository {

    Single<String> getUserName();

    Single<String> getGatewayNumber();

    Completable setGatewayNumber(String number);

    Completable deleteGatewayNumber();

    Single<Integer> getWaitingResultTimeout();

    Completable setWaitingResultTimeout(Integer timeoutSeconds);

    Completable deleteWaitingResultTimeout();

    Single<String> getConfirmationSenderNumber();

    Completable setConfirmationSenderNumber(String number);

    Completable deleteConfirmationSenderNumber();

    Single<SMSMetadata> getMetadataIds();

    Completable setMetadataIds(SMSMetadata metadata);

    Single<Event> getTrackerEventToSubmit(String eventUid);

    Single<Event> getSimpleEventToSubmit(String eventUid);

    Single<TrackedEntityInstance> getTeiEnrollmentToSubmit(String enrollmentUid);

    Completable updateEventSubmissionState(String eventUid, State state);

    Completable updateEnrollmentSubmissionState(TrackedEntityInstance tei, State state);

    Completable setMetadataDownloadConfig(WebApiRepository.GetMetadataIdsConfig metadataIdsConfig);

    Single<WebApiRepository.GetMetadataIdsConfig> getMetadataDownloadConfig();

    Completable setModuleEnabled(boolean enabled);

    Single<Boolean> isModuleEnabled();

    Completable setWaitingForResultEnabled(boolean enabled);

    Single<Boolean> getWaitingForResultEnabled();

    Single<Map<Integer, SubmissionType>> getOngoingSubmissions();

    Single<Integer> generateNextSubmissionId();

    Completable addOngoingSubmission(Integer id, SubmissionType type);

    Completable removeOngoingSubmission(Integer id);

    Single<SMSDataValueSet> getDataValueSet(String dataSet,
                                            String orgUnit,
                                            String period,
                                            String attributeOptionComboUid);

    Completable updateDataSetSubmissionState(String dataSet,
                                             String orgUnit,
                                             String period,
                                             String attributeOptionComboUid,
                                             State state);

    Single<Relationship> getRelationship(String relationshipUid);

    Completable clear();

    class TooManySubmissionsException extends IllegalStateException {
        public TooManySubmissionsException() {
            super("Too many ongoing submissions at the same time >255");
        }
    }
}
