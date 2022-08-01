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

class NMonthlyPeriodGenerator extends AbstractPeriodGenerator {

    final int durationInMonths;
    final String idAdditionalString;
    private final int startMonth;

    NMonthlyPeriodGenerator(Calendar calendar, PeriodType periodType, int durationInMonths,
                            String idAdditionalString, int startMonth) {
        super(calendar, "yyyy", periodType);
        this.durationInMonths = durationInMonths;
        this.idAdditionalString = idAdditionalString;
        this.startMonth = startMonth;
    }

    @Override
    protected void moveToStartOfCurrentPeriod() {
        calendar.set(Calendar.DATE, 1);
        int currentMonth = calendar.get(Calendar.MONTH);
        int monthsFromStart = (currentMonth - startMonth + 12) % durationInMonths;
        int currentPeriodStartMonth = (currentMonth - monthsFromStart + 12) % 12;
        if (currentMonth - monthsFromStart < 0) {
            calendar.add(Calendar.YEAR, -1);
        }
        calendar.set(Calendar.MONTH, currentPeriodStartMonth);
    }

    @Override
    protected void moveToStartOfCurrentYear() {
        calendar.set(Calendar.DATE, 1);
        if (startMonth >= 6) {
            calendar.add(Calendar.YEAR, -1);
        }
        calendar.set(Calendar.MONTH, startMonth);
    }

    @Override
    protected void movePeriods(int number) {
        calendar.add(Calendar.MONTH, durationInMonths * number);
    }

    @Override
    protected String generateId() {
        Calendar calendarCopy = (Calendar) calendar.clone();
        if (calendarCopy.get(Calendar.MONTH) < startMonth) {
            calendarCopy.add(Calendar.YEAR, -1);
        }
        int periodNumber = ((calendarCopy.get(Calendar.MONTH) - startMonth + 12) % 12) / durationInMonths + 1;

        String year;
        if (periodType == PeriodType.SixMonthlyNov) {
            calendarCopy.add(Calendar.YEAR, +1);
            Date date = calendarCopy.getTime();
            calendarCopy.add(Calendar.YEAR, -1);
            year = idFormatter.format(date);
        } else {
            year = idFormatter.format(calendarCopy.getTime());
        }

        return year + idAdditionalString + periodNumber;
    }
}
