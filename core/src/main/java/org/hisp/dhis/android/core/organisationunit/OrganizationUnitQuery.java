package org.hisp.dhis.android.core.organisationunit;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseQuery;
import org.hisp.dhis.android.core.user.User;

@AutoValue
public abstract class OrganizationUnitQuery extends BaseQuery {

    public abstract User user();

    public static OrganizationUnitQuery.Builder builder() {
        return new AutoValue_OrganizationUnitQuery.Builder();
    }

    public static OrganizationUnitQuery defaultQuery(User user) {
        return defaultQueryBuilder()
                .user(user)
                .build();
    }

    public static OrganizationUnitQuery defaultQuery(User user, boolean isTranslationOn,
            String translationLocale) {

        return defaultQueryBuilder()
                .user(user)
                .isTranslationOn(isTranslationOn)
                .translationLocale(translationLocale)
                .build();
    }

    private static Builder defaultQueryBuilder() {
        return builder()
                .user(null)
                .page(0)
                .pageSize(DEFAULT_PAGE_SIZE)
                .isPaging(false)
                .isTranslationOn(DEFAULT_IS_TRANSLATION_ON)
                .translationLocale(DEFAULT_TRANSLATION_LOCALE);
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseQuery.Builder<OrganizationUnitQuery.Builder> {

        public abstract Builder user(User user);

        public abstract OrganizationUnitQuery build();
    }

}
