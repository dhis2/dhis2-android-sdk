package org.hisp.dhis.android.core.category;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseQuery;

import java.util.Set;


@AutoValue
public abstract class CategoryQuery extends BaseQuery {

    public static CategoryQuery.Builder builder() {
        return new AutoValue_CategoryQuery.Builder();
    }

    @NonNull
    public static CategoryQuery defaultQuery() {
        return builder().paging(false).pageSize(
                CategoryQuery.DEFAULT_PAGE_SIZE).page(1).build();
    }

    @NonNull
    public static CategoryQuery defaultQuery(Set<String> uIds) {
        return builder().paging(false).pageSize(
                CategoryQuery.DEFAULT_PAGE_SIZE).page(1)
                .uIds(uIds).build();
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseQuery.Builder<Builder> {

        public abstract CategoryQuery build();
    }
}
