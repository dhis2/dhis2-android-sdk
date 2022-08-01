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

package org.hisp.dhis.android.core.data.relationship;

import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.relationship.RelationshipConstraint;
import org.hisp.dhis.android.core.relationship.RelationshipConstraintType;
import org.hisp.dhis.android.core.relationship.RelationshipEntityType;


public class RelationshipConstraintSamples {

    public static RelationshipConstraint getRelationshipConstraint() {
        return RelationshipConstraint.builder()
                .id(1L)
                .relationshipType(ObjectWithUid.create("relationship_type_uid"))
                .constraintType(RelationshipConstraintType.FROM)
                .relationshipEntity(RelationshipEntityType.TRACKED_ENTITY_INSTANCE)
                .trackedEntityType(ObjectWithUid.create("tracked_entity_type_uid"))
                .program(ObjectWithUid.create("program_uid"))
                .programStage(ObjectWithUid.create("program_stage_uid"))
                .build();
    }
}