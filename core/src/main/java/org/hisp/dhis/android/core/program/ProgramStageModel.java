package org.hisp.dhis.android.core.program;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.common.FormType;
import org.hisp.dhis.android.core.data.database.DbFormTypeColumnAdapter;
import org.hisp.dhis.android.core.program.ProgramStageContract.Columns;

@AutoValue
public abstract class ProgramStageModel extends BaseIdentifiableObjectModel {

    public static ProgramStageModel create(Cursor cursor) {
        return AutoValue_ProgramStageModel.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $$AutoValue_ProgramStageModel.Builder();
    }

    @NonNull
    public abstract ContentValues toContentValues();

    @Nullable
    @ColumnName(Columns.EXECUTION_DATE_LABEL)
    public abstract String executionDateLabel();

    @Nullable
    @ColumnName(Columns.ALLOW_GENERATE_NEXT_VISIT)
    public abstract Boolean allowGenerateNextVisit();

    @Nullable
    @ColumnName(Columns.VALID_COMPLETE_ONLY)
    public abstract Boolean validCompleteOnly();

    @Nullable
    @ColumnName(Columns.REPORT_DATE_TO_USE)
    public abstract String reportDateToUse();

    @Nullable
    @ColumnName(Columns.OPEN_AFTER_ENROLLMENT)
    public abstract Boolean openAfterEnrollment();

    @Nullable
    @ColumnName(Columns.REPEATABLE)
    public abstract Boolean repeatable();

    @Nullable
    @ColumnName(Columns.CAPTURE_COORDINATES)
    public abstract Boolean captureCoordinates();

    @Nullable
    @ColumnName(Columns.FORM_TYPE)
    @ColumnAdapter(DbFormTypeColumnAdapter.class)
    public abstract FormType formType();

    @Nullable
    @ColumnName(Columns.DISPLAY_GENERATE_EVENT_BOX)
    public abstract Boolean displayGenerateEventBox();

    @Nullable
    @ColumnName(Columns.GENERATED_BY_ENROLMENT_DATE)
    public abstract Boolean generatedByEnrollmentDate();

    @Nullable
    @ColumnName(Columns.AUTO_GENERATE_EVENT)
    public abstract Boolean autoGenerateEvent();

    @Nullable
    @ColumnName(Columns.SORT_ORDER)
    public abstract Integer sortOrder();

    @Nullable
    @ColumnName(Columns.HIDE_DUE_DATE)
    public abstract Boolean hideDueDate();

    @Nullable
    @ColumnName(Columns.BLOCK_ENTRY_FORM)
    public abstract Boolean blockEntryForm();

    @Nullable
    @ColumnName(Columns.MIN_DAYS_FROM_START)
    public abstract Integer minDaysFromStart();

    @Nullable
    @ColumnName(Columns.STANDARD_INTERVAL)
    public abstract Integer standardInterval();

    @Nullable
    @ColumnName(Columns.PROGRAM)
    public abstract String program();

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableObjectModel.Builder<Builder> {

        public abstract Builder executionDateLabel(@Nullable String executionDateLabel);

        public abstract Builder allowGenerateNextVisit(@Nullable Boolean allowGenerateNextVisit);

        public abstract Builder validCompleteOnly(@Nullable Boolean validCompleteOnly);

        public abstract Builder reportDateToUse(@Nullable String reportDateToUse);

        public abstract Builder openAfterEnrollment(@Nullable Boolean openAfterEnrollment);

        public abstract Builder repeatable(@Nullable Boolean repeatable);

        public abstract Builder captureCoordinates(@Nullable Boolean captureCoordinates);

        public abstract Builder formType(@Nullable FormType formType);

        public abstract Builder displayGenerateEventBox(@Nullable Boolean displayGenerateEventBox);

        public abstract Builder generatedByEnrollmentDate(@Nullable Boolean generatedByEnrollmentDate);

        public abstract Builder autoGenerateEvent(@Nullable Boolean autoGenerateEvent);

        public abstract Builder sortOrder(@Nullable Integer sortOrder);

        public abstract Builder hideDueDate(@Nullable Boolean hideDueDate);

        public abstract Builder blockEntryForm(@Nullable Boolean blockEntryForm);

        public abstract Builder minDaysFromStart(@Nullable Integer minDaysFromStart);

        public abstract Builder standardInterval(@Nullable Integer standardInterval);

        public abstract Builder program(@Nullable String program);

        public abstract ProgramStageModel build();

    }
}
