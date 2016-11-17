/*
 * Copyright (c) 2016, University of Oslo
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

package org.hisp.dhis.client.sdk.models.trackedentity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.client.sdk.models.common.BaseDataModel;
import org.hisp.dhis.client.sdk.models.relationship.Relationship;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

// TODO: Tests
@AutoValue
@JsonDeserialize(builder = AutoValue_TrackedEntityInstance.Builder.class)
public abstract class TrackedEntityInstance extends BaseDataModel {
    private static final String JSON_PROPERTY_TRACKED_ENTITY_INSTANCE_UID = "trackedEntityInstance";
    private static final String JSON_PROPERTY_CREATED = "created";
    private static final String JSON_PROPERTY_LAST_UPDATED = "lastUpdated";
    private static final String JSON_PROPERTY_ORGANISATION_UNIT = "orgUnit";
    private static final String JSON_PROPERTY_TRACKED_ENTITY_DATA_VALUES = "trackedEntityDataValues";
    private static final String JSON_PROPERTY_RELATIONSHIPS = "relationships";

    @Nullable
    @JsonProperty(JSON_PROPERTY_TRACKED_ENTITY_INSTANCE_UID)
    public abstract String uid();

    @Nullable
    @JsonProperty(JSON_PROPERTY_CREATED)
    public abstract Date created();

    @Nullable
    @JsonProperty(JSON_PROPERTY_LAST_UPDATED)
    public abstract Date lastUpdated();

    @Nullable
    @JsonProperty(JSON_PROPERTY_ORGANISATION_UNIT)
    public abstract String organisationUnit();

    @Nullable
    @JsonProperty(JSON_PROPERTY_TRACKED_ENTITY_DATA_VALUES)
    public abstract List<TrackedEntityAttributeValue> trackedEntityAttributeValues();

    @Nullable
    @JsonProperty(JSON_PROPERTY_RELATIONSHIPS)
    public abstract List<Relationship> relationships();

    public abstract Builder toBuilder();

    @Override
    public boolean isValid() {
        if (uid() == null || created() == null) {
            return false;
        }

        return true;
    }

    public static Builder builder() {
        return new AutoValue_TrackedEntityInstance.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder extends BaseDataModel.Builder<Builder> {
        @JsonProperty(JSON_PROPERTY_TRACKED_ENTITY_INSTANCE_UID)
        public abstract Builder uid(@Nullable String uid);

        @JsonProperty(JSON_PROPERTY_CREATED)
        public abstract Builder created(@Nullable Date created);

        @JsonProperty(JSON_PROPERTY_LAST_UPDATED)
        public abstract Builder lastUpdated(@Nullable Date lastUpdated);

        @JsonProperty(JSON_PROPERTY_ORGANISATION_UNIT)
        public abstract Builder organisationUnit(@Nullable String organisationUnit);

        @JsonProperty(JSON_PROPERTY_TRACKED_ENTITY_DATA_VALUES)
        public abstract Builder trackedEntityAttributeValues(@Nullable List<TrackedEntityAttributeValue> trackedEntityAttributeValues);

        @JsonProperty(JSON_PROPERTY_RELATIONSHIPS)
        public abstract Builder relationships(@Nullable List<Relationship> relationships);

        abstract List<TrackedEntityAttributeValue> trackedEntityAttributeValues();

        abstract TrackedEntityInstance autoBuild();

        public TrackedEntityInstance build() {
            if (trackedEntityAttributeValues() != null) {
                trackedEntityAttributeValues(Collections.unmodifiableList(
                        trackedEntityAttributeValues()));
            }

            return autoBuild();
        }
    }
}
