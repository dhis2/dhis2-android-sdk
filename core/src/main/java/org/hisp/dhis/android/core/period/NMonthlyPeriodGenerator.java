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

import java.util.Calendar;

final class NMonthlyPeriodGenerator extends AbstractPeriodGenerator {

    private final int durationInMonths;
    private final String idAdditionalString;
    private final int startMonth;

    NMonthlyPeriodGenerator(Calendar calendar, PeriodType periodType, int durationInMonths,
                                    String idAdditionalString, int startMonth) {
        super(calendar, "yyyy", periodType);
        this.durationInMonths = durationInMonths;
        this.idAdditionalString = idAdditionalString;
        this.startMonth = startMonth;
    }

    @Override
    protected void setCalendarToStartDate() {
        calendar.set(Calendar.DATE, 1);
        int currentMonth = calendar.get(Calendar.MONTH);
        if (currentMonth < startMonth) {
            calendar.add(Calendar.YEAR, -1);
        }
        int monthsFromStart = currentMonth - startMonth;
        int startMonth = monthsFromStart - (monthsFromStart % durationInMonths);
        calendar.set(Calendar.MONTH, startMonth);
    }

    @Override
    protected void movePeriods(int number) {
        calendar.add(Calendar.MONTH, durationInMonths * number);
    }

    @Override
    protected String generateId() {
        int periodNumber = calendar.get(Calendar.MONTH) / durationInMonths + 1;
        return idFormatter.format(calendar.getTime()) + idAdditionalString + periodNumber;
    }
}
