package org.hisp.dhis.android.core.systeminfo;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseQuery;

@AutoValue
public abstract class SystemInfoQuery extends BaseQuery {


    public static SystemInfoQuery.Builder builder() {
        return new AutoValue_SystemInfoQuery.Builder();
    }

    public static SystemInfoQuery defaultQuery() {
        return defaultQueryBuilder().build();
    }

    public static SystemInfoQuery defaultQuery(boolean isTranslationOn,
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
                .isPaging(false)
                .isTranslationOn(DEFAULT_IS_TRANSLATION_ON)
                .translationLocale(DEFAULT_TRANSLATION_LOCALE);
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseQuery.Builder<SystemInfoQuery.Builder> {

        public abstract SystemInfoQuery build();
    }

}
