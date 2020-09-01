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

import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectStore;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.common.internal.DataStatePropagator;
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentImportHandler;
import org.hisp.dhis.android.core.imports.ImportStatus;
import org.hisp.dhis.android.core.imports.TrackerImportConflict;
import org.hisp.dhis.android.core.imports.internal.EnrollmentImportSummaries;
import org.hisp.dhis.android.core.imports.internal.EnrollmentImportSummary;
import org.hisp.dhis.android.core.imports.internal.TEIImportSummary;
import org.hisp.dhis.android.core.imports.internal.TrackerImportConflictParser;
import org.hisp.dhis.android.core.relationship.RelationshipCollectionRepository;
import org.hisp.dhis.android.core.relationship.internal.RelationshipDHISVersionManager;
import org.hisp.dhis.android.core.relationship.internal.RelationshipStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class TrackedEntityInstanceImportHandlerShould {

    @Mock
    private TrackedEntityInstanceStore trackedEntityInstanceStore;

    @Mock
    private EnrollmentImportHandler enrollmentImportHandler;

    @Mock
    private TEIImportSummary importSummary;

    @Mock
    private EnrollmentImportSummary enrollmentSummary;

    @Mock
    private EnrollmentImportSummaries importEnrollment;

    @Mock
    private ObjectStore<TrackerImportConflict> trackerImportConflictStore;

    @Mock
    private TrackerImportConflictParser trackerImportConflictParser;

    @Mock
    private RelationshipStore relationshipStore;

    @Mock
    private DataStatePropagator dataStatePropagator;

    @Mock
    private RelationshipDHISVersionManager relationshipDHISVersionManager;

    @Mock
    private RelationshipCollectionRepository relationshipCollectionRepository;

    // object to test
    private TrackedEntityInstanceImportHandler trackedEntityInstanceImportHandler;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        trackedEntityInstanceImportHandler =
                new TrackedEntityInstanceImportHandler(trackedEntityInstanceStore, enrollmentImportHandler,
                        trackerImportConflictStore, trackerImportConflictParser, relationshipStore, dataStatePropagator,
                        relationshipDHISVersionManager, relationshipCollectionRepository);
    }

    @Test
    public void do_nothing_when_passing_null_argument() throws Exception {
        trackedEntityInstanceImportHandler.handleTrackedEntityInstanceImportSummaries(null);

        verify(trackedEntityInstanceStore, never()).setStateOrDelete(anyString(), any(State.class));
    }

    @Test
    public void setStatus_shouldUpdateTrackedEntityInstanceStatusSuccess() throws Exception {
        when(importSummary.status()).thenReturn(ImportStatus.SUCCESS);
        when(importSummary.reference()).thenReturn("test_tei_uid");

        trackedEntityInstanceImportHandler.handleTrackedEntityInstanceImportSummaries(
                Collections.singletonList(importSummary)
        );

        verify(trackedEntityInstanceStore, times(1)).setStateOrDelete("test_tei_uid", State.SYNCED);
    }

    @Test
    public void setStatus_shouldUpdateTrackedEntityInstanceStatusError() throws Exception {
        when(importSummary.status()).thenReturn(ImportStatus.ERROR);
        when(importSummary.reference()).thenReturn("test_tei_uid");

        trackedEntityInstanceImportHandler.handleTrackedEntityInstanceImportSummaries(
                Collections.singletonList(importSummary)
        );

        verify(trackedEntityInstanceStore, times(1)).setStateOrDelete("test_tei_uid", State.ERROR);
    }

    @Test
    public void update_tracker_entity_instance_status_success_status_and_handle_import_enrollment_on_import_success() throws Exception {
        when(importSummary.status()).thenReturn(ImportStatus.SUCCESS);
        when(importSummary.reference()).thenReturn("test_tei_uid");
        when(importSummary.enrollments()).thenReturn(importEnrollment);
        List<EnrollmentImportSummary> enrollmentSummaries = Collections.singletonList(enrollmentSummary);
        when(importEnrollment.importSummaries()).thenReturn(enrollmentSummaries);

        trackedEntityInstanceImportHandler.handleTrackedEntityInstanceImportSummaries(
                Collections.singletonList(importSummary)
        );

        verify(trackedEntityInstanceStore, times(1)).setStateOrDelete("test_tei_uid", State.SYNCED);
        verify(enrollmentImportHandler, times(1)).handleEnrollmentImportSummary(
                eq(enrollmentSummaries), anyString());
    }
}