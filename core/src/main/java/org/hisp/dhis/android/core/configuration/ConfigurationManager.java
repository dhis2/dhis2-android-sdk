package org.hisp.dhis.android.core.configuration;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface ConfigurationManager {
    void configure(@NonNull ConfigurationModel configurationModel);

    @Nullable
    ConfigurationModel configuration();

    interface Factory {
        @NonNull
        ConfigurationManager create();
    }
}
