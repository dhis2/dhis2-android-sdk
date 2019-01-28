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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseNameableObjectModel;
import org.hisp.dhis.android.core.utils.Utils;

@Deprecated
@AutoValue
public abstract class ProgramTrackedEntityAttributeModel extends BaseNameableObjectModel {

    public static final String TABLE = "ProgramTrackedEntityAttribute";

    public static class Columns extends BaseNameableObjectModel.Columns {
        public static final String MANDATORY = "mandatory";
        public static final String TRACKED_ENTITY_ATTRIBUTE = "trackedEntityAttribute";
        public static final String ALLOW_FUTURE_DATE = "allowFutureDate";
        public static final String DISPLAY_IN_LIST = "displayInList";
        public static final String PROGRAM = "program";
        public static final String SORT_ORDER = "sortOrder";
        public static final String SEARCHABLE = "searchable";

        @Override
        public String[] all() {
            return Utils.appendInNewArray(super.all(), MANDATORY, TRACKED_ENTITY_ATTRIBUTE, ALLOW_FUTURE_DATE,
                    DISPLAY_IN_LIST, PROGRAM, SORT_ORDER, SEARCHABLE);
        }
    }

    @NonNull
    public static ProgramTrackedEntityAttributeModel.Builder builder() {
        return new $$AutoValue_ProgramTrackedEntityAttributeModel.Builder();
    }

    @NonNull
    public static ProgramTrackedEntityAttributeModel create(Cursor cursor) {
        return AutoValue_ProgramTrackedEntityAttributeModel.createFromCursor(cursor);
    }

    @Nullable
    @ColumnName(Columns.MANDATORY)
    public abstract Boolean mandatory();

    @NonNull
    @ColumnName(Columns.TRACKED_ENTITY_ATTRIBUTE)
    public abstract String trackedEntityAttribute();

    @Nullable
    @ColumnName(Columns.ALLOW_FUTURE_DATE)
    public abstract Boolean allowFutureDate();

    @Nullable
    @ColumnName(Columns.DISPLAY_IN_LIST)
    public abstract Boolean displayInList();

    @Nullable
    @ColumnName(Columns.PROGRAM)
    public abstract String program();

    @Nullable
    @ColumnName(Columns.SORT_ORDER)
    public abstract Integer sortOrder();

    @Nullable
    @ColumnName(Columns.SEARCHABLE)
    public abstract Boolean searchable();

    @AutoValue.Builder
    public static abstract class Builder extends BaseNameableObjectModel.Builder<Builder> {

        public abstract Builder mandatory(@Nullable Boolean mandatory);

        public abstract Builder trackedEntityAttribute(@NonNull String trackedEntityAttribute);

        public abstract Builder allowFutureDate(@Nullable Boolean allowFutureFate);

        public abstract Builder displayInList(@Nullable Boolean displayInList);

        public abstract Builder program(@Nullable String program);

        public abstract Builder sortOrder(@Nullable Integer sortOrder);

        public abstract Builder searchable(@Nullable Boolean searchable);

        public abstract ProgramTrackedEntityAttributeModel build();
    }
}