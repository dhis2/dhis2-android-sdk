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

import org.hisp.dhis.android.core.common.GenericHandler;
import org.hisp.dhis.android.core.common.IdentifiableObjectStore;
import org.hisp.dhis.android.core.common.LinkModelHandler;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.common.ObjectWithoutUidStore;
import org.hisp.dhis.android.core.legendset.LegendSet;
import org.hisp.dhis.android.core.legendset.LegendSetModel;
import org.hisp.dhis.android.core.legendset.LegendSetModelBuilder;
import org.hisp.dhis.android.core.legendset.ProgramIndicatorLegendSetLinkModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class ProgramIndicatorHandlerShould {
    @Mock
    private IdentifiableObjectStore<ProgramIndicatorModel> programIndicatorStore;

    @Mock
    private ProgramStageSectionProgramIndicatorLinkStore programStageSectionProgramIndicatorLinkStore;

    @Mock
    private LinkModelHandler<LegendSet, ProgramIndicatorLegendSetLinkModel> programIndicatorLegendSetLinkHandler;

    @Mock
    private GenericHandler<LegendSet, LegendSetModel> legendSetHandler;

    @Mock
    private ProgramIndicator programIndicator;

    @Mock
    private LegendSet legendSet;

    @Mock
    private ObjectWithUid program;

    private ProgramIndicatorModelBuilder programIndicatorModelBuilder;

    // object to test
    private ProgramIndicatorHandler programIndicatorHandler;

    // list of program indicators
    private List<ProgramIndicator> programIndicators;

    // list of program indicators
    private List<LegendSet> legendSets;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        programIndicatorHandler = new ProgramIndicatorHandler(
                programIndicatorStore, programStageSectionProgramIndicatorLinkStore,
                legendSetHandler, programIndicatorLegendSetLinkHandler);

        programIndicators = new ArrayList<>();
        programIndicators.add(programIndicator);

        programIndicatorModelBuilder = new ProgramIndicatorModelBuilder();

        legendSets = new ArrayList<>();
        legendSets.add(legendSet);

        when(programIndicator.uid()).thenReturn("test_program_indicator_uid");
        when(program.uid()).thenReturn("test_program_uid");
        when(programIndicator.program()).thenReturn(program);
        when(programIndicator.legendSets()).thenReturn(legendSets);
    }

    @Test
    public void do_nothing_when_passing_null_argument() throws Exception {
        programIndicatorHandler.handle(null, null);

        // verify that program indicator store is never called
        verify(programIndicatorStore, never()).delete(anyString());

        verify(programIndicatorStore, never()).update(any(ProgramIndicatorModel.class));

        verify(programIndicatorStore, never()).insert(any(ProgramIndicatorModel.class));

        // verify that link store is never called
        verify(programStageSectionProgramIndicatorLinkStore, never()).update(
                anyString(), anyString(), anyString(), anyString()
        );

        verify(programStageSectionProgramIndicatorLinkStore, never()).insert(anyString(), anyString());
    }


    @Test
    public void delete_shouldDeleteProgramIndicator() throws Exception {
        when(programIndicator.deleted()).thenReturn(Boolean.TRUE);

        programIndicatorHandler.handleMany(programIndicators, programIndicatorModelBuilder);

        // verify that delete is called once
        verify(programIndicatorStore, times(1)).delete(programIndicator.uid());

        // verify that link store is never called because it is self-maintained through sqLite
        verify(programStageSectionProgramIndicatorLinkStore, never()).update(
                anyString(), anyString(), anyString(), anyString()
        );

        verify(programStageSectionProgramIndicatorLinkStore, never()).insert(anyString(), anyString());
    }

    @Test
    public void update_shouldUpdateProgramIndicatorWithoutProgramStageSection() throws Exception {
        programIndicatorHandler.handleMany(programIndicators, programIndicatorModelBuilder);

        // verify that update is called once
        verify(programIndicatorStore, times(1)).updateOrInsert(any(ProgramIndicatorModel.class));

        verify(programIndicatorStore, never()).delete(anyString());

        // verify that link store is never called since we're passing in null program stage section uid
        verify(programStageSectionProgramIndicatorLinkStore, never()).update(
                anyString(), anyString(), anyString(), anyString()
        );

        verify(programStageSectionProgramIndicatorLinkStore, never()).insert(anyString(), anyString());

    }

    @Test
    public void update_shouldUpdateProgramIndicatorWithProgramStageSection() throws Exception {
        programIndicatorHandler.handleManyWithProgramStageSection(programIndicators, programIndicatorModelBuilder,
                "test_program_stage_section");

        // verify that update is called once
        verify(programIndicatorStore, times(1)).updateOrInsert(any(ProgramIndicatorModel.class));

        verify(programIndicatorStore, never()).delete(anyString());

        // verify that link store is updated
        verify(programStageSectionProgramIndicatorLinkStore, times(1)).update(
                anyString(), anyString(), anyString(), anyString()
        );
    }

    @Test
    public void call_program_indicator_legend_set_handler() throws Exception {
        programIndicatorHandler.handleMany(programIndicators, programIndicatorModelBuilder);
        verify(legendSetHandler).handleMany(eq(legendSets), any(LegendSetModelBuilder.class));
    }
}
