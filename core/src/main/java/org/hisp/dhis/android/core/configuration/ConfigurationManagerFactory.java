package org.hisp.dhis.android.core.configuration;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DbOpenHelper;

public final class ConfigurationManagerFactory {

    private ConfigurationManagerFactory() {
        // no instances
    }

    @NonNull
    public static ConfigurationManager create(@NonNull DbOpenHelper dbOpenHelper) {
        if (dbOpenHelper == null) {
            throw new IllegalArgumentException("dbOpenHelper == null");
        }

        ConfigurationStore configurationStore = new ConfigurationStoreImpl(
                dbOpenHelper.getWritableDatabase());
        return new ConfigurationManagerImpl(configurationStore);
    }
}
