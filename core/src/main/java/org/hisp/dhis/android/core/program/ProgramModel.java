/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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

    public static final String TABLE = "Program";

    public static class Columns extends BaseNameableObjectModel.Columns {
        public static final String VERSION = "version";
        public static final String ONLY_ENROLL_ONCE = "onlyEnrollOnce";
        public static final String ENROLLMENT_DATE_LABEL = "enrollmentDateLabel";
        public static final String DISPLAY_INCIDENT_DATE = "displayIncidentDate";
        public static final String INCIDENT_DATE_LABEL = "incidentDateLabel";
        public static final String REGISTRATION = "registration";
        public static final String SELECT_ENROLLMENT_DATES_IN_FUTURE = "selectEnrollmentDatesInFuture";
        public static final String DATA_ENTRY_METHOD = "dataEntryMethod";
        public static final String IGNORE_OVERDUE_EVENTS = "ignoreOverdueEvents";
        public static final String RELATIONSHIP_FROM_A = "relationshipFromA";
        public static final String SELECT_INCIDENT_DATES_IN_FUTURE = "selectIncidentDatesInFuture";
        public static final String CAPTURE_COORDINATES = "captureCoordinates";
        public static final String USE_FIRST_STAGE_DURING_REGISTRATION = "useFirstStageDuringRegistration";
        public static final String DISPLAY_FRONT_PAGE_LIST = "displayFrontPageList";
        public static final String PROGRAM_TYPE = "programType";
        public static final String RELATIONSHIP_TYPE = "relationshipType";
        public static final String RELATIONSHIP_TEXT = "relationshipText";
        public static final String RELATED_PROGRAM = "relatedProgram";
        public static final String TRACKED_ENTITY = "trackedEntity";
    }

    public static ProgramModel create(Cursor cursor) {
        return AutoValue_ProgramModel.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $$AutoValue_ProgramModel.Builder();
    }

    @NonNull
    public abstract ContentValues toContentValues();

    @Nullable
    @ColumnName(Columns.VERSION)
    public abstract Integer version();

    @Nullable
    @ColumnName(Columns.ONLY_ENROLL_ONCE)
    public abstract Boolean onlyEnrollOnce();

    @Nullable
    @ColumnName(Columns.ENROLLMENT_DATE_LABEL)
    public abstract String enrollmentDateLabel();

    @Nullable
    @ColumnName(Columns.DISPLAY_INCIDENT_DATE)
    public abstract Boolean displayIncidentDate();

    @Nullable
    @ColumnName(Columns.INCIDENT_DATE_LABEL)
    public abstract String incidentDateLabel();

    @Nullable
    @ColumnName(Columns.REGISTRATION)
    public abstract Boolean registration();

    @Nullable
    @ColumnName(Columns.SELECT_ENROLLMENT_DATES_IN_FUTURE)
    public abstract Boolean selectEnrollmentDatesInFuture();

    @Nullable
    @ColumnName(Columns.DATA_ENTRY_METHOD)
    public abstract Boolean dataEntryMethod();

    @Nullable
    @ColumnName(Columns.IGNORE_OVERDUE_EVENTS)
    public abstract Boolean ignoreOverdueEvents();

    @Nullable
    @ColumnName(Columns.RELATIONSHIP_FROM_A)
    public abstract Boolean relationshipFromA();

    @Nullable
    @ColumnName(Columns.SELECT_INCIDENT_DATES_IN_FUTURE)
    public abstract Boolean selectIncidentDatesInFuture();

    @Nullable
    @ColumnName(Columns.CAPTURE_COORDINATES)
    public abstract Boolean captureCoordinates();

    @Nullable
    @ColumnName(Columns.USE_FIRST_STAGE_DURING_REGISTRATION)
    public abstract Boolean useFirstStageDuringRegistration();

    @Nullable
    @ColumnName(Columns.DISPLAY_FRONT_PAGE_LIST)
    public abstract Boolean displayFrontPageList();

    @Nullable
    @ColumnName(Columns.PROGRAM_TYPE)
    @ColumnAdapter(DbProgramTypeColumnAdapter.class)
    public abstract ProgramType programType();

    @Nullable
    @ColumnName(Columns.RELATIONSHIP_TYPE)
    public abstract String relationshipType();

    @Nullable
    @ColumnName(Columns.RELATIONSHIP_TEXT)
    public abstract String relationshipText();

    @Nullable
    @ColumnName(Columns.RELATED_PROGRAM)
    public abstract String relatedProgram();

    @Nullable
    @ColumnName(Columns.TRACKED_ENTITY)
    public abstract String trackedEntity();

    //TODO: Add these to the model/sql/... later:
//    @Nullable
//    @ColumnName(Columns.CATEGORY_COMBO)
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
