package org.hisp.dhis.android.core.relationship;

import android.support.annotation.NonNull;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseQuery;

import java.util.Set;
import java.util.TreeSet;

@AutoValue
public abstract class RelationshipTypeQuery extends BaseQuery {

    public static Builder builder() {
        return new AutoValue_RelationshipTypeQuery.Builder();
    }

    public static RelationshipTypeQuery defaultQuery() {
        return defaultQueryBuilder().build();
    }

    public static RelationshipTypeQuery defaultQuery(Set<String> uIds, boolean isTranslationOn,
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
    public static abstract class Builder extends BaseQuery.Builder<RelationshipTypeQuery.Builder> {

        public abstract RelationshipTypeQuery build();
    }

}
