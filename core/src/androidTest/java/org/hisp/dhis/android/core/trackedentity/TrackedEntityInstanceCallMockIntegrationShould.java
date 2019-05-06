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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.arch.db.WhereClauseBuilder;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.file.ResourcesFileReader;
import org.hisp.dhis.android.core.data.server.Dhis2MockServer;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentFields;
import org.hisp.dhis.android.core.enrollment.EnrollmentStore;
import org.hisp.dhis.android.core.enrollment.EnrollmentStoreImpl;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventStore;
import org.hisp.dhis.android.core.event.EventStoreImpl;
import org.hisp.dhis.android.core.relationship.Relationship229Compatible;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import androidx.annotation.NonNull;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class TrackedEntityInstanceCallMockIntegrationShould extends AbsStoreTestCase {

    private Dhis2MockServer dhis2MockServer;
    private D2 d2;

    @Override
    @Before
    public void setUp() throws IOException {
        super.setUp();

        dhis2MockServer = new Dhis2MockServer();
        d2 = D2Factory.create(dhis2MockServer.getBaseEndpoint(), databaseAdapter());
    }

    @Override
    @After
    public void tearDown() throws IOException {
        super.tearDown();

        dhis2MockServer.shutdown();
    }

    @Test
    public void download_tracked_entity_instance_enrollments_and_events() throws Exception {
        String teiUid = "PgmUFEQYZdt";

        givenAMetadataInDatabase();

        Callable<List<TrackedEntityInstance>> trackedEntityInstanceByUidEndPointCall =
                d2.trackedEntityModule().downloadTrackedEntityInstancesByUid(Lists.newArrayList(teiUid));

        dhis2MockServer.enqueueMockResponse("trackedentity/tracked_entity_instance_payload.json");

        trackedEntityInstanceByUidEndPointCall.call();

        verifyDownloadedTrackedEntityInstancePayload("trackedentity/tracked_entity_instance_payload.json", teiUid);
    }

    @Test
    public void remove_data_removed_in_server_after_second_download()
            throws Exception {
        String teiUid = "PgmUFEQYZdt";

        givenAMetadataInDatabase();

        Callable<List<TrackedEntityInstance>> trackedEntityInstanceByUidEndPointCall =
                d2.trackedEntityModule().downloadTrackedEntityInstancesByUid(Lists.newArrayList(teiUid));

        dhis2MockServer.enqueueMockResponse("trackedentity/tracked_entity_instance_payload.json");

        trackedEntityInstanceByUidEndPointCall.call();

        trackedEntityInstanceByUidEndPointCall = d2.trackedEntityModule().downloadTrackedEntityInstancesByUid(Lists.newArrayList(teiUid));


        dhis2MockServer.enqueueMockResponse("trackedentity/tracked_entity_instance_with_removed_data_payload.json");

        trackedEntityInstanceByUidEndPointCall.call();

        verifyDownloadedTrackedEntityInstancePayload("trackedentity/tracked_entity_instance_with_removed_data_payload.json",
                teiUid);
    }

    @Test
    public void download_glass_protected_tracked_entity_instance() throws Exception {
        String teiUid = "PgmUFEQYZdt";

        givenAMetadataInDatabase();

        Callable<List<TrackedEntityInstance>> trackedEntityInstanceByUidEndPointCall =
                d2.trackedEntityModule().downloadTrackedEntityInstancesByUid(Lists.newArrayList(teiUid), "program");

        dhis2MockServer.enqueueMockResponse("trackedentity/tracked_entity_instance.json");
        dhis2MockServer.enqueueMockResponse("trackedentity/glass/break_glass_successful.json");
        dhis2MockServer.enqueueMockResponse(401, "trackedentity/glass/glass_protected_tei_failure.json");

        trackedEntityInstanceByUidEndPointCall.call();

        verifyDownloadedTrackedEntityInstance("trackedentity/tracked_entity_instance.json", teiUid);
    }

    private void givenAMetadataInDatabase() throws Exception {
        dhis2MockServer.enqueueMetadataResponses();
        d2.syncMetaData().call();
    }

    private void verifyDownloadedTrackedEntityInstancePayload(String file, String teiUid)
            throws IOException {
        Payload<TrackedEntityInstance> parsed = parseTrackedEntityInstanceResponse(file,
                new TypeReference<Payload<TrackedEntityInstance>>() {});

        TrackedEntityInstance expectedEnrollmentResponse = removeDeletedData(parsed.items().get(0));

        TrackedEntityInstance downloadedTei = getDownloadedTei(teiUid);

        assertThat(downloadedTei.uid(), is(expectedEnrollmentResponse.uid()));
        assertThat(downloadedTei.trackedEntityAttributeValues().size(),
                is(expectedEnrollmentResponse.trackedEntityAttributeValues().size()));
    }

    private void verifyDownloadedTrackedEntityInstance(String file, String teiUid)
            throws IOException {
        TrackedEntityInstance parsed = parseTrackedEntityInstanceResponse(file,
                new TypeReference<TrackedEntityInstance>() {});

        TrackedEntityInstance expectedEnrollmentResponse = removeDeletedData(parsed);

        TrackedEntityInstance downloadedTei = getDownloadedTei(teiUid);

        assertThat(downloadedTei.uid(), is(expectedEnrollmentResponse.uid()));
        assertThat(downloadedTei.trackedEntityAttributeValues().size(),
                is(expectedEnrollmentResponse.trackedEntityAttributeValues().size()));
    }

    private<M> M parseTrackedEntityInstanceResponse(String file, TypeReference<M> reference)
            throws IOException {
        String expectedEventsResponseJson = new ResourcesFileReader().getStringFromFile(file);

        ObjectMapper objectMapper = new ObjectMapper().setDateFormat(
                BaseIdentifiableObject.DATE_FORMAT.raw());

        return objectMapper.readValue(expectedEventsResponseJson, reference);
    }

    @NonNull
    private TrackedEntityInstance removeDeletedData(TrackedEntityInstance trackedEntityInstance) {
        Map<String, List<Event>> expectedEvents = new HashMap<>();
        List<Enrollment> expectedEnrollments = new ArrayList<>();


        for (Enrollment enrollment : trackedEntityInstance.enrollments()) {
            for (Event event : enrollment.events()) {
                if (!event.deleted()) {
                    if (expectedEvents.get(event.enrollment()) == null) {
                        expectedEvents.put(event.enrollment(), new ArrayList<>());
                    }

                    expectedEvents.get(event.enrollment()).add(event);

                }
            }
            if (!enrollment.deleted()) {
                enrollment = enrollment.toBuilder()
                        .trackedEntityInstance(trackedEntityInstance.uid())
                        .events(expectedEvents.get(enrollment.uid()))
                        .build();

                expectedEnrollments.add(enrollment);
            }
        }

        trackedEntityInstance = trackedEntityInstance.toBuilder().enrollments(expectedEnrollments).build();

        return trackedEntityInstance;
    }

    private TrackedEntityInstance getDownloadedTei(String teiUid) {
        TrackedEntityInstance downloadedTei;

        TrackedEntityAttributeValueStore teiAttributeValuesStore =
                TrackedEntityAttributeValueStoreImpl.create(databaseAdapter());

        List<TrackedEntityAttributeValue> attValues = teiAttributeValuesStore.queryByTrackedEntityInstance(teiUid);
        List<TrackedEntityAttributeValue> attValuesWithoutIdAndTEI = new ArrayList<>();
        for (TrackedEntityAttributeValue trackedEntityAttributeValue : attValues) {
            attValuesWithoutIdAndTEI.add(
                    trackedEntityAttributeValue.toBuilder().id(null).trackedEntityInstance(null).build());
        }

        TrackedEntityInstanceStore teiStore = TrackedEntityInstanceStoreImpl.create(databaseAdapter());

        downloadedTei = teiStore.selectByUid(teiUid);

        EnrollmentStore enrollmentStore = EnrollmentStoreImpl.create(databaseAdapter());

        List<Enrollment> downloadedEnrollments = enrollmentStore.selectWhere(new WhereClauseBuilder()
                .appendKeyStringValue(EnrollmentFields.TRACKED_ENTITY_INSTANCE, teiUid).build());
        List<Enrollment> downloadedEnrollmentsWithoutIdAndDeleteFalse = new ArrayList<>();
        for (Enrollment enrollment : downloadedEnrollments) {
            downloadedEnrollmentsWithoutIdAndDeleteFalse.add(
                    enrollment.toBuilder().id(null).deleted(false).state(null).notes(new ArrayList<>()).build());
        }

        EventStore eventStore = EventStoreImpl.create(databaseAdapter());

        List<Event> downloadedEventsWithoutValues = eventStore.selectAll();
        List<Event> downloadedEventsWithoutValuesAndDeleteFalse = new ArrayList<>();
        for (Event event : downloadedEventsWithoutValues) {
            downloadedEventsWithoutValuesAndDeleteFalse.add(
                    event.toBuilder().id(null).deleted(false).state(null).build());
        }

        List<TrackedEntityDataValue> dataValueList = TrackedEntityDataValueStoreImpl.create(databaseAdapter()).selectAll();
        Map<String, List<TrackedEntityDataValue>> downloadedValues = new HashMap<>();
        for (TrackedEntityDataValue dataValue : dataValueList) {
            if (downloadedValues.get(dataValue.event()) == null) {
                downloadedValues.put(dataValue.event(), new ArrayList<>());
            }

            downloadedValues.get(dataValue.event()).add(dataValue);
        }

        return createTei(downloadedTei, attValuesWithoutIdAndTEI, downloadedEnrollmentsWithoutIdAndDeleteFalse,
                downloadedEventsWithoutValuesAndDeleteFalse, downloadedValues);
    }

    private TrackedEntityInstance createTei(TrackedEntityInstance downloadedTei,
                                            List<TrackedEntityAttributeValue> attValuesWithoutIdAndTEI,
                                            List<Enrollment> downloadedEnrollmentsWithoutEvents,
                                            List<Event> downloadedEventsWithoutValues,
                                            Map<String, List<TrackedEntityDataValue>> downloadedValues) {

        Map<String, List<Event>> downloadedEvents = new HashMap<>();

        List<Enrollment> downloadedEnrollments = new ArrayList<>();

        for (Event event : downloadedEventsWithoutValues) {
            List<TrackedEntityDataValue> trackedEntityDataValuesWithNullIdsAndEvents = new ArrayList<>();

            for (TrackedEntityDataValue trackedEntityDataValue : downloadedValues.get(event.uid())) {
                trackedEntityDataValuesWithNullIdsAndEvents.add(
                        trackedEntityDataValue.toBuilder().id(null).event(null).build());
            }

            event = event.toBuilder().trackedEntityDataValues(trackedEntityDataValuesWithNullIdsAndEvents).build();

            if (downloadedEvents.get(event.enrollment()) == null) {
                downloadedEvents.put(event.enrollment(), new ArrayList<>());
            }

            downloadedEvents.get(event.enrollment()).add(event);
        }

        for (Enrollment enrollment : downloadedEnrollmentsWithoutEvents) {
            enrollment = enrollment.toBuilder()
                    .trackedEntityInstance(downloadedTei.uid())
                    .events(downloadedEvents.get(enrollment.uid()))
                    .build();

            downloadedEnrollments.add(enrollment);
        }

        List<Relationship229Compatible> relationships = new ArrayList<>();

        if (downloadedTei.relationships() != null) {
            relationships = downloadedTei.relationships();
        }

        downloadedTei = downloadedTei.toBuilder()
                .id(null)
                .state(null)
                .deleted(false)
                .trackedEntityAttributeValues(attValuesWithoutIdAndTEI)
                .relationships(relationships)
                .enrollments(downloadedEnrollments)
                .build();

        return downloadedTei;
    }
}