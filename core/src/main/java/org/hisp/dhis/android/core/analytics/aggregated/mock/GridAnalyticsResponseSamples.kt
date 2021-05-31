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

import org.hisp.dhis.android.core.analytics.aggregated.*
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
import java.util.*

object GridSamples {
    val sample1 = GridAnalyticsResponse(
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
        headers = GridHeader(
            columns = listOf(
                listOf(
                    GridHeaderItem(dataElement1, 2),
                    GridHeaderItem(dataElement2, 2)
                ),
                listOf(
                    GridHeaderItem(co1, 1),
                    GridHeaderItem(co2, 1),
                    GridHeaderItem(co1, 1),
                    GridHeaderItem(co2, 1)
                )
            ),
            rows = listOf(
                listOf(
                    GridHeaderItem(period1, 1),
                    GridHeaderItem(period2, 1)
                )
            )
        ),
        dimensions = GridDimension(
            columns = listOf(Dimension.Data, Dimension.Category(co1)),
            rows = listOf(Dimension.Period)
        ),
        filters = listOf(orgunit1, orgunit2),
        values = listOf(
            listOf(
                GridResponseValue(
                    listOf(dataElement1, co1),
                    listOf(period1),
                    "34.5"
                ),
                GridResponseValue(
                    listOf(dataElement1, co2),
                    listOf(period1),
                    "10.0"
                ),
                GridResponseValue(
                    listOf(dataElement2, co1),
                    listOf(period1),
                    "13"
                ),
                GridResponseValue(
                    listOf(dataElement2, co2),
                    listOf(period1),
                    "15"
                )
            ),
            listOf(
                GridResponseValue(
                    listOf(dataElement1, co1),
                    listOf(period2),
                    "34.5"
                ),
                GridResponseValue(
                    listOf(dataElement1, co2),
                    listOf(period2),
                    "10.0"
                ),
                GridResponseValue(
                    listOf(dataElement2, co1),
                    listOf(period2),
                    "13"
                ),
                GridResponseValue(
                    listOf(dataElement2, co2),
                    listOf(period2),
                    "15"
                )
            )
        )
    )
}