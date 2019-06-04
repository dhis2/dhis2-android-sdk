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
package org.hisp.dhis.android.core.relationship;

import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.ReadOnlyCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadWriteWithUidCollectionRepository;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.DateFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.object.ReadWriteObjectRepository;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.common.BaseIdentifiableObjectModel;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.arch.db.stores.internal.StoreWithState;
import org.hisp.dhis.android.core.arch.helpers.UidsHelper;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import dagger.Reusable;

import static org.hisp.dhis.android.core.relationship.RelationshipConstraintType.FROM;
import static org.hisp.dhis.android.core.relationship.RelationshipConstraintType.TO;

@Reusable
public final class RelationshipCollectionRepository
        extends ReadOnlyCollectionRepositoryImpl<Relationship, RelationshipCollectionRepository>
        implements ReadWriteWithUidCollectionRepository<Relationship, Relationship> {

    private final RelationshipStore store;
    private final RelationshipHandler relationshipHandler;
    private final RelationshipItemStore relationshipItemStore;
    private final RelationshipItemElementStoreSelector storeSelector;

    @Inject
    RelationshipCollectionRepository(final RelationshipStore store,
                                     final Map<String, ChildrenAppender<Relationship>> childrenAppenders,
                                     final RepositoryScope scope,
                                     final RelationshipHandler relationshipHandler,
                                     final RelationshipItemStore relationshipItemStore,
                                     final RelationshipItemElementStoreSelector storeSelector) {
        super(store, childrenAppenders, scope, new FilterConnectorFactory<>(scope,
                s -> new RelationshipCollectionRepository(store, childrenAppenders, s,
                        relationshipHandler, relationshipItemStore, storeSelector)));
        this.store = store;
        this.relationshipHandler = relationshipHandler;
        this.relationshipItemStore = relationshipItemStore;
        this.storeSelector = storeSelector;
    }

    @Override
    public String add(Relationship relationship) throws D2Error {
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
                                        "(" + from + ": " + fromState + ")")
                        .build();
            }
        }
        return relationship.uid();
    }

    @Override
    public ReadWriteObjectRepository<Relationship> uid(String uid) {
        return new RelationshipObjectRepository(store, uid, childrenAppenders, scope, storeSelector);
    }

    private boolean isUpdatableState(State state) {
        return state == State.SYNCED || state == State.TO_POST || state == State.TO_UPDATE;
    }

    private void setToUpdate(StoreWithState store, State state, String elementUid) {
        if (state == State.SYNCED) {
            store.setState(elementUid, State.TO_UPDATE);
        }
    }

    public List<Relationship> getByItem(@NonNull RelationshipItem searchItem) {

        // TODO Create query to avoid retrieving the whole table
        List<RelationshipItem> relationshipItems = this.relationshipItemStore.selectAll();

        List<Relationship> allRelationshipsFromDb = this.store.selectAll();

        List<Relationship> relationships = new ArrayList<>();

        for (RelationshipItem iterationItem : relationshipItems) {
            if (itemComponentsEquals(searchItem, iterationItem)) {
                Relationship relationshipFromDb =
                        UidsHelper.findByUid(allRelationshipsFromDb, iterationItem.relationship().uid());

                if (relationshipFromDb == null) {
                    continue;
                }

                RelationshipConstraintType itemType = iterationItem.relationshipItemType();

                RelationshipItem relatedItem = findRelatedTEI(relationshipItems,
                        iterationItem.relationship().uid(), itemType == FROM ? TO : FROM);

                if (relatedItem == null) {
                    continue;
                }

                RelationshipItem from, to;
                if (itemType == FROM) {
                    from = iterationItem;
                    to = relatedItem;
                } else {
                    from = relatedItem;
                    to = iterationItem;
                }

                Relationship relationship = Relationship.builder()
                        .uid(relationshipFromDb.uid())
                        .relationshipType(relationshipFromDb.relationshipType())
                        .from(from)
                        .to(to)
                        .build();

                relationships.add(relationship);
            }
        }

        return relationships;
    }

    private <O> boolean equalsConsideringNull(@Nullable O a, @Nullable O b) {
        if (a == null) {
            return b == null;
        } else {
            return a.equals(b);
        }
    }

    private boolean itemComponentsEquals(RelationshipItem a, RelationshipItem b) {
        return equalsConsideringNull(a.event(), b.event())
                && equalsConsideringNull(a.enrollment(), b.enrollment())
                && equalsConsideringNull(a.trackedEntityInstance(), b.trackedEntityInstance());
    }

    private RelationshipItem findRelatedTEI(Collection<RelationshipItem> items, String relationshipUid,
                                            RelationshipConstraintType type) {
        for (RelationshipItem item : items) {
            if (relationshipUid.equals(item.relationship().uid()) && item.relationshipItemType() == type) {
                return item;
            }
        }
        return null;
    }

    public StringFilterConnector<RelationshipCollectionRepository> byUid() {
        return cf.string(BaseIdentifiableObjectModel.Columns.UID);
    }

    public StringFilterConnector<RelationshipCollectionRepository> byName() {
        return cf.string(BaseIdentifiableObjectModel.Columns.NAME);
    }

    public DateFilterConnector<RelationshipCollectionRepository> byCreated() {
        return cf.date(BaseIdentifiableObjectModel.Columns.CREATED);
    }

    public DateFilterConnector<RelationshipCollectionRepository> byLastUpdated() {
        return cf.date(BaseIdentifiableObjectModel.Columns.LAST_UPDATED);
    }

    public StringFilterConnector<RelationshipCollectionRepository> byRelationshipType() {
        return cf.string(RelationshipTableInfo.Columns.RELATIONSHIP_TYPE);
    }

    public RelationshipCollectionRepository withItems() {
        return cf.withChild(RelationshipFields.ITEMS);
    }
}