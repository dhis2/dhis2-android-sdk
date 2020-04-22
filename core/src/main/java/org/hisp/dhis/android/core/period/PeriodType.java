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

package org.hisp.dhis.android.core.period;

import java.util.Calendar;

public enum PeriodType {
    Daily(59, 1, "\\b(\\d{4})(\\d{2})(\\d{2})\\b"),
    Weekly(12, 1, "\\b(\\d{4})W(\\d[\\d]?)\\b"),
    WeeklyWednesday(12, 1, "\\b(\\d{4})WedW(\\d[\\d]?)\\b"),
    WeeklyThursday(12, 1, "\\b(\\d{4})ThuW(\\d[\\d]?)\\b"),
    WeeklySaturday(12, 1, "\\b(\\d{4})SatW(\\d[\\d]?)\\b"),
    WeeklySunday(12, 1, "\\b(\\d{4})SunW(\\d[\\d]?)\\b"),
    BiWeekly(12, 1, "\\b(\\d{4})BiW(\\d[\\d]?)\\b"),
    Monthly(11, 1, "\\b(\\d{4})[-]?(\\d{2})\\b"),
    BiMonthly(5, 1, "\\b(\\d{4})(\\d{2})B\\b"),
    Quarterly(4, 1, "\\b(\\d{4})Q(\\d)\\b"),
    SixMonthly(4, 1, "\\b(\\d{4})S(\\d)\\b"),
    SixMonthlyApril(4, 1, "\\b(\\d{4})AprilS(\\d)\\b"),
    SixMonthlyNov(4, 1, "\\b(\\d{4})NovS(\\d)\\b"),
    Yearly(4, 1, "\\b(\\d{4})\\b"),
    FinancialApril(4, 1, "\\b(\\d{4})April\\b"),
    FinancialJuly(4, 1, "\\b(\\d{4})July\\b"),
    FinancialOct(4, 1, "\\b(\\d{4})Oct\\b"),
    FinancialNov(4, 1, "\\b(\\d{4})Nov\\b");

    private Integer defaultPastPeriods;

    private Integer defaultFuturePeriods;

    private String pattern;

    PeriodType(Integer defaultPastPeriods, Integer defaultFuturePeriods, String pattern) {
        this.defaultPastPeriods = defaultPastPeriods;
        this.defaultFuturePeriods = defaultFuturePeriods;
        this.pattern = pattern;
    }

    public Integer getDefaultPastPeriods() {
        return this.defaultPastPeriods;
    }

    public Integer getDefaultFuturePeriods() {
        return this.defaultFuturePeriods;
    }

    public String getPattern() {
        return this.pattern;
    }

    public static PeriodType periodTypeFromPeriodId(String periodId) throws IllegalArgumentException {
        for (PeriodType type : PeriodType.values()) {
            if (periodId.matches(type.pattern)) {
                return type;
            }
        }

        throw new IllegalArgumentException("The period id does not match any period type");
    }

    public static Integer firstDayOfTheWeek(PeriodType periodType) {
        switch (periodType) {
            case WeeklySunday:
                return Calendar.SUNDAY;
            case WeeklyWednesday:
                return Calendar.WEDNESDAY;
            case WeeklyThursday:
                return Calendar.THURSDAY;
            case WeeklySaturday:
                return Calendar.SATURDAY;
            default:
                return Calendar.MONDAY;
        }
    }
}