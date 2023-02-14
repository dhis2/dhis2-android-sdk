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
package org.hisp.dhis.android.core.event.internal

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.mock
import java.util.concurrent.Callable
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutorImpl
import org.hisp.dhis.android.core.arch.api.testutils.RetrofitFactory
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.event.Event
import org.hisp.dhis.android.core.mockwebserver.Dhis2MockServer
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode
import org.hisp.dhis.android.core.trackedentity.internal.TrackerQueryCommonParams
import org.hisp.dhis.android.core.trackedentity.internal.TrackerQueryCommonParamsSamples.get
import org.hisp.dhis.android.core.tracker.exporter.TrackerAPIQuery
import org.hisp.dhis.android.core.user.internal.UserAccountDisabledErrorCatcher
import org.junit.AfterClass
import org.junit.BeforeClass
import org.junit.Test
import retrofit2.Retrofit

class EventEndpointCallShould {

    private val orgunit = "orgunitUid"
    private val program = "proramUid"
    private val startDateStr = "2021-01-01"

    private val databaseAdapter: DatabaseAdapter = mock()
    private val userAccountDisabledErrorCatcher: UserAccountDisabledErrorCatcher = mock()

    @Test
    fun realize_request_with_page_filters_when_included_in_query() {
        val eventEndpointCall = givenAEventCallByPagination(2, 32)

        mockWebServer.enqueueMockResponse()

        eventEndpointCall.call()
        val request = mockWebServer.takeRequest()

        assertThat(request.path).contains("paging=true&page=2&pageSize=32")
    }

    @Test
    fun realize_request_with_orgUnit_program_filters_when_included_in_query() {
        val eventEndpointCall = givenAEventCallByOrgUnitAndProgram(orgunit, program)

        mockWebServer.enqueueMockResponse()

        eventEndpointCall.call()
        val request = mockWebServer.takeRequest()

        assertThat(request.path).contains("orgUnit=$orgunit")
        assertThat(request.path).contains("program=$program")
    }

    @Test
    fun include_start_date_if_program_is_defined() {
        val eventEndpointCall = givenAEventCallByOrgUnitAndProgram(orgunit, program, startDateStr)

        mockWebServer.enqueueMockResponse()

        eventEndpointCall.call()
        val request = mockWebServer.takeRequest()

        assertThat(request.path).contains(startDateStr)
    }

    @Test
    fun does_not_include_start_date_if_program_is_not_defined() {
        val eventEndpointCall = givenAEventCallByOrgUnitAndProgram(program, null, startDateStr)

        mockWebServer.enqueueMockResponse()

        eventEndpointCall.call()
        val request = mockWebServer.takeRequest()

        assertThat(request.path).doesNotContain(startDateStr)
    }

    private fun givenAEventCallByPagination(page: Int, pageCount: Int): Callable<List<Event>> {
        val eventQuery = TrackerAPIQuery(
            commonParams = get(),
            page = page,
            pageSize = pageCount,
            paging = true
        )
        return givenACallForQuery(eventQuery)
    }

    private fun givenACallForQuery(eventQuery: TrackerAPIQuery): Callable<List<Event>> {
        return EventEndpointCallFactory(
            retrofit.create(EventService::class.java),
            APICallExecutorImpl.create(databaseAdapter, userAccountDisabledErrorCatcher)
        ).getCall(eventQuery)
    }

    private fun givenAEventCallByOrgUnitAndProgram(
        orgUnit: String,
        program: String?,
        startDate: String? = null
    ): Callable<List<Event>> {
        val eventQuery = TrackerAPIQuery(
            commonParams = TrackerQueryCommonParams(
                listOf(),
                listOfNotNull(program),
                program,
                startDate,
                false,
                OrganisationUnitMode.SELECTED, listOf(orgUnit),
                10
            ),
            orgUnit = orgUnit
        )

        return givenACallForQuery(eventQuery)
    }

    companion object {
        private lateinit var retrofit: Retrofit
        private lateinit var mockWebServer: Dhis2MockServer

        @BeforeClass
        @JvmStatic
        fun setUpClass() {
            mockWebServer = Dhis2MockServer(0)
            retrofit = RetrofitFactory.fromDHIS2MockServer(mockWebServer)
        }

        @AfterClass
        @JvmStatic
        fun tearDownClass() {
            mockWebServer.shutdown()
        }
    }
}
