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
import org.hisp.dhis.android.core.common.ValueTypeRendering;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.program.ProgramStageDataElement;
import org.hisp.dhis.android.core.program.ProgramStageDataElementTableInfo;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ProgramStageDataElementHandlerShould {

    @Mock
    private IdentifiableObjectStore<ProgramStageDataElement> programStageDataElementStore;

    @Mock
    private Handler<DataElement> dataElementHandler;

    @Mock
    private DictionaryTableHandler<ValueTypeRendering> renderTypeHandler;

    @Mock
    private ProgramStageDataElement programStageDataElement;

    @Mock
    private DataElement dataElement;

    @Mock
    private ValueTypeRendering valueTypeRendering;

    // object to test
    private ProgramStageDataElementHandler handler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        handler = new ProgramStageDataElementHandler(programStageDataElementStore, dataElementHandler, renderTypeHandler);
        when(programStageDataElement.uid()).thenReturn("program_stage_data_element");
        when(programStageDataElement.dataElement()).thenReturn(dataElement);
        when(dataElement.uid()).thenReturn("test_data_element_uid");
        when(programStageDataElement.renderType()).thenReturn(valueTypeRendering);
    }

    @Test
    public void call_data_element_handler() throws Exception {
        handler.handle(programStageDataElement);
        verify(dataElementHandler).handle(dataElement);
    }

    @Test
    public void call_value_type_rendering_handler() throws Exception {
        handler.handle(programStageDataElement);
        verify(renderTypeHandler).handle(programStageDataElement.renderType(), programStageDataElement.uid(),
                ProgramStageDataElementTableInfo.TABLE_INFO.name());
    }

    @Test
    public void extend_identifiable_handler_impl() {
        IdentifiableHandlerImpl<ProgramStageDataElement> genericHandler =
                new ProgramStageDataElementHandler(programStageDataElementStore, null, null);
    }
}
