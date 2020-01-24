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

import org.assertj.core.util.Lists;
import org.hisp.dhis.android.core.period.Period;
import org.hisp.dhis.android.core.period.PeriodType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ParentPeriodGeneratorImplShould {

    @Mock
    private PeriodGenerator dailyPeriodGenerator;

    @Mock
    private PeriodGenerator weeklyPeriodGenerator;

    @Mock
    private PeriodGenerator weeklyWednesdayPeriodGenerator;

    @Mock
    private PeriodGenerator weeklyThursdayPeriodGenerator;

    @Mock
    private PeriodGenerator weeklySaturdayPeriodGenerator;

    @Mock
    private PeriodGenerator weeklySundayPeriodGenerator;

    @Mock
    private PeriodGenerator biWeeklyPeriodGenerator;

    @Mock
    private PeriodGenerator monthlyPeriodGenerator;

    @Mock
    private PeriodGenerator quarterPeriodGenerator;

    @Mock
    private PeriodGenerator biMonthlyPeriodGenerator;

    @Mock
    private PeriodGenerator sixMonthlyPeriodGenerator;

    @Mock
    private PeriodGenerator sixMonthlyAprilPeriodGenerator;

    @Mock
    private PeriodGenerator yearlyPeriodGenerator;

    @Mock
    private PeriodGenerator financialAprilPeriodGenerator;

    @Mock
    private PeriodGenerator financialJulyPeriodGenerator;

    @Mock
    private PeriodGenerator financialOctPeriodGenerator;

    @Mock
    private Period dailyPeriod;

    @Mock
    private Period weeklyPeriod;

    @Mock
    private Period weeklyWednesdayPeriod;

    @Mock
    private Period weeklyThursdayPeriod;

    @Mock
    private Period weeklySaturdayPeriod;

    @Mock
    private Period weeklySundayPeriod;

    @Mock
    private Period biWeeklyPeriod;

    @Mock
    private Period monthlyPeriod;

    @Mock
    private Period biMonthlyPeriod;

    @Mock
    private Period quarterPeriod;

    @Mock
    private Period sixMonthlyPeriod;

    @Mock
    private Period sixMonthlyAprilPeriod;

    @Mock
    private Period yearlyPeriod;

    @Mock
    private Period financialAprilPeriod;

    @Mock
    private Period financialJulyPeriod;

    @Mock
    private Period financialOctPeriod;

    // object to test
    private ParentPeriodGeneratorImpl periodGenerator;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        WeeklyPeriodGenerators weeklyPeriodGenerators = new WeeklyPeriodGenerators(weeklyPeriodGenerator,
                weeklyWednesdayPeriodGenerator, weeklyThursdayPeriodGenerator, weeklySaturdayPeriodGenerator,
                weeklySundayPeriodGenerator);
        NMonthlyPeriodGenerators nMonthlyPeriodGenerators = new NMonthlyPeriodGenerators(biMonthlyPeriodGenerator,
                quarterPeriodGenerator, sixMonthlyPeriodGenerator, sixMonthlyAprilPeriodGenerator);
        YearlyPeriodGenerators yearlyPeriodGenerators = new YearlyPeriodGenerators(yearlyPeriodGenerator,
                financialAprilPeriodGenerator, financialJulyPeriodGenerator, financialOctPeriodGenerator);

        periodGenerator = new ParentPeriodGeneratorImpl(dailyPeriodGenerator, weeklyPeriodGenerators,
                biWeeklyPeriodGenerator, monthlyPeriodGenerator, nMonthlyPeriodGenerators, yearlyPeriodGenerators);
        
        mockGenerator(dailyPeriodGenerator, getPast(PeriodType.Daily), getFuture(PeriodType.Daily), dailyPeriod);
        mockGenerator(weeklyPeriodGenerator, getPast(PeriodType.Weekly), getFuture(PeriodType.Weekly), weeklyPeriod);
        mockGenerator(weeklyWednesdayPeriodGenerator, getPast(PeriodType.WeeklyWednesday), getFuture(PeriodType.WeeklyWednesday), weeklyWednesdayPeriod);
        mockGenerator(weeklyThursdayPeriodGenerator, getPast(PeriodType.WeeklyThursday), getFuture(PeriodType.WeeklyThursday), weeklyThursdayPeriod);
        mockGenerator(weeklySaturdayPeriodGenerator, getPast(PeriodType.WeeklySaturday), getFuture(PeriodType.WeeklySaturday), weeklySaturdayPeriod);
        mockGenerator(weeklySundayPeriodGenerator, getPast(PeriodType.WeeklySunday), getFuture(PeriodType.WeeklySunday), weeklySundayPeriod);
        mockGenerator(biWeeklyPeriodGenerator, getPast(PeriodType.BiWeekly), getFuture(PeriodType.BiWeekly), biWeeklyPeriod);
        mockGenerator(monthlyPeriodGenerator, getPast(PeriodType.Monthly), getFuture(PeriodType.Monthly), monthlyPeriod);
        mockGenerator(biMonthlyPeriodGenerator, getPast(PeriodType.BiMonthly), getFuture(PeriodType.BiMonthly), biMonthlyPeriod);
        mockGenerator(quarterPeriodGenerator, getPast(PeriodType.Quarterly), getFuture(PeriodType.Quarterly), quarterPeriod);
        mockGenerator(sixMonthlyPeriodGenerator, getPast(PeriodType.SixMonthly), getFuture(PeriodType.SixMonthly), sixMonthlyPeriod);
        mockGenerator(sixMonthlyAprilPeriodGenerator, getPast(PeriodType.SixMonthlyApril), getFuture(PeriodType.SixMonthlyApril), sixMonthlyAprilPeriod);
        mockGenerator(yearlyPeriodGenerator, getPast(PeriodType.Yearly), getFuture(PeriodType.Yearly), yearlyPeriod);
        mockGenerator(financialAprilPeriodGenerator, getPast(PeriodType.FinancialApril), getFuture(PeriodType.FinancialApril), financialAprilPeriod);
        mockGenerator(financialJulyPeriodGenerator, getPast(PeriodType.FinancialJuly), getFuture(PeriodType.FinancialJuly), financialJulyPeriod);
        mockGenerator(financialOctPeriodGenerator, getPast(PeriodType.FinancialOct), getFuture(PeriodType.FinancialOct), financialOctPeriod);
    }

    private void mockGenerator(PeriodGenerator generator, int past, int future, Period period) {
        when(generator.generatePeriods(past, future)).thenReturn(Lists.newArrayList(period));
    }

    private void verifyChildGeneratorCalled(PeriodGenerator generator, int past, int future) {
        verify(generator).generatePeriods(past, future);
    }

    private void assertChildAnswerInParentAnswer(Period period) {
        assertThat(periodGenerator.generatePeriods().contains(period)).isEqualTo(true);
    }

    @Test
    public void call_all_child_period_generators() {
        periodGenerator.generatePeriods();

        verifyChildGeneratorCalled(dailyPeriodGenerator, getPast(PeriodType.Daily), getFuture(PeriodType.Daily));
        verifyChildGeneratorCalled(weeklyPeriodGenerator, getPast(PeriodType.Weekly), getFuture(PeriodType.Weekly));
        verifyChildGeneratorCalled(weeklyWednesdayPeriodGenerator, getPast(PeriodType.WeeklyWednesday), getFuture(PeriodType.WeeklyWednesday));
        verifyChildGeneratorCalled(weeklyThursdayPeriodGenerator, getPast(PeriodType.WeeklyThursday), getFuture(PeriodType.WeeklyThursday));
        verifyChildGeneratorCalled(weeklySaturdayPeriodGenerator, getPast(PeriodType.WeeklySaturday), getFuture(PeriodType.WeeklySaturday));
        verifyChildGeneratorCalled(weeklySundayPeriodGenerator, getPast(PeriodType.WeeklySunday), getFuture(PeriodType.WeeklySunday));
        verifyChildGeneratorCalled(biWeeklyPeriodGenerator, getPast(PeriodType.BiWeekly), getFuture(PeriodType.BiWeekly));
        verifyChildGeneratorCalled(monthlyPeriodGenerator, getPast(PeriodType.Monthly), getFuture(PeriodType.Monthly));
        verifyChildGeneratorCalled(biMonthlyPeriodGenerator, getPast(PeriodType.BiMonthly), getFuture(PeriodType.BiMonthly));
        verifyChildGeneratorCalled(quarterPeriodGenerator, getPast(PeriodType.Quarterly), getFuture(PeriodType.Quarterly));
        verifyChildGeneratorCalled(sixMonthlyPeriodGenerator, getPast(PeriodType.SixMonthly), getFuture(PeriodType.SixMonthly));
        verifyChildGeneratorCalled(sixMonthlyAprilPeriodGenerator, getPast(PeriodType.SixMonthlyApril), getFuture(PeriodType.SixMonthlyApril));
        verifyChildGeneratorCalled(yearlyPeriodGenerator, getPast(PeriodType.Yearly), getFuture(PeriodType.Yearly));
        verifyChildGeneratorCalled(financialAprilPeriodGenerator, getPast(PeriodType.FinancialApril), getFuture(PeriodType.FinancialApril));
        verifyChildGeneratorCalled(financialJulyPeriodGenerator, getPast(PeriodType.FinancialJuly), getFuture(PeriodType.FinancialJuly));
        verifyChildGeneratorCalled(financialOctPeriodGenerator, getPast(PeriodType.FinancialOct), getFuture(PeriodType.FinancialOct));
    }

    @Test
    public void return_all_child_periods_returned() {
        periodGenerator.generatePeriods();

        assertChildAnswerInParentAnswer(dailyPeriod);
        assertChildAnswerInParentAnswer(weeklyPeriod);
        assertChildAnswerInParentAnswer(weeklyWednesdayPeriod);
        assertChildAnswerInParentAnswer(weeklyThursdayPeriod);
        assertChildAnswerInParentAnswer(weeklySaturdayPeriod);
        assertChildAnswerInParentAnswer(weeklySundayPeriod);
        assertChildAnswerInParentAnswer(biWeeklyPeriod);
        assertChildAnswerInParentAnswer(monthlyPeriod);
        assertChildAnswerInParentAnswer(biMonthlyPeriod);
        assertChildAnswerInParentAnswer(quarterPeriod);
        assertChildAnswerInParentAnswer(sixMonthlyPeriod);
        assertChildAnswerInParentAnswer(sixMonthlyAprilPeriod);
        assertChildAnswerInParentAnswer(yearlyPeriod);
        assertChildAnswerInParentAnswer(financialAprilPeriod);
        assertChildAnswerInParentAnswer(financialJulyPeriod);
        assertChildAnswerInParentAnswer(financialOctPeriod);
    }

    @Test
    public void past_and_future_periods_defined_for_all_period_types() {
        for (PeriodType periodType : PeriodType.values()) {
            Integer pastPeriods = periodGenerator.past.get(periodType);
            assertThat(pastPeriods).isNotNull();

            Integer futurePeriods = periodGenerator.future.get(periodType);
            assertThat(futurePeriods).isNotNull();
        }
    }

    private int getPast(PeriodType periodType) {
        Integer periods = periodGenerator.past.get(periodType);
        return periods == null ? 0 : periods;
    }

    private int getFuture(PeriodType periodType) {
        Integer periods = periodGenerator.future.get(periodType);
        return periods == null ? 0 : periods;
    }
}