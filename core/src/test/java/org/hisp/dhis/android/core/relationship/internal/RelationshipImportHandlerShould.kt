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

import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.common.internal.DataStatePropagator
import org.hisp.dhis.android.core.imports.ImportStatus
import org.hisp.dhis.android.core.imports.internal.ImportConflict
import org.hisp.dhis.android.core.imports.internal.RelationshipImportSummary
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.relationship.RelationshipCollectionRepository
import org.hisp.dhis.android.core.relationship.RelationshipItem
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Answers
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.*

@RunWith(JUnit4::class)
class RelationshipImportHandlerShould {

    private val relationshipStore: RelationshipStore = mock()

    private val dataStatePropagator: DataStatePropagator = mock()

    private val relationshipCollectionRepository: RelationshipCollectionRepository =
        mock(defaultAnswer = Answers.RETURNS_DEEP_STUBS)

    private val importSummary: RelationshipImportSummary = mock()

    private val relationship: Relationship = mock()
    private val relationshipItem: RelationshipItem = mock()

    private val relationships: List<Relationship> = emptyList()

    // object to test
    private lateinit var relationshipImportHandler: RelationshipImportHandler

    @Before
    @Throws(Exception::class)
    fun setUp() {
        relationshipImportHandler =
            RelationshipImportHandler(relationshipStore, dataStatePropagator, relationshipCollectionRepository)

        whenever(relationshipCollectionRepository.withItems().uid(any()).blockingGet()).doReturn(relationship)
        whenever(relationship.from()).doReturn(relationshipItem)
    }

    @Test
    fun do_nothing_when_passing_null_argument() = runTest {
        relationshipImportHandler.handleRelationshipImportSummaries(null, relationships)

        verify(relationshipStore, never()).setSyncStateOrDelete(anyString(), any())
        verify(dataStatePropagator, never()).propagateRelationshipUpdate(any())
    }

    @Test
    fun setStatus_shouldUpdateRelationshipStatusSuccess() = runTest {
        whenever(importSummary.status()).doReturn(ImportStatus.SUCCESS)
        whenever(importSummary.reference()).doReturn("test_uid")

        relationshipImportHandler.handleRelationshipImportSummaries(
            listOf(importSummary),
            relationships,
        )

        verify(relationshipStore, times(1)).setSyncStateOrDelete("test_uid", State.SYNCED)
        verify(dataStatePropagator, times(1)).propagateRelationshipUpdate(any())
    }

    @Test
    fun setStatus_shouldUpdateRelationshipStatusError() = runTest {
        whenever(importSummary.status()).doReturn(ImportStatus.ERROR)
        whenever(importSummary.reference()).doReturn("test_uid")

        relationshipImportHandler.handleRelationshipImportSummaries(
            listOf(importSummary),
            relationships,
        )

        verify(relationshipStore, times(1)).setSyncStateOrDelete("test_uid", State.TO_UPDATE)
        verify(dataStatePropagator, times(1)).propagateRelationshipUpdate(any())
    }

    @Test
    fun mark_as_to_update_relationships_not_present_in_the_response() = runTest {
        whenever(importSummary.status()).doReturn(ImportStatus.SUCCESS)
        whenever(importSummary.reference()).doReturn("test_uid")

        val relationships = listOf(relationship)
        whenever(relationship.uid()).doReturn("missing_uid")

        relationshipImportHandler.handleRelationshipImportSummaries(
            listOf(importSummary),
            relationships,
        )

        verify(relationshipStore, times(1)).setSyncStateOrDelete("test_uid", State.SYNCED)
        verify(relationshipStore, times(1)).setSyncStateOrDelete("missing_uid", State.TO_UPDATE)

        verify(dataStatePropagator, times(2)).propagateRelationshipUpdate(any())
    }

    @Test
    fun delete_relationship_when_not_found_on_server_from_conflicts() = runTest {
        val testUid = "test_uid000"
        val relationshipNotFoundConflict = ImportConflict.create(
            testUid,
            "Relationship '$testUid' not found.",
        )

        whenever(importSummary.status()).thenReturn(ImportStatus.ERROR)
        whenever(importSummary.reference()).thenReturn(testUid)
        whenever(importSummary.conflicts()).thenReturn(listOf(relationshipNotFoundConflict))
        whenever(importSummary.description()).thenReturn(null)
        whenever(relationshipStore.deleteByEntity(any())).thenReturn(true)

        relationshipImportHandler.handleRelationshipImportSummaries(
            listOf(importSummary),
            relationships,
        )

        verify(relationshipStore, times(1)).deleteByEntity(relationship)
        verify(relationshipStore, never()).setSyncStateOrDelete(anyString(), any())
        verify(dataStatePropagator, never()).propagateRelationshipUpdate(any())
    }

    @Test
    fun delete_relationship_when_already_deleted_on_server_from_description() = runTest {
        val testUid = "test_uid000"
        whenever(importSummary.status()).thenReturn(ImportStatus.ERROR)
        whenever(importSummary.reference()).thenReturn(testUid)
        whenever(importSummary.description()).thenReturn(
            "Relationship '$testUid' is already deleted and cannot be modified.",
        )
        whenever(importSummary.conflicts()).thenReturn(emptyList())
        whenever(relationshipStore.deleteByEntity(any())).thenReturn(true)

        relationshipImportHandler.handleRelationshipImportSummaries(
            listOf(importSummary),
            relationships,
        )

        verify(relationshipStore, times(1)).deleteByEntity(relationship)
        verify(relationshipStore, never()).setSyncStateOrDelete(anyString(), any())
        verify(dataStatePropagator, never()).propagateRelationshipUpdate(any())
    }
}
