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

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.common.FormType;
import org.hisp.dhis.android.core.data.database.DbFeatureTypeColumnAdapter;
import org.hisp.dhis.android.core.data.database.DbFormTypeColumnAdapter;
import org.hisp.dhis.android.core.data.database.DbPeriodTypeColumnAdapter;
import org.hisp.dhis.android.core.period.FeatureType;
import org.hisp.dhis.android.core.period.PeriodType;
import org.hisp.dhis.android.core.utils.Utils;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

@SuppressWarnings({"PMD.ExcessivePublicCount", "PMD.GodClass"})
@AutoValue
public abstract class ProgramStageModel extends BaseIdentifiableObjectModel {

    public static final String TABLE = "ProgramStage";

    public static class Columns extends BaseIdentifiableObjectModel.Columns {
        public static final String DESCRIPTION = "description";
        public static final String DISPLAY_DESCRIPTION = "displayDescription";
        public static final String EXECUTION_DATE_LABEL = "executionDateLabel";
        public static final String ALLOW_GENERATE_NEXT_VISIT = "allowGenerateNextVisit";
        public static final String VALID_COMPLETE_ONLY = "validCompleteOnly";
        public static final String REPORT_DATE_TO_USE = "reportDateToUse";
        public static final String OPEN_AFTER_ENROLLMENT = "openAfterEnrollment";
        public static final String REPEATABLE = "repeatable";

        /**
         * @deprecated since 2.29, replaced by {@link #FEATURE_TYPE}
         */
        @Deprecated
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
        public static final String PERIOD_TYPE = "periodType";
        public static final String ACCESS_DATA_WRITE = "accessDataWrite";
        public static final String REMIND_COMPLETED = "remindCompleted";
        public static final String FEATURE_TYPE = "featureType";

        @Override
        public String[] all() {
            return Utils.appendInNewArray(super.all(),
                    DESCRIPTION, DISPLAY_DESCRIPTION, EXECUTION_DATE_LABEL, ALLOW_GENERATE_NEXT_VISIT,
                    VALID_COMPLETE_ONLY, REPORT_DATE_TO_USE, OPEN_AFTER_ENROLLMENT, REPEATABLE, CAPTURE_COORDINATES,
                    FORM_TYPE, DISPLAY_GENERATE_EVENT_BOX, GENERATED_BY_ENROLMENT_DATE,
                    AUTO_GENERATE_EVENT, SORT_ORDER, HIDE_DUE_DATE, BLOCK_ENTRY_FORM,
                    MIN_DAYS_FROM_START, STANDARD_INTERVAL, PROGRAM, PERIOD_TYPE, ACCESS_DATA_WRITE,
                    REMIND_COMPLETED, FEATURE_TYPE);
        }
    }

    public static ProgramStageModel create(Cursor cursor) {
        return AutoValue_ProgramStageModel.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $$AutoValue_ProgramStageModel.Builder();
    }

    @Nullable
    @ColumnName(Columns.DESCRIPTION)
    public abstract String description();

    @Nullable
    @ColumnName(Columns.DISPLAY_DESCRIPTION)
    public abstract String displayDescription();

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

    /**
     * @deprecated since 2.29, replaced by {@link #featureType()}
     */
    @Deprecated
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

    @Nullable
    @ColumnName(Columns.PERIOD_TYPE)
    @ColumnAdapter(DbPeriodTypeColumnAdapter.class)
    public abstract PeriodType periodType();

    @Nullable
    @ColumnName(Columns.ACCESS_DATA_WRITE)
    public abstract Boolean accessDataWrite();

    @Nullable
    @ColumnName(Columns.REMIND_COMPLETED)
    public abstract Boolean remindCompleted();

    @Nullable
    @ColumnName(Columns.FEATURE_TYPE)
    @ColumnAdapter(DbFeatureTypeColumnAdapter.class)
    public abstract FeatureType featureType();


    @Override
    public void bindToStatement(@NonNull SQLiteStatement sqLiteStatement) {
        super.bindToStatement(sqLiteStatement);
        sqLiteBind(sqLiteStatement, 7, description());
        sqLiteBind(sqLiteStatement, 8, displayDescription());
        sqLiteBind(sqLiteStatement, 9, executionDateLabel());
        sqLiteBind(sqLiteStatement, 10, allowGenerateNextVisit());
        sqLiteBind(sqLiteStatement, 11, validCompleteOnly());
        sqLiteBind(sqLiteStatement, 12, reportDateToUse());
        sqLiteBind(sqLiteStatement, 13, openAfterEnrollment());
        sqLiteBind(sqLiteStatement, 14, repeatable());
        sqLiteBind(sqLiteStatement, 15, captureCoordinates());
        sqLiteBind(sqLiteStatement, 16, formType().name());
        sqLiteBind(sqLiteStatement, 17, displayGenerateEventBox());
        sqLiteBind(sqLiteStatement, 18, generatedByEnrollmentDate());
        sqLiteBind(sqLiteStatement, 19, autoGenerateEvent());
        sqLiteBind(sqLiteStatement, 20, sortOrder());
        sqLiteBind(sqLiteStatement, 21, hideDueDate());
        sqLiteBind(sqLiteStatement, 22, blockEntryForm());
        sqLiteBind(sqLiteStatement, 23, minDaysFromStart());
        sqLiteBind(sqLiteStatement, 24, standardInterval());
        sqLiteBind(sqLiteStatement, 25, program());
        sqLiteBind(sqLiteStatement, 26, periodType());
        sqLiteBind(sqLiteStatement, 27, accessDataWrite());
        sqLiteBind(sqLiteStatement, 28, remindCompleted());
        sqLiteBind(sqLiteStatement, 29, featureType());
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableObjectModel.Builder<Builder> {

        public abstract Builder description(@Nullable String description);

        public abstract Builder displayDescription(@Nullable String displayDescription);

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

        public abstract Builder periodType(@Nullable PeriodType periodType);

        public abstract Builder accessDataWrite(@Nullable Boolean accessDataWrite);

        public abstract Builder remindCompleted(@Nullable Boolean remindCompleted);

        public abstract Builder featureType(@Nullable FeatureType featureType);

        public abstract ProgramStageModel build();

    }
}
