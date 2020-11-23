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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(JUnit4.class)
public class BiWeeklyPeriodGeneratorShould {

    protected final Calendar calendar;

    public BiWeeklyPeriodGeneratorShould() {
        this.calendar = Calendar.getInstance();
    }

    @Test
    public void generate_bi_weekly_periods_for_one_bi_week() {
        calendar.set(2018, 2, 8);
        Period period = generateExpectedPeriod("2018BiW5", calendar, Calendar.MONDAY, PeriodType.BiWeekly);

        calendar.set(2018, 2, 22);

        List<Period> generatedPeriods = new BiWeeklyPeriodGenerator(calendar).generatePeriods(1, 0);

        assertThat(generatedPeriods).isEqualTo(Lists.newArrayList(period));
    }

    @Test
    public void generate_bi_weekly_periods() {
        calendar.set(2018,2,8);
        Period period1 = generateExpectedPeriod("2018BiW5", calendar, Calendar.MONDAY, PeriodType.BiWeekly);
        calendar.set(2018, 2, 15);
        Period period2 = generateExpectedPeriod("2018BiW6", calendar, Calendar.MONDAY, PeriodType.BiWeekly);
        calendar.set(2018, 2, 29);

        List<Period> generatedPeriods = new BiWeeklyPeriodGenerator(calendar).generatePeriods(2, 0);

        assertThat(generatedPeriods).isEqualTo(Lists.newArrayList(period1, period2));
    }

    @Test
    public void generate_bi_weekly_periods_for_changing_year() {
        calendar.set(2016,11,31);
        Period period1 = generateExpectedPeriod("2016BiW26", calendar, Calendar.MONDAY, PeriodType.BiWeekly);
        calendar.set(2017, 0, 7);
        Period period2 = generateExpectedPeriod("2017BiW1", calendar, Calendar.MONDAY, PeriodType.BiWeekly);
        calendar.set(2017, 0, 18);
        Period period3 = generateExpectedPeriod("2017BiW2", calendar, Calendar.MONDAY, PeriodType.BiWeekly);
        calendar.set(2017, 1, 1);

        List<Period> generatedPeriods = new BiWeeklyPeriodGenerator(calendar).generatePeriods(3, 0);

        assertThat(generatedPeriods).isEqualTo(Lists.newArrayList(period1, period2, period3));
    }

    @Test
    public void generate_the_first_bi_week_including_january_4() {
        calendar.set(2018, 0, 4);

        Period period = generateExpectedPeriod("2018BiW1", calendar, Calendar.MONDAY, PeriodType.BiWeekly);

        calendar.set(2018, 0, 22);

        List<Period> generatedPeriods = new BiWeeklyPeriodGenerator(calendar).generatePeriods(1, 0);

        assertThat(generatedPeriods).isEqualTo(Lists.newArrayList(period));
    }

    @Test
    public void generate_period_id() throws ParseException {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        PeriodGenerator biWeeklyGenerator = new BiWeeklyPeriodGenerator(calendar);
        assertThat("2019BiW26").isEqualTo(biWeeklyGenerator.generatePeriod(dateFormatter.parse("2019-12-23")).periodId());
        assertThat("2020BiW1").isEqualTo(biWeeklyGenerator.generatePeriod(dateFormatter.parse("2020-01-02")).periodId());
    }

    private Period generateExpectedPeriod(String id, Calendar cal, int weekStartDay, PeriodType periodType) {
        Calendar startCalendar = (Calendar) cal.clone();
        AbstractPeriodGenerator.setCalendarToStartTimeOfADay(startCalendar);
        setFirstDayOfBiWeekAndMinimalDaysInFirstWeek(startCalendar, weekStartDay);
        Calendar endCalendar = (Calendar) startCalendar.clone();
        endCalendar.add(Calendar.WEEK_OF_YEAR, 2);
        endCalendar.add(Calendar.MILLISECOND, -1);
        return Period.builder()
                .periodId(id)
                .periodType(periodType)
                .startDate(startCalendar.getTime())
                .endDate(endCalendar.getTime())
                .build();
    }

    private void setFirstDayOfBiWeekAndMinimalDaysInFirstWeek(Calendar startCalendar, int weekStartDay) {
        startCalendar.getTime();
        startCalendar.setFirstDayOfWeek(weekStartDay);
        startCalendar.setMinimalDaysInFirstWeek(4);
        CalendarUtils.setDayOfWeek(startCalendar, weekStartDay);

        Calendar cal = (Calendar) startCalendar.clone();
        CalendarUtils.setDayOfWeek(cal, weekStartDay + 3);
        Integer weekOfYear = cal.get(Calendar.WEEK_OF_YEAR);
        Boolean secondWeekOfBiWeek = (weekOfYear % 2) == 0;
        if (secondWeekOfBiWeek) {
            startCalendar.add(Calendar.WEEK_OF_YEAR, -1);
        }
    }
}