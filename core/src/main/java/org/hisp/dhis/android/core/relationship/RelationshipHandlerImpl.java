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

import org.hisp.dhis.android.core.common.GenericHandler;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.systeminfo.DHISVersionManager;

import java.util.Collection;
import java.util.List;

import static org.hisp.dhis.android.core.relationship.RelationshipConstraintType.FROM;
import static org.hisp.dhis.android.core.relationship.RelationshipConstraintType.TO;

final class RelationshipHandlerImpl implements RelationshipHandler {

    private final IdentifiableObjectStore<Relationship> relationshipStore;
    private final RelationshipItemStore relationshipItemStore;
    private final GenericHandler<RelationshipItem, RelationshipItemModel> relationshipItemHandler;
    private final RelationshipItemElementStoreSelector storeSelector;
    private final RelationshipDHISVersionManager versionManager;

    RelationshipHandlerImpl(
            IdentifiableObjectStore<Relationship> relationshipStore,
            RelationshipItemStore relationshipItemStore,
            GenericHandler<RelationshipItem, RelationshipItemModel> relationshipItemHandler,
            RelationshipItemElementStoreSelector storeSelector,
            RelationshipDHISVersionManager versionManager) {
        this.relationshipStore = relationshipStore;
        this.relationshipItemStore = relationshipItemStore;
        this.relationshipItemHandler = relationshipItemHandler;
        this.storeSelector = storeSelector;
        this.versionManager = versionManager;
    }

    @Override
    public void handleMany(Collection<Relationship> relationships) {
        for (Relationship r: relationships) {
            handle(r);
        }
    }

    @Override
    public void handle(Relationship relationship) {
        if (!versionManager.isRelationshipSupported(relationship)) {
            throw new RuntimeException("Only TEI to TEI relationships are supported in 2.29");
        }

        if (!itemExists(relationship.from()) || !itemExists(relationship.to())) {
            throw new RuntimeException("Trying to persist relationship for at least one item not present in database");
        }

        String existingRelationshipUid = getExistingRelationshipUid(relationship);

        if (existingRelationshipUid != null && !existingRelationshipUid.equals(relationship.uid())) {
            relationshipStore.delete(existingRelationshipUid);
        }

        relationshipStore.updateOrInsert(relationship);
        relationshipItemHandler.handle(relationship.from(), new RelationshipItemModelBuilder(relationship, FROM));
        relationshipItemHandler.handle(relationship.to(), new RelationshipItemModelBuilder(relationship, TO));
    }

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
            Relationship existingRelationship = this.relationshipStore.selectByUid(existingRelationshipUid,
                    Relationship.factory);

            if (existingRelationship != null &&
                    relationship.relationshipType().equals(existingRelationship.relationshipType())) {
                return existingRelationship.uid();
            }
        }
        return null;
    }

    public static RelationshipHandler create(DatabaseAdapter databaseAdapter, DHISVersionManager versionManager) {
        return new RelationshipHandlerImpl(
                RelationshipStore.create(databaseAdapter),
                RelationshipItemStoreImpl.create(databaseAdapter),
                RelationshipItemHandler.create(databaseAdapter),
                RelationshipItemElementStoreSelector.create(databaseAdapter),
                new RelationshipDHISVersionManager(versionManager)
        );
    }
}
