package org.hisp.dhis.android.core.user;

import org.hisp.dhis.android.core.organisationunit.OrganisationUnitContract;

public final class UserOrganisationUnitContract {
    private static final String USER_ORGANISATION_UNIT = "UserOrganisationUnit";

    public interface Columns {
        String USER = "user";
        String ORGANISATION_UNIT = "organisationUnit";
    }

    public static final String CREATE_TABLE = "CREATE TABLE " + USER_ORGANISATION_UNIT + " (" +
            Columns.USER + " TEXT NOT NULL," +
            Columns.ORGANISATION_UNIT + " TEXT NOT NULL," +
            "FOREIGN KEY (" + Columns.USER + ") REFERENCES " + UserContract.USER +
            " (" + UserContract.Columns.UID + ") ON DELETE CASCADE," +
            "FOREIGN KEY (" + Columns.ORGANISATION_UNIT + ") REFERENCES " + OrganisationUnitContract.ORGANISATION_UNIT +
            " (" + OrganisationUnitContract.Columns.UID + ") ON DELETE CASCADE," +
            "PRIMARY KEY (" + Columns.USER + ", " + Columns.ORGANISATION_UNIT + ")" +
            ");";

    public static final String ORGANISATION_UNIT_JOIN = OrganisationUnitContract.ORGANISATION_UNIT +
            "  LEFT OUTER JOIN " + USER_ORGANISATION_UNIT + " ON " + OrganisationUnitContract.ORGANISATION_UNIT + "." +
            OrganisationUnitContract.Columns.UID + " = " + UserOrganisationUnitContract.USER_ORGANISATION_UNIT + "." + Columns.ORGANISATION_UNIT;

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + USER_ORGANISATION_UNIT;
}
