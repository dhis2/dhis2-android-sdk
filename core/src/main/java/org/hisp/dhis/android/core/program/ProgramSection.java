/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.Model;
import org.hisp.dhis.android.core.common.ObjectWithStyle;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.data.database.IgnoreObjectWithUidListColumnAdapter;
import org.hisp.dhis.android.core.data.database.ObjectWithUidColumnAdapter;

import java.util.List;

@AutoValue
@JsonDeserialize(builder = AutoValue_ProgramSection.Builder.class)
public abstract class ProgramSection extends BaseIdentifiableObject
        implements Model, ObjectWithStyle<ProgramSection, ProgramSection.Builder> {

    @Nullable
    @JsonProperty()
    public abstract String description();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(ObjectWithUidColumnAdapter.class)
    public abstract ObjectWithUid program();

    @Nullable
    @JsonProperty(ProgramSectionFields.ATTRIBUTES)
    @ColumnAdapter(IgnoreObjectWithUidListColumnAdapter.class)
    public abstract List<ObjectWithUid> attributes();

    @Nullable
    @JsonProperty()
    public abstract Integer sortOrder();

    @Nullable
    @JsonProperty()
    public abstract String formName();

    public static Builder builder() {
        return new AutoValue_ProgramSection.Builder();
    }

    static ProgramSection create(Cursor cursor) {
        return $AutoValue_ProgramSection.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder extends BaseIdentifiableObject.Builder<Builder>
            implements ObjectWithStyle.Builder<ProgramSection, Builder> {

        public abstract Builder id(Long id);

        public abstract Builder description(String description);

        public abstract Builder program(ObjectWithUid program);

        @JsonProperty(ProgramSectionFields.ATTRIBUTES)
        public abstract Builder attributes(List<ObjectWithUid> attributes);

        public abstract Builder sortOrder(Integer sortOrder);

        public abstract Builder formName(String formName);

        public abstract ProgramSection build();
    }
}