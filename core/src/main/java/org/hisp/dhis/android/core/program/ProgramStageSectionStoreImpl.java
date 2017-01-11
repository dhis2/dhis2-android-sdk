package org.hisp.dhis.android.core.program;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;

import java.util.Date;

import static org.hisp.dhis.android.core.common.StoreUtils.sqLiteBind;

public class ProgramStageSectionStoreImpl implements ProgramStageSectionStore {

    private static final String INSERT_STATEMENT = "INSERT INTO " + Tables.PROGRAM_STAGE_SECTION + " (" +
            ProgramStageSectionModel.Columns.UID + ", " +
            ProgramStageSectionModel.Columns.CODE + ", " +
            ProgramStageSectionModel.Columns.NAME + ", " +
            ProgramStageSectionModel.Columns.DISPLAY_NAME + ", " +
            ProgramStageSectionModel.Columns.CREATED + ", " +
            ProgramStageSectionModel.Columns.LAST_UPDATED + ", " +
            ProgramStageSectionModel.Columns.SORT_ORDER + ", " +
            ProgramStageSectionModel.Columns.PROGRAM_STAGE + ") " +
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

        sqLiteBind(sqLiteStatement, 1, uid);
        sqLiteBind(sqLiteStatement, 2, code);
        sqLiteBind(sqLiteStatement, 3, name);
        sqLiteBind(sqLiteStatement, 4, displayName);
        sqLiteBind(sqLiteStatement, 5, created);
        sqLiteBind(sqLiteStatement, 6, lastUpdated);
        sqLiteBind(sqLiteStatement, 7, sortOrder);
        sqLiteBind(sqLiteStatement, 8, programStage);

        return sqLiteStatement.executeInsert();
    }

    @Override
    public void close() {
        sqLiteStatement.close();
    }
}
