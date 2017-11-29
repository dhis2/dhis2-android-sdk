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
public class ProgramIndicatorHandlerShould {
    @Mock
    private ProgramIndicatorStore programIndicatorStore;

    @Mock
    private ProgramStageSectionProgramIndicatorLinkStore programStageSectionProgramIndicatorLinkStore;

    @Mock
    private ProgramIndicator programIndicator;

    @Mock
    private Program program;

    // object to test
    private ProgramIndicatorHandler programIndicatorHandler;

    // list of program indicators
    private List<ProgramIndicator> programIndicators;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        programIndicatorHandler = new ProgramIndicatorHandler(
                programIndicatorStore, programStageSectionProgramIndicatorLinkStore
        );

        when(programIndicator.uid()).thenReturn("test_program_indicator_uid");
        when(programIndicator.program()).thenReturn(program);

        programIndicators = new ArrayList<>();
        programIndicators.add(programIndicator);
    }

    @Test
    public void do_nothing_when_passing_null_argument() throws Exception {
        programIndicatorHandler.handleProgramIndicator(null, null);

        // verify that program indicator store is never called
        verify(programIndicatorStore, never()).delete(anyString());

        verify(programIndicatorStore, never()).update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(), anyString(),
                anyBoolean(), anyString(), anyString(), anyString(), anyInt(), anyString(), anyString());

        verify(programIndicatorStore, never()).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(), anyString(),
                anyBoolean(), anyString(), anyString(), anyString(), anyInt(), anyString());

        // verify that link store is never called
        verify(programStageSectionProgramIndicatorLinkStore, never()).update(
                anyString(), anyString(), anyString(), anyString()
        );

        verify(programStageSectionProgramIndicatorLinkStore, never()).insert(anyString(), anyString());
    }


    @Test
    public void delete_shouldDeleteProgramIndicator() throws Exception {
        when(programIndicator.deleted()).thenReturn(Boolean.TRUE);

        programIndicatorHandler.handleProgramIndicator(null, programIndicators);

        // verify that delete is called once
        verify(programIndicatorStore, times(1)).delete(programIndicator.uid());

        // verify that update and insert is never called
        verify(programIndicatorStore, never()).update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(), anyString(),
                anyBoolean(), anyString(), anyString(), anyString(), anyInt(), anyString(), anyString());

        verify(programIndicatorStore, never()).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(), anyString(), anyString(),
                anyBoolean(), anyString(), anyString(), anyString(), anyInt(), anyString());

        // verify that link store is never called because it is self-maintained through sqLite
        verify(programStageSectionProgramIndicatorLinkStore, never()).update(
                anyString(), anyString(), anyString(), anyString()
        );

        verify(programStageSectionProgramIndicatorLinkStore, never()).insert(anyString(), anyString());
    }

    @Test
    public void update_shouldUpdateProgramIndicatorWithoutProgramStageSection() throws Exception {
        when(programIndicatorStore.update(
                anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(),
                anyString(), anyString(), anyBoolean(), anyString(), anyString(),
                anyString(), anyInt(), anyString(), anyString())).thenReturn(1);

        programIndicatorHandler.handleProgramIndicator(null, programIndicators);

        // verify that update is called once
        verify(programIndicatorStore, times(1)).update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(),
                anyString(), anyString(), anyBoolean(), anyString(), anyString(),
                anyString(), anyInt(), anyString(), anyString());

        // verify that insert and delete is never called
        verify(programIndicatorStore, never()).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(),
                anyString(), anyString(), anyBoolean(), anyString(), anyString(),
                anyString(), anyInt(), anyString());

        verify(programIndicatorStore, never()).delete(anyString());

        // verify that link store is never called since we're passing in null program stage section uid
        verify(programStageSectionProgramIndicatorLinkStore, never()).update(
                anyString(), anyString(), anyString(), anyString()
        );

        verify(programStageSectionProgramIndicatorLinkStore, never()).insert(anyString(), anyString());

    }

    @Test
    public void update_shouldUpdateProgramIndicatorWithProgramStageSection() throws Exception {
        when(programIndicatorStore.update(
                anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(),
                anyString(), anyString(), anyBoolean(), anyString(), anyString(),
                anyString(), anyInt(), anyString(), anyString())).thenReturn(1);

        when(programStageSectionProgramIndicatorLinkStore.update(
                anyString(), anyString(), anyString(), anyString())
        ).thenReturn(1);

        programIndicatorHandler.handleProgramIndicator("test_program_stage_section", programIndicators);

        // verify that update is called once
        verify(programIndicatorStore, times(1)).update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(),
                anyString(), anyString(), anyBoolean(), anyString(), anyString(),
                anyString(), anyInt(), anyString(), anyString());

        // verify that insert and delete is never called
        verify(programIndicatorStore, never()).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(),
                anyString(), anyString(), anyBoolean(), anyString(), anyString(),
                anyString(), anyInt(), anyString());

        verify(programIndicatorStore, never()).delete(anyString());

        // verify that link store is updated
        verify(programStageSectionProgramIndicatorLinkStore, times(1)).update(
                anyString(), anyString(), anyString(), anyString()
        );

        verify(programStageSectionProgramIndicatorLinkStore, never()).insert(anyString(), anyString());
    }

    @Test
    public void insert_shouldInsertProgramIndicatorWithoutProgramStageSection() throws Exception {
        when(programIndicatorStore.update(
                anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(),
                anyString(), anyString(), anyBoolean(), anyString(), anyString(),
                anyString(), anyInt(), anyString(), anyString())).thenReturn(0);

        programIndicatorHandler.handleProgramIndicator(null, programIndicators);

        // verify that insert is called once
        verify(programIndicatorStore, times(1)).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(),
                anyString(), anyString(), anyBoolean(), anyString(), anyString(),
                anyString(), anyInt(), anyString());

        // verify that update is called since we update before we insert
        verify(programIndicatorStore, times(1)).update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(),
                anyString(), anyString(), anyBoolean(), anyString(), anyString(),
                anyString(), anyInt(), anyString(), anyString());

        // verify that delete is never called
        verify(programIndicatorStore, never()).delete(anyString());

        // verify that link store is never called
        verify(programStageSectionProgramIndicatorLinkStore, never()).update(
                anyString(), anyString(), anyString(), anyString()
        );

        verify(programStageSectionProgramIndicatorLinkStore, never()).insert(anyString(), anyString());

    }


    @Test
    public void insert_shouldInsertProgramIndicatorWithProgramStageSection() throws Exception {
        when(programIndicatorStore.update(
                anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(),
                anyString(), anyString(), anyBoolean(), anyString(), anyString(),
                anyString(), anyInt(), anyString(), anyString())).thenReturn(0);

        when(programStageSectionProgramIndicatorLinkStore.update(
                anyString(), anyString(), anyString(), anyString())
        ).thenReturn(0);

        programIndicatorHandler.handleProgramIndicator("test_program_stage_section_uid", programIndicators);

        // verify that insert is called once
        verify(programIndicatorStore, times(1)).insert(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(),
                anyString(), anyString(), anyBoolean(), anyString(), anyString(),
                anyString(), anyInt(), anyString());

        // verify that update is called since we update before we insert
        verify(programIndicatorStore, times(1)).update(anyString(), anyString(), anyString(), anyString(),
                any(Date.class), any(Date.class), anyString(), anyString(),
                anyString(), anyString(), anyBoolean(), anyString(), anyString(),
                anyString(), anyInt(), anyString(), anyString());

        // verify that delete is never called
        verify(programIndicatorStore, never()).delete(anyString());

        // verify that link store's insert method is called once
        verify(programStageSectionProgramIndicatorLinkStore, times(1)).insert(anyString(), anyString());

        // verify that link store's update method is called once since we try to update before we insert
        verify(programStageSectionProgramIndicatorLinkStore, times(1)).update(
                anyString(), anyString(), anyString(), anyString()
        );

    }
}
