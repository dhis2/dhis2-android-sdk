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

import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.hisp.dhis.android.core.relationship.RelationshipConstraintType.FROM;
import static org.hisp.dhis.android.core.relationship.RelationshipConstraintType.TO;

public final class RelationshipRepository {

    private final IdentifiableObjectStore<RelationshipModel> relationshipStore;
    private final ObjectWithoutUidStore<RelationshipItemModel> relationshipItemStore;

    RelationshipRepository(IdentifiableObjectStore<RelationshipModel> relationshipStore,
                           ObjectWithoutUidStore<RelationshipItemModel> relationshipItemStore) {
        this.relationshipStore = relationshipStore;
        this.relationshipItemStore = relationshipItemStore;
    }

    public void createTEIRelationship(String relationshipType, String fromUid, String toUid) {


    }

    public List<Relationship> getRelationshipsByTEI(@NonNull String trackedEntityInstanceUid) {

        // TODO Create query to avoid retrieving the whole table
        Set<RelationshipItemModel> relationshipItemModels =
                this.relationshipItemStore.selectAll(RelationshipItemModel.factory);

        Set<RelationshipModel> relationshipModels = this.relationshipStore.selectAll(RelationshipModel.factory);

        List<Relationship> relationships = new ArrayList<>();

        for (RelationshipItemModel relationshipItemModel : relationshipItemModels) {
            if (trackedEntityInstanceUid.equals(relationshipItemModel.trackedEntityInstance())) {
                RelationshipModel relationshipModel =
                        findRelationshipByUid(relationshipModels, relationshipItemModel.relationship());

                if (relationshipModel == null) continue;

                RelationshipConstraintType itemType = relationshipItemModel.relationshipItemType();

                RelationshipItemModel relatedTEI = findRelatedTEI(relationshipItemModels,
                        relationshipItemModel.trackedEntityInstance(), itemType == FROM ? TO : FROM);

                if (relatedTEI == null) continue;

                RelationshipItemModel fromModel, toModel;
                if (itemType == FROM) {
                    fromModel = relationshipItemModel;
                    toModel = relatedTEI;
                } else {
                    fromModel = relatedTEI;
                    toModel = relationshipItemModel;
                }

                relationships.add(Relationship.create(
                        null,
                        null,
                        relationshipModel.uid(),
                        relationshipModel.relationshipType(),
                        null,
                        null,
                        RelationshipItem.create(
                                RelationshipItemTrackedEntityInstance.create(fromModel.trackedEntityInstance()),
                                null,
                                null),
                        RelationshipItem.create(
                                RelationshipItemTrackedEntityInstance.create(toModel.trackedEntityInstance()),
                                null,
                                null
                        )
                ));
            }
        }

        return relationships;
    }

    private RelationshipModel findRelationshipByUid(Set<RelationshipModel> relationshipModels, String uid) {
        for (RelationshipModel relationshipModel : relationshipModels) {
            if (uid.equals(relationshipModel.uid())) {
                return relationshipModel;
            }
        }
        return null;
    }

    private RelationshipItemModel findRelatedTEI(Set<RelationshipItemModel> items, String teiUid,
                                                 RelationshipConstraintType type) {
        for (RelationshipItemModel item : items) {
            if (teiUid.equals(item.trackedEntityInstance()) && item.relationshipItemType() == type) {
                return item;
            }
        }
        return null;
    }

    public static RelationshipRepository create(DatabaseAdapter databaseAdapter) {
        return new RelationshipRepository(
                RelationshipStore.create(databaseAdapter),
                RelationshipItemStore.create(databaseAdapter)
        );
    }
}
