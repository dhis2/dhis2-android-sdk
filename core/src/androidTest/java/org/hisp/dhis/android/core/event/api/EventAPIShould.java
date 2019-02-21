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

package org.hisp.dhis.android.core.event.api;

import org.hisp.dhis.android.core.D2;
import org.hisp.dhis.android.core.arch.api.executors.APICallExecutor;
import org.hisp.dhis.android.core.arch.api.executors.APICallExecutorImpl;
import org.hisp.dhis.android.core.common.D2Factory;
import org.hisp.dhis.android.core.data.database.AbsStoreTestCase;
import org.hisp.dhis.android.core.data.server.RealServerMother;
import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.event.EventFields;
import org.hisp.dhis.android.core.event.EventPayload;
import org.hisp.dhis.android.core.event.EventService;
import org.hisp.dhis.android.core.imports.EventImportSummary;
import org.hisp.dhis.android.core.imports.EventWebResponse;
import org.hisp.dhis.android.core.maintenance.D2Error;
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
        Event serverValidEvent1 = apiCallExecutor.executeObjectCall(eventService.getEvent(validEvent1.uid(), EventFields.allFields));
        Event serverValidEvent2 = apiCallExecutor.executeObjectCall(eventService.getEvent(validEvent2.uid(), EventFields.allFields));

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
        Event serverValidEvent = apiCallExecutor.executeObjectCall(eventService.getEvent(validEvent.uid(), EventFields.allFields));

        assertThat(serverValidEvent).isNotNull();

        try {
            apiCallExecutor.executeObjectCall(eventService.getEvent(invalidEvent.uid(), EventFields.allFields));
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
        Event serverValidEvent = apiCallExecutor.executeObjectCall(eventService.getEvent(validEvent.uid(), EventFields.allFields));

        assertThat(serverValidEvent).isNotNull();

        try {
            apiCallExecutor.executeObjectCall(eventService.getEvent(invalidEvent.uid(), EventFields.allFields));
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
        Event serverValidEvent1 = apiCallExecutor.executeObjectCall(eventService.getEvent(validEvent1.uid(), EventFields.allFields));
        Event serverValidEvent2 = apiCallExecutor.executeObjectCall(eventService.getEvent(validEvent2.uid(), EventFields.allFields));

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
        Event serverValidEvent = apiCallExecutor.executeObjectCall(eventService.getEvent(validEvent.uid(), EventFields.allFields));

        assertThat(serverValidEvent).isNotNull();

        try {
            apiCallExecutor.executeObjectCall(eventService.getEvent(invalidEvent.uid(), EventFields.allFields));
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
                EventFields.allFields));
        Event serverInvalidEvent = apiCallExecutor.executeObjectCall(eventService.getEvent(invalidEvent.uid(),
                EventFields.allFields));

        assertThat(serverValidEvent).isNotNull();
        assertThat(serverValidEvent.trackedEntityDataValues().size()).isEqualTo(2);

        assertThat(serverInvalidEvent).isNotNull();
        assertThat(serverInvalidEvent.trackedEntityDataValues()).isNull();
    }

    private void login() throws Exception {
        d2.userModule().logIn(RealServerMother.user, RealServerMother.password).call();
    }
}
