package org.hisp.dhis.android.core.sms.domain.repository;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.datavalue.DataValue;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.smscompression.models.Metadata;

import java.util.List;
import java.util.Map;

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

    Single<Event> getTrackerEventToSubmit(String eventUid);

    Single<Event> getSimpleEventToSubmit(String eventUid);

    Single<TrackedEntityInstance> getTeiEnrollmentToSubmit(String enrollmentUid);

    Completable updateEventSubmissionState(String eventUid, State state);

    Completable updateEnrollmentSubmissionState(String enrollmentUid, State state);

    Completable setMetadataDownloadConfig(WebApiRepository.GetMetadataIdsConfig metadataIdsConfig);

    Single<WebApiRepository.GetMetadataIdsConfig> getMetadataDownloadConfig();

    Completable setModuleEnabled(boolean enabled);

    Single<Boolean> isModuleEnabled();

    Single<Map<Integer, SubmissionType>> getOngoingSubmissions();

    Completable addOngoingSubmission(Integer id, SubmissionType type);

    Completable removeOngoingSubmission(Integer id);

    Single<List<DataValue>> getDataValues(String orgUnit,
                                          String period,
                                          String attributeOptionComboUid);

    Completable updateDataSetSubmissionState(String dataSet,
                                             String orgUnit,
                                             String period,
                                             String attributeOptionComboUid,
                                             State state);

    Single<Relationship> getRelationship(String relationshipUid);

    enum SubmissionType {
        SIMPLE_EVENT, TRACKER_EVENT, ENROLLMENT, DATA_SET, RELATIONSHIP, DELETION
    }
}
