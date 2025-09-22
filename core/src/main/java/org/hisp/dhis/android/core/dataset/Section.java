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

package org.hisp.dhis.android.core.dataset;

import androidx.annotation.Nullable;

import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.dataelement.DataElementOperand;
import org.hisp.dhis.android.core.indicator.Indicator;

import java.util.List;

@AutoValue
public abstract class Section extends BaseIdentifiableObject implements CoreObject {

    @Nullable
    public abstract String description();

    @Nullable
    public abstract Integer sortOrder();

    @Nullable
    public abstract Boolean showRowTotals();

    @Nullable
    public abstract Boolean showColumnTotals();

    @Nullable
    public abstract ObjectWithUid dataSet();

    @Nullable
    public abstract List<DataElement> dataElements();

    @Nullable
    public abstract List<DataElementOperand> greyedFields();

    @Nullable
    public abstract List<Indicator> indicators();

    @Nullable
    public abstract Boolean disableDataElementAutoGroup();

    @Nullable
    public abstract SectionDisplayOptions displayOptions();

    public static Builder builder() {
        return new AutoValue_Section.Builder();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public abstract static class Builder extends BaseIdentifiableObject.Builder<Builder> {
        public abstract Builder description(String description);

        public abstract Builder sortOrder(Integer sortOrder);

        public abstract Builder showRowTotals(Boolean showRowTotals);

        public abstract Builder showColumnTotals(Boolean showColumnTotals);

        public abstract Builder dataSet(ObjectWithUid dataSet);

        public abstract Builder dataElements(List<DataElement> dataElements);

        public abstract Builder greyedFields(List<DataElementOperand> greyedFields);

        public abstract Builder indicators(List<Indicator> indicators);

        public abstract Builder disableDataElementAutoGroup(Boolean disableDataElementAutoGroup);

        public abstract Builder displayOptions(SectionDisplayOptions sectionDisplayOptions);

        public abstract Section build();
    }
}
