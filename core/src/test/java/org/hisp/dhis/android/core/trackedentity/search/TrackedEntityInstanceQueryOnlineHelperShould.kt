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
import org.hisp.dhis.android.core.arch.repositories.scope.internal.FilterItemOperator
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeFilterItem
import org.hisp.dhis.android.core.common.DateFilterPeriodHelper
import org.hisp.dhis.android.core.common.FilterOperatorsHelper.listToStr
import org.hisp.dhis.android.core.period.internal.CalendarProviderFactory.calendarProvider
import org.hisp.dhis.android.core.period.internal.ParentPeriodGeneratorImpl.Companion.create
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class TrackedEntityInstanceQueryOnlineHelperShould {
    private lateinit var queryBuilder: TrackedEntityInstanceQueryRepositoryScope.Builder
    private lateinit var onlineHelper: TrackedEntityInstanceQueryOnlineHelper

    @Before
    fun setUp() {
        queryBuilder = TrackedEntityInstanceQueryRepositoryScope.builder()
            .orgUnits(listOf("uid"))
        val periodHelper = DateFilterPeriodHelper(calendarProvider, create(calendarProvider))
        onlineHelper = TrackedEntityInstanceQueryOnlineHelper(periodHelper)
    }

    @Test
    fun parse_query_in_api_format() {
        val scope = queryBuilder
            .query(
                RepositoryScopeFilterItem.builder().key("").operator(FilterItemOperator.LIKE).value("filter").build()
            )
            .build()
        val onlineQueries = onlineHelper.fromScope(scope)

        assertThat(onlineQueries.size).isEqualTo(1)
        assertThat(onlineQueries[0].query()).isEqualTo("LIKE:filter")
    }

    @Test
    fun parse_attributes_in_api_format() {
        val scope = queryBuilder
            .attribute(
                listOf(
                    RepositoryScopeFilterItem.builder()
                        .key("attribute1").operator(FilterItemOperator.EQ).value("filter1").build(),
                    RepositoryScopeFilterItem.builder()
                        .key("attribute2").operator(FilterItemOperator.EQ).value("filter21").build(),
                    RepositoryScopeFilterItem.builder()
                        .key("attribute3").operator(FilterItemOperator.LIKE).value("filter31").build(),
                    RepositoryScopeFilterItem.builder()
                        .key("attribute2").operator(FilterItemOperator.LIKE).value("filter22").build()
                )
            ).build()

        val onlineQueries = onlineHelper.fromScope(scope)

        assertThat(onlineQueries.size).isEqualTo(1)
        assertThat(onlineQueries[0].attribute()!!.size).isEqualTo(3)
        assertThat(onlineQueries[0].attribute()).contains("attribute1:EQ:filter1")
        assertThat(onlineQueries[0].attribute()).contains("attribute2:EQ:filter21:LIKE:filter22")
        assertThat(onlineQueries[0].attribute()).contains("attribute3:LIKE:filter31")
    }

    @Test
    fun parse_filters_in_api_format() {
        val scope = queryBuilder
            .filter(
                listOf(
                    RepositoryScopeFilterItem.builder()
                        .key("filterItem1").operator(FilterItemOperator.EQ).value("filter1").build(),
                    RepositoryScopeFilterItem.builder()
                        .key("filterItem2").operator(FilterItemOperator.LIKE).value("filter21").build(),
                    RepositoryScopeFilterItem.builder()
                        .key("filterItem3").operator(FilterItemOperator.LIKE).value("filter31").build(),
                    RepositoryScopeFilterItem.builder()
                        .key("filterItem3").operator(FilterItemOperator.EQ).value("filter32").build()
                )
            ).build()

        val onlineQueries = onlineHelper.fromScope(scope)

        assertThat(onlineQueries.size).isEqualTo(1)
        assertThat(onlineQueries[0].filter()!!.size).isEqualTo(3)
        assertThat(onlineQueries[0].filter()).contains("filterItem1:EQ:filter1")
        assertThat(onlineQueries[0].filter()).contains("filterItem2:LIKE:filter21")
        assertThat(onlineQueries[0].filter()).contains("filterItem3:LIKE:filter31:EQ:filter32")
    }

    @Test
    fun parse_filters_using_in_operator() {
        val list = listOf(
            "nom,app",
            "nom-app"
        )

        val scope = queryBuilder
            .filter(
                listOf(
                    RepositoryScopeFilterItem.builder()
                        .key("filterItem1").operator(FilterItemOperator.IN).value(listToStr(list)).build()
                )
            ).build()

        val onlineQueries = onlineHelper.fromScope(scope)

        assertThat(onlineQueries.size).isEqualTo(1)
        assertThat(onlineQueries[0].filter()!!.size).isEqualTo(1)
        assertThat(onlineQueries[0].filter()!![0]).isEqualTo("filterItem1:IN:nom,app;nom-app")
    }
}
