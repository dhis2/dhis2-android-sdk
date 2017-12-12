package org.hisp.dhis.android.core.category;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseQuery;


@AutoValue
public abstract class CategoryQuery extends BaseQuery {

    public static final int DEFAULT_PAGE_SIZE = 50;


    public static CategoryQuery.Builder builder() {
        return new AutoValue_CategoryQuery.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseQuery.Builder<Builder> {

        public abstract CategoryQuery build();
    }
}
