package org.hisp.dhis.android.core.trackedentity.api;

import junit.framework.Assert;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.APICallExecutor;
import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.imports.ImportSummary;
import org.hisp.dhis.android.core.imports.WebResponse;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstancePayload;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceService;
import org.junit.Before;
import org.junit.Test;

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

public class TrackedEntityInstanceAPIShould extends AbsStoreTestCase {

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

        d2= D2Factory.create(this.serverUrl, databaseAdapter());

        executor = new APICallExecutor();

        trackedEntityInstanceService = d2.retrofit().create(TrackedEntityInstanceService.class);
    }


    @Test
    public void stub() throws Exception {

    }

    //@Test
    public void tei_with_invalid_tracked_entity_attribute() throws Exception {
        login();

        TrackedEntityInstance validTEI = createValidTrackedEntityInstance();

        TrackedEntityInstance invalidTEI = createTrackedEntityInstanceWithInvalidAttribute();

        TrackedEntityInstancePayload payload = new TrackedEntityInstancePayload();
        payload.trackedEntityInstances = Arrays.asList(validTEI, invalidTEI);

        WebResponse response = executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                .postTrackedEntityInstances(payload, this.strategy), Collections.singletonList(409), WebResponse.class);

        assertThat(response.importSummaries().importStatus()).isEqualTo(ERROR);

        for (ImportSummary importSummary : response.importSummaries().importSummaries()) {
            if (validTEI.uid().equals(importSummary.reference())) {
                assertTei(importSummary, SUCCESS);
            }
            else if (invalidTEI.uid().equals(importSummary.reference())) {
                assertTei(importSummary, ERROR);
            }
        }

        // Check server status
        TrackedEntityInstance serverValidTEI = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstance(validTEI.uid(), TrackedEntityInstance.allFields, true));

        try {
            executor.executeObjectCall(trackedEntityInstanceService
                    .getTrackedEntityInstance(invalidTEI.uid(), TrackedEntityInstance.allFields, true));
            Assert.fail("Should not reach that line");
        } catch (D2CallException e) {
            assertThat(e.httpErrorCode()).isEqualTo(404);
        }

        assertThat(serverValidTEI).isNotNull();
    }

    //@Test
    public void tei_with_invalid_orgunit() throws Exception {
        login();

        TrackedEntityInstance validTEI = createValidTrackedEntityInstance();

        TrackedEntityInstance invalidTEI = createTrackedEntityInstanceWithInvalidOrgunit();

        TrackedEntityInstancePayload payload = new TrackedEntityInstancePayload();
        payload.trackedEntityInstances = Arrays.asList(validTEI, invalidTEI);

        WebResponse response = executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                .postTrackedEntityInstances(payload, this.strategy), Collections.singletonList(409), WebResponse.class);

        assertThat(response.importSummaries().importStatus()).isEqualTo(ERROR);

        for (ImportSummary importSummary : response.importSummaries().importSummaries()) {
            if (validTEI.uid().equals(importSummary.reference())) {
                assertTei(importSummary, SUCCESS);
            }
            else if (invalidTEI.uid().equals(importSummary.reference())) {
                assertTei(importSummary, ERROR);
            }
        }

        // Check server status
        TrackedEntityInstance serverValidTEI = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstance(validTEI.uid(), TrackedEntityInstance.allFields, true));

        try {
            executor.executeObjectCall(trackedEntityInstanceService
                    .getTrackedEntityInstance(invalidTEI.uid(), TrackedEntityInstance.allFields, true));
            Assert.fail("Should not reach that line");
        } catch (D2CallException e) {
            assertThat(e.httpErrorCode()).isEqualTo(404);
        }

        assertThat(serverValidTEI).isNotNull();
    }

    //@Test
    public void enrollment_with_valid_values() throws Exception {
        login();

        TrackedEntityInstance validTEI = createValidTrackedEntityInstance();

        TrackedEntityInstance invalidTEI = createValidTrackedEntityInstanceAndEnrollment();

        TrackedEntityInstancePayload payload = new TrackedEntityInstancePayload();
        payload.trackedEntityInstances = Arrays.asList(validTEI, invalidTEI);

        WebResponse response = executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                .postTrackedEntityInstances(payload, this.strategy), Collections.singletonList(409), WebResponse.class);

        assertThat(response.importSummaries().importStatus()).isEqualTo(SUCCESS);

        for (ImportSummary importSummary : response.importSummaries().importSummaries()) {
            if (validTEI.uid().equals(importSummary.reference())) {
                assertTei(importSummary, SUCCESS);
            }
            else if (invalidTEI.uid().equals(importSummary.reference())) {
                assertTei(importSummary, SUCCESS);
            }
        }

        // TODO Check server status
        TrackedEntityInstance serverValidTEI = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstance(validTEI.uid(), TrackedEntityInstance.allFields, true));

        TrackedEntityInstance serverInvalidTEI = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstance(invalidTEI.uid(), TrackedEntityInstance.allFields, true));

    }

    //@Test
    public void enrollment_future_date() throws Exception {
        login();

        TrackedEntityInstance validTEI = createValidTrackedEntityInstanceAndEnrollment();

        TrackedEntityInstance invalidTEI = createValidTrackedEntityInstanceWithFutureEnrollment();

        TrackedEntityInstancePayload payload = new TrackedEntityInstancePayload();
        payload.trackedEntityInstances = Arrays.asList(validTEI, invalidTEI);

        WebResponse response = executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                .postTrackedEntityInstances(payload, this.strategy), Collections.singletonList(409), WebResponse.class);

        assertThat(response.importSummaries().importStatus()).isEqualTo(SUCCESS);

        for (ImportSummary importSummary : response.importSummaries().importSummaries()) {
            if (validTEI.uid().equals(importSummary.reference())) {
                assertTei(importSummary, SUCCESS);
                assertEnrollments(importSummary, SUCCESS);
            }
            else if (invalidTEI.uid().equals(importSummary.reference())) {
                assertTei(importSummary, SUCCESS);
                assertEnrollments(importSummary, ERROR);
            }
        }

        TrackedEntityInstance serverValidTEI = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstance(validTEI.uid(), TrackedEntityInstance.allFields, true));

        TrackedEntityInstance serverInvalidTEI = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstance(invalidTEI.uid(), TrackedEntityInstance.allFields, true));

        assertThat(serverValidTEI.enrollments()).isNotEmpty();
        assertThat(serverInvalidTEI.enrollments()).isEmpty();
    }

    //@Test
    public void already_active_enrollment() throws Exception {
        login();

        TrackedEntityInstance validTEI = createValidTrackedEntityInstanceAndEnrollment();

        TrackedEntityInstance invalidTEI = createTrackedEntityInstanceAndTwoActiveEnrollment();

        TrackedEntityInstancePayload payload = new TrackedEntityInstancePayload();
        payload.trackedEntityInstances = Arrays.asList(validTEI, invalidTEI);

        WebResponse response = executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                .postTrackedEntityInstances(payload, this.strategy), Collections.singletonList(409), WebResponse.class);

        assertThat(response.importSummaries().importStatus()).isEqualTo(SUCCESS);

        for (ImportSummary importSummary : response.importSummaries().importSummaries()) {
            if (validTEI.uid().equals(importSummary.reference())) {
                assertTei(importSummary, SUCCESS);
                assertEnrollments(importSummary, SUCCESS);
            }
            else if (invalidTEI.uid().equals(importSummary.reference())) {
                assertTei(importSummary, SUCCESS);
                assertEnrollments(importSummary, ERROR);
                assertThat(importSummary.importEnrollment().imported()).isEqualTo(1);
                assertThat(importSummary.importEnrollment().ignored()).isEqualTo(1);
            }
        }

        TrackedEntityInstance serverValidTEI = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstance(validTEI.uid(), TrackedEntityInstance.allFields, true));

        TrackedEntityInstance serverInvalidTEI = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstance(invalidTEI.uid(), TrackedEntityInstance.allFields, true));

        assertThat(serverValidTEI.enrollments().size()).isEqualTo(1);
        assertThat(serverInvalidTEI.enrollments().size()).isEqualTo(1);
    }

    //@Test
    public void event_with_valid_values() throws Exception {
        login();

        TrackedEntityInstance validTEI1 = createValidTrackedEntityInstanceAndEnrollment();

        TrackedEntityInstance validTEI2 = createValidTrackedEntityInstanceWithEnrollmentAndEvent();

        TrackedEntityInstancePayload payload = new TrackedEntityInstancePayload();
        payload.trackedEntityInstances = Arrays.asList(validTEI1, validTEI2);

        WebResponse response = executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                .postTrackedEntityInstances(payload, this.strategy), Collections.singletonList(409), WebResponse.class);

        assertThat(response.importSummaries().importStatus()).isEqualTo(SUCCESS);

        for (ImportSummary importSummary : response.importSummaries().importSummaries()) {
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

        TrackedEntityInstance serverValidTEI1 = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstance(validTEI1.uid(), TrackedEntityInstance.allFields, true));

        TrackedEntityInstance serverValidTEI2 = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstance(validTEI2.uid(), TrackedEntityInstance.allFields, true));

        assertThat(serverValidTEI1.enrollments().size()).isEqualTo(1);

        assertThat(serverValidTEI2.enrollments().size()).isEqualTo(1);
        assertThat(serverValidTEI2.enrollments().get(0).events().size()).isEqualTo(1);

    }

    // IMPORTANT: check the programStage is set to "NO WRITE ACCESS" before running the test
    //@Test
    public void event_with_no_write_access() throws Exception {
        login();

        TrackedEntityInstance validTEI1 = createValidTrackedEntityInstanceAndEnrollment();

        TrackedEntityInstance validTEI2 = createValidTrackedEntityInstanceWithEnrollmentAndEvent();

        TrackedEntityInstancePayload payload = new TrackedEntityInstancePayload();
        payload.trackedEntityInstances = Arrays.asList(validTEI1, validTEI2);

        WebResponse response = executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                .postTrackedEntityInstances(payload, this.strategy), Collections.singletonList(409), WebResponse.class);

        assertThat(response.importSummaries().importStatus()).isEqualTo(SUCCESS);

        for (ImportSummary importSummary : response.importSummaries().importSummaries()) {
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

        TrackedEntityInstance serverValidTEI1 = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstance(validTEI1.uid(), TrackedEntityInstance.allFields, true));

        TrackedEntityInstance serverValidTEI2 = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstance(validTEI2.uid(), TrackedEntityInstance.allFields, true));

        assertThat(serverValidTEI1.enrollments().size()).isEqualTo(1);
        assertThat(serverValidTEI1.enrollments().get(0).events()).isEmpty();

        assertThat(serverValidTEI2.enrollments().size()).isEqualTo(1);
        assertThat(serverValidTEI2.enrollments().get(0).events()).isEmpty();

    }

    //@Test
    public void event_with_future_event_date_does_not_fail() throws Exception {
        login();

        TrackedEntityInstance validTEI = createValidTrackedEntityInstanceWithEnrollmentAndEvent();

        TrackedEntityInstance invalidTEI = createTrackedEntityInstanceWithEnrollmentAndFutureEvent();

        TrackedEntityInstancePayload payload = new TrackedEntityInstancePayload();
        payload.trackedEntityInstances = Arrays.asList(validTEI, invalidTEI);

        WebResponse response = executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                .postTrackedEntityInstances(payload, this.strategy), Collections.singletonList(409), WebResponse.class);

        assertThat(response.importSummaries().importStatus()).isEqualTo(SUCCESS);

        for (ImportSummary importSummary : response.importSummaries().importSummaries()) {
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

        TrackedEntityInstance serverValidTEI1 = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstance(validTEI.uid(), TrackedEntityInstance.allFields, true));

        TrackedEntityInstance serverValidTEI2 = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstance(invalidTEI.uid(), TrackedEntityInstance.allFields, true));

        assertThat(serverValidTEI1.enrollments().size()).isEqualTo(1);
        assertThat(serverValidTEI1.enrollments().get(0).events().size()).isEqualTo(1);

        assertThat(serverValidTEI2.enrollments().size()).isEqualTo(1);
        assertThat(serverValidTEI2.enrollments().get(0).events().size()).isEqualTo(1);

    }

    //@Test
    public void event_with_invalid_data_element() throws Exception {
        login();

        TrackedEntityInstance validTEI = createValidTrackedEntityInstanceWithEnrollmentAndEvent();

        TrackedEntityInstance invalidTEI = createTrackedEntityInstanceWithInvalidDataElement();

        TrackedEntityInstancePayload payload = new TrackedEntityInstancePayload();
        payload.trackedEntityInstances = Arrays.asList(validTEI, invalidTEI);

        WebResponse response = executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                .postTrackedEntityInstances(payload, this.strategy), Collections.singletonList(409), WebResponse.class);

        assertThat(response.importSummaries().importStatus()).isEqualTo(SUCCESS);

        for (ImportSummary importSummary : response.importSummaries().importSummaries()) {
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

        TrackedEntityInstance serverValidTEI1 = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstance(validTEI.uid(), TrackedEntityInstance.allFields, true));

        TrackedEntityInstance serverValidTEI2 = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstance(invalidTEI.uid(), TrackedEntityInstance.allFields, true));

        assertThat(serverValidTEI1.enrollments().size()).isEqualTo(1);
        assertThat(serverValidTEI1.enrollments().get(0).events().size()).isEqualTo(1);
        assertThat(serverValidTEI1.enrollments().get(0).events().get(0).trackedEntityDataValues().size()).isEqualTo(1);

        assertThat(serverValidTEI2.enrollments().size()).isEqualTo(1);
        assertThat(serverValidTEI2.enrollments().get(0).events().size()).isEqualTo(1);
        assertThat(serverValidTEI2.enrollments().get(0).events().get(0).trackedEntityDataValues()).isEmpty();
    }

    //@Test
    public void event_with_valid_and_invalid_data_value() throws Exception {
        login();

        TrackedEntityInstance validTEI = createValidTrackedEntityInstanceWithEnrollmentAndEvent();

        TrackedEntityInstance invalidTEI = createTrackedEntityInstanceWithValidAndInvalidDataValue();

        TrackedEntityInstancePayload payload = new TrackedEntityInstancePayload();
        payload.trackedEntityInstances = Arrays.asList(validTEI, invalidTEI);

        WebResponse response = executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                .postTrackedEntityInstances(payload, this.strategy), Collections.singletonList(409), WebResponse.class);

        assertThat(response.importSummaries().importStatus()).isEqualTo(SUCCESS);

        for (ImportSummary importSummary : response.importSummaries().importSummaries()) {
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

        TrackedEntityInstance serverValidTEI1 = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstance(validTEI.uid(), TrackedEntityInstance.allFields, true));

        TrackedEntityInstance serverValidTEI2 = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstance(invalidTEI.uid(), TrackedEntityInstance.allFields, true));

        assertThat(serverValidTEI1.enrollments().size()).isEqualTo(1);
        assertThat(serverValidTEI1.enrollments().get(0).events().size()).isEqualTo(1);
        assertThat(serverValidTEI1.enrollments().get(0).events().get(0).trackedEntityDataValues().size()).isEqualTo(1);

        assertThat(serverValidTEI2.enrollments().size()).isEqualTo(1);
        assertThat(serverValidTEI2.enrollments().get(0).events().size()).isEqualTo(1);
        assertThat(serverValidTEI2.enrollments().get(0).events().get(0).trackedEntityDataValues().size()).isEqualTo(1);
    }

    // This test is failing
    //@Test
    public void event_in_completed_enrollment() throws Exception {
        login();

        TrackedEntityInstance completedEnrollment = createTrackedEntityInstanceWithCompletedEnrollmentAndEvent();

        TrackedEntityInstancePayload payload = new TrackedEntityInstancePayload();
        payload.trackedEntityInstances = Arrays.asList(completedEnrollment);

        WebResponse response = executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                .postTrackedEntityInstances(payload, this.strategy), Collections.singletonList(409), WebResponse.class);

        assertThat(response.importSummaries().importStatus()).isEqualTo(SUCCESS);

        for (ImportSummary importSummary : response.importSummaries().importSummaries()) {
            if (completedEnrollment.uid().equals(importSummary.reference())) {
                assertTei(importSummary, SUCCESS);
                assertEnrollments(importSummary, SUCCESS);
                assertEvents(importSummary, SUCCESS);
            }
        }

        TrackedEntityInstance serverValidTEI1 = executor.executeObjectCall(trackedEntityInstanceService
                .getTrackedEntityInstance(completedEnrollment.uid(), TrackedEntityInstance.allFields, true));

        assertThat(serverValidTEI1.enrollments().size()).isEqualTo(1);
        assertThat(serverValidTEI1.enrollments().get(0).enrollmentStatus()).isEqualTo(EnrollmentStatus.COMPLETED);
        assertThat(serverValidTEI1.enrollments().get(0).events().size()).isEqualTo(1);
        assertThat(serverValidTEI1.enrollments().get(0).events().get(0).trackedEntityDataValues().size()).isEqualTo(1);
        assertThat(serverValidTEI1.enrollments().get(0).events().get(0).status()).isEqualTo(EventStatus.COMPLETED);
    }

    private void login() throws Exception {
        d2.logIn(RealServerMother.user, RealServerMother.password).call();
    }
}
