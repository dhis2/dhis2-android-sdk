package org.hisp.dhis.android.core.organisationunit;

import org.hisp.dhis.android.core.commons.database.BaseNameableObjectContract;
import org.hisp.dhis.android.core.commons.database.DbUtils;

public class OrganisationUnitContract {
    // ContentProvider related properties
    public static final String ORGANISATION_UNITS = "OrganisationUnits";
    public static final String ORGANISATION_UNITS_ID = ORGANISATION_UNITS + "/#";

    public static final String CONTENT_TYPE_DIR = DbUtils.directoryType(ORGANISATION_UNITS);
    public static final String CONTENT_TYPE_ITEM = DbUtils.itemType(ORGANISATION_UNITS);

    // sql schema
    public static final String ORGANISATION_UNIT = "OrganisationUnit";
    public static final String CREATE_TABLE = "CREATE TABLE " + ORGANISATION_UNIT + " (" +
            Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            Columns.UID + " TEXT NOT NULL UNIQUE," +
            Columns.CODE + " TEXT," +
            Columns.NAME + " TEXT," +
            Columns.DISPLAY_NAME + " TEXT," +
            Columns.CREATED + " TEXT," +
            Columns.LAST_UPDATED + " TEXT," +
            Columns.SHORT_NAME + " TEXT," +
            Columns.DISPLAY_SHORT_NAME + " TEXT," +
            Columns.DESCRIPTION + " TEXT," +
            Columns.DISPLAY_DESCRIPTION + " TEXT," +
            Columns.PATH + " TEXT," +
            Columns.OPENING_DATE + " TEXT," +
            Columns.CLOSED_DATE + " TEXT," +
            Columns.LEVEL + " INTEGER," +
            Columns.PARENT + " TEXT NOT NULL," +
            "FOREIGN KEY (" + Columns.PARENT + ") REFERENCES " + ORGANISATION_UNIT + " (" + Columns.UID + ") ON DELETE CASCADE" +
            ");";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + ORGANISATION_UNIT;

    public interface Columns extends BaseNameableObjectContract.Columns {
        String PATH = "path";
        String OPENING_DATE = "openingDate";
        String CLOSED_DATE = "closedDate";
        String LEVEL = "level";
        String PARENT = "parent";
    }
}
