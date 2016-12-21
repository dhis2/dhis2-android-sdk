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

package org.hisp.dhis.android.core.relationship;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;

// TODO: Tests
@AutoValue
@JsonDeserialize(builder = AutoValue_Relationship.Builder.class)
public abstract class Relationship {
    private static final String JSON_PROPERTY_TRACKED_ENTITY_INSTANCE_A = "trackedEntityInstanceA";
    private static final String JSON_PROPERTY_TRACKED_ENTITY_INSTANCE_B = "trackedEntityInstanceB";
    private static final String JSON_PROPERTY_RELATIONSHIP_TYPE = "relationshipType";

    @JsonProperty(JSON_PROPERTY_TRACKED_ENTITY_INSTANCE_A)
    public abstract TrackedEntityInstance trackedEntityInstanceA();

    @JsonProperty(JSON_PROPERTY_TRACKED_ENTITY_INSTANCE_B)
    public abstract TrackedEntityInstance trackedEntityInstanceB();

    @JsonProperty(JSON_PROPERTY_RELATIONSHIP_TYPE)
    public abstract RelationshipType relationshipType();

    public static Builder builder() {
        return new AutoValue_Relationship.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder {
        @JsonProperty(JSON_PROPERTY_TRACKED_ENTITY_INSTANCE_A)
        public abstract Builder trackedEntityInstanceA(TrackedEntityInstance entityInstanceA);

        @JsonProperty(JSON_PROPERTY_TRACKED_ENTITY_INSTANCE_B)
        public abstract Builder trackedEntityInstanceB(TrackedEntityInstance entityInstanceB);

        @JsonProperty(JSON_PROPERTY_RELATIONSHIP_TYPE)
        public abstract Builder relationshipType(RelationshipType relationshipType);

        public abstract Relationship build();
    }
}
