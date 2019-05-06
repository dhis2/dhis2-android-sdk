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

package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.arch.api.executors.APICallExecutor;
import org.hisp.dhis.android.core.arch.db.WhereClauseBuilder;
import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentStore;
import org.hisp.dhis.android.core.enrollment.note.Note;
import org.hisp.dhis.android.core.enrollment.note.NoteToPostTransformer;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventStore;
import org.hisp.dhis.android.core.imports.TEIWebResponse;
import org.hisp.dhis.android.core.imports.TEIWebResponseHandler;
import org.hisp.dhis.android.core.imports.WebResponse;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.relationship.Relationship229Compatible;
import org.hisp.dhis.android.core.relationship.RelationshipCollectionRepository;
import org.hisp.dhis.android.core.relationship.RelationshipDHISVersionManager;
import org.hisp.dhis.android.core.relationship.RelationshipHelper;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import dagger.Reusable;

@SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops", "PMD.ExcessiveImports"})
@Reusable
public final class TrackedEntityInstancePostCall implements Callable<WebResponse> {
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
    private final ObjectWithoutUidStore<Note> noteStore;

    private final TEIWebResponseHandler teiWebResponseHandler;

    private final APICallExecutor apiCallExecutor;

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
                                  @NonNull ObjectWithoutUidStore<Note> noteStore,
                                  @NonNull TEIWebResponseHandler teiWebResponseHandler,
                                  @NonNull APICallExecutor apiCallExecutor) {
        this.versionManager = versionManager;
        this.relationshipDHISVersionManager = relationshipDHISVersionManager;
        this.relationshipRepository = relationshipRepository;
        this.trackedEntityInstanceService = trackedEntityInstanceService;
        this.trackedEntityInstanceStore = trackedEntityInstanceStore;
        this.enrollmentStore = enrollmentStore;
        this.eventStore = eventStore;
        this.trackedEntityDataValueStore = trackedEntityDataValueStore;
        this.trackedEntityAttributeValueStore = trackedEntityAttributeValueStore;
        this.noteStore = noteStore;
        this.teiWebResponseHandler = teiWebResponseHandler;
        this.apiCallExecutor = apiCallExecutor;
    }

    @Override
    public TEIWebResponse call() throws D2Error {
        List<TrackedEntityInstance> trackedEntityInstancesToPost = queryDataToSync();

        // if size is 0, then no need to do network request
        if (trackedEntityInstancesToPost.isEmpty()) {
            return TEIWebResponse.empty();
        }

        TrackedEntityInstancePayload trackedEntityInstancePayload = new TrackedEntityInstancePayload();
        trackedEntityInstancePayload.trackedEntityInstances = trackedEntityInstancesToPost;

        String strategy;
        if (versionManager.is2_29()) {
            strategy = "CREATE_AND_UPDATE";
        } else {
            strategy = "SYNC";
        }

        TEIWebResponse webResponse = apiCallExecutor.executeObjectCallWithAcceptedErrorCodes(
                trackedEntityInstanceService.postTrackedEntityInstances(trackedEntityInstancePayload, strategy),
                Collections.singletonList(409), TEIWebResponse.class);
        teiWebResponseHandler.handleWebResponse(webResponse);
        return webResponse;
    }

    @NonNull
    List<TrackedEntityInstance> queryDataToSync() {
        Map<String, List<TrackedEntityDataValue>> dataValueMap =
                trackedEntityDataValueStore.queryTrackerTrackedEntityDataValues();
        Map<String, List<Event>> eventMap = eventStore.queryEventsAttachedToEnrollmentToPost();
        Map<String, List<Enrollment>> enrollmentMap = enrollmentStore.queryEnrollmentsToPost();
        Map<String, List<TrackedEntityAttributeValue>> attributeValueMap =
                trackedEntityAttributeValueStore.queryTrackedEntityAttributeValueToPost();
        List<TrackedEntityInstance> trackedEntityInstances =
                trackedEntityInstanceStore.queryTrackedEntityInstancesToPost();

        String whereClause = new WhereClauseBuilder()
                .appendKeyStringValue(BaseDataModel.Columns.STATE, State.TO_POST).build();
        List<Note> notes = noteStore.selectWhere(whereClause);

        List<TrackedEntityInstance> trackedEntityInstancesRecreated = new ArrayList<>();
        List<TrackedEntityAttributeValue> emptyAttributeValueList = new ArrayList<>();

        for (TrackedEntityInstance trackedEntityInstance : trackedEntityInstances) {
            String trackedEntityInstanceUid = trackedEntityInstance.uid();
            List<Enrollment> enrollmentsRecreated = new ArrayList<>();
            List<Enrollment> enrollments = enrollmentMap.get(trackedEntityInstanceUid);

            if (enrollments != null) {
                for (Enrollment enrollment : enrollments) {
                    List<Event> eventRecreated = new ArrayList<>();
                    List<Event> eventsForEnrollment = eventMap.get(enrollment.uid());
                    if (eventsForEnrollment != null) {
                        for (Event event : eventsForEnrollment) {
                            List<TrackedEntityDataValue> dataValuesForEvent = dataValueMap.get(event.uid());
                            eventRecreated.add(event.toBuilder().trackedEntityDataValues(dataValuesForEvent).build());
                        }
                    }

                    List<Note> notesForEnrollment = new ArrayList<>();
                    NoteToPostTransformer transformer = new NoteToPostTransformer(versionManager);
                    for (Note note : notes) {
                        if (enrollment.uid().equals(note.enrollment())) {
                            notesForEnrollment.add(transformer.transform(note));
                        }
                    }

                    enrollmentsRecreated.add(enrollment.toBuilder()
                            .events(eventRecreated)
                            .notes(notesForEnrollment)
                            .build());
                }
            }

            List<TrackedEntityAttributeValue> attributeValues = attributeValueMap.get(trackedEntityInstanceUid);

            List<Relationship> dbRelationships =
                    relationshipRepository.getByItem(RelationshipHelper.teiItem(trackedEntityInstance.uid()));
            List<Relationship229Compatible> versionAwareRelationships =
                    relationshipDHISVersionManager.to229Compatible(dbRelationships, trackedEntityInstance.uid());

            TrackedEntityInstance recreatedTrackedEntityInstance = trackedEntityInstance.toBuilder()
                    .trackedEntityAttributeValues(attributeValues == null ? emptyAttributeValueList : attributeValues)
                    .relationships(versionAwareRelationships)
                    .enrollments(enrollmentsRecreated)
                    .build();

            trackedEntityInstancesRecreated.add(recreatedTrackedEntityInstance);
        }

        return trackedEntityInstancesRecreated;

    }
}