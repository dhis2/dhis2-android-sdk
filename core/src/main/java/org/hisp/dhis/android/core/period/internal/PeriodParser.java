/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.period.internal;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.period.PeriodType;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PeriodParser {

    private final CalendarProvider calendarProvider;

    @Inject
    PeriodParser(CalendarProvider calendarProvider) {
        this.calendarProvider = calendarProvider;
    }

    public Date parse(@NonNull String periodId) throws IllegalArgumentException {
        PeriodType periodType = PeriodType.periodTypeFromPeriodId(periodId);
        return parse(periodId, periodType);
    }

    public Date parse(@NonNull String periodId, @NonNull PeriodType periodType) throws IllegalArgumentException {
        Calendar calendar = calendarProvider.getCalendar();

        Pattern pattern = Pattern.compile(periodType.getPattern());
        Matcher matcher = pattern.matcher(periodId);
        Date date = new Date();
        boolean match = matcher.find();

        if (!match) {
            return null;
        }

        if (PeriodType.Daily == periodType) {
            int year = Integer.parseInt(matcher.group(1));
            int month = Integer.parseInt(matcher.group(2));
            int day = Integer.parseInt(matcher.group(3));

            calendar.set(year, month - 1, day);
            date = calendar.getTime();

        } else if (PeriodType.Weekly == periodType ||
                PeriodType.WeeklyWednesday == periodType ||
                PeriodType.WeeklyThursday == periodType ||
                PeriodType.WeeklySaturday == periodType ||
                PeriodType.WeeklySunday == periodType) {
            int year = Integer.parseInt(matcher.group(1));
            int week = Integer.parseInt(matcher.group(2));

            date = getDateTimeFromWeek(year, week, calendar, PeriodType.firstDayOfTheWeek(periodType));

        } else if (PeriodType.BiWeekly == periodType) {
            int year = Integer.parseInt(matcher.group(1));
            int week = Integer.parseInt(matcher.group(2)) * 2 - 1;

            date = getDateTimeFromWeek(year, week, calendar, PeriodType.firstDayOfTheWeek(periodType));

        } else if (PeriodType.Monthly == periodType) {
            int year = Integer.parseInt(matcher.group(1));
            int month = Integer.parseInt(matcher.group(2));

            date = getDateTimeFromMonth(year, month - 1, calendar);

        } else if (PeriodType.BiMonthly == periodType) {
            int year = Integer.parseInt(matcher.group(1));
            int biMonth = Integer.parseInt(matcher.group(2));

            date = getDateTimeFromMonth(year, (biMonth * 2) - 2, calendar);

        } else if (PeriodType.Quarterly == periodType) {
            int year = Integer.parseInt(matcher.group(1));
            int quarter = Integer.parseInt(matcher.group(2));

            date = getDateTimeFromMonth(year, (quarter * 3) - 3, calendar);

        } else if (PeriodType.SixMonthly == periodType) {
            int year = Integer.parseInt(matcher.group(1));
            int semester = Integer.parseInt(matcher.group(2));

            date = getDateTimeFromMonth(year, (semester * 6) - 6, calendar);

        } else if (PeriodType.SixMonthlyApril == periodType) {
            int year = Integer.parseInt(matcher.group(1));
            int semester = Integer.parseInt(matcher.group(2));

            date = getDateTimeFromMonth(year, (semester * 6) - 3, calendar);

        } else if (PeriodType.SixMonthlyNov == periodType) {
            int year = Integer.parseInt(matcher.group(1));
            int semester = Integer.parseInt(matcher.group(2));

            date = getDateTimeFromMonth(semester == 1 ? year - 1 : year,
                    semester == 1 ? Calendar.NOVEMBER :
                    semester == 2 ? Calendar.MAY : -1, calendar);

        } else if (PeriodType.Yearly == periodType) {
            int year = Integer.parseInt(matcher.group(1));

            date = getDateTimeFromMonth(year, Calendar.JANUARY, calendar);

        } else if (PeriodType.FinancialApril == periodType) {
            int year = Integer.parseInt(matcher.group(1));

            date = getDateTimeFromMonth(year, Calendar.APRIL, calendar);

        } else if (PeriodType.FinancialJuly == periodType) {
            int year = Integer.parseInt(matcher.group(1));

            date = getDateTimeFromMonth(year, Calendar.JULY, calendar);

        } else if (PeriodType.FinancialOct == periodType) {
            int year = Integer.parseInt(matcher.group(1));

            date = getDateTimeFromMonth(year, Calendar.OCTOBER, calendar);

        } else if (PeriodType.FinancialNov == periodType) {
            int year = Integer.parseInt(matcher.group(1));

            date = getDateTimeFromMonth(year - 1, Calendar.NOVEMBER, calendar);
        }

        if (date == null) {
            throw new IllegalArgumentException(
                    "It has not been possible to generate a date for the given periodId.");
        } else {
            return date;
        }
    }

    /**
     * returns a date based on a week number
     *
     * @param year           The year of the date
     * @param week           The week of the date
     * @param calendar       The calendar used to calculate the date
     * @param firstDayOfWeek The first day of the week
     * @return The Date of the week
     */
    private Date getDateTimeFromWeek(int year, int week, Calendar calendar, Integer firstDayOfWeek)
            throws IllegalArgumentException {
        if (week < 1 || week > 53) {
            throw new IllegalArgumentException("The week number is outside the year week range.");
        }

        calendar.setFirstDayOfWeek(firstDayOfWeek);
        calendar.setMinimalDaysInFirstWeek(4);
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.WEEK_OF_YEAR, week);
        calendar.set(Calendar.DAY_OF_WEEK, firstDayOfWeek);
        calendar.set(Calendar.HOUR_OF_DAY, 10);

        return calendar.getTime();
    }

    /**
     * returns a date based on a month number
     *
     * @param year              The year of the date
     * @param month             The month of the date
     * @param calendar          The calendar used to calculate the date
     * @return The first Date of the month
     */
    private Date getDateTimeFromMonth(int year, int month, Calendar calendar) throws IllegalArgumentException {
        if (month < 0 || month > 11) {
            throw new IllegalArgumentException("The periodId does not match a real date.");
        }

        calendar.set(year, month, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 10);

        return calendar.getTime();
    }
}
