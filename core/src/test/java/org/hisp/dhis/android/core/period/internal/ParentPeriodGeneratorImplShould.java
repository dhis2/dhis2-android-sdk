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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hisp.dhis.android.core.period.internal.ParentPeriodGeneratorImpl.Past;
import static org.hisp.dhis.android.core.period.internal.ParentPeriodGeneratorImpl.Future;
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
        
        mockGenerator(dailyPeriodGenerator, Past.DAILY_PERIODS, Future.DAILY_PERIODS, dailyPeriod);
        mockGenerator(weeklyPeriodGenerator, Past.WEEKLY_PERIODS, Future.WEEKLY_PERIODS, weeklyPeriod);
        mockGenerator(weeklyWednesdayPeriodGenerator, Past.WEEKLY_PERIODS, Future.WEEKLY_PERIODS, weeklyWednesdayPeriod);
        mockGenerator(weeklyThursdayPeriodGenerator, Past.WEEKLY_PERIODS,Future.WEEKLY_PERIODS, weeklyThursdayPeriod);
        mockGenerator(weeklySaturdayPeriodGenerator, Past.WEEKLY_PERIODS, Future.WEEKLY_PERIODS, weeklySaturdayPeriod);
        mockGenerator(weeklySundayPeriodGenerator, Past.WEEKLY_PERIODS, Future.WEEKLY_PERIODS, weeklySundayPeriod);
        mockGenerator(biWeeklyPeriodGenerator, Past.BIWEEKLY_PERIODS, Future.BIWEEKLY_PERIODS, biWeeklyPeriod);
        mockGenerator(monthlyPeriodGenerator, Past.MONTHLY_PERIODS, Future.MONTHLY_PERIODS, monthlyPeriod);
        mockGenerator(biMonthlyPeriodGenerator, Past.BIMONTHLY_PERIODS, Future.BIMONTHLY_PERIODS, biMonthlyPeriod);
        mockGenerator(quarterPeriodGenerator, Past.QUARTER_PERIODS, Future.QUARTER_PERIODS, quarterPeriod);
        mockGenerator(sixMonthlyPeriodGenerator, Past.SIXMONTHLY_PERIODS, Future.SIXMONTHLY_PERIODS, sixMonthlyPeriod);
        mockGenerator(sixMonthlyAprilPeriodGenerator, Past.SIXMONTHLY_PERIODS, Future.SIXMONTHLY_PERIODS, sixMonthlyAprilPeriod);
        mockGenerator(yearlyPeriodGenerator, Past.YEARLY_PERIODS, Future.YEARLY_PERIODS, yearlyPeriod);
        mockGenerator(financialAprilPeriodGenerator, Past.YEARLY_PERIODS, Future.YEARLY_PERIODS, financialAprilPeriod);
        mockGenerator(financialJulyPeriodGenerator, Past.YEARLY_PERIODS, Future.YEARLY_PERIODS, financialJulyPeriod);
        mockGenerator(financialOctPeriodGenerator, Past.YEARLY_PERIODS, Future.YEARLY_PERIODS, financialOctPeriod);
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

        verifyChildGeneratorCalled(dailyPeriodGenerator, Past.DAILY_PERIODS, Future.DAILY_PERIODS);
        verifyChildGeneratorCalled(weeklyPeriodGenerator, Past.WEEKLY_PERIODS, Future.WEEKLY_PERIODS);
        verifyChildGeneratorCalled(weeklyWednesdayPeriodGenerator, Past.WEEKLY_PERIODS, Future.WEEKLY_PERIODS);
        verifyChildGeneratorCalled(weeklyThursdayPeriodGenerator, Past.WEEKLY_PERIODS, Future.WEEKLY_PERIODS);
        verifyChildGeneratorCalled(weeklySaturdayPeriodGenerator, Past.WEEKLY_PERIODS, Future.WEEKLY_PERIODS);
        verifyChildGeneratorCalled(weeklySundayPeriodGenerator, Past.WEEKLY_PERIODS, Future.WEEKLY_PERIODS);
        verifyChildGeneratorCalled(biWeeklyPeriodGenerator, Past.BIWEEKLY_PERIODS, Future.BIWEEKLY_PERIODS);
        verifyChildGeneratorCalled(monthlyPeriodGenerator, Past.MONTHLY_PERIODS, Future.MONTHLY_PERIODS);
        verifyChildGeneratorCalled(biMonthlyPeriodGenerator, Past.BIMONTHLY_PERIODS, Future.BIMONTHLY_PERIODS);
        verifyChildGeneratorCalled(quarterPeriodGenerator, Past.QUARTER_PERIODS, Future.QUARTER_PERIODS);
        verifyChildGeneratorCalled(sixMonthlyPeriodGenerator, Past.SIXMONTHLY_PERIODS, Future.SIXMONTHLY_PERIODS);
        verifyChildGeneratorCalled(sixMonthlyAprilPeriodGenerator, Past.SIXMONTHLY_PERIODS, Future.SIXMONTHLY_PERIODS);
        verifyChildGeneratorCalled(yearlyPeriodGenerator, Past.YEARLY_PERIODS, Future.YEARLY_PERIODS);
        verifyChildGeneratorCalled(financialAprilPeriodGenerator, Past.YEARLY_PERIODS, Future.YEARLY_PERIODS);
        verifyChildGeneratorCalled(financialJulyPeriodGenerator, Past.YEARLY_PERIODS, Future.YEARLY_PERIODS);
        verifyChildGeneratorCalled(financialOctPeriodGenerator, Past.YEARLY_PERIODS, Future.YEARLY_PERIODS);
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