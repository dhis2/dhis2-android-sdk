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

package org.hisp.dhis.android.core.common;

import org.hisp.dhis.android.core.period.PeriodType;

public enum RelativePeriod {
    TODAY(PeriodType.Daily, 0, 0),
    YESTERDAY(PeriodType.Daily, -1, -1),
    LAST_3_DAYS(PeriodType.Daily, -3, 0),
    LAST_7_DAYS(PeriodType.Daily, -7, 0),
    LAST_14_DAYS(PeriodType.Daily, -14, 0),
    LAST_30_DAYS(PeriodType.Daily, -30, 0),
    LAST_60_DAYS(PeriodType.Daily, -60, 0),
    LAST_90_DAYS(PeriodType.Daily, -90, 0),
    LAST_180_DAYS(PeriodType.Daily, -180, 0),
    THIS_MONTH(PeriodType.Monthly, 0, 0),
    LAST_MONTH(PeriodType.Monthly, -1, -1),
    THIS_BIMONTH(PeriodType.BiMonthly, 0, 0),
    LAST_BIMONTH(PeriodType.BiMonthly, -1, -1),
    THIS_QUARTER(PeriodType.Quarterly, 0, 0),
    LAST_QUARTER(PeriodType.Quarterly, -1, -1),
    THIS_SIX_MONTH(PeriodType.SixMonthly, 0, 0),
    LAST_SIX_MONTH(PeriodType.SixMonthly, -1, -1),
    WEEKS_THIS_YEAR(PeriodType.Weekly, null, null, true, false),
    MONTHS_THIS_YEAR(PeriodType.Monthly, null, null, true, false),
    BIMONTHS_THIS_YEAR(PeriodType.BiMonthly, null, null, true, false),
    QUARTERS_THIS_YEAR(PeriodType.Quarterly, null, null, true, false),
    THIS_YEAR(PeriodType.Yearly, 0, 0),
    MONTHS_LAST_YEAR(PeriodType.Monthly, null, null, false, true),
    QUARTERS_LAST_YEAR(PeriodType.Quarterly, null, null, false, true),
    LAST_YEAR(PeriodType.Yearly, -1, -1),
    LAST_5_YEARS(PeriodType.Yearly, -5, 0),
    LAST_12_MONTHS(PeriodType.Monthly, -12, 0),
    LAST_6_MONTHS(PeriodType.Monthly, -6, 0),
    LAST_3_MONTHS(PeriodType.Monthly, -3, 0),
    LAST_6_BIMONTHS(PeriodType.BiMonthly, -6, 0),
    LAST_4_QUARTERS(PeriodType.Quarterly, -4, 0),
    LAST_2_SIXMONTHS(PeriodType.SixMonthly, -2, 0),
    THIS_FINANCIAL_YEAR(PeriodType.FinancialApril, 0, 0),
    LAST_FINANCIAL_YEAR(PeriodType.FinancialApril, -1, -1),
    LAST_5_FINANCIAL_YEARS(PeriodType.FinancialApril, -5, 0),
    THIS_WEEK(PeriodType.Weekly, 0, 0),
    LAST_WEEK(PeriodType.Weekly, -1, -1),
    THIS_BIWEEK(PeriodType.BiWeekly, 0, 0),
    LAST_BIWEEK(PeriodType.BiWeekly, -1, -1),
    LAST_4_WEEKS(PeriodType.Weekly, -4, 0),
    LAST_4_BIWEEKS(PeriodType.BiWeekly, -4, 0),
    LAST_12_WEEKS(PeriodType.Weekly, -12, 0),
    LAST_52_WEEKS(PeriodType.Weekly, -52, 0);

    private final PeriodType periodType;

    private final Integer start;

    private final Integer end;

    private final Boolean periodsThisYear;

    private final Boolean periodsLastYear;

    RelativePeriod(PeriodType periodType,
                   Integer start,
                   Integer end,
                   Boolean periodsThisYear,
                   Boolean periodsLastYear) {
        this.periodType = periodType;
        this.start = start;
        this.end = end;
        this.periodsThisYear = periodsThisYear;
        this.periodsLastYear = periodsLastYear;
    }

    RelativePeriod(PeriodType periodType, Integer start, Integer end) {
        this(periodType, start, end, false, false);
    }

    PeriodType getPeriodType() {
        return periodType;
    }

    Integer getStart() {
        return this.start;
    }

    Integer getEnd() {
        return this.end;
    }

    Boolean getPeriodsThisYear() {
        return this.periodsThisYear;
    }

    Boolean getPeriodsLastYear() {
        return this.periodsLastYear;
    }
}