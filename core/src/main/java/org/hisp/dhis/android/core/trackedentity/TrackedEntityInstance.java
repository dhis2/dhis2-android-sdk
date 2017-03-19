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

package org.hisp.dhis.android.core.trackedentity;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.NestedField;
import org.hisp.dhis.android.core.relationship.Relationship;

import java.util.Date;
import java.util.List;

import static org.hisp.dhis.android.core.utils.Utils.safeUnmodifiableList;

@AutoValue
public abstract class TrackedEntityInstance {
    private static final String UID = "trackedEntityInstance";
    private static final String CREATED = "created";
    private static final String LAST_UPDATED = "lastUpdated";
    private static final String ORGANISATION_UNIT = "orgUnit";
    private static final String TRACKED_ENTITY_ATTRIBUTES = "attributes";
    private static final String RELATIONSHIPS = "relationships";
    private static final String TRACKED_ENTITY = "trackedEntity";

    public static final Field<TrackedEntityInstance, String> uid = Field.create(UID);
    public static final Field<TrackedEntityInstance, Date> created = Field.create(CREATED);
    public static final Field<TrackedEntityInstance, Date> lastUpdated = Field.create(LAST_UPDATED);
    public static final Field<TrackedEntityInstance, String> organisationUnit = Field.create(ORGANISATION_UNIT);

    public static final NestedField<TrackedEntityInstance, TrackedEntityAttribute> trackedEntityAttributes
            = NestedField.create(TRACKED_ENTITY_ATTRIBUTES);
    public static final NestedField<TrackedEntityInstance, Relationship> relationships
            = NestedField.create(RELATIONSHIPS);

    @JsonProperty(UID)
    public abstract String uid();

    @Nullable
    @JsonProperty(CREATED)
    public abstract Date created();

    @Nullable
    @JsonProperty(LAST_UPDATED)
    public abstract Date lastUpdated();

    @Nullable
    @JsonProperty(ORGANISATION_UNIT)
    public abstract String organisationUnit();

    @Nullable
    @JsonProperty(TRACKED_ENTITY)
    public abstract String trackedEntity();

    @Nullable
    @JsonProperty(TRACKED_ENTITY_ATTRIBUTES)
    public abstract List<TrackedEntityAttributeValue> trackedEntityAttributeValues();

    @Nullable
    @JsonProperty(RELATIONSHIPS)
    public abstract List<Relationship> relationships();

    @JsonCreator
    public static TrackedEntityInstance create(
            @JsonProperty(UID) String uid,
            @JsonProperty(CREATED) Date created,
            @JsonProperty(LAST_UPDATED) Date lastUpdated,
            @JsonProperty(ORGANISATION_UNIT) String organisationUnit,
            @JsonProperty(TRACKED_ENTITY) String trackedEntity,
            @JsonProperty(TRACKED_ENTITY_ATTRIBUTES) List<TrackedEntityAttributeValue> trackedEntityAttributeValues,
            @JsonProperty(RELATIONSHIPS) List<Relationship> relationships) {
        return new AutoValue_TrackedEntityInstance(uid, created, lastUpdated, organisationUnit, trackedEntity,
                safeUnmodifiableList(trackedEntityAttributeValues),
                safeUnmodifiableList(relationships));
    }
}
