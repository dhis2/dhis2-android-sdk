/*
 *  Copyright (c) 2004-2022, University of Oslo
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

package org.hisp.dhis.android.core.trackedentity.api;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.imports.ImportStatus.ERROR;
import static org.hisp.dhis.android.core.imports.ImportStatus.SUCCESS;
import static org.hisp.dhis.android.core.imports.ImportStatus.WARNING;
import static org.hisp.dhis.android.core.trackedentity.api.TrackedEntityInstanceUtils.assertEnrollments;
import static org.hisp.dhis.android.core.trackedentity.api.TrackedEntityInstanceUtils.assertEvents;
import static org.hisp.dhis.android.core.trackedentity.api.TrackedEntityInstanceUtils.assertTei;
import static org.hisp.dhis.android.core.trackedentity.api.TrackedEntityInstanceUtils.createTrackedEntityInstanceAndTwoActiveEnrollment;
import static org.hisp.dhis.android.core.trackedentity.api.TrackedEntityInstanceUtils.createTrackedEntityInstanceWithCompletedEnrollmentAndEvent;
import static org.hisp.dhis.android.core.trackedentity.api.TrackedEntityInstanceUtils.createTrackedEntityInstanceWithEnrollmentAndFutureEvent;
import static org.hisp.dhis.android.core.trackedentity.api.TrackedEntityInstanceUtils.createTrackedEntityInstanceWithInvalidAttribute;
import static org.hisp.dhis.android.core.trackedentity.api.TrackedEntityInstanceUtils.createTrackedEntityInstanceWithInvalidDataElement;
import static org.hisp.dhis.android.core.trackedentity.api.TrackedEntityInstanceUtils.createTrackedEntityInstanceWithInvalidOrgunit;
import static org.hisp.dhis.android.core.trackedentity.api.TrackedEntityInstanceUtils.createTrackedEntityInstanceWithValidAndInvalidDataValue;
import static org.hisp.dhis.android.core.trackedentity.api.TrackedEntityInstanceUtils.createValidTrackedEntityInstance;
import static org.hisp.dhis.android.core.trackedentity.api.TrackedEntityInstanceUtils.createValidTrackedEntityInstanceAndEnrollment;
import static org.hisp.dhis.android.core.trackedentity.api.TrackedEntityInstanceUtils.createValidTrackedEntityInstanceWithEnrollmentAndEvent;
import static org.hisp.dhis.android.core.trackedentity.api.TrackedEntityInstanceUtils.createValidTrackedEntityInstanceWithFutureEnrollment;

import org.hisp.dhis.android.core.BaseRealIntegrationTest;
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor;
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutorImpl;
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentInternalAccessor;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.imports.internal.EnrollmentImportSummary;
import org.hisp.dhis.android.core.imports.internal.EventImportSummary;
import org.hisp.dhis.android.core.imports.internal.TEIImportSummary;
import org.hisp.dhis.android.core.imports.internal.TEIWebResponse;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceFields;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstancePayload;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceService;
import org.junit.Assert;
import org.junit.Before;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class TrackedEntityInstanceAPIShould extends BaseRealIntegrationTest {

    // API version dependant parameters
    private String serverUrl;
    private String strategy;

    private APICallExecutor executor;

    private TrackedEntityInstanceService trackedEntityInstanceService;

    TrackedEntityInstanceAPIShould(String serverUrl, String strategy) {
        super();
        this.serverUrl = serverUrl;
        this.strategy = strategy;
    }

    @Before
    public void setUp() {
        super.setUp();

        executor = APICallExecutorImpl.create(d2.databaseAdapter(), null);

        trackedEntityInstanceService = d2.retrofit().create(TrackedEntityInstanceService.class);
    }

    //@Test
    public void tei_with_invalid_tracked_entity_attribute() throws Exception {
        login();

        TrackedEntityInstance validTEI = createValidTrackedEntityInstance();

        TrackedEntityInstance invalidTEI = createTrackedEntityInstanceWithInvalidAttribute();

        TrackedEntityInstancePayload payload = TrackedEntityInstancePayload.create(Arrays.asList(validTEI, invalidTEI));

        TEIWebResponse response = executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                .postTrackedEntityInstances(payload, this.strategy), Collections.singletonList(409), TEIWebResponse.class);

        assertThat(response.response().status()).isEqualTo(ERROR);

        for (TEIImportSummary importSummary : response.response().importSummaries()) {
            if (validTEI.uid().equals(importSummary.reference())) {
                assertTei(importSummary, SUCCESS);
            }
            else if (invalidTEI.uid().equals(importSummary.reference())) {
                assertTei(importSummary, ERROR);
            }
        }

        // Check server status
        Payload<TrackedEntityInstance> serverValidTEI = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstanceAsCall(validTEI.uid(), TrackedEntityInstanceFields.allFields,
                        true, true));

        try {
            executor.executeObjectCall(trackedEntityInstanceService.getTrackedEntityInstanceAsCall(invalidTEI.uid(),
                    TrackedEntityInstanceFields.allFields, true, true));
            Assert.fail("Should not reach that line");
        } catch (D2Error e) {
            assertThat(e.httpErrorCode()).isEqualTo(404);
        }

        assertThat(serverValidTEI.items().size()).isEqualTo(1);
    }

    //@Test
    public void tei_with_invalid_orgunit() throws Exception {
        login();

        TrackedEntityInstance validTEI = createValidTrackedEntityInstance();

        TrackedEntityInstance invalidTEI = createTrackedEntityInstanceWithInvalidOrgunit();

        TrackedEntityInstancePayload payload = TrackedEntityInstancePayload.create(Arrays.asList(validTEI, invalidTEI));

        TEIWebResponse response = executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                .postTrackedEntityInstances(payload, this.strategy), Collections.singletonList(409), TEIWebResponse.class);

        assertThat(response.response().status()).isEqualTo(ERROR);

        for (TEIImportSummary importSummary : response.response().importSummaries()) {
            if (validTEI.uid().equals(importSummary.reference())) {
                assertTei(importSummary, SUCCESS);
            }
            else if (invalidTEI.uid().equals(importSummary.reference())) {
                assertTei(importSummary, ERROR);
            }
        }

        // Check server status
        Payload<TrackedEntityInstance> serverValidTEI = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstanceAsCall(validTEI.uid(), TrackedEntityInstanceFields.allFields,
                        true, true));

        try {
            executor.executeObjectCall(trackedEntityInstanceService.getTrackedEntityInstanceAsCall(invalidTEI.uid(),
                    TrackedEntityInstanceFields.allFields, true, true));
            Assert.fail("Should not reach that line");
        } catch (D2Error e) {
            assertThat(e.httpErrorCode()).isEqualTo(404);
        }

        assertThat(serverValidTEI.items().size()).isEqualTo(1);
    }

    //@Test
    public void enrollment_with_valid_values() throws Exception {
        login();

        TrackedEntityInstance validTEI = createValidTrackedEntityInstance();

        TrackedEntityInstance invalidTEI = createValidTrackedEntityInstanceAndEnrollment();

        TrackedEntityInstancePayload payload = TrackedEntityInstancePayload.create(Arrays.asList(validTEI, invalidTEI));

        TEIWebResponse response = executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                .postTrackedEntityInstances(payload, this.strategy), Collections.singletonList(409), TEIWebResponse.class);

        assertThat(response.response().status()).isEqualTo(SUCCESS);

        for (TEIImportSummary importSummary : response.response().importSummaries()) {
            if (validTEI.uid().equals(importSummary.reference())) {
                assertTei(importSummary, SUCCESS);
            }
            else if (invalidTEI.uid().equals(importSummary.reference())) {
                assertTei(importSummary, SUCCESS);
            }
        }

        // TODO Check server status
        Payload<TrackedEntityInstance> serverValidTEI = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstanceAsCall(validTEI.uid(), TrackedEntityInstanceFields.allFields,
                        true, true));

        Payload<TrackedEntityInstance> serverInvalidTEI = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstanceAsCall(invalidTEI.uid(), TrackedEntityInstanceFields.allFields,
                        true, true));

    }

    //@Test
    public void enrollment_future_date() throws Exception {
        login();

        TrackedEntityInstance validTEI = createValidTrackedEntityInstanceAndEnrollment();

        TrackedEntityInstance invalidTEI = createValidTrackedEntityInstanceWithFutureEnrollment();

        TrackedEntityInstancePayload payload = TrackedEntityInstancePayload.create(Arrays.asList(validTEI, invalidTEI));

        TEIWebResponse response = executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                .postTrackedEntityInstances(payload, this.strategy), Collections.singletonList(409), TEIWebResponse.class);

        assertThat(response.response().status()).isEqualTo(SUCCESS);

        for (TEIImportSummary importSummary : response.response().importSummaries()) {
            if (validTEI.uid().equals(importSummary.reference())) {
                assertTei(importSummary, SUCCESS);
                assertEnrollments(importSummary, SUCCESS);
            }
            else if (invalidTEI.uid().equals(importSummary.reference())) {
                assertTei(importSummary, SUCCESS);
                assertEnrollments(importSummary, ERROR);
            }
        }

        Payload<TrackedEntityInstance> serverValidTEI = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstanceAsCall(validTEI.uid(), TrackedEntityInstanceFields.allFields,
                        true, true));

        Payload<TrackedEntityInstance> serverInvalidTEI = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstanceAsCall(invalidTEI.uid(), TrackedEntityInstanceFields.allFields,
                        true, true));

        assertThat(getEnrollments(serverValidTEI.items().get(0))).isNotEmpty();
        assertThat(getEnrollments(serverInvalidTEI.items().get(0))).isEmpty();
    }

    //@Test
    public void already_active_enrollment() throws Exception {
        login();

        TrackedEntityInstance validTEI = createValidTrackedEntityInstanceAndEnrollment();

        TrackedEntityInstance invalidTEI = createTrackedEntityInstanceAndTwoActiveEnrollment();

        TrackedEntityInstancePayload payload = TrackedEntityInstancePayload.create(Arrays.asList(validTEI, invalidTEI));

        TEIWebResponse response = executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                .postTrackedEntityInstances(payload, this.strategy), Collections.singletonList(409), TEIWebResponse.class);

        assertThat(response.response().status()).isEqualTo(SUCCESS);

        for (TEIImportSummary importSummary : response.response().importSummaries()) {
            if (validTEI.uid().equals(importSummary.reference())) {
                assertTei(importSummary, SUCCESS);
                assertEnrollments(importSummary, SUCCESS);
            }
            else if (invalidTEI.uid().equals(importSummary.reference())) {
                assertTei(importSummary, SUCCESS);
                assertEnrollments(importSummary, ERROR);
                assertThat(importSummary.enrollments().imported()).isEqualTo(1);
                assertThat(importSummary.enrollments().ignored()).isEqualTo(1);
            }
        }

        Payload<TrackedEntityInstance> serverValidTEI = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstanceAsCall(validTEI.uid(), TrackedEntityInstanceFields.allFields,
                        true, true));

        Payload<TrackedEntityInstance> serverInvalidTEI = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstanceAsCall(invalidTEI.uid(), TrackedEntityInstanceFields.allFields,
                        true, true));

        assertThat(getEnrollments(serverValidTEI.items().get(0)).size()).isEqualTo(1);
        assertThat(getEnrollments(serverInvalidTEI.items().get(0)).size()).isEqualTo(1);
    }

    //@Test
    public void event_with_valid_values() throws Exception {
        login();

        TrackedEntityInstance validTEI1 = createValidTrackedEntityInstanceAndEnrollment();

        TrackedEntityInstance validTEI2 = createValidTrackedEntityInstanceWithEnrollmentAndEvent();

        TrackedEntityInstancePayload payload = TrackedEntityInstancePayload.create(Arrays.asList(validTEI1, validTEI2));

        TEIWebResponse response = executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                .postTrackedEntityInstances(payload, this.strategy), Collections.singletonList(409), TEIWebResponse.class);

        assertThat(response.response().status()).isEqualTo(SUCCESS);

        for (TEIImportSummary importSummary : response.response().importSummaries()) {
            if (validTEI1.uid().equals(importSummary.reference())) {
                assertTei(importSummary, SUCCESS);
                assertEnrollments(importSummary, SUCCESS);
            }
            else if (validTEI2.uid().equals(importSummary.reference())) {
                assertTei(importSummary, SUCCESS);
                assertEnrollments(importSummary, SUCCESS);
                assertEvents(importSummary, SUCCESS);
            }
        }

        Payload<TrackedEntityInstance> serverValidTEI1 = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstanceAsCall(validTEI1.uid(), TrackedEntityInstanceFields.allFields,
                        true, true));

        Payload<TrackedEntityInstance> serverValidTEI2 = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstanceAsCall(validTEI2.uid(), TrackedEntityInstanceFields.allFields,
                        true, true));

        assertThat(getEnrollments(serverValidTEI1.items().get(0)).size()).isEqualTo(1);

        assertThat(getEnrollments(serverValidTEI2.items().get(0)).size()).isEqualTo(1);
        assertThat(getEvents(getEnrollments(serverValidTEI2.items().get(0)).get(0)).size()).isEqualTo(1);

    }

    // IMPORTANT: check the programStage is set to "NO WRITE ACCESS" before running the test
    //@Test
    public void event_with_no_write_access() throws Exception {
        login();

        TrackedEntityInstance validTEI1 = createValidTrackedEntityInstanceAndEnrollment();

        TrackedEntityInstance validTEI2 = createValidTrackedEntityInstanceWithEnrollmentAndEvent();

        TrackedEntityInstancePayload payload = TrackedEntityInstancePayload.create(Arrays.asList(validTEI1, validTEI2));

        TEIWebResponse response = executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                .postTrackedEntityInstances(payload, this.strategy), Collections.singletonList(409), TEIWebResponse.class);

        assertThat(response.response().status()).isEqualTo(SUCCESS);

        for (TEIImportSummary importSummary : response.response().importSummaries()) {
            if (validTEI1.uid().equals(importSummary.reference())) {
                assertTei(importSummary, SUCCESS);
                assertEnrollments(importSummary, SUCCESS);
            }
            else if (validTEI2.uid().equals(importSummary.reference())) {
                assertTei(importSummary, SUCCESS);
                assertEnrollments(importSummary, SUCCESS);
                assertEvents(importSummary, ERROR);
            }
        }

        Payload<TrackedEntityInstance> serverValidTEI1 = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstanceAsCall(validTEI1.uid(), TrackedEntityInstanceFields.allFields,
                        true, true));

        Payload<TrackedEntityInstance> serverValidTEI2 = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstanceAsCall(validTEI2.uid(), TrackedEntityInstanceFields.allFields,
                        true, true));

        assertThat(getEnrollments(serverValidTEI1.items().get(0)).size()).isEqualTo(1);
        assertThat(getEvents(getEnrollments(serverValidTEI1.items().get(0)).get(0))).isEmpty();

        assertThat(getEnrollments(serverValidTEI2.items().get(0)).size()).isEqualTo(1);
        assertThat(getEvents(getEnrollments(serverValidTEI2.items().get(0)).get(0))).isEmpty();

    }

    //@Test
    public void event_with_future_event_date_does_not_fail() throws Exception {
        login();

        TrackedEntityInstance validTEI = createValidTrackedEntityInstanceWithEnrollmentAndEvent();

        TrackedEntityInstance invalidTEI = createTrackedEntityInstanceWithEnrollmentAndFutureEvent();

        TrackedEntityInstancePayload payload = TrackedEntityInstancePayload.create(Arrays.asList(validTEI, invalidTEI));

        TEIWebResponse response = executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                .postTrackedEntityInstances(payload, this.strategy), Collections.singletonList(409), TEIWebResponse.class);

        assertThat(response.response().status()).isEqualTo(SUCCESS);

        for (TEIImportSummary importSummary : response.response().importSummaries()) {
            if (validTEI.uid().equals(importSummary.reference())) {
                assertTei(importSummary, SUCCESS);
                assertEnrollments(importSummary, SUCCESS);
                assertEvents(importSummary, SUCCESS);
            }
            else if (invalidTEI.uid().equals(importSummary.reference())) {
                assertTei(importSummary, SUCCESS);
                assertEnrollments(importSummary, SUCCESS);
                assertEvents(importSummary, SUCCESS);
            }
        }

        Payload<TrackedEntityInstance> serverValidTEI1 = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstanceAsCall(validTEI.uid(), TrackedEntityInstanceFields.allFields,
                        true, true));

        Payload<TrackedEntityInstance> serverValidTEI2 = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstanceAsCall(invalidTEI.uid(), TrackedEntityInstanceFields.allFields,
                        true, true));

        assertThat(getEnrollments(serverValidTEI1.items().get(0)).size()).isEqualTo(1);
        assertThat(getEvents(getEnrollments(serverValidTEI1.items().get(0)).get(0)).size()).isEqualTo(1);

        assertThat(getEnrollments(serverValidTEI2.items().get(0)).size()).isEqualTo(1);
        assertThat(getEvents(getEnrollments(serverValidTEI2.items().get(0)).get(0)).size()).isEqualTo(1);

    }

    //@Test
    public void event_with_invalid_data_element() throws Exception {
        login();

        TrackedEntityInstance validTEI = createValidTrackedEntityInstanceWithEnrollmentAndEvent();

        TrackedEntityInstance invalidTEI = createTrackedEntityInstanceWithInvalidDataElement();

        TrackedEntityInstancePayload payload = TrackedEntityInstancePayload.create(Arrays.asList(validTEI, invalidTEI));

        TEIWebResponse response = executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                .postTrackedEntityInstances(payload, this.strategy), Collections.singletonList(409), TEIWebResponse.class);

        assertThat(response.response().status()).isEqualTo(SUCCESS);

        for (TEIImportSummary importSummary : response.response().importSummaries()) {
            if (validTEI.uid().equals(importSummary.reference())) {
                assertTei(importSummary, SUCCESS);
                assertEnrollments(importSummary, SUCCESS);
                assertEvents(importSummary, SUCCESS);
            }
            else if (invalidTEI.uid().equals(importSummary.reference())) {
                assertTei(importSummary, SUCCESS);
                assertEnrollments(importSummary, SUCCESS);
                assertEvents(importSummary, WARNING);
            }
        }

        Payload<TrackedEntityInstance> serverValidTEI1 = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstanceAsCall(validTEI.uid(), TrackedEntityInstanceFields.allFields,
                        true, true));

        Payload<TrackedEntityInstance> serverValidTEI2 = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstanceAsCall(invalidTEI.uid(), TrackedEntityInstanceFields.allFields,
                        true, true));

        assertThat(getEnrollments(serverValidTEI1.items().get(0)).size()).isEqualTo(1);
        assertThat(getEvents(getEnrollments(serverValidTEI1.items().get(0)).get(0)).size()).isEqualTo(1);
        assertThat(getEvents(getEnrollments(serverValidTEI1.items().get(0)).get(0)).get(0)
                .trackedEntityDataValues().size()).isEqualTo(1);

        assertThat(getEnrollments(serverValidTEI2.items().get(0)).size()).isEqualTo(1);
        assertThat(getEvents(getEnrollments(serverValidTEI2.items().get(0)).get(0)).size()).isEqualTo(1);
        assertThat(getEvents(getEnrollments(serverValidTEI2.items().get(0)).get(0)).get(0)
                .trackedEntityDataValues()).isEmpty();
    }

    //@Test
    public void event_with_valid_and_invalid_data_value() throws Exception {
        login();

        TrackedEntityInstance validTEI = createValidTrackedEntityInstanceWithEnrollmentAndEvent();

        TrackedEntityInstance invalidTEI = createTrackedEntityInstanceWithValidAndInvalidDataValue();

        TrackedEntityInstancePayload payload = TrackedEntityInstancePayload.create(Arrays.asList(validTEI, invalidTEI));

        TEIWebResponse response = executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                .postTrackedEntityInstances(payload, this.strategy), Collections.singletonList(409), TEIWebResponse.class);

        assertThat(response.response().status()).isEqualTo(SUCCESS);

        for (TEIImportSummary importSummary : response.response().importSummaries()) {
            if (validTEI.uid().equals(importSummary.reference())) {
                assertTei(importSummary, SUCCESS);
                assertEnrollments(importSummary, SUCCESS);
                assertEvents(importSummary, SUCCESS);
            }
            else if (invalidTEI.uid().equals(importSummary.reference())) {
                assertTei(importSummary, SUCCESS);
                assertEnrollments(importSummary, SUCCESS);
                assertEvents(importSummary, WARNING);
            }
        }

        Payload<TrackedEntityInstance> serverValidTEI1 = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstanceAsCall(validTEI.uid(), TrackedEntityInstanceFields.allFields,
                        true, true));

        Payload<TrackedEntityInstance> serverValidTEI2 = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstanceAsCall(invalidTEI.uid(), TrackedEntityInstanceFields.allFields,
                        true, true));

        assertThat(getEnrollments(serverValidTEI1.items().get(0)).size()).isEqualTo(1);
        assertThat(getEvents(getEnrollments(serverValidTEI1.items().get(0)).get(0)).size()).isEqualTo(1);
        assertThat(getEvents(getEnrollments(serverValidTEI1.items().get(0)).get(0)).get(0)
                .trackedEntityDataValues().size()).isEqualTo(1);

        assertThat(getEnrollments(serverValidTEI2.items().get(0)).size()).isEqualTo(1);
        assertThat(getEvents(getEnrollments(serverValidTEI2.items().get(0)).get(0)).size()).isEqualTo(1);
        assertThat(getEvents(getEnrollments(serverValidTEI2.items().get(0)).get(0)).get(0)
                .trackedEntityDataValues().size()).isEqualTo(1);
    }

    // This test is failing
    //@Test
    public void event_in_completed_enrollment() throws Exception {
        login();

        TrackedEntityInstance completedEnrollment = createTrackedEntityInstanceWithCompletedEnrollmentAndEvent();

        TrackedEntityInstancePayload payload =
                TrackedEntityInstancePayload.create(Collections.singletonList(completedEnrollment));

        TEIWebResponse response = executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                .postTrackedEntityInstances(payload, this.strategy), Collections.singletonList(409), TEIWebResponse.class);

        assertThat(response.response().status()).isEqualTo(SUCCESS);

        for (TEIImportSummary importSummary : response.response().importSummaries()) {
            if (completedEnrollment.uid().equals(importSummary.reference())) {
                assertTei(importSummary, SUCCESS);
                assertEnrollments(importSummary, SUCCESS);
                assertEvents(importSummary, SUCCESS);
            }
        }

        Payload<TrackedEntityInstance> serverValidTEI1 = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstanceAsCall(completedEnrollment.uid(), TrackedEntityInstanceFields.allFields,
                        true, true));

        assertThat(getEnrollments(serverValidTEI1.items().get(0)).size()).isEqualTo(1);
        assertThat(getEnrollments(serverValidTEI1.items().get(0)).get(0)
                .status()).isEqualTo(EnrollmentStatus.COMPLETED);
        assertThat(getEvents(getEnrollments(serverValidTEI1.items().get(0)).get(0)).size()).isEqualTo(1);
        assertThat(getEvents(getEnrollments(serverValidTEI1.items().get(0)).get(0)).get(0)
                .trackedEntityDataValues().size()).isEqualTo(1);
        assertThat(getEvents(getEnrollments(serverValidTEI1.items().get(0)).get(0)
                ).get(0).status()).isEqualTo(EventStatus.COMPLETED);
    }

    // @Test
    public void tracked_entity_deletion_returns_deleted_equals_1() throws D2Error {
        login();
        syncMetadata();

        d2.trackedEntityModule().trackedEntityInstanceDownloader().limit(100).blockingDownload();

        TrackedEntityInstance instance = getInstanceWithOneEnrollmentAndOneEvent();

        TrackedEntityInstance deletedEvents = setEventsToDelete(instance);
        TrackedEntityInstancePayload deletedEventsPayload =
                TrackedEntityInstancePayload.create(Collections.singletonList(deletedEvents));

        TEIWebResponse deletedEventsResponse = executePostCall(deletedEventsPayload, this.strategy);

        assertThat(deletedEventsResponse.response().status()).isEqualTo(SUCCESS);

        for (TEIImportSummary teiImportSummaries : deletedEventsResponse.response().importSummaries()) {
            assertThat(teiImportSummaries.importCount().updated()).isEqualTo(1);
            for (EnrollmentImportSummary enrollmentImportSummary : teiImportSummaries.enrollments().importSummaries()) {
                assertThat(enrollmentImportSummary.importCount().updated()).isEqualTo(1);
                for (EventImportSummary eventImportSummary : enrollmentImportSummary.events().importSummaries()) {

                    assertThat(eventImportSummary.importCount().deleted()).isEqualTo(1);
                }
            }
        }
    }

    private void login() {
        d2.userModule().logIn(username, password, serverUrl).blockingGet();
    }

    private void syncMetadata() {
        d2.metadataModule().blockingDownload();
    }

    private TEIWebResponse executePostCall(TrackedEntityInstancePayload payload, String strategy) throws D2Error {
        return executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                .postTrackedEntityInstances(payload, strategy), Collections.singletonList(409), TEIWebResponse.class);
    }

    private TrackedEntityInstance getInstanceWithOneEnrollmentAndOneEvent() {
        List<TrackedEntityInstance> instances =
                d2.trackedEntityModule().trackedEntityInstances().blockingGet();

        for (TrackedEntityInstance instance : instances) {
            List<Enrollment> enrollments = d2.enrollmentModule().enrollments()
                    .byTrackedEntityInstance().eq(instance.uid())
                    .blockingGet();
            if (enrollments != null && enrollments.size() == 1) {
                Enrollment enrollment = enrollments.get(0);
                List<Event> events =
                        d2.eventModule().events().byEnrollmentUid().eq(enrollment.uid()).blockingGet();

                if (events.size() == 1) {
                    Enrollment enrollmentWithEvents = EnrollmentInternalAccessor
                            .insertEvents(enrollment.toBuilder(), events).build();

                    return TrackedEntityInstanceInternalAccessor
                            .insertEnrollments(instance.toBuilder(), Collections.singletonList(enrollmentWithEvents))
                            .build();
                }
            }
        }
        throw new RuntimeException("TEI not found");
    }

    private TrackedEntityInstance setEventsToDelete(TrackedEntityInstance instance) {
        List<Enrollment> enrollments = new ArrayList<>();
        for (Enrollment enrollment : getEnrollments(instance)) {
            List<Event> events = new ArrayList<>();
            for (Event event : getEvents(enrollment)) {
                events.add(event.toBuilder().deleted(true).build());
            }
            enrollments.add(EnrollmentInternalAccessor.insertEvents(enrollment.toBuilder(), events).build());
        }

        return TrackedEntityInstanceInternalAccessor
                .insertEnrollments(instance.toBuilder(), enrollments)
                .build();
    }

    private List<Enrollment> getEnrollments(TrackedEntityInstance trackedEntityInstance) {
        return TrackedEntityInstanceInternalAccessor.accessEnrollments(trackedEntityInstance);
    }

    private List<Event> getEvents(Enrollment enrollment) {
        return EnrollmentInternalAccessor.accessEvents(enrollment);
    }
}