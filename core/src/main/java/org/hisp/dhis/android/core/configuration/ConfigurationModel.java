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

package org.hisp.dhis.android.core.configuration;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseModel;

import okhttp3.HttpUrl;

@AutoValue
public abstract class ConfigurationModel extends BaseModel {
    public static final String CONFIGURATION = "Configuration";

    public static class Columns extends BaseModel.Columns {
        public static final String SERVER_URL = "serverUrl";
    }

    @NonNull
    @ColumnName(Columns.SERVER_URL)
    @ColumnAdapter(HttpUrlColumnAdapter.class)
    public abstract HttpUrl serverUrl();

    // package visible for access in the store and manager
    abstract ContentValues toContentValues();

    // package visible for access in the store and manager
    static ConfigurationModel create(Cursor cursor) {
        return AutoValue_ConfigurationModel.createFromCursor(cursor);
    }

    // package visible for access in the store and manager
    public static Builder builder() {
        return new $$AutoValue_ConfigurationModel.Builder();
    }

    // package visible for access in the store and manager
    @AutoValue.Builder
    public static abstract class Builder extends BaseModel.Builder<Builder> {
        public abstract Builder serverUrl(HttpUrl serverUrl);

        public abstract ConfigurationModel build();
    }
}
