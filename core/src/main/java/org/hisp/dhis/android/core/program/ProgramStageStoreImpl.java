package org.hisp.dhis.android.core.program;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.hisp.dhis.android.core.common.FormType;
import org.hisp.dhis.android.core.data.database.DbOpenHelper.Tables;

import java.util.Date;

import static org.hisp.dhis.android.core.common.StoreUtils.sqLiteBind;

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
    public long insert(@NonNull String uid,
            @Nullable String code,
            @NonNull String name,
            @NonNull String displayName,
            @NonNull Date created,
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

        sqLiteBind(sqLiteStatement, 1, uid);
        sqLiteBind(sqLiteStatement, 2, code);
        sqLiteBind(sqLiteStatement, 3, name);
        sqLiteBind(sqLiteStatement, 4, displayName);
        sqLiteBind(sqLiteStatement, 5, created);
        sqLiteBind(sqLiteStatement, 6, lastUpdated);
        sqLiteBind(sqLiteStatement, 7, executionDateLabel);
        sqLiteBind(sqLiteStatement, 8, allowGenerateNextVisit);
        sqLiteBind(sqLiteStatement, 9, validCompleteOnly);
        sqLiteBind(sqLiteStatement, 10, reportDateToUse);
        sqLiteBind(sqLiteStatement, 11, openAfterEnrollment);
        sqLiteBind(sqLiteStatement, 12, repeatable);
        sqLiteBind(sqLiteStatement, 13, captureCoordinates);
        sqLiteBind(sqLiteStatement, 14, formType.name());
        sqLiteBind(sqLiteStatement, 15, displayGenerateEventBox);
        sqLiteBind(sqLiteStatement, 16, generatedByEnrollmentDate);
        sqLiteBind(sqLiteStatement, 17, autoGenerateEvent);
        sqLiteBind(sqLiteStatement, 18, sortOrder);
        sqLiteBind(sqLiteStatement, 19, hideDueDate);
        sqLiteBind(sqLiteStatement, 20, blockEntryForm);
        sqLiteBind(sqLiteStatement, 21, minDaysFromStart);
        sqLiteBind(sqLiteStatement, 22, standardInterval);
        sqLiteBind(sqLiteStatement, 23, program);

        return sqLiteStatement.executeInsert();
    }

    @Override
    public void close() {
        sqLiteStatement.close();
    }
}
