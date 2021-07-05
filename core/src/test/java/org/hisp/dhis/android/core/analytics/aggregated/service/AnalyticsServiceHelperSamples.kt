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

package org.hisp.dhis.android.core.analytics.aggregated.service

import org.hisp.dhis.android.core.analytics.aggregated.DimensionItem
import org.hisp.dhis.android.core.common.RelativePeriod

object AnalyticsServiceHelperSamples {

    val dataElementItem1 = DimensionItem.DataItem.DataElement("uid1")
    val dataElementItem2 = DimensionItem.DataItem.DataElement("uid2")
    val programIndicatorItem = DimensionItem.DataItem.ProgramIndicator("pi1")
    val indicatorItem = DimensionItem.DataItem.Indicator("i1")

    val periodAbsolute1 = DimensionItem.PeriodItem.Absolute("202101")
    val periodAbsolute2 = DimensionItem.PeriodItem.Absolute("202102")
    val periodLast3Days = DimensionItem.PeriodItem.Relative(RelativePeriod.LAST_3_DAYS)

    val orgunitAbsolute = DimensionItem.OrganisationUnitItem.Absolute("ou1")
    val orgunitLevel3 = DimensionItem.OrganisationUnitItem.Level(3)

    val categoryItem1_1 = DimensionItem.CategoryItem("c1", "co11")
    val categoryItem1_2 = DimensionItem.CategoryItem("c1", "co12")
    val categoryItem2_1 = DimensionItem.CategoryItem("c2", "co21")

    val categoryOptionGroupSetItem1_1 = DimensionItem.CategoryOptionGroupSetItem("cogs1", "cog11")
    val categoryOptionGroupSetItem1_2 = DimensionItem.CategoryOptionGroupSetItem("cogs1", "cog12")
    val categoryOptionGroupSetItem2_1 = DimensionItem.CategoryOptionGroupSetItem("cogs2", "cog21")

}