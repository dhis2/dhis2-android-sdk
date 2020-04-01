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
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.period.Period;
import org.hisp.dhis.android.core.period.PeriodType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class SixMonthlyPeriodGeneratorShould extends PeriodGeneratorBaseShould {

    public SixMonthlyPeriodGeneratorShould() {
        super(PeriodType.SixMonthly, Calendar.MONTH, 6);
    }

    @Test
    public void generate_last_period() {
        calendar.set(2018, 0, 1);
        Period period = generateExpectedPeriod("2018S1", calendar);

        calendar.set(2018, 7, 21);
        NMonthlyPeriodGenerator generator = NMonthlyPeriodGeneratorFactory.sixMonthly(calendar);
        List<Period> generatedPeriods = generator.generatePeriods(1, 0);

        assertThat(generatedPeriods).isEqualTo(Lists.newArrayList(period));
    }

    @Test
    public void generate_starting_period_on_first_day_for_january() {
        calendar.set(2018, 0, 1);
        Period period = generateExpectedPeriod("2018S1", calendar);

        calendar.set(2018, 6, 1);
        NMonthlyPeriodGenerator generator = NMonthlyPeriodGeneratorFactory.sixMonthly(calendar);
        List<Period> generatedPeriods = generator.generatePeriods(1, 0);

        assertThat(generatedPeriods).isEqualTo(Lists.newArrayList(period));
    }

    @Test
    public void generate_ending_period_on_last_day_for_january() {
        calendar.set(2017, 6, 1);
        Period period = generateExpectedPeriod("2017S2", calendar);

        calendar.set(2018, 5, 30);
        NMonthlyPeriodGenerator generator = NMonthlyPeriodGeneratorFactory.sixMonthly(calendar);
        List<Period> generatedPeriods = generator.generatePeriods(1, 0);

        assertThat(generatedPeriods).isEqualTo(Lists.newArrayList(period));
    }

    @Test
    public void generate_last_two_periods() {
        calendar.set(2017, 6, 1);
        Period period1 = generateExpectedPeriod("2017S2", calendar);
        calendar.set(2018, 0, 1);
        Period period2 = generateExpectedPeriod("2018S1", calendar);
        List<Period> expectedPeriods = Lists.newArrayList(period1, period2);

        calendar.set(2018, 7, 21);
        NMonthlyPeriodGenerator generator = NMonthlyPeriodGeneratorFactory.sixMonthly(calendar);
        List<Period> generatedPeriods = generator.generatePeriods(2, 0);

        assertThat(generatedPeriods).isEqualTo(expectedPeriods);
    }

    @Test
    public void generate_period_id() throws ParseException {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        PeriodGenerator generator = NMonthlyPeriodGeneratorFactory.sixMonthly(calendar);
        assertThat(generator.generatePeriod(dateFormatter.parse("2019-06-30")).periodId()).isEqualTo("2019S1");
        assertThat(generator.generatePeriod(dateFormatter.parse("2019-07-01")).periodId()).isEqualTo("2019S2");
    }

    @Test
    public void generate_period_with_right_start_and_end_for_april() throws ParseException {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        PeriodGenerator generator = NMonthlyPeriodGeneratorFactory.sixMonthlyApril(calendar);

        Period p1 = generator.generatePeriod(dateFormatter.parse("2019-09-30"));
        Period p2 = generator.generatePeriod(dateFormatter.parse("2019-10-01"));

        assertThat(BaseIdentifiableObject.dateToDateStr(p1.startDate())).isEqualTo("2019-04-01T00:00:00.000");
        assertThat(BaseIdentifiableObject.dateToDateStr(p1.endDate())).isEqualTo("2019-09-30T23:59:59.999");
        assertThat(BaseIdentifiableObject.dateToDateStr(p2.startDate())).isEqualTo("2019-10-01T00:00:00.000");
        assertThat(BaseIdentifiableObject.dateToDateStr(p2.endDate())).isEqualTo("2020-03-31T23:59:59.999");
    }

    @Test
    public void generate_period_with_right_start_and_end_for_november() throws ParseException {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        PeriodGenerator generator = NMonthlyPeriodGeneratorFactory.sixMonthlyNov(calendar);

        Period p1 = generator.generatePeriod(dateFormatter.parse("2019-10-31"));
        Period p2 = generator.generatePeriod(dateFormatter.parse("2019-11-01"));

        assertThat(BaseIdentifiableObject.dateToDateStr(p1.startDate())).isEqualTo("2019-05-01T00:00:00.000");
        assertThat(BaseIdentifiableObject.dateToDateStr(p2.startDate())).isEqualTo("2019-11-01T00:00:00.000");
        assertThat(BaseIdentifiableObject.dateToDateStr(p1.endDate())).isEqualTo("2019-10-31T23:59:59.999");
        assertThat(BaseIdentifiableObject.dateToDateStr(p2.endDate())).isEqualTo("2020-04-30T23:59:59.999");
    }

    @Test
    public void generate_period_ids_for_april() throws ParseException {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        PeriodGenerator generator = NMonthlyPeriodGeneratorFactory.sixMonthlyApril(calendar);
        assertThat(generator.generatePeriod(dateFormatter.parse("2019-03-30")).periodId()).isEqualTo("2018AprilS2");
        assertThat(generator.generatePeriod(dateFormatter.parse("2018-12-31")).periodId()).isEqualTo("2018AprilS2");
        assertThat(generator.generatePeriod(dateFormatter.parse("2019-01-01")).periodId()).isEqualTo("2018AprilS2");
        assertThat(generator.generatePeriod(dateFormatter.parse("2019-04-01")).periodId()).isEqualTo("2019AprilS1");
        assertThat(generator.generatePeriod(dateFormatter.parse("2019-09-30")).periodId()).isEqualTo("2019AprilS1");
        assertThat(generator.generatePeriod(dateFormatter.parse("2019-10-01")).periodId()).isEqualTo("2019AprilS2");
    }

    @Test
    public void generate_period_id_for_november() throws ParseException {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
        PeriodGenerator generator = NMonthlyPeriodGeneratorFactory.sixMonthlyNov(calendar);
        Period period = generator.generatePeriod(dateFormatter.parse("2019-05-01"));
        assertThat(generator.generatePeriod(dateFormatter.parse("2019-04-30")).periodId()).isEqualTo("2019NovS1");
        assertThat(generator.generatePeriod(dateFormatter.parse("2019-05-01")).periodId()).isEqualTo("2019NovS2");
        assertThat(generator.generatePeriod(dateFormatter.parse("2019-10-31")).periodId()).isEqualTo("2019NovS2");
        assertThat(generator.generatePeriod(dateFormatter.parse("2019-11-01")).periodId()).isEqualTo("2020NovS1");
        assertThat(generator.generatePeriod(dateFormatter.parse("2019-12-31")).periodId()).isEqualTo("2020NovS1");
        assertThat(generator.generatePeriod(dateFormatter.parse("2020-01-01")).periodId()).isEqualTo("2020NovS1");
    }

}