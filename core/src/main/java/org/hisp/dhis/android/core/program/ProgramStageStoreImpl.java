package org.hisp.dhis.android.core.program;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.FormType;
import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;

import java.util.Date;

public class ProgramStageStoreImpl implements ProgramStageStore {
    private static final String INSERT_STATEMENT = "INSERT INTO " + Tables.PROGRAM_STAGE + " (" +
            ProgramStageModel.Columns.UID + ", " +
            ProgramStageModel.Columns.CODE + ", " +
            ProgramStageModel.Columns.NAME + ", " +
            ProgramStageModel.Columns.DISPLAY_NAME + ", " +
            ProgramStageModel.Columns.CREATED + ", " +
            ProgramStageModel.Columns.LAST_UPDATED + ", " +
            ProgramStageModel.Columns.EXECUTION_DATE_LABEL + ", " +
            ProgramStageModel.Columns.ALLOW_GENERATE_NEXT_VISIT + ", " +
            ProgramStageModel.Columns.VALID_COMPLETE_ONLY + ", " +
            ProgramStageModel.Columns.REPORT_DATE_TO_USE + ", " +
            ProgramStageModel.Columns.OPEN_AFTER_ENROLLMENT + ", " +
            ProgramStageModel.Columns.REPEATABLE + ", " +
            ProgramStageModel.Columns.CAPTURE_COORDINATES + ", " +
            ProgramStageModel.Columns.FORM_TYPE + ", " +
            ProgramStageModel.Columns.DISPLAY_GENERATE_EVENT_BOX + ", " +
            ProgramStageModel.Columns.GENERATED_BY_ENROLMENT_DATE + ", " +
            ProgramStageModel.Columns.AUTO_GENERATE_EVENT + ", " +
            ProgramStageModel.Columns.SORT_ORDER + ", " +
            ProgramStageModel.Columns.HIDE_DUE_DATE + ", " +
            ProgramStageModel.Columns.BLOCK_ENTRY_FORM + ", " +
            ProgramStageModel.Columns.MIN_DAYS_FROM_START + ", " +
            ProgramStageModel.Columns.STANDARD_INTERVAL + ", " +
            ProgramStageModel.Columns.PROGRAM + ") " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    private final SQLiteStatement sqLiteStatement;

    public ProgramStageStoreImpl(SQLiteDatabase sqLiteDatabase) {
        this.sqLiteStatement = sqLiteDatabase.compileStatement(INSERT_STATEMENT);
    }

    @Override
    public long insert(@NonNull String uid, @Nullable String code, @NonNull String name,
                       @NonNull String displayName, @NonNull Date created,
                       @NonNull Date lastUpdated,
                       @Nullable String executionDateLabel,
                       @NonNull Boolean allowGenerateNextVisit,
                       @NonNull Boolean validCompleteOnly,
                       @Nullable String reportDateToUse,
                       @NonNull Boolean openAfterEnrollment,
                       @NonNull Boolean repeatable,
                       @NonNull Boolean captureCoordinates,
                       @NonNull FormType formType,
                       @NonNull Boolean displayGenerateEventBox,
                       @NonNull Boolean generatedByEnrollmentDate,
                       @NonNull Boolean autoGenerateEvent,
                       @NonNull Integer sortOrder,
                       @NonNull Boolean hideDueDate,
                       @NonNull Boolean blockEntryForm,
                       @NonNull Integer minDaysFromStart,
                       @NonNull Integer standardInterval,
                       @NonNull String program) {

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

        if (executionDateLabel == null) {
            sqLiteStatement.bindNull(7);
        } else {
            sqLiteStatement.bindString(7, executionDateLabel);
        }

        sqLiteStatement.bindLong(8, allowGenerateNextVisit ? 1 : 0);
        sqLiteStatement.bindLong(9, validCompleteOnly ? 1 : 0);

        if (reportDateToUse == null) {
            sqLiteStatement.bindNull(10);
        } else {
            sqLiteStatement.bindString(10, reportDateToUse);
        }

        sqLiteStatement.bindLong(11, openAfterEnrollment ? 1 : 0);
        sqLiteStatement.bindLong(12, repeatable ? 1 : 0);
        sqLiteStatement.bindLong(13, captureCoordinates ? 1 : 0);
        sqLiteStatement.bindString(14, formType.name());
        sqLiteStatement.bindLong(15, displayGenerateEventBox ? 1 : 0);
        sqLiteStatement.bindLong(16, generatedByEnrollmentDate ? 1 : 0);
        sqLiteStatement.bindLong(17, autoGenerateEvent ? 1 : 0);
        sqLiteStatement.bindLong(18, sortOrder);
        sqLiteStatement.bindLong(19, hideDueDate ? 1 : 0);
        sqLiteStatement.bindLong(20, blockEntryForm ? 1 : 0);
        sqLiteStatement.bindLong(21, minDaysFromStart);
        sqLiteStatement.bindLong(22, standardInterval);
        sqLiteStatement.bindString(23, program);

        return sqLiteStatement.executeInsert();
    }

    @Override
    public void close() {
        sqLiteStatement.close();
    }
}
