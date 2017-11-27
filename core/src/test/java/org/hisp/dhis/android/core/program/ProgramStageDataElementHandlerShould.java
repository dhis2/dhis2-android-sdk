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

import org.hisp.dhis.android.core.dataelement.DataElement;
import org.hisp.dhis.android.core.dataelement.DataElementHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ProgramStageDataElementHandlerShould {
    @Mock
    private ProgramStageDataElementStore programStageDataElementStore;

    @Mock
    private DataElementHandler dataElementHandler;

    @Mock
    private ProgramStageDataElement programStageDataElement;

    @Mock
    private DataElement dataElement;

    @Mock
    private ProgramStage programStage;

    private List<ProgramStageDataElement> programStageDataElements;

    // object to test
    private ProgramStageDataElementHandler programStageDataElementHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        programStageDataElementHandler = new ProgramStageDataElementHandler(
                programStageDataElementStore, dataElementHandler
        );

        when(programStageDataElement.uid()).thenReturn("test_psde_uid");

        // mandatory one-to-one relationship fields
        when(programStageDataElement.dataElement()).thenReturn(dataElement);
        when(dataElement.uid()).thenReturn("test_data_element_uid");
        when(programStageDataElement.programStage()).thenReturn(programStage);
        when(programStage.uid()).thenReturn("test_program_stage_uid");

        programStageDataElements = new ArrayList<>();
        programStageDataElements.add(programStageDataElement);
    }

    @Test
    public void do_nothing_when_passing_null_argument() throws Exception {
        programStageDataElementHandler.handleProgramStageDataElements(null);

        // verify that program stage data element store is never invoked
        verify(programStageDataElementStore, never()).delete(anyString());
        verify(programStageDataElementStore, never()).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyBoolean(), anyBoolean(), anyBoolean(), anyInt(), anyBoolean(),
                anyString(), anyString(), anyString());
        verify(programStageDataElementStore, never()).update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyBoolean(), anyBoolean(), anyBoolean(), anyInt(), anyBoolean(),
                anyString(), anyString(), anyString());

        // verify that data element handler is never invoked
        verify(dataElementHandler, never()).handleDataElement(any(DataElement.class));
    }

    @Test
    public void invoke_delete_when_handle_program_stage_data_element_set_as_deleted() throws Exception {
        when(programStageDataElement.deleted()).thenReturn(Boolean.TRUE);

        programStageDataElementHandler.handleProgramStageDataElements(programStageDataElements
        );

        verify(programStageDataElementStore, times(1)).delete(programStageDataElement.uid());

        // verify that insert and any update function is never called
        verify(programStageDataElementStore, never()).insert(
                anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyBoolean(), anyBoolean(), anyBoolean(), anyInt(), anyBoolean(),
                anyString(), anyString(), anyString()
        );
        verify(programStageDataElementStore, never()).update(
                anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyBoolean(), anyBoolean(), anyBoolean(), anyInt(), anyBoolean(),
                anyString(), anyString(), anyString()
        );

        // verify that data element handler is invoked once
        verify(dataElementHandler, times(1)).handleDataElement(any(DataElement.class));
    }

    @Test
    public void invoke_only_update_when_handle_program_stage_data_elements_inserted() throws Exception {
        when(programStageDataElementStore.update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyBoolean(), anyBoolean(), anyBoolean(), anyInt(), anyBoolean(),
                anyString(), anyString(), anyString())).thenReturn(1);

        programStageDataElementHandler.handleProgramStageDataElements(programStageDataElements);

        // verify that update is invoked once
        verify(programStageDataElementStore, times(1)).update(
                anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyBoolean(), anyBoolean(), anyBoolean(), anyInt(), anyBoolean(),
                anyString(), anyString(), anyString()
        );


        verify(programStageDataElementStore, never()).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyBoolean(), anyBoolean(), anyBoolean(), anyInt(), anyBoolean(),
                anyString(), anyString(), anyString());

        verify(programStageDataElementStore, never()).delete(anyString());

        // verify that data element handler is called once
        verify(dataElementHandler, times(1)).handleDataElement(any(DataElement.class));
    }

    @Test
    public void invoke_update_and_insert_when_handle_program_stage_data_elements_not_inserted() throws Exception {
        when(programStageDataElementStore.update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyBoolean(), anyBoolean(), anyBoolean(), anyInt(), anyBoolean(),
                anyString(), anyString(), anyString())).thenReturn(0);
        programStageDataElementHandler.handleProgramStageDataElements(programStageDataElements);

        // verify that insert is called once
        verify(programStageDataElementStore, times(1)).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyBoolean(), anyBoolean(), anyBoolean(), anyInt(), anyBoolean(),
                anyString(), anyString(), anyString());

        // verify that update is invoked once since we update before we insert
        verify(programStageDataElementStore, times(1)).update(
                anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyBoolean(), anyBoolean(), anyBoolean(), anyInt(), anyBoolean(),
                anyString(), anyString(), anyString()
        );

        // verify that delete function is never called
        verify(programStageDataElementStore, never()).delete(anyString());

        // verify that data element handler is never called
        verify(dataElementHandler, times(1)).handleDataElement(any(DataElement.class));
    }
}
