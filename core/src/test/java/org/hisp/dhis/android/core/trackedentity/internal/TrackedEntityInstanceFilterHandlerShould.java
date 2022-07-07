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

package org.hisp.dhis.android.core.trackedentity.internal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.common.collect.Lists;

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction;
import org.hisp.dhis.android.core.arch.handlers.internal.HandlerWithTransformer;
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.trackedentity.AttributeValueFilter;
import org.hisp.dhis.android.core.trackedentity.EntityQueryCriteria;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceEventFilter;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

@RunWith(JUnit4.class)
public class TrackedEntityInstanceFilterHandlerShould {

    @Mock
    private IdentifiableObjectStore<TrackedEntityInstanceFilter> trackedEntityInstanceFilterStore;

    @Mock
    private ObjectStyle objectStyle;

    @Mock
    private HandlerWithTransformer<TrackedEntityInstanceEventFilter> trackedEntityInstanceEventFilterHandler;

    @Mock
    private HandlerWithTransformer<AttributeValueFilter> attributeValueFilterHandler;

    @Mock
    private TrackedEntityInstanceEventFilter eventFilter;

    // object to test
    private List<TrackedEntityInstanceFilter> trackedEntityInstanceFilters;
    private List<TrackedEntityInstanceEventFilter> eventFilters;
    private TrackedEntityInstanceFilterHandler trackedEntityInstanceFilterHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        trackedEntityInstanceFilterHandler = new TrackedEntityInstanceFilterHandler(
                trackedEntityInstanceFilterStore,
                trackedEntityInstanceEventFilterHandler,
                attributeValueFilterHandler);

        eventFilters = Lists.newArrayList(eventFilter);

        TrackedEntityInstanceFilter trackedEntityInstanceFilter = TrackedEntityInstanceFilter.builder()
                .uid("test_tracked_entity_attribute_uid")
                .program(ObjectWithUid.create("program_uid"))
                .style(objectStyle)
                .name("name")
                .displayName("display_name")
                .eventFilters(eventFilters)
                .entityQueryCriteria(EntityQueryCriteria.builder().build())
                .build();

        when(trackedEntityInstanceFilterStore.updateOrInsert(any())).thenReturn(HandleAction.Insert);

        trackedEntityInstanceFilters = new ArrayList<>();
        trackedEntityInstanceFilters.add(trackedEntityInstanceFilter);
    }

    @Test
    public void extend_identifiable_handler_impl() {
        IdentifiableHandlerImpl<TrackedEntityInstanceFilter> genericHandler =
                new TrackedEntityInstanceFilterHandler(trackedEntityInstanceFilterStore,
                        trackedEntityInstanceEventFilterHandler, attributeValueFilterHandler);
    }

    @Test
    public void handle_event_filters() {
        trackedEntityInstanceFilterHandler.handleMany(trackedEntityInstanceFilters);
        verify(trackedEntityInstanceEventFilterHandler).handleMany(eq(eventFilters), any());
    }
}