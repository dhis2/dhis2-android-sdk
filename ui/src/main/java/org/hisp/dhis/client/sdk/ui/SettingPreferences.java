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

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.preference.PreferenceManager;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;


public final class SettingPreferences {
    public static final String BACKGROUND_SYNCHRONIZATION = "backgroundSynchronization";
    public static final String SYNCHRONIZATION_PERIOD = "synchronizationPeriod";
    public static final String CRASH_REPORTS = "crashReports";
    public static final String SYNC_DATE = "syncDate";
    public static final String UPDATE_FREQUENCY = "update_frequency";
    public static final String BACKGROUND_SYNC = "background_sync";

    //Default values:
    public static final int DEFAULT_UPDATE_FREQUENCY = 1440; //one hour
    public static final Boolean DEFAULT_BACKGROUND_SYNC = true;
    public static final Boolean DEFAULT_CRASH_REPORTS = true;

    private static SettingPreferences settingPreferences;
    private SharedPreferences sharedPreferences;

    private SettingPreferences(Context context) {
        isNull(context, "context must not be null");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void init(Context context) {
        settingPreferences = new SettingPreferences(context);
    }

    private static SettingPreferences getInstance() {
        isNull(settingPreferences, "call init(context) first");
        return settingPreferences;
    }

    @Nullable
    public static long getLastSynced() {
        return getInstance().sharedPreferences.getLong(SYNC_DATE, 0l);
    }

    public static void setLastSynched(@NonNull long date) {
        getInstance().sharedPreferences.edit().putLong(SYNC_DATE, date).commit();
    }

    public static void setBackgroundSyncFrequency(int frequency) {
        SharedPreferences.Editor editor = getInstance().sharedPreferences.edit();
        editor.putInt(UPDATE_FREQUENCY, frequency);
        editor.apply();
    }

    public static int getBackgroundSyncFrequency() {
        return getInstance().sharedPreferences.getInt(UPDATE_FREQUENCY, DEFAULT_UPDATE_FREQUENCY);
    }

    public static void setBackgroundSyncState(Boolean enabled) {
        SharedPreferences.Editor editor = getInstance().sharedPreferences.edit();
        editor.putBoolean(BACKGROUND_SYNC, enabled);
        editor.apply();
    }

    public static Boolean getBackgroundSyncState() {
        return getInstance().sharedPreferences.getBoolean(BACKGROUND_SYNC, DEFAULT_BACKGROUND_SYNC);
    }

    public static Boolean getCrashReportsState() {
        return getInstance().sharedPreferences.getBoolean(CRASH_REPORTS, DEFAULT_CRASH_REPORTS);
    }

    public static void setCrashReportsState(Boolean enabled) {
        SharedPreferences.Editor editor = getInstance().sharedPreferences.edit();
        editor.putBoolean(CRASH_REPORTS, enabled);
        editor.apply();
    }
}
