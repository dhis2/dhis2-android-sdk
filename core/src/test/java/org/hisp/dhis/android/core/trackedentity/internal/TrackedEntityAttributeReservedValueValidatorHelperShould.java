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
package org.hisp.dhis.android.core.trackedentity.internal;

import org.hisp.dhis.android.core.period.internal.CalendarUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;

@RunWith(JUnit4.class)
public class TrackedEntityAttributeReservedValueValidatorHelperShould {

    private TrackedEntityAttributeReservedValueValidatorHelper helper;

    @Before
    public void setUp() throws IOException {
        helper = new TrackedEntityAttributeReservedValueValidatorHelper();
    }

    @Test
    public void get_next_expiry_date() throws Exception {
        Date date = helper.nextExpiryDate(true, false, false);
        Calendar cal = setCalendarToDayInit();
        cal.add(Calendar.YEAR, 1);
        cal.set(Calendar.DAY_OF_YEAR, 1);

        assertThat(date).isEqualTo(cal.getTime());
    }

    @Test
    public void get_current_date_list_from_pattern() {
        List<String> result = helper.getCurrentDatePatternStrList("CURRENT_DATE(YYYYMM) + RANDOM(###) + CURRENT_DATE(MMww)");

        assertThat(result).isEqualTo(new ArrayList<>(Arrays.asList("YYYYMM", "MMww")));
    }

    @Test
    public void get_expiry_date_from_pattern() {
        Date date = helper.getExpiryDateCode("CURRENT_DATE(YYYYMM) + RANDOM(###) + CURRENT_DATE(MMww)");
        Calendar cal = setCalendarToDayInit();
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.add(Calendar.WEEK_OF_YEAR, 1);
        CalendarUtils.setDayOfWeek(cal, Calendar.MONDAY);

        assertThat(date).isEqualTo(cal.getTime());
    }

    private Calendar setCalendarToDayInit() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        return cal;
    }
}