package org.hisp.dhis.client.sdk.core.organisationunit;

import android.content.ContentResolver;
import android.database.Cursor;

import org.hisp.dhis.client.sdk.core.commons.AbsIdentifiableObjectStore;
import org.hisp.dhis.client.sdk.core.commons.Mapper;
import org.hisp.dhis.client.sdk.core.organisationunit.OrganisationUnitMapper.OrganisationUnitColumns;
import org.hisp.dhis.client.sdk.models.organisationunit.OrganisationUnit;

import java.util.List;

public class OrganisationUnitStoreImpl extends AbsIdentifiableObjectStore<OrganisationUnit> implements OrganisationUnitStore {

    public static final String CREATE_TABLE_ORGANISATION_UNITS = "CREATE TABLE IF NOT EXISTS " +
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

    private static final String DROP_TABLE_ORGANISATION_UNITS = "DROP TABLE IF EXISTS " +
            OrganisationUnitColumns.TABLE_NAME;

    public OrganisationUnitStoreImpl(ContentResolver contentResolver, Mapper<OrganisationUnit> mapper) {
        super(contentResolver, mapper);
    }

    @Override
    public List<OrganisationUnit> query(String parentOrganisationUnitId) {
        if (parentOrganisationUnitId == null) {
            throw new IllegalArgumentException("parent orgUnit uid must not be null");
        }

        final String[] selectionArgs = new String[]{parentOrganisationUnitId};
        final String selection = OrganisationUnitColumns.COLUMN_PARENT + " = ?";

        Cursor cursor = contentResolver.query(mapper.getContentUri(),
                mapper.getProjection(), selection, selectionArgs, null);
        return toModels(cursor);
    }
}
