package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseQuery;

import java.util.Set;
import java.util.TreeSet;

@AutoValue
public abstract class TrackedEntityAttributeQuery extends BaseQuery {

    public static Builder builder() {
        return new AutoValue_TrackedEntityAttributeQuery.Builder();
    }

    public static TrackedEntityAttributeQuery defaultQuery() {
        return defaultQueryBuilder().build();
    }

    public static TrackedEntityAttributeQuery defaultQuery(Set<String> uIds,
            boolean isTranslationOn,
            @NonNull String translationLocale) {

        return defaultQueryBuilder()
                .uIds(uIds)
                .isTranslationOn(isTranslationOn)
                .translationLocale(translationLocale)
                .build();
    }

    private static Builder defaultQueryBuilder() {
        return builder()
                .page(DEFAULT_PAGE)
                .pageSize(DEFAULT_PAGE_SIZE)
                .isPaging(DEFAULT_IS_PAGING)
                .isTranslationOn(DEFAULT_IS_TRANSLATION_ON)
                .translationLocale(DEFAULT_TRANSLATION_LOCALE)
                .uIds(new TreeSet<String>());
    }

    @AutoValue.Builder
    public static abstract class Builder extends
            BaseQuery.Builder<TrackedEntityAttributeQuery.Builder> {

        public abstract TrackedEntityAttributeQuery build();
    }
}
