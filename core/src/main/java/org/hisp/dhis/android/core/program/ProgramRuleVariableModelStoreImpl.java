package org.hisp.dhis.android.core.program;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;
import org.hisp.dhis.android.core.program.ProgramRuleVariableContract.Columns;

import java.util.Date;

public class ProgramRuleVariableModelStoreImpl implements ProgramRuleVariableModelStore {

    private static final String INSERT_STATEMENT = "INSERT INTO " + Tables.PROGRAM_RULE_VARIABLE + " (" +
            Columns.UID + ", " +
            Columns.CODE + ", " +
            Columns.NAME + ", " +
            Columns.DISPLAY_NAME + ", " +
            Columns.CREATED + ", " +
            Columns.LAST_UPDATED + ", " +
            Columns.USE_CODE_FOR_OPTION_SET + ", " +
            Columns.PROGRAM + ", " +
            Columns.PROGRAM_STAGE + ", " +
            Columns.DATA_ELEMENT + ", " +
            Columns.TRACKED_ENTITY_ATTRIBUTE + ", " +
            Columns.PROGRAM_RULE_VARIABLE_SOURCE_TYPE + ") " +
            "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private final SQLiteStatement sqLiteStatement;

    public ProgramRuleVariableModelStoreImpl(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteStatement = sqLiteDatabase.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(@NonNull String uid, @Nullable String code, @NonNull String name,
                       @NonNull String displayName, @NonNull Date created,
                       @NonNull Date lastUpdated, @Nullable Boolean useCodeForOptionSet,
                       @NonNull String program, @Nullable String programStage,
                       @Nullable String dataElement, @Nullable String trackedEntityAttribute,
                       @Nullable ProgramRuleVariableSourceType programRuleVariableSourceType) {
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

        if (useCodeForOptionSet == null) {
            sqLiteStatement.bindNull(7);
        } else {
            sqLiteStatement.bindLong(7, useCodeForOptionSet ? 1 : 0);
        }

        sqLiteStatement.bindString(8, program);

        if (programStage == null) {
            sqLiteStatement.bindNull(9);
        } else {
            sqLiteStatement.bindString(9, programStage);
        }

        if (dataElement == null) {
            sqLiteStatement.bindNull(10);
        } else {
            sqLiteStatement.bindString(10, dataElement);
        }

        if (trackedEntityAttribute == null) {
            sqLiteStatement.bindNull(11);
        } else {
            sqLiteStatement.bindString(11, trackedEntityAttribute);
        }

        if (programRuleVariableSourceType == null) {
            sqLiteStatement.bindNull(12);
        } else {
            sqLiteStatement.bindString(12, programRuleVariableSourceType.name());
        }

        return sqLiteStatement.executeInsert();
    }

    @Override
    public void close() {
        sqLiteStatement.close();
    }
}
