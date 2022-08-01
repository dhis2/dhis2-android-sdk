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

package org.hisp.dhis.android.core.analytics.aggregated.mock

import org.hisp.dhis.android.core.analytics.aggregated.*
import org.hisp.dhis.android.core.analytics.aggregated.mock.AggregatedSamples.cc1
import org.hisp.dhis.android.core.analytics.aggregated.mock.AggregatedSamples.co11
import org.hisp.dhis.android.core.analytics.aggregated.mock.AggregatedSamples.co12
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
            co11.uid() to MetadataItem.CategoryOptionItem(co11),
            co12.uid() to MetadataItem.CategoryOptionItem(co12),
            cc1.uid() to MetadataItem.CategoryItem(cc1),
            period1.periodId()!! to MetadataItem.PeriodItem(period1),
            period2.periodId()!! to MetadataItem.PeriodItem(period2),
            orgunit1.uid() to MetadataItem.OrganisationUnitItem(orgunit1),
            orgunit2.uid() to MetadataItem.OrganisationUnitItem(orgunit2)
        ),
        dimensions = listOf(Dimension.Data, Dimension.Category(cc1.uid()), Dimension.Period),
        dimensionItems = mapOf(
            Dimension.Data to listOf(
                DimensionItem.DataItem.DataElementItem(dataElement1.uid()),
                DimensionItem.DataItem.DataElementItem(dataElement2.uid())
            ),
            Dimension.Category(cc1.uid()) to listOf(
                DimensionItem.CategoryItem(cc1.uid(), co11.uid()),
                DimensionItem.CategoryItem(cc1.uid(), co12.uid())
            ),
            Dimension.Period to listOf(
                DimensionItem.PeriodItem.Absolute(period1.periodId()!!),
                DimensionItem.PeriodItem.Absolute(period2.periodId()!!)
            ),
            Dimension.OrganisationUnit to listOf(
                DimensionItem.OrganisationUnitItem.Absolute(orgunit1.uid()),
                DimensionItem.OrganisationUnitItem.Absolute(orgunit2.uid())
            )
        ),
        filters = listOf(orgunit1.uid(), orgunit2.uid()),
        values = listOf(
            DimensionalValue(
                listOf(dataElement1.uid(), co11.uid(), period1.periodId()!!),
                "34.5",
                null
            ),
            DimensionalValue(
                listOf(dataElement1.uid(), co12.uid(), period1.periodId()!!),
                "10.0",
                null
            ),
            DimensionalValue(
                listOf(dataElement2.uid(), co11.uid(), period1.periodId()!!),
                "13",
                null
            ),
            DimensionalValue(
                listOf(dataElement2.uid(), co12.uid(), period1.periodId()!!),
                "15",
                null
            ),
            DimensionalValue(
                listOf(dataElement1.uid(), co11.uid(), period2.periodId()!!),
                "34.5",
                null
            ),
            DimensionalValue(
                listOf(dataElement1.uid(), co12.uid(), period2.periodId()!!),
                "10.0",
                null
            ),
            DimensionalValue(
                listOf(dataElement2.uid(), co11.uid(), period2.periodId()!!),
                "13",
                null
            ),
            DimensionalValue(
                listOf(dataElement2.uid(), co12.uid(), period2.periodId()!!),
                "15",
                null
            )
        )
    )
}
