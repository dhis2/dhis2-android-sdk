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

package org.hisp.dhis.android.core.program;

import android.database.Cursor;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.identifiable.internal.DataElementWithUidColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.identifiable.internal.ObjectWithUidColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreValueTypeRenderingAdapter;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.common.ValueTypeRendering;
import org.hisp.dhis.android.core.dataelement.DataElement;

@AutoValue
@JsonDeserialize(builder = $$AutoValue_ProgramStageDataElement.Builder.class)
public abstract class ProgramStageDataElement extends BaseIdentifiableObject implements CoreObject {

    @Nullable
    public abstract Boolean displayInReports();

    @Nullable
    public abstract Boolean compulsory();

    @Nullable
    public abstract Boolean allowProvidedElsewhere();

    @Nullable
    public abstract Integer sortOrder();

    @Nullable
    public abstract Boolean allowFutureDate();

    @Nullable
    @ColumnAdapter(DataElementWithUidColumnAdapter.class)
    public abstract DataElement dataElement();

    @Nullable
    @ColumnAdapter(ObjectWithUidColumnAdapter.class)
    public abstract ObjectWithUid programStage();

    @Nullable
    @ColumnAdapter(IgnoreValueTypeRenderingAdapter.class)
    public abstract ValueTypeRendering renderType();

    public static ProgramStageDataElement create(Cursor cursor) {
        return AutoValue_ProgramStageDataElement.createFromCursor(cursor);
    }

    public static Builder builder() {
        return new $$AutoValue_ProgramStageDataElement.Builder();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder extends BaseIdentifiableObject.Builder<Builder> {

        public abstract Builder id(Long id);

        public abstract Builder displayInReports(Boolean displayInReports);

        public abstract Builder compulsory(Boolean compulsory);

        public abstract Builder allowProvidedElsewhere(Boolean allowProvidedElsewhere);

        public abstract Builder sortOrder(Integer sortOrder);

        public abstract Builder allowFutureDate(Boolean allowFutureDate);

        public abstract Builder dataElement(DataElement dataElement);

        public abstract Builder programStage(ObjectWithUid programStage);

        public abstract Builder renderType(ValueTypeRendering renderType);

        public abstract ProgramStageDataElement build();
    }
}
