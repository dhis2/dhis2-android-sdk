package org.hisp.dhis.android.core.configuration;

import android.support.annotation.NonNull;

import java.util.List;

public interface ConfigurationStore {
    long insert(@NonNull String serverUrl);

    @NonNull
    List<ConfigurationModel> query();

    void close();
}
