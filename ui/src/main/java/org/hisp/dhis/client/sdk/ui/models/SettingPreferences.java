package org.hisp.dhis.client.sdk.ui.models;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;

import static org.hisp.dhis.client.sdk.ui.utils.Preconditions.isNull;

public final class SettingPreferences {
    public static final String BACKGROUND_SYNCHRONIZATION = "backgroundSynchronization";
    public static final String SYNCHRONIZATION_PERIOD = "synchronizationPeriod";
    public static final String CRASH_REPORTS = "crashReports";

    private static final String SYNCHRONIZATION_DEFAULT = "hoursTwentyFour";

    private static SettingPreferences settingPreferences;
    private SharedPreferences sharedPreferences;

    private SettingPreferences(Context context) {
        isNull(context, "context must not be null");
        sharedPreferences= PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void init(Context context) {
        settingPreferences = new SettingPreferences(context);
    }

    private static SettingPreferences getInstance() {
        isNull(settingPreferences, "call init(context) first");
        return settingPreferences;
    }

    public static boolean backgroundSynchronization() {
        return getInstance().sharedPreferences.getBoolean(BACKGROUND_SYNCHRONIZATION, false);
    }

    public static String synchronizationPeriod() {
        return getInstance().sharedPreferences.getString(SYNCHRONIZATION_PERIOD,
                SYNCHRONIZATION_DEFAULT);
    }

    public static boolean crashReports() {
        return getInstance().sharedPreferences.getBoolean(CRASH_REPORTS, true);
    }
}
