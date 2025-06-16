/*
 *  Copyright (c) 2004-2023, University of Oslo
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
package org.hisp.dhis.android.core.relationship.internal

import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.relationship.BaseRelationship
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.relationship.RelationshipConstraintType
import org.hisp.dhis.android.core.relationship.RelationshipItem
import org.hisp.dhis.android.core.relationship.RelationshipItemTableInfo.Columns.ENROLLMENT
import org.hisp.dhis.android.core.relationship.RelationshipItemTableInfo.Columns.EVENT
import org.hisp.dhis.android.core.relationship.RelationshipItemTableInfo.Columns.TRACKED_ENTITY_INSTANCE
import org.koin.core.annotation.Singleton

@Singleton
internal class RelationshipDHISVersionManager(
    private val relationshipTypeStore: RelationshipTypeStore,
) {
    suspend fun getOwnedRelationships(
        relationships: Collection<Relationship>,
        elementUid: String,
    ): List<Relationship> {
        return relationships.filter { relationship ->
            val fromItem = relationship.from()
            isBidirectional(relationship) || fromItem?.elementUid() == elementUid
        }
    }

    private suspend fun isBidirectional(relationship: Relationship): Boolean {
        return relationship.relationshipType()?.let { relationshipTypeUid ->
            relationshipTypeStore.selectByUid(relationshipTypeUid)?.bidirectional()
        } ?: false
    }

    private fun getRelatedRelationshipItem(baseRelationship: BaseRelationship, parentUid: String): RelationshipItem? {
        val fromUid = baseRelationship.from()?.elementUid()
        val toUid = baseRelationship.to()?.elementUid()

        val itemBuilder = when {
            parentUid == fromUid ->
                baseRelationship.to()?.toBuilder()
                    ?.relationshipItemType(RelationshipConstraintType.TO)

            parentUid == toUid ->
                baseRelationship.from()?.toBuilder()
                    ?.relationshipItemType(RelationshipConstraintType.FROM)

            else ->
                null
        }

        return itemBuilder
            ?.relationship(ObjectWithUid.create(baseRelationship.uid()))
            ?.build()
    }

    fun saveRelativesIfNotExist(
        relationships: Collection<Relationship>,
        parentUid: String,
        relatives: RelationshipItemRelatives,
    ) {
        for (relationship in relationships) {
            val item = getRelatedRelationshipItem(relationship, parentUid)
            if (item != null && relationship.relationshipType() != null && item.relationshipItemType() != null) {
                val relationshipItem = RelationshipItemRelative(
                    itemUid = item.elementUid(),
                    itemType = item.elementType(),
                    relationshipTypeUid = relationship.relationshipType()!!,
                    constraintType = item.relationshipItemType()!!,
                )
                when (item.elementType()) {
                    TRACKED_ENTITY_INSTANCE -> relatives.addTrackedEntityInstance(relationshipItem)
                    ENROLLMENT -> relatives.addEnrollment(relationshipItem)
                    EVENT -> relatives.addEvent(relationshipItem)
                    else -> {}
                }
            }
        }
    }
}
