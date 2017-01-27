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

package org.hisp.dhis.android.core.common;

import android.support.annotation.Nullable;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;

import org.hisp.dhis.android.core.data.database.DbDateColumnAdapter;

import java.util.Date;

public abstract class BaseIdentifiableObjectModel extends BaseModel implements IdentifiableObject {

    public static class Columns extends BaseModel.Columns {
        public static final String UID = "uid";
        public static final String CODE = "code";
        public static final String NAME = "name";
        public static final String DISPLAY_NAME = "displayName";
        public static final String CREATED = "created";
        public static final String LAST_UPDATED = "lastUpdated";
    }

    @Override
    @Nullable
    @ColumnName(Columns.UID)
    public abstract String uid();

    @Override
    @Nullable
    @ColumnName(Columns.CODE)
    public abstract String code();

    @Override
    @Nullable
    @ColumnName(Columns.NAME)
    public abstract String name();

    @Override
    @Nullable
    @ColumnName(Columns.DISPLAY_NAME)
    public abstract String displayName();

    @Override
    @Nullable
    @ColumnName(Columns.CREATED)
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date created();

    @Override
    @Nullable
    @ColumnName(Columns.LAST_UPDATED)
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date lastUpdated();

    protected static abstract class Builder<T extends Builder> extends BaseModel.Builder<T> {
        public abstract T uid(String uid);

        public abstract T code(@Nullable String code);

        public abstract T name(@Nullable String name);

        public abstract T displayName(@Nullable String displayName);

        public abstract T created(@Nullable Date created);

        public abstract T lastUpdated(@Nullable Date lastUpdated);
    }
}
