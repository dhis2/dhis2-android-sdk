/*
 *  Copyright (c) 2004-2025, University of Oslo
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

package org.hisp.dhis.android.network.tracker

import kotlinx.serialization.Serializable
import org.hisp.dhis.android.core.relationship.NewTrackerImporterRelationship
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.relationship.RelationshipConstraintType
import org.hisp.dhis.android.network.common.dto.BaseDeletableDataObjectDTO
import org.hisp.dhis.android.network.common.dto.DateStringDTO
import org.hisp.dhis.android.network.common.dto.toDto

@Serializable
internal data class NewRelationshipDTO(
    override val deleted: Boolean?,
    val relationship: String?,
    val relationshipType: String?,
    val relationshipName: String?,
    val createdAt: DateStringDTO?,
    val updatedAt: DateStringDTO?,
    val bidirectional: Boolean?,
    val from: NewRelationshipItemDTO?,
    val to: NewRelationshipItemDTO?,
) : BaseDeletableDataObjectDTO {

    fun toDomain(): Relationship {
        return Relationship.builder()
            .uid(relationship)
            .relationshipType(relationshipType)
            .name(relationshipName)
            .created(createdAt?.toDomain())
            .lastUpdated(updatedAt?.toDomain())
            .from(from?.toDomain(relationship, RelationshipConstraintType.FROM))
            .to(to?.toDomain(relationship, RelationshipConstraintType.TO))
            .deleted(deleted)
            .build()
    }
}

internal fun NewTrackerImporterRelationship.toDto(): NewRelationshipDTO {
    return NewRelationshipDTO(
        deleted = deleted,
        relationship = uid,
        relationshipType = relationshipType,
        relationshipName = relationshipName,
        createdAt = createdAt?.toDto(),
        updatedAt = updatedAt?.toDto(),
        bidirectional = bidirectional,
        from = from?.toDto(),
        to = to?.toDto(),
    )
}
