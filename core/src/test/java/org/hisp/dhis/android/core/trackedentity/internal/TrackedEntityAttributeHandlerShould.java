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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl;
import org.hisp.dhis.android.core.arch.handlers.internal.OrderedLinkHandler;
import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.ObjectStyle;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeLegendSetLink;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

@RunWith(JUnit4.class)
public class TrackedEntityAttributeHandlerShould {

    @Mock
    private IdentifiableObjectStore<TrackedEntityAttribute> trackedEntityAttributeStore;

    @Mock
    private OrderedLinkHandler<ObjectWithUid, TrackedEntityAttributeLegendSetLink> trackedEntityAttributeLegendSetLinkHandler;

    @Mock
    private ObjectStyle objectStyle;

    @Mock
    private Access access;

    private TrackedEntityAttribute trackedEntityAttribute;

    // object to test
    private List<TrackedEntityAttribute> trackedEntityAttributes;
    private TrackedEntityAttributeHandler trackedEntityAttributeHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        trackedEntityAttributeHandler = new TrackedEntityAttributeHandler(
                trackedEntityAttributeStore,
                trackedEntityAttributeLegendSetLinkHandler
        );

        trackedEntityAttribute = TrackedEntityAttribute.builder()
                .uid("test_tracked_entity_attribute_uid")
                .style(objectStyle)
                .name("name")
                .displayName("display_name")
                .formName("form_name")
                .access(access)
                .build();

        trackedEntityAttributes = new ArrayList<>();
        trackedEntityAttributes.add(trackedEntityAttribute);

        when(access.read()).thenReturn(true);
    }

    @Test
    public void extend_identifiable_handler_impl() {
        IdentifiableHandlerImpl<TrackedEntityAttribute> genericHandler =
                new TrackedEntityAttributeHandler(trackedEntityAttributeStore, trackedEntityAttributeLegendSetLinkHandler);
    }

    @Test
    public void delete_tea_if_no_read_access() {
        when(access.read()).thenReturn(false);
        trackedEntityAttributeHandler.handleMany(trackedEntityAttributes);
        verify(trackedEntityAttributeStore).deleteIfExists(trackedEntityAttribute.uid());
    }
}