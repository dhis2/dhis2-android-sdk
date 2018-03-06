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
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ProgramStageSectionHandlerShould {
    public static final String PROGRAM_STAGE_UID = "test_program_stage_uid";
    public static final String PROGRAM_STAGE_SECTION_UID = "test_program_stage_section_uid";
    public static final String DATA_ELEMENT_UID = "test_data_uid";

    @Mock
    private ProgramStageSectionStore programStageSectionStore;

    @Mock
    private ProgramStageDataElementHandler programStageDataElementHandler;

    @Mock
    private ProgramIndicatorHandler programIndicatorHandler;

    @Mock
    private ProgramStageSection programStageSection;
    private List<ProgramStageSection> programStageSections;

    @Mock
    private DataElement dataElement;
    private List<DataElement> dataElements;

    // object to test
    private ProgramStageSectionHandler programStageSectionHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        programStageSectionHandler = new ProgramStageSectionHandler(
                programStageSectionStore, programStageDataElementHandler, programIndicatorHandler
        );

        when(programStageSection.uid()).thenReturn(PROGRAM_STAGE_SECTION_UID);
        programStageSections = new ArrayList<>();
        programStageSections.add(programStageSection);
        dataElements = new ArrayList<>(1);
        dataElements.add(dataElement);
        when(programStageSection.dataElements()).thenReturn(dataElements);
    }

    @Test
    public void invoke_delete_when_handle_program_stage_section_set_as_deleted() throws Exception {
        when(programStageSection.deleted()).thenReturn(Boolean.TRUE);

        programStageSectionHandler.handleProgramStageSection(PROGRAM_STAGE_UID, programStageSections);

        // verify that delete is called once
        verify(programStageSectionStore, times(1)).delete(programStageSection.uid());

        // verify that update and insert is never called
        verify(programStageSectionStore, never()).insert(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyInt(), anyString(),
                any(ProgramStageSectionRenderingType.class), any(ProgramStageSectionRenderingType.class));

        verify(programStageSectionStore, never()).update(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyInt(), anyString(),
                any(ProgramStageSectionRenderingType.class), any(ProgramStageSectionRenderingType.class),
                anyString());

        // verify that handlers is called once
        verify(programStageDataElementHandler, times(1)).updateProgramStageDataElementWithProgramStageSectionLink(
                anyString(), anyString()
        );

        verify(programIndicatorHandler, times(1)).handleProgramIndicator(
                anyString(), anyListOf(ProgramIndicator.class)
        );

    }

    @Test
    public void invoke_only_update_when_handle_program_stage_section_inserted() throws Exception {
        when(programStageSectionStore.update(
                anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyInt(), anyString(),
                any(ProgramStageSectionRenderingType.class), any(ProgramStageSectionRenderingType.class),
                anyString())
        ).thenReturn(1);

        when(programStageSection.dataElements()).thenReturn(dataElements);
        when(dataElement.uid()).thenReturn(DATA_ELEMENT_UID);

        programStageSectionHandler.handleProgramStageSection(PROGRAM_STAGE_UID, programStageSections);

        // verify that update is called once
        verify(programStageSectionStore, times(1)).update(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyInt(), anyString(),
                any(ProgramStageSectionRenderingType.class), any(ProgramStageSectionRenderingType.class),
                anyString());

        // verify that insert and delete is never called
        verify(programStageSectionStore, never()).insert(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyInt(), anyString(),
                any(ProgramStageSectionRenderingType.class), any(ProgramStageSectionRenderingType.class));

        verify(programStageSectionStore, never()).delete(anyString());


        verify(programStageDataElementHandler, times(1)).updateProgramStageDataElementWithProgramStageSectionLink(
                anyString(), anyString()
        );

        verify(programIndicatorHandler, times(1)).handleProgramIndicator(
                anyString(), anyListOf(ProgramIndicator.class)
        );
    }

    @Test
    public void invoke_update_and_insert_when_handle_program_stage_section_not_inserted() throws Exception {
        when(programStageSectionStore.update(
                anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyInt(), anyString(),
                any(ProgramStageSectionRenderingType.class), any(ProgramStageSectionRenderingType.class),
                anyString())
        ).thenReturn(0);

        when(programStageSection.dataElements()).thenReturn(dataElements);
        when(dataElement.uid()).thenReturn(DATA_ELEMENT_UID);

        programStageSectionHandler.handleProgramStageSection(PROGRAM_STAGE_UID, programStageSections);

        // verify that update is called once since we update before we insert
        verify(programStageSectionStore, times(1)).update(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyInt(), anyString(),
                any(ProgramStageSectionRenderingType.class), any(ProgramStageSectionRenderingType.class),
                anyString());

        // verify that insert is called once
        verify(programStageSectionStore, times(1)).insert(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyInt(), anyString(),
                any(ProgramStageSectionRenderingType.class), any(ProgramStageSectionRenderingType.class));

        // verify that delete is never called
        verify(programStageSectionStore, never()).delete(anyString());

        // verify that handlers is called once
        verify(programStageDataElementHandler, times(1)).updateProgramStageDataElementWithProgramStageSectionLink(
                anyString(), anyString()
        );

        verify(programIndicatorHandler, times(1)).handleProgramIndicator(
                anyString(), anyListOf(ProgramIndicator.class)
        );

    }

    @Test
    public void do_nothing_when_passing_null_program_stage_uid() throws Exception {
        programStageSectionHandler.handleProgramStageSection(null, programStageSections);

        // verify that program stage section store is never invoked
        verify(programStageSectionStore, never()).insert(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyInt(), anyString(),
                any(ProgramStageSectionRenderingType.class), any(ProgramStageSectionRenderingType.class));

        verify(programStageSectionStore, never()).update(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyInt(), anyString(),
                any(ProgramStageSectionRenderingType.class), any(ProgramStageSectionRenderingType.class),
                anyString());

        verify(programStageSectionStore, never()).delete(anyString());

        verify(programStageDataElementHandler, never()).updateProgramStageDataElementWithProgramStageSectionLink(
                anyString(), anyString()
        );

        verify(programIndicatorHandler, never()).handleProgramIndicator(
                anyString(), anyListOf(ProgramIndicator.class)
        );
    }

    @Test
    public void do_nothing_when_passing_null_program_stage_section() throws Exception {
        programStageSectionHandler.handleProgramStageSection(PROGRAM_STAGE_UID, null);

        // verify that program stage section store is never invoked
        verify(programStageSectionStore, never()).insert(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyInt(), anyString(),
                any(ProgramStageSectionRenderingType.class), any(ProgramStageSectionRenderingType.class));

        verify(programStageSectionStore, never()).update(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyInt(), anyString(),
                any(ProgramStageSectionRenderingType.class), any(ProgramStageSectionRenderingType.class),
                anyString());

        verify(programStageSectionStore, never()).delete(anyString());


        verify(programStageDataElementHandler, never()).updateProgramStageDataElementWithProgramStageSectionLink(
                anyString(), anyString()
        );

        verify(programIndicatorHandler, never()).handleProgramIndicator(
                anyString(), anyListOf(ProgramIndicator.class)
        );
    }

    @Test
    public void do_nothing_when_passing_null_arguments() throws Exception {
        programStageSectionHandler.handleProgramStageSection(null, null);

        // verify that program stage section store is never invoked
        verify(programStageSectionStore, never()).insert(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyInt(), anyString(),
                any(ProgramStageSectionRenderingType.class), any(ProgramStageSectionRenderingType.class));

        verify(programStageSectionStore, never()).update(anyString(), anyString(), anyString(),
                anyString(), any(Date.class), any(Date.class), anyInt(), anyString(),
                any(ProgramStageSectionRenderingType.class), any(ProgramStageSectionRenderingType.class),
                anyString());

        verify(programStageSectionStore, never()).delete(anyString());

        verify(programStageDataElementHandler, never()).updateProgramStageDataElementWithProgramStageSectionLink(
                anyString(), anyString()
        );

        verify(programIndicatorHandler, never()).handleProgramIndicator(
                anyString(), anyListOf(ProgramIndicator.class)
        );
    }
}
