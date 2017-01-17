package org.hisp.dhis.android.core.configuration;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

final class ConfigurationManagerImpl implements ConfigurationManager {

    @NonNull
    private final ConfigurationStore configurationStore;

    public ConfigurationManagerImpl(@NonNull ConfigurationStore configurationStore) {
        this.configurationStore = configurationStore;
    }

    @NonNull
    @Override
    public ConfigurationModel save(@NonNull String serverUrl) {
        if (serverUrl == null) {
            throw new IllegalArgumentException("serverUrl == null");
        }

        if (serverUrl.isEmpty()) {
            throw new IllegalArgumentException("serverUrl() must not be empty");
        }

        long configurationId = configurationStore.save(serverUrl);
        return ConfigurationModel.builder()
                .id(configurationId)
                .serverUrl(serverUrl)
                .build();
    }

    @Nullable
    @Override
    public ConfigurationModel get() {
        return configurationStore.query();
    }

    @Override
    public int remove() {
        return configurationStore.delete();
    }
}
