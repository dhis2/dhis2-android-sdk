/*
 * Copyright (c) 2004-2019, University of Oslo
 * All rights reserved.
 *
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

package org.hisp.dhis.android.core.trackedentity;

import android.database.Cursor;
import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.gabrielittner.auto.value.cursor.ColumnAdapter;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.BaseDataModel;
import org.hisp.dhis.android.core.common.ObjectWithDeleteInterface;
import org.hisp.dhis.android.core.common.ObjectWithUidInterface;
import org.hisp.dhis.android.core.data.database.DataDeleteColumnAdapter;
import org.hisp.dhis.android.core.data.database.DbDateColumnAdapter;
import org.hisp.dhis.android.core.data.database.DbFeatureTypeColumnAdapter;
import org.hisp.dhis.android.core.data.database.IgnoreEnrollmentListColumnAdapter;
import org.hisp.dhis.android.core.data.database.IgnoreRelationship229CompatibleListColumnAdapter;
import org.hisp.dhis.android.core.data.database.IgnoreTrackedEntityAttributeValueListColumnAdapter;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.period.FeatureType;
import org.hisp.dhis.android.core.relationship.Relationship229Compatible;

import java.util.Date;
import java.util.List;

@AutoValue
@JsonDeserialize(builder = AutoValue_TrackedEntityInstance.Builder.class)
public abstract class TrackedEntityInstance extends BaseDataModel
        implements ObjectWithUidInterface, ObjectWithDeleteInterface {

    @Override
    @Nullable
    @JsonProperty(TrackedEntityInstanceFields.UID)
    public abstract String uid();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date created();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbDateColumnAdapter.class)
    public abstract Date lastUpdated();

    @Nullable
    @JsonIgnore()
    public abstract String createdAtClient();

    @Nullable
    @JsonIgnore()
    public abstract String lastUpdatedAtClient();

    @Nullable
    @JsonProperty(TrackedEntityInstanceFields.ORGANISATION_UNIT)
    public abstract String organisationUnit();

    @Nullable
    @JsonProperty()
    public abstract String trackedEntityType();

    @Nullable
    @JsonProperty()
    public abstract String coordinates();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DbFeatureTypeColumnAdapter.class)
    public abstract FeatureType featureType();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(DataDeleteColumnAdapter.class)
    public abstract Boolean deleted();

    @Nullable
    @JsonProperty(TrackedEntityInstanceFields.TRACKED_ENTITY_ATTRIBUTE_VALUES)
    @ColumnAdapter(IgnoreTrackedEntityAttributeValueListColumnAdapter.class)
    public abstract List<TrackedEntityAttributeValue> trackedEntityAttributeValues();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreRelationship229CompatibleListColumnAdapter.class)
    public abstract List<Relationship229Compatible> relationships();

    @Nullable
    @JsonProperty()
    @ColumnAdapter(IgnoreEnrollmentListColumnAdapter.class)
    public abstract List<Enrollment> enrollments();

    public static Builder builder() {
        return new $$AutoValue_TrackedEntityInstance.Builder();
    }

    public static TrackedEntityInstance create(Cursor cursor) {
        return $AutoValue_TrackedEntityInstance.createFromCursor(cursor);
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    @JsonPOJOBuilder(withPrefix = "")
    public abstract static class Builder extends BaseDataModel.Builder<Builder> {
        public abstract Builder id(Long id);

        @JsonProperty(TrackedEntityInstanceFields.UID)
        public abstract Builder uid(String uid);

        public abstract Builder created(Date created);

        public abstract Builder lastUpdated(Date lastUpdated);

        public abstract Builder createdAtClient(String createdAtClient);

        public abstract Builder lastUpdatedAtClient(String lastUpdatedAtClient);

        @JsonProperty(TrackedEntityInstanceFields.ORGANISATION_UNIT)
        public abstract Builder organisationUnit(String organisationUnit);

        public abstract Builder trackedEntityType(String trackedEntityType);

        public abstract Builder coordinates(String coordinates);

        public abstract Builder featureType(FeatureType featureType);

        public abstract Builder deleted(Boolean deleted);

        @JsonProperty(TrackedEntityInstanceFields.TRACKED_ENTITY_ATTRIBUTE_VALUES)
        public abstract Builder trackedEntityAttributeValues(
                List<TrackedEntityAttributeValue> trackedEntityAttributeValues);

        public abstract Builder relationships(List<Relationship229Compatible> relationships);

        public abstract Builder enrollments(List<Enrollment> enrollments);

        public abstract TrackedEntityInstance build();
    }
}