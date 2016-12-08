package org.hisp.dhis.android.core.organisationunit;

import android.content.ContentUris;
import android.net.Uri;
import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.commons.BaseNameableObjectContract;
import org.hisp.dhis.android.core.database.DbContract;
import org.hisp.dhis.android.core.database.DbUtils;


// ToDo: Add CONTENT_RESOURCE constants (strings)
public class OrganisationUnitContract {
    // ContentProvider related properties
    public static final String ORGANISATION_UNITS = "organisationUnits";
    public static final String ORGANISATION_UNITS_ID = ORGANISATION_UNITS + "/#";

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

    public interface Columns extends BaseNameableObjectContract.Columns {
        String PATH = "path";
        String OPENING_DATE = "openingDate";
        String CLOSED_DATE = "closedDate";
        String PARENT = "parent";
        String LEVEL = "level";
    }
}
