package org.hisp.dhis.android.core.option;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseQuery;

import java.util.Set;
import java.util.TreeSet;


@AutoValue
public abstract class OptionSetQuery extends BaseQuery {

    @Override
    public abstract Set<String> uIds();

    public static OptionSetQuery.Builder builder() {
        return new AutoValue_OptionSetQuery.Builder();
    }

    public static OptionSetQuery defaultQuery() {
        return defaultQueryBuilder().build();
    }

    public static OptionSetQuery defaultQuery(Set<String> uIds, boolean isTranslationOn,
            String translationLocale) {

        return defaultQueryBuilder()
                .uIds(uIds)
                .isTranslationOn(isTranslationOn)
                .translationLocale(translationLocale)
                .build();
    }

    private static Builder defaultQueryBuilder() {
        return builder()
                .uIds(new TreeSet<String>())
                .page(DEFAULT_PAGE)
                .pageSize(DEFAULT_PAGE_SIZE)
                .isPaging(DEFAULT_IS_PAGING)
                .isTranslationOn(DEFAULT_IS_TRANSLATION_ON)
                .translationLocale(DEFAULT_TRANSLATION_LOCALE);
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseQuery.Builder<OptionSetQuery.Builder> {

        @Override
        public abstract Builder uIds(Set<String> uIdsca);

        public abstract OptionSetQuery build();
    }

}
