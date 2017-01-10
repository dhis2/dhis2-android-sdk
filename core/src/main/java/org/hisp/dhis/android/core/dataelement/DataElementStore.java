package org.hisp.dhis.android.core.dataelement;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.ValueType;

import java.util.Date;

public interface DataElementStore {

    long insert(
            @NonNull String uid,
            @Nullable String code,
            @NonNull String name,
            @NonNull String displayName, @NonNull Date created, @NonNull Date lastUpdated,
            @Nullable String shortName, @Nullable String displayShortName,
            @Nullable String description, @Nullable String displayDescription,
            @NonNull ValueType valueType, @Nullable Boolean zeroIsSignificant,
            @Nullable String aggregationOperator, @Nullable String formName,
            @Nullable String numberType, @Nullable String domainType,
            @Nullable String dimension, @Nullable String displayFormName,
            @Nullable String optionSet
    );

    void close();
}
