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

package org.hisp.dhis.android.core.period.internal;

import java.util.Calendar;

final class WeeklyPeriodGenerators {

    final PeriodGenerator weekly;
    final PeriodGenerator weeklyWednesday;
    final PeriodGenerator weeklyThursday;
    final PeriodGenerator weeklySaturday;
    final PeriodGenerator weeklySunday;

    WeeklyPeriodGenerators(PeriodGenerator weekly,
                           PeriodGenerator weeklyWednesday,
                           PeriodGenerator weeklyThursday,
                           PeriodGenerator weeklySaturday,
                           PeriodGenerator weeklySunday) {
        this.weekly = weekly;
        this.weeklyWednesday = weeklyWednesday;
        this.weeklyThursday = weeklyThursday;
        this.weeklySaturday = weeklySaturday;
        this.weeklySunday = weeklySunday;
    }


    static WeeklyPeriodGenerators create(Calendar calendar) {
        return new WeeklyPeriodGenerators(
                WeeklyPeriodGeneratorFactory.weekly(calendar),
                WeeklyPeriodGeneratorFactory.wednesday(calendar),
                WeeklyPeriodGeneratorFactory.thursday(calendar),
                WeeklyPeriodGeneratorFactory.saturday(calendar),
                WeeklyPeriodGeneratorFactory.sunday(calendar)
        );
    }
}
