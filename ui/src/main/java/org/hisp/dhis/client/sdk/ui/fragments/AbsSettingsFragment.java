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

package org.hisp.dhis.client.sdk.ui.fragments;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import org.hisp.dhis.client.sdk.ui.R;
import org.hisp.dhis.client.sdk.ui.SettingPreferences;
import org.hisp.dhis.client.sdk.ui.activities.BaseActivity;
import org.hisp.dhis.client.sdk.ui.activities.OnBackPressedCallback;
import org.hisp.dhis.client.sdk.ui.activities.OnBackPressedFromFragmentCallback;

public abstract class AbsSettingsFragment extends PreferenceFragmentCompat
        implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener,
        OnBackPressedCallback {
    private Preference backgroundSynchronization;
    private ListPreference synchronizationPeriod;
    private Preference syncNotifications;
    private Preference crashReports;
    private OnBackPressedFromFragmentCallback onBackPressedFromFragmentCallback;

    @Override
    public void onCreatePreferences(Bundle bundle, String string) {
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        backgroundSynchronization = findPreference(SettingPreferences.BACKGROUND_SYNCHRONIZATION);
        backgroundSynchronization.setOnPreferenceChangeListener(this);
        backgroundSynchronization.setOnPreferenceClickListener(this);

        synchronizationPeriod = (ListPreference) findPreference(SettingPreferences.SYNCHRONIZATION_PERIOD);
        synchronizationPeriod.setSummary(getString(R.string.synchronization_period_description) + " " + synchronizationPeriod.getEntry());
        synchronizationPeriod.setOnPreferenceChangeListener(this);
        synchronizationPeriod.setOnPreferenceClickListener(this);

        syncNotifications = findPreference(SettingPreferences.SYNC_NOTIFICATIONS);
        syncNotifications.setOnPreferenceChangeListener(this);
        syncNotifications.setOnPreferenceClickListener(this);

        crashReports = findPreference(SettingPreferences.CRASH_REPORTS);
        crashReports.setOnPreferenceChangeListener(this);
        crashReports.setOnPreferenceClickListener(this);
    }

    @Override
    public void onAttach(Context context) {

        if (context instanceof BaseActivity) {
            ((BaseActivity) context).setOnBackPressedCallback(this);
        }

        if (context instanceof OnBackPressedFromFragmentCallback) {
            onBackPressedFromFragmentCallback = (OnBackPressedFromFragmentCallback) context;
        }
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        // nullifying callback references
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).setOnBackPressedCallback(null);
        }

        onBackPressedFromFragmentCallback = null;

        super.onDetach();
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case SettingPreferences.BACKGROUND_SYNCHRONIZATION: {
                return onBackgroundSynchronizationClick();
            }
            case SettingPreferences.SYNCHRONIZATION_PERIOD: {
                return onSynchronizationPeriodClick();
            }
            case SettingPreferences.CRASH_REPORTS: {
                return onCrashReportsClick();
            }
        }
        return false;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object object) {
        switch (preference.getKey()) {
            case SettingPreferences.BACKGROUND_SYNCHRONIZATION: {
                return onBackgroundSynchronizationChanged((boolean) object);
            }
            case SettingPreferences.SYNCHRONIZATION_PERIOD: {
                if (object instanceof String) {
                    CharSequence newValue = synchronizationPeriod.getEntries()[synchronizationPeriod.findIndexOfValue((String) object)];
                    synchronizationPeriod.setSummary(getString(R.string.synchronization_period_description) + " " + newValue);
                    return onSynchronizationPeriodChanged((String) object);
                }
            }
            case SettingPreferences.SYNC_NOTIFICATIONS: {
                if (!(boolean) (object)) {
                    NotificationManager notificationManager =
                            (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancelAll();
                }
                return onSyncNotificationsChanged((boolean) object);
            }
            case SettingPreferences.CRASH_REPORTS: {
                return onCrashReportsChanged((boolean) object);
            }
        }
        return false;
    }

    protected Preference getCrashReports() {
        return crashReports;
    }

    protected Preference getSynchronizationPeriod() {
        return synchronizationPeriod;
    }

    protected Preference getBackgroundSynchronization() {
        return backgroundSynchronization;
    }

    public abstract boolean onBackgroundSynchronizationClick();

    public abstract boolean onBackgroundSynchronizationChanged(boolean isEnabled);

    public abstract boolean onSynchronizationPeriodClick();

    public abstract boolean onSyncNotificationsChanged(boolean isEnabled);

    public abstract boolean onSynchronizationPeriodChanged(String newPeriod);

    public abstract boolean onCrashReportsClick();

    public abstract boolean onCrashReportsChanged(boolean isEnabled);

    @Override
    public boolean onBackPressed() {
        if (onBackPressedFromFragmentCallback != null) {
            onBackPressedFromFragmentCallback.onBackPressedFromFragment();
            return false;
        }
        return true;
    }
}
