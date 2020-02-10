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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class ParentPeriodGeneratorImpl implements ParentPeriodGenerator {

    private final PeriodGenerator daily;
    private final WeeklyPeriodGenerators weekly;
    private final PeriodGenerator biWeekly;
    private final PeriodGenerator monthly;
    private final NMonthlyPeriodGenerators nMonthly;
    private final YearlyPeriodGenerators yearly;

    final Map<PeriodType, Integer> past;
    final Map<PeriodType, Integer> future;

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

        this.past = getDefaultPastPeriods();
        this.future = getDefaultFuturePeriods();
    }

    private Map<PeriodType, Integer> getDefaultPastPeriods() {
        Map<PeriodType, Integer> past = new HashMap<>();
        past.put(PeriodType.Daily, 59);
        past.put(PeriodType.Weekly, 12);
        past.put(PeriodType.WeeklySaturday, 12);
        past.put(PeriodType.WeeklySunday, 12);
        past.put(PeriodType.WeeklyThursday, 12);
        past.put(PeriodType.WeeklyWednesday, 12);
        past.put(PeriodType.BiWeekly, 12);
        past.put(PeriodType.Monthly, 11);
        past.put(PeriodType.BiMonthly, 5);
        past.put(PeriodType.Quarterly, 4);
        past.put(PeriodType.SixMonthly, 4);
        past.put(PeriodType.SixMonthlyApril, 4);
        past.put(PeriodType.SixMonthlyNov, 4);
        past.put(PeriodType.Yearly, 4);
        past.put(PeriodType.FinancialApril, 4);
        past.put(PeriodType.FinancialJuly, 4);
        past.put(PeriodType.FinancialOct, 4);
        past.put(PeriodType.FinancialNov, 4);
        return past;
    }

    private Map<PeriodType, Integer> getDefaultFuturePeriods() {
        Map<PeriodType, Integer> future = new HashMap<>();
        future.put(PeriodType.Daily, 1);
        future.put(PeriodType.Weekly, 1);
        future.put(PeriodType.WeeklySaturday, 1);
        future.put(PeriodType.WeeklySunday, 1);
        future.put(PeriodType.WeeklyThursday, 1);
        future.put(PeriodType.WeeklyWednesday, 1);
        future.put(PeriodType.BiWeekly, 1);
        future.put(PeriodType.Monthly, 1);
        future.put(PeriodType.BiMonthly, 1);
        future.put(PeriodType.Quarterly, 1);
        future.put(PeriodType.SixMonthly, 1);
        future.put(PeriodType.SixMonthlyApril, 1);
        future.put(PeriodType.SixMonthlyNov, 1);
        future.put(PeriodType.Yearly, 1);
        future.put(PeriodType.FinancialApril, 1);
        future.put(PeriodType.FinancialJuly, 1);
        future.put(PeriodType.FinancialOct, 1);
        future.put(PeriodType.FinancialNov, 1);
        return future;
    }

    public List<Period> generatePeriods() {
        List<Period> periods = new ArrayList<>();

        for (PeriodType periodType : PeriodType.values()) {
            PeriodGenerator periodGenerator = getPeriodGenerator(periodType);
            if (periodGenerator != null) {
                List<Period> ps = periodGenerator.generatePeriods(getPast(periodType), getFuture(periodType));
                periods.addAll(ps);
            }
        }

        return periods;
    }

    public List<Period> generatePeriods(PeriodType periodType, int futurePeriods) {
        PeriodGenerator periodGenerator = getPeriodGenerator(periodType);

        if (periodGenerator == null) {
            return Collections.emptyList();
        } else {
            return periodGenerator.generatePeriods(getPast(periodType), futurePeriods);
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

    private int getPast(PeriodType periodType) {
        Integer periods = past.get(periodType);
        return periods == null ? 0 : periods;
    }

    private int getFuture(PeriodType periodType) {
        Integer periods = future.get(periodType);
        return periods == null ? 0 : periods;
    }

    static ParentPeriodGeneratorImpl create() {
        Calendar calendar = Calendar.getInstance();
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