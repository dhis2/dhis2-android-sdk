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

import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;

@AutoValue
public abstract class ProgramStageDataElementModel extends BaseIdentifiableObjectModel {

    public static final String TABLE = "ProgramStageDataElement";

    public static class Columns extends BaseIdentifiableObjectModel.Columns {
        public static final String DISPLAY_IN_REPORTS = "displayInReports";
        public static final String COMPULSORY = "compulsory";
        public static final String ALLOW_PROVIDED_ELSEWHERE = "allowProvidedElsewhere";
        public static final String SORT_ORDER = "sortOrder";
        public static final String ALLOW_FUTURE_DATE = "allowFutureDate";
        public static final String DATA_ELEMENT = "dataElement";
        public static final String PROGRAM_STAGE_SECTION = "programStageSection";
        public static final String PROGRAM_STAGE = "programStage";
    }

    @Nullable
    @ColumnName(Columns.DISPLAY_IN_REPORTS)
    public abstract Boolean displayInReports();

    @Nullable
    @ColumnName(Columns.COMPULSORY)
    public abstract Boolean compulsory();

    @Nullable
    @ColumnName(Columns.ALLOW_PROVIDED_ELSEWHERE)
    public abstract Boolean allowProvidedElsewhere();

    @Nullable
    @ColumnName(Columns.SORT_ORDER)
    public abstract Integer sortOrder();

    @Nullable
    @ColumnName(Columns.ALLOW_FUTURE_DATE)
    public abstract Boolean allowFutureDate();

    @Nullable
    @ColumnName(Columns.DATA_ELEMENT)
    public abstract String dataElement();

    @Nullable
    @ColumnName(Columns.PROGRAM_STAGE)
    public abstract String programStage();

    @Nullable
    @ColumnName(Columns.PROGRAM_STAGE_SECTION)
    public abstract String programStageSection();

    public static ProgramStageDataElementModel create(Cursor cursor) {
        return AutoValue_ProgramStageDataElementModel.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $$AutoValue_ProgramStageDataElementModel.Builder();
    }

    @NonNull
    public abstract ContentValues toContentValues();

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableObjectModel.Builder<Builder> {

        public abstract Builder displayInReports(@Nullable Boolean displayInReports);

        public abstract Builder compulsory(@Nullable Boolean compulsory);

        public abstract Builder allowProvidedElsewhere(@Nullable Boolean allowProvidedElsewhere);

        public abstract Builder sortOrder(@Nullable Integer sortOrder);

        public abstract Builder allowFutureDate(@Nullable Boolean allowFutureDate);

        public abstract Builder dataElement(@Nullable String dataElement);

        public abstract Builder programStage(@Nullable String programStage);

        public abstract Builder programStageSection(@Nullable String programStageSection);

        public abstract ProgramStageDataElementModel build();
    }

}
