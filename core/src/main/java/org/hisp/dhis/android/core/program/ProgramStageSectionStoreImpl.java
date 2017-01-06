package org.hisp.dhis.android.core.program;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;
import org.hisp.dhis.android.core.program.ProgramStageSectionContract.Columns;

import java.util.Date;

public class ProgramStageSectionStoreImpl implements ProgramStageSectionStore {

    private static final String INSERT_STATEMENT = "INSERT INTO " + Tables.PROGRAM_STAGE_SECTION + " (" +
            Columns.UID + ", " +
            Columns.CODE + ", " +
            Columns.NAME + ", " +
            Columns.DISPLAY_NAME + ", " +
            Columns.CREATED + ", " +
            Columns.LAST_UPDATED + ", " +
            Columns.SORT_ORDER + ", " +
            Columns.PROGRAM_STAGE + ") " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

    private final SQLiteStatement sqLiteStatement;

    public ProgramStageSectionStoreImpl(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteStatement = sqLiteDatabase.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(@NonNull String uid, @Nullable String code,
                       @NonNull String name, @NonNull String displayName,
                       @NonNull Date created, @NonNull Date lastUpdated,
                       @Nullable Integer sortOrder, @Nullable String programStage) {
        sqLiteStatement.clearBindings();

        sqLiteStatement.bindString(1, uid);

        if (code == null) {
            sqLiteStatement.bindNull(2);
        } else {
            sqLiteStatement.bindString(2, code);
        }

        sqLiteStatement.bindString(3, name);
        sqLiteStatement.bindString(4, displayName);
        sqLiteStatement.bindString(5, BaseIdentifiableObject.DATE_FORMAT.format(created));
        sqLiteStatement.bindString(6, BaseIdentifiableObject.DATE_FORMAT.format(lastUpdated));


        if (sortOrder == null) {
            sqLiteStatement.bindNull(7);
        } else {
            sqLiteStatement.bindLong(7, sortOrder);
        }

        if (programStage == null) {
            sqLiteStatement.bindNull(8);
        } else {
            sqLiteStatement.bindString(8, programStage);
        }

        return sqLiteStatement.executeInsert();
    }

    @Override
    public void close() {
        sqLiteStatement.close();
    }
}
