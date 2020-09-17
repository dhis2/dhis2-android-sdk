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
package org.hisp.dhis.android.core.relationship.internal;

import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction;
import org.hisp.dhis.android.core.arch.handlers.internal.Handler;
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.relationship.RelationshipItem;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import dagger.Reusable;

import static org.hisp.dhis.android.core.relationship.RelationshipConstraintType.FROM;
import static org.hisp.dhis.android.core.relationship.RelationshipConstraintType.TO;

@Reusable
final class RelationshipHandlerImpl extends IdentifiableHandlerImpl<Relationship> implements RelationshipHandler {

    private final RelationshipItemStore relationshipItemStore;
    private final Handler<RelationshipItem> relationshipItemHandler;
    private final RelationshipItemElementStoreSelector storeSelector;
    private final RelationshipDHISVersionManager versionManager;

    @Inject
    RelationshipHandlerImpl(
            RelationshipStore relationshipStore,
            RelationshipItemStore relationshipItemStore,
            Handler<RelationshipItem> relationshipItemHandler,
            RelationshipItemElementStoreSelector storeSelector,
            RelationshipDHISVersionManager versionManager) {
        super(relationshipStore);
        this.relationshipItemStore = relationshipItemStore;
        this.relationshipItemHandler = relationshipItemHandler;
        this.storeSelector = storeSelector;
        this.versionManager = versionManager;
    }

    @Override
    protected Collection<Relationship> beforeCollectionHandled(Collection<Relationship> relationships) {
        Collection<Relationship> supportedRelationships = new ArrayList<>();

        for (Relationship relationship : relationships) {
            // Only TEI - TEI relationships are supported so far
            if (relationship.from().hasTrackedEntityInstance() && relationship.to().hasTrackedEntityInstance()) {
                supportedRelationships.add(relationship);
            }
        }

        return supportedRelationships;
    }

    @Override
    protected Relationship beforeObjectHandled(Relationship relationship) {
        if (!versionManager.isRelationshipSupported(relationship)) {
            throw new RuntimeException("Only TEI to TEI relationships are supported in 2.29");
        }

        String existingRelationshipUid = getExistingRelationshipUid(relationship);

        // Compatibility with 2.29. Relationships do not have uids and must be matched based on their items.
        if (existingRelationshipUid != null && !existingRelationshipUid.equals(relationship.uid())) {
            store.delete(existingRelationshipUid);
        }
        return relationship;
    }

    @Override
    protected void afterObjectHandled(Relationship relationship, HandleAction action) {
        relationshipItemHandler.handle(relationship.from().toBuilder()
                .relationship(ObjectWithUid.create(relationship.uid())).relationshipItemType(FROM).build());
        relationshipItemHandler.handle(relationship.to().toBuilder()
                .relationship(ObjectWithUid.create(relationship.uid())).relationshipItemType(TO).build());
    }

    @Override
    public boolean doesRelationshipExist(Relationship relationship) {
        return getExistingRelationshipUid(relationship) != null;
    }

    private boolean itemExists(RelationshipItem item) {
        return storeSelector.getElementStore(item).exists(item.elementUid());
    }

    private String getExistingRelationshipUid(Relationship relationship) {
        List<String> existingRelationshipUidsForPair =
                this.relationshipItemStore.getRelationshipUidsForItems(relationship.from(), relationship.to());

        for (String existingRelationshipUid : existingRelationshipUidsForPair) {
            Relationship existingRelationship = store.selectByUid(existingRelationshipUid);

            if (existingRelationship != null &&
                    relationship.relationshipType().equals(existingRelationship.relationshipType())) {
                return existingRelationship.uid();
            }
        }
        return null;
    }
}
