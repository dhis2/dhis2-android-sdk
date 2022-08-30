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

import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction;
import org.hisp.dhis.android.core.arch.handlers.internal.HandlerWithTransformer;
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl;
import org.hisp.dhis.android.core.event.EventDataFilter;
import org.hisp.dhis.android.core.event.EventFilter;
import org.hisp.dhis.android.core.event.EventQueryCriteria;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class EventFilterHandlerShould {

    @Mock
    private IdentifiableObjectStore<EventFilter> eventFilterStore;

    @Mock
    private HandlerWithTransformer<EventDataFilter> eventDataFilterHandler;

    @Mock
    private EventQueryCriteria eventQueryCriteria;

    @Mock
    private EventDataFilter eventDataFilter;

    // object to test
    private List<EventFilter> eventFilters;
    private List<EventDataFilter> eventDataFilters;
    private EventFilterHandler eventFilterHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        eventFilterHandler = new EventFilterHandler(
                eventFilterStore,
                eventDataFilterHandler);

        eventDataFilters = Lists.newArrayList(eventDataFilter);
        when(eventQueryCriteria.dataFilters()).thenReturn(eventDataFilters);

        EventFilter eventFilter = EventFilter.builder()
                .uid("test_tracked_entity_attribute_uid")
                .program("program_uid")
                .name("name")
                .displayName("display_name")
                .eventQueryCriteria(eventQueryCriteria)
                .build();

        when(eventFilterStore.updateOrInsert(any())).thenReturn(HandleAction.Insert);

        eventFilters = new ArrayList<>();
        eventFilters.add(eventFilter);
    }

    @Test
    public void extend_identifiable_handler_impl() {
        IdentifiableHandlerImpl<EventFilter> genericHandler =
                new EventFilterHandler(eventFilterStore, eventDataFilterHandler);
    }

    @Test
    public void handle_event_filters() {
        eventFilterHandler.handleMany(eventFilters);
        verify(eventDataFilterHandler).handleMany(eq(eventDataFilters), any());
    }
}