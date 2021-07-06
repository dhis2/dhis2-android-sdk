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

import org.hisp.dhis.android.core.category.Category
import org.hisp.dhis.android.core.category.CategoryOption
import org.hisp.dhis.android.core.dataelement.DataElement
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit
import org.hisp.dhis.android.core.period.Period

object AggregatedSamples {
    val dataElement1 = DataElement.builder()
        .uid("fbfJHSPpUQD")
        .displayName("ANC 1st visit")
        .build()

    val dataElement2 = DataElement.builder()
        .uid("cYeuwXTCPkU")
        .displayName("ANC 2nd visit")
        .build()

    val cc1 = Category.builder()
        .uid("fMZEcRHuamy")
        .displayName("Fixed / Outreach")
        .build()

    val co1 = CategoryOption.builder()
        .uid("pq2XI5kz2BY")
        .displayName("Fixed")
        .build()

    val co2 = CategoryOption.builder()
        .uid("PT59n8BQbqM")
        .name("Outreach")
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
        .name("Njandama")
        .build()
}
