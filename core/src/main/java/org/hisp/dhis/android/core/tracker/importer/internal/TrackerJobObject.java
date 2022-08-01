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

package org.hisp.dhis.android.core.tracker.importer.internal;

import android.database.Cursor;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DbDateColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.StringArrayColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.TrackerImporterObjectTypeColumnAdapter;
import org.hisp.dhis.android.core.common.BaseObject;

import java.util.Date;
import java.util.List;

@AutoValue
@JsonDeserialize(builder = $$AutoValue_TrackerJobObject.Builder.class)
public abstract class TrackerJobObject extends BaseObject {

    @NonNull
    @ColumnAdapter(TrackerImporterObjectTypeColumnAdapter.class)
    public abstract TrackerImporterObjectType trackerType();

    @NonNull
    public abstract String objectUid();

    @NonNull
    public abstract String jobUid();

    @NonNull
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date lastUpdated();

    @NonNull
    @ColumnAdapter(StringArrayColumnAdapter.class)
    public abstract List<String> fileResources();

    @NonNull
    public static TrackerJobObject create(Cursor cursor) {
        return AutoValue_TrackerJobObject.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $$AutoValue_TrackerJobObject.Builder();
    }

    abstract Builder toBuilder();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder extends BaseObject.Builder<Builder> {
        public abstract Builder trackerType(TrackerImporterObjectType trackerType);

        public abstract Builder objectUid(String objectUid);

        public abstract Builder jobUid(String jobUid);

        public abstract Builder lastUpdated(Date lastUpdated);

        public abstract Builder fileResources(List<String> fileResources);

        public abstract TrackerJobObject build();
    }
}
