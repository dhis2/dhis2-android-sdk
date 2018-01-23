package org.hisp.dhis.android.core.common;

import android.support.annotation.Nullable;

import java.util.Set;

public abstract class BaseQuery {
    public static final int DEFAULT_PAGE_SIZE = 50;

    @Nullable
    public abstract Set<String> uIds();

    public abstract int page();

    public abstract int pageSize();

    public abstract boolean paging();

    protected static abstract class Builder<T extends BaseQuery.Builder> {
        @Nullable
        public abstract T uIds(Set<String> uIds);

        public abstract T page(int page);

        public abstract T pageSize(int pageSize);

        public abstract T paging(boolean paging);
    }
}
