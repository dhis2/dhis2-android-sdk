package org.hisp.dhis.android.core.configuration;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface ConfigurationManager {

    @NonNull
    ConfigurationModel save(@NonNull String serverUrl);

    @Nullable
    ConfigurationModel get();

    int remove();
}
