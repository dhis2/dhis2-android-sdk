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

package org.hisp.dhis.client.sdk.android.api.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import org.hisp.dhis.client.sdk.core.common.preferences.DateType;
import org.hisp.dhis.client.sdk.core.common.preferences.LastUpdatedPreferences;
import org.hisp.dhis.client.sdk.core.common.preferences.ResourceType;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;


public class LastUpdatedPreferencesImpl implements LastUpdatedPreferences {
    private static final String PREFERENCES = "preferences:lastUpdated";
    private final SharedPreferences preferences;

    public LastUpdatedPreferencesImpl(Context context) {
        isNull(context, "Context object must not be null");
        preferences = context.getSharedPreferences(PREFERENCES,
                Context.MODE_PRIVATE);
    }

    private static String buildKey(ResourceType resourceType, DateType dateType) {
        isNull(resourceType, "ResourceType object must not be null");
        isNull(dateType, "DateType object must not be null");

        return resourceType.name() + "." + dateType.name();
    }

    @Override
    public boolean save(ResourceType resourceType, DateType dateType, DateTime dateTime) {
        isNull(dateTime, "DateTime object must not be null");
        return putString(buildKey(resourceType, dateType), dateTime.toString());
    }

    @Override
    public DateTime get(ResourceType resourceType, DateType dateType) {
        String dateTimeString = getString(buildKey(resourceType, dateType));

        if (dateTimeString != null) {
            return DateTime.parse(dateTimeString);
        }

        return null;
    }

    @Override
    public boolean delete(ResourceType resourceType, DateType dateType) {
        return delete(buildKey(resourceType, dateType));
    }

    @Override
    public List<DateTime> list() {
        Map<String, ?> values = preferences.getAll();
        List<DateTime> dateTimes = new ArrayList<>();

        for (String key : values.keySet()) {
            String value = (String) values.get(key);
            if (value != null) {
                dateTimes.add(DateTime.parse(value));
            }
        }

        return dateTimes;
    }

    @Override
    public boolean clear() {
        return preferences.edit().clear().commit();
    }

    private boolean putString(String key, String value) {
        return preferences.edit().putString(key, value).commit();
    }

    private String getString(String key) {
        return preferences.getString(key, null);
    }

    private boolean delete(String key) {
        return preferences.edit().remove(key).commit();
    }
}
