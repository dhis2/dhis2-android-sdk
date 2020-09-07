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

package org.hisp.dhis.android.core.event.internal;

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.db.stores.internal.ObjectStore;
import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.internal.EnrollmentStore;
import org.hisp.dhis.android.core.imports.ImportStatus;
import org.hisp.dhis.android.core.imports.TrackerImportConflict;
import org.hisp.dhis.android.core.imports.internal.EventImportSummary;
import org.hisp.dhis.android.core.imports.internal.TrackerImportConflictParser;
import org.hisp.dhis.android.core.note.Note;
import org.hisp.dhis.android.core.trackedentity.internal.TrackedEntityInstanceStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class EventImportHandlerShould {
    @Mock
    private EventImportSummary importSummary;

    @Mock
    private EventStore eventStore;

    @Mock
    private EnrollmentStore enrollmentStore;

    @Mock
    private IdentifiableObjectStore<Note> noteStore;

    @Mock
    private TrackedEntityInstanceStore trackedEntityInstanceStore;

    @Mock
    private ObjectStore<TrackerImportConflict> trackerImportConflictStore;

    @Mock
    private TrackerImportConflictParser trackerImportConflictParser;

    // object to test
    private EventImportHandler eventImportHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(importSummary.status()).thenReturn(ImportStatus.SUCCESS);

        eventImportHandler = new EventImportHandler(eventStore, enrollmentStore, noteStore, trackedEntityInstanceStore,
                trackerImportConflictStore, trackerImportConflictParser);
    }

    @Test
    public void do_nothing_when_passing_null_argument() throws Exception {
        eventImportHandler.handleEventImportSummaries(null, null, null);

        verify(eventStore, never()).setStateOrDelete(anyString(), any(State.class));
    }

    @Test
    public void invoke_set_state_after_handle_event_import_summaries_with_success_status_and_reference() throws Exception {
        when(importSummary.status()).thenReturn(ImportStatus.SUCCESS);
        when(importSummary.reference()).thenReturn("test_event_uid");

        eventImportHandler.handleEventImportSummaries(Collections.singletonList(importSummary),
                "test_enrollment_uid", "test_tei_uid");

        verify(eventStore, times(1)).setStateOrDelete("test_event_uid", State.SYNCED);
    }

    @Test
    public void invoke_set_state_after_handle_event_import_summaries_with_error_status_and_reference() throws Exception {
        when(importSummary.status()).thenReturn(ImportStatus.ERROR);
        when(importSummary.reference()).thenReturn("test_event_uid");

        eventImportHandler.handleEventImportSummaries(Collections.singletonList(importSummary),
                "test_enrollment_uid", "test_tei_uid");

        verify(eventStore, times(1)).setStateOrDelete("test_event_uid", State.ERROR);
    }
}