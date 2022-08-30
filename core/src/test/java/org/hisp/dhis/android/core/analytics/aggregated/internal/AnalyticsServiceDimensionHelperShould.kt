
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

package org.hisp.dhis.android.core.analytics.aggregated.internal

import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import org.hisp.dhis.android.core.analytics.aggregated.AbsoluteDimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.Dimension
import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsServiceHelperSamples as s
import org.hisp.dhis.android.core.period.internal.ParentPeriodGenerator
import org.hisp.dhis.android.core.period.internal.PeriodHelper
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AnalyticsServiceDimensionHelperShould {

    private val periodGenerator: ParentPeriodGenerator = mock()

    private val periodHelper: PeriodHelper = mock()

    private val organisationUnitHelper: AnalyticsOrganisationUnitHelper = mock()

    private val helper = AnalyticsServiceDimensionHelper(periodGenerator, periodHelper, organisationUnitHelper)

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

        val dimensionSet = helper.getQueryDimensions(params)

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
        val dimensionsMap = mapOf(
            Dimension.Period to listOf(s.periodAbsolute1, s.periodAbsolute2),
            Dimension.Data to listOf(s.dataElementItem1, s.indicatorItem),
            Dimension.OrganisationUnit to listOf(s.orgunitAbsolute),
            Dimension.Category(s.categoryItem1_1.uid) to listOf(s.categoryItem1_1, s.categoryItem1_2)
        )

        val items = helper.getEvaluationItems(params, dimensionsMap)

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
        val dimensionsMap = mapOf(
            Dimension.Period to listOf(s.periodAbsolute1),
            Dimension.Data to listOf(s.dataElementItem1),
            Dimension.Category(s.categoryItem1_1.uid) to listOf(s.categoryItem1_1)
        )

        val items = helper.getEvaluationItems(params, dimensionsMap)

        assertThat(items).containsExactly(
            item(s.periodAbsolute1, s.dataElementItem1, s.categoryItem1_1)
        )
    }

    @Test
    fun `Should evaluate relative periods`() {
        whenever(periodGenerator.generateRelativePeriods(s.periodLast3Days.relative))
            .thenReturn(listOf(s.period1, s.period2, s.period3))

        whenever(periodHelper.blockingGetPeriodForPeriodId(s.period1.periodId()!!)).doReturn(s.period1)
        whenever(periodHelper.blockingGetPeriodForPeriodId(s.period2.periodId()!!)).doReturn(s.period2)
        whenever(periodHelper.blockingGetPeriodForPeriodId(s.period3.periodId()!!)).doReturn(s.period3)

        val dimensionItems = listOf(
            s.dataElementItem1,
            s.periodLast3Days,
            s.orgunitAbsolute
        )
        val dimensions = listOf(Dimension.Data, Dimension.Period, Dimension.OrganisationUnit)

        val items = helper.getQueryAbsoluteDimensionItems(dimensionItems, dimensions)

        assertThat(items).isEqualTo(
            mapOf(
                Dimension.Data to listOf(s.dataElementItem1),
                Dimension.Period to listOf(
                    DimensionItem.PeriodItem.Absolute("20210701"),
                    DimensionItem.PeriodItem.Absolute("20210702"),
                    DimensionItem.PeriodItem.Absolute("20210703")
                ),
                Dimension.OrganisationUnit to listOf(s.orgunitAbsolute)
            )
        )
    }

    @Test
    fun `Should evaluate orgunits by level`() {
        whenever(organisationUnitHelper.getOrganisationUnitUidsByLevelUid(any()))
            .thenReturn(listOf("orgunit1", "orgunit2", "orgunit3"))

        val dimensionItems = listOf(
            s.dataElementItem1,
            s.periodAbsolute1,
            s.orgunitLevel3
        )
        val dimensions = listOf(Dimension.Data, Dimension.Period, Dimension.OrganisationUnit)

        val items = helper.getQueryAbsoluteDimensionItems(dimensionItems, dimensions)

        assertThat(items).isEqualTo(
            mapOf(
                Dimension.Data to listOf(s.dataElementItem1),
                Dimension.Period to listOf(s.periodAbsolute1),
                Dimension.OrganisationUnit to listOf(
                    DimensionItem.OrganisationUnitItem.Absolute("orgunit1"),
                    DimensionItem.OrganisationUnitItem.Absolute("orgunit2"),
                    DimensionItem.OrganisationUnitItem.Absolute("orgunit3")
                )
            )
        )
    }

    private fun item(vararg items: AbsoluteDimensionItem): AnalyticsServiceEvaluationItem {
        return AnalyticsServiceEvaluationItem(
            dimensionItems = items.toList(),
            filters = listOf()
        )
    }
}
