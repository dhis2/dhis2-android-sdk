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
package org.hisp.dhis.android.core.program.internal;

import org.hisp.dhis.android.core.arch.db.stores.internal.IdentifiableObjectStore;
import org.hisp.dhis.android.core.arch.handlers.internal.DictionaryTableHandler;
import org.hisp.dhis.android.core.arch.handlers.internal.Handler;
import org.hisp.dhis.android.core.arch.handlers.internal.IdentifiableHandlerImpl;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.common.ValueTypeRendering;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttribute;
import org.hisp.dhis.android.core.program.ProgramTrackedEntityAttributeTableInfo;
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
    private IdentifiableObjectStore<ProgramTrackedEntityAttribute> store;

    @Mock
    private List<ProgramTrackedEntityAttribute> programTrackedEntityAttributes;

    @Mock
    private ProgramTrackedEntityAttribute programTrackedEntityAttribute;

    @Mock
    private DictionaryTableHandler<ValueTypeRendering> renderTypeHandler;

    @Mock
    private ObjectWithUid program;

    @Mock
    private ValueTypeRendering valueTypeRendering;

    @Mock
    private ObjectWithUid trackedEntityAttribute;

    // object to test
    private Handler<ProgramTrackedEntityAttribute> handler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        handler = new ProgramTrackedEntityAttributeHandler(store, renderTypeHandler);
        programTrackedEntityAttributes = new ArrayList<>();
        programTrackedEntityAttributes.add(programTrackedEntityAttribute);

        when(programTrackedEntityAttribute.trackedEntityAttribute()).thenReturn(trackedEntityAttribute);
        when(programTrackedEntityAttribute.uid()).thenReturn("program_tracked_entity_attribute_uid");
        when(programTrackedEntityAttribute.renderType()).thenReturn(valueTypeRendering);
        when(trackedEntityAttribute.uid()).thenReturn("tracked_entity_attribute_uid");
        when(programTrackedEntityAttribute.program()).thenReturn(program);
        when(program.uid()).thenReturn("program_uid");
    }

    @Test
    public void extend_identifiable_handler_impl() {
        IdentifiableHandlerImpl<ProgramTrackedEntityAttribute> syncHandler =
                new ProgramTrackedEntityAttributeHandler(store, null);
    }

    @Test
    public void call_render_type_handler() {
        handler.handleMany(programTrackedEntityAttributes);
        verify(renderTypeHandler).handle(valueTypeRendering, programTrackedEntityAttribute.uid(),
                ProgramTrackedEntityAttributeTableInfo.TABLE_INFO.name());
    }
}