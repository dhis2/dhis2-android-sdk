package org.hisp.dhis.client.sdk.ui.bindings.util;

import android.content.ContentResolver;

/**
 * This class unites our calls to Android System Settings. System settings are exposed as static methods, mockable during testing.
 */
public class SystemSettings {
    /**
     * @android.support.annotation.RequiresPermission(value="android.permission.READ_SYNC_SETTINGS") public static boolean getMasterSyncAutomatically()
     * Gets the master auto-sync setting that applies to all the providers and accounts. If this is false then the per-provider auto-sync setting is ignored.
     * This method requires the caller to hold the permission android.Manifest.permission.READ_SYNC_SETTINGS.
     * Returns:
     * the master auto-sync setting that applies to all the providers and accounts
     */
    public static boolean getMasterSyncAutomatically() {
        return ContentResolver.getMasterSyncAutomatically();
    }
}
