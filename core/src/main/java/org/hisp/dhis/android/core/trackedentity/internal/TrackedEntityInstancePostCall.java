/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.trackedentity.internal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor;
import org.hisp.dhis.android.core.arch.call.D2Progress;
import org.hisp.dhis.android.core.arch.call.internal.D2ProgressManager;
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableDeletableDataObjectStore;
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper;
import org.hisp.dhis.android.core.arch.helpers.UidsHelper;
import org.hisp.dhis.android.core.arch.helpers.internal.EnumHelper;
import org.hisp.dhis.android.core.common.CoreColumns;
import org.hisp.dhis.android.core.common.DataColumns;
import org.hisp.dhis.android.core.common.DataObject;
import org.hisp.dhis.android.core.common.ObjectWithUidInterface;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentInternalAccessor;
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.internal.EventStore;
import org.hisp.dhis.android.core.imports.internal.TEIWebResponse;
import org.hisp.dhis.android.core.imports.internal.TEIWebResponseHandler;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.note.Note;
import org.hisp.dhis.android.core.note.internal.NoteToPostTransformer;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.relationship.RelationshipCollectionRepository;
import org.hisp.dhis.android.core.relationship.RelationshipHelper;
import org.hisp.dhis.android.core.relationship.internal.Relationship229Compatible;
import org.hisp.dhis.android.core.relationship.internal.RelationshipDHISVersionManager;
import org.hisp.dhis.android.core.relationship.internal.RelationshipDeleteCall;
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemStore;
import org.hisp.dhis.android.core.relationship.internal.RelationshipStore;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Observable;

@SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops", "PMD.ExcessiveImports", "PMD.TooManyFields"})
@Reusable
public final class TrackedEntityInstancePostCall {
    // internal modules
    private final DHISVersionManager versionManager;
    private final RelationshipDHISVersionManager relationshipDHISVersionManager;
    private final RelationshipCollectionRepository relationshipRepository;

    // service
    private final TrackedEntityInstanceService trackedEntityInstanceService;

    // stores
    private final TrackedEntityInstanceStore trackedEntityInstanceStore;
    private final EnrollmentStore enrollmentStore;
    private final EventStore eventStore;
    private final TrackedEntityDataValueStore trackedEntityDataValueStore;
    private final TrackedEntityAttributeValueStore trackedEntityAttributeValueStore;
    private final RelationshipStore relationshipStore;
    private final RelationshipItemStore relationshipItemStore;
    private final IdentifiableObjectStore<Note> noteStore;

    private final TEIWebResponseHandler teiWebResponseHandler;

    private final APICallExecutor apiCallExecutor;
    private final RelationshipDeleteCall relationshipDeleteCall;

    private static final int DEFAULT_PAGE_SIZE = 10;

    @Inject
    TrackedEntityInstancePostCall(@NonNull DHISVersionManager versionManager,
                                  @NonNull RelationshipDHISVersionManager relationshipDHISVersionManager,
                                  @NonNull RelationshipCollectionRepository relationshipRepository,
                                  @NonNull TrackedEntityInstanceService trackedEntityInstanceService,
                                  @NonNull TrackedEntityInstanceStore trackedEntityInstanceStore,
                                  @NonNull EnrollmentStore enrollmentStore,
                                  @NonNull EventStore eventStore,
                                  @NonNull TrackedEntityDataValueStore trackedEntityDataValueStore,
                                  @NonNull TrackedEntityAttributeValueStore trackedEntityAttributeValueStore,
                                  @NonNull RelationshipStore relationshipStore,
                                  @NonNull RelationshipItemStore relationshipItemStore,
                                  @NonNull IdentifiableObjectStore<Note> noteStore,
                                  @NonNull TEIWebResponseHandler teiWebResponseHandler,
                                  @NonNull APICallExecutor apiCallExecutor,
                                  @NonNull RelationshipDeleteCall relationshipDeleteCall) {
        this.versionManager = versionManager;
        this.relationshipDHISVersionManager = relationshipDHISVersionManager;
        this.relationshipRepository = relationshipRepository;
        this.trackedEntityInstanceService = trackedEntityInstanceService;
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
        this.enrollmentStore = enrollmentStore;
        this.eventStore = eventStore;
        this.trackedEntityDataValueStore = trackedEntityDataValueStore;
        this.trackedEntityAttributeValueStore = trackedEntityAttributeValueStore;
        this.relationshipStore = relationshipStore;
        this.relationshipItemStore = relationshipItemStore;
        this.noteStore = noteStore;
        this.teiWebResponseHandler = teiWebResponseHandler;
        this.apiCallExecutor = apiCallExecutor;
        this.relationshipDeleteCall = relationshipDeleteCall;
    }

    public Observable<D2Progress> uploadTrackedEntityInstances(
            List<TrackedEntityInstance> filteredTrackedEntityInstances) {
        return Observable.defer(() -> {
            List<List<TrackedEntityInstance>> teiPartitions = getPartitionsToSync(filteredTrackedEntityInstances);

            // if size is 0, then no need to do network request
            if (teiPartitions.isEmpty()) {
                return Observable.empty();
            } else {

                return Observable.create(emitter -> {

                    String strategy;
                    if (versionManager.is2_29()) {
                        strategy = "CREATE_AND_UPDATE";
                    } else {
                        strategy = "SYNC";
                    }

                    D2ProgressManager progressManager = new D2ProgressManager(teiPartitions.size());

                    for (List<TrackedEntityInstance> partition : teiPartitions) {
                        partition = relationshipDeleteCall.postDeletedRelationships(partition);

                        TrackedEntityInstancePayload trackedEntityInstancePayload =
                                TrackedEntityInstancePayload.create(partition);

                        try {
                            TEIWebResponse webResponse = apiCallExecutor.executeObjectCallWithAcceptedErrorCodes(
                                    trackedEntityInstanceService.postTrackedEntityInstances(
                                            trackedEntityInstancePayload, strategy),
                                    Collections.singletonList(409), TEIWebResponse.class);
                            teiWebResponseHandler.handleWebResponse(webResponse);
                            emitter.onNext(progressManager.increaseProgress(TrackedEntityInstance.class, false));
                        } catch (D2Error d2Error) {
                            restorePartitionStates(partition);
                            if (d2Error.isOffline()) {
                                emitter.onError(d2Error);
                                break;
                            } else {
                                emitter.onNext(progressManager.increaseProgress(TrackedEntityInstance.class, false));
                            }
                        }
                    }

                    emitter.onComplete();
                });
            }
        });
    }

    @NonNull
    List<List<TrackedEntityInstance>> getPartitionsToSync(List<TrackedEntityInstance> filteredTrackedEntityInstances) {
        Map<String, List<TrackedEntityDataValue>> dataValueMap =
                trackedEntityDataValueStore.queryTrackerTrackedEntityDataValues();
        Map<String, List<Event>> eventMap = eventStore.queryEventsAttachedToEnrollmentToPost();
        Map<String, List<Enrollment>> enrollmentMap = enrollmentStore.queryEnrollmentsToPost();
        Map<String, List<TrackedEntityAttributeValue>> attributeValueMap =
                trackedEntityAttributeValueStore.queryTrackedEntityAttributeValueToPost();
        String whereNotesClause = new WhereClauseBuilder()
                .appendInKeyStringValues(
                        DataColumns.STATE, EnumHelper.asStringList(State.uploadableStatesIncludingError()))
                .build();
        List<Note> notes = noteStore.selectWhere(whereNotesClause);

        List<TrackedEntityInstance> targetTrackedEntityInstances;
        if (filteredTrackedEntityInstances == null) {
            targetTrackedEntityInstances = trackedEntityInstanceStore.queryTrackedEntityInstancesToSync();
        } else {
            targetTrackedEntityInstances = filteredTrackedEntityInstances;
        }

        List<List<TrackedEntityInstance>> trackedEntityInstancesToSync
                = getPagedTrackedEntityInstances(targetTrackedEntityInstances);

        List<List<TrackedEntityInstance>> trackedEntityInstancesRecreated = new ArrayList<>();

        for (List<TrackedEntityInstance> partition : trackedEntityInstancesToSync) {
            List<TrackedEntityInstance> partitionRecreated = new ArrayList<>();
            for (TrackedEntityInstance trackedEntityInstance : partition) {
                TrackedEntityInstance recreatedTrackedEntityInstance = recreateTrackedEntityInstance(
                        trackedEntityInstance, dataValueMap, eventMap, enrollmentMap, attributeValueMap, notes);

                partitionRecreated.add(recreatedTrackedEntityInstance);
            }
            trackedEntityInstancesRecreated.add(partitionRecreated);
            setPartitionStates(partitionRecreated, State.UPLOADING);
        }

        return trackedEntityInstancesRecreated;
    }

    private List<List<TrackedEntityInstance>> getPagedTrackedEntityInstances(
            List<TrackedEntityInstance> filteredTrackedEntityInstances) {
        List<String> includedUids = new ArrayList<>();

        List<Set<TrackedEntityInstance>> partitions =
                CollectionsHelper.setPartition(filteredTrackedEntityInstances, DEFAULT_PAGE_SIZE);

        List<List<TrackedEntityInstance>> partitionsWithRelationships = new ArrayList<>();

        for (Set<TrackedEntityInstance> partition : partitions) {
            List<TrackedEntityInstance> partitionWithoutDuplicates = UidsHelper.excludeUids(partition, includedUids);
            List<TrackedEntityInstance> partitionWithRelationships =
                    getTrackedEntityInstancesWithRelationships(partitionWithoutDuplicates, includedUids);

            partitionsWithRelationships.add(partitionWithRelationships);
            includedUids.addAll(UidsHelper.getUidsList(partitionWithRelationships));
        }

        return partitionsWithRelationships;
    }

    private List<TrackedEntityInstance> getTrackedEntityInstancesWithRelationships(
            List<TrackedEntityInstance> filteredTrackedEntityInstances, List<String> excludedUids) {
        List<TrackedEntityInstance> trackedEntityInstancesInDBToSync =
                trackedEntityInstanceStore.queryTrackedEntityInstancesToSync();

        List<String> filteredUids = UidsHelper.getUidsList(filteredTrackedEntityInstances);
        List<String> teiUidsToPost =
                UidsHelper.getUidsList(trackedEntityInstanceStore.queryTrackedEntityInstancesToPost());
        List<String> relatedTeisToPost = new ArrayList<>();
        List<String> internalRelatedTeis = filteredUids;

        do {
            List<String> relatedTeiUids = relationshipItemStore.getRelatedTeiUids(internalRelatedTeis);

            relatedTeiUids.retainAll(teiUidsToPost);

            relatedTeiUids.removeAll(filteredUids);
            relatedTeiUids.removeAll(relatedTeisToPost);
            relatedTeiUids.removeAll(excludedUids);

            relatedTeisToPost.addAll(relatedTeiUids);
            internalRelatedTeis = relatedTeiUids;
        }
        while (!internalRelatedTeis.isEmpty());

        for (TrackedEntityInstance trackedEntityInstanceInDB : trackedEntityInstancesInDBToSync) {
            if (relatedTeisToPost.contains(trackedEntityInstanceInDB.uid())) {
                filteredTrackedEntityInstances.add(trackedEntityInstanceInDB);
            }
        }
        return filteredTrackedEntityInstances;
    }

    @NonNull
    private TrackedEntityInstance recreateTrackedEntityInstance(
            TrackedEntityInstance trackedEntityInstance,
            Map<String, List<TrackedEntityDataValue>> dataValueMap,
            Map<String, List<Event>> eventMap,
            Map<String, List<Enrollment>> enrollmentMap,
            Map<String, List<TrackedEntityAttributeValue>> attributeValueMap,
            List<Note> notes) {

        String trackedEntityInstanceUid = trackedEntityInstance.uid();
        List<Enrollment> enrollmentsRecreated = new ArrayList<>();
        List<Enrollment> enrollments = enrollmentMap.get(trackedEntityInstanceUid);
        List<TrackedEntityAttributeValue> emptyAttributeValueList = new ArrayList<>();

        if (enrollments != null) {
            for (Enrollment enrollment : enrollments) {
                List<Event> eventRecreated = new ArrayList<>();
                List<Event> eventsForEnrollment = eventMap.get(enrollment.uid());
                NoteToPostTransformer transformer = new NoteToPostTransformer(versionManager);
                if (eventsForEnrollment != null) {
                    for (Event event : eventsForEnrollment) {
                        List<TrackedEntityDataValue> dataValuesForEvent = dataValueMap.get(event.uid());
                        List<Note> notesForEvent = getEventNotes(notes, event, transformer);

                        if (versionManager.is2_30()) {
                            eventRecreated.add(event.toBuilder()
                                    .trackedEntityDataValues(dataValuesForEvent)
                                    .notes(notesForEvent)
                                    .geometry(null)
                                    .build());
                        } else {
                            eventRecreated.add(event.toBuilder()
                                    .trackedEntityDataValues(dataValuesForEvent)
                                    .notes(notesForEvent)
                                    .build());
                        }
                    }
                }

                List<Note> notesForEnrollment = getEnrollmentNotes(notes, enrollment, transformer);
                enrollmentsRecreated.add(
                        EnrollmentInternalAccessor.insertEvents(enrollment.toBuilder(), eventRecreated)
                        .notes(notesForEnrollment)
                        .build());
            }
        }

        List<TrackedEntityAttributeValue> attributeValues = attributeValueMap.get(trackedEntityInstanceUid);

        List<Relationship> dbRelationships =
                relationshipRepository.getByItem(RelationshipHelper.teiItem(trackedEntityInstance.uid()), true);
        List<Relationship> ownedRelationships =
                relationshipDHISVersionManager.getOwnedRelationships(dbRelationships, trackedEntityInstance.uid());
        List<Relationship229Compatible> versionAwareRelationships =
                relationshipDHISVersionManager.to229Compatible(ownedRelationships, trackedEntityInstance.uid());

        return TrackedEntityInstanceInternalAccessor
                .insertEnrollments(
                        TrackedEntityInstanceInternalAccessor
                                .insertRelationships(trackedEntityInstance.toBuilder(), versionAwareRelationships),
                        enrollmentsRecreated)
                .trackedEntityAttributeValues(attributeValues == null ? emptyAttributeValueList : attributeValues)
                .build();
    }

    private List<Note> getEventNotes(List<Note> notes, Event event, NoteToPostTransformer transformer) {
        List<Note> notesForEvent = new ArrayList<>();
        for (Note note : notes) {
            if (event.uid().equals(note.event())) {
                notesForEvent.add(transformer.transform(note));
            }
        }
        return notesForEvent;
    }

    private List<Note> getEnrollmentNotes(List<Note> notes, Enrollment enrollment, NoteToPostTransformer transformer) {
        List<Note> notesForEnrollment = new ArrayList<>();
        for (Note note : notes) {
            if (enrollment.uid().equals(note.enrollment())) {
                notesForEnrollment.add(transformer.transform(note));
            }
        }
        return notesForEnrollment;
    }


    private void restorePartitionStates(List<TrackedEntityInstance> partition) {
        setPartitionStates(partition, null);
    }

    private void setPartitionStates(List<TrackedEntityInstance> partition, @Nullable State forcedState) {
        Map<State, List<String>> teiMap = new HashMap<>();
        Map<State, List<String>> enrollmentMap = new HashMap<>();
        Map<State, List<String>> eventMap = new HashMap<>();
        Map<State, List<String>> relationshipMap = new HashMap<>();

        for (TrackedEntityInstance instance : partition) {
            addState(teiMap, instance, forcedState);
            for (Enrollment enrollment : TrackedEntityInstanceInternalAccessor.accessEnrollments(instance)) {
                addState(enrollmentMap, enrollment, forcedState);
                for (Event event : EnrollmentInternalAccessor.accessEvents(enrollment)) {
                    addState(eventMap, event, forcedState);
                }
            }
            for (Relationship229Compatible r : TrackedEntityInstanceInternalAccessor.accessRelationships(instance)) {
                if (versionManager.is2_29()) {
                    String whereClause = new WhereClauseBuilder().appendKeyStringValue(CoreColumns.ID, r.id()).build();
                    Relationship dbRelationship = relationshipStore.selectOneWhere(whereClause);
                    if (dbRelationship != null) {
                        addState(relationshipMap, dbRelationship, forcedState);
                    }
                } else {
                    addState(relationshipMap, r, forcedState);
                }
            }
        }

        persistStates(teiMap, trackedEntityInstanceStore);
        persistStates(enrollmentMap, enrollmentStore);
        persistStates(eventMap, eventStore);
        persistStates(relationshipMap, relationshipStore);
    }

    private <O extends DataObject & ObjectWithUidInterface> void addState(Map<State, List<String>> stateMap, O o,
                                                                          @Nullable State forcedState) {
        State s = getStateToSet(o, forcedState);
        if (!stateMap.containsKey(s)) {
            stateMap.put(s, new ArrayList<>());
        }
        stateMap.get(s).add(o.uid());
    }

    private <O extends DataObject & ObjectWithUidInterface> State getStateToSet(O o, @Nullable State forcedState) {
        if (forcedState == null) {
            return o.state() == State.UPLOADING ? State.TO_UPDATE : o.state();
        } else {
            return forcedState;
        }
    }

    private void persistStates(Map<State, List<String>> map, IdentifiableDeletableDataObjectStore<?> store) {
        for (Map.Entry<State, List<String>> kv : map.entrySet()) {
            store.setState(kv.getValue(), kv.getKey());
        }
    }
}