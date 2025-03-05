/*
 *  Copyright (c) 2004-2025, University of Oslo
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

package org.hisp.dhis.android.core.common.internal

import kotlinx.serialization.Serializable
import org.hisp.dhis.android.core.common.DateFilterPeriod
import org.hisp.dhis.android.core.common.DatePeriodType
import org.hisp.dhis.android.core.common.RelativePeriod
import org.hisp.dhis.android.core.util.simpleDateFormat
import org.hisp.dhis.android.core.util.toJavaSimpleDate

@Serializable
internal data class DateFilterPeriodDAO(
    val startBuffer: Int?,
    val endBuffer: Int?,
    val startDate: String?,
    val endDate: String?,
    val period: String?,
    val type: String?,
) {
    fun toDomain(): DateFilterPeriod {
        return DateFilterPeriod.builder()
            .startBuffer(startBuffer)
            .endBuffer(endBuffer)
            .startDate(startDate?.let { it.toJavaSimpleDate() })
            .endDate(endDate?.let { it.toJavaSimpleDate() })
            .period(period?.let { RelativePeriod.valueOf(it) })
            .type(type?.let { DatePeriodType.valueOf(it) })
            .build()
    }

    companion object {
        fun DateFilterPeriod.toDao(): DateFilterPeriodDAO {
            return DateFilterPeriodDAO(
                startBuffer = this.startBuffer(),
                endBuffer = this.endBuffer(),
                startDate = this.startDate()?.simpleDateFormat(),
                endDate = this.endDate()?.simpleDateFormat(),
                period = this.period()?.name,
                type = this.type()?.name,
            )
        }
    }
}
