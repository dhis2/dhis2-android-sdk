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

import org.hisp.dhis.android.core.period.Period;
import org.hisp.dhis.android.core.period.PeriodType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

class ParentPeriodGeneratorImpl implements ParentPeriodGenerator {

    private final PeriodGenerator daily;
    private final WeeklyPeriodGenerators weekly;
    private final PeriodGenerator biWeekly;
    private final PeriodGenerator monthly;
    private final NMonthlyPeriodGenerators nMonthly;
    private final YearlyPeriodGenerators yearly;

    ParentPeriodGeneratorImpl(PeriodGenerator daily,
                              WeeklyPeriodGenerators weekly,
                              PeriodGenerator biWeekly,
                              PeriodGenerator monthly,
                              NMonthlyPeriodGenerators nMonthly,
                              YearlyPeriodGenerators yearly) {
        this.daily = daily;
        this.weekly = weekly;
        this.biWeekly = biWeekly;
        this.monthly = monthly;
        this.nMonthly = nMonthly;
        this.yearly = yearly;
    }

    public List<Period> generatePeriods() {
        List<Period> periods = new ArrayList<>();

        for (PeriodType periodType : PeriodType.values()) {
            List<Period> periodsInType = generatePeriods(periodType, periodType.getDefaultFuturePeriods());
            periods.addAll(periodsInType);
        }

        return periods;
    }

    public List<Period> generatePeriods(PeriodType periodType, int futurePeriods) {
        return generatePeriods(periodType, periodType.getDefaultPastPeriods(), futurePeriods);
    }

    public List<Period> generatePeriods(PeriodType periodType, int pastPeriods, int futurePeriods) {
        PeriodGenerator periodGenerator = getPeriodGenerator(periodType);

        if (periodGenerator == null) {
            return Collections.emptyList();
        } else {
            return periodGenerator.generatePeriods(pastPeriods, futurePeriods);
        }
    }

    public Period generatePeriod(PeriodType periodType, Date date) {
        PeriodGenerator periodGenerator = getPeriodGenerator(periodType);

        if (periodGenerator == null) {
            return null;
        } else {
            return periodGenerator.generatePeriod(date);
        }
    }

    @SuppressWarnings({
            "PMD.CyclomaticComplexity",
            "PMD.ModifiedCyclomaticComplexity",
            "PMD.StdCyclomaticComplexity"
    })
    private PeriodGenerator getPeriodGenerator(PeriodType periodType) {
        if (periodType == PeriodType.Daily) {
            return daily;
        } else if (periodType == PeriodType.Weekly) {
            return weekly.weekly;
        } else if (periodType == PeriodType.WeeklyWednesday) {
            return weekly.weeklyWednesday;
        } else if (periodType == PeriodType.WeeklyThursday) {
            return weekly.weeklyThursday;
        } else if (periodType == PeriodType.WeeklySaturday) {
            return weekly.weeklySaturday;
        } else if (periodType == PeriodType.WeeklySunday) {
            return weekly.weeklySunday;
        } else if (periodType == PeriodType.BiWeekly) {
            return biWeekly;
        } else if (periodType == PeriodType.Monthly) {
            return monthly;
        } else if (periodType == PeriodType.BiMonthly) {
            return nMonthly.biMonthly;
        } else if (periodType == PeriodType.Quarterly) {
            return nMonthly.quarter;
        } else if (periodType == PeriodType.SixMonthly) {
            return nMonthly.sixMonthly;
        } else if (periodType == PeriodType.SixMonthlyApril) {
            return nMonthly.sixMonthlyApril;
        } else if (periodType == PeriodType.SixMonthlyNov) {
            return nMonthly.sixMonthlyNov;
        } else if (periodType == PeriodType.Yearly) {
            return yearly.yearly;
        } else if (periodType == PeriodType.FinancialApril) {
            return yearly.financialApril;
        } else if (periodType == PeriodType.FinancialJuly) {
            return yearly.financialJuly;
        } else if (periodType == PeriodType.FinancialOct) {
            return yearly.financialOct;
        } else if (periodType == PeriodType.FinancialNov) {
            return yearly.financialNov;
        } else {
            return null;
        }
    }

    static ParentPeriodGeneratorImpl create(CalendarProvider calendarProvider) {
        Calendar calendar = calendarProvider.getCalendar();
        return new ParentPeriodGeneratorImpl(
                new DailyPeriodGenerator(calendar),
                WeeklyPeriodGenerators.create(calendar),
                new BiWeeklyPeriodGenerator(calendar),
                new MonthlyPeriodGenerator(calendar),
                NMonthlyPeriodGenerators.create(calendar),
                YearlyPeriodGenerators.create(calendar)
        );
    }
}