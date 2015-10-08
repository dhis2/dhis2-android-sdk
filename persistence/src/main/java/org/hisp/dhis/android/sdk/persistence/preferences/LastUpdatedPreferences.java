/*
 * Copyright (c) 2015, University of Oslo
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

package org.hisp.dhis.android.sdk.persistence.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import org.hisp.dhis.android.sdk.corejava.common.preferences.ILastUpdatedPreferences;
import org.hisp.dhis.android.sdk.corejava.common.preferences.ResourceType;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hisp.dhis.android.sdk.models.utils.Preconditions.isNull;

public class LastUpdatedPreferences implements ILastUpdatedPreferences {
    private static final String PREFERENCES = "preferences:lastUpdated";
    private static final String METADATA_UPDATE_DATETIME = "key:metaDataUpdateDateTime";

    private final SharedPreferences mPrefs;

    public LastUpdatedPreferences(Context context) {
        isNull(context, "Context object must not be null");
        mPrefs = context.getSharedPreferences(PREFERENCES,
                Context.MODE_PRIVATE);
    }

    @Override
    public boolean save(ResourceType resourceType, DateTime dateTime) {
        return save(resourceType, dateTime, null);
    }

    @Override
    public boolean save(ResourceType resourceType, DateTime dateTime, String extra) {
        isNull(resourceType, "ResourceType object must not be null");
        isNull(dateTime, "DateTime object must not be null");

        String identifier = METADATA_UPDATE_DATETIME + resourceType.toString();
        if (extra != null) {
            identifier += extra;
        }

        putString(identifier, dateTime.toString());
        return true;
    }

    @Override
    public boolean delete(ResourceType resourceType) {
        mPrefs.edit().clear().commit();
        return true;
    }

    @Override
    public boolean isSet(ResourceType resourceType) {
        return get(resourceType) != null;
    }

    @Override
    public boolean clear() {
        mPrefs.edit().clear().commit();
        return true;
    }

    @Override
    public DateTime get(ResourceType resourceType) {
        return get(resourceType, null);
    }

    @Override
    public DateTime get(ResourceType resourceType, String extra) {
        String identifier = METADATA_UPDATE_DATETIME + resourceType.toString();
        if (extra != null) {
            identifier += extra;
        }

        String dateTimeString = getString(identifier);
        if (dateTimeString != null) {
            return DateTime.parse(dateTimeString);
        }
        return null;
    }

    @Override
    public List<DateTime> get() {
        Map<String, ?> values = mPrefs.getAll();
        List<DateTime> dateTimes = new ArrayList<>();
        for (String key : values.keySet()) {
            String value = (String) values.get(key);
            if (value != null) {
                dateTimes.add(DateTime.parse(value));
            }
        }
        return dateTimes;
    }

    private void putString(String key, String value) {
        mPrefs.edit().putString(key, value).commit();
    }

    private String getString(String key) {
        return mPrefs.getString(key, null);
    }
}
