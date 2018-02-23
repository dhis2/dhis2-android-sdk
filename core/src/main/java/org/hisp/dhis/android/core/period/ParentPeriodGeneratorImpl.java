/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

final class ParentPeriodGeneratorImpl implements ParentPeriodGenerator {

    private final PeriodGenerator dailyPeriodGenerator;
    private final PeriodGenerator weeklyPeriodGenerator;
    private final PeriodGenerator weeklyWednesdayPeriodGenerator;
    private final PeriodGenerator weeklyThursdayPeriodGenerator;
    private final PeriodGenerator weeklySaturdayPeriodGenerator;
    private final PeriodGenerator weeklySundayPeriodGenerator;
    private final PeriodGenerator monthlyPeriodGenerator;
    private final PeriodGenerator biMonthlyPeriodGenerator;
    private final PeriodGenerator quarterPeriodGenerator;
    private final PeriodGenerator sixMonthlyPeriodGenerator;
    private final PeriodGenerator sixMonthlyAprilPeriodGenerator;
    private final PeriodGenerator yearlyPeriodGenerator;
    private final PeriodGenerator financialAprilPeriodGenerator;
    private final PeriodGenerator financialJulyPeriodGenerator;
    private final PeriodGenerator financialOctPeriodGenerator;

    ParentPeriodGeneratorImpl(PeriodGenerator dailyPeriodGenerator,
                              PeriodGenerator weeklyPeriodGenerator,
                              PeriodGenerator weeklyWednesdayPeriodGenerator,
                              PeriodGenerator weeklyThursdayPeriodGenerator,
                              PeriodGenerator weeklySaturdayPeriodGenerator,
                              PeriodGenerator weeklySundayPeriodGenerator,
                              PeriodGenerator monthlyPeriodGenerator,
                              PeriodGenerator biMonthlyPeriodGenerator,
                              PeriodGenerator quarterPeriodGenerator,
                              PeriodGenerator sixMonthlyPeriodGenerator,
                              PeriodGenerator sixMonthlyAprilPeriodGenerator,
                              PeriodGenerator yearlyPeriodGenerator,
                              PeriodGenerator financialAprilPeriodGenerator,
                              PeriodGenerator financialJulyPeriodGenerator,
                              PeriodGenerator financialOctPeriodGenerator) {
        this.dailyPeriodGenerator = dailyPeriodGenerator;
        this.weeklyPeriodGenerator = weeklyPeriodGenerator;
        this.weeklyWednesdayPeriodGenerator = weeklyWednesdayPeriodGenerator;
        this.weeklyThursdayPeriodGenerator = weeklyThursdayPeriodGenerator;
        this.weeklySaturdayPeriodGenerator = weeklySaturdayPeriodGenerator;
        this.weeklySundayPeriodGenerator = weeklySundayPeriodGenerator;
        this.monthlyPeriodGenerator = monthlyPeriodGenerator;
        this.biMonthlyPeriodGenerator = biMonthlyPeriodGenerator;
        this.quarterPeriodGenerator = quarterPeriodGenerator;
        this.sixMonthlyPeriodGenerator = sixMonthlyPeriodGenerator;
        this.sixMonthlyAprilPeriodGenerator = sixMonthlyAprilPeriodGenerator;
        this.yearlyPeriodGenerator = yearlyPeriodGenerator;
        this.financialAprilPeriodGenerator = financialAprilPeriodGenerator;
        this.financialJulyPeriodGenerator = financialJulyPeriodGenerator;
        this.financialOctPeriodGenerator = financialOctPeriodGenerator;
    }

    public List<PeriodModel> generatePeriods() {
        List<PeriodModel> periods = new ArrayList<>();
        periods.addAll(dailyPeriodGenerator.generateLastPeriods(60));

        periods.addAll(weeklyPeriodGenerator.generateLastPeriods(13));
        periods.addAll(weeklyWednesdayPeriodGenerator.generateLastPeriods(13));
        periods.addAll(weeklyThursdayPeriodGenerator.generateLastPeriods(13));
        periods.addAll(weeklySaturdayPeriodGenerator.generateLastPeriods(13));
        periods.addAll(weeklySundayPeriodGenerator.generateLastPeriods(13));

        periods.addAll(monthlyPeriodGenerator.generateLastPeriods(12));

        periods.addAll(biMonthlyPeriodGenerator.generateLastPeriods(6));

        periods.addAll(quarterPeriodGenerator.generateLastPeriods(4));

        periods.addAll(sixMonthlyPeriodGenerator.generateLastPeriods(2));
        periods.addAll(sixMonthlyAprilPeriodGenerator.generateLastPeriods(2));

        periods.addAll(yearlyPeriodGenerator.generateLastPeriods(5));
        periods.addAll(financialAprilPeriodGenerator.generateLastPeriods(5));
        periods.addAll(financialJulyPeriodGenerator.generateLastPeriods(5));
        periods.addAll(financialOctPeriodGenerator.generateLastPeriods(5));

        return periods;
    }

    static ParentPeriodGeneratorImpl create() {
        Calendar calendar = Calendar.getInstance();
        return new ParentPeriodGeneratorImpl(
                new DailyPeriodGenerator(calendar),
                WeeklyPeriodGeneratorFactory.weekly(calendar),
                WeeklyPeriodGeneratorFactory.wednesday(calendar),
                WeeklyPeriodGeneratorFactory.thursday(calendar),
                WeeklyPeriodGeneratorFactory.saturday(calendar),
                WeeklyPeriodGeneratorFactory.sunday(calendar),
                new MonthlyPeriodGenerator(calendar),
                NMonthlyPeriodGeneratorFactory.biMonthly(calendar),
                NMonthlyPeriodGeneratorFactory.quarter(calendar),
                NMonthlyPeriodGeneratorFactory.sixMonthly(calendar),
                NMonthlyPeriodGeneratorFactory.sixMonthlyApril(calendar),
                YearlyPeriodGenerator.yearly(calendar),
                YearlyPeriodGenerator.financialApril(calendar),
                YearlyPeriodGenerator.financialJuly(calendar),
                YearlyPeriodGenerator.financialOct(calendar)
        );
    }
}
