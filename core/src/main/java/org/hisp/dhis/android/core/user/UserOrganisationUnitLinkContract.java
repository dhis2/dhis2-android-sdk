package org.hisp.dhis.android.core.user;

import android.content.ContentUris;
import android.net.Uri;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.commons.BaseModelContract;
import org.hisp.dhis.android.core.database.DbContract;
import org.hisp.dhis.android.core.database.DbUtils;

public final class UserOrganisationUnitLinkContract {
    public static final String USER_ORGANISATION_UNIT_LINKS = "userOrganisationUnits";
    public static final String USER_ORGANISATION_UNIT_LINKS_ID = USER_ORGANISATION_UNIT_LINKS + "/#";

    public static final String CONTENT_TYPE_DIR = DbUtils.directoryType(USER_ORGANISATION_UNIT_LINKS);
    public static final String CONTENT_TYPE_ITEM = DbUtils.itemType(USER_ORGANISATION_UNIT_LINKS_ID);

    @NonNull
    public static Uri userOrganisationUnits() {
        return Uri.withAppendedPath(DbContract.AUTHORITY_URI, USER_ORGANISATION_UNIT_LINKS);
    }

    @NonNull
    public static Uri userOrganisationUnits(long id) {
        return ContentUris.withAppendedId(userOrganisationUnits(), id);
    }

    public interface Columns extends BaseModelContract.Columns {
        String USER = "user";
        String ORGANISATION_UNIT = "organisationUnit";
    }
}
