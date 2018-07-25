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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.data.api.Field;
import org.hisp.dhis.android.core.data.api.Fields;

@AutoValue
public abstract class RelationshipConstraint {
    private static final String RELATIONSHIP_ENTITY = "relationshipEntity";
    private static final String TRACKED_ENTITY_TYPE = "trackedEntityType";
    private static final String PROGRAM = "program";
    private static final String PROGRAM_STAGE = "programStage";

    private static final Field<RelationshipConstraint, String> relationshipEntity = Field.create(RELATIONSHIP_ENTITY);
    private static final Field<RelationshipConstraint, ObjectWithUid> trackedEntityType = Field.create(TRACKED_ENTITY_TYPE);
    private static final Field<RelationshipConstraint, ObjectWithUid> program = Field.create(PROGRAM);
    private static final Field<RelationshipConstraint, ObjectWithUid> programStage = Field.create(PROGRAM_STAGE);

    static final Fields<RelationshipConstraint> allFields = Fields.<RelationshipConstraint>builder().fields(
            relationshipEntity, trackedEntityType, program, programStage).build();

    @Nullable
    @JsonProperty(RELATIONSHIP_ENTITY)
    public abstract String relationshipEntity();

    @Nullable
    @JsonProperty(TRACKED_ENTITY_TYPE)
    public abstract ObjectWithUid trackedEntityType();

    @Nullable
    @JsonProperty(PROGRAM)
    public abstract ObjectWithUid program();

    @Nullable
    @JsonProperty(PROGRAM_STAGE)
    public abstract ObjectWithUid programStage();

    @JsonCreator
    public static RelationshipConstraint create(
            @JsonProperty(RELATIONSHIP_ENTITY) String relationshipEntity,
            @JsonProperty(TRACKED_ENTITY_TYPE) ObjectWithUid trackedEntityType,
            @JsonProperty(PROGRAM) ObjectWithUid program,
            @JsonProperty(PROGRAM_STAGE) ObjectWithUid programStage) {

        return new AutoValue_RelationshipConstraint(
                relationshipEntity,
                trackedEntityType,
                program,
                programStage);
    }
}