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
import static org.hisp.dhis.android.core.trackedentity.api.TrackedEntityInstanceUtils.assertEnrollments;
import static org.hisp.dhis.android.core.trackedentity.api.TrackedEntityInstanceUtils.assertEvents;
import static org.hisp.dhis.android.core.trackedentity.api.TrackedEntityInstanceUtils.assertTei;

import org.hisp.dhis.android.core.BaseRealIntegrationTest;
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor;
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutorImpl;
import org.hisp.dhis.android.core.arch.helpers.UidGenerator;
import org.hisp.dhis.android.core.arch.helpers.UidGeneratorImpl;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.enrollment.EnrollmentInternalAccessor;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.imports.internal.HttpMessageResponse;
import org.hisp.dhis.android.core.imports.internal.TEIImportSummary;
import org.hisp.dhis.android.core.imports.internal.TEIWebResponse;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstancePayload;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceService;
import org.hisp.dhis.android.core.trackedentity.ownership.OwnershipService;
import org.junit.Before;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

public class BreakTheGlassAPIShould extends BaseRealIntegrationTest {

    /**
     * Expected configuration to run these tests:
     * - user: android
     * - role: not a superuser
     * - capture orgunit: DiszpKrYNg8 - Negelhun CHC
     * - search orgunit: YuQRtpLP10I - Badja
     * <p>
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

    private APICallExecutor executor;

    private TrackedEntityInstanceService trackedEntityInstanceService;
    private OwnershipService ownershipService;

    private UidGenerator uidGenerator = new UidGeneratorImpl();

    @Before
    public void setUp() {
        super.setUp();

        executor = APICallExecutorImpl.create(d2.databaseAdapter(), null);

        trackedEntityInstanceService = d2.retrofit().create(TrackedEntityInstanceService.class);
        ownershipService = d2.retrofit().create(OwnershipService.class);

        login();
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

        TrackedEntityInstance tei = teiWithEnrollmentInSearchScope();

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

        TrackedEntityInstance tei = teiWithEnrollmentInSearchScope();

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
                executor.executeObjectCall(ownershipService.breakGlass(tei.uid(), program, "Sync"));

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
        return TrackedEntityInstanceInternalAccessor
                .insertEnrollments(TrackedEntityInstance.builder(), Arrays.asList(validEnrollment()))
                .uid(uidGenerator.generate())
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
                .build();
    }

    private Enrollment validEnrollment() {
        return EnrollmentInternalAccessor.insertEvents(Enrollment.builder(), Arrays.asList(validEvent()))
                .uid(uidGenerator.generate())
                .organisationUnit(captureOrgunit)
                .program(program)
                .status(EnrollmentStatus.ACTIVE)
                .build();
    }

    private Event validEvent() {
        return Event.builder()
                .uid(uidGenerator.generate())
                .organisationUnit(captureOrgunit)
                .programStage(programStage1)
                .attributeOptionCombo(attributeOptionCombo)
                .build();
    }

    private TrackedEntityInstancePayload wrapPayload(TrackedEntityInstance... instances) {
        TrackedEntityInstancePayload payload = TrackedEntityInstancePayload.create(Arrays.asList(instances));
        return payload;
    }

    private TrackedEntityInstance teiWithEventInSearchScope() {

        return TrackedEntityInstanceInternalAccessor.insertEnrollments(validTei().toBuilder(),
                Collections.singletonList(
                        EnrollmentInternalAccessor.insertEvents(validEnrollment().toBuilder(),
                                Collections.singletonList(validEvent().toBuilder()
                                        .organisationUnit(searchOrgunit)
                                        .build()))
                                .build()))
                .build();
    }

    private TrackedEntityInstance teiWithEnrollmentInSearchScope() {
        return TrackedEntityInstanceInternalAccessor.insertEnrollments(validTei().toBuilder(), Collections.singletonList(
                EnrollmentInternalAccessor.insertEvents(validEnrollment().toBuilder(),
                        Collections.singletonList(validEvent()))
                        .organisationUnit(searchOrgunit).build()))
                .build();
    }

    private TrackedEntityInstance teiInSearchScope() {
        return validTei().toBuilder()
                .organisationUnit(searchOrgunit)
                .build();
    }

    private void login() {
        d2.userModule().logIn(username, password, serverUrl).blockingGet();
    }
}
