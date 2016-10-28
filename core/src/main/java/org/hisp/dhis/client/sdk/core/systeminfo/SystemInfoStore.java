/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.client.sdk.core.systeminfo;

import android.content.Context;
import android.content.SharedPreferences;

import org.hisp.dhis.client.sdk.models.systeminfo.SystemInfo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public class SystemInfoStore {
    private static final String PREFERENCES = "preferences:systemInfo";
    private static final String KEY_SERVER_DATE = "key:serverDate";
    private static final String KEY_DATE_FORMAT = "key:dateFormat";

    private final SharedPreferences preferences;

    public SystemInfoStore(Context context) {
        isNull(context, "Context object must not be null");
        preferences = context.getSharedPreferences(PREFERENCES,
                Context.MODE_PRIVATE);
    }

    public synchronized boolean save(SystemInfo systemInfo) {
        isNull(systemInfo, "SystemInfo object must not be null");
        isNull(systemInfo.dateFormat(), "dateFormat object must not be null");
        isNull(systemInfo.serverDate(), "serverDate object must not be null");

        String serverDate = systemInfo.serverDate().toString();
        String dateFormat = systemInfo.dateFormat();

        return preferences.edit().putString(KEY_SERVER_DATE, serverDate).commit() &&
                preferences.edit().putString(KEY_DATE_FORMAT, dateFormat).commit();
    }

    public SystemInfo get() {
        String dateFormat = preferences.getString(KEY_DATE_FORMAT, null);
        String dateTime = preferences.getString(KEY_SERVER_DATE, null);

        if (dateFormat != null && dateTime != null) {
            DateFormat simpleDateFormat = SimpleDateFormat.getDateTimeInstance();
            try {
                Date date = simpleDateFormat.parse(dateTime);

                return SystemInfo.builder()
                        .dateFormat(dateFormat)
                        .serverDate(date)
                        .build();
            } catch (ParseException exception) {
                // throwing exception out in order not to
                // miss important information
                throw new RuntimeException(exception);
            }
        }

        return null;
    }

    public boolean clear() {
        return preferences.edit().clear().commit();
    }
}
