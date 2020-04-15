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

package org.hisp.dhis.android.core.data.relationship;

import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.relationship.RelationshipConstraint;
import org.hisp.dhis.android.core.relationship.RelationshipConstraintType;
import org.hisp.dhis.android.core.relationship.RelationshipEntityType;
import org.hisp.dhis.android.core.relationship.RelationshipType;

import java.util.HashMap;
import java.util.Map;


public class RelationshipTypeSamples {

    public static String RELATIONSHIP_TYPE_UID_1 = "RELATIONSHIP_TYPE_UID_1";
    public static String RELATIONSHIP_TYPE_UID_2 = "RELATIONSHIP_TYPE_UID_2";
    public static String RELATIONSHIP_TYPE_UID_3 = "RELATIONSHIP_TYPE_UID_3";
    public static String TET_FOR_RELATIONSHIP_3_UID = "nEenWmSyUEp";

    public static RelationshipConstraint FROM_CONSTRAINT_1 = RelationshipConstraint
            .builder()
            .id(1L)
            .constraintType(RelationshipConstraintType.FROM)
            .relationshipType(ObjectWithUid.create(RELATIONSHIP_TYPE_UID_1))
            .build();

    public static RelationshipConstraint TO_CONSTRAINT_1 = RelationshipConstraint
            .builder()
            .id(2L)
            .constraintType(RelationshipConstraintType.TO)
            .relationshipType(ObjectWithUid.create(RELATIONSHIP_TYPE_UID_1))
            .build();

    public static RelationshipConstraint FROM_CONSTRAINT_2 = RelationshipConstraint
            .builder()
            .id(3L)
            .constraintType(RelationshipConstraintType.FROM)
            .relationshipType(ObjectWithUid.create(RELATIONSHIP_TYPE_UID_2))
            .build();

    public static RelationshipConstraint TO_CONSTRAINT_2 = RelationshipConstraint
            .builder()
            .id(4L)
            .constraintType(RelationshipConstraintType.TO)
            .relationshipType(ObjectWithUid.create(RELATIONSHIP_TYPE_UID_2))
            .relationshipEntity(RelationshipEntityType.TRACKED_ENTITY_INSTANCE)
            .trackedEntityType(ObjectWithUid.create(TET_FOR_RELATIONSHIP_3_UID))
            .build();

    public static RelationshipConstraint FROM_CONSTRAINT_3 = RelationshipConstraint
            .builder()
            .id(5L)
            .constraintType(RelationshipConstraintType.FROM)
            .relationshipType(ObjectWithUid.create(RELATIONSHIP_TYPE_UID_3))
            .relationshipEntity(RelationshipEntityType.TRACKED_ENTITY_INSTANCE)
            .trackedEntityType(ObjectWithUid.create(TET_FOR_RELATIONSHIP_3_UID))
            .build();

    public static RelationshipConstraint TO_CONSTRAINT_3 = RelationshipConstraint
            .builder()
            .id(6L)
            .constraintType(RelationshipConstraintType.TO)
            .relationshipType(ObjectWithUid.create(RELATIONSHIP_TYPE_UID_3))
            .build();

    public static RelationshipType RELATIONSHIP_TYPE_1 = RelationshipType
            .builder()
            .id(1L)
            .uid(RELATIONSHIP_TYPE_UID_1)
            .fromConstraint(FROM_CONSTRAINT_1)
            .toConstraint(TO_CONSTRAINT_1)
            .build();

    public static RelationshipType RELATIONSHIP_TYPE_2 = RelationshipType
            .builder()
            .id(2L)
            .uid(RELATIONSHIP_TYPE_UID_2)
            .fromConstraint(FROM_CONSTRAINT_2)
            .toConstraint(TO_CONSTRAINT_2)
            .build();

    public static RelationshipType RELATIONSHIP_TYPE_3 = RelationshipType
            .builder()
            .id(3L)
            .uid(RELATIONSHIP_TYPE_UID_3)
            .fromConstraint(FROM_CONSTRAINT_3)
            .toConstraint(TO_CONSTRAINT_3)
            .build();

    public static Map<String, RelationshipType> typeMap() {
        Map<String, RelationshipType> typeMap = new HashMap<>();
        typeMap.put(RELATIONSHIP_TYPE_UID_1, RELATIONSHIP_TYPE_1);
        typeMap.put(RELATIONSHIP_TYPE_UID_2, RELATIONSHIP_TYPE_2);
        typeMap.put(RELATIONSHIP_TYPE_UID_3, RELATIONSHIP_TYPE_3);
        return typeMap;
    }
}