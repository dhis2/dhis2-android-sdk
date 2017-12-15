package org.hisp.dhis.android.core.common;

public abstract class BaseQuery {
    public static final int DEFAULT_PAGE_SIZE = 50;

    public abstract int page();

    public abstract int pageSize();

    public abstract boolean paging();

    protected static abstract class Builder<T extends BaseQuery.Builder> {
        public abstract T page(int page);

        public abstract T pageSize(int pageSize);

        public abstract T paging(boolean paging);
    }
}
