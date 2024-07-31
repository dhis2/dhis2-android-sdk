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
import org.hisp.dhis.android.core.period.clock.internal.ClockProviderFactory.clockProvider
import org.hisp.dhis.android.core.period.internal.CalendarProviderFactory.calendarProvider
import org.hisp.dhis.android.core.period.internal.ParentPeriodGeneratorImpl.Companion.create
import org.hisp.dhis.android.core.trackedentity.search.TrackedEntityInstanceQueryOnlineHelper.Companion.toAPIFilterFormat
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
        val periodHelper = DateFilterPeriodHelper(calendarProvider, create(clockProvider))
        onlineHelper = TrackedEntityInstanceQueryOnlineHelper(periodHelper)
    }

    @Test
    fun parse_filters_using_in_operator() {
        val list = listOf(
            "nom,app",
            "nom-app",
        )

        val scope = queryBuilder
            .filter(
                listOf(
                    RepositoryScopeFilterItem.builder()
                        .key("filterItem1").operator(FilterItemOperator.IN).value(listToStr(list)).build(),
                ),
            ).build()

        val onlineQueries = onlineHelper.fromScope(scope)

        assertThat(onlineQueries.size).isEqualTo(1)
        assertThat(onlineQueries[0].attributeFilter.size).isEqualTo(1)
    }

    @Test
    fun to_API_filter_format() {
        // List of filters
        val list = listOf(
            "nom,app",
            "nom:app",
            "nom;app",
        )

        val expectedList = listOf(
            "filterItemIN:in:nom/,app;nom/:app;nom/;app",
            "filterItemLIKE1:like:nom/,app",
            "filterItemLIKE2:like:nom/:app",
            "filterItemLIKE3:like:nom/;app",
        )

        val scope = queryBuilder
            .filter(
                listOf(
                    RepositoryScopeFilterItem.builder()
                        .key("filterItemIN").operator(FilterItemOperator.IN).value(listToStr(list)).build(),
                    RepositoryScopeFilterItem.builder()
                        .key("filterItemLIKE1").operator(FilterItemOperator.LIKE).value(list[0]).build(),
                    RepositoryScopeFilterItem.builder()
                        .key("filterItemLIKE2").operator(FilterItemOperator.LIKE).value(list[1]).build(),
                    RepositoryScopeFilterItem.builder()
                        .key("filterItemLIKE3").operator(FilterItemOperator.LIKE).value(list[2]).build(),
                ),
            ).build()

        val formattedQueries = toAPIFilterFormat(scope.filter(), false)

        assertThat(formattedQueries).isEqualTo(expectedList)
    }
}
