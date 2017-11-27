package org.hisp.dhis.android.core.trackedentity;

import org.hisp.dhis.android.core.common.State;
import org.hisp.dhis.android.core.enrollment.EnrollmentImportHandler;
import org.hisp.dhis.android.core.event.EventImportHandler;
import org.hisp.dhis.android.core.imports.ImportEnrollment;
import org.hisp.dhis.android.core.imports.ImportEvent;
import org.hisp.dhis.android.core.imports.ImportStatus;
import org.hisp.dhis.android.core.imports.ImportSummary;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

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
    private EventImportHandler eventImportHandler;

    @Mock
    private ImportSummary importSummary;

    @Mock
    private ImportSummary enrollmentSummary;

    @Mock
    private ImportSummary eventSummary;

    @Mock
    private ImportEnrollment importEnrollment;

    @Mock
    private ImportEvent importEvent;

    // object to test
    private TrackedEntityInstanceImportHandler trackedEntityInstanceImportHandler;


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        trackedEntityInstanceImportHandler = new TrackedEntityInstanceImportHandler(
                trackedEntityInstanceStore, enrollmentImportHandler, eventImportHandler
        );
    }

    @Test
    public void do_nothing_when_passing_null_argument() throws Exception {
        trackedEntityInstanceImportHandler.handleTrackedEntityInstanceImportSummaries(null);

        verify(trackedEntityInstanceStore, never()).setState(anyString(), any(State.class));
    }

    @Test
    public void setStatus_shouldUpdateTrackedEntityInstanceStatusSuccess() throws Exception {
        when(importSummary.importStatus()).thenReturn(ImportStatus.SUCCESS);
        when(importSummary.reference()).thenReturn("test_tei_uid");

        trackedEntityInstanceImportHandler.handleTrackedEntityInstanceImportSummaries(
                Collections.singletonList(importSummary)
        );

        verify(trackedEntityInstanceStore, times(1)).setState("test_tei_uid", State.SYNCED);
    }

    @Test
    public void setStatus_shouldUpdateTrackedEntityInstanceStatusError() throws Exception {
        when(importSummary.importStatus()).thenReturn(ImportStatus.ERROR);
        when(importSummary.reference()).thenReturn("test_tei_uid");

        trackedEntityInstanceImportHandler.handleTrackedEntityInstanceImportSummaries(
                Collections.singletonList(importSummary)
        );

        verify(trackedEntityInstanceStore, times(1)).setState("test_tei_uid", State.ERROR);
    }

    @Test
    public void update_tracker_entity_instance_status_success_status_and_handle_import_enrollment_on_import_success() throws Exception {
        when(importSummary.importStatus()).thenReturn(ImportStatus.SUCCESS);
        when(importSummary.reference()).thenReturn("test_tei_uid");
        when(importSummary.importEnrollment()).thenReturn(importEnrollment);
        List<ImportSummary> enrollmentSummaries = Collections.singletonList(enrollmentSummary);
        when(importEnrollment.importSummaries()).thenReturn(enrollmentSummaries);

        trackedEntityInstanceImportHandler.handleTrackedEntityInstanceImportSummaries(
                Collections.singletonList(importSummary)
        );

        verify(trackedEntityInstanceStore, times(1)).setState("test_tei_uid", State.SYNCED);
        verify(enrollmentImportHandler, times(1)).handleEnrollmentImportSummary(enrollmentSummaries);

    }

    @Test
    public void update_tracker_entity_instance_status_success_status_and_handle_import_event_on_import_success() throws Exception {
        when(importSummary.importStatus()).thenReturn(ImportStatus.SUCCESS);
        when(importSummary.reference()).thenReturn("test_tei_uid");
        when(importSummary.importEvent()).thenReturn(importEvent);
        List<ImportSummary> eventSummaries = Collections.singletonList(eventSummary);
        when(importEvent.importSummaries()).thenReturn(eventSummaries);

        trackedEntityInstanceImportHandler.handleTrackedEntityInstanceImportSummaries(
                Collections.singletonList(importSummary)
        );

        verify(trackedEntityInstanceStore, times(1)).setState("test_tei_uid", State.SYNCED);
        verify(eventImportHandler, times(1)).handleEventImportSummaries(eventSummaries);

    }
}
