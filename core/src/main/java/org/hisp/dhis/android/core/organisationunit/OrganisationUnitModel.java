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

package org.hisp.dhis.android.core.organisationunit;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseNameableObjectModel;
import org.hisp.dhis.android.core.data.database.DbDateColumnAdapter;

import java.util.Date;

@AutoValue
public abstract class OrganisationUnitModel extends BaseNameableObjectModel {
    public static final String TABLE = "OrganisationUnit";

    public static class Columns extends BaseNameableObjectModel.Columns {
        public static final String PATH = "path";
        public static final String OPENING_DATE = "openingDate";
        public static final String CLOSED_DATE = "closedDate";
        public static final String PARENT = "parent";
        public static final String LEVEL = "level";
    }
    public enum Scope {
        SCOPE_DATA_CAPTURE,
        SCOPE_TEI_SEARCH
    }

    public static OrganisationUnitModel create(Cursor cursor) {
        return AutoValue_OrganisationUnitModel.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $$AutoValue_OrganisationUnitModel.Builder();
    }

    @Nullable
    @ColumnName(Columns.PARENT)
    public abstract String parent();

    @Nullable
    @ColumnName(Columns.PATH)
    public abstract String path();

    @Nullable
    @ColumnName(Columns.OPENING_DATE)
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date openingDate();

    @Nullable
    @ColumnName(Columns.CLOSED_DATE)
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date closedDate();

    @Nullable
    @ColumnName(Columns.LEVEL)
    public abstract Integer level();

    @NonNull
    public abstract ContentValues toContentValues();

    @AutoValue.Builder
    public static abstract class Builder extends BaseNameableObjectModel.Builder<Builder> {
        public abstract Builder parent(@Nullable String parent);

        public abstract Builder path(@Nullable String path);

        public abstract Builder openingDate(@Nullable Date openingDate);

        public abstract Builder closedDate(@Nullable Date closedDate);

        public abstract Builder level(@Nullable Integer level);

        public abstract OrganisationUnitModel build();
    }
}
