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

package org.hisp.dhis.android.core.trackedentity.internal;

import org.hisp.dhis.android.core.arch.cleaners.internal.OrphanCleaner;
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction;
import org.hisp.dhis.android.core.arch.handlers.internal.HandlerWithTransformer;
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableDataHandler;
import org.hisp.dhis.android.core.arch.handlers.internal.Transformer;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.relationship.Relationship;
import org.hisp.dhis.android.core.relationship.RelationshipHelper;
import org.hisp.dhis.android.core.relationship.internal.Relationship229Compatible;
import org.hisp.dhis.android.core.relationship.internal.RelationshipDHISVersionManager;
import org.hisp.dhis.android.core.relationship.internal.RelationshipHandler;
import org.hisp.dhis.android.core.relationship.internal.RelationshipItemRelatives;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeValue;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceInternalAccessor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class TrackedEntityInstanceHandlerShould {
    @Mock
    private RelationshipDHISVersionManager relationshipVersionManager;

    @Mock
    private RelationshipHandler relationshipHandler;

    @Mock
    private TrackedEntityInstanceStore trackedEntityInstanceStore;

    @Mock
    private HandlerWithTransformer<TrackedEntityAttributeValue> trackedEntityAttributeValueHandler;

    @Mock
    private IdentifiableDataHandler<Enrollment> enrollmentHandler;

    @Mock
    private TrackedEntityInstance trackedEntityInstance;

    @Mock
    private Enrollment enrollment;

    @Mock
    private Relationship229Compatible relationship229Compatible;

    @Mock
    private Relationship relationship;

    @Mock
    private TrackedEntityInstance relative;

    @Mock
    private TrackedEntityInstance.Builder relativeBuilder;

    @Mock
    private OrphanCleaner<TrackedEntityInstance, Enrollment> enrollmentCleaner;

    @Mock
    private OrphanCleaner<TrackedEntityInstance, Relationship229Compatible> relationshipCleaner;

    @Mock
    private RelationshipItemRelatives relatives;

    @Mock
    private TrackedEntityInstance.Builder teiBuilder;

    // Constants
    private String TEI_UID = "test_tei_uid";
    private String RELATIVE_UID = "relative_uid";
    private String RELATIONSHIP_TYPE = "type_uid";

    // object to test
    private TrackedEntityInstanceHandler trackedEntityInstanceHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(trackedEntityInstance.uid()).thenReturn(TEI_UID);
        when(trackedEntityInstance.toBuilder()).thenReturn(teiBuilder);
        when(teiBuilder.state(State.SYNCED)).thenReturn(teiBuilder);
        when(teiBuilder.build()).thenReturn(trackedEntityInstance);
        when(TrackedEntityInstanceInternalAccessor.accessEnrollments(trackedEntityInstance))
                .thenReturn(Collections.singletonList(enrollment));
        when(TrackedEntityInstanceInternalAccessor.accessRelationships(trackedEntityInstance))
                .thenReturn(Collections.singletonList(relationship229Compatible));
        when(relationshipVersionManager.from229Compatible(Collections.singletonList(relationship229Compatible)))
                .thenReturn(Collections.singletonList(relationship));

        when(relationship.relationshipType()).thenReturn(RELATIONSHIP_TYPE);
        when(relationship.from()).thenReturn(RelationshipHelper.teiItem(TEI_UID));
        when(relationship.to()).thenReturn(RelationshipHelper.teiItem(RELATIVE_UID));
        when(relative.uid()).thenReturn(RELATIVE_UID);

        trackedEntityInstanceHandler = new TrackedEntityInstanceHandler(
                relationshipVersionManager, relationshipHandler, trackedEntityInstanceStore,
                trackedEntityAttributeValueHandler, enrollmentHandler, enrollmentCleaner, relationshipCleaner);
    }

    @Test
    public void do_nothing_when_passing_null_argument() {
        trackedEntityInstanceHandler.handleMany(null,null, false);

        // verify that tracked entity instance store is never called
        verify(trackedEntityInstanceStore, never()).deleteIfExists(anyString());
        verify(trackedEntityInstanceStore, never()).updateOrInsert(any(TrackedEntityInstance.class));
        verify(trackedEntityAttributeValueHandler, never()).handleMany(
                anyCollection(), any(Transformer.class));
        verify(enrollmentHandler, never()).handleMany(anyCollection(), any(), anyBoolean());
        verify(enrollmentCleaner, never()).deleteOrphan(any(TrackedEntityInstance.class), anyCollection());
        verify(relationshipCleaner, never()).deleteOrphan(any(TrackedEntityInstance.class), anyCollection());
    }

    @Test
    public void invoke_delete_when_handle_program_tracked_entity_instance_set_as_deleted() {
        when(trackedEntityInstance.deleted()).thenReturn(Boolean.TRUE);

        trackedEntityInstanceHandler.handleMany(Collections.singletonList(trackedEntityInstance), o -> o, false);

        // verify that tracked entity instance store is only called with delete
        verify(trackedEntityInstanceStore, times(1)).deleteIfExists(anyString());

        verify(trackedEntityInstanceStore, never()).updateOrInsert(any(TrackedEntityInstance.class));
        verify(trackedEntityAttributeValueHandler, never()).handleMany(
                anyCollection(), any(Transformer.class));

        // verify that enrollment handler is never called
        verify(enrollmentHandler, never()).handleMany(anyCollection(), any(), anyBoolean());
    }

    @Test
    public void invoke_only_update_or_insert_when_handle_tracked_entity_instance_inserted() {
        when(trackedEntityInstance.deleted()).thenReturn(Boolean.FALSE);
        when(trackedEntityInstanceStore.updateOrInsert(any(TrackedEntityInstance.class))).thenReturn(HandleAction.Update);

        trackedEntityInstanceHandler.handleMany(Collections.singletonList(trackedEntityInstance), o -> o, false);

        // verify that tracked entity instance store is only called with update
        verify(trackedEntityInstanceStore, times(1)).updateOrInsert(any(TrackedEntityInstance.class));

        verify(trackedEntityInstanceStore, never()).deleteIfExists(anyString());

        verify(trackedEntityAttributeValueHandler, times(1)).handleMany(
                anyCollection(), any(Transformer.class));

        // verify that enrollment handler is called once
        verify(enrollmentHandler, times(1)).handleMany(anyCollection(), any(Transformer.class), anyBoolean());
    }

    @Test
    public void invoke_cleaners_if_full_update() {
        when(trackedEntityInstance.toBuilder()).thenReturn(TrackedEntityInstance.builder().uid("uid"));
        when(trackedEntityInstanceStore.updateOrInsert(any(TrackedEntityInstance.class))).thenReturn(HandleAction.Update);

        trackedEntityInstanceHandler.handleMany(Collections.singletonList(trackedEntityInstance), false,
                true, false, relatives);

        verify(enrollmentCleaner, times(1)).deleteOrphan(any(TrackedEntityInstance.class), anyCollection());
        verify(relationshipCleaner, times(1)).deleteOrphan(any(TrackedEntityInstance.class), anyCollection());
    }

    @Test
    public void do_not_invoke_cleaners_if_not_full_update() {
        when(trackedEntityInstance.toBuilder()).thenReturn(TrackedEntityInstance.builder().uid("uid"));
        when(trackedEntityInstanceStore.updateOrInsert(any(TrackedEntityInstance.class))).thenReturn(HandleAction.Update);

        trackedEntityInstanceHandler.handleMany(Collections.singletonList(trackedEntityInstance), false,
                false, false, relatives);

        verify(enrollmentCleaner, never()).deleteOrphan(any(TrackedEntityInstance.class), anyCollection());
        verify(relationshipCleaner, never()).deleteOrphan(any(TrackedEntityInstance.class), anyCollection());
    }

    @Test
    public void invoke_relationship_handler_with_relationship_from_version_manager() {
        when(relationshipVersionManager.getRelativeTei(relationship229Compatible, TEI_UID)).thenReturn(relative);
        when(relative.toBuilder()).thenReturn(relativeBuilder);
        when(relativeBuilder.state(any(State.class))).thenReturn(relativeBuilder);
        when(relativeBuilder.build()).thenReturn(relative);

        trackedEntityInstanceHandler.handleMany(Collections.singletonList(trackedEntityInstance), false,
                false, false, relatives);
        verify(relationshipHandler, times(1)).handleMany(anyList(), any());
        verify(relationshipVersionManager, times(1))
                .createRelativesIfNotExist(Collections.singletonList(relationship), TEI_UID, relatives);
    }

    @Test
    public void do_not_invoke_relationship_repository_when_no_relative() {
        when(relationshipVersionManager.getRelativeTei(relationship229Compatible, TEI_UID)).thenReturn(null);
        trackedEntityInstanceHandler.handleMany(Collections.singletonList(trackedEntityInstance), o -> o, false);
        verify(relationshipHandler, never()).handle(any(Relationship.class));
    }
}