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

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor;
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutorImpl;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.imports.internal.HttpMessageResponse;
import org.hisp.dhis.android.core.imports.internal.TEIImportSummary;
import org.hisp.dhis.android.core.imports.internal.TEIWebResponse;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstancePayload;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceService;
import org.hisp.dhis.android.core.utils.CodeGenerator;
import org.hisp.dhis.android.core.utils.CodeGeneratorImpl;
import org.hisp.dhis.android.core.utils.integration.real.BaseRealIntegrationTest;
import org.junit.Before;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.imports.ImportStatus.ERROR;
import static org.hisp.dhis.android.core.imports.ImportStatus.SUCCESS;
import static org.hisp.dhis.android.core.trackedentity.api.TrackedEntityInstanceUtils.assertEnrollments;
import static org.hisp.dhis.android.core.trackedentity.api.TrackedEntityInstanceUtils.assertEvents;
import static org.hisp.dhis.android.core.trackedentity.api.TrackedEntityInstanceUtils.assertTei;

public class BreakTheGlassAPIShould extends BaseRealIntegrationTest {

    /**
     * Expected configuration to run these tests:
     * - user: android
     * - role: not a superuser
     * - capture orgunit: DiszpKrYNg8 - Negelhun CHC
     * - search orgunit: YuQRtpLP10I - Badja
     *
     * - read/write access to PROTECTED program: IpHINAT79UW
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

    //@Test
    public void tei_with_event_in_search_scope_in_open_program() throws Exception {

        TrackedEntityInstance tei = teiWithEventInSearchScope();

        for (int i = 0; i < 2; i++) {
            TEIWebResponse response =
                    executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                            .postTrackedEntityInstances(wrapPayload(tei), this.strategy), Collections.singletonList(409),
                    TEIWebResponse.class);

            assertThat(response.response().status()).isEqualTo(SUCCESS);

            for (TEIImportSummary importSummary : response.response().importSummaries()) {
                assertTei(importSummary, SUCCESS);
                assertEnrollments(importSummary, SUCCESS);
                assertEvents(importSummary, SUCCESS);
            }
        }
    }

    // Make program protected
    //@Test
    public void tei_with_event_in_search_scope_in_protected_program() throws Exception {

        TrackedEntityInstance tei = teiWithEventInSearchScope();

        TEIWebResponse response = executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                        .postTrackedEntityInstances(wrapPayload(tei), this.strategy), Collections.singletonList(409),
                TEIWebResponse.class);

        assertThat(response.response().status()).isEqualTo(SUCCESS);

        for (TEIImportSummary importSummary : response.response().importSummaries()) {
            assertTei(importSummary, SUCCESS);
            assertEnrollments(importSummary, SUCCESS);
            assertEvents(importSummary, SUCCESS);
        }

        TEIWebResponse response2 =
                executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                        .postTrackedEntityInstances(wrapPayload(tei), this.strategy), Collections.singletonList(409),
                        TEIWebResponse.class);

        assertThat(response2.response().status()).isEqualTo(SUCCESS);

        for (TEIImportSummary importSummary : response2.response().importSummaries()) {
            assertTei(importSummary, SUCCESS);
            assertEnrollments(importSummary, SUCCESS);
            assertEvents(importSummary, SUCCESS);
        }
    }

    // Make program protected
    //@Test
    public void tei_with_enrollment_in_search_scope_in_protected_program() throws Exception {

        TrackedEntityInstance tei = teiWithEnollmentInSearchScope();

        TEIWebResponse response = executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                        .postTrackedEntityInstances(wrapPayload(tei), this.strategy), Collections.singletonList(409),
                TEIWebResponse.class);

        assertThat(response.response().status()).isEqualTo(SUCCESS);

        for (TEIImportSummary importSummary : response.response().importSummaries()) {
            assertTei(importSummary, SUCCESS);
            assertEnrollments(importSummary, SUCCESS);  // Because it is the first upload.Ownership is not defined
            assertEvents(importSummary, ERROR);         // It takes enrollment ownership
        }

        TEIWebResponse response2 = executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                        .postTrackedEntityInstances(wrapPayload(tei), this.strategy), Collections.singletonList(409),
                TEIWebResponse.class);

        assertThat(response2.response().status()).isEqualTo(SUCCESS);

        for (TEIImportSummary importSummary : response2.response().importSummaries()) {
            assertTei(importSummary, SUCCESS);
            assertEnrollments(importSummary, ERROR);    // Because ownership was previously set
        }
    }

    // Make program protected
    // @Test
    public void tei_with_enrollment_in_search_scope_in_protected_program_breaking_glass() throws Exception {

        TrackedEntityInstance tei = teiWithEnollmentInSearchScope();

        TEIWebResponse response = executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                        .postTrackedEntityInstances(wrapPayload(tei), this.strategy), Collections.singletonList(409),
                TEIWebResponse.class);

        assertThat(response.response().status()).isEqualTo(SUCCESS);

        for (TEIImportSummary importSummary : response.response().importSummaries()) {
            assertTei(importSummary, SUCCESS);
            assertEnrollments(importSummary, SUCCESS);  // Because it is the first upload.Ownership is not defined
            assertEvents(importSummary, ERROR);         // It takes enrollment ownership
        }

        HttpMessageResponse glassResponse =
                executor.executeObjectCall(trackedEntityInstanceService.breakGlass(tei.uid(), program, "Sync"));

        TEIWebResponse response2 = executor.executeObjectCallWithAcceptedErrorCodes(trackedEntityInstanceService
                        .postTrackedEntityInstances(wrapPayload(tei), this.strategy), Collections.singletonList(409),
                TEIWebResponse.class);

        assertThat(response2.response().status()).isEqualTo(SUCCESS);

        for (TEIImportSummary importSummary : response2.response().importSummaries()) {
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
    private void login() {
        d2.userModule().logIn(RealServerMother.user, RealServerMother.password).blockingGet();
    }
}
