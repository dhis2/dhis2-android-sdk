package org.hisp.dhis.android.core.category;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseQuery;

import java.util.Set;
import java.util.TreeSet;

@AutoValue
public abstract class CategoryComboQuery extends BaseQuery {

    public static CategoryComboQuery.Builder builder() {
        return new AutoValue_CategoryComboQuery.Builder();
    }

    public static CategoryComboQuery defaultQuery() {
        return defaultQueryBuilder().build();
    }

    public static CategoryComboQuery defaultQuery(boolean isTranslationOn,
            String translationLocale) {

        return defaultQueryBuilder()
                .isTranslationOn(isTranslationOn)
                .translationLocale(translationLocale)
                .build();
    }

    private static Builder defaultQueryBuilder() {
        return builder()
                .isPaging(DEFAULT_IS_PAGING)
                .pageSize(DEFAULT_PAGE_SIZE)
                .isTranslationOn(DEFAULT_IS_TRANSLATION_ON)
                .translationLocale(DEFAULT_TRANSLATION_LOCALE)
                .page(DEFAULT_PAGE)
                .uIds(new TreeSet<String>());
    }

    public static CategoryComboQuery defaultQuery(Set<String> uIds, boolean isTranslationOn,
            @NonNull String translationLocale) {

        return defaultQueryBuilder()
                .uIds(uIds)
                .isTranslationOn(isTranslationOn)
                .translationLocale(translationLocale)
                .build();
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseQuery.Builder<Builder> {

        public abstract CategoryComboQuery build();
    }
}
