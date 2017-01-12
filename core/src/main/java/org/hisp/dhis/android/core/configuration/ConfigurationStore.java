package org.hisp.dhis.android.core.configuration;

import android.support.annotation.NonNull;

import java.util.List;

public interface ConfigurationStore {
    long save(@NonNull ConfigurationModel configurationModel);

    @NonNull
    List<ConfigurationModel> query();
}
