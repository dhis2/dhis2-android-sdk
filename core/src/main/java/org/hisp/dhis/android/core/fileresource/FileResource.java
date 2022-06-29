/*
 *  Copyright (c) 2004-2022, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.core.fileresource;

import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DbDateColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.FileResourceDomainColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreFileResourceStorageStatusAdapter;
import org.hisp.dhis.android.core.common.BaseDataObject;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.ObjectWithUidInterface;

import java.util.Date;

@AutoValue
@JsonDeserialize(builder = $$AutoValue_FileResource.Builder.class)
public abstract class FileResource extends BaseDataObject implements ObjectWithUidInterface {

    @Nullable
    @JsonProperty(BaseIdentifiableObject.UID)
    public abstract String uid();

    @Nullable
    @JsonProperty
    public abstract String name();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date created();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date lastUpdated();

    @Nullable
    @JsonProperty
    public abstract String contentType();

    @Nullable
    @JsonProperty
    public abstract Long contentLength();

    @Nullable
    @JsonProperty
    public abstract String path();

    @Nullable
    @JsonProperty
    @ColumnAdapter(FileResourceDomainColumnAdapter.class)
    public abstract FileResourceDomain domain();

    @Nullable
    @JsonProperty
    @ColumnAdapter(IgnoreFileResourceStorageStatusAdapter.class)
    abstract FileResourceStorageStatus storageStatus();

    @NonNull
    public static FileResource create(Cursor cursor) {
        return AutoValue_FileResource.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new $$AutoValue_FileResource.Builder();
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder extends BaseDataObject.Builder<Builder> {

        @JsonProperty(BaseIdentifiableObject.UID)
        public abstract Builder uid(@NonNull String uid);

        public abstract Builder name(@NonNull String name);

        public abstract Builder created(@NonNull Date created);

        public abstract Builder lastUpdated(@NonNull Date lastUpdated);

        public abstract Builder contentType(@NonNull String contentType);

        public abstract Builder contentLength(@NonNull Long contentLength);

        public abstract Builder path(@NonNull String path);

        public abstract Builder domain(FileResourceDomain domain);

        abstract Builder storageStatus(FileResourceStorageStatus storageStatus);

        public abstract FileResource build();
    }
}