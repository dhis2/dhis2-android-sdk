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
package org.hisp.dhis.android.core.period.internal

import com.google.common.collect.Lists
import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.period.Period
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever

@RunWith(JUnit4::class)
class PeriodHandlerShould {
    private val store: PeriodStore = mock()
    private val generator: ParentPeriodGenerator = mock()
    private val p1: Period = mock()
    private val p2: Period = mock()

    // object to test
    private lateinit var periodHandler: PeriodHandler

    @Before
    @Throws(Exception::class)
    fun setUp() {
        periodHandler = PeriodHandler(store, generator)
        whenever(generator.generatePeriods()).thenReturn(Lists.newArrayList(p1, p2))
    }

    @Test
    fun call_generator_to_generate_periods() = runTest {
        periodHandler.generateAndPersist()

        verify(generator).generatePeriods()
        verifyNoMoreInteractions(generator)
    }

    @Test
    fun call_store_to_persist_periods() = runTest {
        periodHandler.generateAndPersist()

        verify(store).updateOrInsertWhere(p1)
        verify(store).updateOrInsertWhere(p2)
        verifyNoMoreInteractions(store)
    }
}
