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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@SuppressWarnings({"PMD.AvoidSynchronizedAtMethodLevel"})
abstract class AbstractPeriodGenerator implements PeriodGenerator {
    private final Calendar initialCalendar;
    protected Calendar calendar;
    final SimpleDateFormat idFormatter;
    protected final PeriodType periodType;


    AbstractPeriodGenerator(Calendar calendar, String dateFormatStr, PeriodType periodType) {
        this.initialCalendar = (Calendar) calendar.clone();
        this.calendar = (Calendar) calendar.clone();
        this.idFormatter = new SimpleDateFormat(dateFormatStr, Locale.US);
        this.periodType = periodType;
    }

    @Override
    synchronized public final List<Period> generatePeriods(int start, int end) throws RuntimeException {
        this.calendar = (Calendar) initialCalendar.clone();

        if (start >= end) {
            return Collections.emptyList();
        }

        List<Period> periods = new ArrayList<>();
        setCalendarToStartTimeOfADay(calendar);
        moveToStartOfCurrentPeriod();
        moveToTheFirstPeriod(start);

        for (int i = 0; i < end - start; i++) {
            Period period = generatePeriod(calendar.getTime(), 0);
            periods.add(period);

            calendar.setTime(period.startDate());
            movePeriods(1);
        }
        return periods;
    }

    @Override
    synchronized public final Period generatePeriod(Date date, int periodOffset) {
        this.calendar = (Calendar) initialCalendar.clone();

        moveToStartOfThePeriodOfADayWithOffset(date, periodOffset);

        Date startDate = calendar.getTime();
        String periodId = generateId();
        this.movePeriods(1);
        calendar.add(Calendar.MILLISECOND, -1);
        Date endDate = calendar.getTime();

        moveToStartOfThePeriodOfADayWithOffset(date, periodOffset);

        return Period.builder()
                .periodType(periodType)
                .startDate(startDate)
                .periodId(periodId)
                .endDate(endDate)
                .build();
    }

    @Override
    synchronized public List<Period> generatePeriodsInYear(int yearOffset) {
        this.calendar = (Calendar) initialCalendar.clone();

        int targetYear = calendar.get(Calendar.YEAR) + yearOffset;
        calendar.set(Calendar.YEAR, targetYear);
        setCalendarToStartTimeOfADay(calendar);
        moveToStartOfCurrentYear();

        List<Period> periods = new ArrayList<>();
        Period period;

        while (true) {
            period = generatePeriod(calendar.getTime(), 0);

            if (period.periodId() != null &&
                    period.periodId().startsWith(Integer.toString(targetYear))) {
                periods.add(period);
                movePeriods(1);
            } else {
                break;
            }
        }

        return periods;
    }

    private void moveToTheFirstPeriod(int start) {
        int periods = 0;
        while (periods < Math.abs(start)) {
            Period period = generatePeriod(calendar.getTime(), 0);
            if (start > 0) {
                calendar.setTime(period.startDate());
                movePeriods(1);
            } else {
                calendar.setTime(period.startDate());
                calendar.add(Calendar.MILLISECOND, -1);
            }
            periods++;
        }
    }

    private void moveToStartOfThePeriodOfADayWithOffset(Date date, int periodOffset) {
        this.calendar.setTime(date);
        setCalendarToStartTimeOfADay(calendar);
        moveToStartOfCurrentPeriod();
        this.movePeriods(periodOffset);
    }

    static void setCalendarToStartTimeOfADay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    protected abstract void moveToStartOfCurrentPeriod();

    protected abstract void movePeriods(int number);

    protected String generateId() {
        return idFormatter.format(calendar.getTime());
    }

    protected abstract void moveToStartOfCurrentYear();
}