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
package org.hisp.dhis.android.core.visualization.internal

import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.settings.internal.AnalyticsDhisVisualizationCleaner
import org.hisp.dhis.android.core.visualization.TrackerVisualization
import org.hisp.dhis.android.core.visualization.TrackerVisualizationDimension
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.*

@RunWith(JUnit4::class)
class TrackerVisualizationHandlerShould {

    private val store: TrackerVisualizationStore = mock()
    private val collectionCleaner: TrackerVisualizationCollectionCleaner = mock()
    private val dimensionHandler: TrackerVisualizationDimensionHandler = mock()
    private val analyticsDhisVisualizationCleaner: AnalyticsDhisVisualizationCleaner = mock()
    private val dimension: TrackerVisualizationDimension = TrackerVisualizationDimension.builder().build()
    private val trackerVisualization: TrackerVisualization = mock()

    // object to test
    private lateinit var trackerVisualizationHandler: TrackerVisualizationHandler

    @Before
    fun setUp() = runTest {
        trackerVisualizationHandler = TrackerVisualizationHandler(
            store,
            collectionCleaner,
            analyticsDhisVisualizationCleaner,
            dimensionHandler,
        )

        whenever(trackerVisualization.columns()).doReturn(listOf(dimension))
        whenever(trackerVisualization.filters()).doReturn(listOf(dimension))
        whenever(store.updateOrInsert(any())).doReturn(HandleAction.Insert)
        whenever(trackerVisualization.uid()).doReturn("tracker_visualization_uid")
    }

    @Test
    fun call_items_handler() = runTest {
        trackerVisualizationHandler.handleMany(listOf(trackerVisualization))
        verify(dimensionHandler).handleMany(any(), any())
    }

    @Test
    fun call_collection_cleaner() = runTest {
        trackerVisualizationHandler.handleMany(listOf(trackerVisualization))
        verify(collectionCleaner).deleteNotPresent(any())
    }
}
