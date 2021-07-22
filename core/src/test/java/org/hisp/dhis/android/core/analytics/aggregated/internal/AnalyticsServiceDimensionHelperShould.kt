
/*
 *  Copyright (c) 2004-2021, University of Oslo
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

package org.hisp.dhis.android.core.analytics.aggregated.internal

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.hisp.dhis.android.core.analytics.aggregated.AbsoluteDimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.Dimension
import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsServiceHelperSamples as s
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.period.internal.ParentPeriodGenerator
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AnalyticsServiceDimensionHelperShould {

    private val periodGenerator: ParentPeriodGenerator = mock()

    private val organisationUnitHelper: AnalyticsOrganisationUnitHelper = mock()

    private val helper = AnalyticsServiceDimensionHelper(periodGenerator, organisationUnitHelper)

    @Test
    fun `Should extract unique dimension values`() {
        val params = AnalyticsRepositoryParams(
            dimensions = listOf(
                s.dataElementItem1,
                s.dataElementItem2,
                s.periodAbsolute1,
                s.periodAbsolute2,
                s.programIndicatorItem,
                s.indicatorItem,
                s.periodLast3Days,
                s.categoryItem1_1,
                s.orgunitAbsolute,
                s.categoryItem1_2,
                s.categoryItem2_1
            ),
            filters = listOf()
        )

        val dimensionSet = helper.getDimensions(params)

        assertThat(dimensionSet).containsExactly(
            Dimension.Data,
            Dimension.Period,
            Dimension.OrganisationUnit,
            Dimension.Category(s.categoryItem1_1.uid),
            Dimension.Category(s.categoryItem2_1.uid)
        )
    }

    @Test
    fun `Should generate cartesian product based on given dimensions using absolute arguments`() {
        val params = AnalyticsRepositoryParams(
            dimensions = listOf(
                s.dataElementItem1,
                s.periodAbsolute1,
                s.indicatorItem,
                s.periodAbsolute2,
                s.orgunitAbsolute,
                s.categoryItem1_1,
                s.categoryItem1_2
            ),
            filters = listOf()
        )
        val dimensions = listOf(
            Dimension.Period, Dimension.Data, Dimension.OrganisationUnit,
            Dimension.Category(s.categoryItem1_1.uid)
        )

        val items = helper.getEvaluationItems(params, dimensions)

        assertThat(items).containsExactly(
            item(s.periodAbsolute1, s.dataElementItem1, s.orgunitAbsolute, s.categoryItem1_1),
            item(s.periodAbsolute1, s.dataElementItem1, s.orgunitAbsolute, s.categoryItem1_2),
            item(s.periodAbsolute1, s.indicatorItem, s.orgunitAbsolute, s.categoryItem1_1),
            item(s.periodAbsolute1, s.indicatorItem, s.orgunitAbsolute, s.categoryItem1_2),
            item(s.periodAbsolute2, s.dataElementItem1, s.orgunitAbsolute, s.categoryItem1_1),
            item(s.periodAbsolute2, s.dataElementItem1, s.orgunitAbsolute, s.categoryItem1_2),
            item(s.periodAbsolute2, s.indicatorItem, s.orgunitAbsolute, s.categoryItem1_1),
            item(s.periodAbsolute2, s.indicatorItem, s.orgunitAbsolute, s.categoryItem1_2)
        )
    }

    @Test
    fun `Should ignore missing dimensions`() {
        val params = AnalyticsRepositoryParams(
            dimensions = listOf(
                s.dataElementItem1,
                s.periodAbsolute1,
                s.orgunitAbsolute,
                s.categoryItem1_1
            ),
            filters = listOf()
        )
        val dimensions = listOf(Dimension.Period, Dimension.Data, Dimension.Category(s.categoryItem1_1.uid))

        val items = helper.getEvaluationItems(params, dimensions)

        assertThat(items).containsExactly(
            item(s.periodAbsolute1, s.dataElementItem1, s.categoryItem1_1)
        )
    }

    @Test
    fun `Should evaluate relative periods`() {
        whenever(periodGenerator.generateRelativePeriods(s.periodLast3Days.relative))
            .thenReturn(
                listOf(
                    Period.builder().periodId("20210701").build(),
                    Period.builder().periodId("20210702").build(),
                    Period.builder().periodId("20210703").build()
                )
            )

        val params = AnalyticsRepositoryParams(
            dimensions = listOf(
                s.dataElementItem1,
                s.periodLast3Days,
                s.orgunitAbsolute
            ),
            filters = listOf()
        )
        val dimensions = listOf(Dimension.Data, Dimension.Period, Dimension.OrganisationUnit)

        val items = helper.getEvaluationItems(params, dimensions)

        assertThat(items).containsExactly(
            item(s.dataElementItem1, DimensionItem.PeriodItem.Absolute("20210701"), s.orgunitAbsolute),
            item(s.dataElementItem1, DimensionItem.PeriodItem.Absolute("20210702"), s.orgunitAbsolute),
            item(s.dataElementItem1, DimensionItem.PeriodItem.Absolute("20210703"), s.orgunitAbsolute)
        )
    }

    @Test
    fun `Should evaluate orgunits by level`() {
        whenever(organisationUnitHelper.getOrganisationUnitUidsByLevel(any()))
            .thenReturn(listOf("orgunit1", "orgunit2", "orgunit3"))

        val params = AnalyticsRepositoryParams(
            dimensions = listOf(
                s.dataElementItem1,
                s.periodAbsolute1,
                s.orgunitLevel3
            ),
            filters = listOf()
        )
        val dimensions = listOf(Dimension.Data, Dimension.Period, Dimension.OrganisationUnit)

        val items = helper.getEvaluationItems(params, dimensions)

        assertThat(items).containsExactly(
            item(s.dataElementItem1, s.periodAbsolute1, DimensionItem.OrganisationUnitItem.Absolute("orgunit1")),
            item(s.dataElementItem1, s.periodAbsolute1, DimensionItem.OrganisationUnitItem.Absolute("orgunit2")),
            item(s.dataElementItem1, s.periodAbsolute1, DimensionItem.OrganisationUnitItem.Absolute("orgunit3"))
        )
    }

    private fun item(vararg items: AbsoluteDimensionItem): AnalyticsServiceEvaluationItem {
        return AnalyticsServiceEvaluationItem(
            dimensionItems = items.toList(),
            filters = listOf()
        )
    }
}
