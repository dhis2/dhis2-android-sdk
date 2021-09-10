/*
 *  Copyright (c) 2004-2021, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.sms.data.localdbrepository.internal;

import android.content.Context;
import android.content.SharedPreferences;

import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.json.internal.ObjectMapperFactory;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.common.internal.DataStatePropagator;
import org.hisp.dhis.android.core.dataset.DataSetCompleteRegistrationTableInfo;
import org.hisp.dhis.android.core.dataset.internal.DataSetCompleteRegistrationStore;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentInternalAccessor;
import org.hisp.dhis.android.core.enrollment.EnrollmentModule;
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventModule;
import org.hisp.dhis.android.core.event.internal.EventStore;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.relationship.RelationshipConstraintType;
import org.hisp.dhis.android.core.relationship.RelationshipItem;
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemStore;
import org.hisp.dhis.android.core.relationship.internal.RelationshipStore;
import org.hisp.dhis.android.core.sms.domain.model.internal.SMSDataValueSet;
import org.hisp.dhis.android.core.sms.domain.repository.WebApiRepository;
import org.hisp.dhis.android.core.sms.domain.repository.internal.LocalDbRepository;
import org.hisp.dhis.android.core.sms.domain.repository.internal.SubmissionType;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityModule;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore;
import org.hisp.dhis.android.core.user.AuthenticatedUserObjectRepository;
import org.hisp.dhis.smscompression.models.SMSMetadata;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

@SuppressWarnings({"PMD.ExcessiveImports", "PMD.TooManyFields"})
public class LocalDbRepositoryImpl implements LocalDbRepository {
    private final Context context;
    private final AuthenticatedUserObjectRepository userRepository;
    private final TrackedEntityModule trackedEntityModule;
    private final EventModule eventModule;
    private final EnrollmentModule enrollmentModule;
    private final FileResourceCleaner fileResourceCleaner;
    private final EventStore eventStore;
    private final EnrollmentStore enrollmentStore;
    private static final String CONFIG_FILE = "smsconfig";
    private static final String KEY_GATEWAY = "gateway";
    private static final String KEY_CONFIRMATION_SENDER = "confirmationsender";
    private static final String KEY_WAITING_RESULT_TIMEOUT = "reading_timeout";
    private static final String KEY_METADATA_CONFIG = "metadata_conf";
    private static final String KEY_MODULE_ENABLED = "module_enabled";
    private static final String KEY_WAIT_FOR_RESULT = "wait_for_result";

    private final MetadataIdsStore metadataIdsStore;
    private final OngoingSubmissionsStore ongoingSubmissionsStore;
    private final RelationshipStore relationshipStore;
    private final RelationshipItemStore relationshipItemStore;
    private final DataSetsStore dataSetsStore;
    private final TrackedEntityInstanceStore trackedEntityInstanceStore;
    private final DataSetCompleteRegistrationStore dataSetCompleteRegistrationStore;
    private final DataStatePropagator dataStatePropagator;

    @Inject
    LocalDbRepositoryImpl(Context ctx,
                          AuthenticatedUserObjectRepository userRepository,
                          TrackedEntityModule trackedEntityModule,
                          EventModule eventModule,
                          EnrollmentModule enrollmentModule,
                          FileResourceCleaner fileResourceCleaner,
                          EventStore eventStore,
                          EnrollmentStore enrollmentStore,
                          RelationshipStore relationshipStore,
                          RelationshipItemStore relationshipItemStore,
                          DataSetsStore dataSetsStore,
                          TrackedEntityInstanceStore trackedEntityInstanceStore,
                          DataSetCompleteRegistrationStore dataSetCompleteRegistrationStore,
                          DataStatePropagator dataStatePropagator) {
        this.context = ctx;
        this.userRepository = userRepository;
        this.trackedEntityModule = trackedEntityModule;
        this.eventModule = eventModule;
        this.enrollmentModule = enrollmentModule;
        this.fileResourceCleaner = fileResourceCleaner;
        this.eventStore = eventStore;
        this.enrollmentStore = enrollmentStore;
        this.relationshipStore = relationshipStore;
        this.relationshipItemStore = relationshipItemStore;
        this.dataSetsStore = dataSetsStore;
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
        this.dataSetCompleteRegistrationStore = dataSetCompleteRegistrationStore;
        this.dataStatePropagator = dataStatePropagator;
        metadataIdsStore = new MetadataIdsStore(context);
        ongoingSubmissionsStore = new OngoingSubmissionsStore(context);
    }

    @Override
    public Single<String> getUserName() {
        return Single.fromCallable(() -> userRepository.blockingGet().user());
    }

    @Override
    public Single<String> getGatewayNumber() {
        return Single.fromCallable(() ->
                prefs().getString(KEY_GATEWAY, "")
        );
    }

    @Override
    public Completable setGatewayNumber(String number) {
        return Completable.fromAction(() -> {
            boolean result = prefs().edit().putString(KEY_GATEWAY, number).commit();
            if (!result) {
                throw new IOException("Failed writing gateway number to local storage");
            }
        });
    }

    @Override
    public Completable deleteGatewayNumber() {
        return deleteKey(KEY_GATEWAY);
    }

    private Completable deleteKey(String key) {
        return Completable.fromAction(() -> {
            boolean result = prefs().edit().remove(key).commit();
            if (!result) {
                throw new IOException("Failed deleting value from local storage for key: " + key);
            }
        });
    }

    @Override
    public Single<Integer> getWaitingResultTimeout() {
        return Single.fromCallable(() ->
                prefs().getInt(KEY_WAITING_RESULT_TIMEOUT, 120)
        );
    }

    @Override
    public Completable setWaitingResultTimeout(Integer timeoutSeconds) {
        return Completable.fromAction(() -> {
            boolean result = prefs().edit().putInt(KEY_WAITING_RESULT_TIMEOUT, timeoutSeconds).commit();
            if (!result) {
                throw new IOException("Failed writing timeout setting to local storage");
            }
        });
    }

    @Override
    public Completable deleteWaitingResultTimeout() {
        return deleteKey(KEY_WAITING_RESULT_TIMEOUT);
    }

    @Override
    public Single<String> getConfirmationSenderNumber() {
        return Single.fromCallable(() ->
                prefs().getString(KEY_CONFIRMATION_SENDER, "")
        );
    }

    @Override
    public Completable setConfirmationSenderNumber(String number) {
        return Completable.fromAction(() -> {
            boolean result = prefs().edit().putString(KEY_CONFIRMATION_SENDER, number).commit();
            if (!result) {
                throw new IOException("Failed writing confirmation sender number to local storage");
            }
        });
    }

    @Override
    public Completable deleteConfirmationSenderNumber() {
        return deleteKey(KEY_CONFIRMATION_SENDER);
    }

    @Override
    public Single<SMSMetadata> getMetadataIds() {
        return metadataIdsStore.getMetadataIds();
    }

    @Override
    public Completable setMetadataIds(final SMSMetadata metadata) {
        return metadataIdsStore.setMetadataIds(metadata);
    }

    @Override
    public Single<Event> getTrackerEventToSubmit(String eventUid) {
        // simple event is the same object as tracker event
        return getSimpleEventToSubmit(eventUid);
    }

    @Override
    public Single<Event> getSimpleEventToSubmit(String eventUid) {
        return eventModule.events().withTrackedEntityDataValues()
                .byUid().eq(eventUid).one()
                .get()
                .flatMap(fileResourceCleaner::removeFileDataValues);
    }

    @Override
    public Single<TrackedEntityInstance> getTeiEnrollmentToSubmit(String enrollmentUid) {
        return Single.fromCallable(() -> {
            Enrollment enrollment = enrollmentModule.enrollments().byUid().eq(enrollmentUid).one().blockingGet();
            List<Event> events = getEventsForEnrollment(enrollmentUid).blockingGet();

            Enrollment enrollmentWithEvents = EnrollmentInternalAccessor
                    .insertEvents(enrollment.toBuilder(), events)
                    .build();

            TrackedEntityInstance trackedEntityInstance =
                    getTrackedEntityInstance(enrollment.trackedEntityInstance()).blockingGet();

            return TrackedEntityInstanceInternalAccessor
                    .insertEnrollments(trackedEntityInstance.toBuilder(),
                            Collections.singletonList(enrollmentWithEvents))
                    .build();
        });
    }

    private Single<TrackedEntityInstance> getTrackedEntityInstance(String instanceUid) {
        return trackedEntityModule.trackedEntityInstances()
                .withTrackedEntityAttributeValues()
                .uid(instanceUid)
                .get()
                .flatMap(fileResourceCleaner::removeFileAttributeValues);
    }

    private Single<List<Event>> getEventsForEnrollment(String enrollmentUid) {
        return eventModule.events()
                .byEnrollmentUid().eq(enrollmentUid)
                .bySyncState().in(State.uploadableStates())
                .withTrackedEntityDataValues()
                .get()
                .flatMapObservable(Observable::fromIterable)
                .flatMapSingle(fileResourceCleaner::removeFileDataValues)
                .toList();
    }

    @Override
    public Completable updateEventSubmissionState(String eventUid, State state) {
        return Completable.fromAction(() -> {
            eventStore.setSyncState(eventUid, state);
            Event event = eventStore.selectByUid(eventUid);
            dataStatePropagator.propagateEventUpdate(event);
        });
    }

    @Override
    public Completable updateEnrollmentSubmissionState(TrackedEntityInstance tei, State state) {
        return Completable.fromAction(() -> {
            Enrollment enrollment = TrackedEntityInstanceInternalAccessor.accessEnrollments(tei).get(0);
            List<Event> events = EnrollmentInternalAccessor.accessEvents(enrollment);

            if (events != null && !events.isEmpty()) {
                for (Event event : events) {
                    eventStore.setSyncState(event.uid(), state);
                    dataStatePropagator.propagateEventUpdate(event);
                }
            }

            enrollmentStore.setSyncState(enrollment.uid(), state);
            dataStatePropagator.propagateEnrollmentUpdate(enrollment);

            trackedEntityInstanceStore.setSyncState(enrollment.trackedEntityInstance(), state);
            dataStatePropagator.propagateTrackedEntityInstanceUpdate(tei);
        });
    }

    @Override
    public Completable updateRelationshipSubmissionState(String relationshipUid, State state) {
        return Completable.fromAction(() -> {
            relationshipStore.setSyncState(relationshipUid, state);
            RelationshipItem fromItem = relationshipItemStore
                    .getForRelationshipUidAndConstraintType(relationshipUid, RelationshipConstraintType.FROM);
            dataStatePropagator.propagateRelationshipUpdate(fromItem);
        });
    }

    @Override
    public Completable setMetadataDownloadConfig(WebApiRepository.GetMetadataIdsConfig config) {
        return Completable.fromAction(() -> {
            String value = ObjectMapperFactory.objectMapper().writeValueAsString(config);
            SharedPreferences.Editor editor = prefs().edit().putString(KEY_METADATA_CONFIG, value);
            if (!editor.commit()) {
                throw new IOException("Failed writing SMS metadata config to local storage");
            }
        });
    }

    @Override
    public Single<WebApiRepository.GetMetadataIdsConfig> getMetadataDownloadConfig() {
        return Single.fromCallable(() -> {
            String stringVal = prefs().getString(KEY_METADATA_CONFIG, null);
            return ObjectMapperFactory.objectMapper()
                    .readValue(stringVal, WebApiRepository.GetMetadataIdsConfig.class);
        });
    }

    @Override
    public Completable setModuleEnabled(boolean enabled) {
        return Completable.fromAction(() -> {
            boolean result = prefs().edit().putBoolean(KEY_MODULE_ENABLED, enabled).commit();
            if (!result) {
                throw new IOException("Failed writing module enabled value to local storage");
            }
        });
    }

    @Override
    public Single<Boolean> isModuleEnabled() {
        return Single.fromCallable(() ->
                prefs().getBoolean(KEY_MODULE_ENABLED, false)
        );
    }

    @Override
    public Completable setWaitingForResultEnabled(boolean enabled) {
        return Completable.fromAction(() -> {
            boolean result = prefs().edit().putBoolean(KEY_WAIT_FOR_RESULT, enabled).commit();
            if (!result) {
                throw new IOException("Failed writing value to local storage, waiting for result");
            }
        });
    }

    @Override
    public Single<Boolean> getWaitingForResultEnabled() {
        return Single.fromCallable(() ->
                prefs().getBoolean(KEY_WAIT_FOR_RESULT, false)
        );
    }

    @Override
    public Single<Map<Integer, SubmissionType>> getOngoingSubmissions() {
        return ongoingSubmissionsStore.getOngoingSubmissions();
    }

    @Override
    public Single<Integer> generateNextSubmissionId() {
        return ongoingSubmissionsStore.generateNextSubmissionId();
    }

    @Override
    public Completable addOngoingSubmission(Integer id, SubmissionType type) {
        return ongoingSubmissionsStore.addOngoingSubmission(id, type);
    }

    @Override
    public Completable removeOngoingSubmission(Integer id) {
        return ongoingSubmissionsStore.removeOngoingSubmission(id);
    }

    @Override
    public Single<SMSDataValueSet> getDataValueSet(String dataset, String orgUnit,
                                                   String period, String attributeOptionComboUid) {
        return dataSetsStore.getDataValues(dataset, orgUnit, period, attributeOptionComboUid).map(values -> {
            Boolean isCompleted = isDataValueSetCompleted(dataset, orgUnit, period, attributeOptionComboUid);
            return SMSDataValueSet.builder()
                    .dataValues(values)
                    .completed(isCompleted)
                    .build();
        });
    }

    private Boolean isDataValueSetCompleted(String dataset, String orgUnit,
                                            String period, String attributeOptionComboUid) {
        String whereClause = new WhereClauseBuilder()
                .appendKeyStringValue(DataSetCompleteRegistrationTableInfo.Columns.DATA_SET, dataset)
                .appendKeyStringValue(DataSetCompleteRegistrationTableInfo.Columns.ORGANISATION_UNIT, orgUnit)
                .appendKeyStringValue(DataSetCompleteRegistrationTableInfo.Columns.PERIOD, period)
                .appendKeyStringValue(DataSetCompleteRegistrationTableInfo.Columns.ATTRIBUTE_OPTION_COMBO,
                        attributeOptionComboUid)
                .appendKeyNumberValue(DataSetCompleteRegistrationTableInfo.Columns.DELETED, 0)
                .build();
        return dataSetCompleteRegistrationStore.countWhere(whereClause) > 0;
    }

    @Override
    public Completable updateDataSetSubmissionState(String dataSetId,
                                                    String orgUnit,
                                                    String period,
                                                    String attributeOptionComboUid,
                                                    State state) {
        return Completable.mergeArray(
                dataSetsStore.updateDataSetValuesState(
                        dataSetId, orgUnit, period, attributeOptionComboUid, state),
                dataSetsStore.updateDataSetCompleteRegistrationState(
                        dataSetId, orgUnit, period, attributeOptionComboUid, state)
        );
    }

    @Override
    public Single<Relationship> getRelationship(String relationshipUid) {
        return Single.fromCallable(() -> relationshipStore.selectByUid(relationshipUid));
    }

    @Override
    public Completable clear() {
        return Completable.mergeArray(
                Completable.fromAction(() -> prefs().edit().clear().commit()),
                metadataIdsStore.clear()
        );
    }

    @Override
    public void blockingClear() {
        clear().blockingAwait();
    }

    private SharedPreferences prefs() {
        return context.getSharedPreferences(CONFIG_FILE, Context.MODE_PRIVATE);
    }
}
