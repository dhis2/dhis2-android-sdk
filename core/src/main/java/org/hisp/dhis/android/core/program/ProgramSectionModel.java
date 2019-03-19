/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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
import androidx.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.utils.Utils;

@Deprecated
@AutoValue
public abstract class ProgramSectionModel extends BaseIdentifiableObjectModel {

    public static final String TABLE = "ProgramSection";

    public static class Columns extends BaseIdentifiableObjectModel.Columns {
        public static final String DESCRIPTION = "description";
        public static final String PROGRAM = "program";
        public static final String SORT_ORDER = "sortOrder";
        public static final String FORM_NAME = "formName";

        @Override
        public String[] all() {
            return Utils.appendInNewArray(super.all(), DESCRIPTION, PROGRAM, SORT_ORDER, FORM_NAME);
        }
    }

    public static ProgramSectionModel create(Cursor cursor) {
        return AutoValue_ProgramSectionModel.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $$AutoValue_ProgramSectionModel.Builder();
    }

    @Nullable
    @ColumnName(Columns.DESCRIPTION)
    public abstract String description();

    @Nullable
    @ColumnName(Columns.PROGRAM)
    public abstract String program();

    @Nullable
    @ColumnName(Columns.SORT_ORDER)
    public abstract Integer sortOrder();

    @Nullable
    @ColumnName(Columns.FORM_NAME)
    public abstract String formName();

    @AutoValue.Builder
    public static abstract class Builder extends BaseIdentifiableObjectModel.Builder<Builder> {

        public abstract Builder description(@Nullable String description);

        public abstract Builder program(@Nullable String program);

        public abstract Builder sortOrder(@Nullable Integer sortOrder);

        public abstract Builder formName(@Nullable String formName);

        public abstract ProgramSectionModel build();
    }
}
