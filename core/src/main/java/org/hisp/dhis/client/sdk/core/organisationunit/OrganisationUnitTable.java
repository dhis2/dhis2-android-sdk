package org.hisp.dhis.client.sdk.core.organisationunit;

import android.net.Uri;

import org.hisp.dhis.client.sdk.core.commons.database.DbContract;
import org.hisp.dhis.client.sdk.core.commons.database.DbUtils;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;

public interface OrganisationUnitTable {
    interface OrganisationUnitColumns extends DbContract.NameableColumns, DbContract.BodyColumn {
        String TABLE_NAME = "organisationUnits";
        String COLUMN_PARENT = "parent";
        String COLUMN_OPENING_DATE = "openingDate";
        String COLUMN_CLOSED_DATE = "closedDate";
        String COLUMN_LEVEL = "level";
        String COLUMN_PATH = "path";
    }

    String CREATE_TABLE_ORGANISATION_UNITS = "CREATE TABLE IF NOT EXISTS " +
            OrganisationUnitColumns.TABLE_NAME + " (" +
            OrganisationUnitColumns.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            OrganisationUnitColumns.COLUMN_UID + " TEXT NOT NULL," +
            OrganisationUnitColumns.COLUMN_CODE + " TEXT," +
            OrganisationUnitColumns.COLUMN_CREATED + " TEXT NOT NULL," +
            OrganisationUnitColumns.COLUMN_LAST_UPDATED + " TEXT NOT NULL," +
            OrganisationUnitColumns.COLUMN_NAME + " TEXT," +
            OrganisationUnitColumns.COLUMN_DISPLAY_NAME + " TEXT," +
            OrganisationUnitColumns.COLUMN_SHORT_NAME + "TEXT," +
            OrganisationUnitColumns.COLUMN_DISPLAY_SHORT_NAME + "TEXT," +
            OrganisationUnitColumns.COLUMN_DESCRIPTION + "TEXT," +
            OrganisationUnitColumns.COLUMN_DISPLAY_DESCRIPTION + "TEXT," +
            OrganisationUnitColumns.COLUMN_PARENT + "TEXT," +
            OrganisationUnitColumns.COLUMN_PATH + "TEXT NOT NULL," +
            OrganisationUnitColumns.COLUMN_OPENING_DATE + "TEXT NOT NULL," +
            OrganisationUnitColumns.COLUMN_CLOSED_DATE + "TEXT," +
            OrganisationUnitColumns.COLUMN_LEVEL + "INTEGER," +
            OrganisationUnitColumns.COLUMN_BODY + "TEXT NOT NULL" +
            " UNIQUE " + "(" + OrganisationUnitColumns.COLUMN_UID + ")" + " ON CONFLICT REPLACE" + " )";

    String DROP_TABLE_ORGANISATION_UNITS = "DROP TABLE IF EXISTS " +
            OrganisationUnitColumns.TABLE_NAME;

    Uri CONTENT_URI = DbContract.BASE_CONTENT_URI.buildUpon()
            .appendPath(OrganisationUnitColumns.TABLE_NAME).build();

    String ORGANISATION_UNITS = OrganisationUnitColumns.TABLE_NAME;
    String ORGANISATION_UNIT_ID = OrganisationUnitColumns.TABLE_NAME + "/#";

    String CONTENT_TYPE = DbUtils.getContentType(OrganisationUnit.class);
    String CONTENT_ITEM_TYPE = DbUtils.getContentItemType(OrganisationUnit.class);

    String[] PROJECTION = new String[]{
            OrganisationUnitColumns.COLUMN_ID,
            OrganisationUnitColumns.COLUMN_UID,
            OrganisationUnitColumns.COLUMN_CODE,
            OrganisationUnitColumns.COLUMN_CREATED,
            OrganisationUnitColumns.COLUMN_LAST_UPDATED,
            OrganisationUnitColumns.COLUMN_NAME,
            OrganisationUnitColumns.COLUMN_DISPLAY_NAME,
            OrganisationUnitColumns.COLUMN_SHORT_NAME,
            OrganisationUnitColumns.COLUMN_DISPLAY_SHORT_NAME,
            OrganisationUnitColumns.COLUMN_DESCRIPTION,
            OrganisationUnitColumns.COLUMN_DISPLAY_DESCRIPTION,
            OrganisationUnitColumns.COLUMN_PARENT,
            OrganisationUnitColumns.COLUMN_PATH,
            OrganisationUnitColumns.COLUMN_OPENING_DATE,
            OrganisationUnitColumns.COLUMN_CLOSED_DATE,
            OrganisationUnitColumns.COLUMN_LEVEL
    };
}
