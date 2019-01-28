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

package org.hisp.dhis.android.core.program;

import android.database.Cursor;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseNameableObject;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;

@AutoValue
@JsonDeserialize(builder = AutoValue_ProgramTrackedEntityAttribute.Builder.class)
public abstract class ProgramTrackedEntityAttribute extends BaseNameableObject {

    @Nullable
    public abstract Boolean mandatory();

    @Nullable
    public abstract TrackedEntityAttribute trackedEntityAttribute();

    @Nullable
    public abstract Boolean allowFutureDate();

    @Nullable
    public abstract Boolean displayInList();

    @Nullable
    public abstract Program program();

    @Nullable
    public abstract Integer sortOrder();

    @Nullable
    public abstract Boolean searchable();

    public static Builder builder() {
        return new AutoValue_ProgramTrackedEntityAttribute.Builder();
    }
    static ProgramTrackedEntityAttribute create(Cursor cursor) {
        return $AutoValue_ProgramTrackedEntityAttribute.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder extends BaseNameableObject.Builder<Builder>{

        public abstract Builder id(Long id);

        public abstract Builder mandatory(Boolean mandatory);

        public abstract Builder trackedEntityAttribute(TrackedEntityAttribute trackedEntityAttribute);

        public abstract Builder allowFutureDate(Boolean allowFutureDate);

        public abstract Builder displayInList(Boolean displayInList);

        public abstract Builder program(Program program);

        public abstract Builder sortOrder(Integer sortOrder);

        public abstract Builder searchable(Boolean searchable);

        public abstract ProgramTrackedEntityAttribute build();
    }
}