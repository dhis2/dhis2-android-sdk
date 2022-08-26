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
package org.hisp.dhis.android.core.trackedentity.search

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.*
import java.text.ParseException
import java.util.*
import java.util.concurrent.Callable
import javax.net.ssl.HttpsURLConnection
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor
import org.hisp.dhis.android.core.common.AssignedUserMode
import org.hisp.dhis.android.core.common.BaseCallShould
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceService
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.stubbing.OngoingStubbing
import retrofit2.Call

@RunWith(JUnit4::class)
class TrackedEntityInstanceQueryCallShould : BaseCallShould() {

    private val service: TrackedEntityInstanceService = mock()
    private val apiCallExecutor: APICallExecutor = mock()
    private val mapper: SearchGridMapper = mock()
    private val dhisVersionManager: DHISVersionManager = mock()
    private val searchGrid: SearchGrid = mock()
    private val searchGridCall: Call<SearchGrid> = mock()
    private val teis: List<TrackedEntityInstance> = mock()
    private val attribute: List<String> = mock()
    private val filter: List<String> = mock()

    private lateinit var query: TrackedEntityInstanceQueryOnline

    // object to test
    private lateinit var call: Callable<List<TrackedEntityInstance>>

    @Before
    override fun setUp() {
        super.setUp()
        val orgUnits = listOf("ou1", "ou2")

        query =
            TrackedEntityInstanceQueryOnline.builder()
                .uids(listOf("uid1", "uid2"))
                .orgUnits(orgUnits)
                .orgUnitMode(OrganisationUnitMode.ACCESSIBLE)
                .program("program")
                .programStage("progra_stage")
                .programStartDate(Date())
                .programEndDate(Date())
                .enrollmentStatus(EnrollmentStatus.ACTIVE)
                .followUp(true)
                .eventStartDate(Date())
                .eventEndDate(Date())
                .eventStatus(EventStatus.OVERDUE)
                .incidentStartDate(Date())
                .incidentEndDate(Date())
                .trackedEntityType("teiTypeStr")
                .query("queryStr")
                .attribute(attribute)
                .filter(filter)
                .includeDeleted(false)
                .lastUpdatedStartDate(Date())
                .lastUpdatedEndDate(Date())
                .order("lastupdated:desc")
                .assignedUserMode(AssignedUserMode.ANY)
                .paging(false)
                .page(2)
                .pageSize(33)
                .build()

        whenServiceQuery().thenReturn(searchGridCall)
        whenever(apiCallExecutor.executeObjectCallWithErrorCatcher(eq(searchGridCall), any())).doReturn(searchGrid)
        whenever(mapper.transform(any())).doReturn(teis)
        whenever(dhisVersionManager.isGreaterThan(DHISVersion.V2_33)).doReturn(true)

        // Metadata call
        call =
            TrackedEntityInstanceQueryCallFactory(service, mapper, apiCallExecutor, dhisVersionManager).getCall(query)
    }

    @Test
    fun succeed_when_endpoint_calls_succeed() {
        val teisResponse = call.call()
        assertThat(teisResponse).isEqualTo(teis)
    }

    @Test
    fun call_mapper_with_search_grid() {
        call.call()
        verify(mapper).transform(searchGrid)
        verifyNoMoreInteractions(mapper)
    }

    @Test
    fun call_service_with_query_parameters() {
        call.call()
        verifyService(query)
        verifyNoMoreInteractions(service)
    }

    @Test
    fun throw_D2CallException_when_service_call_returns_failed_response() {
        whenever(apiCallExecutor.executeObjectCallWithErrorCatcher(eq(searchGridCall), any())).doThrow(d2Error)
        whenever(d2Error.errorCode()).doReturn(D2ErrorCode.MAX_TEI_COUNT_REACHED)

        try {
            call.call()
            fail("D2Error was expected but was not thrown")
        } catch (d2e: D2Error) {
            assertThat(d2e.errorCode() == D2ErrorCode.MAX_TEI_COUNT_REACHED).isTrue()
        }
    }

    @Test
    fun throw_too_many_org_units_exception_when_request_was_too_long() {
        whenever(apiCallExecutor.executeObjectCallWithErrorCatcher(eq(searchGridCall), any())).doThrow(d2Error)
        whenever(d2Error.errorCode()).doReturn(D2ErrorCode.TOO_MANY_ORG_UNITS)
        whenever(d2Error.httpErrorCode()).doReturn(HttpsURLConnection.HTTP_REQ_TOO_LONG)

        try {
            call.call()
            fail("D2Error was expected but was not thrown")
        } catch (d2e: D2Error) {
            assertThat(d2e.errorCode() == D2ErrorCode.TOO_MANY_ORG_UNITS).isTrue()
        }
    }

    @Test(expected = D2Error::class)
    fun throw_D2CallException_when_mapper_throws_exception() {
        whenever(mapper.transform(searchGrid)).thenThrow(ParseException::class.java)
        call.call()
    }

    @Test
    fun should_not_map_active_event_status_if_greater_than_2_33() {
        whenever(dhisVersionManager.isGreaterThan(DHISVersion.V2_33)).doReturn(true)
        val activeQuery = query.toBuilder().eventStatus(EventStatus.ACTIVE).build()
        val activeCall = TrackedEntityInstanceQueryCallFactory(
            service, mapper, apiCallExecutor, dhisVersionManager
        ).getCall(activeQuery)

        activeCall.call()

        verifyService(activeQuery, EventStatus.ACTIVE)
    }

    @Test
    fun should_map_active_event_status_if_not_greater_than_2_33() {
        whenever(dhisVersionManager.isGreaterThan(DHISVersion.V2_33)).doReturn(false)

        val activeQuery = query.toBuilder().eventStatus(EventStatus.ACTIVE).build()
        val activeCall = TrackedEntityInstanceQueryCallFactory(
            service, mapper, apiCallExecutor, dhisVersionManager
        ).getCall(activeQuery)

        activeCall.call()

        verifyService(activeQuery, EventStatus.VISITED)

        val nonActiveQuery = query.toBuilder().eventStatus(EventStatus.SCHEDULE).build()
        val nonActiveCall = TrackedEntityInstanceQueryCallFactory(
            service, mapper, apiCallExecutor, dhisVersionManager
        ).getCall(nonActiveQuery)

        nonActiveCall.call()

        verifyService(activeQuery, EventStatus.SCHEDULE)
    }

    private fun verifyService(
        query: TrackedEntityInstanceQueryOnline,
        expectedStatus: EventStatus? = query.eventStatus()
    ) {
        Mockito.verify(service).query(
            eq(query.uids()!![0] + ";" + query.uids()!![1]),
            eq(query.orgUnits()[0] + ";" + query.orgUnits()[1]),
            eq(query.orgUnitMode().toString()),
            eq(query.program()),
            eq(query.programStage()),
            eq(query.formattedProgramStartDate()),
            eq(query.formattedProgramEndDate()),
            eq(query.enrollmentStatus().toString()),
            eq(query.formattedIncidentStartDate()),
            eq(query.formattedIncidentEndDate()),
            eq(query.followUp()),
            eq(query.formattedEventStartDate()),
            eq(query.formattedEventEndDate()),
            eq(expectedStatus.toString()),
            eq(query.trackedEntityType()),
            eq(query.query()),
            eq(query.attribute()),
            eq(query.filter()),
            eq(query.assignedUserMode().toString()),
            eq(query.formattedLastUpdatedStartDate()),
            eq(query.formattedLastUpdatedEndDate()),
            eq(query.order()),
            eq(query.paging()),
            eq(query.page()),
            eq(query.pageSize())
        )
    }

    private fun whenServiceQuery(): OngoingStubbing<Call<SearchGrid>?> {
        return whenever(
            service.query(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        )
    }
}
