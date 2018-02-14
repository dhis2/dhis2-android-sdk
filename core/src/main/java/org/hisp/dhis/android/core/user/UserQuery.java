package org.hisp.dhis.android.core.user;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseQuery;

import java.util.TreeSet;

@AutoValue
public abstract class UserQuery extends BaseQuery {

    public static UserQuery.Builder builder() {
        return new AutoValue_UserQuery.Builder();
    }

    public static UserQuery defaultQuery() {
        return defaultQueryBuilder().build();
    }

    public static UserQuery defaultQuery(boolean isTranslationOn,
            String translationLocale) {

        return defaultQueryBuilder()
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
    public static abstract class Builder extends BaseQuery.Builder<UserQuery.Builder> {

        public abstract UserQuery build();
    }

}
