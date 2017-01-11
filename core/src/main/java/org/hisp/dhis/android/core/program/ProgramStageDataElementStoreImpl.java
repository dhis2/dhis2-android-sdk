package org.hisp.dhis.android.core.program;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;

import java.util.Date;

import static org.hisp.dhis.android.core.common.StoreUtils.sqLiteBind;

public class ProgramStageDataElementStoreImpl implements ProgramStageDataElementStore {

    private static final String INSERT_STATEMENT = "INSERT INTO " + Tables.PROGRAM_STAGE_DATA_ELEMENT + " (" +
            ProgramStageDataElementModel.Columns.UID + ", " +
            ProgramStageDataElementModel.Columns.CODE + ", " +
            ProgramStageDataElementModel.Columns.NAME + ", " +
            ProgramStageDataElementModel.Columns.DISPLAY_NAME + ", " +
            ProgramStageDataElementModel.Columns.CREATED + ", " +
            ProgramStageDataElementModel.Columns.LAST_UPDATED + ", " +
            ProgramStageDataElementModel.Columns.DISPLAY_IN_REPORTS + ", " +
            ProgramStageDataElementModel.Columns.COMPULSORY + ", " +
            ProgramStageDataElementModel.Columns.ALLOW_PROVIDED_ELSEWHERE + ", " +
            ProgramStageDataElementModel.Columns.SORT_ORDER + ", " +
            ProgramStageDataElementModel.Columns.ALLOW_FUTURE_DATE + ", " +
            ProgramStageDataElementModel.Columns.DATA_ELEMENT + ", " +
            ProgramStageDataElementModel.Columns.PROGRAM_STAGE_SECTION + ") " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private final SQLiteStatement sqLiteStatement;

    public ProgramStageDataElementStoreImpl(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteStatement = sqLiteDatabase.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(@NonNull String uid, @Nullable String code, @Nullable String name,
                       @Nullable String displayName, @NonNull Date created,
                       @NonNull Date lastUpdated, @NonNull Boolean displayInReports,
                       @NonNull Boolean compulsory, @NonNull Boolean allowProvidedElsewhere,
                       @Nullable Integer sortOrder, @NonNull Boolean allowFutureDate,
                       @NonNull String dataElement, @Nullable String programStageSection) {
        sqLiteStatement.clearBindings();

        sqLiteBind(sqLiteStatement, 1, uid);
        sqLiteBind(sqLiteStatement, 2, code);
        sqLiteBind(sqLiteStatement, 3, name);
        sqLiteBind(sqLiteStatement, 4, displayName);
        sqLiteBind(sqLiteStatement, 5, created);
        sqLiteBind(sqLiteStatement, 6, lastUpdated);
        sqLiteBind(sqLiteStatement, 7, displayInReports);
        sqLiteBind(sqLiteStatement, 8, compulsory);
        sqLiteBind(sqLiteStatement, 9, allowProvidedElsewhere);
        sqLiteBind(sqLiteStatement, 10, sortOrder);
        sqLiteBind(sqLiteStatement, 11, allowFutureDate);
        sqLiteBind(sqLiteStatement, 12, dataElement);
        sqLiteBind(sqLiteStatement, 13, programStageSection);

        return sqLiteStatement.executeInsert();
    }

    @Override
    public void close() {
        sqLiteStatement.close();
    }
}
