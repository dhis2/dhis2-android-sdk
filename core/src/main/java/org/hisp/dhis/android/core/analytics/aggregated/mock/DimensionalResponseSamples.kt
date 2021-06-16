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

import java.util.*
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
import org.hisp.dhis.android.core.period.PeriodType

object DimensionalResponseSamples {
    val sample1 = DimensionalResponse(
        metadata = mapOf(
            dataElement1 to MetadataItem.DataElement(dataElement1, "ANC 1st visit"),
            dataElement2 to MetadataItem.DataElement(dataElement2, "ANC 2nd visit"),
            co1 to MetadataItem.CategoryOption(co1, "Fixed", cc1),
            co2 to MetadataItem.CategoryOption(co2, "Outreach", cc1),
            cc1 to MetadataItem.Category(cc1, "Fixed / Outreach"),
            period1 to MetadataItem.Period(period1, PeriodType.Daily, Date(), Date()),
            period2 to MetadataItem.Period(period2, PeriodType.Daily, Date(), Date()),
            orgunit1 to MetadataItem.OrganisationUnit(orgunit1, "Ngelehun CHC"),
            orgunit2 to MetadataItem.OrganisationUnit(orgunit2, "Njandama MCHP")
        ),
        dimensions = listOf(Dimension.Data, Dimension.Category(co1), Dimension.Period),
        filters = listOf(orgunit1, orgunit2),
        values = listOf(
            DimensionalValue(
                listOf(dataElement1, co1, period1),
                "34.5"
            ),
            DimensionalValue(
                listOf(dataElement1, co2, period1),
                "10.0"
            ),
            DimensionalValue(
                listOf(dataElement2, co1, period1),
                "13"
            ),
            DimensionalValue(
                listOf(dataElement2, co2, period1),
                "15"
            ),
            DimensionalValue(
                listOf(dataElement1, co1, period2),
                "34.5"
            ),
            DimensionalValue(
                listOf(dataElement1, co2, period2),
                "10.0"
            ),
            DimensionalValue(
                listOf(dataElement2, co1, period2),
                "13"
            ),
            DimensionalValue(
                listOf(dataElement2, co2, period2),
                "15"
            )
        )
    )
}
