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

package org.hisp.dhis.android.core.note;

import android.database.Cursor;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.NoteTypeColumnAdapter;
import org.hisp.dhis.android.core.arch.helpers.UidGeneratorImpl;
import org.hisp.dhis.android.core.common.BaseDeletableDataObject;
import org.hisp.dhis.android.core.common.ObjectWithUidInterface;
import org.hisp.dhis.android.core.note.internal.NoteFields;

@AutoValue
@JsonDeserialize(builder = AutoValue_NewTrackerImporterNote.Builder.class)
public abstract class NewTrackerImporterNote extends BaseDeletableDataObject implements ObjectWithUidInterface {

   
    @JsonProperty(NoteFields.UID)
    public abstract String uid();

    @Nullable
    @JsonIgnore()
    @ColumnAdapter(NoteTypeColumnAdapter.class)
    public abstract Note.NoteType noteType();

    @Nullable
    @JsonIgnore()
    public abstract String event();

    @Nullable
    @JsonIgnore()
    public abstract String enrollment();

    @Nullable
    @JsonProperty()
    public abstract String value();

    @Nullable
    @JsonProperty()
    public abstract String storedBy();

    @Nullable
    @JsonProperty()
    public abstract String storedAt();

    public static Builder builder() {
        return new $$AutoValue_NewTrackerImporterNote.Builder();
    }

    public static NewTrackerImporterNote create(Cursor cursor) {
        return $AutoValue_NewTrackerImporterNote.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder extends BaseDeletableDataObject.Builder<Builder> {
        public abstract Builder id(Long id);

        @JsonProperty(NoteFields.UID)
        public abstract Builder uid(String uid);

        public abstract Builder noteType(Note.NoteType noteType);

        public abstract Builder event(String event);

        public abstract Builder enrollment(String enrollment);

        public abstract Builder value(String value);

        public abstract Builder storedBy(String storedBy);

        public abstract Builder storedAt(String storedAt);

        abstract NewTrackerImporterNote autoBuild();

        // Auxiliary fields
        abstract String uid();

        public NewTrackerImporterNote build() {
            try {
                uid();
            } catch (IllegalStateException e) {
                uid(new UidGeneratorImpl().generate());
            }

            return autoBuild();
        }
    }
}