/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.hisp.dhis.android.core.period.internal;

import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.period.Period;
import org.hisp.dhis.android.core.period.PeriodType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.google.common.truth.Truth.assertThat;
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
    private PeriodGenerator sixMonthlyNovPeriodGenerator;

    @Mock
    private PeriodGenerator yearlyPeriodGenerator;

    @Mock
    private PeriodGenerator financialAprilPeriodGenerator;

    @Mock
    private PeriodGenerator financialJulyPeriodGenerator;

    @Mock
    private PeriodGenerator financialOctPeriodGenerator;

    @Mock
    private PeriodGenerator financialNovPeriodGenerator;

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
                quarterPeriodGenerator, sixMonthlyPeriodGenerator, sixMonthlyAprilPeriodGenerator, sixMonthlyNovPeriodGenerator);
        YearlyPeriodGenerators yearlyPeriodGenerators = new YearlyPeriodGenerators(yearlyPeriodGenerator,
                financialAprilPeriodGenerator, financialJulyPeriodGenerator, financialOctPeriodGenerator, financialNovPeriodGenerator);

        periodGenerator = new ParentPeriodGeneratorImpl(dailyPeriodGenerator, weeklyPeriodGenerators,
                biWeeklyPeriodGenerator, monthlyPeriodGenerator, nMonthlyPeriodGenerators, yearlyPeriodGenerators);

        mockGenerator(dailyPeriodGenerator, PeriodType.Daily.getDefaultStartPeriods(), PeriodType.Daily.getDefaultEndPeriods(), dailyPeriod);
        mockGenerator(weeklyPeriodGenerator, PeriodType.Weekly.getDefaultStartPeriods(), PeriodType.Weekly.getDefaultEndPeriods(), weeklyPeriod);
        mockGenerator(weeklyWednesdayPeriodGenerator, PeriodType.WeeklyWednesday.getDefaultStartPeriods(), PeriodType.WeeklyWednesday.getDefaultEndPeriods(), weeklyWednesdayPeriod);
        mockGenerator(weeklyThursdayPeriodGenerator, PeriodType.WeeklyThursday.getDefaultStartPeriods(), PeriodType.WeeklyThursday.getDefaultEndPeriods(), weeklyThursdayPeriod);
        mockGenerator(weeklySaturdayPeriodGenerator, PeriodType.WeeklySaturday.getDefaultStartPeriods(), PeriodType.WeeklySaturday.getDefaultEndPeriods(), weeklySaturdayPeriod);
        mockGenerator(weeklySundayPeriodGenerator, PeriodType.WeeklySunday.getDefaultStartPeriods(), PeriodType.WeeklySunday.getDefaultEndPeriods(), weeklySundayPeriod);
        mockGenerator(biWeeklyPeriodGenerator, PeriodType.BiWeekly.getDefaultStartPeriods(), PeriodType.BiWeekly.getDefaultEndPeriods(), biWeeklyPeriod);
        mockGenerator(monthlyPeriodGenerator, PeriodType.Monthly.getDefaultStartPeriods(), PeriodType.Monthly.getDefaultEndPeriods(), monthlyPeriod);
        mockGenerator(biMonthlyPeriodGenerator, PeriodType.BiMonthly.getDefaultStartPeriods(), PeriodType.BiMonthly.getDefaultEndPeriods(), biMonthlyPeriod);
        mockGenerator(quarterPeriodGenerator, PeriodType.Quarterly.getDefaultStartPeriods(), PeriodType.Quarterly.getDefaultEndPeriods(), quarterPeriod);
        mockGenerator(sixMonthlyPeriodGenerator, PeriodType.SixMonthly.getDefaultStartPeriods(), PeriodType.SixMonthly.getDefaultEndPeriods(), sixMonthlyPeriod);
        mockGenerator(sixMonthlyAprilPeriodGenerator, PeriodType.SixMonthlyApril.getDefaultStartPeriods(), PeriodType.SixMonthlyApril.getDefaultEndPeriods(), sixMonthlyAprilPeriod);
        mockGenerator(yearlyPeriodGenerator, PeriodType.Yearly.getDefaultStartPeriods(), PeriodType.Yearly.getDefaultEndPeriods(), yearlyPeriod);
        mockGenerator(financialAprilPeriodGenerator, PeriodType.FinancialApril.getDefaultStartPeriods(), PeriodType.FinancialApril.getDefaultEndPeriods(), financialAprilPeriod);
        mockGenerator(financialJulyPeriodGenerator, PeriodType.FinancialJuly.getDefaultStartPeriods(), PeriodType.FinancialJuly.getDefaultEndPeriods(), financialJulyPeriod);
        mockGenerator(financialOctPeriodGenerator, PeriodType.FinancialOct.getDefaultStartPeriods(), PeriodType.FinancialOct.getDefaultEndPeriods(), financialOctPeriod);
    }

    private void mockGenerator(PeriodGenerator generator, int past, int future, Period period) {
        when(generator.generatePeriods(past, future)).thenReturn(Lists.newArrayList(period));
    }

    private void verifyChildGeneratorCalled(PeriodGenerator generator, int past, int future) {
        verify(generator).generatePeriods(past, future);
    }

    private void assertChildAnswerInParentAnswer(Period period) {
        assertThat(periodGenerator.generatePeriods().contains(period)).isTrue();
    }

    @Test
    public void call_all_child_period_generators() {
        periodGenerator.generatePeriods();

        verifyChildGeneratorCalled(dailyPeriodGenerator, PeriodType.Daily.getDefaultStartPeriods(), PeriodType.Daily.getDefaultEndPeriods());
        verifyChildGeneratorCalled(weeklyPeriodGenerator, PeriodType.Weekly.getDefaultStartPeriods(), PeriodType.Weekly.getDefaultEndPeriods());
        verifyChildGeneratorCalled(weeklyWednesdayPeriodGenerator, PeriodType.WeeklyWednesday.getDefaultStartPeriods(), PeriodType.WeeklyWednesday.getDefaultEndPeriods());
        verifyChildGeneratorCalled(weeklyThursdayPeriodGenerator, PeriodType.WeeklyThursday.getDefaultStartPeriods(), PeriodType.WeeklyThursday.getDefaultEndPeriods());
        verifyChildGeneratorCalled(weeklySaturdayPeriodGenerator, PeriodType.WeeklySaturday.getDefaultStartPeriods(), PeriodType.WeeklySaturday.getDefaultEndPeriods());
        verifyChildGeneratorCalled(weeklySundayPeriodGenerator, PeriodType.WeeklySunday.getDefaultStartPeriods(), PeriodType.WeeklySunday.getDefaultEndPeriods());
        verifyChildGeneratorCalled(biWeeklyPeriodGenerator, PeriodType.BiWeekly.getDefaultStartPeriods(), PeriodType.BiWeekly.getDefaultEndPeriods());
        verifyChildGeneratorCalled(monthlyPeriodGenerator, PeriodType.Monthly.getDefaultStartPeriods(), PeriodType.Monthly.getDefaultEndPeriods());
        verifyChildGeneratorCalled(biMonthlyPeriodGenerator, PeriodType.BiMonthly.getDefaultStartPeriods(), PeriodType.BiMonthly.getDefaultEndPeriods());
        verifyChildGeneratorCalled(quarterPeriodGenerator, PeriodType.Quarterly.getDefaultStartPeriods(), PeriodType.Quarterly.getDefaultEndPeriods());
        verifyChildGeneratorCalled(sixMonthlyPeriodGenerator, PeriodType.SixMonthly.getDefaultStartPeriods(), PeriodType.SixMonthly.getDefaultEndPeriods());
        verifyChildGeneratorCalled(sixMonthlyAprilPeriodGenerator, PeriodType.SixMonthlyApril.getDefaultStartPeriods(), PeriodType.SixMonthlyApril.getDefaultEndPeriods());
        verifyChildGeneratorCalled(yearlyPeriodGenerator, PeriodType.Yearly.getDefaultStartPeriods(), PeriodType.Yearly.getDefaultEndPeriods());
        verifyChildGeneratorCalled(financialAprilPeriodGenerator, PeriodType.FinancialApril.getDefaultStartPeriods(), PeriodType.FinancialApril.getDefaultEndPeriods());
        verifyChildGeneratorCalled(financialJulyPeriodGenerator, PeriodType.FinancialJuly.getDefaultStartPeriods(), PeriodType.FinancialJuly.getDefaultEndPeriods());
        verifyChildGeneratorCalled(financialOctPeriodGenerator, PeriodType.FinancialOct.getDefaultStartPeriods(), PeriodType.FinancialOct.getDefaultEndPeriods());
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
}