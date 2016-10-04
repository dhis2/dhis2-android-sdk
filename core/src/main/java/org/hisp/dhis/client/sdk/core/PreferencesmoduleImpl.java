package org.hisp.dhis.client.sdk.core;

import android.content.Context;

public class PreferencesModuleImpl implements PreferencesModule {
    private final ConfigurationPreferences configurationPreferences;
    private final LastUpdatedPreferences lastUpdatedPreferences;
    private final UserPreferences userPreferences;
    private final SystemInfoPreferences systemInfoPreferences;

    public PreferencesModuleImpl(Context context) {
        configurationPreferences = new ConfigurationPreferencesImpl(context);
        lastUpdatedPreferences = new LastUpdatedPreferencesImpl(context);
        userPreferences = new UserPreferencesImpl(context);
        systemInfoPreferences = new SystemInfoPreferencesImpl(context);
    }

    @Override
    public ConfigurationPreferences getConfigurationPreferences() {
        return configurationPreferences;
    }

    @Override
    public LastUpdatedPreferences getLastUpdatedPreferences() {
        return lastUpdatedPreferences;
    }

    @Override
    public UserPreferences getUserPreferences() {
        return userPreferences;
    }

    @Override
    public SystemInfoPreferences getSystemInfoPreferences() {
        return systemInfoPreferences;
    }

    @Override
    public boolean clearAllPreferences() {
        return configurationPreferences.clear() &&
                lastUpdatedPreferences.clear() &&
                userPreferences.clear() &&
                systemInfoPreferences.clear();
    }
}

