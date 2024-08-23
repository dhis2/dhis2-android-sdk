/*
 *  Copyright (c) 2004-2024, University of Oslo
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

package org.hisp.dhis.android.core.period.internal

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toJavaInstant
import kotlinx.datetime.toLocalDateTime
import org.hisp.dhis.android.core.arch.helpers.DateUtils.atStartOfDayInSystem
import org.hisp.dhis.android.core.period.Period
import org.hisp.dhis.android.core.period.generator.internal.PeriodKt
import org.hisp.dhis.android.core.util.toLocalDate
import java.util.Date

internal fun List<PeriodKt>.toPeriods(): List<Period> {
    return this.map { it.toPeriods() }
}

internal fun PeriodKt.toPeriods(): Period {
    return Period.builder()
        .periodId(this.periodId)
        .periodType(this.periodType)
        .startDate(toStartDate(this.startDate))
        .endDate(toEndDate(this.endDate))
        .build()
}

private fun toStartDate(localDate: LocalDate): Date {
    return Date.from(localDate.atStartOfDayInSystem().toJavaInstant())
}

private fun toEndDate(localDate: LocalDate): Date {
    val nextDay = toStartDate(localDate.plus(1, DateTimeUnit.DAY))
    return Date(nextDay.time - 1)
}

internal fun Date.toLocalDate(): LocalDate {
    return Instant.fromEpochMilliseconds(this.time).toLocalDateTime(TimeZone.currentSystemDefault()).toLocalDate()
}
