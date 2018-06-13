package org.hisp.dhis.android.core.common;

import com.google.auto.value.AutoValue;

import java.util.Set;

import javax.annotation.Nullable;

@AutoValue
public abstract class UidsQuery extends BaseQuery {

    @Nullable
    public abstract Set<String> uids();

    public static UidsQuery create(Set<String> uids) {
        return new AutoValue_UidsQuery(1, BaseQuery.DEFAULT_PAGE_SIZE, false, uids);
    }
}
