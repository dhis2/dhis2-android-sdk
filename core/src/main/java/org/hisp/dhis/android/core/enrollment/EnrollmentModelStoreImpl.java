package org.hisp.dhis.android.core.enrollment;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;
import org.hisp.dhis.android.core.enrollment.EnrollmentModel.Columns;

import java.util.Date;

import static org.hisp.dhis.android.core.common.StoreUtils.sqLiteBind;

public class EnrollmentModelStoreImpl implements EnrollmentModelStore {

    private static final String INSERT_STATEMENT = "INSERT INTO " + Tables.ENROLLMENT + " (" +
            Columns.UID + ", " +
            Columns.CREATED + ", " +
            Columns.LAST_UPDATED + ", " +
            Columns.ORGANISATION_UNIT + ", " +
            Columns.PROGRAM + ", " +
            Columns.DATE_OF_ENROLLMENT + ", " +
            Columns.DATE_OF_INCIDENT + ", " +
            Columns.FOLLOW_UP + ", " +
            Columns.ENROLLMENT_STATUS + ", " +
            Columns.TRACKED_ENTITY_INSTANCE + ", " +
            Columns.LATITUDE + ", " +
            Columns.LONGITUDE + ", " +
            Columns.STATE + ") " +
            "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?);";

    private final SQLiteStatement sqLiteStatement;

    public EnrollmentModelStoreImpl(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteStatement = sqLiteDatabase.compileStatement(INSERT_STATEMENT);
    }


    @Override
    public long insert(@NonNull String uid, @Nullable Date created, @Nullable Date lastUpdated,
                       @Nullable String organisationUnit, @Nullable String program, @Nullable Date dateOfEnrollment,
                       @Nullable Date dateOfIncident, @Nullable Boolean followUp,
                       @Nullable EnrollmentStatus enrollmentStatus, @Nullable String trackedEntityInstance,
                       @Nullable String latitude, @Nullable String longitude, @Nullable State state) {
        sqLiteStatement.clearBindings();

        sqLiteBind(sqLiteStatement, 1, uid);
        sqLiteBind(sqLiteStatement, 2, created);
        sqLiteBind(sqLiteStatement, 3, lastUpdated);
        sqLiteBind(sqLiteStatement, 4, organisationUnit);
        sqLiteBind(sqLiteStatement, 5, program);
        sqLiteBind(sqLiteStatement, 6, dateOfEnrollment);
        sqLiteBind(sqLiteStatement, 7, dateOfIncident);
        sqLiteBind(sqLiteStatement, 8, followUp);
        sqLiteBind(sqLiteStatement, 9, enrollmentStatus);
        sqLiteBind(sqLiteStatement, 10, trackedEntityInstance);
        sqLiteBind(sqLiteStatement, 11, latitude);
        sqLiteBind(sqLiteStatement, 12, longitude);
        sqLiteBind(sqLiteStatement, 13, state);

        return sqLiteStatement.executeInsert();
    }

    @Override
    public void close() {
        sqLiteStatement.close();
    }
}
