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

import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.analytics.aggregated.mock.AggregatedSamples
import org.hisp.dhis.android.core.analytics.aggregated.mock.AggregatedSamples.level3
import org.hisp.dhis.android.core.arch.helpers.DateUtils
import org.hisp.dhis.android.core.common.RelativePeriod
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.period.PeriodType

object AnalyticsServiceHelperSamples {

    val dataElementItem1 = DimensionItem.DataItem.DataElementItem(AggregatedSamples.dataElement1.uid())
    val dataElementItem2 = DimensionItem.DataItem.DataElementItem(AggregatedSamples.dataElement2.uid())
    val programIndicatorItem = DimensionItem.DataItem.ProgramIndicatorItem(AggregatedSamples.programIndicator1.uid())
    val indicatorItem = DimensionItem.DataItem.IndicatorItem(AggregatedSamples.indicator1.uid())

    val periodAbsolute1 = DimensionItem.PeriodItem.Absolute(AggregatedSamples.period1.periodId()!!)
    val periodAbsolute2 = DimensionItem.PeriodItem.Absolute(AggregatedSamples.period2.periodId()!!)
    val periodLast3Days = DimensionItem.PeriodItem.Relative(RelativePeriod.LAST_3_DAYS)

    val orgunitAbsolute = DimensionItem.OrganisationUnitItem.Absolute(AggregatedSamples.orgunit1.uid())
    val orgunitLevel3 = DimensionItem.OrganisationUnitItem.Level(level3.uid())

    val categoryItem1_1 = DimensionItem.CategoryItem(AggregatedSamples.cc1.uid(), AggregatedSamples.co11.uid())
    val categoryItem1_2 = DimensionItem.CategoryItem(AggregatedSamples.cc1.uid(), AggregatedSamples.co12.uid())
    val categoryItem2_1 = DimensionItem.CategoryItem(AggregatedSamples.cc2.uid(), AggregatedSamples.co21.uid())

    val period1 = Period.builder()
        .periodType(PeriodType.Daily)
        .periodId("20210701")
        .startDate(DateUtils.DATE_FORMAT.parse("2021-07-01T00:00:00.000"))
        .build()

    val period2 = Period.builder()
        .periodType(PeriodType.Daily)
        .periodId("20210702")
        .startDate(DateUtils.DATE_FORMAT.parse("2021-07-02T00:00:00.000"))
        .build()

    val period3 = Period.builder()
        .periodType(PeriodType.Daily)
        .periodId("20210703")
        .startDate(DateUtils.DATE_FORMAT.parse("2021-07-03T00:00:00.000"))
        .build()
}
