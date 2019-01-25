package org.hisp.dhis.android.core.trackedentity.api;

import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.arch.api.executors.APICallExecutor;
import org.hisp.dhis.android.core.arch.api.executors.APICallExecutorImpl;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.imports.ImportSummary;
import org.hisp.dhis.android.core.imports.WebResponse;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFields;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstancePayload;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceService;
import org.hisp.dhis.android.core.trackedentity.glass.BreakGlassResponse;
import org.hisp.dhis.android.core.utils.CodeGenerator;
import org.hisp.dhis.android.core.utils.CodeGeneratorImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.imports.ImportStatus.ERROR;
import static org.hisp.dhis.android.core.imports.ImportStatus.SUCCESS;
import static org.hisp.dhis.android.core.trackedentity.api.TrackedEntityInstanceUtils.assertEnrollments;
import static org.hisp.dhis.android.core.trackedentity.api.TrackedEntityInstanceUtils.assertEvents;
import static org.hisp.dhis.android.core.trackedentity.api.TrackedEntityInstanceUtils.assertTei;
import static org.hisp.dhis.android.core.trackedentity.api.TrackedEntityInstanceUtils.createTrackedEntityInstanceWithInvalidAttribute;
import static org.hisp.dhis.android.core.trackedentity.api.TrackedEntityInstanceUtils.createValidTrackedEntityInstance;

@RunWith(AndroidJUnit4.class)
public class BreakTheGlassAPIShould extends AbsStoreTestCase {

    /**
     * Expected configuration to run these tests:
     * - user: android
     * - capture orgunit:
     * - search orgunit:
     *
     * - read/write access to program:
     */

    private String captureOrgunit = "DiszpKrYNg8";      // Ngelehun CHC
    private String searchOrgunit = "g8upMTyEZGZ";       // Njandama MCHP
    private String outOfScopeOrgunit = "jNb63DIHuwU";   // Baoma Hospital

    private String trackedEntityType = "nEenWmSyUEp";   // Person
    private String attribute1 = "w75KJ2mc4zz";          // First name
    private String attribute2 = "zDhUuAYrxNC";          // Last name


    private String program = "IpHINAT79UW";             // Child programme
    private String programStage1 = "A03MvHHogjR";       // Birth
    private String programStage2 = "ZzYYXq4fJie";       // Baby Postnatal
    private String attributeOptionCombo = "HllvX50cXC0";    // Default

    // API version dependant parameters
    private String serverUrl = RealServerMother.url2_30;
    private String strategy = "SYNC";

    private D2 d2;
    private APICallExecutor executor;

    private TrackedEntityInstanceService trackedEntityInstanceService;

    private CodeGenerator codeGenerator = new CodeGeneratorImpl();

    @Before
    public void setUp() throws IOException {
        super.setUp();

        d2 = D2Factory.create(this.serverUrl, databaseAdapter());

        executor = APICallExecutorImpl.create(d2.databaseAdapter());

        trackedEntityInstanceService = d2.retrofit().create(TrackedEntityInstanceService.class);

        try {
            login();
        } catch (Exception e) {
            throw new IOException();
        }
    }

    @Test
    public void tei_with_event_in_search_scope_in_open_program() throws Exception {

        TrackedEntityInstance tei = teiWithEventInSearchScope();

        for (int i = 0; i < 2; i++) {
            WebResponse response = executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                            .postTrackedEntityInstances(wrapPayload(tei), this.strategy), Collections.singletonList(409),
                    WebResponse.class);

            assertThat(response.importSummaries().importStatus()).isEqualTo(SUCCESS);

            for (ImportSummary importSummary : response.importSummaries().importSummaries()) {
                assertTei(importSummary, SUCCESS);
                assertEnrollments(importSummary, SUCCESS);
                assertEvents(importSummary, SUCCESS);
            }
        }
    }

    @Test
    public void tei_with_event_in_search_scope_in_protected_program() throws Exception {

        TrackedEntityInstance tei = teiWithEventInSearchScope();

        WebResponse response = executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                        .postTrackedEntityInstances(wrapPayload(tei), this.strategy), Collections.singletonList(409),
                WebResponse.class);

        assertThat(response.importSummaries().importStatus()).isEqualTo(SUCCESS);

        for (ImportSummary importSummary : response.importSummaries().importSummaries()) {
            assertTei(importSummary, SUCCESS);
            assertEnrollments(importSummary, SUCCESS);
            assertEvents(importSummary, SUCCESS);
        }

        WebResponse response2 =
                executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                        .postTrackedEntityInstances(wrapPayload(tei), this.strategy), Collections.singletonList(409),
                WebResponse.class);

        assertThat(response2.importSummaries().importStatus()).isEqualTo(SUCCESS);

        for (ImportSummary importSummary : response2.importSummaries().importSummaries()) {
            assertTei(importSummary, SUCCESS);
            assertEnrollments(importSummary, SUCCESS);
            assertEvents(importSummary, SUCCESS);
        }
    }

    @Test
    public void tei_with_enrollment_in_search_scope_breaking_glass() throws Exception {

        TrackedEntityInstance tei = teiWithEnollmentInSearchScope();

        WebResponse response = executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                        .postTrackedEntityInstances(wrapPayload(tei), this.strategy), Collections.singletonList(409),
                WebResponse.class);

        assertThat(response.importSummaries().importStatus()).isEqualTo(SUCCESS);

        for (ImportSummary importSummary : response.importSummaries().importSummaries()) {
            assertTei(importSummary, SUCCESS);
            assertEnrollments(importSummary, ERROR);
        }

        BreakGlassResponse glassResponse =
                executor.executeObjectCall(trackedEntityInstanceService.breakGlass(tei.uid(), program, "Sync"));

        WebResponse secondResponse = executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                        .postTrackedEntityInstances(wrapPayload(tei), this.strategy), Collections.singletonList(409),
                WebResponse.class);

        assertThat(secondResponse.importSummaries().importStatus()).isEqualTo(SUCCESS);

        for (ImportSummary importSummary : secondResponse.importSummaries().importSummaries()) {
            assertTei(importSummary, SUCCESS);
            assertEnrollments(importSummary, SUCCESS);
            assertEvents(importSummary, SUCCESS);
        }
    }

    @Test
    public void tei_in_search_scope() throws Exception {

        TrackedEntityInstance tei = teiInSearchScope();

        WebResponse response = executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                        .postTrackedEntityInstances(wrapPayload(tei), this.strategy), Collections.singletonList(409),
                WebResponse.class);

        assertThat(response.importSummaries().importStatus()).isEqualTo(SUCCESS);

        for (ImportSummary importSummary : response.importSummaries().importSummaries()) {
            assertTei(importSummary, SUCCESS);
            assertEnrollments(importSummary, SUCCESS);
            assertEvents(importSummary, SUCCESS);
        }
    }

    // Make program protected
    @Test
    public void tei_with_enrollment_in_search_scope_in_protected_program() throws Exception {

        TrackedEntityInstance tei = teiWithEnollmentInSearchScope();

        WebResponse response = executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                        .postTrackedEntityInstances(wrapPayload(tei), this.strategy), Collections.singletonList(409),
                WebResponse.class);

        assertThat(response.importSummaries().importStatus()).isEqualTo(SUCCESS);

        for (ImportSummary importSummary : response.importSummaries().importSummaries()) {
            assertTei(importSummary, SUCCESS);
            assertEnrollments(importSummary, SUCCESS);  // Because it is the first upload.Ownership is not defined
            assertEvents(importSummary, ERROR);         // It takes enrollment ownership
        }

        WebResponse response2 = executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                        .postTrackedEntityInstances(wrapPayload(tei), this.strategy), Collections.singletonList(409),
                WebResponse.class);

        assertThat(response2.importSummaries().importStatus()).isEqualTo(SUCCESS);

        for (ImportSummary importSummary : response2.importSummaries().importSummaries()) {
            assertTei(importSummary, SUCCESS);
            assertEnrollments(importSummary, ERROR);    // Because ownership was previously set
        }
    }

    // Make program protected
    @Test
    public void tei_with_enrollment_in_search_scope_in_protected_program_breaking_glass() throws Exception {

        TrackedEntityInstance tei = teiWithEnollmentInSearchScope();

        WebResponse response = executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                        .postTrackedEntityInstances(wrapPayload(tei), this.strategy), Collections.singletonList(409),
                WebResponse.class);

        assertThat(response.importSummaries().importStatus()).isEqualTo(SUCCESS);

        for (ImportSummary importSummary : response.importSummaries().importSummaries()) {
            assertTei(importSummary, SUCCESS);
            assertEnrollments(importSummary, SUCCESS);  // Because it is the first upload.Ownership is not defined
            assertEvents(importSummary, ERROR);         // It takes enrollment ownership
        }

        BreakGlassResponse glassResponse =
                executor.executeObjectCall(trackedEntityInstanceService.breakGlass(tei.uid(), program, "Sync"));

        WebResponse response2 = executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                        .postTrackedEntityInstances(wrapPayload(tei), this.strategy), Collections.singletonList(409),
                WebResponse.class);

        assertThat(response2.importSummaries().importStatus()).isEqualTo(SUCCESS);

        for (ImportSummary importSummary : response2.importSummaries().importSummaries()) {
            assertTei(importSummary, SUCCESS);
            assertEnrollments(importSummary, SUCCESS);
            assertEvents(importSummary, SUCCESS);

        }
    }

    private TrackedEntityInstance validTei() {
        return TrackedEntityInstance.builder()
                .uid(codeGenerator.generate())
                .organisationUnit(captureOrgunit)
                .trackedEntityType(trackedEntityType)
                .trackedEntityAttributeValues(Arrays.asList(
                        TrackedEntityAttributeValue.builder()
                                .trackedEntityAttribute(attribute1)
                                .value("Test")
                                .build(),
                        TrackedEntityAttributeValue.builder()
                                .trackedEntityAttribute(attribute2)
                                .value("TrackedEntity")
                                .build()
                ))
                .enrollments(Arrays.asList(validEnrollment()))
                .build();
    }

    private Enrollment validEnrollment() {
        return Enrollment.builder()
                .uid(codeGenerator.generate())
                .organisationUnit(captureOrgunit)
                .program(program)
                .status(EnrollmentStatus.ACTIVE)
                .events(Arrays.asList(validEvent()))
                .build();
    }

    private Event validEvent() {
        return Event.builder()
                .uid(codeGenerator.generate())
                .organisationUnit(captureOrgunit)
                .programStage(programStage1)
                .attributeOptionCombo(attributeOptionCombo)
                .build();
    }

    private TrackedEntityInstancePayload wrapPayload(TrackedEntityInstance ...instances) {
        TrackedEntityInstancePayload payload = new TrackedEntityInstancePayload();
        payload.trackedEntityInstances = Arrays.asList(instances);
        return payload;
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
                .getTrackedEntityInstance(validTEI.uid(), TrackedEntityInstanceFields.allFields, true));

        try {
            executor.executeObjectCall(trackedEntityInstanceService
                    .getTrackedEntityInstance(invalidTEI.uid(), TrackedEntityInstanceFields.allFields, true));
            Assert.fail("Should not reach that line");
        } catch (D2Error e) {
            assertThat(e.httpErrorCode()).isEqualTo(404);
        }

        assertThat(serverValidTEI).isNotNull();
    }

    private TrackedEntityInstance teiWithEventInSearchScope() {
        return validTei().toBuilder()
                .enrollments(Arrays.asList(validEnrollment().toBuilder()
                        .events(Arrays.asList(validEvent().toBuilder()
                                .organisationUnit(searchOrgunit)
                                .build()))
                        .build()))
                .build();
    }

    private TrackedEntityInstance teiWithEnollmentInSearchScope() {
        return validTei().toBuilder()
                .enrollments(Arrays.asList(validEnrollment().toBuilder()
                        .organisationUnit(searchOrgunit)
                        .events(Arrays.asList(validEvent()))
                        .build()))
                .build();
    }

    private TrackedEntityInstance teiInSearchScope() {
        return validTei().toBuilder()
                .organisationUnit(searchOrgunit)
                .build();
    }
    private void login() throws Exception {
        d2.userModule().logIn(RealServerMother.user, RealServerMother.password).call();
    }
}
