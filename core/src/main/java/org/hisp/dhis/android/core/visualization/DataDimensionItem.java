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

import android.database.Cursor;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.DataDimensionItemTypeColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.identifiable.internal.DataDimensionItemProgramAttributeWithUidColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.identifiable.internal.DataDimensionItemProgramDataElementWithUidColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.identifiable.internal.ObjectWithUidColumnAdapter;
import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.common.ObjectWithUidInterface;

@AutoValue
@JsonDeserialize(builder = $$AutoValue_DataDimensionItem.Builder.class)
public abstract class DataDimensionItem implements CoreObject {

    @Nullable
    public abstract String visualization();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DataDimensionItemTypeColumnAdapter.class)
    public abstract DataDimensionItemType dataDimensionItemType();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(ObjectWithUidColumnAdapter.class)
    public abstract ObjectWithUid indicator();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(ObjectWithUidColumnAdapter.class)
    public abstract ObjectWithUid dataElement();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(ObjectWithUidColumnAdapter.class)
    public abstract ObjectWithUid dataElementOperand();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(ObjectWithUidColumnAdapter.class)
    public abstract ObjectWithUid reportingRate();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(ObjectWithUidColumnAdapter.class)
    public abstract ObjectWithUid programIndicator();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DataDimensionItemProgramDataElementWithUidColumnAdapter.class)
    public abstract DataDimensionItemProgramDataElement programDataElement();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DataDimensionItemProgramAttributeWithUidColumnAdapter.class)
    public abstract DataDimensionItemProgramAttribute programAttribute();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(ObjectWithUidColumnAdapter.class)
    public abstract ObjectWithUid validationRule();

    @Nullable
    public String dataDimensionItem() {
        ObjectWithUidInterface item = dataDimensionItemObject();
        if (item == null) {
            return null;
        } else {
            return item.uid();
        }
    }

    @Nullable
    private ObjectWithUidInterface dataDimensionItemObject() {
        DataDimensionItemType type = dataDimensionItemType();
        if (type == null) {
            return null;
        } else {
            switch (type) {
                case INDICATOR:
                    return indicator();
                case DATA_ELEMENT:
                    return dataElement();
                case PROGRAM_ATTRIBUTE:
                    return programAttribute();
                case PROGRAM_DATA_ELEMENT:
                    return programDataElement();
                case PROGRAM_INDICATOR:
                    return programIndicator();
                case REPORTING_RATE:
                    return reportingRate();
                case DATA_ELEMENT_OPERAND:
                    return dataElementOperand();
                case VALIDATION_RULE:
                    return validationRule();
                default:
                    return null;
            }
        }
    }

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

        public abstract Builder visualization(String visualization);

        public abstract Builder dataDimensionItemType(DataDimensionItemType dataDimensionItemType);

        public abstract Builder indicator(ObjectWithUid indicator);

        public abstract Builder dataElement(ObjectWithUid dataElement);

        public abstract Builder dataElementOperand(ObjectWithUid dataElementOperand);

        public abstract Builder reportingRate(ObjectWithUid reportingRate);

        public abstract Builder programIndicator(ObjectWithUid programIndicator);

        public abstract Builder programDataElement(DataDimensionItemProgramDataElement programDataElement);

        public abstract Builder programAttribute(DataDimensionItemProgramAttribute programAttribute);

        public abstract Builder validationRule(ObjectWithUid validationRule);

        public abstract DataDimensionItem build();
    }
}