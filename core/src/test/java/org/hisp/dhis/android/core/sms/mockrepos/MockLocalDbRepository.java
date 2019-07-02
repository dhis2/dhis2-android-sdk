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
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Single;

public class MockLocalDbRepository implements LocalDbRepository {
    private String gatewayNumber = null;
    private String confirmationSenderNumber = null;
    private Integer resultWaitingTimeout = 120;
    private SMSMetadata metadata = new MockMetadata();

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
        return null;
    }

    @Override
    public Single<Event> getSimpleEventToSubmit(String eventUid) {
        return null;
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
        return Completable.complete();
    }

    @Override
    public Single<WebApiRepository.GetMetadataIdsConfig> getMetadataDownloadConfig() {
        return Single.just(new WebApiRepository.GetMetadataIdsConfig());
    }

    @Override
    public Completable setModuleEnabled(boolean enabled) {
        return Completable.complete();
    }

    @Override
    public Single<Boolean> isModuleEnabled() {
        return Single.just(true);
    }

    @Override
    public Completable setWaitingForResultEnabled(boolean enabled) {
        return null;
    }

    @Override
    public Single<Boolean> getWaitingForResultEnabled() {
        return null;
    }

    @Override
    public Single<Map<Integer, SubmissionType>> getOngoingSubmissions() {
        return null;
    }

    @Override
    public Single<Integer> generateNextSubmissionId() {
        return null;
    }

    @Override
    public Completable addOngoingSubmission(Integer id, SubmissionType type) {
        return null;
    }

    @Override
    public Completable removeOngoingSubmission(Integer id) {
        return null;
    }

    @Override
    public Single<List<DataValue>> getDataValues(String orgUnit, String period, String attributeOptionComboUid) {
        return null;
    }

    @Override
    public Completable updateDataSetSubmissionState(String dataSet, String orgUnit, String period, String attributeOptionComboUid, State state) {
        return null;
    }

    @Override
    public Single<Relationship> getRelationship(String relationshipUid) {
        return null;
    }
}