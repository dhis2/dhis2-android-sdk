/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.trackedentity.api

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.BaseRealIntegrationTest
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutor
import org.hisp.dhis.android.core.arch.helpers.UidGenerator
import org.hisp.dhis.android.core.arch.helpers.UidGeneratorImpl
import org.hisp.dhis.android.core.data.server.RealServerMother
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.EnrollmentInternalAccessor
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.imports.ImportStatus
import org.hisp.dhis.android.core.imports.internal.HttpMessageResponse
import org.hisp.dhis.android.core.imports.internal.TEIWebResponse
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstancePayload
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceService
import org.hisp.dhis.android.core.trackedentity.ownership.OwnershipService
import org.junit.Before
import java.util.Arrays

@OptIn(ExperimentalCoroutinesApi::class)
class BreakTheGlassAPIShould : BaseRealIntegrationTest() {
    /**
     * Expected configuration to run these tests:
     * - user: android
     * - role: not a superuser
     * - capture orgunit: DiszpKrYNg8 - Negelhun CHC
     * - search orgunit: YuQRtpLP10I - Badja
     *
     *
     * - read/write access to PROTECTED program: IpHINAT79UW
     */
    private val captureOrgunit = "DiszpKrYNg8" // Ngelehun CHC
    private val searchOrgunit = "g8upMTyEZGZ" // Njandama MCHP
    private val outOfScopeOrgunit = "jNb63DIHuwU" // Baoma Hospital
    private val trackedEntityType = "nEenWmSyUEp" // Person
    private val attribute1 = "w75KJ2mc4zz" // First name
    private val attribute2 = "zDhUuAYrxNC" // Last name
    private val program = "IpHINAT79UW" // Child programme
    private val programStage1 = "A03MvHHogjR" // Birth
    private val programStage2 = "ZzYYXq4fJie" // Baby Postnatal
    private val attributeOptionCombo = "HllvX50cXC0" // Default

    // API version dependant parameters
    private val serverUrl = RealServerMother.url2_30
    private val strategy = "SYNC"
    private lateinit var executor: CoroutineAPICallExecutor
    private lateinit var trackedEntityInstanceService: TrackedEntityInstanceService
    private lateinit var ownershipService: OwnershipService
    private val uidGenerator: UidGenerator = UidGeneratorImpl()
    @Before
    override fun setUp() {
        super.setUp()
        executor = d2.coroutineAPICallExecutor()
        trackedEntityInstanceService = d2.retrofit().create(
            TrackedEntityInstanceService::class.java
        )
        ownershipService = d2.retrofit().create(OwnershipService::class.java)
        login()
    }

    //@Test
    @Throws(Exception::class)
    fun tei_with_event_in_search_scope_in_open_program() = runTest {
        val tei = teiWithEventInSearchScope()
        for (i in 0..1) {
            val response = postTrackedEntities(tei)
            assertThat(response.response()!!.status()).isEqualTo(ImportStatus.SUCCESS)

            for (importSummary in response.response()!!.importSummaries()!!) {
                TrackedEntityInstanceUtils.assertTei(importSummary, ImportStatus.SUCCESS)
                TrackedEntityInstanceUtils.assertEnrollments(importSummary, ImportStatus.SUCCESS)
                TrackedEntityInstanceUtils.assertEvents(importSummary, ImportStatus.SUCCESS)
            }
        }
    }

    // Make program protected
    //@Test
    @Throws(Exception::class)
    fun tei_with_event_in_search_scope_in_protected_program() = runTest {
        val tei = teiWithEventInSearchScope()

        val response = postTrackedEntities(tei)
        assertThat(response.response()!!.status()).isEqualTo(ImportStatus.SUCCESS)
        for (importSummary in response.response()!!.importSummaries()!!) {
            TrackedEntityInstanceUtils.assertTei(importSummary, ImportStatus.SUCCESS)
            TrackedEntityInstanceUtils.assertEnrollments(importSummary, ImportStatus.SUCCESS)
            TrackedEntityInstanceUtils.assertEvents(importSummary, ImportStatus.SUCCESS)
        }

        val response2 = postTrackedEntities(tei)
        assertThat(response2.response()!!.status()).isEqualTo(ImportStatus.SUCCESS)
        for (importSummary in response2.response()!!.importSummaries()!!) {
            TrackedEntityInstanceUtils.assertTei(importSummary, ImportStatus.SUCCESS)
            TrackedEntityInstanceUtils.assertEnrollments(importSummary, ImportStatus.SUCCESS)
            TrackedEntityInstanceUtils.assertEvents(importSummary, ImportStatus.SUCCESS)
        }
    }

    // Make program protected
    //@Test
    @Throws(Exception::class)
    fun tei_with_enrollment_in_search_scope_in_protected_program() = runTest {
        val tei = teiWithEnrollmentInSearchScope()
        val response = postTrackedEntities(tei)
        assertThat(response.response()!!.status()).isEqualTo(ImportStatus.SUCCESS)
        for (importSummary in response.response()!!.importSummaries()!!) {
            TrackedEntityInstanceUtils.assertTei(importSummary, ImportStatus.SUCCESS)
            TrackedEntityInstanceUtils.assertEnrollments(
                importSummary,
                ImportStatus.SUCCESS
            ) // Because it is the first upload.Ownership is not defined
            TrackedEntityInstanceUtils.assertEvents(importSummary, ImportStatus.ERROR) // It takes enrollment ownership
        }

        val response2 = postTrackedEntities(tei)
        assertThat(response2.response()!!.status()).isEqualTo(ImportStatus.SUCCESS)
        for (importSummary in response2.response()!!.importSummaries()!!) {
            TrackedEntityInstanceUtils.assertTei(importSummary, ImportStatus.SUCCESS)
            TrackedEntityInstanceUtils.assertEnrollments(
                importSummary,
                ImportStatus.ERROR
            ) // Because ownership was previously set
        }
    }

    // Make program protected
    // @Test
    @Throws(Exception::class)
    fun tei_with_enrollment_in_search_scope_in_protected_program_breaking_glass() = runTest {
        val tei = teiWithEnrollmentInSearchScope()
        val response = postTrackedEntities(tei)
        assertThat(response.response()!!.status()).isEqualTo(ImportStatus.SUCCESS)
        for (importSummary in response.response()!!.importSummaries()!!) {
            TrackedEntityInstanceUtils.assertTei(importSummary, ImportStatus.SUCCESS)
            TrackedEntityInstanceUtils.assertEnrollments(
                importSummary,
                ImportStatus.SUCCESS
            ) // Because it is the first upload.Ownership is not defined
            TrackedEntityInstanceUtils.assertEvents(importSummary, ImportStatus.ERROR) // It takes enrollment ownership
        }

        val glassResponse: HttpMessageResponse =
            executor.wrap { ownershipService.breakGlass(tei.uid(), program, "Sync") }.getOrThrow()

        val response2 = postTrackedEntities(tei)
        assertThat(response2.response()!!.status()).isEqualTo(ImportStatus.SUCCESS)
        for (importSummary in response2.response()!!.importSummaries()!!) {
            TrackedEntityInstanceUtils.assertTei(importSummary, ImportStatus.SUCCESS)
            TrackedEntityInstanceUtils.assertEnrollments(importSummary, ImportStatus.SUCCESS)
            TrackedEntityInstanceUtils.assertEvents(importSummary, ImportStatus.SUCCESS)
        }
    }

    private fun validTei(): TrackedEntityInstance {
        return TrackedEntityInstanceInternalAccessor
            .insertEnrollments(TrackedEntityInstance.builder(), Arrays.asList(validEnrollment()))
            .uid(uidGenerator.generate())
            .organisationUnit(captureOrgunit)
            .trackedEntityType(trackedEntityType)
            .trackedEntityAttributeValues(
                Arrays.asList(
                    TrackedEntityAttributeValue.builder()
                        .trackedEntityAttribute(attribute1)
                        .value("Test")
                        .build(),
                    TrackedEntityAttributeValue.builder()
                        .trackedEntityAttribute(attribute2)
                        .value("TrackedEntity")
                        .build()
                )
            )
            .build()
    }

    private fun validEnrollment(): Enrollment {
        return EnrollmentInternalAccessor.insertEvents(Enrollment.builder(), Arrays.asList(validEvent()))
            .uid(uidGenerator.generate())
            .organisationUnit(captureOrgunit)
            .program(program)
            .status(EnrollmentStatus.ACTIVE)
            .build()
    }

    private fun validEvent(): Event {
        return Event.builder()
            .uid(uidGenerator.generate())
            .organisationUnit(captureOrgunit)
            .programStage(programStage1)
            .attributeOptionCombo(attributeOptionCombo)
            .build()
    }

    private fun wrapPayload(vararg instances: TrackedEntityInstance): TrackedEntityInstancePayload {
        return TrackedEntityInstancePayload.create(listOf(*instances))
    }

    private fun teiWithEventInSearchScope(): TrackedEntityInstance {
        return TrackedEntityInstanceInternalAccessor.insertEnrollments(
            validTei().toBuilder(), listOf(
                EnrollmentInternalAccessor.insertEvents(
                    validEnrollment().toBuilder(), listOf(
                        validEvent().toBuilder()
                            .organisationUnit(searchOrgunit)
                            .build()
                    )
                )
                    .build()
            )
        )
            .build()
    }

    private fun teiWithEnrollmentInSearchScope(): TrackedEntityInstance {
        return TrackedEntityInstanceInternalAccessor.insertEnrollments(
            validTei().toBuilder(), listOf(
                EnrollmentInternalAccessor.insertEvents(validEnrollment().toBuilder(), listOf(validEvent()))
                    .organisationUnit(searchOrgunit).build()
            )
        )
            .build()
    }

    private fun teiInSearchScope(): TrackedEntityInstance {
        return validTei().toBuilder()
            .organisationUnit(searchOrgunit)
            .build()
    }

    private suspend fun postTrackedEntities(vararg instances: TrackedEntityInstance): TEIWebResponse {
        return executor.wrap(
            storeError = false,
            acceptedErrorCodes = listOf(409),
            errorClass = TEIWebResponse::class.java
        ) {
            trackedEntityInstanceService
                .postTrackedEntityInstances(wrapPayload(*instances), strategy)
        }.getOrThrow()
    }

    private fun login() {
        d2.userModule().logIn(username, password, serverUrl).blockingGet()
    }
}