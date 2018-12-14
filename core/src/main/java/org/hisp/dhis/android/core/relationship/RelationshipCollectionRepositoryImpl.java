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

import org.hisp.dhis.android.core.arch.repositories.children.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadOnlyCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.object.ReadWriteObjectRepository;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.PojoBuilder;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.common.StoreWithState;
import org.hisp.dhis.android.core.common.UidsHelper;
import org.hisp.dhis.android.core.data.database.DatabaseAdapter;
import org.hisp.dhis.android.core.imports.ImportSummary;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;

import static org.hisp.dhis.android.core.relationship.RelationshipConstraintType.FROM;
import static org.hisp.dhis.android.core.relationship.RelationshipConstraintType.TO;

final class RelationshipCollectionRepositoryImpl extends ReadOnlyCollectionRepositoryImpl<Relationship>
        implements RelationshipCollectionRepository {

    private final IdentifiableObjectStore<Relationship> store;
    private final RelationshipHandler relationshipHandler;
    private final RelationshipItemStore relationshipItemStore;
    private final RelationshipItemElementStoreSelector storeSelector;
    private final PojoBuilder<RelationshipItem, RelationshipItemModel> relationshipItemPojoBuilder;

    private RelationshipCollectionRepositoryImpl(IdentifiableObjectStore<Relationship> store,
                                                 RelationshipHandler relationshipHandler,
                                                 RelationshipItemStore relationshipItemStore,
                                                 RelationshipItemElementStoreSelector storeSelector,
                                                 PojoBuilder<RelationshipItem, RelationshipItemModel>
                                               relationshipItemPojoBuilder,
                                                 Collection<ChildrenAppender<Relationship>> childrenAppenders) {
        super(store, childrenAppenders);
        this.store = store;
        this.relationshipHandler = relationshipHandler;
        this.relationshipItemStore = relationshipItemStore;
        this.storeSelector = storeSelector;
        this.relationshipItemPojoBuilder = relationshipItemPojoBuilder;
    }

    @Override
    public void add(Relationship relationship) throws D2Error {
        if (relationshipHandler.doesRelationshipExist(relationship)) {
            throw D2Error
                    .builder()
                    .errorComponent(D2ErrorComponent.SDK)
                    .errorCode(D2ErrorCode.CANT_CREATE_EXISTING_OBJECT)
                    .errorDescription("Tried to create already existing Relationship: " + relationship)
                    .build();
        } else {
            RelationshipItem from = relationship.from();
            StoreWithState fromStore = storeSelector.getElementStore(from);
            State fromState = fromStore.getState(from.elementUid());

            if (isUpdatableState(fromState)) {
                relationshipHandler.handle(relationship);
                setToUpdate(fromStore, fromState, from.elementUid());
            } else {
                throw D2Error
                        .builder()
                        .errorComponent(D2ErrorComponent.SDK)
                        .errorCode(D2ErrorCode.OBJECT_CANT_BE_UPDATED)
                        .errorDescription(
                                "RelationshipItem from doesn't have updatable state: " +
                                "(" + from + ": " + fromState +  ")")
                        .build();
            }
        }
    }

    @Override
    public Callable<ImportSummary> upload() throws D2Error {
        throw D2Error.builder()
                .errorComponent(D2ErrorComponent.SDK)
                .errorCode(D2ErrorCode.NOT_IMPLEMENTED)
                .errorDescription("Not yet implemented")
                .build();
    }

    @Override
    public ReadWriteObjectRepository<Relationship> uid(String uid) {
        return new RelationshipObjectRepository(store, uid, childrenAppenders, storeSelector);
    }

    private boolean isUpdatableState(State state) {
        return state == State.SYNCED || state == State.TO_POST || state == State.TO_UPDATE;
    }

    private void setToUpdate(StoreWithState store, State state, String elementUid) {
        if (state == State.SYNCED) {
            store.setState(elementUid, State.TO_UPDATE);
        }
    }

    @Override
    public List<Relationship> getByItem(@NonNull RelationshipItem searchItem) {

        // TODO Create query to avoid retrieving the whole table
        List<RelationshipItemModel> relationshipItemModels = this.relationshipItemStore.selectAll();

        List<Relationship> allRelationshipsFromDb = this.store.selectAll();

        List<Relationship> relationships = new ArrayList<>();

        for (RelationshipItemModel iterationItemModel : relationshipItemModels) {
            RelationshipItem iterationItem = relationshipItemPojoBuilder.buildPojo(iterationItemModel);

            if (searchItem.equals(iterationItem)) {
                Relationship relationshipFromDb =
                        UidsHelper.findByUid(allRelationshipsFromDb, iterationItemModel.relationship());

                if (relationshipFromDb == null) {
                    continue;
                }

                RelationshipConstraintType itemType = iterationItemModel.relationshipItemType();

                RelationshipItemModel relatedItemModel = findRelatedTEI(relationshipItemModels,
                        iterationItemModel.relationship(), itemType == FROM ? TO : FROM);

                if (relatedItemModel == null) {
                    continue;
                }

                RelationshipItemModel fromModel, toModel;
                if (itemType == FROM) {
                    fromModel = iterationItemModel;
                    toModel = relatedItemModel;
                } else {
                    fromModel = relatedItemModel;
                    toModel = iterationItemModel;
                }

                Relationship relationship = Relationship.builder()
                        .uid(relationshipFromDb.uid())
                        .relationshipType(relationshipFromDb.relationshipType())
                        .from(relationshipItemPojoBuilder.buildPojo(fromModel))
                        .to(relationshipItemPojoBuilder.buildPojo(toModel))
                        .build();

                relationships.add(relationship);
            }
        }

        return relationships;
    }

    private RelationshipItemModel findRelatedTEI(Collection<RelationshipItemModel> items, String relationshipUid,
                                                 RelationshipConstraintType type) {
        for (RelationshipItemModel item : items) {
            if (relationshipUid.equals(item.relationship()) && item.relationshipItemType() == type) {
                return item;
            }
        }
        return null;
    }

    static RelationshipCollectionRepository create(DatabaseAdapter databaseAdapter,
                                                       RelationshipHandler relationshipHandler) {
        RelationshipItemStore itemStore = RelationshipItemStoreImpl.create(databaseAdapter);
        PojoBuilder<RelationshipItem, RelationshipItemModel> itemPojoBuilder = new RelationshipItemPojoBuilder();

        List<ChildrenAppender<Relationship>> appenders = new ArrayList<>(1);
        appenders.add(new RelationshipItemChildrenAppender(itemStore, itemPojoBuilder));

        return new RelationshipCollectionRepositoryImpl(
                RelationshipStore.create(databaseAdapter),
                relationshipHandler,
                RelationshipItemStoreImpl.create(databaseAdapter),
                RelationshipItemElementStoreSelectorImpl.create(databaseAdapter),
                itemPojoBuilder,
                appenders
        );
    }
}
