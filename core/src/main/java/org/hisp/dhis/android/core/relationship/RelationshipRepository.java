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

import android.support.annotation.NonNull;

import org.hisp.dhis.android.core.common.D2CallException;
import org.hisp.dhis.android.core.common.D2ErrorCode;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.UidsHelper;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.utils.CodeGeneratorImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hisp.dhis.android.core.relationship.RelationshipConstraintType.FROM;
import static org.hisp.dhis.android.core.relationship.RelationshipConstraintType.TO;

final class RelationshipRepository implements RelationshipRepositoryInterface {

    private final IdentifiableObjectStore<Relationship> relationshipStore;
    private final RelationshipHandler relationshipHandler;
    private final RelationshipItemStoreInterface relationshipItemStore;

    private RelationshipRepository(IdentifiableObjectStore<Relationship> relationshipStore,
                           RelationshipHandler relationshipHandler,
                           RelationshipItemStoreInterface relationshipItemStore) {
        this.relationshipStore = relationshipStore;
        this.relationshipHandler = relationshipHandler;
        this.relationshipItemStore = relationshipItemStore;
    }

    @Override
    public void createTEIRelationship(String relationshipType, String fromUid, String toUid) throws D2CallException {
        if (!relationshipHandler.doesTEIRelationshipExist(fromUid, toUid, relationshipType)) {
            Relationship relationship = Relationship.builder()
                    .uid(new CodeGeneratorImpl().generate())
                    .from(RelationshipHelper.teiItem(fromUid))
                    .to(RelationshipHelper.teiItem(toUid))
                    .relationshipType(relationshipType)
                    .build();
            relationshipHandler.handle(relationship);
        } else {
            throw D2CallException
                    .builder()
                    .errorCode(D2ErrorCode.CANT_CREATE_EXISTING_OBJECT)
                    .errorDescription("Tried to create existing Relationship: ( " + fromUid + ", "
                            + toUid + ", " + relationshipType)
                    .build();
        }
    }

    @Override
    public List<Relationship> getRelationshipsByTEI(@NonNull String trackedEntityInstanceUid) {

        // TODO Create query to avoid retrieving the whole table
        Set<RelationshipItemModel> relationshipItemModels =
                this.relationshipItemStore.selectAll(RelationshipItemModel.factory);

        Set<Relationship> relationshipModels = this.relationshipStore.selectAll(Relationship.factory);

        List<Relationship> relationships = new ArrayList<>();

        for (RelationshipItemModel relationshipItemModel : relationshipItemModels) {
            if (trackedEntityInstanceUid.equals(relationshipItemModel.trackedEntityInstance())) {
                Relationship relationshipFromDb =
                        UidsHelper.findByUid(relationshipModels, relationshipItemModel.relationship());

                if (relationshipFromDb == null) {
                    continue;
                }

                RelationshipConstraintType itemType = relationshipItemModel.relationshipItemType();

                RelationshipItemModel relatedItemModel = findRelatedTEI(relationshipItemModels,
                        relationshipItemModel.relationship(), itemType == FROM ? TO : FROM);

                if (relatedItemModel == null) {
                    continue;
                }

                RelationshipItemModel fromModel, toModel;
                if (itemType == FROM) {
                    fromModel = relationshipItemModel;
                    toModel = relatedItemModel;
                } else {
                    fromModel = relatedItemModel;
                    toModel = relationshipItemModel;
                }

                Relationship relationship = Relationship.builder()
                        .uid(relationshipFromDb.uid())
                        .relationshipType(relationshipFromDb.relationshipType())
                        .from(RelationshipHelper.teiItem(fromModel.trackedEntityInstance()))
                        .to(RelationshipHelper.teiItem(toModel.trackedEntityInstance()))
                        .build();

                relationships.add(relationship);
            }
        }

        return relationships;
    }

    private RelationshipItemModel findRelatedTEI(Set<RelationshipItemModel> items, String relationshipUid,
                                                 RelationshipConstraintType type) {
        for (RelationshipItemModel item : items) {
            if (relationshipUid.equals(item.relationship()) && item.relationshipItemType() == type) {
                return item;
            }
        }
        return null;
    }

    static RelationshipRepository create(DatabaseAdapter databaseAdapter, RelationshipHandler relationshipHandler) {
        return new RelationshipRepository(
                RelationshipStore.create(databaseAdapter),
                relationshipHandler,
                RelationshipItemStore.create(databaseAdapter)
        );
    }
}
