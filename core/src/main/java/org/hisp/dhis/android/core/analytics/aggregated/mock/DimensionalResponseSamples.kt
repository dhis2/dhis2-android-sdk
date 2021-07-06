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

package org.hisp.dhis.android.core.analytics.aggregated.mock

import org.hisp.dhis.android.core.analytics.aggregated.Dimension
import org.hisp.dhis.android.core.analytics.aggregated.DimensionalResponse
import org.hisp.dhis.android.core.analytics.aggregated.DimensionalValue
import org.hisp.dhis.android.core.analytics.aggregated.MetadataItem
import org.hisp.dhis.android.core.analytics.aggregated.mock.AggregatedSamples.cc1
import org.hisp.dhis.android.core.analytics.aggregated.mock.AggregatedSamples.co1
import org.hisp.dhis.android.core.analytics.aggregated.mock.AggregatedSamples.co2
import org.hisp.dhis.android.core.analytics.aggregated.mock.AggregatedSamples.dataElement1
import org.hisp.dhis.android.core.analytics.aggregated.mock.AggregatedSamples.dataElement2
import org.hisp.dhis.android.core.analytics.aggregated.mock.AggregatedSamples.orgunit1
import org.hisp.dhis.android.core.analytics.aggregated.mock.AggregatedSamples.orgunit2
import org.hisp.dhis.android.core.analytics.aggregated.mock.AggregatedSamples.period1
import org.hisp.dhis.android.core.analytics.aggregated.mock.AggregatedSamples.period2

object DimensionalResponseSamples {
    val sample1 = DimensionalResponse(
        metadata = mapOf(
            dataElement1.uid() to MetadataItem.DataElementItem(dataElement1),
            dataElement2.uid() to MetadataItem.DataElementItem(dataElement2),
            co1.uid() to MetadataItem.CategoryOptionItem(co1),
            co2.uid() to MetadataItem.CategoryOptionItem(co2),
            cc1.uid() to MetadataItem.CategoryItem(cc1),
            period1.periodId()!! to MetadataItem.PeriodItem(period1),
            period2.periodId()!! to MetadataItem.PeriodItem(period2),
            orgunit1.uid() to MetadataItem.OrganisationUnitItem(orgunit1),
            orgunit2.uid() to MetadataItem.OrganisationUnitItem(orgunit2)
        ),
        dimensions = setOf(Dimension.Data, Dimension.Category(cc1.uid()), Dimension.Period),
        filters = listOf(orgunit1.uid(), orgunit2.uid()),
        values = listOf(
            DimensionalValue(
                listOf(dataElement1.uid(), co1.uid(), period1.periodId()!!),
                "34.5"
            ),
            DimensionalValue(
                listOf(dataElement1.uid(), co2.uid(), period1.periodId()!!),
                "10.0"
            ),
            DimensionalValue(
                listOf(dataElement2.uid(), co1.uid(), period1.periodId()!!),
                "13"
            ),
            DimensionalValue(
                listOf(dataElement2.uid(), co2.uid(), period1.periodId()!!),
                "15"
            ),
            DimensionalValue(
                listOf(dataElement1.uid(), co1.uid(), period2.periodId()!!),
                "34.5"
            ),
            DimensionalValue(
                listOf(dataElement1.uid(), co2.uid(), period2.periodId()!!),
                "10.0"
            ),
            DimensionalValue(
                listOf(dataElement2.uid(), co1.uid(), period2.periodId()!!),
                "13"
            ),
            DimensionalValue(
                listOf(dataElement2.uid(), co2.uid(), period2.periodId()!!),
                "15"
            )
        )
    )
}
