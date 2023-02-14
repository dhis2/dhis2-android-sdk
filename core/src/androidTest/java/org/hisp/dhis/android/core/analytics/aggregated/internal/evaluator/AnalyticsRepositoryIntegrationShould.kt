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
import org.hisp.dhis.android.core.arch.helpers.Result
import org.hisp.dhis.android.core.common.AggregationType
import org.hisp.dhis.android.core.common.RelativePeriod
import org.hisp.dhis.android.core.dataelement.internal.DataElementStore
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

class AnalyticsRepositoryIntegrationShould : BaseMockIntegrationTestFullDispatcher() {

    @Test
    fun order_periods() {
        val result = d2.analyticsModule().analytics()
            .withDimension(DimensionItem.DataItem.DataElementItem("g9eOBujte1U"))
            .withDimension(DimensionItem.PeriodItem.Absolute("2022"))
            .withDimension(DimensionItem.PeriodItem.Absolute("202105"))
            .withDimension(DimensionItem.PeriodItem.Absolute("2021"))
            .blockingEvaluate()
            .getOrThrow()

        val valuePeriods = result.values.map { it.dimensions.last() }
        assertThat(valuePeriods.size).isEqualTo(3)
        assertThat(valuePeriods[0]).isEqualTo("202105")
        assertThat(valuePeriods[1]).isEqualTo("2021")
        assertThat(valuePeriods[2]).isEqualTo("2022")
    }

    @Test
    fun order_relative_periods() {
        val result = d2.analyticsModule().analytics()
            .withDimension(DimensionItem.DataItem.DataElementItem("g9eOBujte1U"))
            .withDimension(DimensionItem.PeriodItem.Relative(RelativePeriod.LAST_3_MONTHS))
            .blockingEvaluate()
            .getOrThrow()

        val valuePeriods = result.values.map { it.dimensions.last() }
        assertThat(valuePeriods.size).isEqualTo(3)
        assertThat(valuePeriods[0]).isEqualTo("201909")
        assertThat(valuePeriods[1]).isEqualTo("201910")
        assertThat(valuePeriods[2]).isEqualTo("201911")
    }

    @Test
    fun remove_duplicate_periods() {
        val result = d2.analyticsModule().analytics()
            .withDimension(DimensionItem.DataItem.DataElementItem("g9eOBujte1U"))
            .withDimension(DimensionItem.PeriodItem.Absolute("2021"))
            .withDimension(DimensionItem.PeriodItem.Absolute("2022"))
            .withDimension(DimensionItem.PeriodItem.Absolute("2022"))
            .withDimension(DimensionItem.PeriodItem.Absolute("2021"))
            .blockingEvaluate()
            .getOrThrow()

        val valuePeriods = result.values.map { it.dimensions.last() }
        assertThat(valuePeriods.size).isEqualTo(2)
        assertThat(valuePeriods[0]).isEqualTo("2021")
        assertThat(valuePeriods[1]).isEqualTo("2022")
    }

    @Test
    fun should_fail_if_unsupported_aggregation_type() {
        val dataElementStore = DataElementStore.create(databaseAdapter)
        val dataElement = dataElementStore.selectByUid("g9eOBujte1U")!!

        val varianceDataElement = dataElement.toBuilder().aggregationType(AggregationType.VARIANCE.name).build()
        dataElementStore.updateOrInsert(varianceDataElement)

        val result = d2.analyticsModule().analytics()
            .withDimension(DimensionItem.DataItem.DataElementItem("g9eOBujte1U"))
            .withDimension(DimensionItem.PeriodItem.Absolute("2021"))
            .blockingEvaluate()

        assertThat(result.succeeded).isFalse()

        assertThat((result as Result.Failure).failure)
            .isInstanceOf(AnalyticsException.UnsupportedAggregationType::class.java)

        dataElementStore.updateOrInsert(dataElement)
    }
}
