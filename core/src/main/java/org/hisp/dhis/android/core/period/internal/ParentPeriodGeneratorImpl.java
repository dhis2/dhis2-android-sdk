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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

class ParentPeriodGeneratorImpl implements ParentPeriodGenerator {

    static final int DAILY_PERIODS = 60;
    static final int WEEKLY_PERIODS = 13;
    static final int BIWEEKLY_PERIODS = 13;
    static final int MONTHLY_PERIODS = 12;
    static final int BIMONTHLY_PERIODS = 6;
    static final int QUARTER_PERIODS = 5;
    static final int SIXMONTHLY_PERIODS = 5;
    static final int YEARLY_PERIODS = 5;

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
        periods.addAll(daily.generateLastPeriods(DAILY_PERIODS));

        periods.addAll(weekly.weekly.generateLastPeriods(WEEKLY_PERIODS));
        periods.addAll(weekly.weeklyWednesday.generateLastPeriods(WEEKLY_PERIODS));
        periods.addAll(weekly.weeklyThursday.generateLastPeriods(WEEKLY_PERIODS));
        periods.addAll(weekly.weeklySaturday.generateLastPeriods(WEEKLY_PERIODS));
        periods.addAll(weekly.weeklySunday.generateLastPeriods(WEEKLY_PERIODS));

        periods.addAll(biWeekly.generateLastPeriods(BIWEEKLY_PERIODS));

        periods.addAll(monthly.generateLastPeriods(MONTHLY_PERIODS));

        periods.addAll(nMonthly.biMonthly.generateLastPeriods(BIMONTHLY_PERIODS));
        periods.addAll(nMonthly.quarter.generateLastPeriods(QUARTER_PERIODS));
        periods.addAll(nMonthly.sixMonthly.generateLastPeriods(SIXMONTHLY_PERIODS));
        periods.addAll(nMonthly.sixMonthlyApril.generateLastPeriods(SIXMONTHLY_PERIODS));

        periods.addAll(yearly.yearly.generateLastPeriods(YEARLY_PERIODS));
        periods.addAll(yearly.financialApril.generateLastPeriods(YEARLY_PERIODS));
        periods.addAll(yearly.financialJuly.generateLastPeriods(YEARLY_PERIODS));
        periods.addAll(yearly.financialOct.generateLastPeriods(YEARLY_PERIODS));

        return periods;
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