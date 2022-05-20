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
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction;
import org.hisp.dhis.android.core.arch.handlers.internal.Handler;
import org.hisp.dhis.android.core.arch.handlers.internal.LinkHandler;
import org.hisp.dhis.android.core.arch.handlers.internal.OrderedLinkHandler;
import org.hisp.dhis.android.core.common.ObjectWithUid;
import org.hisp.dhis.android.core.legendset.ProgramIndicatorLegendSetLink;
import org.hisp.dhis.android.core.program.AnalyticsPeriodBoundary;
import org.hisp.dhis.android.core.program.ProgramIndicator;
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

import com.google.common.collect.Lists;

@RunWith(JUnit4.class)
public class ProgramIndicatorHandlerShould {
    @Mock
    private IdentifiableObjectStore<ProgramIndicator> programIndicatorStore;

    @Mock
    private OrderedLinkHandler<ObjectWithUid, ProgramIndicatorLegendSetLink> programIndicatorLegendSetLinkHandler;

    @Mock
    private LinkHandler<AnalyticsPeriodBoundary, AnalyticsPeriodBoundary> analyticsPeriodBoundaryHandler;

    @Mock
    private Handler<ObjectWithUid> legendSetHandler;

    @Mock
    private ProgramIndicator programIndicator;

    @Mock
    private ObjectWithUid legendSet;

    @Mock
    private AnalyticsPeriodBoundary analyticsPeriodBoundary;

    @Mock
    private AnalyticsPeriodBoundary.Builder analyticsPeriodBoundaryBuilder;

    @Mock
    private ObjectWithUid program;

    // object to test
    private Handler<ProgramIndicator> programIndicatorHandler;

    // list of program indicators
    private List<ProgramIndicator> programIndicators;

    // list of program indicators
    private List<ObjectWithUid> legendSets;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        programIndicatorHandler = new ProgramIndicatorHandler(
                programIndicatorStore,
                programIndicatorLegendSetLinkHandler,
                analyticsPeriodBoundaryHandler
        );

        programIndicators = new ArrayList<>();
        programIndicators.add(programIndicator);

        legendSets = new ArrayList<>();
        legendSets.add(legendSet);

        when(programIndicator.uid()).thenReturn("test_program_indicator_uid");
        when(program.uid()).thenReturn("test_program_uid");
        when(programIndicator.program()).thenReturn(program);
        when(programIndicator.legendSets()).thenReturn(legendSets);
        when(programIndicator.analyticsPeriodBoundaries()).thenReturn(Lists.newArrayList(analyticsPeriodBoundary));
        when(analyticsPeriodBoundary.toBuilder()).thenReturn(analyticsPeriodBoundaryBuilder);
        when(analyticsPeriodBoundaryBuilder.programIndicator(any())).thenReturn(analyticsPeriodBoundaryBuilder);
        when(analyticsPeriodBoundaryBuilder.build()).thenReturn(analyticsPeriodBoundary);
        when(programIndicatorStore.updateOrInsert(any())).thenReturn(HandleAction.Insert);
    }

    @Test
    public void do_nothing_when_passing_null_argument() {
        programIndicatorHandler.handle(null);

        // verify that program indicator store is never called
        verify(programIndicatorStore, never()).delete(anyString());

        verify(programIndicatorStore, never()).update(any(ProgramIndicator.class));

        verify(programIndicatorStore, never()).insert(any(ProgramIndicator.class));
    }


    @Test
    public void delete_shouldDeleteProgramIndicator() {
        when(programIndicator.deleted()).thenReturn(Boolean.TRUE);

        programIndicatorHandler.handleMany(programIndicators);

        // verify that delete is called once
        verify(programIndicatorStore, times(1)).deleteIfExists(programIndicator.uid());
    }

    @Test
    public void update_shouldUpdateProgramIndicatorWithoutProgramStageSection() {
        programIndicatorHandler.handleMany(programIndicators);

        // verify that update is called once
        verify(programIndicatorStore, times(1)).updateOrInsert(any(ProgramIndicator.class));

        verify(programIndicatorStore, never()).delete(anyString());
    }


    public void call_program_indicator_legend_set_handler() {
        programIndicatorHandler.handleMany(programIndicators);
        verify(legendSetHandler).handleMany(eq(legendSets));
    }

    @Test
    public void call_program_indicator_analytics_period_boundary_handler_and_store() {
        programIndicatorHandler.handleMany(programIndicators);
        verify(analyticsPeriodBoundaryHandler).handleMany(any(), any(), any());
    }
}