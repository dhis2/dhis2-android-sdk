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

final class PeriodGeneratorImpl implements PeriodGenerator {

    private final DailyPeriodGenerator dailyPeriodGenerator;
    private final WeeklyPeriodGenerator weeklyPeriodGenerator;
    private final WeeklyPeriodGenerator weeklyWednesdayPeriodGenerator;
    private final WeeklyPeriodGenerator weeklyThursdayPeriodGenerator;
    private final WeeklyPeriodGenerator weeklySaturdayPeriodGenerator;
    private final WeeklyPeriodGenerator weeklySundayPeriodGenerator;
    private final MonthlyPeriodGenerator monthlyPeriodGenerator;
    private final NMonthlyPeriodGenerator biMonthlyPeriodGenerator;
    private final NMonthlyPeriodGenerator quarterPeriodGenerator;
    private final SixMonthlyPeriodGenerator sixMonthlyPeriodGenerator;
    private final SixMonthlyPeriodGenerator sixMonthlyAprilPeriodGenerator;
    private final YearlyPeriodGenerator yearlyPeriodGenerator;
    private final YearlyPeriodGenerator financialAprilPeriodGenerator;
    private final YearlyPeriodGenerator financialJulyPeriodGenerator;
    private final YearlyPeriodGenerator financialOctPeriodGenerator;

    PeriodGeneratorImpl(DailyPeriodGenerator dailyPeriodGenerator,
                        WeeklyPeriodGenerator weeklyPeriodGenerator,
                        WeeklyPeriodGenerator weeklyWednesdayPeriodGenerator,
                        WeeklyPeriodGenerator weeklyThursdayPeriodGenerator,
                        WeeklyPeriodGenerator weeklySaturdayPeriodGenerator,
                        WeeklyPeriodGenerator weeklySundayPeriodGenerator,
                        MonthlyPeriodGenerator monthlyPeriodGenerator,
                        NMonthlyPeriodGenerator biMonthlyPeriodGenerator,
                        NMonthlyPeriodGenerator quarterPeriodGenerator,
                        SixMonthlyPeriodGenerator sixMonthlyPeriodGenerator,
                        SixMonthlyPeriodGenerator sixMonthlyAprilPeriodGenerator,
                        YearlyPeriodGenerator yearlyPeriodGenerator,
                        YearlyPeriodGenerator financialAprilPeriodGenerator,
                        YearlyPeriodGenerator financialJulyPeriodGenerator,
                        YearlyPeriodGenerator financialOctPeriodGenerator) {
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

    static PeriodGeneratorImpl create() {
        Calendar calendar = Calendar.getInstance();
        return new PeriodGeneratorImpl(
                new DailyPeriodGenerator(calendar),
                new WeeklyPeriodGenerator(calendar, PeriodType.Weekly, Calendar.MONDAY, "W"),
                new WeeklyPeriodGenerator(calendar, PeriodType.WeeklyWednesday, Calendar.WEDNESDAY, "WedW"),
                new WeeklyPeriodGenerator(calendar, PeriodType.WeeklyThursday, Calendar.THURSDAY, "ThuW"),
                new WeeklyPeriodGenerator(calendar, PeriodType.WeeklySaturday, Calendar.SATURDAY, "SatW"),
                new WeeklyPeriodGenerator(calendar, PeriodType.WeeklySunday, Calendar.SUNDAY, "SunW"),
                new MonthlyPeriodGenerator(calendar),
                NMonthlyPeriodGenerator.biMonthly(calendar),
                NMonthlyPeriodGenerator.quarter(calendar),
                new SixMonthlyPeriodGenerator(calendar, PeriodType.SixMonthly, "", Calendar.JANUARY),
                new SixMonthlyPeriodGenerator(calendar, PeriodType.SixMonthlyApril, "", Calendar.APRIL),
                new YearlyPeriodGenerator(calendar, PeriodType.Yearly, Calendar.JANUARY, ""),
                new YearlyPeriodGenerator(calendar, PeriodType.FinancialApril, Calendar.APRIL, "April"),
                new YearlyPeriodGenerator(calendar, PeriodType.FinancialJuly, Calendar.JULY, "July"),
                new YearlyPeriodGenerator(calendar, PeriodType.FinancialOct, Calendar.OCTOBER, "Oct")
        );
    }
}
