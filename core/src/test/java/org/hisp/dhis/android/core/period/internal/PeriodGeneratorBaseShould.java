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

import org.hisp.dhis.android.core.period.Period;
import org.hisp.dhis.android.core.period.PeriodType;

import java.util.Calendar;

public abstract class PeriodGeneratorBaseShould {

    protected final PeriodType periodType;
    protected final Calendar calendar;
    private final int calendarCode;
    private final int incrementsAmount;

    PeriodGeneratorBaseShould(PeriodType periodType, int calendarCode) {
        this(periodType, calendarCode, 1);
    }

    PeriodGeneratorBaseShould(PeriodType periodType, int calendarCode, int incrementsAmount) {
        this.periodType = periodType;
        this.calendar = Calendar.getInstance();
        this.calendarCode = calendarCode;
        this.incrementsAmount = incrementsAmount;
    }

    Period generateExpectedPeriod(String id, Calendar cal) {
        Calendar startCalendar = (Calendar) cal.clone();
        AbstractPeriodGenerator.setCalendarToStartTimeOfADay(startCalendar);
        setStartCalendar(startCalendar);
        Calendar endCalendar = (Calendar) startCalendar.clone();
        endCalendar.add(calendarCode, incrementsAmount);
        endCalendar.add(Calendar.MILLISECOND, -1);
        return Period.builder()
                .periodId(id)
                .periodType(periodType)
                .startDate(startCalendar.getTime())
                .endDate(endCalendar.getTime())
                .build();
    }

    protected void setStartCalendar(Calendar calendar) {
        // do nothing.
    }
}