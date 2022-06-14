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
package org.hisp.dhis.android.core.relationship.internal

import dagger.Reusable
import javax.inject.Inject
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.relationship.RelationshipConstraintType
import org.hisp.dhis.android.core.relationship.RelationshipItem

@Reusable
internal class RelationshipHandlerImpl @Inject constructor(
    relationshipStore: RelationshipStore,
    private val relationshipItemStore: RelationshipItemStore,
    private val relationshipItemHandler: Handler<RelationshipItem>,
    private val storeSelector: RelationshipItemElementStoreSelector
) : IdentifiableHandlerImpl<Relationship>(relationshipStore), RelationshipHandler {

    override fun afterObjectHandled(o: Relationship, action: HandleAction) {
        relationshipItemHandler.handle(
            o.from()!!.toBuilder()
                .relationship(ObjectWithUid.create(o.uid()))
                .relationshipItemType(RelationshipConstraintType.FROM).build()
        )
        relationshipItemHandler.handle(
            o.to()!!.toBuilder()
                .relationship(ObjectWithUid.create(o.uid()))
                .relationshipItemType(RelationshipConstraintType.TO).build()
        )
    }

    override fun doesRelationshipExist(relationship: Relationship): Boolean {
        return getExistingRelationshipUid(relationship) != null
    }

    override fun doesRelationshipItemExist(item: RelationshipItem): Boolean {
        return storeSelector.getElementStore(item).exists(item.elementUid())
    }

    override fun deleteLinkedRelationships(entityUid: String) {
        relationshipItemStore.getByEntityUid(entityUid)
            .mapNotNull { it.relationship()?.uid() }
            .distinct()
            .forEach { store.deleteIfExists(it) }
    }

    private fun getExistingRelationshipUid(relationship: Relationship): String? {
        val existingRelationshipUidsForPair = relationshipItemStore.getRelationshipUidsForItems(
            relationship.from()!!, relationship.to()!!
        )
        for (existingRelationshipUid in existingRelationshipUidsForPair) {
            val existingRelationship = store.selectByUid(existingRelationshipUid)
            if (existingRelationship != null && relationship.relationshipType()
                == existingRelationship.relationshipType()
            ) {
                return existingRelationship.uid()
            }
        }
        return null
    }
}
