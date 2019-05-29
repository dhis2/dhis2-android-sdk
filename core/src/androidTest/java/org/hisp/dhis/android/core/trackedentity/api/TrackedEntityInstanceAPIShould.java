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

package org.hisp.dhis.android.core.trackedentity.api;

import junit.framework.Assert;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.arch.api.executors.APICallExecutor;
import org.hisp.dhis.android.core.arch.api.executors.APICallExecutorImpl;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.common.Payload;
import org.hisp.dhis.android.core.utils.integration.real.BaseRealIntegrationTest;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.imports.TEIImportSummary;
import org.hisp.dhis.android.core.imports.TEIWebResponse;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFields;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstancePayload;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceService;
import org.junit.Before;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

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

public abstract class TrackedEntityInstanceAPIShould extends BaseRealIntegrationTest {

    // API version dependant parameters
    private String serverUrl;
    private String strategy;

    private D2 d2;
    private APICallExecutor executor;

    private TrackedEntityInstanceService trackedEntityInstanceService;

    TrackedEntityInstanceAPIShould(String serverUrl, String strategy) {
        super();
        this.serverUrl = serverUrl;
        this.strategy = strategy;
    }

    @Before
    public void setUp() throws IOException {
        super.setUp();

        d2 = D2Factory.create(this.serverUrl, databaseAdapter());

        executor = APICallExecutorImpl.create(d2.databaseAdapter());

        trackedEntityInstanceService = d2.retrofit().create(TrackedEntityInstanceService.class);
    }

    //@Test
    public void tei_with_invalid_tracked_entity_attribute() throws Exception {
        login();

        TrackedEntityInstance validTEI = createValidTrackedEntityInstance();

        TrackedEntityInstance invalidTEI = createTrackedEntityInstanceWithInvalidAttribute();

        TrackedEntityInstancePayload payload = new TrackedEntityInstancePayload();
        payload.trackedEntityInstances = Arrays.asList(validTEI, invalidTEI);

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

        TrackedEntityInstancePayload payload = new TrackedEntityInstancePayload();
        payload.trackedEntityInstances = Arrays.asList(validTEI, invalidTEI);

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

        TrackedEntityInstancePayload payload = new TrackedEntityInstancePayload();
        payload.trackedEntityInstances = Arrays.asList(validTEI, invalidTEI);

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

        TrackedEntityInstancePayload payload = new TrackedEntityInstancePayload();
        payload.trackedEntityInstances = Arrays.asList(validTEI, invalidTEI);

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

        assertThat(serverValidTEI.items().get(0).enrollments()).isNotEmpty();
        assertThat(serverInvalidTEI.items().get(0).enrollments()).isEmpty();
    }

    //@Test
    public void already_active_enrollment() throws Exception {
        login();

        TrackedEntityInstance validTEI = createValidTrackedEntityInstanceAndEnrollment();

        TrackedEntityInstance invalidTEI = createTrackedEntityInstanceAndTwoActiveEnrollment();

        TrackedEntityInstancePayload payload = new TrackedEntityInstancePayload();
        payload.trackedEntityInstances = Arrays.asList(validTEI, invalidTEI);

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

        assertThat(serverValidTEI.items().get(0).enrollments().size()).isEqualTo(1);
        assertThat(serverInvalidTEI.items().get(0).enrollments().size()).isEqualTo(1);
    }

    //@Test
    public void event_with_valid_values() throws Exception {
        login();

        TrackedEntityInstance validTEI1 = createValidTrackedEntityInstanceAndEnrollment();

        TrackedEntityInstance validTEI2 = createValidTrackedEntityInstanceWithEnrollmentAndEvent();

        TrackedEntityInstancePayload payload = new TrackedEntityInstancePayload();
        payload.trackedEntityInstances = Arrays.asList(validTEI1, validTEI2);

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

        assertThat(serverValidTEI1.items().get(0).enrollments().size()).isEqualTo(1);

        assertThat(serverValidTEI2.items().get(0).enrollments().size()).isEqualTo(1);
        assertThat(serverValidTEI2.items().get(0).enrollments().get(0).events().size()).isEqualTo(1);

    }

    // IMPORTANT: check the programStage is set to "NO WRITE ACCESS" before running the test
    //@Test
    public void event_with_no_write_access() throws Exception {
        login();

        TrackedEntityInstance validTEI1 = createValidTrackedEntityInstanceAndEnrollment();

        TrackedEntityInstance validTEI2 = createValidTrackedEntityInstanceWithEnrollmentAndEvent();

        TrackedEntityInstancePayload payload = new TrackedEntityInstancePayload();
        payload.trackedEntityInstances = Arrays.asList(validTEI1, validTEI2);

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

        assertThat(serverValidTEI1.items().get(0).enrollments().size()).isEqualTo(1);
        assertThat(serverValidTEI1.items().get(0).enrollments().get(0).events()).isEmpty();

        assertThat(serverValidTEI2.items().get(0).enrollments().size()).isEqualTo(1);
        assertThat(serverValidTEI2.items().get(0).enrollments().get(0).events()).isEmpty();

    }

    //@Test
    public void event_with_future_event_date_does_not_fail() throws Exception {
        login();

        TrackedEntityInstance validTEI = createValidTrackedEntityInstanceWithEnrollmentAndEvent();

        TrackedEntityInstance invalidTEI = createTrackedEntityInstanceWithEnrollmentAndFutureEvent();

        TrackedEntityInstancePayload payload = new TrackedEntityInstancePayload();
        payload.trackedEntityInstances = Arrays.asList(validTEI, invalidTEI);

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

        assertThat(serverValidTEI1.items().get(0).enrollments().size()).isEqualTo(1);
        assertThat(serverValidTEI1.items().get(0).enrollments().get(0).events().size()).isEqualTo(1);

        assertThat(serverValidTEI2.items().get(0).enrollments().size()).isEqualTo(1);
        assertThat(serverValidTEI2.items().get(0).enrollments().get(0).events().size()).isEqualTo(1);

    }

    //@Test
    public void event_with_invalid_data_element() throws Exception {
        login();

        TrackedEntityInstance validTEI = createValidTrackedEntityInstanceWithEnrollmentAndEvent();

        TrackedEntityInstance invalidTEI = createTrackedEntityInstanceWithInvalidDataElement();

        TrackedEntityInstancePayload payload = new TrackedEntityInstancePayload();
        payload.trackedEntityInstances = Arrays.asList(validTEI, invalidTEI);

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

        assertThat(serverValidTEI1.items().get(0).enrollments().size()).isEqualTo(1);
        assertThat(serverValidTEI1.items().get(0).enrollments().get(0).events().size()).isEqualTo(1);
        assertThat(serverValidTEI1.items().get(0).enrollments().get(0).events().get(0)
                .trackedEntityDataValues().size()).isEqualTo(1);

        assertThat(serverValidTEI2.items().get(0).enrollments().size()).isEqualTo(1);
        assertThat(serverValidTEI2.items().get(0).enrollments().get(0).events().size()).isEqualTo(1);
        assertThat(serverValidTEI2.items().get(0).enrollments().get(0).events().get(0)
                .trackedEntityDataValues()).isEmpty();
    }

    //@Test
    public void event_with_valid_and_invalid_data_value() throws Exception {
        login();

        TrackedEntityInstance validTEI = createValidTrackedEntityInstanceWithEnrollmentAndEvent();

        TrackedEntityInstance invalidTEI = createTrackedEntityInstanceWithValidAndInvalidDataValue();

        TrackedEntityInstancePayload payload = new TrackedEntityInstancePayload();
        payload.trackedEntityInstances = Arrays.asList(validTEI, invalidTEI);

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

        assertThat(serverValidTEI1.items().get(0).enrollments().size()).isEqualTo(1);
        assertThat(serverValidTEI1.items().get(0).enrollments().get(0).events().size()).isEqualTo(1);
        assertThat(serverValidTEI1.items().get(0).enrollments().get(0).events().get(0)
                .trackedEntityDataValues().size()).isEqualTo(1);

        assertThat(serverValidTEI2.items().get(0).enrollments().size()).isEqualTo(1);
        assertThat(serverValidTEI2.items().get(0).enrollments().get(0).events().size()).isEqualTo(1);
        assertThat(serverValidTEI2.items().get(0).enrollments().get(0).events().get(0)
                .trackedEntityDataValues().size()).isEqualTo(1);
    }

    // This test is failing
    //@Test
    public void event_in_completed_enrollment() throws Exception {
        login();

        TrackedEntityInstance completedEnrollment = createTrackedEntityInstanceWithCompletedEnrollmentAndEvent();

        TrackedEntityInstancePayload payload = new TrackedEntityInstancePayload();
        payload.trackedEntityInstances = Arrays.asList(completedEnrollment);

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

        assertThat(serverValidTEI1.items().get(0).enrollments().size()).isEqualTo(1);
        assertThat(serverValidTEI1.items().get(0).enrollments().get(0)
                .status()).isEqualTo(EnrollmentStatus.COMPLETED);
        assertThat(serverValidTEI1.items().get(0).enrollments().get(0).events().size()).isEqualTo(1);
        assertThat(serverValidTEI1.items().get(0).enrollments().get(0).events().get(0)
                .trackedEntityDataValues().size()).isEqualTo(1);
        assertThat(serverValidTEI1.items().get(0).enrollments().get(0)
                .events().get(0).status()).isEqualTo(EventStatus.COMPLETED);
    }

    private void login() throws Exception {
        d2.userModule().logIn(RealServerMother.user, RealServerMother.password).call();
    }
}