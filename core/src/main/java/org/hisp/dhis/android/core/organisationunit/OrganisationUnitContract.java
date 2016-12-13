package org.hisp.dhis.android.core.organisationunit;

import android.content.ContentUris;
import android.net.Uri;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.commons.BaseNameableObjectContract;
import org.hisp.dhis.android.core.data.database.DbContract;
import org.hisp.dhis.android.core.data.database.DbUtils;
import org.hisp.dhis.android.core.user.UserContract;


// ToDo: Add CONTENT_RESOURCE constants (strings)
public class OrganisationUnitContract {
    // ContentProvider related properties
    public static final String ORGANISATION_UNITS = "organisationUnits";
    public static final String ORGANISATION_UNITS_ID = ORGANISATION_UNITS + "/#";
    public static final String ORGANISATION_UNITS_ID_USERS = ORGANISATION_UNITS + "/*/" +
            UserContract.USERS;

    public static final String CONTENT_TYPE_DIR = DbUtils.directoryType(ORGANISATION_UNITS);
    public static final String CONTENT_TYPE_ITEM = DbUtils.itemType(ORGANISATION_UNITS);

    @NonNull
    public static Uri organisationUnits() {
        return Uri.withAppendedPath(DbContract.AUTHORITY_URI, ORGANISATION_UNITS);
    }

    @NonNull
    public static Uri organisationUnits(long id) {
        return ContentUris.withAppendedId(organisationUnits(), id);
    }

    @NonNull
    public static Uri users(@NonNull String organisationUnit) {
        return organisationUnits().buildUpon().appendPath(organisationUnit)
                .appendPath(UserContract.USERS).build();
    }

    public interface Columns extends BaseNameableObjectContract.Columns {
        String PATH = "path";
        String OPENING_DATE = "openingDate";
        String CLOSED_DATE = "closedDate";
        String PARENT = "parent";
        String LEVEL = "level";
    }
}
