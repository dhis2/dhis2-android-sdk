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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.Fields;
import org.hisp.dhis.android.core.data.api.NestedField;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;

// TODO: Tests when relationship is fixed to be queried.
@AutoValue
@JsonInclude(Include.NON_NULL)
public abstract class Relationship {
    private static final String TRACKED_ENTITY_INSTANCE_A = "trackedEntityInstanceA";
    private static final String TRACKED_ENTITY_INSTANCE_B = "trackedEntityInstanceB";
    private static final String RELATIONSHIP = "relationship";
    private static final String RELATIONSHIP_TYPE = "relationshipType";
    private static final String DISPLAY_NAME = "displayName";
    private static final String RELATIVE = "relative";
    private static final String FROM = "from";
    private static final String TO = "to";

    public static final Field<Relationship, String> trackedEntityInstanceA
            = Field.create(TRACKED_ENTITY_INSTANCE_A);
    public static final Field<Relationship, String> trackedEntityInstanceB
            = Field.create(TRACKED_ENTITY_INSTANCE_B);
    public static final Field<Relationship, String> relationship = Field.create(RELATIONSHIP);
    public static final Field<Relationship, String> relationshipType = Field.create(RELATIONSHIP_TYPE);
    public static final Field<Relationship, String> displayName = Field.create(DISPLAY_NAME);
    public static final NestedField<Relationship, TrackedEntityInstance> relative = NestedField.create(RELATIVE);
    public static final NestedField<Relationship, RelationshipItem> from = NestedField.create(FROM);
    public static final NestedField<Relationship, RelationshipItem> to = NestedField.create(TO);

    public static final Fields<Relationship> allFields = Fields.<Relationship>builder().fields(
            trackedEntityInstanceA, trackedEntityInstanceB, relationship, relationshipType, displayName,
            from.with(RelationshipItem.allFields), to.with(RelationshipItem.allFields), relative).build();

    @Nullable
    @JsonProperty(TRACKED_ENTITY_INSTANCE_A)
    public abstract String trackedEntityInstanceA();

    @Nullable
    @JsonProperty(TRACKED_ENTITY_INSTANCE_B)
    public abstract String trackedEntityInstanceB();

    @JsonProperty(RELATIONSHIP)
    public abstract String relationship();

    @Nullable
    @JsonProperty(RELATIONSHIP_TYPE)
    public abstract String relationshipType();

    @Nullable
    @JsonProperty(DISPLAY_NAME)
    public abstract String displayName();

    @Nullable
    @JsonProperty(RELATIVE)
    public abstract TrackedEntityInstance relative();

    @Nullable
    @JsonProperty(FROM)
    public abstract RelationshipItem from();

    @Nullable
    @JsonProperty(TO)
    public abstract RelationshipItem to();

    @JsonCreator
    public static Relationship create(
            @JsonProperty(TRACKED_ENTITY_INSTANCE_A) String trackedEntityInstanceA,
            @JsonProperty(TRACKED_ENTITY_INSTANCE_B) String trackedEntityInstanceB,
            @JsonProperty(RELATIONSHIP) String relationship,
            @JsonProperty(RELATIONSHIP_TYPE) String relationshipType,
            @JsonProperty(DISPLAY_NAME) String displayName,
            @JsonProperty(RELATIVE) TrackedEntityInstance relative,
            @JsonProperty(FROM) RelationshipItem from,
            @JsonProperty(TO) RelationshipItem to) {

        return new AutoValue_Relationship(trackedEntityInstanceA, trackedEntityInstanceB, relationship,
                relationshipType, displayName, relative, from, to);
    }
}
