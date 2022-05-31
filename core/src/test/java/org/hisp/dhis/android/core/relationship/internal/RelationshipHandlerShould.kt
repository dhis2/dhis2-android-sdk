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
package org.hisp.dhis.android.core.relationship.internal

import com.nhaarman.mockitokotlin2.*
import org.hisp.dhis.android.core.arch.db.stores.internal.StoreWithState
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.relationship.RelationshipConstraintType
import org.hisp.dhis.android.core.relationship.RelationshipHelper
import org.hisp.dhis.android.core.relationship.RelationshipItem
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class RelationshipHandlerShould {
    private val relationshipStore: RelationshipStore = mock()

    private val relationshipItemStore: RelationshipItemStore = mock()

    private val relationshipItemHandler: Handler<RelationshipItem> = mock()

    private val storeSelector: RelationshipItemElementStoreSelector = mock()

    private val itemElementStore: StoreWithState<*> = mock()

    private val NEW_UID = "new-uid"
    private val TEI_3_UID = "tei3"
    private val TEI_4_UID = "tei4"
    private val tei3Item: RelationshipItem = RelationshipHelper.teiItem(TEI_3_UID)
    private val tei4Item: RelationshipItem = RelationshipHelper.teiItem(TEI_4_UID)
    private val existingRelationship: Relationship = RelationshipSamples.get230()
    private val existingRelationshipWithNewUid: Relationship =
        RelationshipSamples.get230().toBuilder().uid(NEW_UID).build()
    private val newRelationship: Relationship = RelationshipSamples.get230(NEW_UID, TEI_3_UID, TEI_4_UID)

    // object to test
    private lateinit var relationshipHandler: RelationshipHandlerImpl

    @Before
    fun setUp() {
        relationshipHandler = RelationshipHandlerImpl(
            relationshipStore, relationshipItemStore,
            relationshipItemHandler, storeSelector
        )
        whenever(storeSelector.getElementStore(any())).thenReturn(itemElementStore)
        whenever(itemElementStore.exists(RelationshipSamples.FROM_UID)).thenReturn(true)
        whenever(itemElementStore.exists(RelationshipSamples.TO_UID)).thenReturn(true)
        whenever(itemElementStore.exists(TEI_3_UID)).thenReturn(true)
        whenever(itemElementStore.exists(TEI_4_UID)).thenReturn(true)
        whenever(
            relationshipItemStore.getRelationshipUidsForItems(
                RelationshipSamples.fromItem, RelationshipSamples.toItem
            )
        ).thenReturn(listOf(RelationshipSamples.UID))
        whenever(relationshipItemStore.getRelationshipUidsForItems(tei3Item, tei4Item)).thenReturn(emptyList())
        whenever(relationshipStore.selectByUid(RelationshipSamples.UID)).thenReturn(RelationshipSamples.get230())
        whenever(relationshipStore.updateOrInsert(any())).thenReturn(HandleAction.Insert)
    }

    @Test
    fun not_delete_relationship_if_existing_id_matches() {
        relationshipHandler.handle(existingRelationship)
        verify(relationshipStore, never()).delete(RelationshipSamples.UID)
    }

    @Test
    fun not_call_delete_if_no_existing_relationship() {
        relationshipHandler.handle(newRelationship)
        verify(relationshipStore, never()).delete(RelationshipSamples.UID)
    }

    @Test
    fun update_relationship_store_for_existing_relationship() {
        relationshipHandler.handle(existingRelationship)
        verify(relationshipStore).updateOrInsert(existingRelationship)
    }

    @Test
    fun update_relationship_store_for_existing_relationship_with_new_uid() {
        relationshipHandler.handle(existingRelationshipWithNewUid)
        verify(relationshipStore).updateOrInsert(existingRelationshipWithNewUid)
    }

    @Test
    fun update_relationship_store_for_new_relationship() {
        relationshipHandler.handle(newRelationship)
        verify(relationshipStore).updateOrInsert(newRelationship)
    }

    @Test
    fun update_relationship_handler_store_for_existing_relationship() {
        relationshipHandler.handle(existingRelationship)
        verify(relationshipItemHandler).handle(
            RelationshipSamples.fromItem.toBuilder().relationship(ObjectWithUid.create(existingRelationship.uid()))
                .relationshipItemType(RelationshipConstraintType.FROM).build()
        )
        verify(relationshipItemHandler).handle(
            RelationshipSamples.toItem.toBuilder().relationship(ObjectWithUid.create(existingRelationship.uid()))
                .relationshipItemType(RelationshipConstraintType.TO).build()
        )
    }

    @Test
    fun update_relationship_item_handler_for_existing_relationship_with_new_uid() {
        relationshipHandler.handle(existingRelationshipWithNewUid)
        verify(relationshipItemHandler).handle(
            RelationshipSamples.fromItem.toBuilder()
                .relationship(ObjectWithUid.create(existingRelationshipWithNewUid.uid()))
                .relationshipItemType(RelationshipConstraintType.FROM).build()
        )
        verify(relationshipItemHandler).handle(
            RelationshipSamples.toItem.toBuilder()
                .relationship(ObjectWithUid.create(existingRelationshipWithNewUid.uid()))
                .relationshipItemType(RelationshipConstraintType.TO).build()
        )
    }

    @Test
    fun update_relationship_item_handler_for_new_relationship() {
        relationshipHandler.handle(newRelationship)
        verify(relationshipItemHandler).handle(
            tei3Item.toBuilder().relationship(ObjectWithUid.create(newRelationship.uid()))
                .relationshipItemType(RelationshipConstraintType.FROM).build()
        )
        verify(relationshipItemHandler).handle(
            tei4Item.toBuilder().relationship(ObjectWithUid.create(newRelationship.uid()))
                .relationshipItemType(RelationshipConstraintType.TO).build()
        )
    }
}
