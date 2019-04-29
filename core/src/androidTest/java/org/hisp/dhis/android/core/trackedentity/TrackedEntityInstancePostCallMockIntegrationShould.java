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

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.ResourcesFileReader;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.hisp.dhis.android.core.data.trackedentity.TrackedEntityDataValueSamples;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentStoreImpl;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventStoreImpl;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitStore;
import org.hisp.dhis.android.core.program.Program;
import org.hisp.dhis.android.core.program.ProgramStage;
import org.hisp.dhis.android.core.program.ProgramStageStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import androidx.test.runner.AndroidJUnit4;

import static com.google.common.truth.Truth.assertThat;

@RunWith(AndroidJUnit4.class)
public class TrackedEntityInstancePostCallMockIntegrationShould extends AbsStoreTestCase {

    private Dhis2MockServer dhis2MockServer;
    private D2 d2;

    private TrackedEntityInstancePostCall trackedEntityInstancePostCall;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        dhis2MockServer = new Dhis2MockServer(new ResourcesFileReader());
        d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());

        trackedEntityInstancePostCall = getD2DIComponent(d2).trackedEntityInstancePostCall();
    }

    @Override
    @After
    public void tearDown() throws IOException {
        super.tearDown();

        dhis2MockServer.shutdown();
    }

    @Test
    public void build_payload_with_different_enrollments() throws Exception {
        givenAMetadataInDatabase();

        storeTrackedEntityInstance();

        List<TrackedEntityInstance> instances = trackedEntityInstancePostCall.queryDataToSync();

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
    public void build_payload_with_the_enrollments_events_and_values_set_for_upload_update_or_delete() throws Exception {
        givenAMetadataInDatabase();

        storeTrackedEntityInstance();

        List<TrackedEntityInstance> instances = trackedEntityInstancePostCall.queryDataToSync();
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

        EnrollmentStoreImpl.create(databaseAdapter()).setState("enrollment3Id", State.TO_POST);
        instances = trackedEntityInstancePostCall.queryDataToSync();
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

        EventStoreImpl.create(databaseAdapter()).setState("event3Id", State.TO_POST);
        instances = trackedEntityInstancePostCall.queryDataToSync();
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
    public void handleImportConflictsCorrectly() throws Exception {
        givenAMetadataInDatabase();

        storeTrackedEntityInstance();

        dhis2MockServer.enqueueMockResponse("imports/web_response_with_import_conflicts_2.json");

        d2.trackedEntityModule().trackedEntityInstances.upload().call();

        assertThat(d2.importModule().trackerImportConflicts.count()).isEqualTo(3);
    }

    private void givenAMetadataInDatabase() throws Exception {
        dhis2MockServer.enqueueMetadataResponses();
        d2.syncMetaData().call();
    }

    private void storeTrackedEntityInstance() {
        String teiId = "teiId";
        String enrollment1Id = "enrollment1Id";
        String enrollment2Id = "enrollment2Id";
        String enrollment3Id = "enrollment3Id";
        String event1Id = "event1Id";
        String event2Id = "event2Id";
        String event3Id = "event3Id";

        OrganisationUnit orgUnit = OrganisationUnitStore.create(databaseAdapter()).selectFirst();
        TrackedEntityType teiType = TrackedEntityTypeStore.create(databaseAdapter()).selectFirst();
        Program program = d2.programModule().programs.one().get();
        ProgramStage programStage = ProgramStageStore.create(databaseAdapter()).selectFirst();

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

        TrackedEntityInstanceStoreImpl.create(databaseAdapter()).insert(tei);
        EnrollmentStoreImpl.create(databaseAdapter()).insert(enrollment1);
        EnrollmentStoreImpl.create(databaseAdapter()).insert(enrollment2);
        EnrollmentStoreImpl.create(databaseAdapter()).insert(enrollment3);
        EventStoreImpl.create(databaseAdapter()).insert(event1);
        EventStoreImpl.create(databaseAdapter()).insert(event2);
        EventStoreImpl.create(databaseAdapter()).insert(event3);
        TrackedEntityDataValueStoreImpl.create(databaseAdapter()).insert(dataValue1);
        TrackedEntityDataValueStoreImpl.create(databaseAdapter()).insert(dataValue2);
        TrackedEntityDataValueStoreImpl.create(databaseAdapter()).insert(dataValue3);
    }
}