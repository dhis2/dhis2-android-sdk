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

@AutoValue
public abstract class NoteCreateProjection {

    @Nullable
    public abstract Note.NoteType noteType();

    @Nullable
    public abstract String event();

    @Nullable
    public abstract String enrollment();

    @Nullable
    public abstract String value();

    /**
     * @deprecated replaced by {@link #create(Note.NoteType, String, String)}
     */
    @Deprecated
    public static NoteCreateProjection create(String enrollment, String value) {
        return builder()
                .noteType(Note.NoteType.ENROLLMENT_NOTE)
                .enrollment(enrollment)
                .value(value)
                .build();
    }

    public static NoteCreateProjection create(Note.NoteType noteType, String ownerUid, String value) {
        Builder builder = builder()
                .noteType(noteType)
                .value(value);

        if (noteType == Note.NoteType.ENROLLMENT_NOTE) {
            return builder.enrollment(ownerUid).build();
        } else if (noteType == Note.NoteType.EVENT_NOTE) {
            return builder.event(ownerUid).build();
        } else {
            throw new IllegalArgumentException("Unknown note type");
        }
    }

    public static Builder builder() {
        return new AutoValue_NoteCreateProjection.Builder();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder noteType(Note.NoteType noteType);

        public abstract Builder event(String event);

        public abstract Builder enrollment(String enrollment);

        public abstract Builder value(String value);

        abstract NoteCreateProjection autoBuild();

        // Auxiliary fields
        abstract Note.NoteType noteType();
        abstract String event();
        abstract String enrollment();

        public NoteCreateProjection build() {
            if (noteType() == null) {
                throw new IllegalArgumentException("Note type is null");
            } else if (noteType() == Note.NoteType.ENROLLMENT_NOTE && enrollment() == null) {
                throw new IllegalArgumentException("Enrollment note type need an enrollment uid");
            } else if (noteType() == Note.NoteType.EVENT_NOTE && event() == null) {
                throw new IllegalArgumentException("Event note type need an event uid");
            }

            return autoBuild();
        }
    }
}
