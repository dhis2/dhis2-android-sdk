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

package org.hisp.dhis.android.core.relationship.internal;

import org.hisp.dhis.android.core.arch.cleaners.internal.OrphanCleaner;
import org.hisp.dhis.android.core.common.ObjectWithUidInterface;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.relationship.BaseRelationship;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.relationship.RelationshipCollectionRepository;
import org.hisp.dhis.android.core.relationship.RelationshipItem;

import java.util.Collection;
import java.util.List;

import static org.hisp.dhis.android.core.relationship.RelationshipHelper.areItemsEqual;

abstract class RelationshipOrphanCleanerImpl<O extends ObjectWithUidInterface, R extends BaseRelationship>
        implements OrphanCleaner<O, R> {

    private final RelationshipStore relationshipStore;
    private final RelationshipCollectionRepository relationshipRepository;

    RelationshipOrphanCleanerImpl(RelationshipStore relationshipStore,
                                  RelationshipCollectionRepository relationshipRepository) {
        this.relationshipStore = relationshipStore;
        this.relationshipRepository = relationshipRepository;
    }

    abstract RelationshipItem getItem(String uid);

    abstract Collection<Relationship> relationships(Collection<R> relationships);

    public boolean deleteOrphan(O instance, Collection<R> relationships) {
        if (instance == null || relationships == null) {
            return false;
        }

        List<Relationship> existingRelationships =
                relationshipRepository.getByItem(getItem(instance.uid()), true, false);

        int count = 0;
        for (Relationship existingRelationship : existingRelationships) {
            if (isSynced(existingRelationship.syncState()) &&
                    !isInRelationshipList(existingRelationship, relationships(relationships))) {
                relationshipStore.delete(existingRelationship.uid());
                count++;
            }
        }

        return count > 0;
    }

    private boolean isSynced(State state) {
        return State.SYNCED.equals(state) || State.SYNCED_VIA_SMS.equals(state);
    }

    private boolean isInRelationshipList(Relationship target,
                                         Collection<Relationship> list) {
        for (Relationship relationship : list) {
            if (target.from() == null || target.to() == null || relationship.from() == null || target.to() == null) {
                continue;
            }
            if (areItemsEqual(target.from(), relationship.from()) &&
                    areItemsEqual(target.to(), relationship.to()) &&
                    target.relationshipType().equals(relationship.relationshipType())) {
                return true;
            }
        }
        return false;
    }
}