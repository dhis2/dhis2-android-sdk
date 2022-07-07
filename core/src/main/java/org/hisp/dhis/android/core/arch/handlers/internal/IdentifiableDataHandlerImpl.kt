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
package org.hisp.dhis.android.core.arch.handlers.internal

import org.hisp.dhis.android.core.arch.db.querybuilders.internal.WhereClauseBuilder
import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableDataObjectStore
import org.hisp.dhis.android.core.arch.helpers.CollectionsHelper
import org.hisp.dhis.android.core.arch.helpers.UidsHelper.getUidsList
import org.hisp.dhis.android.core.common.*
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.relationship.internal.RelationshipDHISVersionManager
import org.hisp.dhis.android.core.relationship.internal.RelationshipHandler
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemRelatives

@Suppress("TooManyFunctions")
internal abstract class IdentifiableDataHandlerImpl<O>(
    val store: IdentifiableDataObjectStore<O>,
    private val relationshipVersionManager: RelationshipDHISVersionManager,
    private val relationshipHandler: RelationshipHandler
) : IdentifiableDataHandler<O> where O : DeletableDataObject, O : ObjectWithUidInterface {

    @JvmSuppressWildcards
    protected fun handle(
        o: O?,
        transformer: (O) -> O,
        oTransformedCollection: MutableList<O>,
        params: IdentifiableDataHandlerParams
    ) {
        if (o == null) {
            return
        }
        val oTransformed = handleInternal(o, transformer, params)
        oTransformedCollection.add(oTransformed)
    }

    @JvmSuppressWildcards
    protected fun handle(
        o: O?,
        transformer: (O) -> O,
        oTransformedCollection: MutableList<O>,
        params: IdentifiableDataHandlerParams,
        relatives: RelationshipItemRelatives?
    ) {
        if (o == null) {
            return
        }
        val oTransformed = handleInternal(o, transformer, params, relatives)
        oTransformedCollection.add(oTransformed)
    }

    private fun handleInternal(o: O, transformer: (O) -> O, params: IdentifiableDataHandlerParams): O {
        val o2 = beforeObjectHandled(o, params)
        val o3 = transformer(o2)
        val action = deleteOrPersist(o3)
        afterObjectHandled(o3, action, params, null)
        return o3
    }

    private fun handleInternal(
        o: O,
        transformer: (O) -> O,
        params: IdentifiableDataHandlerParams,
        relatives: RelationshipItemRelatives?
    ): O {
        val o2 = beforeObjectHandled(o, params)
        val o3 = transformer(o2)
        val action = deleteOrPersist(o3)
        afterObjectHandled(o3, action, params, relatives)
        return o3
    }

    @JvmSuppressWildcards
    override fun handleMany(
        oCollection: Collection<O>?,
        params: IdentifiableDataHandlerParams,
        relatives: RelationshipItemRelatives?
    ) {
        if (oCollection == null) {
            return
        }
        val transformer =
            if (params.asRelationship) {
                relationshipTransformer()
            } else {
                { o: O -> addSyncedState(o) }
            }
        val preHandledCollection = beforeCollectionHandled(oCollection, params)
        val transformedCollection: MutableList<O> = ArrayList(preHandledCollection.size)
        for (o in preHandledCollection) {
            handle(o, transformer, transformedCollection, params, relatives)
        }
        afterCollectionHandled(transformedCollection, params)
    }

    private fun relationshipTransformer(): (O) -> O {
        return { o: O ->
            val currentState = store.getSyncState(o.uid())
            if (currentState == State.RELATIONSHIP || currentState == null) {
                addRelationshipState(o)
            } else {
                o
            }
        }
    }

    @JvmSuppressWildcards
    protected fun handleRelationships(
        relationships: Collection<Relationship>,
        parent: ObjectWithUidInterface,
        relatives: RelationshipItemRelatives?
    ) {
        val ownedRelationships = relationshipVersionManager.getOwnedRelationships(relationships, parent.uid())

        if (relatives != null) {
            relationshipVersionManager.saveRelativesIfNotExist(
                ownedRelationships, parent.uid(), relatives, relationshipHandler
            )
        }
        relationshipHandler.handleMany(
            ownedRelationships
        ) { relationship: Relationship ->
            relationship.toBuilder()
                .syncState(State.SYNCED)
                .deleted(false)
                .build()!!
        }
    }

    protected fun deleteLinkedRelationships(o: O) {
        o.uid()?.let { relationshipHandler.deleteLinkedRelationships(it) }
    }

    protected abstract fun addRelationshipState(o: O): O
    protected abstract fun addSyncedState(o: O): O
    protected fun deleteOrPersist(o: O): HandleAction {
        val modelUid = o.uid()
        return if ((CollectionsHelper.isDeleted(o) || deleteIfCondition(o)) && modelUid != null) {
            deleteLinkedRelationships(o)
            store.deleteIfExists(modelUid)
            HandleAction.Delete
        } else {
            store.updateOrInsert(o)
        }
    }

    protected open fun deleteIfCondition(o: O): Boolean {
        return false
    }

    protected open fun beforeObjectHandled(o: O, params: IdentifiableDataHandlerParams): O {
        return o
    }

    protected abstract fun afterObjectHandled(
        o: O,
        action: HandleAction?,
        params: IdentifiableDataHandlerParams,
        relatives: RelationshipItemRelatives?
    )

    protected fun beforeCollectionHandled(
        oCollection: Collection<O>,
        params: IdentifiableDataHandlerParams
    ): Collection<O> {
        return when {
            params.overwrite -> {
                oCollection
            }
            params.asRelationship -> {
                removeAllowedExistingObjects(
                    oCollection,
                    listOf(State.RELATIONSHIP.name)
                )
            }
            else -> {
                removeAllowedExistingObjects(
                    oCollection,
                    listOf(
                        State.SYNCED.name,
                        State.RELATIONSHIP.name,
                        State.SYNCED_VIA_SMS.name
                    )
                )
            }
        }
    }

    protected fun afterCollectionHandled(oCollection: Collection<O>?, params: IdentifiableDataHandlerParams) {
        /* Method is not abstract since empty action is the default action and we don't want it to
         * be unnecessarily written in every child.
         */
    }

    private fun removeAllowedExistingObjects(os: Collection<O>, allowedStates: List<String>): Collection<O> {
        val storedObjectUids = storedObjectUids(os)
        val allowedObjectUids = objectWithStatesUids(storedObjectUids, allowedStates)
        val objectsToStore: MutableList<O> = ArrayList()
        for (o in os) {
            if (!storedObjectUids.contains(o.uid()) || allowedObjectUids.contains(o.uid()) ||
                CollectionsHelper.isDeleted(o)
            ) {
                objectsToStore.add(o)
            }
        }
        return objectsToStore
    }

    private fun storedObjectUids(os: Collection<O>): List<String> {
        val objectUids = getUidsList(os)
        val storedObjectUidsWhereClause = WhereClauseBuilder()
            .appendInKeyStringValues(IdentifiableColumns.UID, objectUids).build()
        return store.selectUidsWhere(storedObjectUidsWhereClause)
    }

    private fun objectWithStatesUids(storedObjectUids: List<String>, states: List<String>): List<String> {
        if (storedObjectUids.isNotEmpty()) {
            val syncedObjectUidsWhereClause2 = WhereClauseBuilder()
                .appendInKeyStringValues(IdentifiableColumns.UID, storedObjectUids)
                .appendInKeyStringValues(DataColumns.SYNC_STATE, states)
                .appendInKeyStringValues(DataColumns.AGGREGATED_SYNC_STATE, states)
                .build()
            return store.selectUidsWhere(syncedObjectUidsWhereClause2)
        }
        return ArrayList()
    }
}
