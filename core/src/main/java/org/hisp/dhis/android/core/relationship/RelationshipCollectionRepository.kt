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
package org.hisp.dhis.android.core.relationship

import io.reactivex.Single
import org.hisp.dhis.android.core.arch.db.access.DatabaseAdapter
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.helpers.DateUtils.toJavaDate
import org.hisp.dhis.android.core.arch.helpers.UidGeneratorImpl
import org.hisp.dhis.android.core.arch.repositories.children.internal.ChildrenAppenderGetter
import org.hisp.dhis.android.core.arch.repositories.collection.ReadWriteWithUidCollectionRepository
import org.hisp.dhis.android.core.arch.repositories.collection.internal.BaseReadOnlyWithUidCollectionRepositoryImpl
import org.hisp.dhis.android.core.arch.repositories.filters.internal.DateFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.EnumFilterConnector
import org.hisp.dhis.android.core.arch.repositories.filters.internal.FilterConnectorFactory
import org.hisp.dhis.android.core.arch.repositories.filters.internal.StringFilterConnector
import org.hisp.dhis.android.core.arch.repositories.`object`.ReadWriteObjectRepository
import org.hisp.dhis.android.core.arch.repositories.scope.RepositoryScope
import org.hisp.dhis.android.core.arch.repositories.scope.internal.RepositoryScopeHelper.withUidFilterItem
import org.hisp.dhis.android.core.common.IdentifiableColumns
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.internal.TrackerDataManager
import org.hisp.dhis.android.core.maintenance.D2Error
import org.hisp.dhis.android.core.maintenance.D2ErrorCode
import org.hisp.dhis.android.core.maintenance.D2ErrorComponent
import org.hisp.dhis.android.core.period.clock.internal.ClockProviderFactory.clockProvider
import org.hisp.dhis.android.core.relationship.internal.RelationshipHandler
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemChildrenAppender
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemElementStoreSelector
import org.hisp.dhis.android.core.relationship.internal.RelationshipManager
import org.hisp.dhis.android.core.relationship.internal.RelationshipStore
import org.koin.core.annotation.Singleton

@Singleton
@Suppress("TooManyFunctions")
class RelationshipCollectionRepository internal constructor(
    private val relationshipStore: RelationshipStore,
    databaseAdapter: DatabaseAdapter,
    scope: RepositoryScope,
    private val relationshipHandler: RelationshipHandler,
    private val storeSelector: RelationshipItemElementStoreSelector,
    private val relationshipManager: RelationshipManager,
    private val trackerDataManager: TrackerDataManager,
) : BaseReadOnlyWithUidCollectionRepositoryImpl<Relationship, RelationshipCollectionRepository>(
    relationshipStore,
    databaseAdapter,
    childrenAppenders,
    scope,
    FilterConnectorFactory(
        scope,
    ) { s: RepositoryScope ->
        RelationshipCollectionRepository(
            relationshipStore,
            databaseAdapter,
            s,
            relationshipHandler,
            storeSelector,
            relationshipManager,
            trackerDataManager,
        )
    },
),
    ReadWriteWithUidCollectionRepository<Relationship, Relationship> {
    override fun add(o: Relationship): Single<String> {
        return Single.fromCallable { blockingAdd(o) }
    }

    @Throws(D2Error::class)
    override fun blockingAdd(o: Relationship): String {
        val relationshipWithUid: Relationship
        if (relationshipHandler.doesRelationshipExist(o)) {
            throw D2Error
                .builder()
                .errorComponent(D2ErrorComponent.SDK)
                .errorCode(D2ErrorCode.CANT_CREATE_EXISTING_OBJECT)
                .errorDescription("Tried to create already existing Relationship: $o")
                .build()
        } else if (o.from() == null || o.to() == null) {
            throw D2Error
                .builder()
                .errorComponent(D2ErrorComponent.SDK)
                .errorCode(D2ErrorCode.CANT_CREATE_EXISTING_OBJECT)
                .errorDescription("Relationship is missing either 'from' or 'to' component.")
                .build()
        } else {
            val from = o.from()
            relationshipWithUid = if (o.uid() == null) {
                val generatedUid = UidGeneratorImpl().generate()
                o.toBuilder().uid(generatedUid).build()
            } else {
                o
            }
            val fromStore = storeSelector.getElementStore(from)
            val fromState = fromStore.getSyncState(from!!.elementUid())
            if (isUpdatableState(fromState)) {
                relationshipHandler.handle(relationshipWithUid) { r: Relationship ->
                    r.toBuilder()
                        .syncState(State.TO_POST)
                        .created(clockProvider.clock.now().toJavaDate())
                        .lastUpdated(clockProvider.clock.now().toJavaDate())
                        .deleted(false)
                        .build()
                }
                trackerDataManager.propagateRelationshipUpdate(o, HandleAction.Insert)
            } else {
                throw D2Error
                    .builder()
                    .errorComponent(D2ErrorComponent.SDK)
                    .errorCode(D2ErrorCode.OBJECT_CANT_BE_UPDATED)
                    .errorDescription(
                        "RelationshipItem from doesn't have updatable state: " +
                            "(" + from + ": " + fromState + ")",
                    )
                    .build()
            }
        }
        return relationshipWithUid.uid()!!
    }

    override fun uid(uid: String?): ReadWriteObjectRepository<Relationship> {
        val updatedScope: RepositoryScope = withUidFilterItem(scope, uid)
        return RelationshipObjectRepository(
            relationshipStore,
            uid,
            databaseAdapter,
            childrenAppenders,
            updatedScope,
            trackerDataManager,
        )
    }

    private fun isUpdatableState(state: State?): Boolean {
        return state !== State.RELATIONSHIP
    }

    /**
     * Returns the relationship accessible by the searchItem, it means the searchItem is the owner or the relationship
     * is bidirectional. It does not include deleted relationships.
     * @param searchItem Relationship item
     * @return List of relationships
     */
    fun getByItem(searchItem: RelationshipItem): List<Relationship> {
        return relationshipManager.getByItem(searchItem, includeDeleted = false, onlyAccessible = true)
    }

    /**
     * Returns the relationship accessible by the searchItem, it means the searchItem is the owner or the relationship
     * is bidirectional.
     * @param searchItem Relationship item
     * @param includeDeleted Whether to include deleted relationships or not
     * @return List of relationships
     */
    fun getByItem(searchItem: RelationshipItem, includeDeleted: Boolean): List<Relationship> {
        return relationshipManager.getByItem(searchItem, includeDeleted, true)
    }

    /**
     * Returns the relationship linked to the searchItem.
     * @param searchItem Relationship item
     * @param includeDeleted Whether to include deleted relationships or not
     * @param onlyAccessible Whether to include only accessible relationships (owned relationships or any bidirectional
     * relationship) or all linked relationships
     * @return List of relationships
     */
    fun getByItem(
        searchItem: RelationshipItem,
        includeDeleted: Boolean,
        onlyAccessible: Boolean,
    ): List<Relationship> {
        return relationshipManager.getByItem(searchItem, includeDeleted, onlyAccessible)
    }

    fun byUid(): StringFilterConnector<RelationshipCollectionRepository> {
        return cf.string(IdentifiableColumns.UID)
    }

    fun byName(): StringFilterConnector<RelationshipCollectionRepository> {
        return cf.string(IdentifiableColumns.NAME)
    }

    fun byCreated(): DateFilterConnector<RelationshipCollectionRepository> {
        return cf.date(IdentifiableColumns.CREATED)
    }

    fun byLastUpdated(): DateFilterConnector<RelationshipCollectionRepository> {
        return cf.date(IdentifiableColumns.LAST_UPDATED)
    }

    fun byRelationshipType(): StringFilterConnector<RelationshipCollectionRepository> {
        return cf.string(RelationshipTableInfo.Columns.RELATIONSHIP_TYPE)
    }

    fun bySyncState(): EnumFilterConnector<RelationshipCollectionRepository, State> {
        return cf.enumC(RelationshipTableInfo.Columns.SYNC_STATE)
    }

    fun withItems(): RelationshipCollectionRepository {
        return cf.withChild(RelationshipFields.ITEMS)
    }

    internal companion object {
        val childrenAppenders: ChildrenAppenderGetter<Relationship> = mapOf(
            RelationshipFields.ITEMS to ::RelationshipItemChildrenAppender,
        )
    }
}
