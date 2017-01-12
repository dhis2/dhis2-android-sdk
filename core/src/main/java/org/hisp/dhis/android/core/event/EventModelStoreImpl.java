package org.hisp.dhis.android.core.event;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;
import org.hisp.dhis.android.core.event.EventModel.Columns;

import java.util.Date;

import static org.hisp.dhis.android.core.common.StoreUtils.sqLiteBind;

public class EventModelStoreImpl implements EventModelStore {

    private static final String INSERT_STATEMENT = "INSERT INTO " + Tables.EVENT + " (" +
            Columns.EVENT_UID + ", " +
            Columns.ENROLLMENT_UID + ", " +
            Columns.CREATED + ", " +
            Columns.LAST_UPDATED + ", " +
            Columns.STATUS + ", " +
            Columns.LATITUDE + ", " +
            Columns.LONGITUDE + ", " +
            Columns.PROGRAM + ", " +
            Columns.PROGRAM_STAGE + ", " +
            Columns.ORGANISATION_UNIT + ", " +
            Columns.EVENT_DATE + ", " +
            Columns.COMPLETE_DATE + ", " +
            Columns.DUE_DATE + ", " +
            Columns.STATE + ") " +
            "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final SQLiteStatement sqLiteStatement;

    public EventModelStoreImpl(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteStatement = sqLiteDatabase.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(@NonNull String uid, @Nullable String enrollmentUid,
                       @Nullable Date created, @Nullable Date lastUpdated,
                       @Nullable String status, @Nullable Double latitude,
                       @Nullable Double longitude, @Nullable String program,
                       @Nullable String programStage, @Nullable String organisationUnit,
                       @Nullable Date eventDate, @Nullable Date completedDate,
                       @Nullable Date dueDate, @Nullable State state) {
        sqLiteStatement.clearBindings();

        sqLiteBind(sqLiteStatement, 1, uid);
        sqLiteBind(sqLiteStatement, 2, enrollmentUid);
        sqLiteBind(sqLiteStatement, 3, created);
        sqLiteBind(sqLiteStatement, 4, lastUpdated);
        sqLiteBind(sqLiteStatement, 5, status);
        sqLiteBind(sqLiteStatement, 6, latitude);
        sqLiteBind(sqLiteStatement, 7, longitude);
        sqLiteBind(sqLiteStatement, 8, program);
        sqLiteBind(sqLiteStatement, 9, programStage);
        sqLiteBind(sqLiteStatement, 10, organisationUnit);
        sqLiteBind(sqLiteStatement, 11, eventDate);
        sqLiteBind(sqLiteStatement, 12, completedDate);
        sqLiteBind(sqLiteStatement, 13, dueDate);
        sqLiteBind(sqLiteStatement, 14, state);

        return sqLiteStatement.executeInsert();
    }

    @Override
    public void close() {
        sqLiteStatement.close();
    }
}
