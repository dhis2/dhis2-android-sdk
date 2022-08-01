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
public class DailyPeriodGeneratorShould extends PeriodGeneratorBaseShould {

    public DailyPeriodGeneratorShould() {
        super(PeriodType.Daily, Calendar.DATE);
    }

    @Test
    public void generate_daily_periods_for_one_day() {
        calendar.set(2018, 1, 1);
        Period period = generateExpectedPeriod("20180201", calendar);
        calendar.set(2018, 1, 2);

        List<Period> generatedPeriods = new DailyPeriodGenerator(calendar).generatePeriods(-1, 0);

        assertThat(generatedPeriods).isEqualTo(Lists.newArrayList(period));
    }

    @Test
    public void generate_daily_periods() {
        calendar.set(2018, 2, 4);
        Period period1 = generateExpectedPeriod("20180304", calendar);
        calendar.set(2018, 2, 5);
        Period period2 = generateExpectedPeriod("20180305", calendar);
        calendar.set(2018, 2, 6);

        List<Period> generatedPeriods = new DailyPeriodGenerator(calendar).generatePeriods(-2, 0);
        List<Period> expectedPeriods = Lists.newArrayList(period1, period2);

        assertThat(generatedPeriods).isEqualTo(expectedPeriods);
    }

    @Test
    public void generate_daily_periods_for_changing_year() {
        calendar.set(2017, 11, 31);
        Period period1 = generateExpectedPeriod("20171231", calendar);
        calendar.set(2018, 0, 1);
        Period period2 = generateExpectedPeriod("20180101", calendar);
        calendar.set(2018, 0, 2);
        Period period3 = generateExpectedPeriod("20180102", calendar);
        calendar.set(2018, 0, 3);

        List<Period> generatedPeriods = new DailyPeriodGenerator(calendar).generatePeriods(-3, 0);
        List<Period> expectedPeriods = Lists.newArrayList(period1, period2, period3);

        assertThat(generatedPeriods).isEqualTo(expectedPeriods);
    }

    @Test
    public void generate_period_id() throws ParseException {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        PeriodGenerator dailyGenerator = new DailyPeriodGenerator(calendar);
        assertThat("20191230").isEqualTo(dailyGenerator.generatePeriod(dateFormatter.parse("2019-12-30"), 0).periodId());
        assertThat("20200102").isEqualTo(dailyGenerator.generatePeriod(dateFormatter.parse("2020-01-02"), 0).periodId());
    }

    @Test
    public void generate_period_id_with_offset() throws ParseException {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        PeriodGenerator dailyGenerator = new DailyPeriodGenerator(calendar);
        assertThat("20200101").isEqualTo(dailyGenerator.generatePeriod(dateFormatter.parse("2019-12-30"), 2).periodId());
        assertThat("20191229").isEqualTo(dailyGenerator.generatePeriod(dateFormatter.parse("2020-01-02"), -4).periodId());
    }

    @Test
    public void generate_periods_in_this_year() {
        calendar.set(2020, 7, 29);
        PeriodGenerator generator = new DailyPeriodGenerator(calendar);

        List<Period> periods = generator.generatePeriodsInYear(0);

        assertThat(periods.size()).isEqualTo(366);
        assertThat(periods.get(0).periodId()).isEqualTo("20200101");
        assertThat(periods.get(365).periodId()).isEqualTo("20201231");
    }

    @Test
    public void generate_periods_in_last_year() {
        calendar.set(2020, 7, 29);
        PeriodGenerator generator = new DailyPeriodGenerator(calendar);

        List<Period> periods = generator.generatePeriodsInYear(-1);

        assertThat(periods.size()).isEqualTo(365);
        assertThat(periods.get(0).periodId()).isEqualTo("20190101");
        assertThat(periods.get(364).periodId()).isEqualTo("20191231");
    }
}