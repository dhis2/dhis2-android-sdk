package org.hisp.dhis.android.core.dataelement;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseQuery;
import java.util.Set;
import java.util.TreeSet;

@AutoValue
public abstract class DataElementQuery extends BaseQuery {

    public static Builder builder() {
        return new AutoValue_DataElementQuery.Builder();
    }

    public static DataElementQuery defaultQuery() {
        return defaultQueryBuilder().build();
    }

    public static DataElementQuery defaultQuery(Set<String> uIds, boolean isTranslationOn,
            @NonNull String translationLocale) {

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
    public static abstract class Builder extends BaseQuery.Builder<DataElementQuery.Builder> {

        public abstract DataElementQuery build();
    }
}
