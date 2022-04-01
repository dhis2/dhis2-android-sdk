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

import org.hisp.dhis.android.core.category.Category
import org.hisp.dhis.android.core.category.CategoryOption
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.indicator.Indicator
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitLevel
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.program.ProgramIndicator

object AggregatedSamples {
    val dataElement1 = DataElement.builder()
        .uid("fbfJHSPpUQD")
        .displayName("ANC 1st visit")
        .build()

    val dataElement2 = DataElement.builder()
        .uid("cYeuwXTCPkU")
        .displayName("ANC 2nd visit")
        .build()

    val programIndicator1 = ProgramIndicator.builder()
        .uid("p2Zxg0wcPQ3")
        .displayName("BCG doses")
        .build()

    val indicator1 = Indicator.builder()
        .uid("Uvn6LCg7dVU")
        .displayName("ANC 1 Coverage")
        .build()

    val cc1 = Category.builder()
        .uid("fMZEcRHuamy")
        .displayName("Fixed / Outreach")
        .build()

    val co11 = CategoryOption.builder()
        .uid("pq2XI5kz2BY")
        .displayName("Fixed")
        .build()

    val co12 = CategoryOption.builder()
        .uid("PT59n8BQbqM")
        .displayName("Outreach")
        .build()

    val cc2 = Category.builder()
        .uid("cX5k9anHEHd")
        .displayName("Gender")
        .build()

    val co21 = CategoryOption.builder()
        .uid("jRbMi0aBjYn")
        .displayName("Male")
        .build()

    val period1 = Period.builder()
        .periodId("202103")
        .build()

    val period2 = Period.builder()
        .periodId("202104")
        .build()

    val orgunit1 = OrganisationUnit.builder()
        .uid("DiszpKrYNg8")
        .displayName("Ngelehun")
        .build()

    val orgunit2 = OrganisationUnit.builder()
        .uid("g8upMTyEZGZ")
        .displayName("Njandama")
        .build()

    @SuppressWarnings("MagicNumber")
    val level3 = OrganisationUnitLevel.builder()
        .uid("VVkwUWGNpNR")
        .displayName("Level 3")
        .level(3)
        .build()
}
