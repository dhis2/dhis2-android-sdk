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

package org.hisp.dhis.android.core.trackedentity;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseNameableObjectModel;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.data.database.DbValueTypeColumnAdapter;

import static org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeContract.Columns.DISPLAY_IN_LIST_NO_PROGRAM;
import static org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeContract.Columns.DISPLAY_ON_VISIT_SCHEDULE;
import static org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeContract.Columns.EXPRESSION;
import static org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeContract.Columns.GENERATED;
import static org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeContract.Columns.INHERIT;
import static org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeContract.Columns.OPTION_SET;
import static org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeContract.Columns.ORG_UNIT_SCOPE;
import static org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeContract.Columns.PATTERN;
import static org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeContract.Columns.PROGRAM_SCOPE;
import static org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeContract.Columns.SEARCH_SCOPE;
import static org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeContract.Columns.SORT_ORDER_IN_LIST_NO_PROGRAM;
import static org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeContract.Columns.UNIQUE;
import static org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeContract.Columns.VALUE_TYPE;

// TODO: Tests
@AutoValue
public abstract class TrackedEntityAttributeModel extends BaseNameableObjectModel {

    @NonNull
    public static Builder builder() {
        return new $$AutoValue_TrackedEntityAttributeModel.Builder();
    }

    @NonNull
    public static TrackedEntityAttributeModel create(Cursor cursor) {
        return AutoValue_TrackedEntityAttributeModel.createFromCursor(cursor);
    }

    @Nullable
    @ColumnName(PATTERN)
    public abstract String pattern();

    @Nullable
    @ColumnName(SORT_ORDER_IN_LIST_NO_PROGRAM)
    public abstract Integer sortOrderInListNoProgram();

    @Nullable
    @ColumnName(OPTION_SET)
    public abstract String optionSet();

    @Nullable
    @ColumnName(VALUE_TYPE)
    @ColumnAdapter(DbValueTypeColumnAdapter.class)
    public abstract ValueType valueType();

    @Nullable
    @ColumnName(EXPRESSION)
    public abstract String expression();

    @Nullable
    @ColumnName(SEARCH_SCOPE)
    @ColumnAdapter(TrackedEntityAttributeSearchScopeColumnAdapter.class)
    public abstract TrackedEntityAttributeSearchScope searchScope();

    @Nullable
    @ColumnName(PROGRAM_SCOPE)
    public abstract Boolean programScope();

    @Nullable
    @ColumnName(DISPLAY_IN_LIST_NO_PROGRAM)
    public abstract Boolean displayInListNoProgram();

    @Nullable
    @ColumnName(GENERATED)
    public abstract Boolean generated();

    @Nullable
    @ColumnName(DISPLAY_ON_VISIT_SCHEDULE)
    public abstract Boolean displayOnVisitSchedule();

    @Nullable
    @ColumnName(ORG_UNIT_SCOPE)
    public abstract Boolean orgUnitScope();

    @Nullable
    @ColumnName(UNIQUE)
    public abstract Boolean unique();

    @Nullable
    @ColumnName(INHERIT)
    public abstract Boolean inherit();

    @NonNull
    public abstract ContentValues toContentValues();

    @AutoValue.Builder
    public static abstract class Builder extends BaseNameableObjectModel.Builder<Builder> {
        public abstract Builder pattern(@Nullable String pattern);

        public abstract Builder sortOrderInListNoProgram(@Nullable Integer sortInProgram);

        public abstract Builder optionSet(@Nullable String optionSet);

        public abstract Builder valueType(@Nullable ValueType valueType);

        public abstract Builder expression(@Nullable String expression);

        public abstract Builder searchScope(@Nullable TrackedEntityAttributeSearchScope searchScope);

        public abstract Builder programScope(@Nullable Boolean programScope);

        public abstract Builder displayInListNoProgram(@Nullable Boolean displayInListNoProgram);

        public abstract Builder generated(@Nullable Boolean generated);

        public abstract Builder displayOnVisitSchedule(@Nullable Boolean displayOnVisitSchedule);

        public abstract Builder orgUnitScope(@Nullable Boolean orgUnitScope);

        public abstract Builder unique(@Nullable Boolean unique);

        public abstract Builder inherit(@Nullable Boolean inherit);

        public abstract TrackedEntityAttributeModel build();
    }
}
