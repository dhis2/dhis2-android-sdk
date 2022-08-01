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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.AggregationTypeColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.AnalyticsTypeColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.identifiable.internal.ObjectWithUidColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreAnalyticsPeriodBoundaryListColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreObjectWithUidListColumnAdapter;
import org.hisp.dhis.android.core.common.AggregationType;
import org.hisp.dhis.android.core.common.AnalyticsType;
import org.hisp.dhis.android.core.common.BaseNameableObject;
import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.common.ObjectWithUid;

import java.util.List;

@AutoValue
@JsonDeserialize(builder = $$AutoValue_ProgramIndicator.Builder.class)
public abstract class ProgramIndicator extends BaseNameableObject implements CoreObject {

    @Nullable
    @JsonProperty()
    public abstract Boolean displayInForm();

    @Nullable
    @JsonProperty()
    public abstract String expression();

    @Nullable
    @JsonProperty()
    public abstract String dimensionItem();

    @Nullable
    @JsonProperty()
    public abstract String filter();

    @Nullable
    @JsonProperty()
    public abstract Integer decimals();
    @Nullable
    @JsonProperty()
    @ColumnAdapter(AggregationTypeColumnAdapter.class)
    public abstract AggregationType aggregationType();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(ObjectWithUidColumnAdapter.class)
    public abstract ObjectWithUid program();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(AnalyticsTypeColumnAdapter.class)
    public abstract AnalyticsType analyticsType();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreAnalyticsPeriodBoundaryListColumnAdapter.class)
    public abstract List<AnalyticsPeriodBoundary> analyticsPeriodBoundaries();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreObjectWithUidListColumnAdapter.class)
    public abstract List<ObjectWithUid> legendSets();

    public static ProgramIndicator create(Cursor cursor) {
        return $AutoValue_ProgramIndicator.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new $$AutoValue_ProgramIndicator.Builder()
                .aggregationType(AggregationType.NONE);
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder  extends BaseNameableObject.Builder<Builder> {
        public abstract Builder id(Long id);

        public abstract Builder displayInForm(Boolean displayInForm);

        public abstract Builder expression(String expression);

        public abstract Builder dimensionItem(String dimensionItem);

        public abstract Builder filter(String filter);

        public abstract Builder decimals(Integer decimals);

        public abstract Builder aggregationType(AggregationType aggregationType);

        public abstract Builder program(ObjectWithUid program);

        public abstract Builder analyticsType(AnalyticsType analyticsType);

        public abstract Builder analyticsPeriodBoundaries(List<AnalyticsPeriodBoundary> analyticsPeriodBoundaries);

        public abstract Builder legendSets(List<ObjectWithUid> legendSets);

        public abstract ProgramIndicator build();
    }
}