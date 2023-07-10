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

import org.hisp.dhis.android.core.arch.cleaners.internal.OrphanCleaner
import org.hisp.dhis.android.core.common.ObjectWithUidInterface
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.relationship.*

internal abstract class RelationshipOrphanCleaner<O : ObjectWithUidInterface, R : BaseRelationship>(
    private val relationshipStore: RelationshipStore,
    private val relationshipRepository: RelationshipCollectionRepository
) : OrphanCleaner<O, R> {
    abstract fun getItem(uid: String): RelationshipItem
    abstract fun relationships(relationships: Collection<R>): Collection<Relationship>
    override fun deleteOrphan(parent: O?, children: Collection<R>?): Boolean {
        if (parent == null || children == null) {
            return false
        }
        val existingRelationships = relationshipRepository.getByItem(getItem(parent.uid()), true, false)
        var count = 0
        for (existingRelationship in existingRelationships) {
            if (isSynced(existingRelationship.syncState()!!) &&
                !isInRelationshipList(existingRelationship, relationships(children))
            ) {
                relationshipStore.delete(existingRelationship.uid()!!)
                count++
            }
        }
        return count > 0
    }

    private fun isSynced(state: State): Boolean {
        return State.SYNCED == state || State.SYNCED_VIA_SMS == state
    }

    private fun isInRelationshipList(
        target: Relationship,
        list: Collection<Relationship>
    ): Boolean {
        return list.any { relationship ->
            target.from() != null && target.to() != null && relationship.from() != null && relationship.to() != null &&
                RelationshipHelper.areItemsEqual(target.from(), relationship.from()) &&
                RelationshipHelper.areItemsEqual(target.to(), relationship.to()) &&
                target.relationshipType() == relationship.relationshipType()
        }
    }
}
