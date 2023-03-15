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
package org.hisp.dhis.android.core.relationship;

import androidx.annotation.NonNull;

import org.hisp.dhis.android.core.arch.db.stores.internal.StoreWithState;
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction;
import org.hisp.dhis.android.core.arch.helpers.UidGeneratorImpl;
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppender;
import org.hisp.dhis.android.core.arch.repositories.collection.ReadWriteWithUidCollectionRepository;
import org.hisp.dhis.android.core.arch.repositories.collection.internal.BaseReadOnlyWithUidCollectionRepositoryImpl;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.DateFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory;
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector;
import org.hisp.dhis.android.core.arch.repositories.object.ReadWriteObjectRepository;
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope;
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeHelper;
import org.hisp.dhis.android.core.common.IdentifiableColumns;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.common.internal.TrackerDataManager;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.maintenance.D2ErrorCode;
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent;
import org.hisp.dhis.android.core.relationship.internal.RelationshipHandler;
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemElementStoreSelector;
import org.hisp.dhis.android.core.relationship.internal.RelationshipManager;
import org.hisp.dhis.android.core.relationship.internal.RelationshipStore;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.Reusable;
import io.reactivex.Single;

@Reusable
public class RelationshipCollectionRepository
        extends BaseReadOnlyWithUidCollectionRepositoryImpl<Relationship, RelationshipCollectionRepository>
        implements ReadWriteWithUidCollectionRepository<Relationship, Relationship> {

    private final RelationshipStore store;
    private final RelationshipHandler relationshipHandler;
    private final RelationshipItemElementStoreSelector storeSelector;
    private final RelationshipManager relationshipManager;
    private final TrackerDataManager trackerDataManager;

    @Inject
    RelationshipCollectionRepository(final RelationshipStore store,
                                     final Map<String, ChildrenAppender<Relationship>> childrenAppenders,
                                     final RepositoryScope scope,
                                     final RelationshipHandler relationshipHandler,
                                     final RelationshipItemElementStoreSelector storeSelector,
                                     final RelationshipManager relationshipManager,
                                     final TrackerDataManager trackerDataManager) {
        super(store, childrenAppenders, scope, new FilterConnectorFactory<>(scope,
                s -> new RelationshipCollectionRepository(store, childrenAppenders, s,
                        relationshipHandler, storeSelector, relationshipManager, trackerDataManager)));
        this.store = store;
        this.relationshipHandler = relationshipHandler;
        this.storeSelector = storeSelector;
        this.relationshipManager = relationshipManager;
        this.trackerDataManager = trackerDataManager;
    }

    @Override
    public Single<String> add(Relationship relationship) {
        return Single.fromCallable(() -> blockingAdd(relationship));
    }

    @Override
    public String blockingAdd(Relationship relationship) throws D2Error {
        Relationship relationshipWithUid;
        if (relationshipHandler.doesRelationshipExist(relationship)) {
            throw D2Error
                    .builder()
                    .errorComponent(D2ErrorComponent.SDK)
                    .errorCode(D2ErrorCode.CANT_CREATE_EXISTING_OBJECT)
                    .errorDescription("Tried to create already existing Relationship: " + relationship)
                    .build();
        } else if (relationship.from() == null || relationship.to() == null) {
            throw D2Error
                    .builder()
                    .errorComponent(D2ErrorComponent.SDK)
                    .errorCode(D2ErrorCode.CANT_CREATE_EXISTING_OBJECT)
                    .errorDescription("Relationship is missing either 'from' or 'to' component.")
                    .build();
        } else {
            RelationshipItem from = relationship.from();
            if (relationship.uid() == null) {
                String generatedUid = new UidGeneratorImpl().generate();
                relationshipWithUid = relationship.toBuilder().uid(generatedUid).build();
            } else {
                relationshipWithUid = relationship;
            }

            StoreWithState fromStore = storeSelector.getElementStore(from);
            State fromState = fromStore.getSyncState(from.elementUid());

            if (isUpdatableState(fromState)) {
                relationshipHandler.handle(relationshipWithUid, r -> r.toBuilder()
                        .syncState(State.TO_POST)
                        .deleted(false)
                        .build());
                trackerDataManager.propagateRelationshipUpdate(relationship, HandleAction.Insert);
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
        return relationshipWithUid.uid();
    }

    @Override
    public ReadWriteObjectRepository<Relationship> uid(String uid) {
        RepositoryScope updatedScope = RepositoryScopeHelper.withUidFilterItem(scope, uid);
        return new RelationshipObjectRepository(store, uid, childrenAppenders, updatedScope, trackerDataManager);
    }

    private boolean isUpdatableState(State state) {
        return state != State.RELATIONSHIP;
    }

    /**
     * Returns the relationship accessible by the searchItem, it means the searchItem is the owner or the relationship
     * is bidirectional. It does not include deleted relationships.
     * @param searchItem Relationship item
     * @return List of relationships
     */
    public List<Relationship> getByItem(@NonNull RelationshipItem searchItem) {
        return relationshipManager.getByItem(searchItem, false, true);
    }

    /**
     * Returns the relationship accessible by the searchItem, it means the searchItem is the owner or the relationship
     * is bidirectional.
     * @param searchItem Relationship item
     * @param includeDeleted Whether to include deleted relationships or not
     * @return List of relationships
     */
    public List<Relationship> getByItem(@NonNull RelationshipItem searchItem, Boolean includeDeleted) {
        return relationshipManager.getByItem(searchItem, includeDeleted, true);
    }

    /**
     * Returns the relationship linked to the searchItem.
     * @param searchItem Relationship item
     * @param includeDeleted Whether to include deleted relationships or not
     * @param onlyAccessible Whether to include only accessible relationships (owned relationships or any bidirectional
     *                       relationship) or all linked relationships
     * @return List of relationships
     */
    public List<Relationship> getByItem(@NonNull RelationshipItem searchItem, Boolean includeDeleted,
                                        Boolean onlyAccessible) {
        return relationshipManager.getByItem(searchItem, includeDeleted, onlyAccessible);
    }

    public StringFilterConnector<RelationshipCollectionRepository> byUid() {
        return cf.string(IdentifiableColumns.UID);
    }

    public StringFilterConnector<RelationshipCollectionRepository> byName() {
        return cf.string(IdentifiableColumns.NAME);
    }

    public DateFilterConnector<RelationshipCollectionRepository> byCreated() {
        return cf.date(IdentifiableColumns.CREATED);
    }

    public DateFilterConnector<RelationshipCollectionRepository> byLastUpdated() {
        return cf.date(IdentifiableColumns.LAST_UPDATED);
    }

    public StringFilterConnector<RelationshipCollectionRepository> byRelationshipType() {
        return cf.string(RelationshipTableInfo.Columns.RELATIONSHIP_TYPE);
    }

    public EnumFilterConnector<RelationshipCollectionRepository, State> bySyncState() {
        return cf.enumC(RelationshipTableInfo.Columns.SYNC_STATE);
    }

    public RelationshipCollectionRepository withItems() {
        return cf.withChild(RelationshipFields.ITEMS);
    }
}