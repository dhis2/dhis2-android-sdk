package org.hisp.dhis.android.core.program;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;
import org.hisp.dhis.android.core.program.ProgramStageDataElementContract.Columns;

import java.util.Date;

public class ProgramStageDataElementStoreImpl implements ProgramStageDataElementStore {

    private static final String INSERT_STATEMENT = "INSERT INTO " + Tables.PROGRAM_STAGE_DATA_ELEMENT + " (" +
            Columns.UID + ", " +
            Columns.CODE + ", " +
            Columns.NAME + ", " +
            Columns.DISPLAY_NAME + ", " +
            Columns.CREATED + ", " +
            Columns.LAST_UPDATED + ", " +
            Columns.DISPLAY_IN_REPORTS + ", " +
            Columns.COMPULSORY + ", " +
            Columns.ALLOW_PROVIDED_ELSEWHERE + ", " +
            Columns.SORT_ORDER + ", " +
            Columns.ALLOW_FUTURE_DATE + ", " +
            Columns.DATA_ELEMENT + ", " +
            Columns.PROGRAM_STAGE_SECTION + ") " +
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

        sqLiteStatement.bindString(1, uid);

        if (code == null) {
            sqLiteStatement.bindNull(2);
        } else {
            sqLiteStatement.bindString(2, code);
        }

        if (name == null) {
            sqLiteStatement.bindNull(3);
        } else {
            sqLiteStatement.bindString(3, name);
        }

        if (displayName == null) {
            sqLiteStatement.bindNull(4);
        } else {
            sqLiteStatement.bindString(4, displayName);
        }

        sqLiteStatement.bindString(5, BaseIdentifiableObject.DATE_FORMAT.format(created));
        sqLiteStatement.bindString(6, BaseIdentifiableObject.DATE_FORMAT.format(lastUpdated));

        sqLiteStatement.bindLong(7, displayInReports ? 1 : 0);
        sqLiteStatement.bindLong(8, compulsory ? 1 : 0);
        sqLiteStatement.bindLong(9, allowProvidedElsewhere ? 1 : 0);

        if (sortOrder == null) {
            sqLiteStatement.bindNull(10);
        } else {
            sqLiteStatement.bindLong(10, sortOrder);
        }

        sqLiteStatement.bindLong(11, allowFutureDate ? 1 : 0);
        sqLiteStatement.bindString(12, dataElement);

        if (programStageSection == null) {
            sqLiteStatement.bindNull(13);
        } else {
            sqLiteStatement.bindString(13, programStageSection);
        }


        return sqLiteStatement.executeInsert();
    }

    @Override
    public void close() {
        sqLiteStatement.close();
    }
}
