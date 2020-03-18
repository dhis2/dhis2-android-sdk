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

        mockGenerator(dailyPeriodGenerator, PeriodType.Daily.getDefaultPastPeriods(), PeriodType.Daily.getDefaultFuturePeriods(), dailyPeriod);
        mockGenerator(weeklyPeriodGenerator, PeriodType.Weekly.getDefaultPastPeriods(), PeriodType.Weekly.getDefaultFuturePeriods(), weeklyPeriod);
        mockGenerator(weeklyWednesdayPeriodGenerator, PeriodType.WeeklyWednesday.getDefaultPastPeriods(), PeriodType.WeeklyWednesday.getDefaultFuturePeriods(), weeklyWednesdayPeriod);
        mockGenerator(weeklyThursdayPeriodGenerator, PeriodType.WeeklyThursday.getDefaultPastPeriods(), PeriodType.WeeklyThursday.getDefaultFuturePeriods(), weeklyThursdayPeriod);
        mockGenerator(weeklySaturdayPeriodGenerator, PeriodType.WeeklySaturday.getDefaultPastPeriods(), PeriodType.WeeklySaturday.getDefaultFuturePeriods(), weeklySaturdayPeriod);
        mockGenerator(weeklySundayPeriodGenerator, PeriodType.WeeklySunday.getDefaultPastPeriods(), PeriodType.WeeklySunday.getDefaultFuturePeriods(), weeklySundayPeriod);
        mockGenerator(biWeeklyPeriodGenerator, PeriodType.BiWeekly.getDefaultPastPeriods(), PeriodType.BiWeekly.getDefaultFuturePeriods(), biWeeklyPeriod);
        mockGenerator(monthlyPeriodGenerator, PeriodType.Monthly.getDefaultPastPeriods(), PeriodType.Monthly.getDefaultFuturePeriods(), monthlyPeriod);
        mockGenerator(biMonthlyPeriodGenerator, PeriodType.BiMonthly.getDefaultPastPeriods(), PeriodType.BiMonthly.getDefaultFuturePeriods(), biMonthlyPeriod);
        mockGenerator(quarterPeriodGenerator, PeriodType.Quarterly.getDefaultPastPeriods(), PeriodType.Quarterly.getDefaultFuturePeriods(), quarterPeriod);
        mockGenerator(sixMonthlyPeriodGenerator, PeriodType.SixMonthly.getDefaultPastPeriods(), PeriodType.SixMonthly.getDefaultFuturePeriods(), sixMonthlyPeriod);
        mockGenerator(sixMonthlyAprilPeriodGenerator, PeriodType.SixMonthlyApril.getDefaultPastPeriods(), PeriodType.SixMonthlyApril.getDefaultFuturePeriods(), sixMonthlyAprilPeriod);
        mockGenerator(yearlyPeriodGenerator, PeriodType.Yearly.getDefaultPastPeriods(), PeriodType.Yearly.getDefaultFuturePeriods(), yearlyPeriod);
        mockGenerator(financialAprilPeriodGenerator, PeriodType.FinancialApril.getDefaultPastPeriods(), PeriodType.FinancialApril.getDefaultFuturePeriods(), financialAprilPeriod);
        mockGenerator(financialJulyPeriodGenerator, PeriodType.FinancialJuly.getDefaultPastPeriods(), PeriodType.FinancialJuly.getDefaultFuturePeriods(), financialJulyPeriod);
        mockGenerator(financialOctPeriodGenerator, PeriodType.FinancialOct.getDefaultPastPeriods(), PeriodType.FinancialOct.getDefaultFuturePeriods(), financialOctPeriod);
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

        verifyChildGeneratorCalled(dailyPeriodGenerator, PeriodType.Daily.getDefaultPastPeriods(), PeriodType.Daily.getDefaultFuturePeriods());
        verifyChildGeneratorCalled(weeklyPeriodGenerator, PeriodType.Weekly.getDefaultPastPeriods(), PeriodType.Weekly.getDefaultFuturePeriods());
        verifyChildGeneratorCalled(weeklyWednesdayPeriodGenerator, PeriodType.WeeklyWednesday.getDefaultPastPeriods(), PeriodType.WeeklyWednesday.getDefaultFuturePeriods());
        verifyChildGeneratorCalled(weeklyThursdayPeriodGenerator, PeriodType.WeeklyThursday.getDefaultPastPeriods(), PeriodType.WeeklyThursday.getDefaultFuturePeriods());
        verifyChildGeneratorCalled(weeklySaturdayPeriodGenerator, PeriodType.WeeklySaturday.getDefaultPastPeriods(), PeriodType.WeeklySaturday.getDefaultFuturePeriods());
        verifyChildGeneratorCalled(weeklySundayPeriodGenerator, PeriodType.WeeklySunday.getDefaultPastPeriods(), PeriodType.WeeklySunday.getDefaultFuturePeriods());
        verifyChildGeneratorCalled(biWeeklyPeriodGenerator, PeriodType.BiWeekly.getDefaultPastPeriods(), PeriodType.BiWeekly.getDefaultFuturePeriods());
        verifyChildGeneratorCalled(monthlyPeriodGenerator, PeriodType.Monthly.getDefaultPastPeriods(), PeriodType.Monthly.getDefaultFuturePeriods());
        verifyChildGeneratorCalled(biMonthlyPeriodGenerator, PeriodType.BiMonthly.getDefaultPastPeriods(), PeriodType.BiMonthly.getDefaultFuturePeriods());
        verifyChildGeneratorCalled(quarterPeriodGenerator, PeriodType.Quarterly.getDefaultPastPeriods(), PeriodType.Quarterly.getDefaultFuturePeriods());
        verifyChildGeneratorCalled(sixMonthlyPeriodGenerator, PeriodType.SixMonthly.getDefaultPastPeriods(), PeriodType.SixMonthly.getDefaultFuturePeriods());
        verifyChildGeneratorCalled(sixMonthlyAprilPeriodGenerator, PeriodType.SixMonthlyApril.getDefaultPastPeriods(), PeriodType.SixMonthlyApril.getDefaultFuturePeriods());
        verifyChildGeneratorCalled(yearlyPeriodGenerator, PeriodType.Yearly.getDefaultPastPeriods(), PeriodType.Yearly.getDefaultFuturePeriods());
        verifyChildGeneratorCalled(financialAprilPeriodGenerator, PeriodType.FinancialApril.getDefaultPastPeriods(), PeriodType.FinancialApril.getDefaultFuturePeriods());
        verifyChildGeneratorCalled(financialJulyPeriodGenerator, PeriodType.FinancialJuly.getDefaultPastPeriods(), PeriodType.FinancialJuly.getDefaultFuturePeriods());
        verifyChildGeneratorCalled(financialOctPeriodGenerator, PeriodType.FinancialOct.getDefaultPastPeriods(), PeriodType.FinancialOct.getDefaultFuturePeriods());
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