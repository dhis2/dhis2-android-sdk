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

import org.hisp.dhis.android.core.period.PeriodType;

import java.util.Calendar;
import java.util.Date;

final class BiWeeklyPeriodGenerator extends AbstractPeriodGenerator {

    BiWeeklyPeriodGenerator(Calendar calendar) {
        super(calendar, "yyyy", PeriodType.BiWeekly);
    }

    @Override
    protected void moveToStartOfCurrentPeriod() {
        calendar.getTime();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setMinimalDaysInFirstWeek(4);
        CalendarUtils.setDayOfWeek(calendar, Calendar.MONDAY);

        Calendar cal = (Calendar) calendar.clone();
        CalendarUtils.setDayOfWeek(cal, Calendar.MONDAY + 3);
        Integer weekOfYear = cal.get(Calendar.WEEK_OF_YEAR);
        Boolean secondWeekOfBiWeek = weekOfYear % 2 == 0;
        if (secondWeekOfBiWeek) {
            calendar.add(Calendar.WEEK_OF_YEAR, -1);
        }
    }

    @Override
    protected void moveToStartOfCurrentYear() {
        moveToStartOfCurrentPeriod();
        calendar.set(Calendar.WEEK_OF_YEAR, 1);
    }

    @Override
    protected void movePeriods(int number) {
        calendar.add(Calendar.WEEK_OF_YEAR, number * 2);
    }

    @Override
    protected String generateId() {
        Calendar cal = (Calendar) calendar.clone();
        CalendarUtils.setDayOfWeek(cal, Calendar.MONDAY + 3);
        Date fourthWeekDay = cal.getTime();
        String year = idFormatter.format(fourthWeekDay);
        Integer weekOfYear = cal.get(Calendar.WEEK_OF_YEAR);
        Integer biWeekOfYear = (int) Math.ceil((double) weekOfYear / 2.0);
        return year + "BiW" + biWeekOfYear;
    }
}
