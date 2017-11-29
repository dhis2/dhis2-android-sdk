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

import org.hisp.dhis.android.core.common.FormType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ProgramStageHandlerShould {
    @Mock
    private ProgramStageStore programStageStore;

    @Mock
    private ProgramStageSectionHandler programStageSectionHandler;

    @Mock
    private ProgramStageDataElementHandler programStageDataElementHandler;

    @Mock
    private ProgramStage programStage;

    private List<ProgramStage> programStages;

    // object to test
    private ProgramStageHandler programStageHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        programStageHandler = new ProgramStageHandler(
                programStageStore, programStageSectionHandler,
                programStageDataElementHandler
        );

        when(programStage.uid()).thenReturn("test_program_stage_uid");
        programStages = new ArrayList<>();
        programStages.add(programStage);
    }

    @Test
    public void invoke_delete_when_handle_program_stage_set_as_deleted() throws Exception {
        when(programStage.deleted()).thenReturn(Boolean.TRUE);

        programStageHandler.handleProgramStage("test_program_uid", programStages);

        // verify that one program stage
        assertThat(programStages.size()).isEqualTo(1);

        // verify that delete is called once
        verify(programStageStore, times(1)).delete(anyString());

        // verify that update and insert is never called
        verify(programStageStore, never()).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyBoolean(), anyBoolean(), anyString(),
                anyBoolean(), anyBoolean(), anyBoolean(), any(FormType.class), anyBoolean(), anyBoolean(),
                anyBoolean(), anyInt(), anyBoolean(), anyBoolean(), anyInt(), anyInt(), anyString());

        verify(programStageStore, never()).update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyBoolean(), anyBoolean(), anyString(),
                anyBoolean(), anyBoolean(), anyBoolean(), any(FormType.class), anyBoolean(), anyBoolean(),
                anyBoolean(), anyInt(), anyBoolean(), anyBoolean(), anyInt(), anyInt(), anyString(), anyString());

        // verify that the handlers is invoked
        verify(programStageSectionHandler, times(1)).handleProgramStageSection(
                anyString(), anyListOf(ProgramStageSection.class)
        );

        verify(programStageDataElementHandler, times(1)).handleProgramStageDataElements(
                anyListOf(ProgramStageDataElement.class)
        );

    }

    @Test
    public void invoke_only_update_when_handle_program_stage_inserted() throws Exception {
        when(programStageStore.update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyBoolean(), anyBoolean(), anyString(),
                anyBoolean(), anyBoolean(), anyBoolean(), any(FormType.class), anyBoolean(), anyBoolean(),
                anyBoolean(), anyInt(), anyBoolean(), anyBoolean(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(1);

        // now there is two program stages
        programStages.add(programStage);

        programStageHandler.handleProgramStage("test_program_uid", programStages);

        // verify that update is called twice
        verify(programStageStore, times(2)).update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyBoolean(), anyBoolean(), anyString(),
                anyBoolean(), anyBoolean(), anyBoolean(), any(FormType.class), anyBoolean(), anyBoolean(),
                anyBoolean(), anyInt(), anyBoolean(), anyBoolean(), anyInt(), anyInt(), anyString(), anyString());

        // verify that delete and insert is never invoked
        verify(programStageStore, never()).delete(anyString());

        // verify that update and insert is never called
        verify(programStageStore, never()).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyBoolean(), anyBoolean(), anyString(),
                anyBoolean(), anyBoolean(), anyBoolean(), any(FormType.class), anyBoolean(), anyBoolean(),
                anyBoolean(), anyInt(), anyBoolean(), anyBoolean(), anyInt(), anyInt(), anyString());

        // verify that the handlers is invoked twice
        verify(programStageSectionHandler, times(2)).handleProgramStageSection(
                anyString(), anyListOf(ProgramStageSection.class)
        );

        verify(programStageDataElementHandler, times(2)).handleProgramStageDataElements(
                anyListOf(ProgramStageDataElement.class)
        );

    }

    @Test
    public void invoke_update_and_insert_when_handle_program_stage_not_inserted() throws Exception {
        // simulate that programStageStore didn't update anything
        when(programStageStore.update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyBoolean(), anyBoolean(), anyString(),
                anyBoolean(), anyBoolean(), anyBoolean(), any(FormType.class), anyBoolean(), anyBoolean(),
                anyBoolean(), anyInt(), anyBoolean(), anyBoolean(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(0);

        programStageHandler.handleProgramStage("test_program_uid", programStages);

        verify(programStageStore, times(1)).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyBoolean(), anyBoolean(), anyString(),
                anyBoolean(), anyBoolean(), anyBoolean(), any(FormType.class), anyBoolean(), anyBoolean(),
                anyBoolean(), anyInt(), anyBoolean(), anyBoolean(), anyInt(), anyInt(), anyString());

        // verify that update is called once since we update before we insert
        verify(programStageStore, times(1)).update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyBoolean(), anyBoolean(), anyString(),
                anyBoolean(), anyBoolean(), anyBoolean(), any(FormType.class), anyBoolean(), anyBoolean(),
                anyBoolean(), anyInt(), anyBoolean(), anyBoolean(), anyInt(), anyInt(), anyString(), anyString());

        // verify that delete is never called
        verify(programStageStore, never()).delete(anyString());

        // verify that the handlers is invoked once
        verify(programStageSectionHandler, times(1)).handleProgramStageSection(
                anyString(), anyListOf(ProgramStageSection.class)
        );

        verify(programStageDataElementHandler, times(1)).handleProgramStageDataElements(
                anyListOf(ProgramStageDataElement.class)
        );

    }

    @Test
    public void do_nothing_when_passing_null_program_uid() throws Exception {
        programStageHandler.handleProgramStage(null, programStages);

        // verify that programStageStore is never invoked
        verify(programStageStore, never()).delete(anyString());
        verify(programStageStore, never()).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyBoolean(), anyBoolean(), anyString(),
                anyBoolean(), anyBoolean(), anyBoolean(), any(FormType.class), anyBoolean(), anyBoolean(),
                anyBoolean(), anyInt(), anyBoolean(), anyBoolean(), anyInt(), anyInt(), anyString());
        verify(programStageStore, never()).update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyBoolean(), anyBoolean(), anyString(),
                anyBoolean(), anyBoolean(), anyBoolean(), any(FormType.class), anyBoolean(), anyBoolean(),
                anyBoolean(), anyInt(), anyBoolean(), anyBoolean(), anyInt(), anyInt(), anyString(), anyString());


        // verify that the handlers is never invoked
        verify(programStageSectionHandler, never()).handleProgramStageSection(
                anyString(), anyListOf(ProgramStageSection.class)
        );

        verify(programStageDataElementHandler, never()).handleProgramStageDataElements(
                anyListOf(ProgramStageDataElement.class)
        );

    }

    @Test
    public void do_nothing_when_passing_null_program_stge() throws Exception {
        programStageHandler.handleProgramStage("test_program_uid", null);

        // verify that programStageStore is never invoked
        verify(programStageStore, never()).delete(anyString());
        verify(programStageStore, never()).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyBoolean(), anyBoolean(), anyString(),
                anyBoolean(), anyBoolean(), anyBoolean(), any(FormType.class), anyBoolean(), anyBoolean(),
                anyBoolean(), anyInt(), anyBoolean(), anyBoolean(), anyInt(), anyInt(), anyString());
        verify(programStageStore, never()).update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyBoolean(), anyBoolean(), anyString(),
                anyBoolean(), anyBoolean(), anyBoolean(), any(FormType.class), anyBoolean(), anyBoolean(),
                anyBoolean(), anyInt(), anyBoolean(), anyBoolean(), anyInt(), anyInt(), anyString(), anyString());


        // verify that the handlers is never invoked
        verify(programStageSectionHandler, never()).handleProgramStageSection(
                anyString(), anyListOf(ProgramStageSection.class)
        );

        verify(programStageDataElementHandler, never()).handleProgramStageDataElements(
                anyListOf(ProgramStageDataElement.class)
        );

    }

    @Test
    public void do_nothing_when_passing_null_arguments() throws Exception {
        programStageHandler.handleProgramStage(null, null);

        // verify that programStageStore is never invoked
        verify(programStageStore, never()).delete(anyString());
        verify(programStageStore, never()).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyBoolean(), anyBoolean(), anyString(),
                anyBoolean(), anyBoolean(), anyBoolean(), any(FormType.class), anyBoolean(), anyBoolean(),
                anyBoolean(), anyInt(), anyBoolean(), anyBoolean(), anyInt(), anyInt(), anyString());
        verify(programStageStore, never()).update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyBoolean(), anyBoolean(), anyString(),
                anyBoolean(), anyBoolean(), anyBoolean(), any(FormType.class), anyBoolean(), anyBoolean(),
                anyBoolean(), anyInt(), anyBoolean(), anyBoolean(), anyInt(), anyInt(), anyString(), anyString());


        // verify that the handlers is never invoked
        verify(programStageSectionHandler, never()).handleProgramStageSection(
                anyString(), anyListOf(ProgramStageSection.class)
        );

        verify(programStageDataElementHandler, never()).handleProgramStageDataElements(
                anyListOf(ProgramStageDataElement.class)
        );
    }
}
