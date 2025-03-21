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

import com.google.common.truth.Truth
import org.hisp.dhis.android.core.common.BaseNameableObject
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestFullDispatcher
import org.junit.Test

class RelationshipCollectionRepositoryMockIntegrationShould : BaseMockIntegrationTestFullDispatcher() {
    @Test
    fun find_all() {
        val relationships = d2.relationshipModule().relationships()
            .blockingGet()

        Truth.assertThat(relationships.size).isEqualTo(3)
    }

    @Test
    fun filter_by_uid() {
        val relationships = d2.relationshipModule().relationships()
            .byUid().eq("AJOytZW7OaI")
            .blockingGet()

        Truth.assertThat(relationships.size).isEqualTo(1)
    }

    @Test
    fun filter_by_name() {
        val relationships = d2.relationshipModule().relationships()
            .byName().eq("Lab Sample to Person")
            .blockingGet()

        Truth.assertThat(relationships.size).isEqualTo(1)
    }

    @Test
    fun filter_by_created() {
        val relationships = d2.relationshipModule().relationships()
            .byCreated().eq(BaseNameableObject.DATE_FORMAT.parse("2019-02-07T08:06:28.369"))
            .blockingGet()

        Truth.assertThat(relationships.size).isEqualTo(1)
    }

    @Test
    fun filter_by_last_updated() {
        val relationships = d2.relationshipModule().relationships()
            .byLastUpdated().eq(BaseNameableObject.DATE_FORMAT.parse("2018-02-07T08:06:28.369"))
            .blockingGet()

        Truth.assertThat(relationships.size).isEqualTo(1)
    }

    @Test
    fun filter_by_relationship_type() {
        val relationships = d2.relationshipModule().relationships()
            .byRelationshipType().eq("V2kkHafqs8G")
            .blockingGet()

        Truth.assertThat(relationships.size).isEqualTo(1)
    }

    @Test
    fun filter_by_sync_state() {
        val relationships = d2.relationshipModule().relationships()
            .bySyncState().eq(State.SYNCED)
            .blockingGet()

        Truth.assertThat(relationships.size).isEqualTo(3)
    }

    @Test
    fun get_by_item() {
        val item = RelationshipItem.builder().trackedEntityInstance(
            RelationshipItemTrackedEntityInstance.builder().trackedEntityInstance("nWrB0TfWlvh").build(),
        ).build()
        val relationships = d2.relationshipModule().relationships()
            .getByItem(item)

        Truth.assertThat(relationships.size).isEqualTo(1)
    }

    @Test
    fun get_by_item_including_deleted() {
        val item = RelationshipItem.builder().trackedEntityInstance(
            RelationshipItemTrackedEntityInstance.builder().trackedEntityInstance("nWrB0TfWlvh")
                .build(),
        ).build()
        val relationships = d2.relationshipModule().relationships().getByItem(item, true)
        Truth.assertThat(relationships.size).isEqualTo(1)
    }

    @Test
    fun get_by_item_including_all_linked() {
        val item = RelationshipItem.builder().trackedEntityInstance(
            RelationshipItemTrackedEntityInstance.builder().trackedEntityInstance("nWrB0TfWlvh")
                .build(),
        ).build()
        val relationships = d2.relationshipModule().relationships().getByItem(
            item,
            includeDeleted = false,
            onlyAccessible = false,
        )
        Truth.assertThat(relationships.size).isEqualTo(2)
    }

    @Test
    fun filter_by_item() {
        val item = RelationshipItem.builder()
            .trackedEntityInstance(
                RelationshipItemTrackedEntityInstance.builder().trackedEntityInstance("nWrB0TfWlvh")
                    .build(),
            )
            .relationshipItemType(RelationshipConstraintType.FROM)
            .build()

        val relationships = d2.relationshipModule().relationships()
            .byItem(item)
            .blockingGet()

        Truth.assertThat(relationships.size).isEqualTo(1)
    }

    @Test
    fun filter_by_object_repository() {
        val relationship = d2.relationshipModule().relationships()
            .uid("AJOytZW7OaB")
            .blockingGet()

        Truth.assertThat(relationship!!.uid()).isEqualTo("AJOytZW7OaB")
    }
}
