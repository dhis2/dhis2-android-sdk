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

package org.hisp.dhis.android.core.visualization;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.apache.commons.lang3.ArrayUtils;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreObjectWithUidColumnAdapter;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.common.ObjectWithUidInterface;
import org.hisp.dhis.android.core.visualization.internal.DataDimensionItemProgramDataElementFields;

@AutoValue
@JsonDeserialize(builder = AutoValue_DataDimensionItemProgramDataElement.Builder.class)
public abstract class DataDimensionItemProgramDataElement implements ObjectWithUidInterface {

    @Nullable
    @JsonProperty(DataDimensionItemProgramDataElementFields.DIMENSION_ITEM)
    public abstract String uid();

    @Nullable
    @JsonIgnore()
    @ColumnAdapter(IgnoreObjectWithUidColumnAdapter.class)
    public ObjectWithUid program() {
        return uid() == null ? null : getTokenAt(0);
    }

    @Nullable
    @JsonIgnore()
    @ColumnAdapter(IgnoreObjectWithUidColumnAdapter.class)
    public ObjectWithUid dataElement() {
        return uid() == null ? null : getTokenAt(1);
    }

    public static Builder builder() {
        return new AutoValue_DataDimensionItemProgramDataElement.Builder();
    }

    public abstract Builder toBuilder();

    private ObjectWithUid getTokenAt(int position) {
        String[] tokens = uid() == null ? ArrayUtils.EMPTY_STRING_ARRAY : uid().split("\\.");
        String uid = tokens.length > position ? tokens[position] : null;
        return uid == null ? null : ObjectWithUid.create(uid);
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder {

        @JsonProperty(DataDimensionItemProgramDataElementFields.DIMENSION_ITEM)
        public abstract Builder uid(String uid);

        public abstract DataDimensionItemProgramDataElement build();
    }
}