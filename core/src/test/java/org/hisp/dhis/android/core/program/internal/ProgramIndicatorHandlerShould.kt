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
package org.hisp.dhis.android.core.program.internal

import com.google.common.collect.Lists
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.arch.handlers.internal.Handler
import org.hisp.dhis.android.core.common.ObjectWithUid
import org.hisp.dhis.android.core.legendset.internal.ProgramIndicatorLegendSetLinkHandler
import org.hisp.dhis.android.core.program.AnalyticsPeriodBoundary
import org.hisp.dhis.android.core.program.ProgramIndicator
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class ProgramIndicatorHandlerShould {
    private val programIndicatorStore: ProgramIndicatorStore = mock()
    private val programIndicatorLegendSetLinkHandler: ProgramIndicatorLegendSetLinkHandler = mock()
    private val analyticsPeriodBoundaryHandler: AnalyticsPeriodBoundaryHandler = mock()
    private val programIndicator: ProgramIndicator = mock()
    private val legendSet: ObjectWithUid = mock()
    private val analyticsPeriodBoundary: AnalyticsPeriodBoundary = mock()
    private val analyticsPeriodBoundaryBuilder: AnalyticsPeriodBoundary.Builder = mock()
    private val program: ObjectWithUid = mock()

    // object to test
    private lateinit var programIndicatorHandler: Handler<ProgramIndicator>
    private lateinit var programIndicators: MutableList<ProgramIndicator>
    private lateinit var legendSets: MutableList<ObjectWithUid>

    @Before
    @Throws(Exception::class)
    fun setUp() {
        programIndicatorHandler = ProgramIndicatorHandler(
            programIndicatorStore,
            programIndicatorLegendSetLinkHandler,
            analyticsPeriodBoundaryHandler,
        )

        programIndicators = mutableListOf(programIndicator)
        legendSets = mutableListOf(legendSet)

        whenever(programIndicator.uid()).thenReturn("test_program_indicator_uid")
        whenever(program.uid()).thenReturn("test_program_uid")
        whenever(programIndicator.program()).thenReturn(program)
        whenever(programIndicator.legendSets()).thenReturn(legendSets)
        whenever(programIndicator.analyticsPeriodBoundaries()).thenReturn(Lists.newArrayList(analyticsPeriodBoundary))
        whenever(analyticsPeriodBoundary.toBuilder()).thenReturn(analyticsPeriodBoundaryBuilder)
        whenever(analyticsPeriodBoundaryBuilder.programIndicator(any())).thenReturn(analyticsPeriodBoundaryBuilder)
        whenever(analyticsPeriodBoundaryBuilder.build()).thenReturn(analyticsPeriodBoundary)
        whenever(programIndicatorStore.updateOrInsert(any())).thenReturn(HandleAction.Insert)
    }

    @Test
    fun do_nothing_when_passing_null_argument() = runTest {
        programIndicatorHandler.handle(null)

        // verify that program indicator store is never called
        verify(programIndicatorStore, never()).delete(any())
        verify(programIndicatorStore, never()).update(any())
        verify(programIndicatorStore, never()).insert(any<ProgramIndicator>())
    }

    @Test
    fun delete_shouldDeleteProgramIndicator() = runTest {
        whenever(programIndicator.deleted()).thenReturn(true)
        programIndicatorHandler.handleMany(programIndicators)

        // verify that delete is called once
        verify(programIndicatorStore, times(1)).deleteIfExists(programIndicator.uid())
    }

    @Test
    fun update_shouldUpdateProgramIndicatorWithoutProgramStageSection() = runTest {
        programIndicatorHandler.handleMany(programIndicators)

        // verify that update is called once
        verify(programIndicatorStore, times(1)).updateOrInsert(any<ProgramIndicator>())
        verify(programIndicatorStore, never()).delete(any())
    }

    @Test
    fun call_program_indicator_legend_set_handler() = runTest {
        programIndicatorHandler.handleMany(programIndicators)
        verify(programIndicatorLegendSetLinkHandler).handleMany(eq(programIndicator.uid()), eq(legendSets), any())
    }

    @Test
    fun call_program_indicator_analytics_period_boundary_handler_and_store() = runTest {
        programIndicatorHandler.handleMany(programIndicators)
        verify(analyticsPeriodBoundaryHandler).handleMany(any(), any(), any())
    }
}
