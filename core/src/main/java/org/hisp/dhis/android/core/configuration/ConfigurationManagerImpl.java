package org.hisp.dhis.android.core.configuration;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

final class ConfigurationManagerImpl implements ConfigurationManager {

    @NonNull
    private final ConfigurationStore configurationStore;

    public ConfigurationManagerImpl(@NonNull ConfigurationStore configurationStore) {
        this.configurationStore = configurationStore;
    }

    @Override
    public void configure(@NonNull ConfigurationModel configurationModel) {
        if (configurationModel == null) {
            throw new IllegalArgumentException("configurationModel == null");
        }

        if (configurationModel.serverUrl() == null) {
            throw new IllegalArgumentException("configurationModel.serverUrl() == null");
        }

        if (configurationModel.serverUrl().isEmpty()) {
            throw new IllegalArgumentException("configurationModel.serverUrl() must not be empty");
        }

        List<ConfigurationModel> configurations = configurationStore.query();
        if (!configurations.isEmpty()) {
            throw new IllegalArgumentException("Has been already configured with: " + configurations.get(0));
        }

        configurationStore.save(configurationModel);
    }

    @Nullable
    @Override
    public ConfigurationModel configuration() {
        List<ConfigurationModel> configurations = configurationStore.query();

        if (!configurations.isEmpty()) {
            return configurations.get(0);
        }

        return null;
    }
}
