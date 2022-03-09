/*
 *  Copyright (c) 2004-2021, University of Oslo
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
package org.hisp.dhis.android.core.trackedentity.internal

import com.nhaarman.mockitokotlin2.*
import java.lang.Boolean
import kotlin.Exception
import kotlin.Throws
import org.hisp.dhis.android.core.arch.cleaners.internal.OrphanCleaner
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.handlers.internal.HandlerWithTransformer
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableDataHandler
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableDataHandlerParams
import org.hisp.dhis.android.core.common.State
import org.hisp.dhis.android.core.enrollment.Enrollment
import org.hisp.dhis.android.core.relationship.Relationship
import org.hisp.dhis.android.core.relationship.RelationshipHelper
import org.hisp.dhis.android.core.relationship.internal.Relationship229Compatible
import org.hisp.dhis.android.core.relationship.internal.RelationshipDHISVersionManager
import org.hisp.dhis.android.core.relationship.internal.RelationshipHandler
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemRelatives
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers

@RunWith(JUnit4::class)
class TrackedEntityInstanceHandlerShould {
    private val relationshipVersionManager: RelationshipDHISVersionManager = mock()
    private val relationshipHandler: RelationshipHandler = mock()
    private val trackedEntityInstanceStore: TrackedEntityInstanceStore = mock()
    private val trackedEntityAttributeValueStore: TrackedEntityAttributeValueStore = mock()
    private val trackedEntityAttributeValueHandler: HandlerWithTransformer<TrackedEntityAttributeValue> = mock()
    private val enrollmentHandler: IdentifiableDataHandler<Enrollment> = mock()

    private val trackedEntityInstance: TrackedEntityInstance = mock()
    private val enrollment: Enrollment = mock()
    private val relationship229Compatible: Relationship229Compatible = mock()
    private val relationshipItemRelatives: RelationshipItemRelatives = mock()
    private val relationship: Relationship = mock()
    private val relative: TrackedEntityInstance = mock()
    private val relativeBuilder: TrackedEntityInstance.Builder = mock()
    private val enrollmentCleaner: OrphanCleaner<TrackedEntityInstance, Enrollment> = mock()
    private val relationshipCleaner: OrphanCleaner<TrackedEntityInstance, Relationship229Compatible> = mock()
    private val relatives: RelationshipItemRelatives = mock()
    private val teiBuilder: TrackedEntityInstance.Builder = mock()

    // Constants
    private val TEI_UID = "test_tei_uid"
    private val RELATIVE_UID = "relative_uid"
    private val RELATIONSHIP_TYPE = "type_uid"

    // object to test
    private lateinit var trackedEntityInstanceHandler: TrackedEntityInstanceHandler

    @Before
    @Throws(Exception::class)
    fun setUp() {
        whenever(trackedEntityInstance.uid()).doReturn(TEI_UID)
        whenever(trackedEntityInstance.toBuilder()).doReturn(teiBuilder)
        whenever(teiBuilder.syncState(State.SYNCED)).thenReturn(teiBuilder)
        whenever(teiBuilder.aggregatedSyncState(State.SYNCED)).thenReturn(teiBuilder)
        whenever(teiBuilder.build()).thenReturn(trackedEntityInstance)
        whenever(TrackedEntityInstanceInternalAccessor.accessEnrollments(trackedEntityInstance))
            .thenReturn(listOf(enrollment))
        whenever(TrackedEntityInstanceInternalAccessor.accessRelationships(trackedEntityInstance))
            .thenReturn(listOf(relationship229Compatible))
        whenever(relationshipVersionManager.from229Compatible(listOf(relationship229Compatible)))
            .thenReturn(listOf(relationship))
        whenever(relationship.relationshipType()).thenReturn(RELATIONSHIP_TYPE)
        whenever(relationship.from()).thenReturn(RelationshipHelper.teiItem(TEI_UID))
        whenever(relationship.to()).thenReturn(RelationshipHelper.teiItem(RELATIVE_UID))
        whenever(relative.uid()).thenReturn(RELATIVE_UID)
        whenever(trackedEntityInstanceStore.updateOrInsert(ArgumentMatchers.any()))
            .thenReturn(HandleAction.Insert)

        trackedEntityInstanceHandler = TrackedEntityInstanceHandler(
            relationshipVersionManager,
            relationshipHandler,
            trackedEntityInstanceStore,
            trackedEntityAttributeValueStore,
            trackedEntityAttributeValueHandler,
            enrollmentHandler,
            enrollmentCleaner,
            relationshipCleaner
        )
    }

    @Test
    fun do_nothing_when_passing_null_argument() {
        val params = IdentifiableDataHandlerParams(true, false, false, false)
        trackedEntityInstanceHandler.handleMany(null, params, relationshipItemRelatives)

        // verify that tracked entity instance store is never called
        verify(trackedEntityInstanceStore, never()).deleteIfExists(any())
        verify(trackedEntityInstanceStore, never()).updateOrInsert(any())
        verify(trackedEntityAttributeValueHandler, never()).handleMany(any(), any())
        verify(trackedEntityAttributeValueStore, never()).deleteByInstanceAndNotInAttributes(any(), any())
        verify(enrollmentHandler, never()).handleMany(any(), any(), any())
        verify(enrollmentCleaner, never()).deleteOrphan(any(), any())
        verify(relationshipCleaner, never()).deleteOrphan(any(), any())
    }

    @Test
    fun invoke_delete_when_handle_program_tracked_entity_instance_set_as_deleted() {
        whenever(trackedEntityInstance.deleted()).thenReturn(Boolean.TRUE)

        val params = IdentifiableDataHandlerParams(true, false, false, false)
        trackedEntityInstanceHandler.handleMany(listOf(trackedEntityInstance), params, relationshipItemRelatives)

        // verify that tracked entity instance store is only called with delete
        verify(trackedEntityInstanceStore, times(1)).deleteIfExists(any())
        verify(trackedEntityInstanceStore, never()).updateOrInsert(any())
        verify(trackedEntityAttributeValueHandler, never()).handleMany(any(), any())
        verify(trackedEntityAttributeValueStore, never()).deleteByInstanceAndNotInAttributes(any(), any())

        // verify that enrollment handler is never called
        verify(enrollmentHandler, never()).handleMany(any(), any(), any())
    }

    @Test
    fun invoke_only_update_or_insert_when_handle_tracked_entity_instance_inserted() {
        whenever(trackedEntityInstance.deleted()).doReturn(Boolean.FALSE)
        whenever(trackedEntityInstanceStore.updateOrInsert(any())).doReturn(HandleAction.Update)
        whenever(trackedEntityInstance.trackedEntityAttributeValues()).doReturn(
            listOf(
                TrackedEntityAttributeValue.builder().trackedEntityAttribute("att").build()
            )
        )

        val params = IdentifiableDataHandlerParams(true, false, false, false)
        trackedEntityInstanceHandler.handleMany(listOf(trackedEntityInstance), params, relationshipItemRelatives)

        // verify that tracked entity instance store is only called with update
        verify(trackedEntityInstanceStore, times(1)).updateOrInsert(any())
        verify(trackedEntityInstanceStore, never()).deleteIfExists(any())
        verify(trackedEntityAttributeValueHandler, times(1)).handleMany(any(), any())
        verify(trackedEntityAttributeValueStore, times(1)).deleteByInstanceAndNotInAttributes(any(), any())

        // verify that enrollment handler is called once
        verify(enrollmentHandler, times(1)).handleMany(any(), any(), any())
    }

    @Test
    fun invoke_cleaners_if_full_update() {
        whenever(trackedEntityInstance.toBuilder()).doReturn(TrackedEntityInstance.builder().uid("uid"))
        whenever(trackedEntityInstanceStore.updateOrInsert(any())).thenReturn(HandleAction.Update)

        val params = IdentifiableDataHandlerParams(true, true, false, false)
        trackedEntityInstanceHandler.handleMany(listOf(trackedEntityInstance), params, relatives)

        verify(enrollmentCleaner, times(1)).deleteOrphan(any(), anyOrNull())
        verify(relationshipCleaner, times(1)).deleteOrphan(any(), anyOrNull())
    }

    @Test
    fun do_not_invoke_cleaners_if_not_full_update() {
        whenever(trackedEntityInstance.toBuilder()).doReturn(TrackedEntityInstance.builder().uid("uid"))
        whenever(trackedEntityInstanceStore.updateOrInsert(any())).doReturn(HandleAction.Update)

        val params = IdentifiableDataHandlerParams(true, false, false, false)
        trackedEntityInstanceHandler.handleMany(listOf(trackedEntityInstance), params, relatives)

        verify(enrollmentCleaner, never()).deleteOrphan(any(), any())
        verify(relationshipCleaner, never()).deleteOrphan(any(), any())
    }

    @Test
    fun invoke_relationship_handler_with_relationship_from_version_manager() {
        whenever(relationshipVersionManager.getRelativeTei(relationship229Compatible, TEI_UID)).doReturn(relative)
        whenever(relationshipVersionManager.getOwnedRelationships(listOf(relationship), TEI_UID))
            .doReturn(listOf(relationship))
        whenever(relative.toBuilder()).doReturn(relativeBuilder)
        whenever(relativeBuilder.syncState(any())).doReturn(relativeBuilder)
        whenever(relativeBuilder.aggregatedSyncState(any())).doReturn(relativeBuilder)
        whenever(relativeBuilder.build()).doReturn(relative)

        val params = IdentifiableDataHandlerParams(true, false, false, false)
        trackedEntityInstanceHandler.handleMany(listOf(trackedEntityInstance), params, relatives)

        verify(relationshipHandler, times(1)).handleMany(any(), any())
        verify(relationshipVersionManager, times(1)).saveRelativesIfNotExist(
            listOf(relationship), TEI_UID, relatives,
            relationshipHandler
        )
    }

    @Test
    fun do_not_invoke_relationship_repository_when_no_relative() {
        whenever(relationshipVersionManager.getRelativeTei(relationship229Compatible, TEI_UID)).doReturn(null)

        val params = IdentifiableDataHandlerParams(true, false, false, false)
        trackedEntityInstanceHandler.handleMany(listOf(trackedEntityInstance), params, relationshipItemRelatives)

        verify(relationshipHandler, never()).handle(any())
    }

    @Test
    fun do_not_delete_orphan_attribute_values_if_not_all_attributes() {
        val params = IdentifiableDataHandlerParams(false, false, false, false)
        trackedEntityInstanceHandler.handleMany(listOf(trackedEntityInstance), params, relatives)

        verify(trackedEntityInstanceStore, times(1)).updateOrInsert(any())
        verify(trackedEntityInstanceStore, never()).deleteIfExists(any())
        verify(trackedEntityAttributeValueHandler, times(1)).handleMany(any(), any())
        verify(trackedEntityAttributeValueStore, never()).deleteByInstanceAndNotInAttributes(any(), any())
    }
}
