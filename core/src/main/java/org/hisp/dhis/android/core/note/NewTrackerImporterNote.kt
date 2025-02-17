/*
 *  Copyright (c) 2004-2023, University of Oslo
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

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.helpers.UidGeneratorImpl;
import org.hisp.dhis.android.core.common.ObjectWithDeleteInterface;
import org.hisp.dhis.android.core.common.ObjectWithSyncStateInterface;
import org.hisp.dhis.android.core.common.ObjectWithUidInterface;
import org.hisp.dhis.android.core.common.State;

@AutoValue
public abstract class NewTrackerImporterNote implements ObjectWithUidInterface,
        ObjectWithSyncStateInterface, ObjectWithDeleteInterface {

    @Override
    public abstract String uid();

    @Nullable
    @Override
    public abstract Boolean deleted();

    @Nullable
    @Override
    public abstract State syncState();

    @Nullable
    public abstract Note.NoteType noteType();

    @Nullable
    public abstract String event();

    @Nullable
    public abstract String enrollment();

    @Nullable
    public abstract String value();

    @Nullable
    public abstract String storedBy();

    @Nullable
    public abstract String storedAt();

    public static Builder builder() {
        return new AutoValue_NewTrackerImporterNote.Builder();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder uid(String uid);

        public abstract Builder deleted(Boolean deleted);

        public abstract Builder syncState(State syncState);

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
