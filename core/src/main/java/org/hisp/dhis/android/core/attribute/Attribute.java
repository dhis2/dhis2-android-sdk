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

package org.hisp.dhis.android.core.attribute;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseNameableObject;
import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.common.ValueType;

@AutoValue
@SuppressWarnings({"PMD.ExcessivePublicCount"})
public abstract class Attribute extends BaseNameableObject implements CoreObject {

    @Nullable
    public abstract ValueType valueType();

    @Nullable
    public abstract Boolean unique();

    @Nullable
    public abstract Boolean mandatory();

    @Nullable
    public abstract Boolean indicatorAttribute();

    @Nullable
    public abstract Boolean indicatorGroupAttribute();

    @Nullable
    public abstract Boolean userGroupAttribute();

    @Nullable
    public abstract Boolean dataElementAttribute();

    @Nullable
    public abstract Boolean constantAttribute();

    @Nullable
    public abstract Boolean categoryOptionAttribute();

    @Nullable
    public abstract Boolean optionSetAttribute();

    @Nullable
    public abstract Boolean sqlViewAttribute();

    @Nullable
    public abstract Boolean legendSetAttribute();

    @Nullable
    public abstract Boolean trackedEntityAttributeAttribute();

    @Nullable
    public abstract Boolean organisationUnitAttribute();

    @Nullable
    public abstract Boolean dataSetAttribute();

    @Nullable
    public abstract Boolean documentAttribute();

    @Nullable
    public abstract Boolean validationRuleGroupAttribute();

    @Nullable
    public abstract Boolean dataElementGroupAttribute();

    @Nullable
    public abstract Boolean sectionAttribute();

    @Nullable
    public abstract Boolean trackedEntityTypeAttribute();

    @Nullable
    public abstract Boolean userAttribute();

    @Nullable
    public abstract Boolean categoryOptionGroupAttribute();

    @Nullable
    public abstract Boolean programStageAttribute();

    @Nullable
    public abstract Boolean programAttribute();

    @Nullable
    public abstract Boolean categoryAttribute();

    @Nullable
    public abstract Boolean categoryOptionComboAttribute();

    @Nullable
    public abstract Boolean categoryOptionGroupSetAttribute();

    @Nullable
    public abstract Boolean validationRuleAttribute();

    @Nullable
    public abstract Boolean programIndicatorAttribute();

    @Nullable
    public abstract Boolean organisationUnitGroupAttribute();

    @Nullable
    public abstract Boolean dataElementGroupSetAttribute();

    @Nullable
    public abstract Boolean organisationUnitGroupSetAttribute();

    @Nullable
    public abstract Boolean optionAttribute();


    public static Builder builder() {
        return new AutoValue_Attribute.Builder();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder extends BaseNameableObject.Builder<Builder> {
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
