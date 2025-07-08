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
package org.hisp.dhis.android.core.relationship.internal

import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.data.database.ObjectWithoutUidStoreAbstractIntegrationShould
import org.hisp.dhis.android.core.data.relationship.RelationshipItemSamples
import org.hisp.dhis.android.core.relationship.RelationshipItem
import org.hisp.dhis.android.core.relationship.RelationshipItemEvent
import org.hisp.dhis.android.core.utils.integration.mock.TestDatabaseAdapterFactory
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.hisp.dhis.android.persistence.relationship.RelationshipItemStoreImpl
import org.hisp.dhis.android.persistence.relationship.RelationshipItemTableInfo
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class RelationshipItemStoreIntegrationShould : ObjectWithoutUidStoreAbstractIntegrationShould<RelationshipItem>(
    RelationshipItemStoreImpl(TestDatabaseAdapterFactory.get()),
    RelationshipItemTableInfo.TABLE_INFO,
    TestDatabaseAdapterFactory.get(),
) {

    private val relationshipItemStore = store as RelationshipItemStore

    override fun buildObject(): RelationshipItem {
        return RelationshipItemSamples.getRelationshipItem()
    }

    override fun buildObjectToUpdate(): RelationshipItem {
        return RelationshipItemSamples.getRelationshipItem().toBuilder()
            .event(RelationshipItemEvent.builder().event("new_event").build())
            .build()
    }

    @Test
    fun get_by_entity_uid() = runTest {
        val sample = RelationshipItemSamples.getRelationshipItem()
        relationshipItemStore.insert(RelationshipItemSamples.getRelationshipItem())

        val itemsByExistingEntityUid = relationshipItemStore.getByEntityUid(sample.event()!!.event())
        assertThat(itemsByExistingEntityUid.size).isEqualTo(1)

        val itemsByNonExistingEntityUid = relationshipItemStore.getByEntityUid("other_entity")
        assertThat(itemsByNonExistingEntityUid).isEmpty()

        relationshipItemStore.delete()
    }
}
