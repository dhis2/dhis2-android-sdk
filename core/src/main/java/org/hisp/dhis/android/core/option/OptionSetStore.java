package org.hisp.dhis.android.core.option;

import android.support.annotation.NonNull;

import org.hisp.dhis.android.models.common.ValueType;

import java.util.Date;

public interface OptionSetStore {
    long insert(
            @NonNull String uid, @NonNull String code, @NonNull String name,
            @NonNull String displayName, @NonNull Date created, @NonNull Date lastUpdated,
            @NonNull Integer version, @NonNull ValueType valueType
    );

    void close();
}
