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
package org.hisp.dhis.android.core.data.relationship

import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.relationship.RelationshipConstraint
import org.hisp.dhis.android.core.relationship.RelationshipConstraintType
import org.hisp.dhis.android.core.relationship.RelationshipEntityType
import org.hisp.dhis.android.core.relationship.RelationshipType

object RelationshipTypeSamples {
    var RELATIONSHIP_TYPE_UID_1 = "RELATIONSHIP_TYPE_UID_1"
    var RELATIONSHIP_TYPE_UID_2 = "RELATIONSHIP_TYPE_UID_2"
    var RELATIONSHIP_TYPE_UID_3 = "RELATIONSHIP_TYPE_UID_3"
    var TET_FOR_RELATIONSHIP_3_UID = "nEenWmSyUEp"
    var FROM_CONSTRAINT_1 = RelationshipConstraint
        .builder()
        .id(100L)
        .constraintType(RelationshipConstraintType.FROM)
        .relationshipType(ObjectWithUid.create(RELATIONSHIP_TYPE_UID_1))
        .build()
    var TO_CONSTRAINT_1 = RelationshipConstraint
        .builder()
        .id(200L)
        .constraintType(RelationshipConstraintType.TO)
        .relationshipType(ObjectWithUid.create(RELATIONSHIP_TYPE_UID_1))
        .build()
    var FROM_CONSTRAINT_2 = RelationshipConstraint
        .builder()
        .id(300L)
        .constraintType(RelationshipConstraintType.FROM)
        .relationshipType(ObjectWithUid.create(RELATIONSHIP_TYPE_UID_2))
        .build()
    var TO_CONSTRAINT_2 = RelationshipConstraint
        .builder()
        .id(400L)
        .constraintType(RelationshipConstraintType.TO)
        .relationshipType(ObjectWithUid.create(RELATIONSHIP_TYPE_UID_2))
        .relationshipEntity(RelationshipEntityType.TRACKED_ENTITY_INSTANCE)
        .trackedEntityType(ObjectWithUid.create(TET_FOR_RELATIONSHIP_3_UID))
        .build()
    var FROM_CONSTRAINT_3 = RelationshipConstraint
        .builder()
        .id(500L)
        .constraintType(RelationshipConstraintType.FROM)
        .relationshipType(ObjectWithUid.create(RELATIONSHIP_TYPE_UID_3))
        .relationshipEntity(RelationshipEntityType.TRACKED_ENTITY_INSTANCE)
        .trackedEntityType(ObjectWithUid.create(TET_FOR_RELATIONSHIP_3_UID))
        .build()
    var TO_CONSTRAINT_3 = RelationshipConstraint
        .builder()
        .id(600L)
        .constraintType(RelationshipConstraintType.TO)
        .relationshipType(ObjectWithUid.create(RELATIONSHIP_TYPE_UID_3))
        .build()
    var RELATIONSHIP_TYPE_1 = RelationshipType
        .builder()
        .id(100L)
        .uid(RELATIONSHIP_TYPE_UID_1)
        .fromConstraint(FROM_CONSTRAINT_1)
        .toConstraint(TO_CONSTRAINT_1)
        .build()
    var RELATIONSHIP_TYPE_2 = RelationshipType
        .builder()
        .id(200L)
        .uid(RELATIONSHIP_TYPE_UID_2)
        .fromConstraint(FROM_CONSTRAINT_2)
        .toConstraint(TO_CONSTRAINT_2)
        .build()
    var RELATIONSHIP_TYPE_3 = RelationshipType
        .builder()
        .id(300L)
        .uid(RELATIONSHIP_TYPE_UID_3)
        .fromConstraint(FROM_CONSTRAINT_3)
        .toConstraint(TO_CONSTRAINT_3)
        .build()

    val typeMap: Map<String, RelationshipType> = mapOf(
        RELATIONSHIP_TYPE_UID_1 to RELATIONSHIP_TYPE_1,
        RELATIONSHIP_TYPE_UID_2 to RELATIONSHIP_TYPE_2,
        RELATIONSHIP_TYPE_UID_3 to RELATIONSHIP_TYPE_3
    )
}
