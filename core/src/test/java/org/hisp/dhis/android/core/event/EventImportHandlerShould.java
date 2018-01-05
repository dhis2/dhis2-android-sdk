package org.hisp.dhis.android.core.event;

import org.hisp.dhis.android.core.common.State;
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
public class EventImportHandlerShould {
    @Mock
    private List<ImportSummary> importSummaries;

    @Mock
    private ImportSummary importSummary;

    @Mock
    private EventStore eventStore;

    // object to test
    private EventImportHandler eventImportHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(importSummary.importStatus()).thenReturn(ImportStatus.SUCCESS);

        eventImportHandler = new EventImportHandler(eventStore);

    }

    @Test
    public void do_nothing_when_passing_null_argument() throws Exception {
        eventImportHandler.handleEventImportSummaries(null);

        verify(eventStore, never()).setState(anyString(), any(State.class));
    }

    @Test
    public void invoke_set_state_after_handle_event_import_summaries_with_success_status_and_reference() throws Exception {
        when(importSummary.importStatus()).thenReturn(ImportStatus.SUCCESS);
        when(importSummary.reference()).thenReturn("test_event_uid");

        eventImportHandler.handleEventImportSummaries(Collections.singletonList(importSummary));

        verify(eventStore, times(1)).setState("test_event_uid", State.SYNCED);
    }

    @Test
    public void invoke_set_state_after_handle_event_import_summaries_with_error_status_and_reference() throws Exception {
        when(importSummary.importStatus()).thenReturn(ImportStatus.ERROR);
        when(importSummary.reference()).thenReturn("test_event_uid");

        eventImportHandler.handleEventImportSummaries(Collections.singletonList(importSummary));

        verify(eventStore, times(1)).setState("test_event_uid", State.ERROR);
    }
}
