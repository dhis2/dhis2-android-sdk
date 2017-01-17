package org.hisp.dhis.android.core.configuration;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface ConfigurationStore {
    long save(@NonNull String serverUrl);

    @Nullable
    ConfigurationModel query();

    int delete();
}
