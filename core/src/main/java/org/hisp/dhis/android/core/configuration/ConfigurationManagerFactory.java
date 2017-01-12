package org.hisp.dhis.android.core.configuration;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.data.database.DbOpenHelper;

public class ConfigurationManagerFactory implements ConfigurationManager.Factory {

    @NonNull
    private final DbOpenHelper dbOpenHelper;

    private ConfigurationManagerFactory(@NonNull DbOpenHelper dbOpenHelper) {
        this.dbOpenHelper = dbOpenHelper;
    }

    @NonNull
    public static ConfigurationManager.Factory create(@NonNull DbOpenHelper dbOpenHelper) {
        return new ConfigurationManagerFactory(dbOpenHelper);
    }

    @NonNull
    @Override
    public ConfigurationManager create() {
        ConfigurationStore configurationStore = new ConfigurationStoreImpl(
                dbOpenHelper.getWritableDatabase());
        return new ConfigurationManagerImpl(configurationStore);
    }
}
