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

package org.hisp.dhis.android.core.analytics.aggregated.internal.evaluator

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.analytics.AnalyticsException
import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.internal.AnalyticsServiceEvaluationItem
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AnalyticsDimensionHelperShould {

    @Test
    fun `Should return single dimension item`() {
        val item = AnalyticsServiceEvaluationItem(
            dimensionItems = listOf(
                DimensionItem.DataItem.DataElementItem("dataElement1"),
                DimensionItem.PeriodItem.Absolute("periodId")
            ),
            filters = listOf(
                DimensionItem.OrganisationUnitItem.Absolute("orgunit")
            )
        )

        val dataItem = AnalyticsDimensionHelper.getSingleItemByDimension<DimensionItem.DataItem>(item)
        assertThat(dataItem.size).isEqualTo(1)
        assertThat(dataItem.first()).isInstanceOf(DimensionItem.DataItem::class.java)

        val periodItem = AnalyticsDimensionHelper.getSingleItemByDimension<DimensionItem.PeriodItem>(item)
        assertThat(periodItem.size).isEqualTo(1)
        assertThat(periodItem.first()).isInstanceOf(DimensionItem.PeriodItem::class.java)

        val ouItem = AnalyticsDimensionHelper.getSingleItemByDimension<DimensionItem.OrganisationUnitItem>(item)
        assertThat(ouItem.size).isEqualTo(1)
        assertThat(ouItem.first()).isInstanceOf(DimensionItem.OrganisationUnitItem::class.java)
    }

    @Test(expected = AnalyticsException.InvalidArguments::class)
    fun `Should return error when multiple dimension items`() {
        val item = AnalyticsServiceEvaluationItem(
            dimensionItems = listOf(
                DimensionItem.DataItem.DataElementItem("dataElement1"),
                DimensionItem.DataItem.DataElementItem("dataElement2")
            ),
            filters = listOf()
        )

        AnalyticsDimensionHelper.getSingleItemByDimension<DimensionItem.DataItem>(item)
    }

    @Test
    fun `Should return last item when multiple items has filter`() {
        val item = AnalyticsServiceEvaluationItem(
            dimensionItems = listOf(),
            filters = listOf(
                DimensionItem.DataItem.DataElementItem("dataElement1"),
                DimensionItem.DataItem.DataElementItem("dataElement2")
            )
        )

        val dataItem = AnalyticsDimensionHelper.getSingleItemByDimension<DimensionItem.DataItem>(item)
        assertThat(dataItem.size).isEqualTo(2)
        assertThat(dataItem[0]).isInstanceOf(DimensionItem.DataItem::class.java)
        assertThat(dataItem[1]).isInstanceOf(DimensionItem.DataItem::class.java)
    }
}
