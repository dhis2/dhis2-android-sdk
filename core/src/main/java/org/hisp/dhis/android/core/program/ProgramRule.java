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

import android.database.sqlite.SQLiteStatement;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.BaseModel;
import org.hisp.dhis.android.core.common.Model;
import org.hisp.dhis.android.core.common.UidsHelper;
import org.hisp.dhis.android.core.data.database.IgnoreProgramRuleActionListAdapter;
import org.hisp.dhis.android.core.data.database.ProgramStageWithUidColumnAdapter;
import org.hisp.dhis.android.core.data.database.ProgramWithUidColumnAdapter;

import java.util.List;

import static org.hisp.dhis.android.core.utils.StoreUtils.sqLiteBind;

@AutoValue
@JsonDeserialize(builder = AutoValue_ProgramRule.Builder.class)
public abstract class ProgramRule extends BaseIdentifiableObject implements Model {

    // TODO move to base class after whole object refactor
    @Override
    @Nullable
    @ColumnName(BaseModel.Columns.ID)
    @JsonIgnore()
    public abstract Long id();

    @Nullable
    public abstract Integer priority();

    @Nullable
    public abstract String condition();

    @Nullable
    @ColumnAdapter(ProgramWithUidColumnAdapter.class)
    public abstract Program program();

    @Nullable
    @ColumnAdapter(ProgramStageWithUidColumnAdapter.class)
    public abstract ProgramStage programStage();

    @Nullable
    @ColumnAdapter(IgnoreProgramRuleActionListAdapter.class)
    public abstract List<ProgramRuleAction> programRuleActions();

    @Override
    public void bindToStatement(@NonNull SQLiteStatement sqLiteStatement) {
        super.bindToStatement(sqLiteStatement);
        sqLiteBind(sqLiteStatement, 7, priority());
        sqLiteBind(sqLiteStatement, 8, condition());
        sqLiteBind(sqLiteStatement, 9, UidsHelper.getUidOrNull(program()));
        sqLiteBind(sqLiteStatement, 10, UidsHelper.getUidOrNull(programStage()));
    }

    public static Builder builder() {
        return new AutoValue_ProgramRule.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder extends BaseIdentifiableObject.Builder<ProgramRule.Builder> {

        public abstract Builder id(Long id);

        public abstract Builder priority(Integer priority);

        public abstract Builder condition(String condition);

        public abstract Builder program(Program program);

        public abstract Builder programStage(ProgramStage programStage);

        public abstract Builder programRuleActions(List<ProgramRuleAction> programRuleActions);

        public abstract ProgramRule build();
    }
}