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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

abstract class AbstractPeriodGenerator {
    protected final Calendar calendar;
    protected final SimpleDateFormat idFormatter;
    private final PeriodType periodType;


    AbstractPeriodGenerator(Calendar calendar, String dateFormatStr, PeriodType periodType) {
        this.calendar = (Calendar) calendar.clone();
        this.idFormatter = new SimpleDateFormat(dateFormatStr, Locale.US);
        this.periodType = periodType;
        setCalendarToStartDate();
    }

    final List<PeriodModel> generateLastPeriods(int count) throws RuntimeException {
        if (count < 1) throw new RuntimeException("Number of last periods must be positive.");

        List<PeriodModel> periods = new ArrayList<>();
        setCalendarToFirstPeriod(count);

        for (int i = 0; i < count; i++) {
            Date startDate = getStartDateAndUpdateCalendar();
            String periodId = generateId();
            Date endDate = getEndDateAndUpdateCalendar();

            PeriodModel period = PeriodModel.builder()
                    .periodType(periodType)
                    .startDate(startDate)
                    .periodId(periodId)
                    .endDate(endDate)
                    .build();
            periods.add(period);

            incrementCalendar();
        }
        return periods;
    }

    protected abstract void setCalendarToStartDate();

    protected abstract void setCalendarToFirstPeriod(int count);

    protected String generateId() {
        return idFormatter.format(calendar.getTime());
    }

    protected Date getStartDateAndUpdateCalendar() {
        return calendar.getTime();
    }

    protected abstract Date getEndDateAndUpdateCalendar();

    protected void incrementCalendar() {
        calendar.add(Calendar.DATE, 1);
    }
}
