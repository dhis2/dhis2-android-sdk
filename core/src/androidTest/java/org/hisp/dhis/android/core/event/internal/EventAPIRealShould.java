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

package org.hisp.dhis.android.core.event.internal;

import static com.google.common.truth.Truth.assertThat;
import static org.hisp.dhis.android.core.event.internal.EventUtils.assertEvent;
import static org.hisp.dhis.android.core.event.internal.EventUtils.createEventWithFutureDate;
import static org.hisp.dhis.android.core.event.internal.EventUtils.createEventWithInvalidAttributeOptionCombo;
import static org.hisp.dhis.android.core.event.internal.EventUtils.createEventWithInvalidDataValues;
import static org.hisp.dhis.android.core.event.internal.EventUtils.createEventWithInvalidOrgunit;
import static org.hisp.dhis.android.core.event.internal.EventUtils.createEventWithInvalidProgram;
import static org.hisp.dhis.android.core.event.internal.EventUtils.createValidEvent;
import static org.hisp.dhis.android.core.imports.ImportStatus.ERROR;
import static org.hisp.dhis.android.core.imports.ImportStatus.SUCCESS;
import static org.hisp.dhis.android.core.imports.ImportStatus.WARNING;

import org.hisp.dhis.android.core.BaseRealIntegrationTest;
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutor;
import org.hisp.dhis.android.core.arch.api.executors.internal.APICallExecutorImpl;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.imports.internal.EventImportSummary;
import org.hisp.dhis.android.core.imports.internal.EventWebResponse;
import org.hisp.dhis.android.core.maintenance.D2Error;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnitMode;
import org.junit.Assert;
import org.junit.Before;

import java.util.Arrays;
import java.util.Collections;

public abstract class EventAPIRealShould extends BaseRealIntegrationTest {

    // API version dependant parameters
    private String serverUrl;
    private String strategy;
    private String ouMode = OrganisationUnitMode.ACCESSIBLE.name();

    private APICallExecutor apiCallExecutor;

    private EventService eventService;

    EventAPIRealShould(String serverUrl, String strategy) {
        super();
        this.serverUrl = serverUrl;
        this.strategy = strategy;
    }

    @Before
    public void setUp() {
        super.setUp();
        apiCallExecutor = APICallExecutorImpl.create(d2.databaseAdapter(), null);

        eventService = d2.retrofit().create(EventService.class);
    }

    //@Test
    public void valid_events() throws Exception {
        login();

        Event validEvent1 = createValidEvent();

        Event validEvent2 = createValidEvent();

        EventPayload payload = new EventPayload();
        payload.events = Arrays.asList(validEvent1, validEvent2);

        EventWebResponse response = apiCallExecutor.executeObjectCallWithAcceptedErrorCodes(eventService
                .postEvents(payload, this.strategy), Collections.singletonList(409), EventWebResponse.class);

        assertThat(response.response().status()).isEqualTo(SUCCESS);

        for (EventImportSummary importSummary : response.response().importSummaries()) {
            if (validEvent1.uid().equals(importSummary.reference())) {
                assertEvent(importSummary, SUCCESS);
            }
            else if (validEvent2.uid().equals(importSummary.reference())) {
                assertEvent(importSummary, SUCCESS);
            }
        }

        // Check server status
        Event serverValidEvent1 = apiCallExecutor.executeObjectCall(eventService.getEvent(validEvent1.uid(), EventFields.allFields, ouMode));
        Event serverValidEvent2 = apiCallExecutor.executeObjectCall(eventService.getEvent(validEvent2.uid(), EventFields.allFields, ouMode));

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

        EventWebResponse response = apiCallExecutor.executeObjectCallWithAcceptedErrorCodes(eventService
                .postEvents(payload, this.strategy), Collections.singletonList(409), EventWebResponse.class);

        assertThat(response.response().status()).isEqualTo(ERROR);

        for (EventImportSummary importSummary : response.response().importSummaries()) {
            if (validEvent.uid().equals(importSummary.reference())) {
                assertEvent(importSummary, SUCCESS);
            }
            else if (invalidEvent.uid().equals(importSummary.reference())) {
                assertEvent(importSummary, ERROR);
            }
        }

        // Check server status
        Event serverValidEvent = apiCallExecutor.executeObjectCall(eventService.getEvent(validEvent.uid(), EventFields.allFields, ouMode));

        assertThat(serverValidEvent).isNotNull();

        try {
            apiCallExecutor.executeObjectCall(eventService.getEvent(invalidEvent.uid(), EventFields.allFields, ouMode));
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

        EventWebResponse response = apiCallExecutor.executeObjectCallWithAcceptedErrorCodes(eventService
                .postEvents(payload, this.strategy), Collections.singletonList(409), EventWebResponse.class);

        assertThat(response.response().status()).isEqualTo(ERROR);

        for (EventImportSummary importSummary : response.response().importSummaries()) {
            if (validEvent.uid().equals(importSummary.reference())) {
                assertEvent(importSummary, SUCCESS);
            }
            else if (invalidEvent.uid().equals(importSummary.reference())) {
                assertEvent(importSummary, ERROR);
            }
        }

        // Check server status
        Event serverValidEvent = apiCallExecutor.executeObjectCall(eventService.getEvent(validEvent.uid(), EventFields.allFields, ouMode));

        assertThat(serverValidEvent).isNotNull();

        try {
            apiCallExecutor.executeObjectCall(eventService.getEvent(invalidEvent.uid(), EventFields.allFields, ouMode));
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

        EventWebResponse response = apiCallExecutor.executeObjectCallWithAcceptedErrorCodes(eventService
                .postEvents(payload, this.strategy), Collections.singletonList(409), EventWebResponse.class);

        assertThat(response.response().status()).isEqualTo(SUCCESS);

        for (EventImportSummary importSummary : response.response().importSummaries()) {
            if (validEvent1.uid().equals(importSummary.reference())) {
                assertEvent(importSummary, SUCCESS);
            }
            else if (validEvent2.uid().equals(importSummary.reference())) {
                assertEvent(importSummary, SUCCESS);
            }
        }

        // Check server status
        Event serverValidEvent1 = apiCallExecutor.executeObjectCall(eventService.getEvent(validEvent1.uid(), EventFields.allFields, ouMode));
        Event serverValidEvent2 = apiCallExecutor.executeObjectCall(eventService.getEvent(validEvent2.uid(), EventFields.allFields, ouMode));

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

        EventWebResponse response = apiCallExecutor.executeObjectCallWithAcceptedErrorCodes(eventService
                .postEvents(payload, this.strategy), Collections.singletonList(409), EventWebResponse.class);

        assertThat(response.response().status()).isEqualTo(ERROR);

        for (EventImportSummary importSummary : response.response().importSummaries()) {
            if (validEvent.uid().equals(importSummary.reference())) {
                assertEvent(importSummary, SUCCESS);
            }
            else if (invalidEvent.uid().equals(importSummary.reference())) {
                assertEvent(importSummary, ERROR);
            }
        }

        // Check server status
        Event serverValidEvent = apiCallExecutor.executeObjectCall(eventService.getEvent(validEvent.uid(), EventFields.allFields, ouMode));

        assertThat(serverValidEvent).isNotNull();

        try {
            apiCallExecutor.executeObjectCall(eventService.getEvent(invalidEvent.uid(), EventFields.allFields, ouMode));
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

        EventWebResponse response = apiCallExecutor.executeObjectCallWithAcceptedErrorCodes(eventService
                .postEvents(payload, this.strategy), Collections.singletonList(409), EventWebResponse.class);

        assertThat(response.response().status()).isEqualTo(WARNING);

        for (EventImportSummary importSummary : response.response().importSummaries()) {
            if (validEvent.uid().equals(importSummary.reference())) {
                assertEvent(importSummary, SUCCESS);
            }
            else if (invalidEvent.uid().equals(importSummary.reference())) {
                assertEvent(importSummary, WARNING);
            }
        }

        // Check server status
        Event serverValidEvent = apiCallExecutor.executeObjectCall(eventService.getEvent(validEvent.uid(),
                EventFields.allFields, ouMode));
        Event serverInvalidEvent = apiCallExecutor.executeObjectCall(eventService.getEvent(invalidEvent.uid(),
                EventFields.allFields, ouMode));

        assertThat(serverValidEvent).isNotNull();
        assertThat(serverValidEvent.trackedEntityDataValues().size()).isEqualTo(2);

        assertThat(serverInvalidEvent).isNotNull();
        assertThat(serverInvalidEvent.trackedEntityDataValues()).isNull();
    }

    private void login() {
        d2.userModule().logIn(username, password, url).blockingGet();
    }
}
