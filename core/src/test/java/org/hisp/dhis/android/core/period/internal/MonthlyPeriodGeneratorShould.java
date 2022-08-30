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

import org.apache.commons.lang3.builder.ToStringExclude;
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
public class MonthlyPeriodGeneratorShould extends PeriodGeneratorBaseShould {

    public MonthlyPeriodGeneratorShould() {
        super(PeriodType.Monthly, Calendar.MONTH);
    }

    @Test
    public void generate_periods_for_one_month() {
        calendar.set(2018, 2, 11);
        Period period = generateExpectedPeriod("201803", calendar);
        calendar.set(2018, 3, 11);

        MonthlyPeriodGenerator generator = new MonthlyPeriodGenerator(calendar);
        List<Period> generatedPeriods = generator.generatePeriods(-1, 0);

        assertThat(generatedPeriods).isEqualTo(Lists.newArrayList(period));
    }

    @Test
    public void generate_starting_period_on_feb_29() {
        calendar.set(2016, 1, 29);
        Period period = generateExpectedPeriod("201602", calendar);
        calendar.set(2016, 2, 5);

        MonthlyPeriodGenerator generator = new MonthlyPeriodGenerator(calendar);
        List<Period> generatedPeriods = generator.generatePeriods(-1, 0);

        assertThat(generatedPeriods).isEqualTo(Lists.newArrayList(period));
    }

    @Test
    public void generate_periods_for_three_months() {
        calendar.set(2018, 8, 11);
        Period period1 = generateExpectedPeriod("201809", calendar);
        calendar.set(2018, 9, 11);
        Period period2 = generateExpectedPeriod("201810", calendar);
        calendar.set(2018, 10, 11);
        Period period3 = generateExpectedPeriod("201811", calendar);
        List<Period> expectedPeriods = Lists.newArrayList(period1, period2, period3);
        calendar.set(2018, 11, 11);

        MonthlyPeriodGenerator generator = new MonthlyPeriodGenerator(calendar);
        List<Period> generatedPeriods = generator.generatePeriods(-3, 0);

        assertThat(generatedPeriods).isEqualTo(expectedPeriods);
    }

    @Test
    public void generate_period_id() throws ParseException {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        PeriodGenerator monthlyGenerator = new MonthlyPeriodGenerator(calendar);
        assertThat("201906").isEqualTo(monthlyGenerator.generatePeriod(dateFormatter.parse("2019-06-30"),0).periodId());
        assertThat("201907").isEqualTo(monthlyGenerator.generatePeriod(dateFormatter.parse("2019-07-01"), 0).periodId());

        PeriodGenerator biMonthlyGenerator = NMonthlyPeriodGeneratorFactory.biMonthly(calendar);
        assertThat("201903B").isEqualTo(biMonthlyGenerator.generatePeriod(dateFormatter.parse("2019-06-30"), 0).periodId());
        assertThat("201904B").isEqualTo(biMonthlyGenerator.generatePeriod(dateFormatter.parse("2019-07-01"), 0).periodId());
    }

    @Test
    public void generate_period_id_with_offset() throws ParseException {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        PeriodGenerator monthlyGenerator = new MonthlyPeriodGenerator(calendar);
        assertThat("201908").isEqualTo(monthlyGenerator.generatePeriod(dateFormatter.parse("2019-06-30"),2).periodId());
        assertThat("201905").isEqualTo(monthlyGenerator.generatePeriod(dateFormatter.parse("2019-07-01"), -2).periodId());

        PeriodGenerator biMonthlyGenerator = NMonthlyPeriodGeneratorFactory.biMonthly(calendar);
        assertThat("201905B").isEqualTo(biMonthlyGenerator.generatePeriod(dateFormatter.parse("2019-06-30"), 2).periodId());
        assertThat("201903B").isEqualTo(biMonthlyGenerator.generatePeriod(dateFormatter.parse("2019-07-01"), -1).periodId());
    }

    @Test
    public void generate_periods_in_this_year() {
        calendar.set(2019, 7, 29);
        PeriodGenerator generator = new MonthlyPeriodGenerator(calendar);

        List<Period> periods = generator.generatePeriodsInYear(0);

        assertThat(periods.size()).isEqualTo(12);
        assertThat(periods.get(0).periodId()).isEqualTo("201901");
        assertThat(periods.get(11).periodId()).isEqualTo("201912");
    }

    @Test
    public void generate_periods_in_last_year() {
        calendar.set(2019, 7, 29);
        PeriodGenerator generator = new MonthlyPeriodGenerator(calendar);

        List<Period> periods = generator.generatePeriodsInYear(-1);

        assertThat(periods.size()).isEqualTo(12);
        assertThat(periods.get(0).periodId()).isEqualTo("201801");
        assertThat(periods.get(11).periodId()).isEqualTo("201812");
    }

    @Override
    protected void setStartCalendar(Calendar startCalendar) {
        startCalendar.set(Calendar.DAY_OF_MONTH, 1);
    }
}