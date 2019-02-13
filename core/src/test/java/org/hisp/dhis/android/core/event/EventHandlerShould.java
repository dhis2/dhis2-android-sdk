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

package org.hisp.dhis.android.core.event;

import org.hisp.dhis.android.core.arch.handlers.SyncHandlerWithTransformer;
import org.hisp.dhis.android.core.common.HandleAction;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityDataValue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class EventHandlerShould {

    @Mock
    private EventStore eventStore;

    @Mock
    private SyncHandlerWithTransformer<TrackedEntityDataValue> trackedEntityDataValueHandler;

    @Mock
    private Event event;

    // object to test
    private EventHandler eventHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(event.uid()).thenReturn("test_event_uid");

        eventHandler = new EventHandler(eventStore, trackedEntityDataValueHandler);
    }

    @Test
    public void do_nothing_when_passing_empty_list_argument() throws Exception {
        eventHandler.handleMany(new ArrayList<>());

        // verify that store is never invoked
        verify(eventStore, never()).deleteIfExists(anyString());
        verify(eventStore, never()).update(any(Event.class));
        verify(eventStore, never()).insert(any(Event.class));
    }

    @Test
    public void invoke_only_delete_when_a_event_is_set_as_deleted() throws Exception {
        when(event.deleted()).thenReturn(Boolean.TRUE);

        eventHandler.handle(event);

        // verify that delete is invoked once
        verify(eventStore, times(1)).deleteIfExists(event.uid());

        // verify that update and insert is never invoked
        verify(eventStore, never()).update(any(Event.class));
        verify(eventStore, never()).insert(any(Event.class));
    }

    @Test
    public void invoke_update_and_insert_when_handle_event_not_inserted() throws Exception {
        when(eventStore.updateOrInsert(any(Event.class))).thenReturn(HandleAction.Insert);
        when(event.organisationUnit()).thenReturn("org_unit_uid");
        when(event.status()).thenReturn(EventStatus.SCHEDULE);

        eventHandler.handle(event);

        // verify that update and insert is invoked, since we're updating before inserting
        verify(eventStore, times(1)).updateOrInsert(any(Event.class));

        // verify that delete is never invoked
        verify(eventStore, never()).deleteIfExists(anyString());
    }
}