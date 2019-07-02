package org.hisp.dhis.android.core.sms.mockrepos;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.datavalue.DataValue;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.sms.domain.repository.LocalDbRepository;
import org.hisp.dhis.android.core.sms.domain.repository.SubmissionType;
import org.hisp.dhis.android.core.sms.domain.repository.WebApiRepository;
import org.hisp.dhis.android.core.sms.mockrepos.testobjects.MockMetadata;
import org.hisp.dhis.android.core.sms.mockrepos.testobjects.MockObjects;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.smscompression.models.SMSMetadata;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Single;

public class MockLocalDbRepository implements LocalDbRepository {
    private String gatewayNumber = null;
    private String confirmationSenderNumber = null;
    private Integer resultWaitingTimeout = 120;
    private SMSMetadata metadata = new MockMetadata();
    private WebApiRepository.GetMetadataIdsConfig metadataIdsConfig = new WebApiRepository.GetMetadataIdsConfig();
    private boolean moduleEnabled = true;
    private boolean waitingForResult = false;
    private HashMap<Integer, SubmissionType> ongoingSubmissions = new HashMap<>();

    public MockLocalDbRepository() {
        metadata.lastSyncDate = new Date();
    }

    @Override
    public Single<String> getUserName() {
        return Single.fromCallable(() -> MockObjects.user);
    }

    @Override
    public Single<String> getGatewayNumber() {
        return Single.fromCallable(() -> gatewayNumber);
    }

    @Override
    public Completable setGatewayNumber(String number) {
        return Completable.fromAction(() -> gatewayNumber = number);
    }

    @Override
    public Single<Integer> getWaitingResultTimeout() {
        return Single.fromCallable(() -> resultWaitingTimeout);
    }

    @Override
    public Completable setWaitingResultTimeout(Integer timeoutSeconds) {
        return Completable.fromAction(() -> resultWaitingTimeout = timeoutSeconds);
    }

    @Override
    public Single<String> getConfirmationSenderNumber() {
        return Single.fromCallable(() -> confirmationSenderNumber);
    }

    @Override
    public Completable setConfirmationSenderNumber(String number) {
        return Completable.fromAction(() -> confirmationSenderNumber = number);
    }

    @Override
    public Single<SMSMetadata> getMetadataIds() {
        return Single.fromCallable(() -> metadata);
    }

    @Override
    public Completable setMetadataIds(SMSMetadata metadata) {
        return Completable.fromAction(() -> this.metadata = metadata);
    }

    @Override
    public Single<Event> getTrackerEventToSubmit(String eventUid) {
        return Single.fromCallable(MockObjects::getTrackerEvent);
    }

    @Override
    public Single<Event> getSimpleEventToSubmit(String eventUid) {
        return Single.fromCallable(MockObjects::getSimpleEvent);
    }

    @Override
    public Single<TrackedEntityInstance> getTeiEnrollmentToSubmit(String enrollmentUid) {
        return Single.fromCallable(MockObjects::getTEIEnrollment);
    }

    @Override
    public Completable updateEventSubmissionState(String eventUid, State state) {
        return Completable.complete();
    }

    @Override
    public Completable updateEnrollmentSubmissionState(String enrollmentUid, State state) {
        return Completable.complete();
    }

    @Override
    public Completable setMetadataDownloadConfig(WebApiRepository.GetMetadataIdsConfig metadataIdsConfig) {
        return Completable.fromAction(() -> this.metadataIdsConfig = metadataIdsConfig);
    }

    @Override
    public Single<WebApiRepository.GetMetadataIdsConfig> getMetadataDownloadConfig() {
        return Single.fromCallable(() -> metadataIdsConfig);
    }

    @Override
    public Completable setModuleEnabled(boolean enabled) {
        return Completable.fromAction(() -> this.moduleEnabled = enabled);
    }

    @Override
    public Single<Boolean> isModuleEnabled() {
        return Single.fromCallable(() -> moduleEnabled);
    }

    @Override
    public Completable setWaitingForResultEnabled(boolean enabled) {
        return Completable.fromAction(() -> waitingForResult = enabled);
    }

    @Override
    public Single<Boolean> getWaitingForResultEnabled() {
        return Single.fromCallable(() -> waitingForResult);
    }

    @Override
    public Single<Map<Integer, SubmissionType>> getOngoingSubmissions() {
        return Single.fromCallable(() -> ongoingSubmissions);
    }

    @Override
    public Single<Integer> generateNextSubmissionId() {
        return Single.fromCallable(() -> {
            int next = 0;
            do {
                if (!ongoingSubmissions.keySet().contains(next)) {
                    return next;
                }
                next++;
            } while (next <= 255);
            throw new LocalDbRepository.TooManySubmissionsException();
        });
    }

    @Override
    public Completable addOngoingSubmission(Integer id, SubmissionType type) {
        return Completable.fromAction(() -> ongoingSubmissions.put(id, type));
    }

    @Override
    public Completable removeOngoingSubmission(Integer id) {
        return Completable.fromAction(() -> ongoingSubmissions.remove(id));
    }

    @Override
    public Single<List<DataValue>> getDataValues(String orgUnit, String period, String attributeOptionComboUid) {
        return Single.fromCallable(MockObjects::getDataValues);
    }

    @Override
    public Completable updateDataSetSubmissionState(String dataSet, String orgUnit, String period, String attributeOptionComboUid, State state) {
        return Completable.complete();
    }

    @Override
    public Single<Relationship> getRelationship(String relationshipUid) {
        return Single.fromCallable(MockObjects::getRelationship);
    }
}