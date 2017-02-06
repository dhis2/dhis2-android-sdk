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
import org.hisp.dhis.android.core.data.database.DbProgramRuleVariableSourceTypeColumnAdapter;

@AutoValue
public abstract class ProgramRuleVariableModel extends BaseIdentifiableObjectModel {

    public static final String TABLE = "ProgramRuleVariable";

    public static class Columns extends BaseIdentifiableObjectModel.Columns {
        public static final String PROGRAM_STAGE = "programStage";
        public static final String PROGRAM_RULE_VARIABLE_SOURCE_TYPE = "programRuleVariableSourceType";
        public static final String USE_CODE_FOR_OPTION_SET = "useCodeForOptionSet";
        public static final String PROGRAM = "program";
        public static final String DATA_ELEMENT = "dataElement";
        public static final String TRACKED_ENTITY_ATTRIBUTE = "trackedEntityAttribute";
    }

    public static ProgramRuleVariableModel create(Cursor cursor) {
        return AutoValue_ProgramRuleVariableModel.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $$AutoValue_ProgramRuleVariableModel.Builder();
    }

    @NonNull
    public abstract ContentValues toContentValues();

    @Nullable
    @ColumnName(Columns.USE_CODE_FOR_OPTION_SET)
    public abstract Boolean useCodeForOptionSet();

    @Nullable
    @ColumnName(Columns.PROGRAM)
    public abstract String program();

    @Nullable
    @ColumnName(Columns.PROGRAM_STAGE)
    public abstract String programStage();

    @Nullable
    @ColumnName(Columns.DATA_ELEMENT)
    public abstract String dataElement();

    @Nullable
    @ColumnName(Columns.TRACKED_ENTITY_ATTRIBUTE)
    public abstract String trackedEntityAttribute();

    @Nullable
    @ColumnName(Columns.PROGRAM_RULE_VARIABLE_SOURCE_TYPE)
    @ColumnAdapter(DbProgramRuleVariableSourceTypeColumnAdapter.class)
    public abstract ProgramRuleVariableSourceType programRuleVariableSourceType();

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableObjectModel.Builder<Builder> {

        public abstract Builder useCodeForOptionSet(@Nullable Boolean useCodeForOptionSet);

        public abstract Builder program(@Nullable String program);

        public abstract Builder programStage(@Nullable String programStage);

        public abstract Builder dataElement(@Nullable String dataElement);

        public abstract Builder trackedEntityAttribute(@Nullable String trackedEntityAttribute);

        public abstract Builder programRuleVariableSourceType(
                @Nullable ProgramRuleVariableSourceType programRuleVariableSourceType);

        public abstract ProgramRuleVariableModel build();

    }

}
