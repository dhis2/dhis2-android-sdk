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

package org.hisp.dhis.android.core.relationship;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.data.api.Field;

// TODO: Tests when relationshipType is fixed to be queried.
@AutoValue
@JsonDeserialize(builder = AutoValue_Relationship.Builder.class)
public abstract class Relationship {
    private static final String TRACKED_ENTITY_INSTANCE_A = "trackedEntityInstanceA";
    private static final String TRACKED_ENTITY_INSTANCE_B = "trackedEntityInstanceB";
    private static final String RELATIONSHIP_TYPE = "relationship";


    public static final Field<Relationship, String> trackedEntityInstanceA
            = Field.create(TRACKED_ENTITY_INSTANCE_A);
    public static final Field<Relationship, String> trackedEntityInstanceB
            = Field.create(TRACKED_ENTITY_INSTANCE_B);
    public static final Field<Relationship, String> relationship = Field.create(RELATIONSHIP_TYPE);

    @JsonProperty(TRACKED_ENTITY_INSTANCE_A)
    public abstract String trackedEntityInstanceA();

    @JsonProperty(TRACKED_ENTITY_INSTANCE_B)
    public abstract String trackedEntityInstanceB();

    @Nullable
    @JsonProperty(RELATIONSHIP_TYPE)
    public abstract String relationshipType();

    abstract Relationship.Builder toBuilder();

    public static Relationship.Builder builder() {
        return new AutoValue_Relationship.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder {
        @JsonProperty(TRACKED_ENTITY_INSTANCE_A)
        public abstract Builder trackedEntityInstanceA(String trackedEntityInstanceA);

        @JsonProperty(TRACKED_ENTITY_INSTANCE_B)
        public abstract Builder trackedEntityInstanceB(String trackedEntityInstanceB);

        @Nullable
        @JsonProperty(RELATIONSHIP_TYPE)
        public abstract Builder relationshipType(String relationshipType);

        public abstract Relationship build();
    }
}
