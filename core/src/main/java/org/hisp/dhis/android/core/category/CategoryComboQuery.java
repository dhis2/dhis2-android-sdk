package org.hisp.dhis.android.core.category;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseQuery;

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
                .isPaging(false)
                .pageSize(DEFAULT_PAGE_SIZE)
                .isTranslationOn(DEFAULT_IS_TRANSLATION_ON)
                .translationLocale(DEFAULT_TRANSLATION_LOCALE)
                .page(0);
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseQuery.Builder<Builder> {

        public abstract CategoryComboQuery build();
    }
}
