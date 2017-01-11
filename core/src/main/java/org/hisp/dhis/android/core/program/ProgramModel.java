package org.hisp.dhis.android.core.program;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseNameableObjectModel;
import org.hisp.dhis.android.core.data.database.DbProgramTypeColumnAdapter;

@AutoValue
public abstract class ProgramModel extends BaseNameableObjectModel {

    public static ProgramModel create(Cursor cursor) {
        return AutoValue_ProgramModel.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $$AutoValue_ProgramModel.Builder();
    }

    @NonNull
    public abstract ContentValues toContentValues();

    @Nullable
    @ColumnName(ProgramContract.Columns.VERSION)
    public abstract Integer version();

    @Nullable
    @ColumnName(ProgramContract.Columns.ONLY_ENROLL_ONCE)
    public abstract Boolean onlyEnrollOnce();

    @Nullable
    @ColumnName(ProgramContract.Columns.ENROLLMENT_DATE_LABEL)
    public abstract String enrollmentDateLabel();

    @Nullable
    @ColumnName(ProgramContract.Columns.DISPLAY_INCIDENT_DATE)
    public abstract Boolean displayIncidentDate();

    @Nullable
    @ColumnName(ProgramContract.Columns.INCIDENT_DATE_LABEL)
    public abstract String incidentDateLabel();

    @Nullable
    @ColumnName(ProgramContract.Columns.REGISTRATION)
    public abstract Boolean registration();

    @Nullable
    @ColumnName(ProgramContract.Columns.SELECT_ENROLLMENT_DATES_IN_FUTURE)
    public abstract Boolean selectEnrollmentDatesInFuture();

    @Nullable
    @ColumnName(ProgramContract.Columns.DATA_ENTRY_METHOD)
    public abstract Boolean dataEntryMethod();

    @Nullable
    @ColumnName(ProgramContract.Columns.IGNORE_OVERDUE_EVENTS)
    public abstract Boolean ignoreOverdueEvents();

    @Nullable
    @ColumnName(ProgramContract.Columns.RELATIONSHIP_FROM_A)
    public abstract Boolean relationshipFromA();

    @Nullable
    @ColumnName(ProgramContract.Columns.SELECT_INCIDENT_DATES_IN_FUTURE)
    public abstract Boolean selectIncidentDatesInFuture();

    @Nullable
    @ColumnName(ProgramContract.Columns.CAPTURE_COORDINATES)
    public abstract Boolean captureCoordinates();

    @Nullable
    @ColumnName(ProgramContract.Columns.USE_FIRST_STAGE_DURING_REGISTRATION)
    public abstract Boolean useFirstStageDuringRegistration();

    @Nullable
    @ColumnName(ProgramContract.Columns.DISPLAY_FRONT_PAGE_LIST)
    public abstract Boolean displayFrontPageList();

    @Nullable
    @ColumnName(ProgramContract.Columns.PROGRAM_TYPE)
    @ColumnAdapter(DbProgramTypeColumnAdapter.class)
    public abstract ProgramType programType();

    @Nullable
    @ColumnName(ProgramContract.Columns.RELATIONSHIP_TYPE)
    public abstract String relationshipType();

    @Nullable
    @ColumnName(ProgramContract.Columns.RELATIONSHIP_TEXT)
    public abstract String relationshipText();

    @Nullable
    @ColumnName(ProgramContract.Columns.RELATED_PROGRAM)
    public abstract String relatedProgram();

    @Nullable
    @ColumnName(ProgramContract.Columns.TRACKED_ENTITY)
    public abstract String trackedEntity();

    //TODO: Add these to the model/sql/... later:
//    @Nullable
//    @ColumnName(ProgramContract.Columns.CATEGORY_COMBO)
//    public abstract String categoryCombo();
//

    @AutoValue.Builder
    public static abstract class Builder extends BaseNameableObjectModel.Builder<Builder> {

        public abstract Builder version(@Nullable Integer version);

        public abstract Builder onlyEnrollOnce(@Nullable Boolean onlyEnrollOnce);

        public abstract Builder enrollmentDateLabel(@Nullable String enrollmentDateLabel);

        public abstract Builder displayIncidentDate(@Nullable Boolean displayIncidentDate);

        public abstract Builder incidentDateLabel(@Nullable String incidentDateLabel);

        public abstract Builder registration(@Nullable Boolean registration);

        public abstract Builder selectEnrollmentDatesInFuture(@Nullable Boolean selectEnrollmentDatesInFuture);

        public abstract Builder dataEntryMethod(@Nullable Boolean dataEntryMethod);

        public abstract Builder ignoreOverdueEvents(@Nullable Boolean ignoreOverdueEvents);

        public abstract Builder relationshipFromA(@Nullable Boolean relationshipFromA);

        public abstract Builder selectIncidentDatesInFuture(@Nullable Boolean selectIncidentDatesInFuture);

        public abstract Builder captureCoordinates(@Nullable Boolean captureCoordinates);

        public abstract Builder useFirstStageDuringRegistration(@Nullable Boolean useFirstStageDuringRegistration);

        public abstract Builder displayFrontPageList(@Nullable Boolean displayInFrontPageList);

        public abstract Builder programType(@Nullable ProgramType programType);

        public abstract Builder relationshipType(@Nullable String relationshipType);

        public abstract Builder relationshipText(@Nullable String relationshipText);

        public abstract Builder relatedProgram(@Nullable String relatedProgram);

        public abstract Builder trackedEntity(@Nullable String trackedEntity);
//
//        public abstract Builder categoryCombo(@Nullable String categoryCombo);

        public abstract ProgramModel build();
    }
}
