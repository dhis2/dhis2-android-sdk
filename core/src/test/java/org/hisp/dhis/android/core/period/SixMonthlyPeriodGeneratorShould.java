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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Calendar;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class SixMonthlyPeriodGeneratorShould {

    @Test
    public void generate_last_period() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2018, 1, 21);
        NMonthlyPeriodGenerator generator = NMonthlyPeriodGeneratorFactory.sixMonthly(calendar);

        Calendar periodStartCalendar = (Calendar) calendar.clone();
        periodStartCalendar.set(2018, 0, 1);
        PeriodModel period = generateExpectedPeriod("2018S1", periodStartCalendar);

        List<PeriodModel> generatedPeriods = generator.generateLastPeriods(1);
        List<PeriodModel> expectedPeriods = Lists.newArrayList(period);

        assertThat(generatedPeriods).isEqualTo(expectedPeriods);
    }

    @Test
    public void generate_starting_period_on_first_day_for_january() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2018, 0, 1);
        NMonthlyPeriodGenerator generator = NMonthlyPeriodGeneratorFactory.sixMonthly(calendar);

        Calendar periodStartCalendar = (Calendar) calendar.clone();
        periodStartCalendar.set(2018, 0, 1);
        PeriodModel period = generateExpectedPeriod("2018S1", periodStartCalendar);

        List<PeriodModel> generatedPeriods = generator.generateLastPeriods(1);
        List<PeriodModel> expectedPeriods = Lists.newArrayList(period);

        assertThat(generatedPeriods).isEqualTo(expectedPeriods);
    }

    @Test
    public void generate_ending_period_on_last_day_for_january() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 11, 31);
        NMonthlyPeriodGenerator generator = NMonthlyPeriodGeneratorFactory.sixMonthly(calendar);

        Calendar periodStartCalendar = (Calendar) calendar.clone();
        periodStartCalendar.set(2017, 6, 1);
        PeriodModel period = generateExpectedPeriod("2017S2", periodStartCalendar);

        List<PeriodModel> generatedPeriods = generator.generateLastPeriods(1);
        List<PeriodModel> expectedPeriods = Lists.newArrayList(period);

        assertThat(generatedPeriods).isEqualTo(expectedPeriods);
    }

    @Test
    public void generate_last_two_periods() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2018, 1, 21);
        NMonthlyPeriodGenerator generator = NMonthlyPeriodGeneratorFactory.sixMonthly(calendar);

        Calendar period1StartCalendar = (Calendar) calendar.clone();
        period1StartCalendar.set(2017, 6, 1);
        PeriodModel period1 = generateExpectedPeriod("2017S2", period1StartCalendar);

        Calendar period2StartCalendar = (Calendar) calendar.clone();
        period2StartCalendar.set(2018, 0, 1);
        PeriodModel period2 = generateExpectedPeriod("2018S1", period2StartCalendar);

        List<PeriodModel> generatedPeriods = generator.generateLastPeriods(2);
        List<PeriodModel> expectedPeriods = Lists.newArrayList(period1, period2);

        assertThat(generatedPeriods).isEqualTo(expectedPeriods);
    }

    private PeriodModel generateExpectedPeriod(String id, Calendar cal) {
        Calendar calendar = (Calendar) cal.clone();
        AbstractPeriodGenerator.setCalendarToStartTimeOfADay(calendar);
        Calendar endCalendar = (Calendar) calendar.clone();
        endCalendar.add(Calendar.MONTH, 6);
        endCalendar.add(Calendar.MILLISECOND, -1);
        return PeriodModel.builder()
                .periodId(id)
                .periodType(PeriodType.SixMonthly)
                .startDate(calendar.getTime())
                .endDate(endCalendar.getTime())
                .build();
    }
}