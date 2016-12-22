package org.hisp.dhis.android.core.option;

import android.support.annotation.NonNull;

import java.util.Date;

public interface OptionStore {
    long insert(
            @NonNull String uid, @NonNull String code, @NonNull String name,
            @NonNull String displayName, @NonNull Date created, @NonNull Date lastUpdated
    );

    void close();
}
