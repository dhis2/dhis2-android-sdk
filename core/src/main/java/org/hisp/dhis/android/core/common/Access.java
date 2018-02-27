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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.NestedField;

@AutoValue
public abstract class Access {
    private static final String READ = "read";
    private static final String WRITE = "write";
    private static final String UPDATE = "update";
    private static final String DELETE = "delete";
    private static final String EXTERNALIZE = "externalize";
    private static final String MANAGE = "manage";
    private static final String DATA = "data";

    public static final Field<Access, Boolean> read = Field.create(READ);
    public static final Field<Access, Boolean> write = Field.create(WRITE);
    public static final Field<Access, Boolean> update = Field.create(UPDATE);
    public static final Field<Access, Boolean> delete = Field.create(DELETE);
    public static final Field<Access, Boolean> externalize = Field.create(EXTERNALIZE);
    public static final Field<Access, Boolean> manage = Field.create(MANAGE);
    public static final NestedField<Access, DataAccess> data = NestedField.create(DATA);

    @Nullable
    @JsonProperty(READ)
    public abstract Boolean read();

    @Nullable
    @JsonProperty(WRITE)
    public abstract Boolean write();

    @Nullable
    @JsonProperty(UPDATE)
    public abstract Boolean update();

    @Nullable
    @JsonProperty(DELETE)
    public abstract Boolean delete();

    @Nullable
    @JsonProperty(EXTERNALIZE)
    public abstract Boolean externalize();

    @Nullable
    @JsonProperty(MANAGE)
    public abstract Boolean manage();

    @NonNull
    @JsonProperty(DATA)
    public abstract DataAccess data();

    @JsonCreator
    public static Access create(@JsonProperty(READ) Boolean read,
                                @JsonProperty(WRITE) Boolean write,
                                @JsonProperty(UPDATE) Boolean update,
                                @JsonProperty(DELETE) Boolean delete,
                                @JsonProperty(EXTERNALIZE) Boolean externalize,
                                @JsonProperty(MANAGE) Boolean manage,
                                @JsonProperty(DATA) DataAccess data) {
        return new AutoValue_Access(read, write, update, delete, externalize, manage, data);
    }
}