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

package org.hisp.dhis.client.sdk.android.systeminfo;

import android.content.Context;
import android.content.SharedPreferences;

import org.hisp.dhis.client.sdk.core.systeminfo.SystemInfoPreferences;
import org.hisp.dhis.client.sdk.models.common.SystemInfo;
import org.joda.time.DateTime;

import java.util.Map;

import static org.hisp.dhis.client.sdk.models.utils.Preconditions.isNull;

public class SystemInfoPreferencesImpl implements SystemInfoPreferences {
    private static final String PREFERENCES = "preferences:systemInfo";
    private static final String KEY_BUILD_TIME = "key:buildTime";
    private static final String KEY_SERVER_DATE = "key:serverDate";
    private static final String KEY_CALENDAR = "key:calendar";
    private static final String KEY_DATE_FORMAT = "key:dateFormat";
    private static final String KEY_REVISION = "key:revision";
    private static final String KEY_VERSION = "key:version";
    private static final String KEY_ANALYTICS_SYNC = "key:intervalSinceLastAnalyticsTableSuccess";
    private static final String KEY_LAST_TABLE_SUCCESS = "key:lastAnalyticsTableSuccess";

    private final SharedPreferences preferences;

    public SystemInfoPreferencesImpl(Context context) {
        isNull(context, "Context object must not be null");
        preferences = context.getSharedPreferences(PREFERENCES,
                Context.MODE_PRIVATE);
    }

    @Override
    public boolean save(SystemInfo systemInfo) {
        isNull(systemInfo, "SystemInfo object must not be null");

        String buildTime = systemInfo.getBuildTime().toString();
        String serverDate = systemInfo.getServerDate().toString();

        boolean isBuildTimeSaved = putString(KEY_BUILD_TIME, buildTime);
        boolean isServerDateSaved = putString(KEY_SERVER_DATE, serverDate);
        boolean isCalendarSaved = putString(KEY_CALENDAR, systemInfo.getCalendar());
        boolean isDateFormatSaved = putString(KEY_DATE_FORMAT, systemInfo.getDateFormat());
        boolean isKeyRevisionSaved = putInt(KEY_REVISION, systemInfo.getRevision());
        boolean isKeyVersionSaved = putString(KEY_VERSION, systemInfo.getVersion());
        boolean isAnalyticsSyncSaved = putString(KEY_ANALYTICS_SYNC,
                systemInfo.getIntervalSinceLastAnalyticsTableSuccess());
        boolean isLastTableSuccessSaved = putString(KEY_LAST_TABLE_SUCCESS,
                systemInfo.getLastAnalyticsTableSuccess());

        return isBuildTimeSaved && isServerDateSaved &&
                isCalendarSaved && isDateFormatSaved &&
                isKeyRevisionSaved && isKeyVersionSaved &&
                isAnalyticsSyncSaved && isLastTableSuccessSaved;
    }

    @Override
    public SystemInfo get() {
        if (!isEmpty()) {
            DateTime buildTime = DateTime.parse(getString(KEY_BUILD_TIME));
            DateTime serverDate = DateTime.parse(getString(KEY_SERVER_DATE));

            SystemInfo systemInfo = new SystemInfo();
            systemInfo.setBuildTime(buildTime);
            systemInfo.setServerDate(serverDate);
            systemInfo.setCalendar(getString(KEY_CALENDAR));
            systemInfo.setDateFormat(getString(KEY_DATE_FORMAT));
            systemInfo.setRevision(getInt(KEY_REVISION));
            systemInfo.setVersion(getString(KEY_VERSION));
            systemInfo.setIntervalSinceLastAnalyticsTableSuccess(getString(KEY_ANALYTICS_SYNC));
            systemInfo.setLastAnalyticsTableSuccess(getString(KEY_LAST_TABLE_SUCCESS));

            return systemInfo;
        }

        return null;
    }

    @Override
    public boolean clear() {
        return preferences.edit().clear().commit();
    }

    @Override
    public boolean isEmpty() {
        Map<String, ?> entries = preferences.getAll();
        return entries == null || entries.isEmpty();
    }

    private boolean putString(String key, String value) {
        return preferences.edit().putString(key, value).commit();
    }

    private boolean putInt(String key, int value) {
        return preferences.edit().putInt(key, value).commit();
    }

    private String getString(String key) {
        return preferences.getString(key, null);
    }

    private int getInt(String key) {
        return preferences.getInt(key, -1);
    }
}
