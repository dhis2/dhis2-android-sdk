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
package org.hisp.dhis.android.core.legendset.internal

import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.handlers.internal.HandleAction
import org.hisp.dhis.android.core.legendset.Legend
import org.hisp.dhis.android.core.legendset.LegendSet
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class LegendSetHandlerShould {
    private val legendSetStore: LegendSetStore = mock()
    private val legendHandler: LegendHandler = mock()
    private val legend: Legend = mock()
    private var legends: MutableList<Legend> = mock()
    private val legendSet: LegendSet = mock()
    private val legendCleaner: LegendSetLegendOrphanCleaner = mock()

    // object to test
    private lateinit var legendSetHandler: LegendSetHandler

    @Before
    @Throws(Exception::class)
    fun setUp() {
        legendSetHandler = LegendSetHandler(legendSetStore, legendHandler, legendCleaner)
        legends = mutableListOf(legend)
        whenever(legendSet.legends()).thenReturn(legends)
        whenever(legendSetStore.updateOrInsert(any())).thenReturn(HandleAction.Insert)
    }

    @Test
    fun extend_identifiable_sync_handler_impl() {
        val genericHandler = LegendSetHandler(legendSetStore, legendHandler, legendCleaner)
    }

    @Test
    fun call_style_handler() = runTest {
        legendSetHandler.handle(legendSet)
        verify(legendHandler).handleMany(eq(legendSet.legends()))
    }

    @Test
    fun clean_orphan_legends_after_update() = runTest {
        whenever(legendSetStore.updateOrInsert(any())).thenReturn(HandleAction.Update)
        legendSetHandler.handle(legendSet)
        verify(legendCleaner).deleteOrphan(legendSet, legends)
    }

    @Test
    fun not_clean_orphan_legends_after_insert() = runTest {
        whenever(legendSetStore.updateOrInsert(any())).thenReturn(HandleAction.Insert)
        legendSetHandler.handle(legendSet)
        verify(legendCleaner, never()).deleteOrphan(legendSet, legends)
    }
}
