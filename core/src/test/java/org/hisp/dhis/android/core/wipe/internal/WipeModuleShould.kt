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
package org.hisp.dhis.android.core.wipe.internal

import kotlinx.coroutines.test.runTest
import org.hisp.dhis.android.core.arch.call.executors.internal.D2CallExecutorInterface
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.mock

@RunWith(JUnit4::class)
class WipeModuleShould {
    private val moduleWiperA: ModuleWiper = mock()
    private val moduleWiperB: ModuleWiper = mock()
    private var wipeModule: WipeModule = mock()

    @Before
    @Throws(Exception::class)
    fun setUp() = runTest {
        MockitoAnnotations.openMocks(this)
        val wipers = listOf(moduleWiperA, moduleWiperB)
        wipeModule = WipeModuleImpl(FakeD2CallExecutor(), wipers)
    }

    @Test
    @Throws(Exception::class)
    fun wipe_all_modules() = runTest {
        wipeModule.wipeEverything()

        Mockito.verify(moduleWiperA).wipeMetadata()
        Mockito.verify(moduleWiperB).wipeMetadata()

        Mockito.verify(moduleWiperA).wipeData()
        Mockito.verify(moduleWiperB).wipeData()
    }

    @Test
    @Throws(Exception::class)
    fun wipe_metadata_in_modules() = runTest {
        wipeModule.wipeMetadata()

        Mockito.verify(moduleWiperA).wipeMetadata()
        Mockito.verify(moduleWiperB).wipeMetadata()

        Mockito.verify(moduleWiperA, Mockito.never()).wipeData()
        Mockito.verify(moduleWiperB, Mockito.never()).wipeData()
    }

    @Test
    @Throws(Exception::class)
    fun wipe_data_in_modules() = runTest {
        wipeModule.wipeData()

        Mockito.verify(moduleWiperA, Mockito.never()).wipeMetadata()
        Mockito.verify(moduleWiperB, Mockito.never()).wipeMetadata()

        Mockito.verify(moduleWiperA).wipeData()
        Mockito.verify(moduleWiperB).wipeData()
    }
}

internal class FakeD2CallExecutor : D2CallExecutorInterface {
    override suspend fun <C> executeD2CallTransactionally(call: suspend () -> C): C {
        return call()
    }

    override suspend fun <C> executeD2Call(call: suspend () -> C): C {
        return call()
    }
}
