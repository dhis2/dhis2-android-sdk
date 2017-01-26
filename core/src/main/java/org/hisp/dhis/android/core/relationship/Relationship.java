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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.data.api.NestedField;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;

// TODO: Tests when relationship is fixed to be queried.
@AutoValue
public abstract class Relationship {
    private static final String TRACKED_ENTITY_INSTANCE_A = "trackedEntityInstanceA";
    private static final String TRACKED_ENTITY_INSTANCE_B = "trackedEntityInstanceB";
    private static final String RELATIONSHIP_TYPE = "relationshipType";

    /*  //Uncommented because of api issued. We cannot query Relationship itself.
        //https://play.dhis2.org/test/api/schemas/relationship.json  returns a json with 404 http status.
        public static final NestedField<Relationship, TrackedEntityInstance> trackedEntityInstanceA
        = NestedField.create(TRACKED_ENTITY_INSTANCE_A);
        public static final NestedField<Relationship, TrackedEntityInstance> trackedEntityInstacenB
        = NestedField.create(TRACKED_ENTITY_INSTANCE_B);*/
    public static final NestedField<Relationship, RelationshipType> relationshipType
            = NestedField.create(RELATIONSHIP_TYPE);

    @JsonProperty(TRACKED_ENTITY_INSTANCE_A)
    public abstract TrackedEntityInstance trackedEntityInstanceA();

    @JsonProperty(TRACKED_ENTITY_INSTANCE_B)
    public abstract TrackedEntityInstance trackedEntityInstanceB();

    @JsonProperty(RELATIONSHIP_TYPE)
    public abstract RelationshipType relationshipType();

    @JsonCreator
    public static Relationship create(
            @JsonProperty(TRACKED_ENTITY_INSTANCE_A) TrackedEntityInstance trackedEntityInstanceA,
            @JsonProperty(TRACKED_ENTITY_INSTANCE_B) TrackedEntityInstance trackedEntityInstanceB,
            @JsonProperty(RELATIONSHIP_TYPE) RelationshipType relationshipType) {

        return new AutoValue_Relationship(trackedEntityInstanceA, trackedEntityInstanceB, relationshipType);
    }
}
