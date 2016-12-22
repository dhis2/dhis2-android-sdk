package org.hisp.dhis.android.core.user;

import org.hisp.dhis.android.core.common.BaseModelContract;

public final class UserOrganisationUnitLinkContract {
    public interface Columns extends BaseModelContract.Columns {
        String USER = "user";
        String ORGANISATION_UNIT = "organisationUnit";
        String ORGANISATION_UNIT_SCOPE = "organisationUnitScope";
    }
}
