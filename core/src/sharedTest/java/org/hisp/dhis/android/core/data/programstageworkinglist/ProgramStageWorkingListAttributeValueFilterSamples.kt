/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.data.programstageworkinglist

import org.hisp.dhis.android.core.common.DateFilterPeriod
import org.hisp.dhis.android.core.common.DatePeriodType
import org.hisp.dhis.android.core.data.utils.FillPropertiesTestUtils.parseDate
import org.hisp.dhis.android.core.programstageworkinglist.ProgramStageWorkingListAttributeValueFilter

internal object ProgramStageWorkingListAttributeValueFilterSamples {
    fun get(): ProgramStageWorkingListAttributeValueFilter {
        return ProgramStageWorkingListAttributeValueFilter.builder()
            .id(1L)
            .programStageWorkingList("programStageWorkingList")
            .attribute("attributeUid")
            .sw("as")
            .ew("sa")
            .le("20")
            .ge("10")
            .gt("10")
            .lt("20")
            .eq("abc")
            .`in`(setOf("Norway"))
            .like("abc")
            .dateFilter(
                DateFilterPeriod.builder()
                    .startDate(parseDate("2014-05-01"))
                    .endDate(parseDate("2019-03-20"))
                    .type(DatePeriodType.ABSOLUTE)
                    .build(),
            )
            .build()
    }
}
