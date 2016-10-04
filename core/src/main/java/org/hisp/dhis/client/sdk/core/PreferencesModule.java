package org.hisp.dhis.client.sdk.core;


public interface PreferencesModule {
    ConfigurationPreferences getConfigurationPreferences();

    LastUpdatedPreferences getLastUpdatedPreferences();

    UserPreferences getUserPreferences();

    SystemInfoPreferences getSystemInfoPreferences();

    boolean clearAllPreferences();
}
