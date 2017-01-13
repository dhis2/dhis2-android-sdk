package org.hisp.dhis.android.core.constant;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

public interface ConstantStore {

    long insert(
            @Nullable String uid, @Nullable String code, @NonNull String name,
            @Nullable String displayName, @Nullable Date created,
            @Nullable Date lastUpdated, @NonNull String value
    );

    void close();
}
