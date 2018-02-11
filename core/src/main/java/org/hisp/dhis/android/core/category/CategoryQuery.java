package org.hisp.dhis.android.core.category;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseQuery;

import java.util.Set;
import java.util.TreeSet;


@AutoValue
public abstract class CategoryQuery extends BaseQuery {

    public static CategoryQuery.Builder builder() {
        return new AutoValue_CategoryQuery.Builder();
    }

    @NonNull
    public static CategoryQuery defaultQuery() {
        return defaultQueryBuilder().build();
    }

    @NonNull
    public static CategoryQuery defaultQuery(boolean isTranslationOn,
            String translationLocale) {

        return defaultQueryBuilder()
                .isTranslationOn(isTranslationOn)
                .translationLocale(translationLocale)
                .build();
    }

    private static Builder defaultQueryBuilder() {
        return builder()
                .uIds(new TreeSet<String>())
                .isPaging(DEFAULT_IS_PAGING)
                .pageSize(DEFAULT_PAGE_SIZE)
                .page(DEFAULT_PAGE)
                .isTranslationOn(DEFAULT_IS_TRANSLATION_ON)
                .translationLocale(DEFAULT_TRANSLATION_LOCALE);
    }

    public static CategoryQuery defaultQuery(Set<String> uIds, boolean isTranslationOn,
            @NonNull String translationLocale) {

        return defaultQueryBuilder()
                .uIds(uIds)
                .isTranslationOn(isTranslationOn)
                .translationLocale(translationLocale)
                .build();
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseQuery.Builder<Builder> {

        public abstract CategoryQuery build();
    }
}
