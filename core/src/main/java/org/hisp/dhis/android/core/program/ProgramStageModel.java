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

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.common.FormType;
import org.hisp.dhis.android.core.data.database.DbFormTypeColumnAdapter;

@AutoValue
public abstract class ProgramStageModel extends BaseIdentifiableObjectModel {

    public static final String TABLE = "ProgramStage";

    public static class Columns extends BaseIdentifiableObjectModel.Columns {
        public static final String EXECUTION_DATE_LABEL = "executionDateLabel";
        public static final String ALLOW_GENERATE_NEXT_VISIT = "allowGenerateNextVisit";
        public static final String VALID_COMPLETE_ONLY = "validCompleteOnly";
        public static final String REPORT_DATE_TO_USE = "reportDateToUse";
        public static final String OPEN_AFTER_ENROLLMENT = "openAfterEnrollment";
        public static final String REPEATABLE = "repeatable";
        public static final String CAPTURE_COORDINATES = "captureCoordinates";
        public static final String FORM_TYPE = "formType";
        public static final String DISPLAY_GENERATE_EVENT_BOX = "displayGenerateEventBox";
        public static final String GENERATED_BY_ENROLMENT_DATE = "generatedByEnrollmentDate";
        public static final String AUTO_GENERATE_EVENT = "autoGenerateEvent";
        public static final String SORT_ORDER = "sortOrder";
        public static final String HIDE_DUE_DATE = "hideDueDate";
        public static final String BLOCK_ENTRY_FORM = "blockEntryForm";
        public static final String MIN_DAYS_FROM_START = "minDaysFromStart";
        public static final String STANDARD_INTERVAL = "standardInterval";
        public static final String PROGRAM = "program";
    }

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
