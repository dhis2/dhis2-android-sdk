package org.hisp.dhis.android.core.trackedentity;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseQuery;

import java.util.Set;
import java.util.TreeSet;

@AutoValue
public abstract class TrackedEntityQuery extends BaseQuery {

    public abstract Set<String> uids();


    public static TrackedEntityQuery.Builder builder() {
        return new AutoValue_TrackedEntityQuery.Builder();
    }

    public static TrackedEntityQuery defaultQuery() {
        return defaultQueryBuilder().build();
    }

    public static TrackedEntityQuery defaultQuery(Set<String> uids, boolean isTranslationOn,
            String translationLocale) {

        return defaultQueryBuilder()
                .uids(uids)
                .isTranslationOn(isTranslationOn)
                .translationLocale(translationLocale)
                .build();
    }

    private static Builder defaultQueryBuilder() {
        return builder()
                .uids(new TreeSet<String>())
                .page(DEFAULT_PAGE)
                .pageSize(DEFAULT_PAGE_SIZE)
                .isPaging(false)
                .isTranslationOn(DEFAULT_IS_TRANSLATION_ON)
                .translationLocale(DEFAULT_TRANSLATION_LOCALE);
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseQuery.Builder<TrackedEntityQuery.Builder> {

        public abstract Builder uids(Set<String> uids);

        public abstract TrackedEntityQuery build();
    }

}
