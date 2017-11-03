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

package org.hisp.dhis.client.sdk.ui;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferencesImpl implements AppPreferences {
    public final static String PREFS_NAME = "preferences:application";

    public static final String CRASH_REPORTS = "crashReports";
    public static final String SYNC_DATE = "syncDate";
    public static final String UPDATE_FREQUENCY = "update_frequency";
    public static final String BACKGROUND_SYNC = "background_sync";
    public static final String SYNC_NOTIFICATIONS = "sync_notifications";
    public static final String API_VERSION = "apiVersion";

    //Default values:
    public static final int DEFAULT_UPDATE_FREQUENCY = 1440; // (1 day in minutes)
    public static final Boolean DEFAULT_BACKGROUND_SYNC = true;
    public static final Boolean DEFAULT_CRASH_REPORTS = true;
    public static final Boolean DEFAULT_SYNC_NOTIFICATIONS = true;
    public static final String DEFAULT_API_VERSION = "2.27";


    private final SharedPreferences sharedPreferences;

    public AppPreferencesImpl(Context context) {
        isNull(context, "context must not be null");
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        //sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public long getLastSynced() {
        return sharedPreferences.getLong(SYNC_DATE, 0L);
    }

    @Override
    public boolean setLastSynced(long date) {
        return sharedPreferences.edit().putLong(SYNC_DATE, date).commit();
    }

    @Override
    public void setBackgroundSyncFrequency(int minutes) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(UPDATE_FREQUENCY, minutes);
        editor.apply();
    }

    @Override
    public int getBackgroundSyncFrequency() {
        return sharedPreferences.getInt(UPDATE_FREQUENCY, DEFAULT_UPDATE_FREQUENCY);
    }

    @Override
    public void setBackgroundSyncState(Boolean enabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(BACKGROUND_SYNC, enabled);
        editor.apply();
    }

    @Override
    public void setSyncNotifications(Boolean enabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SYNC_NOTIFICATIONS, enabled);
        editor.apply();
    }

    @Override
    public boolean getSyncNotifications() {
        return sharedPreferences.getBoolean(SYNC_NOTIFICATIONS, DEFAULT_SYNC_NOTIFICATIONS);
    }

    @Override
    public boolean getBackgroundSyncState() {
        return sharedPreferences.getBoolean(BACKGROUND_SYNC, DEFAULT_BACKGROUND_SYNC);
    }

    @Override
    public boolean getCrashReportsState() {
        return sharedPreferences.getBoolean(CRASH_REPORTS, DEFAULT_CRASH_REPORTS);
    }

    @Override
    public void setCrashReportsState(Boolean enabled) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(CRASH_REPORTS, enabled);
        editor.apply();
    }

    @Override
    public String getAPiVersion() {
        return sharedPreferences.getString(API_VERSION, DEFAULT_API_VERSION);
    }

    @Override
    public void setApiVersion(String version) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(API_VERSION, version);
        editor.apply();
    }
}