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

import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hisp.dhis.android.core.period.ParentPeriodGeneratorImpl.BIMONTHLY_PERIODS;
import static org.hisp.dhis.android.core.period.ParentPeriodGeneratorImpl.BIWEEKLY_PERIODS;
import static org.hisp.dhis.android.core.period.ParentPeriodGeneratorImpl.DAILY_PERIODS;
import static org.hisp.dhis.android.core.period.ParentPeriodGeneratorImpl.MONTHLY_PERIODS;
import static org.hisp.dhis.android.core.period.ParentPeriodGeneratorImpl.QUARTER_PERIODS;
import static org.hisp.dhis.android.core.period.ParentPeriodGeneratorImpl.SIXMONTHLY_PERIODS;
import static org.hisp.dhis.android.core.period.ParentPeriodGeneratorImpl.WEEKLY_PERIODS;
import static org.hisp.dhis.android.core.period.ParentPeriodGeneratorImpl.YEARLY_PERIODS;
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
    private PeriodModel dailyPeriod;

    @Mock
    private PeriodModel weeklyPeriod;

    @Mock
    private PeriodModel weeklyWednesdayPeriod;

    @Mock
    private PeriodModel weeklyThursdayPeriod;

    @Mock
    private PeriodModel weeklySaturdayPeriod;

    @Mock
    private PeriodModel weeklySundayPeriod;

    @Mock
    private PeriodModel biWeeklyPeriod;

    @Mock
    private PeriodModel monthlyPeriod;

    @Mock
    private PeriodModel biMonthlyPeriod;

    @Mock
    private PeriodModel quarterPeriod;

    @Mock
    private PeriodModel sixMonthlyPeriod;

    @Mock
    private PeriodModel sixMonthlyAprilPeriod;

    @Mock
    private PeriodModel yearlyPeriod;

    @Mock
    private PeriodModel financialAprilPeriod;

    @Mock
    private PeriodModel financialJulyPeriod;

    @Mock
    private PeriodModel financialOctPeriod;

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
        
        mockGenerator(dailyPeriodGenerator, DAILY_PERIODS, dailyPeriod);
        mockGenerator(weeklyPeriodGenerator, WEEKLY_PERIODS, weeklyPeriod);
        mockGenerator(weeklyWednesdayPeriodGenerator, WEEKLY_PERIODS, weeklyWednesdayPeriod);
        mockGenerator(weeklyThursdayPeriodGenerator, WEEKLY_PERIODS, weeklyThursdayPeriod);
        mockGenerator(weeklySaturdayPeriodGenerator, WEEKLY_PERIODS, weeklySaturdayPeriod);
        mockGenerator(weeklySundayPeriodGenerator, WEEKLY_PERIODS, weeklySundayPeriod);
        mockGenerator(biWeeklyPeriodGenerator, BIWEEKLY_PERIODS, biWeeklyPeriod);
        mockGenerator(monthlyPeriodGenerator, MONTHLY_PERIODS, monthlyPeriod);
        mockGenerator(biMonthlyPeriodGenerator, BIMONTHLY_PERIODS, biMonthlyPeriod);
        mockGenerator(quarterPeriodGenerator, QUARTER_PERIODS, quarterPeriod);
        mockGenerator(sixMonthlyPeriodGenerator, SIXMONTHLY_PERIODS, sixMonthlyPeriod);
        mockGenerator(sixMonthlyAprilPeriodGenerator, SIXMONTHLY_PERIODS, sixMonthlyAprilPeriod);
        mockGenerator(yearlyPeriodGenerator, YEARLY_PERIODS, yearlyPeriod);
        mockGenerator(financialAprilPeriodGenerator, YEARLY_PERIODS, financialAprilPeriod);
        mockGenerator(financialJulyPeriodGenerator, YEARLY_PERIODS, financialJulyPeriod);
        mockGenerator(financialOctPeriodGenerator, YEARLY_PERIODS, financialOctPeriod);
    }

    private void mockGenerator(PeriodGenerator generator, int periodCount, PeriodModel periodModel) {
        when(generator.generateLastPeriods(periodCount)).thenReturn(Lists.newArrayList(periodModel));
    }

    private void verifyChildGeneratorCalled(PeriodGenerator generator, int periodCount) throws Exception {
        verify(generator).generateLastPeriods(periodCount);
    }

    private void assertChildAnswerInParentAnswer(PeriodModel period) throws Exception {
        assertThat(periodGenerator.generatePeriods().contains(period)).isEqualTo(true);
    }

    @Test
    public void call_all_child_period_generators() throws Exception {
        periodGenerator.generatePeriods();

        verifyChildGeneratorCalled(dailyPeriodGenerator, DAILY_PERIODS);
        verifyChildGeneratorCalled(weeklyPeriodGenerator, WEEKLY_PERIODS);
        verifyChildGeneratorCalled(weeklyWednesdayPeriodGenerator, WEEKLY_PERIODS);
        verifyChildGeneratorCalled(weeklyThursdayPeriodGenerator, WEEKLY_PERIODS);
        verifyChildGeneratorCalled(weeklySaturdayPeriodGenerator, WEEKLY_PERIODS);
        verifyChildGeneratorCalled(weeklySundayPeriodGenerator, WEEKLY_PERIODS);
        verifyChildGeneratorCalled(biWeeklyPeriodGenerator, BIWEEKLY_PERIODS);
        verifyChildGeneratorCalled(monthlyPeriodGenerator, MONTHLY_PERIODS);
        verifyChildGeneratorCalled(biMonthlyPeriodGenerator, BIMONTHLY_PERIODS);
        verifyChildGeneratorCalled(quarterPeriodGenerator, QUARTER_PERIODS);
        verifyChildGeneratorCalled(sixMonthlyPeriodGenerator, SIXMONTHLY_PERIODS);
        verifyChildGeneratorCalled(sixMonthlyAprilPeriodGenerator, SIXMONTHLY_PERIODS);
        verifyChildGeneratorCalled(yearlyPeriodGenerator, YEARLY_PERIODS);
        verifyChildGeneratorCalled(financialAprilPeriodGenerator, YEARLY_PERIODS);
        verifyChildGeneratorCalled(financialJulyPeriodGenerator, YEARLY_PERIODS);
        verifyChildGeneratorCalled(financialOctPeriodGenerator, YEARLY_PERIODS);
    }

    @Test
    public void return_all_child_periods_returned() throws Exception {
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