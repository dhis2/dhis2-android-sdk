/*
 * Copyright (c) 2017, University of Oslo
 *
 * All rights reserved.
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
package org.hisp.dhis.android.core.program;

import org.hisp.dhis.android.core.common.Access;
import org.hisp.dhis.android.core.common.GenericHandler;
import org.hisp.dhis.android.core.common.IdentifiableHandlerImpl;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttribute;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeHandler;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityAttributeStore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ProgramTrackedEntityAttributeHandlerShould {

    @Mock
    private IdentifiableObjectStore<ProgramTrackedEntityAttributeModel> store;

    @Mock
    private TrackedEntityAttributeHandler trackedEntityAttributeHandler;

    @Mock
    private List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes;

    @Mock
    private ProgramTrackedEntityAttribute programTrackedEntityAttribute;

    @Mock
    private TrackedEntityAttributeStore trackedEntityAttributeStore;

    @Mock
    private Access access;

    @Mock
    private Program program;

    @Mock
    private TrackedEntityAttribute trackedEntityAttribute;

    // object to test
    private GenericHandler<ProgramTrackedEntityAttribute, ProgramTrackedEntityAttributeModel> handler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        handler = new ProgramTrackedEntityAttributeHandler(store, trackedEntityAttributeHandler,
                trackedEntityAttributeStore);
        programTrackedEntityAttributes = new ArrayList<>();
        programTrackedEntityAttributes.add(programTrackedEntityAttribute);

        when(programTrackedEntityAttribute.trackedEntityAttribute()).thenReturn(trackedEntityAttribute);
        when(trackedEntityAttribute.uid()).thenReturn("tracked_entity_attribute_uid");
        when(trackedEntityAttribute.access()).thenReturn(access);
        when(access.read()).thenReturn(true);
        when(programTrackedEntityAttribute.program()).thenReturn(program);
        when(program.uid()).thenReturn("program_uid");
    }

    @Test
    public void extend_identifiable_handler_impl() {
        IdentifiableHandlerImpl<ProgramTrackedEntityAttribute, ProgramTrackedEntityAttributeModel> genericHandler =
                new ProgramTrackedEntityAttributeHandler(null, null, null);
    }

    @Test
    public void call_tracked_entity_attribute_handler() throws Exception {
        handler.handleMany(programTrackedEntityAttributes, new ProgramTrackedEntityAttributeModelBuilder());
        verify(trackedEntityAttributeHandler).handleTrackedEntityAttribute(trackedEntityAttribute);
    }
}