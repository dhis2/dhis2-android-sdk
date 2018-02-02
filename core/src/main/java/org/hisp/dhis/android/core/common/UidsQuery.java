package org.hisp.dhis.android.core.common;

import com.google.auto.value.AutoValue;

import java.util.Set;

@AutoValue
public abstract class UidsQuery extends BaseQuery {
    public abstract Set<String> uids();

    abstract Integer limit();

    @Override
    boolean isValid() {
        return limit() == null || uids().size() <= limit();
    }

    public static UidsQuery create(Set<String> uids, Integer limit) {
        return new AutoValue_UidsQuery(1, BaseQuery.DEFAULT_PAGE_SIZE, false, uids, limit);
    }
}
