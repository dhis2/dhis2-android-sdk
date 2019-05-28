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

import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.common.D2CallExecutor;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.common.UidsHelper;
import org.hisp.dhis.android.core.data.relationship.RelationshipSamples;
import org.hisp.dhis.android.core.data.trackedentity.TrackedEntityDataValueSamples;
import org.hisp.dhis.android.core.data.trackedentity.TrackedEntityInstanceSamples;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentStoreImpl;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventStoreImpl;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.ForeignKeyCleanerImpl;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.program.ProgramStageStore;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.relationship.RelationshipConstraintType;
import org.hisp.dhis.android.core.relationship.RelationshipItem;
import org.hisp.dhis.android.core.relationship.RelationshipItemStoreImpl;
import org.hisp.dhis.android.core.relationship.RelationshipItemTrackedEntityInstance;
import org.hisp.dhis.android.core.relationship.RelationshipStoreImpl;
import org.hisp.dhis.android.core.relationship.RelationshipType;
import org.hisp.dhis.android.core.relationship.RelationshipTypeStore;
import org.hisp.dhis.android.core.utils.integration.BaseIntegrationTestMetadataEnqueable;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import androidx.test.runner.AndroidJUnit4;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityInstancePostCallMockIntegrationShould extends BaseIntegrationTestMetadataEnqueable {

    private static TrackedEntityInstancePostCall trackedEntityInstancePostCall;

    @After
    public void tearDown() throws D2Error {
        d2.wipeModule().wipeData();
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        BaseIntegrationTestMetadataEnqueable.setUpClass();
        trackedEntityInstancePostCall = objects.d2DIComponent.trackedEntityInstancePostCall();
    }

    @Test
    public void build_payload_with_different_enrollments() {
        storeTrackedEntityInstance();

        List<TrackedEntityInstance> instances = trackedEntityInstancePostCall.queryDataToSync(null);

        assertThat(instances.size()).isEqualTo(1);
        for (TrackedEntityInstance instance : instances) {
            assertThat(instance.enrollments().size()).isEqualTo(2);
            for (Enrollment enrollment : instance.enrollments()) {
                assertThat(enrollment.events().size()).isEqualTo(1);
                for (Event event : enrollment.events()) {
                    assertThat(event.trackedEntityDataValues().size()).isEqualTo(1);
                }
            }
        }
    }

    @Test
    public void build_payload_with_the_enrollments_events_and_values_set_for_upload_update_or_delete() {
        storeTrackedEntityInstance();

        List<TrackedEntityInstance> instances = trackedEntityInstancePostCall.queryDataToSync(null);
        assertThat(instances.size()).isEqualTo(1);
        for (TrackedEntityInstance instance : instances) {
            assertThat(instance.enrollments().size()).isEqualTo(2);
            for (Enrollment enrollment : instance.enrollments()) {
                assertThat(enrollment.events().size()).isEqualTo(1);
                for (Event event : enrollment.events()) {
                    assertThat(event.trackedEntityDataValues().size()).isEqualTo(1);
                }
            }
        }

        EnrollmentStoreImpl.create(databaseAdapter).setState("enrollment3Id", State.TO_POST);
        instances = trackedEntityInstancePostCall.queryDataToSync(null);
        assertThat(instances.size()).isEqualTo(1);
        for (TrackedEntityInstance instance : instances) {
            assertThat(instance.enrollments().size()).isEqualTo(3);
            for (Enrollment enrollment : instance.enrollments()) {
                if (enrollment.uid().equals("enrollment3Id")) {
                    assertThat(enrollment.events().size()).isEqualTo(0);
                } else {
                    assertThat(enrollment.events().size()).isEqualTo(1);
                }
            }
        }

        EventStoreImpl.create(databaseAdapter).setState("event3Id", State.TO_POST);
        instances = trackedEntityInstancePostCall.queryDataToSync(null);
        assertThat(instances.size()).isEqualTo(1);
        for (TrackedEntityInstance instance : instances) {
            assertThat(instance.enrollments().size()).isEqualTo(3);
            for (Enrollment enrollment : instance.enrollments()) {
                assertThat(enrollment.events().size()).isEqualTo(1);
                for (Event event : enrollment.events()) {
                    assertThat(event.trackedEntityDataValues().size()).isEqualTo(1);
                }
            }
        }
    }

    @Test
    public void handle_import_conflicts_correctly() throws Exception {
        storeTrackedEntityInstance();

        dhis2MockServer.enqueueMockResponse("imports/web_response_with_import_conflicts_2.json");

        d2.trackedEntityModule().trackedEntityInstances.upload().call();

        assertThat(d2.importModule().trackerImportConflicts.count()).isEqualTo(3);
    }

    @Test
    public void delete_old_import_conflicts() throws Exception {
        storeTrackedEntityInstance();

        dhis2MockServer.enqueueMockResponse("imports/web_response_with_import_conflicts_2.json");
        d2.trackedEntityModule().trackedEntityInstances.upload().call();
        assertThat(d2.importModule().trackerImportConflicts.count()).isEqualTo(3);


        TrackedEntityInstanceStoreImpl.create(databaseAdapter).setState("teiId", State.TO_POST);
        EnrollmentStoreImpl.create(databaseAdapter).setState("enrollment1Id", State.TO_POST);
        EnrollmentStoreImpl.create(databaseAdapter).setState("enrollment2Id", State.TO_POST);
        EventStoreImpl.create(databaseAdapter).setState("event1Id", State.TO_POST);
        EventStoreImpl.create(databaseAdapter).setState("event2Id", State.TO_POST);

        dhis2MockServer.enqueueMockResponse("imports/web_response_with_import_conflicts_3.json");
        d2.trackedEntityModule().trackedEntityInstances.upload().call();
        assertThat(d2.importModule().trackerImportConflicts.count()).isEqualTo(1);
    }

    @Test
    public void handle_tei_deletions() throws Exception {
        storeTrackedEntityInstance();

        TrackedEntityInstanceStoreImpl.create(databaseAdapter).setState("teiId", State.TO_DELETE);

        dhis2MockServer.enqueueMockResponse("imports/web_response_with_import_conflicts_2.json");

        d2.trackedEntityModule().trackedEntityInstances.upload().call();

        assertThat(d2.trackedEntityModule().trackedEntityInstances.count()).isEqualTo(0);
        assertThat(d2.enrollmentModule().enrollments.count()).isEqualTo(0);
        assertThat(d2.eventModule().events.count()).isEqualTo(0);
        assertThat(d2.importModule().trackerImportConflicts.count()).isEqualTo(0);
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

        List<TrackedEntityInstance> instances = trackedEntityInstancePostCall.queryDataToSync(
                d2.trackedEntityModule().trackedEntityInstances.byUid().eq(tei1)
                .byState().in(State.TO_POST, State.TO_UPDATE, State.TO_DELETE).get());

        assertThat(instances.size()).isEqualTo(3);
        assertThat(UidsHelper.getUidsList(instances).containsAll(Lists.newArrayList(tei1, tei2, tei3))).isEqualTo(true);
    }

    private void storeTrackedEntityInstance() {
        String teiId = "teiId";
        String enrollment1Id = "enrollment1Id";
        String enrollment2Id = "enrollment2Id";
        String enrollment3Id = "enrollment3Id";
        String event1Id = "event1Id";
        String event2Id = "event2Id";
        String event3Id = "event3Id";

        OrganisationUnit orgUnit = OrganisationUnitStore.create(databaseAdapter).selectFirst();
        TrackedEntityType teiType = TrackedEntityTypeStore.create(databaseAdapter).selectFirst();
        Program program = d2.programModule().programs.one().get();
        ProgramStage programStage = ProgramStageStore.create(databaseAdapter).selectFirst();

        TrackedEntityDataValue dataValue1 = TrackedEntityDataValueSamples.get().toBuilder().event(event1Id).build();

        Event event1 = Event.builder()
                .uid(event1Id)
                .enrollment(enrollment1Id)
                .organisationUnit(orgUnit.uid())
                .program(program.uid())
                .programStage(programStage.uid())
                .state(State.TO_POST)
                .trackedEntityDataValues(Collections.singletonList(dataValue1))
                .build();

        Enrollment enrollment1 = Enrollment.builder()
                .uid(enrollment1Id)
                .program(program.uid())
                .organisationUnit(orgUnit.uid())
                .state(State.TO_POST)
                .trackedEntityInstance(teiId)
                .events(Collections.singletonList(event1))
                .build();

        TrackedEntityDataValue dataValue2 = TrackedEntityDataValueSamples.get().toBuilder().event(event2Id).build();

        Event event2 = Event.builder()
                .uid(event2Id)
                .enrollment(enrollment2Id)
                .organisationUnit(orgUnit.uid())
                .program(program.uid())
                .programStage(programStage.uid())
                .state(State.TO_POST)
                .trackedEntityDataValues(Collections.singletonList(dataValue2))
                .build();

        Enrollment enrollment2 = Enrollment.builder()
                .uid(enrollment2Id)
                .program(program.uid())
                .organisationUnit(orgUnit.uid())
                .state(State.TO_POST)
                .trackedEntityInstance(teiId)
                .events(Collections.singletonList(event2))
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

        Enrollment enrollment3 = Enrollment.builder()
                .uid(enrollment3Id)
                .program(program.uid())
                .organisationUnit(orgUnit.uid())
                .state(State.SYNCED)
                .trackedEntityInstance(teiId)
                .events(Collections.singletonList(event3))
                .build();

        TrackedEntityInstance tei = TrackedEntityInstance.builder()
                .uid(teiId)
                .trackedEntityType(teiType.uid())
                .organisationUnit(orgUnit.uid())
                .state(State.TO_POST)
                .enrollments(Arrays.asList(enrollment1, enrollment2, enrollment3))
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
                            .relationship(Relationship.builder().uid(relationshipUid).build())
                            .relationshipItemType(RelationshipConstraintType.FROM)
                            .trackedEntityInstance(
                                    RelationshipItemTrackedEntityInstance.builder().trackedEntityInstance(fromUid).build())
                            .build()
            );
            RelationshipItemStoreImpl.create(databaseAdapter).insert(
                    RelationshipItem.builder()
                            .relationship(Relationship.builder().uid(relationshipUid).build())
                            .relationshipItemType(RelationshipConstraintType.TO)
                            .trackedEntityInstance(
                                    RelationshipItemTrackedEntityInstance.builder().trackedEntityInstance(toUid).build())
                            .build()
            );

            ForeignKeyCleanerImpl.create(databaseAdapter).cleanForeignKeyErrors();

            return null;
        });
    }
}