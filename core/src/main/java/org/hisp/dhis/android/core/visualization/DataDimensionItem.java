/*
 *  Copyright (c) 2004-2021, University of Oslo
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

import android.database.Cursor;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.common.ObjectWithUid;

@AutoValue
@JsonDeserialize(builder = $$AutoValue_DataDimensionItem.Builder.class)
public abstract class DataDimensionItem implements CoreObject {

    @Nullable
    @JsonProperty()
    public abstract DataDimensionItemType dataDimensionItemType();

    @Nullable
    @JsonProperty()
    public abstract ObjectWithUid indicator();

    @Nullable
    @JsonProperty()
    public abstract ObjectWithUid dataElement();

    @Nullable
    @JsonProperty()
    public abstract ObjectWithUid dataElementOperand();

    @Nullable
    @JsonProperty()
    public abstract ObjectWithUid reportingRate();

    @Nullable
    @JsonProperty()
    public abstract ObjectWithUid programIndicator();

    @Nullable
    @JsonProperty()
    public abstract ObjectWithUid programDataElement();

    @Nullable
    @JsonProperty()
    public abstract ObjectWithUid programAttribute();
    

    public static Builder builder() {
        return new $$AutoValue_DataDimensionItem.Builder();
    }

    public static DataDimensionItem create(Cursor cursor) {
        return AutoValue_DataDimensionItem.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder {

        public abstract Builder id(Long id);

        public abstract Builder dataDimensionItemType(DataDimensionItemType dataDimensionItemType);

        public abstract Builder indicator(ObjectWithUid indicator);

        public abstract Builder dataElement(ObjectWithUid dataElement);

        public abstract Builder dataElementOperand(ObjectWithUid dataElementOperand);

        public abstract Builder reportingRate(ObjectWithUid reportingRate);

        public abstract Builder programIndicator(ObjectWithUid programIndicator);

        public abstract Builder programDataElement(ObjectWithUid programDataElement);

        public abstract Builder programAttribute(ObjectWithUid programAttribute);

        public abstract DataDimensionItem build();
    }
}