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
package org.hisp.dhis.android.core.period.generator.internal

import kotlinx.datetime.Clock
import kotlinx.datetime.Month
import org.hisp.dhis.android.core.period.PeriodType

@Suppress("MagicNumber")
internal object NMonthlyPeriodGeneratorFactory {
    fun biMonthly(clock: Clock): NMonthlyPeriodGenerator {
        return BiMonthlyPeriodGenerator(clock)
    }

    fun quarterly(clock: Clock): NMonthlyPeriodGenerator {
        return NMonthlyPeriodGenerator(clock, PeriodType.Quarterly, 3, "Q", Month.JANUARY)
    }

    fun quarterlyNov(clock: Clock): NMonthlyPeriodGenerator {
        return NMonthlyPeriodGenerator(clock, PeriodType.QuarterlyNov, 3, "NovQ", Month.NOVEMBER)
    }

    fun sixMonthly(clock: Clock): NMonthlyPeriodGenerator {
        return NMonthlyPeriodGenerator(clock, PeriodType.SixMonthly, 6, "S", Month.JANUARY)
    }

    fun sixMonthlyApril(clock: Clock): NMonthlyPeriodGenerator {
        return NMonthlyPeriodGenerator(clock, PeriodType.SixMonthlyApril, 6, "AprilS", Month.APRIL)
    }

    fun sixMonthlyNov(clock: Clock): NMonthlyPeriodGenerator {
        return NMonthlyPeriodGenerator(clock, PeriodType.SixMonthlyNov, 6, "NovS", Month.NOVEMBER)
    }
}
