package org.hisp.dhis.android.core.organisationunit;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseQuery;
import org.hisp.dhis.android.core.user.User;

@AutoValue
public abstract class OrganizationUnitQuery extends BaseQuery {

    public static final String DEFAULT_UID = "";
    public abstract User user();
    public abstract String uid();


    public static OrganizationUnitQuery.Builder builder() {
        return new AutoValue_OrganizationUnitQuery.Builder();
    }

    public static OrganizationUnitQuery defaultQuery(User user) {
        return defaultQueryBuilder()
                .user(user)
                .build();
    }

    public static OrganizationUnitQuery defaultQuery(User user, boolean isTranslationOn,
            String translationLocale,
            String uid) {

        return defaultQueryBuilder()
                .user(user)
                .isTranslationOn(isTranslationOn)
                .translationLocale(translationLocale)
                .uid(uid)
                .build();
    }

    private static Builder defaultQueryBuilder() {
        return builder()
                .page(DEFAULT_PAGE)
                .pageSize(DEFAULT_PAGE_SIZE)
                .isPaging(DEFAULT_IS_PAGING)
                .isTranslationOn(DEFAULT_IS_TRANSLATION_ON)
                .translationLocale(DEFAULT_TRANSLATION_LOCALE)
                .uid(DEFAULT_UID);

    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseQuery.Builder<OrganizationUnitQuery.Builder> {

        public abstract Builder user(User user);

        public abstract Builder uid(String uid);

        public abstract OrganizationUnitQuery build();
    }

}
