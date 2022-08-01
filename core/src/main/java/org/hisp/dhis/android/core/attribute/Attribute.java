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

package org.hisp.dhis.android.core.attribute;

import android.database.Cursor;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DbValueTypeColumnAdapter;
import org.hisp.dhis.android.core.common.BaseNameableObject;
import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.common.ValueType;

@AutoValue
@JsonDeserialize(builder = $$AutoValue_Attribute.Builder.class)
@SuppressWarnings({"PMD.ExcessivePublicCount"})
public abstract class Attribute extends BaseNameableObject implements CoreObject {

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbValueTypeColumnAdapter.class)
    public abstract ValueType valueType();

    @Nullable
    @JsonProperty()
    @ColumnName(AttributeTableInfo.Columns.UNIQUE)
    public abstract Boolean unique();

    @Nullable
    @JsonProperty()
    public abstract Boolean mandatory();

    @Nullable
    @JsonProperty()
    public abstract Boolean indicatorAttribute();

    @Nullable
    @JsonProperty()
    public abstract Boolean indicatorGroupAttribute();

    @Nullable
    @JsonProperty()
    public abstract Boolean userGroupAttribute();

    @Nullable
    @JsonProperty()
    public abstract Boolean dataElementAttribute();

    @Nullable
    @JsonProperty()
    public abstract Boolean constantAttribute();

    @Nullable
    @JsonProperty()
    public abstract Boolean categoryOptionAttribute();

    @Nullable
    @JsonProperty()
    public abstract Boolean optionSetAttribute();

    @Nullable
    @JsonProperty()
    public abstract Boolean sqlViewAttribute();

    @Nullable
    @JsonProperty()
    public abstract Boolean legendSetAttribute();

    @Nullable
    @JsonProperty()
    public abstract Boolean trackedEntityAttributeAttribute();

    @Nullable
    @JsonProperty()
    public abstract Boolean organisationUnitAttribute();

    @Nullable
    @JsonProperty()
    public abstract Boolean dataSetAttribute();

    @Nullable
    @JsonProperty()
    public abstract Boolean documentAttribute();

    @Nullable
    @JsonProperty()
    public abstract Boolean validationRuleGroupAttribute();

    @Nullable
    @JsonProperty()
    public abstract Boolean dataElementGroupAttribute();

    @Nullable
    @JsonProperty()
    public abstract Boolean sectionAttribute();

    @Nullable
    @JsonProperty()
    public abstract Boolean trackedEntityTypeAttribute();

    @Nullable
    @JsonProperty()
    public abstract Boolean userAttribute();

    @Nullable
    @JsonProperty()
    public abstract Boolean categoryOptionGroupAttribute();

    @Nullable
    @JsonProperty()
    public abstract Boolean programStageAttribute();

    @Nullable
    @JsonProperty()
    public abstract Boolean programAttribute();

    @Nullable
    @JsonProperty()
    public abstract Boolean categoryAttribute();

    @Nullable
    @JsonProperty()
    public abstract Boolean categoryOptionComboAttribute();

    @Nullable
    @JsonProperty()
    public abstract Boolean categoryOptionGroupSetAttribute();

    @Nullable
    @JsonProperty()
    public abstract Boolean validationRuleAttribute();

    @Nullable
    @JsonProperty()
    public abstract Boolean programIndicatorAttribute();

    @Nullable
    @JsonProperty()
    public abstract Boolean organisationUnitGroupAttribute();

    @Nullable
    @JsonProperty()
    public abstract Boolean dataElementGroupSetAttribute();

    @Nullable
    @JsonProperty()
    public abstract Boolean organisationUnitGroupSetAttribute();

    @Nullable
    @JsonProperty()
    public abstract Boolean optionAttribute();
    

    public static Builder builder() {
        return new $$AutoValue_Attribute.Builder();
    }

    public static Attribute create(Cursor cursor) {
        return AutoValue_Attribute.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder extends BaseNameableObject.Builder<Builder> {

        public abstract Builder id(Long id);

        public abstract Builder valueType(ValueType valueType);

        public abstract Builder mandatory(Boolean mandatory);

        public abstract Builder unique(Boolean unique);

        public abstract Builder indicatorAttribute(Boolean value);

        public abstract Builder indicatorGroupAttribute(Boolean value);

        public abstract Builder userGroupAttribute(Boolean value);

        public abstract Builder dataElementAttribute(Boolean value);

        public abstract Builder constantAttribute(Boolean value);

        public abstract Builder categoryOptionAttribute(Boolean value);

        public abstract Builder optionSetAttribute(Boolean value);

        public abstract Builder sqlViewAttribute(Boolean value);

        public abstract Builder legendSetAttribute(Boolean value);

        public abstract Builder trackedEntityAttributeAttribute(Boolean value);

        public abstract Builder organisationUnitAttribute(Boolean value);

        public abstract Builder dataSetAttribute(Boolean value);

        public abstract Builder documentAttribute(Boolean value);

        public abstract Builder validationRuleGroupAttribute(Boolean value);

        public abstract Builder dataElementGroupAttribute(Boolean value);

        public abstract Builder sectionAttribute(Boolean value);

        public abstract Builder trackedEntityTypeAttribute(Boolean value);

        public abstract Builder userAttribute(Boolean value);

        public abstract Builder categoryOptionGroupAttribute(Boolean value);

        public abstract Builder programStageAttribute(Boolean value);

        public abstract Builder programAttribute(Boolean value);

        public abstract Builder categoryAttribute(Boolean value);

        public abstract Builder categoryOptionComboAttribute(Boolean value);

        public abstract Builder categoryOptionGroupSetAttribute(Boolean value);

        public abstract Builder validationRuleAttribute(Boolean value);

        public abstract Builder programIndicatorAttribute(Boolean value);

        public abstract Builder organisationUnitGroupAttribute(Boolean value);

        public abstract Builder dataElementGroupSetAttribute(Boolean value);

        public abstract Builder organisationUnitGroupSetAttribute(Boolean value);

        public abstract Builder optionAttribute(Boolean value);

        public abstract Attribute build();
    }
}
