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
package org.hisp.dhis.android.core.arch.repositories.collection

import com.google.common.truth.Truth.assertThat
import org.hisp.dhis.android.core.arch.repositories.collection.RelationshipTypeAsserts.assertTypesWithoutConstraints
import org.hisp.dhis.android.core.data.relationship.RelationshipTypeSamples
import org.hisp.dhis.android.core.relationship.RelationshipType
import org.hisp.dhis.android.core.relationship.RelationshipTypeCollectionRepository
import org.hisp.dhis.android.core.utils.integration.mock.BaseMockIntegrationTestEmptyDispatcher
import org.hisp.dhis.android.core.utils.runner.D2JunitRunner
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(D2JunitRunner::class)
class ReadOnlyCollectionRepositoryImplIntegrationShould : BaseMockIntegrationTestEmptyDispatcher() {
    @Test
    fun get_all_relationship_types_without_children_when_calling_get() {
        val types = relationshipTypeCollectionRepository.blockingGet()

        assertThat(types.size).isEqualTo(2)

        for (targetType in types) {
            val referenceType = typeMap[targetType.uid()]
            assertTypesWithoutConstraints(targetType, referenceType!!)
        }
    }

    @Test
    fun get_all_relationship_types_without_children_when_calling_get_async() {
        val types = relationshipTypeCollectionRepository.get().blockingGet()

        assertThat(types.size).isEqualTo(2)

        for (targetType in types) {
            val referenceType = typeMap[targetType.uid()]
            assertTypesWithoutConstraints(targetType, referenceType!!)
        }
    }

    @Test
    fun get_count_with_unrestricted_scope() {
        val count = relationshipTypeCollectionRepository.blockingCount()

        assertThat(count).isEqualTo(2)
    }

    @Test
    fun get_count_with_restricted_scope() {
        val count = relationshipTypeCollectionRepository
            .byUid().eq(RelationshipTypeSamples.RELATIONSHIP_TYPE_UID_1)
            .blockingCount()

        assertThat(count).isEqualTo(1)
    }

    @Test
    fun get_isEmpty_with_unrestricted_scope() {
        val isEmpty = relationshipTypeCollectionRepository.blockingIsEmpty()

        assertThat(isEmpty).isFalse()
    }

    @Test
    fun get_isEmpty_with_restricted_scope() {
        val isEmpty = relationshipTypeCollectionRepository
            .byUid().eq(RelationshipTypeSamples.RELATIONSHIP_TYPE_UID_1)
            .blockingIsEmpty()

        assertThat(isEmpty).isFalse()
    }

    @Test
    fun get_isEmpty_with_zero_elements_scope() {
        val isEmpty = relationshipTypeCollectionRepository
            .byCode().eq("non-existing")
            .blockingIsEmpty()

        assertThat(isEmpty).isTrue()
    }

    companion object {
        private lateinit var typeMap: Map<String, RelationshipType>
        private lateinit var relationshipTypeCollectionRepository: RelationshipTypeCollectionRepository

        @BeforeClass
        @JvmStatic
        fun setUpTestClass() {
            setUpClass()
            typeMap = RelationshipTypeSamples.typeMap
            val handler = objects.d2DIComponent.relationshipTypeHandler()
            handler.handle(RelationshipTypeSamples.RELATIONSHIP_TYPE_1)
            handler.handle(RelationshipTypeSamples.RELATIONSHIP_TYPE_2)
            relationshipTypeCollectionRepository = d2.relationshipModule().relationshipTypes()
        }
    }
}
