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
import android.support.v7.preference.PreferenceManager;

import static org.hisp.dhis.client.sdk.utils.Preconditions.isNull;

public final class SettingPreferences {
    public static final String BACKGROUND_SYNCHRONIZATION = "backgroundSynchronization";
    public static final String SYNCHRONIZATION_PERIOD = "synchronizationPeriod";
    public static final String SYNC_NOTIFICATIONS = "syncNotifications";
    public static final String CRASH_REPORTS = "crashReports";
    public static final boolean DEFAULT_BACKGROUND_SYNC = false;
    public static final boolean DEFAULT_CRASH_REPORTS = false;

    private static final String DEFAULT_UPDATE_FREQUENCY = "hoursTwentyFour";

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

    public static boolean backgroundSynchronization() {
        return getInstance().sharedPreferences.getBoolean(BACKGROUND_SYNCHRONIZATION,
                DEFAULT_BACKGROUND_SYNC);
    }

    public static String synchronizationPeriod() {
        return getInstance().sharedPreferences.getString(SYNCHRONIZATION_PERIOD,
                DEFAULT_UPDATE_FREQUENCY);
    }

    public static Boolean crashReports() {
        return getInstance().sharedPreferences.getBoolean(CRASH_REPORTS,
                DEFAULT_CRASH_REPORTS);
    }
}
