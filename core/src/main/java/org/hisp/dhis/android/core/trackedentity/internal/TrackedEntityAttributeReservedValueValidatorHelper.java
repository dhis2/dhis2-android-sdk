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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TrackedEntityAttributeReservedValueValidatorHelper {
    Date getExpiryDateCode(String pattern) throws IllegalStateException {
        List<String> matches = getCurrentDatePatternStrList(pattern);

        boolean yearly = false;
        boolean monthly = false;
        boolean weekly = false;

        for (String match : matches) {
            char[] charArray = match.toCharArray();
            for (char ch : charArray) {
                switch (ch) {
                    case 'Y': yearly = true;
                        break;
                    case 'M': monthly = true;
                        break;
                    case 'w': weekly = true;
                        break;
                    default:
                        break;
                }
            }
        }

        return nextExpiryDate(yearly, monthly, weekly);
    }

    List<String> getCurrentDatePatternStrList(String pattern) {
        String regex = "CURRENT_DATE\\((.*?)\\)";

        Pattern idCodePattern = Pattern.compile(regex, Pattern.MULTILINE);
        Matcher idCodeMatcher = idCodePattern.matcher(pattern);

        List<String> matches = new ArrayList<>();

        while (idCodeMatcher.find()) {
            for (int i = 1; i <= idCodeMatcher.groupCount(); i++) {
                matches.add(idCodeMatcher.group(i));
            }
        }

        return matches;
    }

    Date nextExpiryDate(boolean yearly, boolean monthly, boolean weekly) throws IllegalStateException {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);

        if (weekly) {
            cal.setFirstDayOfWeek(Calendar.MONDAY);
            cal.add(Calendar.WEEK_OF_YEAR, 1);
            CalendarUtils.setDayOfWeek(cal, Calendar.MONDAY);
        } else if (monthly) {
            cal.add(Calendar.MONTH, 1);
            cal.set(Calendar.DAY_OF_MONTH, 1);
        } else if (yearly) {
            cal.add(Calendar.YEAR, 1);
            cal.set(Calendar.DAY_OF_YEAR, 1);
        } else {
            throw new IllegalStateException("No expiry date available for this pattern.");
        }

        return cal.getTime();
    }
}