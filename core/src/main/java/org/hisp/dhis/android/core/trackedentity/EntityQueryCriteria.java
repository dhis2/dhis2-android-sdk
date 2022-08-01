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

package org.hisp.dhis.android.core.trackedentity;

import android.database.Cursor;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DateFilterPeriodColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.StringListColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.EnrollmentStatusColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreAttributeValueFilterListColumnAdapter;
import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.common.DateFilterPeriod;
import org.hisp.dhis.android.core.common.FilterQueryCriteria;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;

import java.util.List;

@AutoValue
@JsonDeserialize(builder = $$AutoValue_EntityQueryCriteria.Builder.class)
public abstract class EntityQueryCriteria extends FilterQueryCriteria implements CoreObject {

    @Nullable
    @JsonProperty()
    public abstract String programStage();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(StringListColumnAdapter.class)
    public abstract List<String> trackedEntityInstances();

    @Nullable
    @JsonProperty()
    public abstract String trackedEntityType();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(EnrollmentStatusColumnAdapter.class)
    public abstract EnrollmentStatus enrollmentStatus();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DateFilterPeriodColumnAdapter.class)
    public abstract DateFilterPeriod enrollmentIncidentDate();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DateFilterPeriodColumnAdapter.class)
    public abstract DateFilterPeriod enrollmentCreatedDate();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreAttributeValueFilterListColumnAdapter.class)
    public abstract List<AttributeValueFilter> attributeValueFilters();

    public static Builder builder() {
        return new $$AutoValue_EntityQueryCriteria.Builder();
    }

    public abstract Builder toBuilder();

    public static EntityQueryCriteria create(Cursor cursor) {
        return $AutoValue_EntityQueryCriteria.createFromCursor(cursor);
    }

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder extends FilterQueryCriteria.Builder<Builder> {
        public abstract Builder id(Long id);

        public abstract Builder programStage(String programStage);

        public abstract Builder trackedEntityInstances(List<String> trackedEntityInstances);

        public abstract Builder trackedEntityType(String trackedEntityType);

        public abstract Builder enrollmentStatus(EnrollmentStatus enrollmentStatus);

        public abstract Builder enrollmentIncidentDate(DateFilterPeriod enrollmentIncidentDate);

        public abstract Builder enrollmentCreatedDate(DateFilterPeriod enrollmentCreatedDate);

        public abstract Builder attributeValueFilters(List<AttributeValueFilter> attributeValueFilters);

        public abstract EntityQueryCriteria build();
    }
}