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
package org.hisp.dhis.android.core.trackedentity.internal

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.common.truth.Truth.assertThat
import java.io.IOException
import junit.framework.Assert.fail
import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.file.ResourcesFileReader
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.enrollment.EnrollmentInternalAccessor
import org.hisp.dhis.android.core.enrollment.EnrollmentTableInfo
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStoreImpl
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.internal.EventStoreImpl
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestMetadataEnqueable
import org.junit.Test

class TrackedEntityInstanceCallMockIntegrationShould : BaseMockIntegrationTestMetadataEnqueable() {

    @Test
    fun download_tracked_entity_instance_enrollments_and_events() {
        val teiUid = "PgmUFEQYZdt"

        dhis2MockServer.enqueueSystemInfoResponse()
        dhis2MockServer.enqueueMockResponse("trackedentity/tracked_entity_instance_collection.json")

        d2.trackedEntityModule().trackedEntityInstanceDownloader().byUid().eq(teiUid).blockingDownload()

        verifyDownloadedTrackedEntityInstanceSingle("trackedentity/tracked_entity_instance.json", teiUid)
    }

    @Test
    fun remove_data_removed_in_server_after_second_download() {
        val teiUid = "PgmUFEQYZdt"
        val program = "lxAQ7Zs9VYR"

        dhis2MockServer.enqueueSystemInfoResponse()
        dhis2MockServer.enqueueMockResponse("trackedentity/tracked_entity_instance_single.json")

        d2.trackedEntityModule().trackedEntityInstanceDownloader()
            .byUid().eq(teiUid)
            .byProgramUid(program)
            .blockingDownload()

        dhis2MockServer.enqueueSystemInfoResponse()
        dhis2MockServer.enqueueMockResponse("trackedentity/tracked_entity_instance_with_removed_data_single.json")

        d2.trackedEntityModule().trackedEntityInstanceDownloader()
            .byUid().eq(teiUid)
            .byProgramUid(program)
            .blockingDownload()

        verifyDownloadedTrackedEntityInstanceSingle(
            "trackedentity/tracked_entity_instance_with_removed_data_single.json",
            teiUid
        )
    }

    @Test
    fun download_glass_protected_tracked_entity_instance() {
        val teiUid = "PgmUFEQYZdt"
        val program = "lxAQ7Zs9VYR"

        dhis2MockServer.enqueueSystemInfoResponse()
        dhis2MockServer.enqueueMockResponse(401, "trackedentity/glass/glass_protected_tei_failure.json")
        try {
            d2.trackedEntityModule().trackedEntityInstanceDownloader()
                .byUid().eq(teiUid)
                .byProgramUid(program)
                .blockingDownload()
            fail("It should throw ownership error")
        } catch (e: RuntimeException) {
            assertThat(e.cause is D2Error).isTrue()
            assertThat((e.cause as D2Error).errorCode()).isEqualTo(D2ErrorCode.OWNERSHIP_ACCESS_DENIED)
        }

        dhis2MockServer.enqueueMockResponse("trackedentity/glass/break_glass_successful.json")
        d2.trackedEntityModule().ownershipManager().blockingBreakGlass(teiUid, program, "Reason")

        dhis2MockServer.enqueueSystemInfoResponse()
        dhis2MockServer.enqueueMockResponse("trackedentity/tracked_entity_instance.json")
        d2.trackedEntityModule().trackedEntityInstanceDownloader()
            .byUid().eq(teiUid)
            .byProgramUid(program)
            .blockingDownload()

        verifyDownloadedTrackedEntityInstance("trackedentity/tracked_entity_instance.json", teiUid)
    }

    private fun verifyDownloadedTrackedEntityInstanceSingle(file: String, teiUid: String) {
        val parsed = parseTrackedEntityInstanceResponse(
            file,
            object : TypeReference<TrackedEntityInstance>() {}
        )
        val expectedEnrollmentResponse = removeDeletedData(parsed)
        val downloadedTei = getDownloadedTei(teiUid)

        assertThat(downloadedTei!!.uid()).isEqualTo(expectedEnrollmentResponse.uid())
        assertThat(downloadedTei.trackedEntityAttributeValues()!!.size)
            .isEqualTo(expectedEnrollmentResponse.trackedEntityAttributeValues()!!.size)
        assertThat(getEnrollments(downloadedTei).size).isEqualTo(getEnrollments(expectedEnrollmentResponse).size)
    }

    @Throws(IOException::class)
    private fun verifyDownloadedTrackedEntityInstance(file: String, teiUid: String) {
        val parsed = parseTrackedEntityInstanceResponse(
            file,
            object : TypeReference<TrackedEntityInstance>() {}
        )
        val expectedEnrollmentResponse = removeDeletedData(parsed)
        val downloadedTei = getDownloadedTei(teiUid)

        assertThat(downloadedTei!!.uid()).isEqualTo(expectedEnrollmentResponse.uid())
        assertThat(downloadedTei.trackedEntityAttributeValues()!!.size)
            .isEqualTo(expectedEnrollmentResponse.trackedEntityAttributeValues()!!.size)
    }

    private fun <M> parseTrackedEntityInstanceResponse(file: String, reference: TypeReference<M>): M {
        val expectedEventsResponseJson = ResourcesFileReader().getStringFromFile(file)
        val objectMapper = ObjectMapper().setDateFormat(DateUtils.DATE_FORMAT.raw())
        return objectMapper.readValue(expectedEventsResponseJson, reference)
    }

    private fun removeDeletedData(trackedEntityInstance: TrackedEntityInstance): TrackedEntityInstance {
        val enrollments = getEnrollments(trackedEntityInstance)
            .filter { it.deleted() != true }
            .map { enrollment ->
                val events = EnrollmentInternalAccessor.accessEvents(enrollment)
                    .filter { it?.deleted() != true }

                EnrollmentInternalAccessor.insertEvents(enrollment.toBuilder(), events)
                    .trackedEntityInstance(trackedEntityInstance.uid())
                    .build()
            }

        return TrackedEntityInstanceInternalAccessor
            .insertEnrollments(trackedEntityInstance.toBuilder(), enrollments)
            .build()
    }

    private fun getDownloadedTei(teiUid: String): TrackedEntityInstance? {
        val teiAttributeValuesStore = TrackedEntityAttributeValueStoreImpl.create(databaseAdapter)
        val attValues = teiAttributeValuesStore.queryByTrackedEntityInstance(teiUid)
        val attValuesWithoutIdAndTEI = attValues.map {
            it.toBuilder().id(null).trackedEntityInstance(null).build()
        }

        val teiStore = TrackedEntityInstanceStoreImpl.create(databaseAdapter)
        val downloadedTei = teiStore.selectByUid(teiUid)
        val enrollmentStore = EnrollmentStoreImpl.create(databaseAdapter)
        val downloadedEnrollments = enrollmentStore.selectWhere(
            WhereClauseBuilder()
                .appendKeyStringValue(EnrollmentTableInfo.Columns.TRACKED_ENTITY_INSTANCE, teiUid).build()
        )
        val downloadedEnrollmentsWithoutIdAndDeleteFalse = downloadedEnrollments.map {
            it.toBuilder().id(null).deleted(false).notes(ArrayList()).build()
        }

        val eventStore = EventStoreImpl.create(databaseAdapter)
        val downloadedEventsWithoutValues = eventStore.selectAll()
        val downloadedEventsWithoutValuesAndDeleteFalse = downloadedEventsWithoutValues.map {
            it.toBuilder().id(null).deleted(false).build()
        }

        val dataValueList = TrackedEntityDataValueStoreImpl.create(databaseAdapter).selectAll()
        val downloadedValues = dataValueList.groupBy { it.event() }

        return createTei(
            downloadedTei, attValuesWithoutIdAndTEI, downloadedEnrollmentsWithoutIdAndDeleteFalse,
            downloadedEventsWithoutValuesAndDeleteFalse, downloadedValues
        )
    }

    private fun createTei(
        downloadedTei: TrackedEntityInstance?,
        attValuesWithoutIdAndTEI: List<TrackedEntityAttributeValue>,
        downloadedEnrollmentsWithoutEvents: List<Enrollment>,
        downloadedEventsWithoutValues: List<Event>,
        downloadedValues: Map<String?, List<TrackedEntityDataValue>?>
    ): TrackedEntityInstance? {
        val downloadedEvents = downloadedEventsWithoutValues.map { event ->
            val trackedEntityDataValuesWithNullIdsAndEvents = downloadedValues[event.uid()]!!.map {
                it.toBuilder().id(null).event(null).build()
            }

            event.toBuilder().trackedEntityDataValues(trackedEntityDataValuesWithNullIdsAndEvents).build()
        }.groupBy { it.enrollment() }

        val downloadedEnrollments = downloadedEnrollmentsWithoutEvents.map { enrollment ->
            EnrollmentInternalAccessor.insertEvents(
                enrollment.toBuilder(),
                downloadedEvents[enrollment.uid()]
            )
                .trackedEntityInstance(downloadedTei!!.uid())
                .build()
        }

        val relationships = TrackedEntityInstanceInternalAccessor.accessRelationships(downloadedTei) ?: ArrayList()

        return TrackedEntityInstanceInternalAccessor.insertEnrollments(
            TrackedEntityInstanceInternalAccessor.insertRelationships(
                downloadedTei!!.toBuilder(), relationships
            ),
            downloadedEnrollments
        )
            .id(null)
            .deleted(false)
            .trackedEntityAttributeValues(attValuesWithoutIdAndTEI)
            .build()
    }

    private fun getEnrollments(trackedEntityInstance: TrackedEntityInstance?): List<Enrollment> {
        return TrackedEntityInstanceInternalAccessor.accessEnrollments(trackedEntityInstance)
    }
}
