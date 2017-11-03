package org.hisp.dhis.client.sdk.ui;


public interface AppPreferences {

    long getLastSynced();

    boolean setLastSynced(long date);

    void setBackgroundSyncFrequency(int frequency);

    int getBackgroundSyncFrequency();

    void setBackgroundSyncState(Boolean enabled);

    void setSyncNotifications(Boolean enabled);

    boolean getSyncNotifications();

    boolean getBackgroundSyncState();

    boolean getCrashReportsState();

    void setCrashReportsState(Boolean enabled);

    String getAPiVersion();

    void setApiVersion(String version);
}
