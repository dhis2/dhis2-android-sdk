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
import com.gabrielittner.auto.value.cursor.ColumnName;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DbDateColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.custom.internal.DbGeometryColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.enums.internal.StateColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreNewTrackerImporterEnrollmentListColumnAdapter;
import org.hisp.dhis.android.core.arch.db.adapters.ignore.internal.IgnoreNewTrackerImporterTrackedEntityAttributeValueListColumnAdapter;
import org.hisp.dhis.android.core.common.BaseDeletableDataObject;
import org.hisp.dhis.android.core.common.DataColumns;
import org.hisp.dhis.android.core.common.Geometry;
import org.hisp.dhis.android.core.common.ObjectWithUidInterface;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.NewTrackerImporterEnrollment;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceFields;

import java.util.Date;
import java.util.List;

@AutoValue
@JsonDeserialize(builder = AutoValue_NewTrackerImporterTrackedEntity.Builder.class)
public abstract class NewTrackerImporterTrackedEntity extends BaseDeletableDataObject
        implements ObjectWithUidInterface {

    @Override
    @JsonProperty("trackedEntity")
    public abstract String uid();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date createdAt();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date updatedAt();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date createdAtClient();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date updatedAtClient();

    @Nullable
    @JsonProperty(TrackedEntityInstanceFields.ORGANISATION_UNIT)
    public abstract String organisationUnit();

    @Nullable
    @JsonProperty()
    public abstract String trackedEntityType();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbGeometryColumnAdapter.class)
    public abstract Geometry geometry();

    @Nullable
    @ColumnName(DataColumns.AGGREGATED_SYNC_STATE)
    @ColumnAdapter(StateColumnAdapter.class)
    public abstract State aggregatedSyncState();

    @Nullable
    @JsonProperty(TrackedEntityInstanceFields.TRACKED_ENTITY_ATTRIBUTE_VALUES)
    @ColumnAdapter(IgnoreNewTrackerImporterTrackedEntityAttributeValueListColumnAdapter.class)
    public abstract List<NewTrackerImporterTrackedEntityAttributeValue> trackedEntityAttributeValues();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreNewTrackerImporterEnrollmentListColumnAdapter.class)
    public abstract List<NewTrackerImporterEnrollment> enrollments();

    public static Builder builder() {
        return new $$AutoValue_NewTrackerImporterTrackedEntity.Builder();
    }

    public static NewTrackerImporterTrackedEntity create(Cursor cursor) {
        return $AutoValue_NewTrackerImporterTrackedEntity.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder extends BaseDeletableDataObject.Builder<Builder> {
        public abstract Builder id(Long id);

        @JsonProperty("trackedEntity")
        public abstract Builder uid(String uid);

        public abstract Builder createdAt(Date created);

        public abstract Builder updatedAt(Date updatedAt);

        public abstract Builder createdAtClient(Date createdAtClient);

        public abstract Builder updatedAtClient(Date updatedAtClient);

        @JsonProperty(TrackedEntityInstanceFields.ORGANISATION_UNIT)
        public abstract Builder organisationUnit(String organisationUnit);

        public abstract Builder trackedEntityType(String trackedEntityType);

        public abstract Builder geometry(Geometry geometry);

        public abstract Builder aggregatedSyncState(State state);

        @JsonProperty(TrackedEntityInstanceFields.TRACKED_ENTITY_ATTRIBUTE_VALUES)
        public abstract Builder trackedEntityAttributeValues(
                List<NewTrackerImporterTrackedEntityAttributeValue> trackedEntityAttributeValues);

        public abstract Builder enrollments(List<NewTrackerImporterEnrollment> enrollments);

        public abstract NewTrackerImporterTrackedEntity build();
    }
}