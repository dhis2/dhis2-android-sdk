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

package org.hisp.dhis.android.core.dataelement;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.category.CategoryComboModel;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.BaseModel;
import org.hisp.dhis.android.core.common.BaseNameableObject;
import org.hisp.dhis.android.core.common.Model;
import org.hisp.dhis.android.core.common.ObjectWithStyle;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.common.ValueType;
import org.hisp.dhis.android.core.common.ValueTypeRendering;
import org.hisp.dhis.android.core.data.database.DbValueTypeColumnAdapter;
import org.hisp.dhis.android.core.data.database.IgnoreAccessAdapter;
import org.hisp.dhis.android.core.data.database.IgnoreValueTypeRenderingAdapter;
import org.hisp.dhis.android.core.data.database.ObjectWithUidColumnAdapter;

@AutoValue
@JsonDeserialize(builder = AutoValue_DataElement.Builder.class)
public abstract class DataElement extends BaseNameableObject
        implements Model, ObjectWithStyle<DataElement, DataElement.Builder> {

    // TODO move to base class after whole object refactor
    @Override
    @Nullable
    @ColumnName(BaseModel.Columns.ID)
    @JsonIgnore()
    public abstract Long id();

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
    public abstract String numberType();

    @Nullable
    public abstract String domainType();

    @Nullable
    public abstract String dimension();

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

    String categoryComboUid() {
        ObjectWithUid combo = categoryCombo();
        return combo == null ? CategoryComboModel.DEFAULT_UID : combo.uid();
    }

    @Nullable
    @ColumnAdapter(IgnoreValueTypeRenderingAdapter.class)
    public abstract ValueTypeRendering renderType();

    @Nullable
    @ColumnAdapter(IgnoreAccessAdapter.class)
    public abstract Access access();

    public static Builder builder() {
        return new $$AutoValue_DataElement.Builder();
    }

    static DataElement create(Cursor cursor) {
        return $AutoValue_DataElement.createFromCursor(cursor);
    }

    public abstract ContentValues toContentValues();

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

        public abstract DataElement.Builder numberType(String numberType);

        public abstract DataElement.Builder domainType(String domainType);

        public abstract DataElement.Builder dimension(String dimension);

        public abstract DataElement.Builder displayFormName(String displayFormName);

        public abstract DataElement.Builder optionSet(ObjectWithUid optionSet);

        public abstract DataElement.Builder categoryCombo(ObjectWithUid categoryCombo);

        public abstract DataElement.Builder renderType(ValueTypeRendering renderType);

        public abstract DataElement.Builder access(Access access);

        public abstract DataElement build();
    }
}
