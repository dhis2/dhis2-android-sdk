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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;
import javax.inject.Singleton;

@SuppressWarnings({
        "PMD.CyclomaticComplexity",
        "PMD.StdCyclomaticComplexity"
})
@Singleton
public class PeriodParser {

    @Inject
    PeriodParser() { }

    public Date parse(@NonNull String periodId) throws IllegalArgumentException {
        PeriodType periodType = PeriodType.periodTypeFromPeriodId(periodId);
        return parse(periodId, periodType);
    }

    public Date parse(@NonNull String periodId, @NonNull PeriodType periodType) throws IllegalArgumentException {
        Matcher matcher = getMatcherFromPeriodId(periodId, periodType);
        Date date = getDateFromPeriodId(matcher, periodType);
        if (date == null) {
            throw new IllegalArgumentException(
                    "It has not been possible to generate a date for the given periodId.");
        } else {
            return date;
        }
    }

    private Matcher getMatcherFromPeriodId(String periodId, PeriodType periodType) {
        Pattern pattern = Pattern.compile(periodType.getPattern());
        Matcher matcher = pattern.matcher(periodId);
        boolean match = matcher.find();

        if (!match) {
            throw new IllegalArgumentException(
                    "It has not been possible to generate a match for the period pattern.");
        }

        return matcher;
    }

    private Date getDateFromPeriodId(Matcher matcher, PeriodType periodType) {
        int year = Integer.parseInt(matcher.group(1));
        int month;
        int semester;
        int week;

        switch (periodType) {
            case Daily:
                month = Integer.parseInt(matcher.group(2));
                int day = Integer.parseInt(matcher.group(3));

                LocalDate localDate = LocalDate.of(year, month, day);
                return localDateToDate(localDate);
            case Weekly:
            case WeeklyWednesday:
            case WeeklyThursday:
            case WeeklySaturday:
            case WeeklySunday:
                week = Integer.parseInt(matcher.group(2));
                return getDateTimeFromWeek(year, week, PeriodType.firstDayOfTheWeek(periodType));
            case BiWeekly:
                week = Integer.parseInt(matcher.group(2)) * 2 - 1;
                return getDateTimeFromWeek(year, week, PeriodType.firstDayOfTheWeek(periodType));
            case Monthly:
                month = Integer.parseInt(matcher.group(2));
                return getDateTimeFromMonth(year, Month.of(month));
            case BiMonthly:
                int biMonth = Integer.parseInt(matcher.group(2));
                return getDateTimeFromMonth(year, Month.of(biMonth  * 2 - 1));
            case Quarterly:
                int quarter = Integer.parseInt(matcher.group(2));
                return getDateTimeFromMonth(year, Month.of(quarter * 3 - 2));
            case SixMonthly:
                semester = Integer.parseInt(matcher.group(2));
                return getDateTimeFromMonth(year, Month.of(semester * 6 - 5));
            case SixMonthlyApril:
                semester = Integer.parseInt(matcher.group(2));
                return getDateTimeFromMonth(year, Month.of(semester * 6 - 2));
            case SixMonthlyNov:
                semester = Integer.parseInt(matcher.group(2));
                return getDateTimeFromMonth(semester == 1 ? year - 1 : year,
                        semester == 1 ? Month.NOVEMBER : Month.MAY);
            case Yearly:
                return getDateTimeFromMonth(year, Month.JANUARY);
            case FinancialApril:
                return getDateTimeFromMonth(year, Month.APRIL);
            case FinancialJuly:
                return getDateTimeFromMonth(year, Month.JULY);
            case FinancialOct:
                return getDateTimeFromMonth(year, Month.OCTOBER);
            case FinancialNov:
                return getDateTimeFromMonth(year - 1, Month.NOVEMBER);
            default:
                return null;
        }
    }

    /**
     * returns a date based on a week number
     *
     * @param year           The year of the date
     * @param week           The week of the date
     * @param firstDayOfWeek The first day of the week
     * @return The Date of the week
     */
    private Date getDateTimeFromWeek(int year, int week, DayOfWeek firstDayOfWeek)
            throws IllegalArgumentException {
        if (week < 1 || week > 53) {
            throw new IllegalArgumentException("The week number is outside the year week range.");
        }

        WeekFields weekFields = WeekFields.of(firstDayOfWeek, 4);

        LocalDate localDate = LocalDate.now()
                .withYear(year)
                .with(weekFields.weekOfYear(), week)
                .with(weekFields.dayOfWeek(), 2);

        return localDateToDate(localDate);
    }

    /**
     * returns a date based on a month number
     *
     * @param year              The year of the date
     * @param month             The month of the date
     * @return The first Date of the month
     */
    private Date getDateTimeFromMonth(int year, Month month) throws IllegalArgumentException {
        LocalDate localDate = LocalDate.of(year, month, 2);

        return localDateToDate(localDate);
    }

    private Date localDateToDate(LocalDate localDate) {
        ZoneOffset defaultZoneId = ZoneId.systemDefault().getRules().getOffset(LocalDateTime.now());
        return Date.from(localDate.atTime(10, 0).toInstant(defaultZoneId));
    }
}
