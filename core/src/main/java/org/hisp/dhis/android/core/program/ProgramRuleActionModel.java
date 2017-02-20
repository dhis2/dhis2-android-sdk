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

@AutoValue
public abstract class ProgramRuleActionModel extends BaseIdentifiableObjectModel {

    public static final String TABLE = "ProgramRuleAction";

    public static class Columns extends BaseIdentifiableObjectModel.Columns {
        public static final String DATA = "data";
        public static final String CONTENT = "content";
        public static final String LOCATION = "location";
        public static final String TRACKED_ENTITY_ATTRIBUTE = "trackedEntityAttribute";
        public static final String PROGRAM_INDICATOR = "programIndicator";
        public static final String PROGRAM_STAGE_SECTION = "programStageSection";
        public static final String PROGRAM_RULE_ACTION_TYPE = "programRuleActionType";
        public static final String PROGRAM_STAGE = "programStage";
        public static final String DATA_ELEMENT = "dataElement";
        public static final String PROGRAM_RULE = "programRule";
    }

    @NonNull
    public static ProgramRuleActionModel.Builder builder() {
        return new $$AutoValue_ProgramRuleActionModel.Builder();
    }

    @NonNull
    public static ProgramRuleActionModel create(Cursor cursor) {
        return AutoValue_ProgramRuleActionModel.createFromCursor(cursor);
    }

    @Nullable
    @ColumnName(Columns.DATA)
    public abstract String data();

    @Nullable
    @ColumnName(Columns.CONTENT)
    public abstract String content();

    @Nullable
    @ColumnName(Columns.LOCATION)
    public abstract String location();

    @Nullable
    @ColumnName(Columns.TRACKED_ENTITY_ATTRIBUTE)
    public abstract String trackedEntityAttribute();

    @Nullable
    @ColumnName(Columns.PROGRAM_INDICATOR)
    public abstract String programIndicator();

    @Nullable
    @ColumnName(Columns.PROGRAM_STAGE_SECTION)
    public abstract String programStageSection();

    @NonNull
    @ColumnName(Columns.PROGRAM_RULE_ACTION_TYPE)
    @ColumnAdapter(ProgramRuleActionTypeColumnAdapter.class)
    public abstract ProgramRuleActionType programRuleActionType();

    @Nullable
    @ColumnName(Columns.PROGRAM_STAGE)
    public abstract String programStage();

    @Nullable
    @ColumnName(Columns.DATA_ELEMENT)
    public abstract String dataElement();

    @Nullable
    @ColumnName(Columns.PROGRAM_RULE)
    public abstract String programRule();

    @NonNull
    public abstract ContentValues toContentValues();

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableObjectModel.Builder<Builder> {

        public abstract Builder data(@Nullable String data);

        public abstract Builder content(@Nullable String content);

        public abstract Builder location(@Nullable String location);

        public abstract Builder trackedEntityAttribute(
                @Nullable String trackedEntityAttribute);

        public abstract Builder programIndicator(@Nullable String programIndicator);

        public abstract Builder programStageSection(
                @Nullable String programStageSection);

        public abstract Builder programRuleActionType(
                @Nullable ProgramRuleActionType programRuleActionType);

        public abstract Builder programStage(@Nullable String programStage);

        public abstract Builder dataElement(@Nullable String dataElement);

        public abstract Builder programRule(@Nullable String programRule);

        public abstract ProgramRuleActionModel build();
    }
}
