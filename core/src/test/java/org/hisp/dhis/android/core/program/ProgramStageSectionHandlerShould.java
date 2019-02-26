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
package org.hisp.dhis.android.core.program;

import org.hisp.dhis.android.core.arch.handlers.LinkSyncHandler;
import org.hisp.dhis.android.core.arch.handlers.SyncHandler;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.LinkModelHandler;
import org.hisp.dhis.android.core.dataelement.DataElement;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ProgramStageSectionHandlerShould {
    private static final String PROGRAM_STAGE_SECTION_UID = "test_program_stage_section_uid";

    @Mock
    private IdentifiableObjectStore<ProgramStageSection> programStageSectionStore;

    @Mock
    private SyncHandler<ProgramIndicator> programIndicatorHandler;

    @Mock
    private LinkModelHandler<ProgramIndicator, ProgramStageSectionProgramIndicatorLinkModel>
            programStageSectionProgramIndicatorLinkHandler;

    @Mock
    private LinkSyncHandler<ProgramStageSectionDataElementLink> programStageSectionDataElementLinkHandler;

    @Mock
    private ProgramStageSection programStageSection;

    @Mock
    private DataElement dataElement;

    @Mock
    private List<ProgramIndicator> programIndicators;

    // object to test
    private ProgramStageSectionHandler programStageSectionHandler;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        programStageSectionHandler = new ProgramStageSectionHandler(
                programStageSectionStore, programIndicatorHandler,
                programStageSectionProgramIndicatorLinkHandler, programStageSectionDataElementLinkHandler);

        when(programStageSection.uid()).thenReturn(PROGRAM_STAGE_SECTION_UID);
        List<DataElement> dataElements = new ArrayList<>(1);
        dataElements.add(dataElement);
        when(programStageSection.dataElements()).thenReturn(dataElements);
        when(programStageSection.programIndicators()).thenReturn(programIndicators);
        when(dataElement.uid()).thenReturn("data_element_uid");
    }

    @Test
    public void handle_program_indicators() {
        programStageSectionHandler.handle(programStageSection);
        verify(programIndicatorHandler).handleMany(eq(programIndicators));
    }

    @Test
    public void handle_program_stage_section_data_element_links() {
        programStageSectionHandler.handle(programStageSection);
        verify(programStageSectionDataElementLinkHandler).handleMany(any(String.class),
                anyListOf(ProgramStageSectionDataElementLink.class));
    }

    @Test
    public void handle_program_stage_section_program_indicator_links() {
        programStageSectionHandler.handle(programStageSection);
        verify(programStageSectionProgramIndicatorLinkHandler).handleMany(any(String.class),
                anyListOf(ProgramIndicator.class), any(ProgramStageSectionProgramIndicatorLinkModelBuilder.class));
    }
}