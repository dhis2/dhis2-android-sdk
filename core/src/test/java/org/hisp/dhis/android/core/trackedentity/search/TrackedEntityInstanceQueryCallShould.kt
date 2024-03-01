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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.api.executors.internal.CoroutineAPICallExecutorMock
import org.hisp.dhis.android.core.arch.api.payload.internal.Payload
import org.hisp.dhis.android.core.arch.repositories.scope.internal.FilterItemOperator
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem
import org.hisp.dhis.android.core.common.AssignedUserMode
import org.hisp.dhis.android.core.common.BaseCallShould
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.event.EventInternalAccessor
import org.hisp.dhis.android.core.event.EventStatus
import org.hisp.dhis.android.core.event.internal.EventFields
import org.hisp.dhis.android.core.event.internal.EventService
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.systeminfo.DHISVersion
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceService
import org.hisp.dhis.android.core.util.simpleDateFormat
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.invocation.InvocationOnMock
import java.text.ParseException
import java.util.*
import javax.net.ssl.HttpsURLConnection

@RunWith(JUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class TrackedEntityInstanceQueryCallShould : BaseCallShould() {

    private val trackedEntityService: TrackedEntityInstanceService = mock()
    private val eventService: EventService = mock()
    private val mapper: SearchGridMapper = mock()
    private val coroutineAPICallExecutor = CoroutineAPICallExecutorMock()
    private val dhisVersionManager: DHISVersionManager = mock()
    private val searchGrid: SearchGrid = mock()
    private val teis: List<TrackedEntityInstance> = mock()
    private val eventPayload: Payload<Event> = mock()
    private val attribute: List<RepositoryScopeFilterItem> = emptyList()
    private val order: List<TrackedEntityInstanceQueryScopeOrderByItem> =
        listOf(TrackedEntityInstanceQueryScopeOrderByItem.DEFAULT_TRACKER_ORDER)

    private lateinit var query: TrackedEntityInstanceQueryOnline

    // object to test
    private lateinit var callFactory: TrackedEntityInstanceQueryCallFactory

    @Before
    override fun setUp() {
        super.setUp()
        val orgUnits = listOf("ou1", "ou2")

        query = TrackedEntityInstanceQueryOnline(
            uids = listOf("uid1", "uid2"),
            orgUnits = orgUnits,
            orgUnitMode = OrganisationUnitMode.DESCENDANTS,
            program = "program",
            programStartDate = Date(),
            programEndDate = Date(),
            enrollmentStatus = EnrollmentStatus.ACTIVE,
            followUp = true,
            incidentStartDate = Date(),
            incidentEndDate = Date(),
            trackedEntityType = "teiTypeStr",
            attributeFilter = attribute,
            includeDeleted = false,
            lastUpdatedStartDate = Date(),
            lastUpdatedEndDate = Date(),
            order = order,
            assignedUserMode = AssignedUserMode.ANY,
            paging = false,
            page = 2,
            pageSize = 33,
        )

        whenServiceQuery { searchGrid }
        whenEventServiceQuery { eventPayload }

        whenever(mapper.transform(any())).doReturn(teis)
        whenever(dhisVersionManager.isGreaterThan(DHISVersion.V2_33)).doReturn(true)

        // Metadata call
        callFactory = TrackedEntityInstanceQueryCallFactory(
            trackedEntityService,
            eventService,
            mapper,
            coroutineAPICallExecutor,
            dhisVersionManager,
        )
    }

    @Test
    fun succeed_when_endpoint_calls_succeed() = runTest {
        val teisResponse = callFactory.getCall(query)
        assertThat(teisResponse.trackedEntities).isEqualTo(teis)
    }

    @Test
    fun call_mapper_with_search_grid() = runTest {
        callFactory.getCall(query)
        verify(mapper).transform(searchGrid)
        verifyNoMoreInteractions(mapper)
    }

    @Test
    fun call_service_with_query_parameters() = runTest {
        callFactory.getCall(query)
        verifyService(query)
        verifyNoMoreInteractions(trackedEntityService)
    }

    @Test
    fun throw_D2CallException_when_service_call_returns_failed_response() = runTest {
        whenServiceQuery { throw d2Error }
        whenever(d2Error.errorCode()).doReturn(D2ErrorCode.MAX_TEI_COUNT_REACHED)

        try {
            callFactory.getCall(query)
            fail("D2Error was expected but was not thrown")
        } catch (d2e: D2Error) {
            assertThat(d2e.errorCode() == D2ErrorCode.MAX_TEI_COUNT_REACHED).isTrue()
        }
    }

    @Test
    fun throw_too_many_org_units_exception_when_request_was_too_long() = runTest {
        whenServiceQuery { throw d2Error }
        whenever(d2Error.errorCode()).doReturn(D2ErrorCode.TOO_MANY_ORG_UNITS)
        whenever(d2Error.httpErrorCode()).doReturn(HttpsURLConnection.HTTP_REQ_TOO_LONG)

        try {
            callFactory.getCall(query)
            fail("D2Error was expected but was not thrown")
        } catch (d2e: D2Error) {
            assertThat(d2e.errorCode() == D2ErrorCode.TOO_MANY_ORG_UNITS).isTrue()
        }
    }

    @Test(expected = D2Error::class)
    fun throw_D2CallException_when_mapper_throws_exception() = runTest {
        whenever(mapper.transform(searchGrid)).thenThrow(ParseException::class.java)
        callFactory.getCall(query)
    }

    @Test
    fun should_query_events_if_data_value_filter() = runTest {
        val events = listOf(
            EventInternalAccessor.insertTrackedEntityInstance(Event.builder().uid("uid1"), "tei1").build(),
            EventInternalAccessor.insertTrackedEntityInstance(Event.builder().uid("uid2"), "tei2").build(),
        )
        whenever(eventPayload.items()).doReturn(events)

        val query = query.copy(
            dataValueFilter = listOf(
                RepositoryScopeFilterItem.builder()
                    .key("dataElement")
                    .operator(FilterItemOperator.EQ)
                    .value("2")
                    .build(),
            ),
        )

        callFactory.getCall(query)

        val expectedTeiQuery = TrackedEntityInstanceQueryCallFactory.getPostEventTeiQuery(query, events)

        verifyEventService(query)
        verifyService(expectedTeiQuery)
    }

    @Test
    fun should_query_events_for_multiple_orgunits() = runTest {
        whenever(eventPayload.items()).doReturn(emptyList())

        val query = query.copy(
            dataValueFilter = listOf(
                RepositoryScopeFilterItem.builder()
                    .key("dataElement")
                    .operator(FilterItemOperator.EQ)
                    .value("2")
                    .build(),
            ),
            orgUnits = listOf("orgunit1", "orgunit2"),
        )

        callFactory.getCall(query)

        verifyEventService(query)
    }

    private fun verifyService(
        query: TrackedEntityInstanceQueryOnline,
        expectedStatus: EventStatus? = query.eventStatus,
    ) = runBlocking {
        verify(trackedEntityService).query(
            eq(query.uids?.joinToString(";")),
            getExpectedOrgunit(query.orgUnits, query.orgUnitMode),
            eq(query.orgUnitMode.toString()),
            eq(query.program),
            eq(query.programStage),
            eq(query.programStartDate.simpleDateFormat()),
            eq(query.programEndDate.simpleDateFormat()),
            eq(query.enrollmentStatus?.toString()),
            eq(query.incidentStartDate.simpleDateFormat()),
            eq(query.incidentEndDate.simpleDateFormat()),
            eq(query.followUp),
            eq(query.eventStartDate.simpleDateFormat()),
            eq(query.eventEndDate.simpleDateFormat()),
            eq(expectedStatus?.toString()),
            eq(query.trackedEntityType),
            any(),
            eq(query.assignedUserMode?.toString()),
            eq(query.lastUpdatedStartDate.simpleDateFormat()),
            eq(query.lastUpdatedEndDate.simpleDateFormat()),
            any(),
            eq(query.paging),
            eq(query.page.takeIf { query.paging }),
            eq(query.pageSize.takeIf { query.paging }),
        )
    }

    private fun verifyEventService(
        query: TrackedEntityInstanceQueryOnline,
    ) {
        if (query.orgUnits.size <= 1) {
            verifyEventServiceForOrgunit(query, query.orgUnits.firstOrNull())
        } else {
            query.orgUnits.forEach {
                verifyEventServiceForOrgunit(query, it)
            }
        }
    }

    private fun verifyEventServiceForOrgunit(query: TrackedEntityInstanceQueryOnline, orgunit: String?) = runBlocking {
        verify(eventService).getEvents(
            eq(EventFields.teiQueryFields),
            getExpectedOrgunit(orgunit?.let { listOf(it) }, query.orgUnitMode),
            eq(query.orgUnitMode?.toString()),
            eq(query.eventStatus?.toString()),
            eq(query.program),
            eq(query.programStage),
            eq(query.enrollmentStatus?.toString()),
            any(),
            eq(query.followUp),
            eq(query.eventStartDate.simpleDateFormat()),
            eq(query.eventEndDate.simpleDateFormat()),
            eq(query.dueStartDate.simpleDateFormat()),
            eq(query.dueEndDate.simpleDateFormat()),
            any(),
            eq(query.assignedUserMode?.toString()),
            eq(query.paging),
            eq(query.page.takeIf { query.paging }),
            eq(query.pageSize.takeIf { query.paging }),
            eq(query.lastUpdatedStartDate.simpleDateFormat()),
            eq(query.lastUpdatedEndDate.simpleDateFormat()),
            eq(query.includeDeleted),
            eq(null),
        )
    }

    private fun getExpectedOrgunit(orgUnits: List<String>?, mode: OrganisationUnitMode?): String? {
        return if (mode == OrganisationUnitMode.ALL ||
            mode == OrganisationUnitMode.ACCESSIBLE ||
            mode == OrganisationUnitMode.CAPTURE
        ) {
            eq(null)
        } else {
            eq(orgUnits?.joinToString(";"))
        }
    }

    private fun whenServiceQuery(answer: (InvocationOnMock) -> SearchGrid?) {
        trackedEntityService.stub {
            onBlocking {
                query(
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                )
            }.doAnswer(answer)
        }
    }

    private fun whenEventServiceQuery(answer: (InvocationOnMock) -> Payload<Event>) {
        eventService.stub {
            onBlocking {
                getEvents(
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                    anyOrNull(),
                )
            }.doAnswer(answer)
        }
    }
}
