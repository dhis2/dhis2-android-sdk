package org.hisp.dhis.android.core.event.api;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.common.APICallExecutor;
import org.hisp.dhis.android.core.common.APICallExecutorImpl;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventPayload;
import org.hisp.dhis.android.core.event.EventService;
import org.hisp.dhis.android.core.imports.ImportSummary;
import org.hisp.dhis.android.core.imports.WebResponse;
import org.junit.Assert;
import org.junit.Before;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.event.api.EventUtils.assertEvent;
import static org.hisp.dhis.android.core.event.api.EventUtils.createEventWithFutureDate;
import static org.hisp.dhis.android.core.event.api.EventUtils.createEventWithInvalidAttributeOptionCombo;
import static org.hisp.dhis.android.core.event.api.EventUtils.createEventWithInvalidDataValues;
import static org.hisp.dhis.android.core.event.api.EventUtils.createEventWithInvalidOrgunit;
import static org.hisp.dhis.android.core.event.api.EventUtils.createEventWithInvalidProgram;
import static org.hisp.dhis.android.core.event.api.EventUtils.createValidEvent;
import static org.hisp.dhis.android.core.imports.ImportStatus.ERROR;
import static org.hisp.dhis.android.core.imports.ImportStatus.SUCCESS;
import static org.hisp.dhis.android.core.imports.ImportStatus.WARNING;

public abstract class EventAPIShould extends AbsStoreTestCase {

    // API version dependant parameters
    private String serverUrl;
    private String strategy;

    private D2 d2;
    private APICallExecutor apiCallExecutor;

    private EventService eventService;

    EventAPIShould(String serverUrl, String strategy) {
        super();
        this.serverUrl = serverUrl;
        this.strategy = strategy;
    }

    @Before
    public void setUp() throws IOException {
        super.setUp();

        d2 = D2Factory.create(this.serverUrl, databaseAdapter());
        apiCallExecutor = APICallExecutorImpl.create(d2.databaseAdapter());

        eventService = d2.retrofit().create(EventService.class);
    }

    //@Test
    public void valid_events() throws Exception {
        login();

        Event validEvent1 = createValidEvent();

        Event validEvent2 = createValidEvent();

        EventPayload payload = new EventPayload();
        payload.events = Arrays.asList(validEvent1, validEvent2);

        WebResponse response = apiCallExecutor.executeObjectCallWithAcceptedErrorCodes(eventService
                .postEvents(payload, this.strategy), Collections.singletonList(409), WebResponse.class);

        assertThat(response.importSummaries().importStatus()).isEqualTo(SUCCESS);

        for (ImportSummary importSummary : response.importSummaries().importSummaries()) {
            if (validEvent1.uid().equals(importSummary.reference())) {
                assertEvent(importSummary, SUCCESS);
            }
            else if (validEvent2.uid().equals(importSummary.reference())) {
                assertEvent(importSummary, SUCCESS);
            }
        }

        // Check server status
        Event serverValidEvent1 = apiCallExecutor.executeObjectCall(eventService.getEvent(validEvent1.uid(), Event.allFields));
        Event serverValidEvent2 = apiCallExecutor.executeObjectCall(eventService.getEvent(validEvent2.uid(), Event.allFields));

        assertThat(serverValidEvent1).isNotNull();
        assertThat(serverValidEvent2).isNotNull();
    }

    //@Test
    public void event_with_invalid_orgunit() throws Exception {
        login();

        Event validEvent = createValidEvent();

        Event invalidEvent = createEventWithInvalidOrgunit();

        EventPayload payload = new EventPayload();
        payload.events = Arrays.asList(validEvent, invalidEvent);

        WebResponse response = apiCallExecutor.executeObjectCallWithAcceptedErrorCodes(eventService
                .postEvents(payload, this.strategy), Collections.singletonList(409), WebResponse.class);

        assertThat(response.importSummaries().importStatus()).isEqualTo(ERROR);

        for (ImportSummary importSummary : response.importSummaries().importSummaries()) {
            if (validEvent.uid().equals(importSummary.reference())) {
                assertEvent(importSummary, SUCCESS);
            }
            else if (invalidEvent.uid().equals(importSummary.reference())) {
                assertEvent(importSummary, ERROR);
            }
        }

        // Check server status
        Event serverValidEvent = apiCallExecutor.executeObjectCall(eventService.getEvent(validEvent.uid(), Event.allFields));

        assertThat(serverValidEvent).isNotNull();

        try {
            apiCallExecutor.executeObjectCall(eventService.getEvent(invalidEvent.uid(), Event.allFields));
            Assert.fail("Should not reach that line");
        } catch (D2Error e) {
            assertThat(e.httpErrorCode()).isEqualTo(404);
        }
    }

    //@Test
    public void event_with_invalid_attribute_option_combo() throws Exception {
        login();

        Event validEvent = createValidEvent();

        Event invalidEvent = createEventWithInvalidAttributeOptionCombo();

        EventPayload payload = new EventPayload();
        payload.events = Arrays.asList(validEvent, invalidEvent);

        WebResponse response = apiCallExecutor.executeObjectCallWithAcceptedErrorCodes(eventService
                .postEvents(payload, this.strategy), Collections.singletonList(409), WebResponse.class);

        assertThat(response.importSummaries().importStatus()).isEqualTo(ERROR);

        for (ImportSummary importSummary : response.importSummaries().importSummaries()) {
            if (validEvent.uid().equals(importSummary.reference())) {
                assertEvent(importSummary, SUCCESS);
            }
            else if (invalidEvent.uid().equals(importSummary.reference())) {
                assertEvent(importSummary, ERROR);
            }
        }

        // Check server status
        Event serverValidEvent = apiCallExecutor.executeObjectCall(eventService.getEvent(validEvent.uid(), Event.allFields));

        assertThat(serverValidEvent).isNotNull();

        try {
            apiCallExecutor.executeObjectCall(eventService.getEvent(invalidEvent.uid(), Event.allFields));
            Assert.fail("Should not reach that line");
        } catch (D2Error e) {
            assertThat(e.httpErrorCode()).isEqualTo(404);
        }
    }

    //@Test
    public void event_with_future_date_does_not_fail() throws Exception {
        login();

        Event validEvent1 = createValidEvent();

        Event validEvent2 = createEventWithFutureDate();

        EventPayload payload = new EventPayload();
        payload.events = Arrays.asList(validEvent1, validEvent2);

        WebResponse response = apiCallExecutor.executeObjectCallWithAcceptedErrorCodes(eventService
                .postEvents(payload, this.strategy), Collections.singletonList(409), WebResponse.class);

        assertThat(response.importSummaries().importStatus()).isEqualTo(SUCCESS);

        for (ImportSummary importSummary : response.importSummaries().importSummaries()) {
            if (validEvent1.uid().equals(importSummary.reference())) {
                assertEvent(importSummary, SUCCESS);
            }
            else if (validEvent2.uid().equals(importSummary.reference())) {
                assertEvent(importSummary, SUCCESS);
            }
        }

        // Check server status
        Event serverValidEvent1 = apiCallExecutor.executeObjectCall(eventService.getEvent(validEvent1.uid(), Event.allFields));
        Event serverValidEvent2 = apiCallExecutor.executeObjectCall(eventService.getEvent(validEvent2.uid(), Event.allFields));

        assertThat(serverValidEvent1).isNotNull();
        assertThat(serverValidEvent2).isNotNull();
    }

    //@Test
    public void event_with_invalid_program() throws Exception {
        login();

        Event validEvent = createValidEvent();

        Event invalidEvent = createEventWithInvalidProgram();

        EventPayload payload = new EventPayload();
        payload.events = Arrays.asList(validEvent, invalidEvent);

        WebResponse response = apiCallExecutor.executeObjectCallWithAcceptedErrorCodes(eventService
                .postEvents(payload, this.strategy), Collections.singletonList(409), WebResponse.class);

        assertThat(response.importSummaries().importStatus()).isEqualTo(ERROR);

        for (ImportSummary importSummary : response.importSummaries().importSummaries()) {
            if (validEvent.uid().equals(importSummary.reference())) {
                assertEvent(importSummary, SUCCESS);
            }
            else if (invalidEvent.uid().equals(importSummary.reference())) {
                assertEvent(importSummary, ERROR);
            }
        }

        // Check server status
        Event serverValidEvent = apiCallExecutor.executeObjectCall(eventService.getEvent(validEvent.uid(), Event.allFields));

        assertThat(serverValidEvent).isNotNull();

        try {
            apiCallExecutor.executeObjectCall(eventService.getEvent(invalidEvent.uid(), Event.allFields));
            Assert.fail("Should not reach that line");
        } catch (D2Error e) {
            assertThat(e.httpErrorCode()).isEqualTo(404);
        }
    }

    //@Test
    public void event_with_invalid_data_values() throws Exception {
        login();

        Event validEvent = createValidEvent();

        Event invalidEvent = createEventWithInvalidDataValues();

        EventPayload payload = new EventPayload();
        payload.events = Arrays.asList(validEvent, invalidEvent);

        WebResponse response = apiCallExecutor.executeObjectCallWithAcceptedErrorCodes(eventService
                .postEvents(payload, this.strategy), Collections.singletonList(409), WebResponse.class);

        assertThat(response.importSummaries().importStatus()).isEqualTo(WARNING);

        for (ImportSummary importSummary : response.importSummaries().importSummaries()) {
            if (validEvent.uid().equals(importSummary.reference())) {
                assertEvent(importSummary, SUCCESS);
            }
            else if (invalidEvent.uid().equals(importSummary.reference())) {
                assertEvent(importSummary, WARNING);
            }
        }

        // Check server status
        Event serverValidEvent = apiCallExecutor.executeObjectCall(eventService.getEvent(validEvent.uid(), Event.allFields));
        Event serverInvalidEvent = apiCallExecutor.executeObjectCall(eventService.getEvent(invalidEvent.uid(),
                Event.allFields));

        assertThat(serverValidEvent).isNotNull();
        assertThat(serverValidEvent.trackedEntityDataValues().size()).isEqualTo(2);

        assertThat(serverInvalidEvent).isNotNull();
        assertThat(serverInvalidEvent.trackedEntityDataValues()).isNull();
    }

    private void login() throws Exception {
        d2.logIn(RealServerMother.user, RealServerMother.password).call();
    }
}
