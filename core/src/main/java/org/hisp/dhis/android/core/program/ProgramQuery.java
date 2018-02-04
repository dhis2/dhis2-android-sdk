package org.hisp.dhis.android.core.program;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseQuery;

import java.util.Set;
import java.util.TreeSet;

@AutoValue
public abstract class ProgramQuery extends BaseQuery {

    public abstract Set<String> uids();

    public static ProgramQuery.Builder builder() {
        return new AutoValue_ProgramQuery.Builder();
    }

    public static ProgramQuery defaultQuery() {
        return defaultQueryBuilder().build();
    }

    public static ProgramQuery defaultQuery(Set<String> uids, boolean isTranslationOn,
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
                .page(0)
                .pageSize(DEFAULT_PAGE_SIZE)
                .isPaging(false)
                .isTranslationOn(DEFAULT_IS_TRANSLATION_ON)
                .translationLocale(DEFAULT_TRANSLATION_LOCALE);
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseQuery.Builder<ProgramQuery.Builder> {

        public abstract Builder uids(Set<String> uids);

        public abstract ProgramQuery build();
    }

}
