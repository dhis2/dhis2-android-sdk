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

package org.hisp.dhis.android.core.dataelement;

import android.database.Cursor;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DbValueTypeColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.identifiable.internal.ObjectWithUidColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreAttributeValuesListAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreObjectWithUidListColumnAdapter;
import org.hisp.dhis.android.core.attribute.AttributeValue;
import org.hisp.dhis.android.core.category.CategoryCombo;
import org.hisp.dhis.android.core.common.BaseNameableObject;
import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ObjectWithStyle;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.common.ValueType;

import java.util.List;

@AutoValue
@JsonDeserialize(builder = $$AutoValue_DataElement.Builder.class)
public abstract class DataElement extends BaseNameableObject
        implements CoreObject, ObjectWithStyle<DataElement, DataElement.Builder> {

    @Nullable
    @ColumnAdapter(DbValueTypeColumnAdapter.class)
    public abstract ValueType valueType();

    @Nullable
    public abstract Boolean zeroIsSignificant();

    @Nullable
    public abstract String aggregationType();

    @Nullable
    public abstract String formName();

    @Nullable
    public abstract String domainType();

    @Nullable
    public abstract String displayFormName();

    @Nullable
    @ColumnAdapter(ObjectWithUidColumnAdapter.class)
    public abstract ObjectWithUid optionSet();

    public String optionSetUid() {
        ObjectWithUid optionSet = optionSet();
        return optionSet == null ? null : optionSet.uid();
    }

    @Nullable
    @ColumnAdapter(ObjectWithUidColumnAdapter.class)
    public abstract ObjectWithUid categoryCombo();

    public String categoryComboUid() {
        ObjectWithUid combo = categoryCombo();
        return combo == null ? CategoryCombo.DEFAULT_UID : combo.uid();
    }

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreObjectWithUidListColumnAdapter.class)
    public abstract List<ObjectWithUid> legendSets();

    @Nullable
    public abstract String fieldMask();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreAttributeValuesListAdapter.class)
    public abstract List<AttributeValue> attributeValues();

    public static Builder builder() {
        return new $$AutoValue_DataElement.Builder();
    }

    public static DataElement create(Cursor cursor) {
        return $AutoValue_DataElement.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder extends BaseNameableObject.Builder<DataElement.Builder>
            implements ObjectWithStyle.Builder<DataElement, DataElement.Builder> {

        public abstract DataElement.Builder id(Long id);

        public abstract DataElement.Builder valueType(ValueType valueType);

        public abstract DataElement.Builder zeroIsSignificant(Boolean zeroIsSignificant);

        public abstract DataElement.Builder aggregationType(String aggregationType);

        public abstract DataElement.Builder formName(String formName);

        public abstract DataElement.Builder domainType(String domainType);

        public abstract DataElement.Builder displayFormName(String displayFormName);

        public abstract DataElement.Builder optionSet(ObjectWithUid optionSet);

        public abstract DataElement.Builder categoryCombo(ObjectWithUid categoryCombo);

        public abstract DataElement.Builder legendSets(List<ObjectWithUid> legendSets);

        public abstract DataElement.Builder fieldMask(String fieldMask);

        public abstract Builder attributeValues(List<AttributeValue> attributeValues);

        abstract DataElement autoBuild();

        // Auxiliary fields
        abstract ObjectStyle style();

        public DataElement build() {
            try {
                style();
            } catch (IllegalStateException e) {
                style(ObjectStyle.builder().build());
            }

            return autoBuild();
        }
    }
}
