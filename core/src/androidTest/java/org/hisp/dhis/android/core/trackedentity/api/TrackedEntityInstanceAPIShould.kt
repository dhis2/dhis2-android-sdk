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
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.EnrollmentInternalAccessor
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.imports.ImportStatus
import org.hisp.dhis.android.core.imports.internal.TEIWebResponse
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceFields
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstancePayload
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceService
import org.junit.Assert
import org.junit.Before
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
abstract class TrackedEntityInstanceAPIShould internal constructor(
    // API version dependant parameters
    private val serverUrl: String,
    private val strategy: String,
) : BaseRealIntegrationTest() {
    private lateinit var executor: CoroutineAPICallExecutor
    private lateinit var trackedEntityInstanceService: TrackedEntityInstanceService

    @Before
    override fun setUp() {
        super.setUp()
        executor = d2.coroutineAPICallExecutor()
        trackedEntityInstanceService = TrackedEntityInstanceService(d2.httpServiceClient())
    }

    // @Test
    @Throws(Exception::class)
    fun tei_with_invalid_tracked_entity_attribute() = runTest {
        login()
        val validTEI = TrackedEntityInstanceUtils.createValidTrackedEntityInstance()
        val invalidTEI = TrackedEntityInstanceUtils.createTrackedEntityInstanceWithInvalidAttribute()
        val payload = TrackedEntityInstancePayload.create(listOf(validTEI, invalidTEI))
        val response = executePostCall(payload)

        assertThat(response.response()!!.status()).isEqualTo(ImportStatus.ERROR)

        for (importSummary in response.response()!!.importSummaries()!!) {
            if (validTEI.uid() == importSummary.reference()) {
                TrackedEntityInstanceUtils.assertTei(importSummary, ImportStatus.SUCCESS)
            } else if (invalidTEI.uid() == importSummary.reference()) {
                TrackedEntityInstanceUtils.assertTei(importSummary, ImportStatus.ERROR)
            }
        }

        // Check server status
        val serverValidTEI = getTrackedEntity(validTEI.uid())
        try {
            getTrackedEntity(invalidTEI.uid())
            Assert.fail("Should not reach that line")
        } catch (e: D2Error) {
            assertThat(e.httpErrorCode()).isEqualTo(404)
        }
        assertThat(serverValidTEI.items().size).isEqualTo(1)
    }

    // @Test
    @Throws(Exception::class)
    fun tei_with_invalid_orgunit() = runTest {
        login()
        val validTEI = TrackedEntityInstanceUtils.createValidTrackedEntityInstance()
        val invalidTEI = TrackedEntityInstanceUtils.createTrackedEntityInstanceWithInvalidOrgunit()
        val payload = TrackedEntityInstancePayload.create(listOf(validTEI, invalidTEI))
        val response = executePostCall(payload)

        assertThat(response.response()!!.status()).isEqualTo(ImportStatus.ERROR)

        for (importSummary in response.response()!!.importSummaries()!!) {
            if (validTEI.uid() == importSummary.reference()) {
                TrackedEntityInstanceUtils.assertTei(importSummary, ImportStatus.SUCCESS)
            } else if (invalidTEI.uid() == importSummary.reference()) {
                TrackedEntityInstanceUtils.assertTei(importSummary, ImportStatus.ERROR)
            }
        }

        // Check server status
        val serverValidTEI = getTrackedEntity(validTEI.uid())
        try {
            getTrackedEntity(invalidTEI.uid())
            Assert.fail("Should not reach that line")
        } catch (e: D2Error) {
            assertThat(e.httpErrorCode()).isEqualTo(404)
        }
        assertThat(serverValidTEI.items().size).isEqualTo(1)
    }

    // @Test
    @Throws(Exception::class)
    fun enrollment_with_valid_values() = runTest {
        login()
        val validTEI = TrackedEntityInstanceUtils.createValidTrackedEntityInstance()
        val invalidTEI = TrackedEntityInstanceUtils.createValidTrackedEntityInstanceAndEnrollment()
        val payload = TrackedEntityInstancePayload.create(listOf(validTEI, invalidTEI))
        val response = executePostCall(payload)
        assertThat(response.response()!!.status()).isEqualTo(ImportStatus.SUCCESS)
        for (importSummary in response.response()!!.importSummaries()!!) {
            if (validTEI.uid() == importSummary.reference()) {
                TrackedEntityInstanceUtils.assertTei(importSummary, ImportStatus.SUCCESS)
            } else if (invalidTEI.uid() == importSummary.reference()) {
                TrackedEntityInstanceUtils.assertTei(importSummary, ImportStatus.SUCCESS)
            }
        }

        // TODO Check server status
        val serverValidTEI = getTrackedEntity(validTEI.uid())
        val serverInvalidTEI = getTrackedEntity(invalidTEI.uid())
    }

    // @Test
    @Throws(Exception::class)
    fun enrollment_future_date() = runTest {
        login()
        val validTEI = TrackedEntityInstanceUtils.createValidTrackedEntityInstanceAndEnrollment()
        val invalidTEI = TrackedEntityInstanceUtils.createValidTrackedEntityInstanceWithFutureEnrollment()
        val payload = TrackedEntityInstancePayload.create(listOf(validTEI, invalidTEI))
        val response = executePostCall(payload)
        assertThat(response.response()!!.status()).isEqualTo(ImportStatus.SUCCESS)
        for (importSummary in response.response()!!.importSummaries()!!) {
            if (validTEI.uid() == importSummary.reference()) {
                TrackedEntityInstanceUtils.assertTei(importSummary, ImportStatus.SUCCESS)
                TrackedEntityInstanceUtils.assertEnrollments(importSummary, ImportStatus.SUCCESS)
            } else if (invalidTEI.uid() == importSummary.reference()) {
                TrackedEntityInstanceUtils.assertTei(importSummary, ImportStatus.SUCCESS)
                TrackedEntityInstanceUtils.assertEnrollments(importSummary, ImportStatus.ERROR)
            }
        }
        val serverValidTEI = getTrackedEntity(validTEI.uid())
        val serverInvalidTEI = getTrackedEntity(invalidTEI.uid())
        assertThat(getEnrollments(serverValidTEI.items()[0])).isNotEmpty()
        assertThat(getEnrollments(serverInvalidTEI.items()[0])).isEmpty()
    }

    // @Test
    @Throws(Exception::class)
    fun already_active_enrollment() = runTest {
        login()
        val validTEI = TrackedEntityInstanceUtils.createValidTrackedEntityInstanceAndEnrollment()
        val invalidTEI = TrackedEntityInstanceUtils.createTrackedEntityInstanceAndTwoActiveEnrollment()
        val payload = TrackedEntityInstancePayload.create(listOf(validTEI, invalidTEI))
        val response = executePostCall(payload)
        assertThat(response.response()!!.status()).isEqualTo(ImportStatus.SUCCESS)
        for (importSummary in response.response()!!.importSummaries()!!) {
            if (validTEI.uid() == importSummary.reference()) {
                TrackedEntityInstanceUtils.assertTei(importSummary, ImportStatus.SUCCESS)
                TrackedEntityInstanceUtils.assertEnrollments(importSummary, ImportStatus.SUCCESS)
            } else if (invalidTEI.uid() == importSummary.reference()) {
                TrackedEntityInstanceUtils.assertTei(importSummary, ImportStatus.SUCCESS)
                TrackedEntityInstanceUtils.assertEnrollments(importSummary, ImportStatus.ERROR)
                assertThat(importSummary.enrollments()!!.imported()).isEqualTo(1)
                assertThat(importSummary.enrollments()!!.ignored()).isEqualTo(1)
            }
        }
        val serverValidTEI = getTrackedEntity(validTEI.uid())
        val serverInvalidTEI = getTrackedEntity(invalidTEI.uid())
        assertThat(getEnrollments(serverValidTEI.items()[0]).size).isEqualTo(1)
        assertThat(getEnrollments(serverInvalidTEI.items()[0]).size).isEqualTo(1)
    }

    // @Test
    @Throws(Exception::class)
    fun event_with_valid_values() = runTest {
        login()
        val validTEI1 = TrackedEntityInstanceUtils.createValidTrackedEntityInstanceAndEnrollment()
        val validTEI2 = TrackedEntityInstanceUtils.createValidTrackedEntityInstanceWithEnrollmentAndEvent()
        val payload = TrackedEntityInstancePayload.create(listOf(validTEI1, validTEI2))
        val response = executePostCall(payload)
        assertThat(response.response()!!.status()).isEqualTo(ImportStatus.SUCCESS)
        for (importSummary in response.response()!!.importSummaries()!!) {
            if (validTEI1.uid() == importSummary.reference()) {
                TrackedEntityInstanceUtils.assertTei(importSummary, ImportStatus.SUCCESS)
                TrackedEntityInstanceUtils.assertEnrollments(importSummary, ImportStatus.SUCCESS)
            } else if (validTEI2.uid() == importSummary.reference()) {
                TrackedEntityInstanceUtils.assertTei(importSummary, ImportStatus.SUCCESS)
                TrackedEntityInstanceUtils.assertEnrollments(importSummary, ImportStatus.SUCCESS)
                TrackedEntityInstanceUtils.assertEvents(importSummary, ImportStatus.SUCCESS)
            }
        }
        val serverValidTEI1 = getTrackedEntity(validTEI1.uid())
        val serverValidTEI2 = getTrackedEntity(validTEI2.uid())
        assertThat(getEnrollments(serverValidTEI1.items()[0]).size).isEqualTo(1)
        assertThat(getEnrollments(serverValidTEI2.items()[0]).size).isEqualTo(1)
        assertThat(getEvents(getEnrollments(serverValidTEI2.items()[0])[0]).size).isEqualTo(1)
    }

    // IMPORTANT: check the programStage is set to "NO WRITE ACCESS" before running the test
    // @Test
    @Throws(Exception::class)
    fun event_with_no_write_access() = runTest {
        login()
        val validTEI1 = TrackedEntityInstanceUtils.createValidTrackedEntityInstanceAndEnrollment()
        val validTEI2 = TrackedEntityInstanceUtils.createValidTrackedEntityInstanceWithEnrollmentAndEvent()
        val payload = TrackedEntityInstancePayload.create(listOf(validTEI1, validTEI2))
        val response = executePostCall(payload)
        assertThat(response.response()!!.status()).isEqualTo(ImportStatus.SUCCESS)
        for (importSummary in response.response()!!.importSummaries()!!) {
            if (validTEI1.uid() == importSummary.reference()) {
                TrackedEntityInstanceUtils.assertTei(importSummary, ImportStatus.SUCCESS)
                TrackedEntityInstanceUtils.assertEnrollments(importSummary, ImportStatus.SUCCESS)
            } else if (validTEI2.uid() == importSummary.reference()) {
                TrackedEntityInstanceUtils.assertTei(importSummary, ImportStatus.SUCCESS)
                TrackedEntityInstanceUtils.assertEnrollments(importSummary, ImportStatus.SUCCESS)
                TrackedEntityInstanceUtils.assertEvents(importSummary, ImportStatus.ERROR)
            }
        }
        val serverValidTEI1 = getTrackedEntity(validTEI1.uid())
        val serverValidTEI2 = getTrackedEntity(validTEI2.uid())
        assertThat(getEnrollments(serverValidTEI1.items()[0]).size).isEqualTo(1)
        assertThat(getEvents(getEnrollments(serverValidTEI1.items()[0])[0])).isEmpty()
        assertThat(getEnrollments(serverValidTEI2.items()[0]).size).isEqualTo(1)
        assertThat(getEvents(getEnrollments(serverValidTEI2.items()[0])[0])).isEmpty()
    }

    // @Test
    @Throws(Exception::class)
    fun event_with_future_event_date_does_not_fail() = runTest {
        login()
        val validTEI = TrackedEntityInstanceUtils.createValidTrackedEntityInstanceWithEnrollmentAndEvent()
        val invalidTEI = TrackedEntityInstanceUtils.createTrackedEntityInstanceWithEnrollmentAndFutureEvent()
        val payload = TrackedEntityInstancePayload.create(listOf(validTEI, invalidTEI))
        val response = executePostCall(payload)
        assertThat(response.response()!!.status()).isEqualTo(ImportStatus.SUCCESS)
        for (importSummary in response.response()!!.importSummaries()!!) {
            if (validTEI.uid() == importSummary.reference()) {
                TrackedEntityInstanceUtils.assertTei(importSummary, ImportStatus.SUCCESS)
                TrackedEntityInstanceUtils.assertEnrollments(importSummary, ImportStatus.SUCCESS)
                TrackedEntityInstanceUtils.assertEvents(importSummary, ImportStatus.SUCCESS)
            } else if (invalidTEI.uid() == importSummary.reference()) {
                TrackedEntityInstanceUtils.assertTei(importSummary, ImportStatus.SUCCESS)
                TrackedEntityInstanceUtils.assertEnrollments(importSummary, ImportStatus.SUCCESS)
                TrackedEntityInstanceUtils.assertEvents(importSummary, ImportStatus.SUCCESS)
            }
        }
        val serverValidTEI1 = getTrackedEntity(validTEI.uid())
        val serverValidTEI2 = getTrackedEntity(invalidTEI.uid())
        assertThat(getEnrollments(serverValidTEI1.items()[0]).size).isEqualTo(1)
        assertThat(getEvents(getEnrollments(serverValidTEI1.items()[0])[0]).size).isEqualTo(1)
        assertThat(getEnrollments(serverValidTEI2.items()[0]).size).isEqualTo(1)
        assertThat(getEvents(getEnrollments(serverValidTEI2.items()[0])[0]).size).isEqualTo(1)
    }

    // @Test
    @Throws(Exception::class)
    fun event_with_invalid_data_element() = runTest {
        login()
        val validTEI = TrackedEntityInstanceUtils.createValidTrackedEntityInstanceWithEnrollmentAndEvent()
        val invalidTEI = TrackedEntityInstanceUtils.createTrackedEntityInstanceWithInvalidDataElement()
        val payload = TrackedEntityInstancePayload.create(listOf(validTEI, invalidTEI))
        val response = executePostCall(payload)
        assertThat(response.response()!!.status()).isEqualTo(ImportStatus.SUCCESS)
        for (importSummary in response.response()!!.importSummaries()!!) {
            if (validTEI.uid() == importSummary.reference()) {
                TrackedEntityInstanceUtils.assertTei(importSummary, ImportStatus.SUCCESS)
                TrackedEntityInstanceUtils.assertEnrollments(importSummary, ImportStatus.SUCCESS)
                TrackedEntityInstanceUtils.assertEvents(importSummary, ImportStatus.SUCCESS)
            } else if (invalidTEI.uid() == importSummary.reference()) {
                TrackedEntityInstanceUtils.assertTei(importSummary, ImportStatus.SUCCESS)
                TrackedEntityInstanceUtils.assertEnrollments(importSummary, ImportStatus.SUCCESS)
                TrackedEntityInstanceUtils.assertEvents(importSummary, ImportStatus.WARNING)
            }
        }
        val serverValidTEI1 = getTrackedEntity(validTEI.uid())
        val serverValidTEI2 = getTrackedEntity(invalidTEI.uid())
        assertThat(getEnrollments(serverValidTEI1.items()[0]).size).isEqualTo(1)
        assertThat(getEvents(getEnrollments(serverValidTEI1.items()[0])[0]).size).isEqualTo(1)
        assertThat(
            getEvents(getEnrollments(serverValidTEI1.items()[0])[0])[0]!!
                .trackedEntityDataValues()!!.size,
        ).isEqualTo(1)
        assertThat(getEnrollments(serverValidTEI2.items()[0]).size).isEqualTo(1)
        assertThat(getEvents(getEnrollments(serverValidTEI2.items()[0])[0]).size).isEqualTo(1)
        assertThat(
            getEvents(getEnrollments(serverValidTEI2.items()[0])[0])[0]!!
                .trackedEntityDataValues(),
        ).isEmpty()
    }

    // @Test
    @Throws(Exception::class)
    fun event_with_valid_and_invalid_data_value() = runTest {
        login()
        val validTEI = TrackedEntityInstanceUtils.createValidTrackedEntityInstanceWithEnrollmentAndEvent()
        val invalidTEI = TrackedEntityInstanceUtils.createTrackedEntityInstanceWithValidAndInvalidDataValue()
        val payload = TrackedEntityInstancePayload.create(listOf(validTEI, invalidTEI))
        val response = executePostCall(payload)
        assertThat(response.response()!!.status()).isEqualTo(ImportStatus.SUCCESS)
        for (importSummary in response.response()!!.importSummaries()!!) {
            if (validTEI.uid() == importSummary.reference()) {
                TrackedEntityInstanceUtils.assertTei(importSummary, ImportStatus.SUCCESS)
                TrackedEntityInstanceUtils.assertEnrollments(importSummary, ImportStatus.SUCCESS)
                TrackedEntityInstanceUtils.assertEvents(importSummary, ImportStatus.SUCCESS)
            } else if (invalidTEI.uid() == importSummary.reference()) {
                TrackedEntityInstanceUtils.assertTei(importSummary, ImportStatus.SUCCESS)
                TrackedEntityInstanceUtils.assertEnrollments(importSummary, ImportStatus.SUCCESS)
                TrackedEntityInstanceUtils.assertEvents(importSummary, ImportStatus.WARNING)
            }
        }
        val serverValidTEI1 = getTrackedEntity(validTEI.uid())
        val serverValidTEI2 = getTrackedEntity(invalidTEI.uid())
        assertThat(getEnrollments(serverValidTEI1.items()[0]).size).isEqualTo(1)
        assertThat(getEvents(getEnrollments(serverValidTEI1.items()[0])[0]).size).isEqualTo(1)
        assertThat(
            getEvents(getEnrollments(serverValidTEI1.items()[0])[0])[0]!!
                .trackedEntityDataValues()!!.size,
        ).isEqualTo(1)
        assertThat(getEnrollments(serverValidTEI2.items()[0]).size).isEqualTo(1)
        assertThat(getEvents(getEnrollments(serverValidTEI2.items()[0])[0]).size).isEqualTo(1)
        assertThat(
            getEvents(getEnrollments(serverValidTEI2.items()[0])[0])[0]!!
                .trackedEntityDataValues()!!.size,
        ).isEqualTo(1)
    }

    // This test is failing
    // @Test
    @Throws(Exception::class)
    fun event_in_completed_enrollment() = runTest {
        login()
        val completedEnrollment =
            TrackedEntityInstanceUtils.createTrackedEntityInstanceWithCompletedEnrollmentAndEvent()
        val payload = TrackedEntityInstancePayload.create(listOf(completedEnrollment))
        val response = executePostCall(payload)
        assertThat(response.response()!!.status()).isEqualTo(ImportStatus.SUCCESS)
        for (importSummary in response.response()!!.importSummaries()!!) {
            if (completedEnrollment.uid() == importSummary.reference()) {
                TrackedEntityInstanceUtils.assertTei(importSummary, ImportStatus.SUCCESS)
                TrackedEntityInstanceUtils.assertEnrollments(importSummary, ImportStatus.SUCCESS)
                TrackedEntityInstanceUtils.assertEvents(importSummary, ImportStatus.SUCCESS)
            }
        }
        val serverValidTEI1 = getTrackedEntity(completedEnrollment.uid())
        assertThat(getEnrollments(serverValidTEI1.items()[0]).size).isEqualTo(1)
        assertThat(
            getEnrollments(serverValidTEI1.items()[0])[0]!!
                .status(),
        ).isEqualTo(EnrollmentStatus.COMPLETED)
        assertThat(getEvents(getEnrollments(serverValidTEI1.items()[0])[0]).size).isEqualTo(1)
        assertThat(
            getEvents(getEnrollments(serverValidTEI1.items()[0])[0])[0]!!
                .trackedEntityDataValues()!!.size,
        ).isEqualTo(1)
        assertThat(
            getEvents(
                getEnrollments(serverValidTEI1.items()[0])[0],
            )[0]!!.status(),
        ).isEqualTo(EventStatus.COMPLETED)
    }

    // @Test
    @Throws(D2Error::class)
    fun tracked_entity_deletion_returns_deleted_equals_1() = runTest {
        login()
        syncMetadata()
        d2.trackedEntityModule().trackedEntityInstanceDownloader().limit(100).blockingDownload()
        val instance = instanceWithOneEnrollmentAndOneEvent
        val deletedEvents = setEventsToDelete(instance)
        val deletedEventsPayload = TrackedEntityInstancePayload.create(listOf(deletedEvents))
        val deletedEventsResponse = executePostCall(deletedEventsPayload)
        assertThat(deletedEventsResponse.response()!!.status()).isEqualTo(ImportStatus.SUCCESS)
        for (teiImportSummaries in deletedEventsResponse.response()!!.importSummaries()!!) {
            assertThat(teiImportSummaries.importCount().updated()).isEqualTo(1)
            for (
            enrollmentImportSummary in teiImportSummaries.enrollments()!!
                .importSummaries()!!
            ) {
                assertThat(enrollmentImportSummary.importCount().updated()).isEqualTo(1)
                for (eventImportSummary in enrollmentImportSummary.events()!!.importSummaries()!!) {
                    assertThat(eventImportSummary.importCount().deleted()).isEqualTo(1)
                }
            }
        }
    }

    private fun login() {
        d2.userModule().logIn(username, password, serverUrl).blockingGet()
    }

    private fun syncMetadata() {
        d2.metadataModule().blockingDownload()
    }

    @Throws(D2Error::class)
    private suspend fun executePostCall(payload: TrackedEntityInstancePayload): TEIWebResponse {
        return executor.wrap(
            storeError = false,
            acceptedErrorCodes = listOf(409),
            errorClass = TEIWebResponse::class.java,
        ) {
            trackedEntityInstanceService.postTrackedEntityInstances(payload, strategy)
        }.getOrThrow()
    }

    private suspend fun getTrackedEntity(teiUid: String): Payload<TrackedEntityInstance> {
        return executor.wrap {
            trackedEntityInstanceService
                .getTrackedEntityInstance(
                    teiUid,
                    OrganisationUnitMode.ACCESSIBLE.name,
                    TrackedEntityInstanceFields.allFields,
                    includeAllAttributes = true,
                    includeDeleted = true,
                )
        }.getOrThrow()
    }

    private val instanceWithOneEnrollmentAndOneEvent: TrackedEntityInstance
        private get() {
            val instances = d2.trackedEntityModule().trackedEntityInstances().blockingGet()
            for (instance in instances) {
                val enrollments = d2.enrollmentModule().enrollments()
                    .byTrackedEntityInstance().eq(instance.uid())
                    .blockingGet()
                if (enrollments != null && enrollments.size == 1) {
                    val enrollment = enrollments[0]
                    val events = d2.eventModule().events().byEnrollmentUid().eq(enrollment.uid()).blockingGet()
                    if (events.size == 1) {
                        val enrollmentWithEvents = EnrollmentInternalAccessor
                            .insertEvents(enrollment.toBuilder(), events).build()
                        return TrackedEntityInstanceInternalAccessor
                            .insertEnrollments(instance.toBuilder(), listOf(enrollmentWithEvents))
                            .build()
                    }
                }
            }
            throw RuntimeException("TEI not found")
        }

    private fun setEventsToDelete(instance: TrackedEntityInstance): TrackedEntityInstance {
        val enrollments: MutableList<Enrollment> = ArrayList()
        for (enrollment in getEnrollments(instance)) {
            val events: MutableList<Event> = ArrayList()
            for (event in getEvents(enrollment)) {
                events.add(event!!.toBuilder().deleted(true).build())
            }
            enrollments.add(EnrollmentInternalAccessor.insertEvents(enrollment!!.toBuilder(), events).build())
        }
        return TrackedEntityInstanceInternalAccessor
            .insertEnrollments(instance.toBuilder(), enrollments)
            .build()
    }

    private fun getEnrollments(trackedEntityInstance: TrackedEntityInstance): List<Enrollment?> {
        return TrackedEntityInstanceInternalAccessor.accessEnrollments(trackedEntityInstance)
    }

    private fun getEvents(enrollment: Enrollment?): List<Event?> {
        return EnrollmentInternalAccessor.accessEvents(enrollment)
    }
}
