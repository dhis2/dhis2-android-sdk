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

import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.arch.call.executors.internal.D2CallExecutor;
import org.hisp.dhis.android.core.arch.helpers.UidsHelper;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.relationship.RelationshipSamples;
import org.hisp.dhis.android.core.data.trackedentity.TrackedEntityDataValueSamples;
import org.hisp.dhis.android.core.data.trackedentity.TrackedEntityInstanceSamples;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentInternalAccessor;
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStoreImpl;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.internal.EventStoreImpl;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.internal.ForeignKeyCleanerImpl;
import org.hisp.dhis.android.core.note.Note;
import org.hisp.dhis.android.core.note.NoteCreateProjection;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.internal.OrganisationUnitStore;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.program.internal.ProgramStageStore;
import org.hisp.dhis.android.core.relationship.RelationshipConstraintType;
import org.hisp.dhis.android.core.relationship.RelationshipItem;
import org.hisp.dhis.android.core.relationship.RelationshipItemTrackedEntityInstance;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemStoreImpl;
import org.hisp.dhis.android.core.relationship.internal.RelationshipStoreImpl;
import org.hisp.dhis.android.core.relationship.internal.RelationshipTypeStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityType;
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestMetadataEnqueable;
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(D2JunitRunner.class)
public class TrackedEntityInstancePostCallMockIntegrationShould extends BaseMockIntegrationTestMetadataEnqueable {

    private static TrackedEntityInstancePostCall trackedEntityInstancePostCall;

    private final String teiId = "teiId";
    private final String enrollment1Id = "enrollment1Id";
    private final String enrollment2Id = "enrollment2Id";
    private final String enrollment3Id = "enrollment3Id";
    private final String event1Id = "event1Id";
    private final String event2Id = "event2Id";
    private final String event3Id = "event3Id";

    @After
    public void tearDown() throws D2Error {
        d2.wipeModule().wipeData();
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        BaseMockIntegrationTestMetadataEnqueable.setUpClass();
        trackedEntityInstancePostCall = objects.d2DIComponent.trackedEntityInstancePostCall();
    }

    @Test
    public void build_payload_with_different_enrollments() {
        storeTrackedEntityInstance();

        List<List<TrackedEntityInstance>> partitions =
                trackedEntityInstancePostCall.getPartitionsToSync(null);

        assertThat(partitions.size()).isEqualTo(1);
        assertThat(partitions.get(0).size()).isEqualTo(1);
        for (TrackedEntityInstance instance : partitions.get(0)) {
            assertThat(getEnrollments(instance).size()).isEqualTo(2);
            for (Enrollment enrollment : getEnrollments(instance)) {
                assertThat(getEvents(enrollment).size()).isEqualTo(1);
                for (Event event : getEvents(enrollment)) {
                    assertThat(event.trackedEntityDataValues().size()).isEqualTo(1);
                }
            }
        }
    }

    @Test
    public void build_payload_with_the_enrollments_events_and_values_set_for_upload() {
        storeTrackedEntityInstance();

        List<List<TrackedEntityInstance>> partitions =
                trackedEntityInstancePostCall.getPartitionsToSync(null);

        assertThat(partitions.size()).isEqualTo(1);
        assertThat(partitions.get(0).size()).isEqualTo(1);
        for (TrackedEntityInstance instance : partitions.get(0)) {
            assertThat(getEnrollments(instance).size()).isEqualTo(2);
            for (Enrollment enrollment : getEnrollments(instance)) {
                assertThat(getEvents(enrollment).size()).isEqualTo(1);
                for (Event event : getEvents(enrollment)) {
                    assertThat(event.trackedEntityDataValues().size()).isEqualTo(1);
                }
            }
        }
    }

    @Test
    public void build_payload_without_events_marked_as_error() {
        storeTrackedEntityInstance();

        EnrollmentStoreImpl.create(databaseAdapter).setState("enrollment3Id", State.TO_POST);
        List<List<TrackedEntityInstance>> partitions =
                trackedEntityInstancePostCall.getPartitionsToSync(null);

        assertThat(partitions.size()).isEqualTo(1);
        assertThat(partitions.get(0).size()).isEqualTo(1);
        for (TrackedEntityInstance instance : partitions.get(0)) {
            assertThat(getEnrollments(instance).size()).isEqualTo(3);
            for (Enrollment enrollment : getEnrollments(instance)) {
                if (enrollment.uid().equals("enrollment3Id")) {
                    assertThat(getEvents(enrollment).size()).isEqualTo(0);
                } else {
                    assertThat(getEvents(enrollment).size()).isEqualTo(1);
                }
            }
        }
    }

    @Test
    public void handle_import_conflicts_correctly() {
        storeTrackedEntityInstance();

        dhis2MockServer.enqueueMockResponse("imports/web_response_with_import_conflicts_2.json");

        d2.trackedEntityModule().trackedEntityInstances().blockingUpload();

        assertThat(d2.importModule().trackerImportConflicts().blockingCount()).isEqualTo(3);
    }

    @Test
    public void delete_old_import_conflicts() {
        storeTrackedEntityInstance();

        dhis2MockServer.enqueueMockResponse("imports/web_response_with_import_conflicts_2.json");
        d2.trackedEntityModule().trackedEntityInstances().blockingUpload();
        assertThat(d2.importModule().trackerImportConflicts().blockingCount()).isEqualTo(3);


        TrackedEntityInstanceStoreImpl.create(databaseAdapter).setState("teiId", State.TO_POST);
        EnrollmentStoreImpl.create(databaseAdapter).setState("enrollment1Id", State.TO_POST);
        EnrollmentStoreImpl.create(databaseAdapter).setState("enrollment2Id", State.TO_POST);
        EventStoreImpl.create(databaseAdapter).setState("event1Id", State.TO_POST);
        EventStoreImpl.create(databaseAdapter).setState("event2Id", State.TO_POST);

        dhis2MockServer.enqueueMockResponse("imports/web_response_with_import_conflicts_3.json");
        d2.trackedEntityModule().trackedEntityInstances().blockingUpload();
        assertThat(d2.importModule().trackerImportConflicts().blockingCount()).isEqualTo(1);
    }

    @Test
    public void handle_tei_deletions() throws D2Error {
        storeTrackedEntityInstance();

        d2.trackedEntityModule().trackedEntityInstances().uid("teiId").blockingDelete();

        // There is no TEIs to upload, so there is no request to enqueue.

        d2.trackedEntityModule().trackedEntityInstances().blockingUpload();

        assertThat(d2.trackedEntityModule().trackedEntityInstances().blockingCount()).isEqualTo(0);
        assertThat(d2.enrollmentModule().enrollments().blockingCount()).isEqualTo(0);
        assertThat(d2.eventModule().events().blockingCount()).isEqualTo(0);
        assertThat(d2.importModule().trackerImportConflicts().blockingCount()).isEqualTo(0);
    }

    @Test
    public void recreate_teis_with_filters_and_relationships() throws Exception {
        String tei1 = "tei1";
        String tei2 = "tei2";
        String tei3 = "tei3";
        String tei4 = "tei4";
        String tei5 = "tei5";

        storeSimpleTrackedEntityInstance(tei1, State.TO_POST);
        storeSimpleTrackedEntityInstance(tei2, State.TO_POST);
        storeSimpleTrackedEntityInstance(tei3, State.TO_POST);
        storeSimpleTrackedEntityInstance(tei4, State.TO_POST);
        storeSimpleTrackedEntityInstance(tei5, State.SYNCED);

        storeRelationship("relationship1", tei1, tei2);
        storeRelationship("relationship2", tei2, tei3);
        storeRelationship("relationship3", tei1, tei5);
        storeRelationship("relationship4", tei5, tei4);

        List<List<TrackedEntityInstance>> partitions = trackedEntityInstancePostCall.getPartitionsToSync(
                d2.trackedEntityModule().trackedEntityInstances().byUid().eq(tei1)
                .byState().in(State.uploadableStates()).blockingGet());

        assertThat(partitions.size()).isEqualTo(1);
        assertThat(partitions.get(0).size()).isEqualTo(3);
        assertThat(UidsHelper.getUidsList(partitions.get(0)).containsAll(Lists.newArrayList(tei1, tei2, tei3))).isEqualTo(true);
    }

    @Test
    public void mark_payload_as_uploading() {
        storeTrackedEntityInstance();

        // Ignore result. Just interested in check that target TEIs are marked as UPLOADING
        List<List<TrackedEntityInstance>> partitions = trackedEntityInstancePostCall.getPartitionsToSync(null);

        TrackedEntityInstance instance = TrackedEntityInstanceStoreImpl.create(databaseAdapter).selectFirst();
        assertThat(instance.state()).isEqualTo(State.UPLOADING);

        List<Enrollment> enrollments = EnrollmentStoreImpl.create(databaseAdapter).selectAll();
        for (Enrollment enrollment : enrollments) {
            if ("enrollment1Id".equals(enrollment.uid()) || "enrollment2Id".equals(enrollment.uid())) {
                assertThat(enrollment.state()).isEqualTo(State.UPLOADING);
            } else {
                assertThat(enrollment.state()).isNotEqualTo(State.UPLOADING);
            }
        }

        List<Event> events = EventStoreImpl.create(databaseAdapter).selectAll();
        for (Event event : events) {
            if ("event1Id".equals(event.uid()) || "event2Id".equals(event.uid())) {
                assertThat(event.state()).isEqualTo(State.UPLOADING);
            } else {
                assertThat(event.state()).isNotEqualTo(State.UPLOADING);
            }
        }
    }

    @Test
    public void restore_payload_states_when_error_500() {
        storeTrackedEntityInstance();

        dhis2MockServer.enqueueMockResponse(500, "Internal Server Error");

        d2.trackedEntityModule().trackedEntityInstances().blockingUpload();

        TrackedEntityInstance instance = TrackedEntityInstanceStoreImpl.create(databaseAdapter).selectFirst();
        assertThat(instance.state()).isEqualTo(State.TO_POST);

        List<Enrollment> enrollments = EnrollmentStoreImpl.create(databaseAdapter).selectAll();
        for (Enrollment enrollment : enrollments) {
            if ("enrollment1Id".equals(enrollment.uid()) || "enrollment2Id".equals(enrollment.uid())) {
                assertThat(enrollment.state()).isEqualTo(State.TO_POST);
            }
        }

        List<Event> events = EventStoreImpl.create(databaseAdapter).selectAll();
        for (Event event : events) {
            if ("event1Id".equals(event.uid())) {
                assertThat(event.state()).isEqualTo(State.TO_UPDATE);
            }
            if ("event2Id".equals(event.uid())) {
                assertThat(event.state()).isEqualTo(State.SYNCED_VIA_SMS);
            }
        }
    }

    @Test
    public void build_payload_with_enrollment_notes() throws D2Error {
        storeTrackedEntityInstance();

        d2.noteModule().notes().blockingAdd(NoteCreateProjection.builder()
                .enrollment(enrollment1Id)
                .noteType(Note.NoteType.ENROLLMENT_NOTE)
                .value("This is an enrollment note")
                .build());

        List<List<TrackedEntityInstance>> partitions =
                trackedEntityInstancePostCall.getPartitionsToSync(null);

        assertThat(partitions.size()).isEqualTo(1);
        assertThat(partitions.get(0).size()).isEqualTo(1);
        for (TrackedEntityInstance instance : partitions.get(0)) {
            for (Enrollment enrollment : getEnrollments(instance)) {
                if (enrollment.uid().equals(enrollment1Id)) {
                    assertThat(enrollment.notes().size()).isEqualTo(1);
                } else {
                    assertThat(enrollment.notes().size()).isEqualTo(0);
                }
            }
        }
    }

    @Test
    public void build_payload_with_event_notes() throws D2Error {
        storeTrackedEntityInstance();

        d2.noteModule().notes().blockingAdd(NoteCreateProjection.builder()
                .event(event1Id)
                .noteType(Note.NoteType.EVENT_NOTE)
                .value("This is an event note")
                .build());

        List<List<TrackedEntityInstance>> partitions =
                trackedEntityInstancePostCall.getPartitionsToSync(null);

        assertThat(partitions.size()).isEqualTo(1);
        assertThat(partitions.get(0).size()).isEqualTo(1);
        for (TrackedEntityInstance instance : partitions.get(0)) {
            for (Enrollment enrollment : getEnrollments(instance)) {
                if (enrollment.uid().equals(enrollment1Id)) {
                    for (Event event : getEvents(enrollment)) {
                        if (event.uid().equals(event1Id)) {
                            assertThat(event.notes().size()).isEqualTo(1);
                        } else {
                            assertThat(enrollment.notes().size()).isEqualTo(0);
                        }
                    }
                }
            }
        }
    }

    private void storeTrackedEntityInstance() {
        OrganisationUnit orgUnit = OrganisationUnitStore.create(databaseAdapter).selectFirst();
        TrackedEntityType teiType = TrackedEntityTypeStore.create(databaseAdapter).selectFirst();
        Program program = d2.programModule().programs().one().blockingGet();
        ProgramStage programStage = ProgramStageStore.create(databaseAdapter).selectFirst();

        TrackedEntityDataValue dataValue1 = TrackedEntityDataValueSamples.get().toBuilder().event(event1Id).build();

        Event event1 = Event.builder()
                .uid(event1Id)
                .enrollment(enrollment1Id)
                .organisationUnit(orgUnit.uid())
                .program(program.uid())
                .programStage(programStage.uid())
                .state(State.TO_UPDATE)
                .trackedEntityDataValues(Collections.singletonList(dataValue1))
                .build();

        Enrollment enrollment1 = EnrollmentInternalAccessor.insertEvents(Enrollment.builder(),
                Collections.singletonList(event1))
                .uid(enrollment1Id)
                .program(program.uid())
                .organisationUnit(orgUnit.uid())
                .state(State.TO_POST)
                .trackedEntityInstance(teiId)
                .build();

        TrackedEntityDataValue dataValue2 = TrackedEntityDataValueSamples.get().toBuilder().event(event2Id).build();

        Event event2 = Event.builder()
                .uid(event2Id)
                .enrollment(enrollment2Id)
                .organisationUnit(orgUnit.uid())
                .program(program.uid())
                .programStage(programStage.uid())
                .state(State.SYNCED_VIA_SMS)
                .trackedEntityDataValues(Collections.singletonList(dataValue2))
                .build();

        Enrollment enrollment2 = EnrollmentInternalAccessor.insertEvents(Enrollment.builder(),
                Collections.singletonList(event2))
                .uid(enrollment2Id)
                .program(program.uid())
                .organisationUnit(orgUnit.uid())
                .state(State.TO_POST)
                .trackedEntityInstance(teiId)
                .build();

        TrackedEntityDataValue dataValue3 = TrackedEntityDataValueSamples.get().toBuilder().event(event3Id).build();

        Event event3 = Event.builder()
                .uid(event3Id)
                .enrollment(enrollment3Id)
                .organisationUnit(orgUnit.uid())
                .program(program.uid())
                .programStage(programStage.uid())
                .state(State.ERROR)
                .trackedEntityDataValues(Collections.singletonList(dataValue3))
                .build();

        Enrollment enrollment3 = EnrollmentInternalAccessor.insertEvents(Enrollment.builder(),
                Collections.singletonList(event3))
                .uid(enrollment3Id)
                .program(program.uid())
                .organisationUnit(orgUnit.uid())
                .state(State.SYNCED)
                .trackedEntityInstance(teiId)
                .build();

        TrackedEntityInstance tei  = TrackedEntityInstanceInternalAccessor.insertEnrollments(
                TrackedEntityInstance.builder(), Arrays.asList(enrollment1, enrollment2, enrollment3))
                .uid(teiId)
                .trackedEntityType(teiType.uid())
                .organisationUnit(orgUnit.uid())
                .state(State.TO_POST)
                .build();

        TrackedEntityInstanceStoreImpl.create(databaseAdapter).insert(tei);
        EnrollmentStoreImpl.create(databaseAdapter).insert(enrollment1);
        EnrollmentStoreImpl.create(databaseAdapter).insert(enrollment2);
        EnrollmentStoreImpl.create(databaseAdapter).insert(enrollment3);
        EventStoreImpl.create(databaseAdapter).insert(event1);
        EventStoreImpl.create(databaseAdapter).insert(event2);
        EventStoreImpl.create(databaseAdapter).insert(event3);
        TrackedEntityDataValueStoreImpl.create(databaseAdapter).insert(dataValue1);
        TrackedEntityDataValueStoreImpl.create(databaseAdapter).insert(dataValue2);
        TrackedEntityDataValueStoreImpl.create(databaseAdapter).insert(dataValue3);
    }

    private void storeSimpleTrackedEntityInstance(String teiUid, State state) {
        OrganisationUnit orgUnit = OrganisationUnitStore.create(databaseAdapter).selectFirst();
        TrackedEntityType teiType = TrackedEntityTypeStore.create(databaseAdapter).selectFirst();

        TrackedEntityInstanceStoreImpl.create(databaseAdapter).insert(
                TrackedEntityInstanceSamples.get().toBuilder()
                        .uid(teiUid)
                        .trackedEntityType(teiType.uid())
                        .organisationUnit(orgUnit.uid())
                        .state(state)
                        .build());
    }

    private void storeRelationship(String relationshipUid, String fromUid, String toUid) throws D2Error {

        RelationshipType relationshipType = RelationshipTypeStore.create(databaseAdapter).selectFirst();
        final D2CallExecutor executor = D2CallExecutor.create(databaseAdapter);

        executor.executeD2CallTransactionally(() -> {

            RelationshipStoreImpl.create(databaseAdapter).insert(
                    RelationshipSamples.get230(relationshipUid, fromUid, toUid).toBuilder()
                            .relationshipType(relationshipType.uid()).build());
            RelationshipItemStoreImpl.create(databaseAdapter).insert(
                    RelationshipItem.builder()
                            .relationship(ObjectWithUid.create(relationshipUid))
                            .relationshipItemType(RelationshipConstraintType.FROM)
                            .trackedEntityInstance(
                                    RelationshipItemTrackedEntityInstance.builder().trackedEntityInstance(fromUid).build())
                            .build()
            );
            RelationshipItemStoreImpl.create(databaseAdapter).insert(
                    RelationshipItem.builder()
                            .relationship(ObjectWithUid.create(relationshipUid))
                            .relationshipItemType(RelationshipConstraintType.TO)
                            .trackedEntityInstance(
                                    RelationshipItemTrackedEntityInstance.builder().trackedEntityInstance(toUid).build())
                            .build()
            );

            ForeignKeyCleanerImpl.create(databaseAdapter).cleanForeignKeyErrors();

            return null;
        });
    }

    private List<Enrollment> getEnrollments(TrackedEntityInstance trackedEntityInstance) {
        return TrackedEntityInstanceInternalAccessor.accessEnrollments(trackedEntityInstance);
    }

    private List<Event> getEvents(Enrollment enrollment) {
        return EnrollmentInternalAccessor.accessEvents(enrollment);
    }
}