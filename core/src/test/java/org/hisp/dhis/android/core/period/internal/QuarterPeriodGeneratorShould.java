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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class QuarterPeriodGeneratorShould extends PeriodGeneratorBaseShould {

    public QuarterPeriodGeneratorShould() {
        super(PeriodType.Quarterly, Calendar.MONTH, 3);
    }

    @Test
    public void generate_last_period_forQ1() {
        calendar.set(2018, 0, 1);
        Period period = generateExpectedPeriod("2018Q1", calendar);

        calendar.set(2018, 3, 21);
        NMonthlyPeriodGenerator generator = NMonthlyPeriodGeneratorFactory.quarter(calendar);
        List<Period> generatedPeriods = generator.generatePeriods(-1, 0);

        assertThat(generatedPeriods).isEqualTo(Lists.newArrayList(period));
    }

    @Test
    public void generate_last_period_forQ2() {
        calendar.set(2018, 3, 1);
        Period period = generateExpectedPeriod("2018Q2", calendar);

        calendar.set(2018, 8, 11);
        NMonthlyPeriodGenerator generator = NMonthlyPeriodGeneratorFactory.quarter(calendar);
        List<Period> generatedPeriods = generator.generatePeriods(-1, 0);

        assertThat(generatedPeriods).isEqualTo(Lists.newArrayList(period));
    }

    @Test
    public void generate_last_period_forQ3() {
        calendar.set(2018, 6, 1);
        Period period = generateExpectedPeriod("2018Q3", calendar);

        calendar.set(2018, 11, 3);
        NMonthlyPeriodGenerator generator = NMonthlyPeriodGeneratorFactory.quarter(calendar);
        List<Period> generatedPeriods = generator.generatePeriods(-1, 0);

        assertThat(generatedPeriods).isEqualTo(Lists.newArrayList(period));
    }

    @Test
    public void generate_last_period_forQ4() {
        calendar.set(2018, 9, 1);
        Period period = generateExpectedPeriod("2018Q4", calendar);

        calendar.set(2019, 1, 3);
        NMonthlyPeriodGenerator generator = NMonthlyPeriodGeneratorFactory.quarter(calendar);
        List<Period> generatedPeriods = generator.generatePeriods(-1, 0);

        assertThat(generatedPeriods).isEqualTo(Lists.newArrayList(period));
    }

    @Test
    public void generate_starting_period_on_first_day() {
        calendar.set(2018, 0, 1);
        Period period = generateExpectedPeriod("2018Q1", calendar);

        calendar.set(2018, 3, 1);
        NMonthlyPeriodGenerator generator = NMonthlyPeriodGeneratorFactory.quarter(calendar);
        List<Period> generatedPeriods = generator.generatePeriods(-1, 0);

        assertThat(generatedPeriods).isEqualTo(Lists.newArrayList(period));
    }

    @Test
    public void generate_ending_period_on_last_day() {
        calendar.set(2018, 0, 1);
        Period period = generateExpectedPeriod("2018Q1", calendar);

        calendar.set(2018, 5, 30);
        NMonthlyPeriodGenerator generator = NMonthlyPeriodGeneratorFactory.quarter(calendar);
        List<Period> generatedPeriods = generator.generatePeriods(-1, 0);

        assertThat(generatedPeriods).isEqualTo(Lists.newArrayList(period));
    }

    @Test
    public void generate_last_two_periods() {
        calendar.set(2017, 9, 1);
        Period period1 = generateExpectedPeriod("2017Q4", calendar);
        calendar.set(2018, 0, 1);
        Period period2 = generateExpectedPeriod("2018Q1", calendar);
        List<Period> expectedPeriods = Lists.newArrayList(period1, period2);

        calendar.set(2018, 4, 21);
        NMonthlyPeriodGenerator generator = NMonthlyPeriodGeneratorFactory.quarter(calendar);
        List<Period> generatedPeriods = generator.generatePeriods(-2, 0);

        assertThat(generatedPeriods).isEqualTo(expectedPeriods);
    }

    @Test
    public void generate_period_id() throws ParseException {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        PeriodGenerator quarterGenerator = NMonthlyPeriodGeneratorFactory.quarter(calendar);
        assertThat("2019Q2").isEqualTo(quarterGenerator.generatePeriod(dateFormatter.parse("2019-06-30"), 0).periodId());
        assertThat("2019Q3").isEqualTo(quarterGenerator.generatePeriod(dateFormatter.parse("2019-07-01"), 0).periodId());
    }

    @Test
    public void generate_period_id_with_offset() throws ParseException {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        PeriodGenerator quarterGenerator = NMonthlyPeriodGeneratorFactory.quarter(calendar);
        assertThat("2019Q3").isEqualTo(quarterGenerator.generatePeriod(dateFormatter.parse("2019-06-30"), 1).periodId());
        assertThat("2019Q2").isEqualTo(quarterGenerator.generatePeriod(dateFormatter.parse("2019-07-01"), -1).periodId());
    }

    @Test
    public void generate_periods_in_this_year() {
        calendar.set(2019, 7, 29);
        PeriodGenerator generator = NMonthlyPeriodGeneratorFactory.quarter(calendar);

        List<Period> periods = generator.generatePeriodsInYear(0);

        assertThat(periods.size()).isEqualTo(4);
        assertThat(periods.get(0).periodId()).isEqualTo("2019Q1");
        assertThat(periods.get(3).periodId()).isEqualTo("2019Q4");
    }

    @Test
    public void generate_periods_in_last_year() {
        calendar.set(2019, 7, 29);
        PeriodGenerator generator = NMonthlyPeriodGeneratorFactory.quarter(calendar);

        List<Period> periods = generator.generatePeriodsInYear(-1);

        assertThat(periods.size()).isEqualTo(4);
        assertThat(periods.get(0).periodId()).isEqualTo("2018Q1");
        assertThat(periods.get(3).periodId()).isEqualTo("2018Q4");
    }
}