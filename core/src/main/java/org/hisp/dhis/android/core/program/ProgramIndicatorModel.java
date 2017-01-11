/*
 * Copyright (c) 2016, University of Oslo
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

import org.hisp.dhis.android.core.common.BaseNameableObjectModel;

import static org.hisp.dhis.android.core.program.ProgramIndicatorContract.Columns.DECIMALS;
import static org.hisp.dhis.android.core.program.ProgramIndicatorContract.Columns.DIMENSION_ITEM;
import static org.hisp.dhis.android.core.program.ProgramIndicatorContract.Columns.DISPLAY_IN_FORM;
import static org.hisp.dhis.android.core.program.ProgramIndicatorContract.Columns.EXPRESSION;
import static org.hisp.dhis.android.core.program.ProgramIndicatorContract.Columns.FILTER;

@AutoValue
public abstract class ProgramIndicatorModel extends BaseNameableObjectModel {

    @NonNull
    public static ProgramIndicatorModel.Builder builder() {
        return new $$AutoValue_ProgramIndicatorModel.Builder();
    }

    @NonNull
    public static ProgramIndicatorModel create(Cursor cursor) {
        return AutoValue_ProgramIndicatorModel.createFromCursor(cursor);
    }

    @Nullable
    @ColumnName(DISPLAY_IN_FORM)
    public abstract Boolean displayInForm();

    @Nullable
    @ColumnName(EXPRESSION)
    public abstract String expression();

    @Nullable
    @ColumnName(DIMENSION_ITEM)
    public abstract String dimensionItem();

    @Nullable
    @ColumnName(FILTER)
    public abstract String filter();

    @Nullable
    @ColumnName(DECIMALS)
    public abstract Integer decimals();

    @NonNull
    public abstract ContentValues toContentValues();

    @AutoValue.Builder
    public static abstract class Builder extends BaseNameableObjectModel.Builder<Builder> {

        @ColumnName(DISPLAY_IN_FORM)
        public abstract Builder displayInForm(@Nullable Boolean displayInForm);

        @ColumnName(EXPRESSION)
        public abstract Builder expression(@Nullable String expression);

        @ColumnName(DIMENSION_ITEM)
        public abstract Builder dimensionItem(@Nullable String dimensionItem);

        @ColumnName(FILTER)
        public abstract Builder filter(@Nullable String filter);

        @ColumnName(DECIMALS)
        public abstract Builder decimals(@Nullable Integer decimals);

        abstract ProgramIndicatorModel build();
    }
}