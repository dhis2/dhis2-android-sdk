package org.hisp.dhis.client.sdk.core.program;

import android.net.Uri;

import org.hisp.dhis.client.sdk.core.commons.database.DbContract;
import org.hisp.dhis.client.sdk.core.commons.database.DbUtils;
import org.hisp.dhis.client.sdk.models.program.Program;

public interface ProgramTable {
    interface ProgramColumns extends DbContract.NameableColumns, DbContract.TimeStampColumns, DbContract.VersionColumn, DbContract.BodyColumn {
        String TABLE_NAME = "programs";
        String COLUMN_PROGRAM_TYPE = "programType";
        String COLUMN_DISPLAY_FRONT_PAGE_LIST = "displayFrontPageList";
    }

    Uri CONTENT_URI = DbContract.BASE_CONTENT_URI.buildUpon()
            .appendPath(ProgramColumns.TABLE_NAME).build();

    String[] PROJECTION = new String[]{
            ProgramColumns.COLUMN_ID,
            ProgramColumns.COLUMN_UID,
            ProgramColumns.COLUMN_CODE,
            ProgramColumns.COLUMN_CREATED,
            ProgramColumns.COLUMN_LAST_UPDATED,
            ProgramColumns.COLUMN_NAME,
            ProgramColumns.COLUMN_DISPLAY_NAME,
            ProgramColumns.COLUMN_SHORT_NAME,
            ProgramColumns.COLUMN_DISPLAY_SHORT_NAME,
            ProgramColumns.COLUMN_DESCRIPTION,
            ProgramColumns.COLUMN_DISPLAY_DESCRIPTION,
            ProgramColumns.COLUMN_DISPLAY_FRONT_PAGE_LIST,
            ProgramColumns.COLUMN_PROGRAM_TYPE,
            ProgramColumns.COLUMN_BODY
    };

    String PROGRAMS = ProgramColumns.TABLE_NAME;
    String PROGRAM_ID = ProgramColumns.TABLE_NAME + "/#";

    String CONTENT_TYPE = DbUtils.getContentType(Program.class);
    String CONTENT_ITEM_TYPE = DbUtils.getContentItemType(Program.class);

    String CREATE_TABLE_PROGRAMS = "CREATE TABLE IF NOT EXISTS " +
            ProgramColumns.TABLE_NAME + "(" +
            ProgramColumns.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            ProgramColumns.COLUMN_UID + " TEXT UNIQUE NOT NULL ON CONFLICT REPLACE," +
            ProgramColumns.COLUMN_CODE + " TEXT," +
            ProgramColumns.COLUMN_CREATED + " TEXT NOT NULL," +
            ProgramColumns.COLUMN_LAST_UPDATED + " TEXT NOT NULL," +
            ProgramColumns.COLUMN_NAME + " TEXT," +
            ProgramColumns.COLUMN_DISPLAY_NAME + " TEXT," +
            ProgramColumns.COLUMN_SHORT_NAME + " TEXT," +
            ProgramColumns.COLUMN_DISPLAY_SHORT_NAME + " TEXT," +
            ProgramColumns.COLUMN_DESCRIPTION + " TEXT," +
            ProgramColumns.COLUMN_DISPLAY_DESCRIPTION + " TEXT," +
            ProgramColumns.COLUMN_PROGRAM_TYPE + " TEXT NOT NULL," +
            ProgramColumns.COLUMN_DISPLAY_FRONT_PAGE_LIST + " INTEGER NOT NULL," +
            ProgramColumns.COLUMN_BODY + " TEXT NOT NULL )";

    String DROP_TABLE_PROGRAMS = "DROP TABLE IF EXISTS " +
            ProgramColumns.TABLE_NAME;
}
