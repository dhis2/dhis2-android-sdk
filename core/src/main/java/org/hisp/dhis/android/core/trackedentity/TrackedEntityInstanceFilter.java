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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.EntityQueryCriteriaColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.identifiable.internal.ObjectWithUidColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreTrackedEntityInstanceEventFilterListColumnAdapter;
import org.hisp.dhis.android.core.common.BaseIdentifiableObject;
import org.hisp.dhis.android.core.common.CoreObject;
import org.hisp.dhis.android.core.common.DateFilterPeriod;
import org.hisp.dhis.android.core.common.FilterPeriod;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ObjectWithStyle;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus;

import java.util.List;

@AutoValue
@JsonDeserialize(builder = $$AutoValue_TrackedEntityInstanceFilter.Builder.class)
public abstract class TrackedEntityInstanceFilter extends BaseIdentifiableObject implements CoreObject,
        ObjectWithStyle<TrackedEntityInstanceFilter, TrackedEntityInstanceFilter.Builder> {

    @Nullable
    @JsonProperty()
    @ColumnAdapter(ObjectWithUidColumnAdapter.class)
    public abstract ObjectWithUid program();

    @Nullable
    @JsonProperty()
    public abstract String description();

    @Nullable
    @JsonProperty()
    public abstract Integer sortOrder();

    /**
     * @deprecated since 2.37. Use {@link #entityQueryCriteria().enrollmentStatus()} instead.
     */
    @Deprecated
    @Nullable
    public EnrollmentStatus enrollmentStatus() {
        return entityQueryCriteria().enrollmentStatus();
    }

    /**
     * @deprecated since 2.37. Use {@link #entityQueryCriteria().followUp()} instead.
     */
    @Deprecated
    @Nullable
    public Boolean followUp() {
        return entityQueryCriteria().followUp();
    }

    /**
     * @deprecated since 2.37. Use {@link #entityQueryCriteria().enrollmentCreatedDate()} instead.
     */
    @Deprecated
    @Nullable
    public FilterPeriod enrollmentCreatedPeriod() {
        DateFilterPeriod dateFilterPeriod = entityQueryCriteria().enrollmentCreatedDate();
        return dateFilterPeriod == null ? null :
                FilterPeriod.builder()
                        .periodFrom(dateFilterPeriod.startBuffer())
                        .periodTo(dateFilterPeriod.endBuffer())
                        .build();
    }

    @NonNull
    @JsonProperty()
    @ColumnAdapter(EntityQueryCriteriaColumnAdapter.class)
    public abstract EntityQueryCriteria entityQueryCriteria();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreTrackedEntityInstanceEventFilterListColumnAdapter.class)
    public abstract List<TrackedEntityInstanceEventFilter> eventFilters();

    public static Builder builder() {
        return new $$AutoValue_TrackedEntityInstanceFilter.Builder();
    }

    public static TrackedEntityInstanceFilter create(Cursor cursor) {
        return $AutoValue_TrackedEntityInstanceFilter.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public static abstract class Builder extends BaseIdentifiableObject.Builder<Builder>
            implements ObjectWithStyle.Builder<TrackedEntityInstanceFilter, Builder> {

        public abstract Builder id(Long id);

        public abstract Builder program(ObjectWithUid program);

        public abstract Builder description(String description);

        public abstract Builder sortOrder(Integer sortOrder);

        public abstract Builder entityQueryCriteria(EntityQueryCriteria entityQueryCriteria);

        public abstract Builder eventFilters(List<TrackedEntityInstanceEventFilter> eventFilters);

        abstract TrackedEntityInstanceFilter autoBuild();

        // Auxiliary fields
        abstract ObjectStyle style();
        abstract EntityQueryCriteria entityQueryCriteria();

        public TrackedEntityInstanceFilter build() {
            try {
                style();
            } catch (IllegalStateException e) {
                style(ObjectStyle.builder().build());
            }

            try {
                if (entityQueryCriteria() == null) {
                    entityQueryCriteria(EntityQueryCriteria.builder().build());
                }
            } catch (IllegalStateException e) {
                entityQueryCriteria(EntityQueryCriteria.builder().build());
            }

            return autoBuild();
        }
    }
}
