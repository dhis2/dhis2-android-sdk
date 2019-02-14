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
package org.hisp.dhis.android.core.period;

import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

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
    public void generate_bi_weekly_periods_for_one_bi_week() throws Exception {
        calendar.set(2018, 2, 8);
        Period period = generateExpectedPeriod("2018BiW5", calendar, Calendar.MONDAY, PeriodType.BiWeekly);

        List<Period> generatedPeriods = new BiWeeklyPeriodGenerator(calendar).generateLastPeriods(1);

        assertThat(generatedPeriods).isEqualTo(Lists.newArrayList(period));
    }

    @Test
    public void generate_bi_weekly_periods() throws Exception {
        calendar.set(2018,2,8);
        Period period1 = generateExpectedPeriod("2018BiW5", calendar, Calendar.MONDAY, PeriodType.BiWeekly);
        calendar.set(2018, 2, 15);
        Period period2 = generateExpectedPeriod("2018BiW6", calendar, Calendar.MONDAY, PeriodType.BiWeekly);

        List<Period> generatedPeriods = new BiWeeklyPeriodGenerator(calendar).generateLastPeriods(2);

        assertThat(generatedPeriods).isEqualTo(Lists.newArrayList(period1, period2));
    }

    @Test
    public void generate_bi_weekly_periods_for_changing_year() throws Exception {
        calendar.set(2016,11,31);
        Period period1 = generateExpectedPeriod("2016BiW26", calendar, Calendar.MONDAY, PeriodType.BiWeekly);
        calendar.set(2017, 0, 7);
        Period period2 = generateExpectedPeriod("2017BiW1", calendar, Calendar.MONDAY, PeriodType.BiWeekly);
        calendar.set(2017, 0, 18);
        Period period3 = generateExpectedPeriod("2017BiW2", calendar, Calendar.MONDAY, PeriodType.BiWeekly);

        List<Period> generatedPeriods = new BiWeeklyPeriodGenerator(calendar).generateLastPeriods(3);

        assertThat(generatedPeriods).isEqualTo(Lists.newArrayList(period1, period2, period3));
    }

    @Test
    public void generate_the_first_bi_week_including_january_4() throws Exception {
        calendar.set(2018, 0, 4);

        List<Period> generatedPeriods = new BiWeeklyPeriodGenerator(calendar).generateLastPeriods(1);

        Period period = generateExpectedPeriod("2018BiW1", calendar, Calendar.MONDAY, PeriodType.BiWeekly);

        assertThat(generatedPeriods).isEqualTo(Lists.newArrayList(period));
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
        startCalendar.set(Calendar.DAY_OF_WEEK, weekStartDay);

        Calendar cal = (Calendar) startCalendar.clone();
        cal.set(Calendar.DAY_OF_WEEK, weekStartDay + 3);
        Integer weekOfYear = cal.get(Calendar.WEEK_OF_YEAR);
        Boolean secondWeekOfBiWeek = (weekOfYear % 2) == 0;
        if (secondWeekOfBiWeek) {
            startCalendar.add(Calendar.WEEK_OF_YEAR, -1);
        }
    }
}